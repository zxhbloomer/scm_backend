package com.xinyirun.scm.mongodb.serviceimpl.log.datachange;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeDetailMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMainMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeMongoEntity;
import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeOperateMongoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeDetailMongoVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeMainVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeMongoVo;
import com.xinyirun.scm.bean.system.vo.mongo.datachange.SLogDataChangeOperateMongoVo;
import com.xinyirun.scm.bean.system.vo.mongo.log.SLogDataChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogFindOrderCodeVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnsVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.serialtype.SerialType;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.reflection.ReflectionUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.repository.LogDataChangeMongoMainRepository;
import com.xinyirun.scm.mongodb.repository.LogDataChangeMongoRepository;
import com.xinyirun.scm.mongodb.repository.LogDataChangeOperateMongoMainRepository;
import com.xinyirun.scm.mongodb.service.log.datachange.LogChangeMongoService;
import com.xinyirun.scm.quartz.util.ScheduleUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.xinyirun.scm.common.utils.pattern.PatternUtils.regexPattern;

/**
 * @数据变动记录
 */
@Slf4j
@Service
public class LogChangeMongoServiceImpl implements LogChangeMongoService {

    @Autowired
    LogDataChangeMongoRepository repository;

    @Autowired
    LogDataChangeMongoMainRepository mainRepository;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LogDataChangeMongoMainRepository logDataChangeMongoMainRepository;

    @Autowired
    private LogDataChangeOperateMongoMainRepository logDataChangeOperateMongoMainRepository;

    /**
     * 保存数据到 mongodb
     *
     */
    @Override
    public void save(SDataChangeLogVo bean) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        // 其他日志的内容
        SLogDataChangeMongoEntity entity = getDataChangeEntity((SDataChangeLogVo) bean);

        /**
         * 因存在更新前与更新后相同的情况，所以考虑entity.getDetails().size() > 0时保存并进行定时任务处理
         *
         */
        if(entity.getDetails().size() > 0) {
            repository.save(entity);
            /**
             * 根据order_code查询出s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
             * 如果找不到，发起定时任务，1分钟后，再次查询，如果还是找不到，写异常
             */
            SDataChangeLogFindOrderCodeVo sDataChangeLogFindOrderCodeVo = new SDataChangeLogFindOrderCodeVo();
            sDataChangeLogFindOrderCodeVo.setS_log_data_change_detail_id(entity.getId());
            try {
                ScheduleUtils.createJobDataChangeFindOrderCode(scheduler,
                        SerialType.DATA_CHANGE_FIND_ORDER_CODE,
                        JSON.toJSONString(sDataChangeLogFindOrderCodeVo),
                        DataSourceHelper.getCurrentDataSourceName()
                        );
            } catch (SchedulerException e) {
                log.error("定时任务启动失败", e);
                throw new BusinessException(e);
            }
        } else {
            log.debug("----------------------------------数据相同，不做处理----------------------------------");
        }

    }    /**
     * 根据订单编码查询数据变更主记录及其详情
     *
     * @param order_code 订单编码
     * @return 数据变更主记录及其详情的VO对象
     */
    @Override
    public SLogDataChangeMainVo findMainByOrderCode(String order_code) {
        // 1. 查询主记录
        SLogDataChangeMainMongoEntity mainEntity = logDataChangeMongoMainRepository.findByOrderCode(order_code)
                .orElse(null);
        
        // 如果主记录不存在，返回空对象
        if (mainEntity == null) {
            log.debug("未找到订单编码对应的主记录: {}", order_code);
            return new SLogDataChangeMainVo();
        }
        
        // 2. 创建返回VO对象并复制主记录属性
        SLogDataChangeMainVo mainVo = new SLogDataChangeMainVo();
        BeanUtilsSupport.copyProperties(mainEntity, mainVo);
        
        // 3. 构建查询条件 - 使用精确匹配而非正则表达式提高效率
        Criteria criteria = Criteria.where("order_main_id").is(mainEntity.getId());
        Query query = Query.query(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "u_time")); // 按更新时间降序排序
        
        // 4. 执行查询并获取结果
        List<SLogDataChangeMongoEntity> changeEntities = mongoTemplate.find(query, SLogDataChangeMongoEntity.class);
        
        // 5. 处理查询结果 - 使用Stream API简化处理逻辑
        List<SLogDataChangeMongoVo> changeVoList = changeEntities.stream()
            .flatMap(entity -> {
                // 将每个实体的详情列表转换为VO对象
                List<SLogDataChangeDetailMongoVo> details = BeanUtilsSupport.copyProperties(
                    entity.getDetails(), SLogDataChangeDetailMongoVo.class);
                
                // 为每个详情创建一个变更VO对象
                return details.stream().map(detail -> {
                    SLogDataChangeMongoVo changeVo = new SLogDataChangeMongoVo();
                    BeanUtilsSupport.copyProperties(entity, changeVo);
                    
                    // 设置详情属性
                    changeVo.setClm_name(detail.getClm_name());
                    changeVo.setClm_label(detail.getClm_label());
                    changeVo.setOld_value(detail.getOld_value());
                    changeVo.setNew_value(detail.getNew_value());
                    
                    return changeVo;
                });
            })
            .collect(Collectors.toList());
        
        // 6. 设置变更列表并返回结果
        mainVo.setDataChangeMongoVoList(changeVoList);
        return mainVo;
    }

    @Override
    public SLogDataChangeOperateMongoVo findOperationByRequestId(String request_id) {
        SLogDataChangeOperateMongoEntity sLogDataChangeOperateMongoEntity = logDataChangeOperateMongoMainRepository.findByRequestId(request_id).orElse(null);

        SLogDataChangeOperateMongoVo sLogDataChangeOperateMongoVo = new SLogDataChangeOperateMongoVo();
        // 创建查询条件
        Criteria criteria = new Criteria();
        criteria.and("request_id").regex(regexPattern(request_id));

        // 创建查询
        Query query = Query.query(criteria);

        // 添加排序条件
        query.with(Sort.by(Sort.Direction.DESC, "u_time")); // 按照 "u_time" 字段进行降序排序
        // 执行查询并获取结果
        List<SLogDataChangeMongoEntity> list = mongoTemplate.find(query, SLogDataChangeMongoEntity.class);

//        SLogDataChangeOperateMongoEntity sLogDataChangeOperateMongoEntity = logChangeOperateMongoService.findByRequestId(request_id);

        BeanUtilsSupport.copyProperties(sLogDataChangeOperateMongoEntity, sLogDataChangeOperateMongoVo);

        List<SLogDataChangeMongoVo> sLogDataChangeMongoVoList = new ArrayList<>();
        for (SLogDataChangeMongoEntity entity: list) {
            List<SLogDataChangeDetailMongoVo> details = BeanUtilsSupport.copyProperties(entity.getDetails(), SLogDataChangeDetailMongoVo.class);
            for (SLogDataChangeDetailMongoVo detail : details) {
                SLogDataChangeMongoVo sLogDataChangeMongoVo = new SLogDataChangeMongoVo();
                BeanUtilsSupport.copyProperties(entity, sLogDataChangeMongoVo);
                /**
                 * 设置clm_name, clm_label,old_value, new_value
                 */
                sLogDataChangeMongoVo.setClm_name(detail.getClm_name());
                sLogDataChangeMongoVo.setClm_label(detail.getClm_label());
                sLogDataChangeMongoVo.setOld_value(detail.getOld_value());
                sLogDataChangeMongoVo.setNew_value(detail.getNew_value());
                sLogDataChangeMongoVoList.add(sLogDataChangeMongoVo);
            }
        }

        sLogDataChangeOperateMongoVo.setDataChangeMongoVoList(sLogDataChangeMongoVoList);
        return sLogDataChangeOperateMongoVo;
    }

    /**
     * 定时任务调用
     * mongodb操作：
     * 1、根据s_log_data_change_detail.id查询出order_code。
     * 2、如果order_code不为空，通过此order_code ,去找s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
     * 3、如果order_code为空，则需要根据table_id去表中搜索order_code。通过反射去调用。
     * 4、找到order_code后，通过此order_code ,去找s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
     */
//    @SysLogAnnotion("数据变更日志定时任务：数据变更日志中，没找到order_code，发起定时任务开始找。")
    public void findOrderCode(String parameterClass , String parameter) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        SDataChangeLogFindOrderCodeVo vo = JSON.parseObject(parameter, SDataChangeLogFindOrderCodeVo.class);
        log.debug("定时任务：开始");
        /**
         * mongodb操作：
         * 1、根据s_log_data_change_detail.id查询出order_code。
         * 2、如果order_code不为空，通过此order_code ,去找s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
         * 3、如果order_code为空，则需要根据table_id去表中搜索order_code。通过反射去调用。
         * 4、找到order_code后，通过此order_code ,去找s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
         */
        SLogDataChangeMongoEntity entity = findById(vo.getS_log_data_change_detail_id());
        if (entity.getOrder_main_id() != null) {
            log.debug("定时任务：结束");
            return;
        }
        if (entity.getOrder_code() != null) {
            // 通过order_code找到s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
            Optional<SLogDataChangeMainMongoEntity> mainMongoEntity = mainRepository.findByOrderCode(entity.getOrder_code());
            if (mainMongoEntity.isPresent()) {
                // 不为空，找到了
                entity.setOrder_main_id(mainMongoEntity.get().getId());
                repository.save(entity);

                // 更新SLogDataChangeMainMongoEntity的，u_id，u_name，u_time
                mainMongoEntity.get().setU_id(entity.getU_id().toString());
                mainMongoEntity.get().setU_name(entity.getU_name());
                mainMongoEntity.get().setU_time(entity.getU_time());
                mainRepository.save(mainMongoEntity.get());;
                log.debug("定时任务：结束");
                return;
            } else {
                // 为空，写异常，注意定时任务不可以排除异常，记录异常就行了
//                throw new BusinessException("找不到order_code：" + entity.getOrder_code());
                log.debug("找不到order_code：" + entity.getOrder_code());
                log.debug("定时任务：结束");
            }
        } else {
            // 通过table_id去表中搜索order_code。通过反射去调用。
            String _class_name = entity.getClass_name();
            String _functionName = "getOrderCode";
            Object arg1 = entity.getTable_id();
//            String order_code = (String) ReflectionUtil.invokex(_class_name, _functionName, arg1);
            // 通过order_code找到s_log_data_change_main中的id，然后更新到s_log_data_change_detail中的order_main_id中
            Optional<SLogDataChangeMainMongoEntity> mainMongoEntity = mainRepository.findByOrderCode(entity.getOrder_code());
            if (mainMongoEntity.isPresent()) {
                // 不为空，找到了
                entity.setOrder_main_id(mainMongoEntity.get().getId());
                repository.save(entity);

                // 更新SLogDataChangeMainMongoEntity的，u_id，u_name，u_time
                mainMongoEntity.get().setU_id(entity.getU_id().toString());
                mainMongoEntity.get().setU_name(entity.getU_name());
                mainMongoEntity.get().setU_time(entity.getU_time());
                mainRepository.save(mainMongoEntity.get());;
                log.debug("定时任务：结束");
                return;
            } else {
                // 为空，写异常，注意定时任务不可以排除异常，记录异常就行了
//                throw new BusinessException("找不到order_code：" + entity.getOrder_code());
                log.debug("找不到order_code：" + entity.getOrder_code());
                log.debug("定时任务：结束");
            }
        }
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public SLogDataChangeMongoEntity findById(String id) {
        Optional<SLogDataChangeMongoEntity> entity = repository.findById(id);
        return entity.orElse(null);
    }

    /**
     * 添加按request_id删除数据的方法
     * @param requestId
     */
    @Override
    public void deleteByRequestId(String requestId) {
        repository.deleteByRequestId(requestId);
    }

    /**
     * 根据SDataChangeLogVo对象生成SLogDataChangeMongoEntity对象
     *
     * @param vo SDataChangeLogVo对象
     * @return SLogDataChangeMongoEntity对象
     */
    private SLogDataChangeMongoEntity getDataChangeEntity(SDataChangeLogVo vo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {

        List<SLogDataChangeDetailMongoEntity> details = new ArrayList<>();

//        Object beforeEntity = JSON.parseObject(vo.getBeforeVo().getResult(), BAdjustEntity.class);
//        Object afterEntity = JSON.parseObject(vo.getAfterVo().getResult(), BAdjustEntity.class);
        switch (vo.getSqlCommandType()) {
            case "INSERT":
//                Object afterEntity = JSON.parseObject(vo.getAfterVo().getResult(), BAdjustEntity.class);
                SDataChangeColumnsVo columnsInsertVo = handleInsert(vo.getAfterVo());
                details = convertToDetailList(columnsInsertVo.getColumns());
                log.debug("columnsVo:{}", columnsInsertVo);
                break;
            case "UPDATE":
                SDataChangeColumnsVo columnsUpdateVo = handleUpdate(vo.getBeforeVo(), vo.getAfterVo());
                details = convertToDetailList(columnsUpdateVo.getColumns());
                log.debug("columnsVo:{}", columnsUpdateVo);
                break;
            case "DELETE":
                SDataChangeColumnsVo columnsDeleteVo = handleDelete(vo.getBeforeVo());
                details = convertToDetailList(columnsDeleteVo.getColumns());
                log.debug("columnsVo:{}", columnsDeleteVo);
                break;
        }

        SLogDataChangeMongoEntity entity = new SLogDataChangeMongoEntity();
        entity.setName(vo.getName());
        entity.setType(getTypeBySqlCommandType(vo.getSqlCommandType()));
        entity.setSqlCommandType(vo.getSqlCommandType());
        entity.setTable_name(vo.getTable_name());
        entity.setEntity_name(vo.getEntity_name());
        entity.setOrder_code(vo.getOrder_code());
        entity.setClass_name(vo.getClass_name());
        entity.setTable_id(vo.getTable_id());
        entity.setDetails(details);
        entity.setRequest_id(vo.getRequest_id());

        switch (vo.getSqlCommandType()) {
            case "INSERT":
                entity.setU_id(vo.getAfterVo().getU_id());
                entity.setU_name(vo.getAfterVo().getU_name());
//                entity.setU_time(vo.getAfterVo().getU_time());
                // 这部分可以通过查询数据来获取
                entity.setC_time(getCTimeBySelectDb(vo.getAfterVo()));
                // 这部分可以通过查询数据来获取
                entity.setU_time(getUTimeBySelectDb(vo.getAfterVo()));
                break;
            case "UPDATE":
                entity.setU_id(vo.getAfterVo().getU_id());
                entity.setU_name(vo.getAfterVo().getU_name());
                // 这部分可以通过查询数据来获取
                entity.setC_time(getCTimeBySelectDb(vo.getAfterVo()));
                // 这部分可以通过查询数据来获取
                entity.setU_time(getUTimeBySelectDb(vo.getAfterVo()));
                break;
            case "DELETE":
                // 这部分可以通过查询数据来获取
                entity.setU_id(vo.getBeforeVo().getU_id());
                entity.setU_name(vo.getBeforeVo().getU_name());
                // 这部分可以通过查询数据来获取
                entity.setC_time(getCTimeBySelectDb(vo.getAfterVo()));
                // 这部分可以通过查询数据来获取
                entity.setU_time(getUTimeBySelectDb(vo.getAfterVo()));
                break;
        }
        return entity;
    }



    /**
     * 处理插入操作。
     *
     * @param afterVo 插入操作后的数据对象。
     * @return 插入操作的数据变更详情。
     */
    private SDataChangeColumnsVo handleInsert(SDataChangeLogDetailVo afterVo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        SDataChangeColumnsVo columnsVo = new SDataChangeColumnsVo();
        List<SDataChangeColumnVo> columnList = new ArrayList<>();
        Object afterEntity = JSON.parseObject(afterVo.getResult(), Class.forName(afterVo.getResult_bean_name()));

        Field[] fields = afterEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // 确保私有字段也可以访问
                Object afterValue = field.get(afterEntity);
                SDataChangeColumnVo columnVo = getColumnVo(field, afterVo.getTableColumns());
                if(columnVo == null) {
                    continue;
                } else {

                    List<SDataChangeColumnVo> rtns = setDataChangeColumnvo(
                            "INSERT",
                            field,
                            afterVo.getClass_name(),
                            null,
                            afterEntity);
                    columnList.addAll(rtns);
                }
            } catch (IllegalAccessException e) {
                log.error("获取字段值失败：" + field.getName());
                throw new BusinessException(e);
            }
        }
        columnsVo.setColumns(columnList);
        return columnsVo;
    }

    /**
     * 处理删除操作。
     *
     * @param beforeVo 删除操作前的数据对象。
     * @return 插入操作的数据变更详情。
     */
    private SDataChangeColumnsVo handleDelete(SDataChangeLogDetailVo beforeVo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        SDataChangeColumnsVo columnsVo = new SDataChangeColumnsVo();
        List<SDataChangeColumnVo> columnList = new ArrayList<>();
        Object beforeEntity = JSON.parseObject(beforeVo.getResult(), Class.forName(beforeVo.getResult_bean_name()));

        Field[] fields = beforeEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // 确保私有字段也可以访问
                Object beforeValue = field.get(beforeEntity);
                SDataChangeColumnVo columnVo = getColumnVo(field, beforeVo.getTableColumns());
                if(columnVo == null) {
                    continue;
                } else {
//                    columnVo.setOld_value(beforeValue);
//                    columnVo.setNew_value(null);
//                    columnList.add(columnVo);
                    List<SDataChangeColumnVo> rtns = setDataChangeColumnvo(
                            "DELETE",
                            field,
                            beforeVo.getClass_name(),
                            beforeEntity,
                            null);
                    columnList.addAll(rtns);
                }

            } catch (IllegalAccessException e) {
                log.error("获取字段值失败：" + field.getName());
                throw new BusinessException(e);
            }
        }

        columnsVo.setColumns(columnList);
        return columnsVo;
    }

    /**
     * 处理更新操作。
     *
     * @param beforeVo 更新操作前的数据对象。
     * @param afterVo 更新操作后的数据对象。
     * @return 插入操作的数据变更详情。
     */
    private SDataChangeColumnsVo handleUpdate(SDataChangeLogDetailVo beforeVo, SDataChangeLogDetailVo afterVo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        SDataChangeColumnsVo columnsVo = new SDataChangeColumnsVo();
        List<SDataChangeColumnVo> columnList = new ArrayList<>();
        Object beforeEntity = JSON.parseObject(beforeVo.getResult(), Class.forName(beforeVo.getResult_bean_name()));
        Object afterEntity = JSON.parseObject(afterVo.getResult(), Class.forName(afterVo.getResult_bean_name()));

        Field[] fields = afterEntity.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true); // 确保私有字段也可以访问
                Object beforeValue = field.get(beforeEntity);
                Object afterValue = field.get(afterEntity);
                SDataChangeColumnVo columnVo = getColumnVo(field, beforeVo.getTableColumns());
                if(columnVo == null) {
                    continue;
                } else {
                    if (areValuesEqual(field, beforeValue, afterValue)) {
                        continue;
                    } else {
//                        columnVo.setOld_value(beforeValue);
//                        columnVo.setNew_value(afterValue);
//                        columnList.add(columnVo);
                        List<SDataChangeColumnVo> rtns = setDataChangeColumnvo(
                                "UPDATE",
                                field,
                                beforeVo.getClass_name(),
                                beforeEntity,
                                afterEntity);
                        columnList.addAll(rtns);
                    }
                }

            } catch (IllegalAccessException e) {
                log.error("获取字段值失败：" + field.getName());
                throw new BusinessException(e);
            }
        }

        columnsVo.setColumns(columnList);
        return columnsVo;
    }

    /**
     * 根据sqlCommandType获取类型
     *
     */
    private String getTypeBySqlCommandType(String sqlCommandType) {
        if ("INSERT".equals(sqlCommandType)) {
            return "新增";
        }
        if ("UPDATE".equals(sqlCommandType)) {
            return "更新";
        }
        if ("DELETE".equals(sqlCommandType)) {
            return "删除";
        }
        return "错误";
    }


    /**
     * 根据给定的字段和列VO，获取列VO
     * @param field 字段
     * @param columnsVo 列VO
     * @return 列VO对象或null
     */
    private SDataChangeColumnVo getColumnVo(Field field , SDataChangeColumnsVo columnsVo) {
        SDataChangeColumnVo columnVo = columnsVo.getColumns_map().get(field.getName());
        if (columnVo != null) {
            SDataChangeColumnVo rtnColumnVo = new SDataChangeColumnVo();
            rtnColumnVo.setClm_name(columnVo.getClm_name());
            rtnColumnVo.setClm_label(columnVo.getClm_label());
            return rtnColumnVo;
        }
        return null;
    }

    /**
     * 将SDataChangeColumnsVo对象转换为SLogDataChangeDetailMongoEntity对象的列表。
     *
     * @param columnsVo List<SDataChangeColumnVo>，用于转换细节列表
     * @return 转换后的SLogDataChangeDetailMongoEntity对象的列表
     */
    private List<SLogDataChangeDetailMongoEntity> convertToDetailList(List<SDataChangeColumnVo> columnsVo) {
        List<SLogDataChangeDetailMongoEntity> details = new ArrayList<>();
        for (SDataChangeColumnVo columnVo : columnsVo) {
            SLogDataChangeDetailMongoEntity detail = new SLogDataChangeDetailMongoEntity();
            detail.setClm_name(columnVo.getClm_name());
            detail.setClm_label(columnVo.getClm_label());
            detail.setOld_value(columnVo.getOld_value());
            detail.setNew_value(columnVo.getNew_value());
            details.add(detail);
        }
        return details;
    }


    /**
     * 比较两个值是否相等
     * @param beforeValue
     * @param afterValue
     * @return
     */
    private boolean areValuesEqual(Field field, Object beforeValue, Object afterValue) {
        DataChangeLabelAnnotation label = field.getAnnotation(DataChangeLabelAnnotation.class);

        // 判断是否需要常显示
        if (label != null && label.fixed()) {
            return false;
        }

        if (beforeValue == null && afterValue == null) {
            return true;
        }
        if (beforeValue == null || afterValue == null) {
            return false;
        }

        // 对于数组，使用 Arrays.equals
        if (beforeValue.getClass().isArray() && afterValue.getClass().isArray()) {
            return Arrays.equals((Object[]) beforeValue, (Object[]) afterValue);
        }

        // 对于其他对象，使用 equals 方法
        return beforeValue.equals(afterValue);
    }

    /**
     * 设置SDataChangeColumnVo对象的列表
     * @param commandType 命令类型
     * @param field 字段对象
     * @param class_name 类名
     * @param beforeBean 在beforeBean下执行field.get(beforeBean)获取到的对象
     * @param afterBean 在afterBean下执行field.get(afterBean)获取到的对象
     * @return SDataChangeColumnVo对象的列表
     * @throws IllegalAccessException 当访问字段或方法出现错误时抛出异常
     * @throws ClassNotFoundException 当找不到指定的类时抛出异常
     * @throws InvocationTargetException 当调用构造方法时出现错误时抛出异常
     * @throws NoSuchMethodException 当方法不存在时抛出异常
     */
    private List<SDataChangeColumnVo> setDataChangeColumnvo(String commandType,
                                                            Field field,
                                                            String class_name,
                                                            Object beforeBean,
                                                            Object afterBean
                                                            ) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
        List<SDataChangeColumnVo> columnList = new ArrayList<>();
        Object beforeValue = (beforeBean != null) ? field.get(beforeBean) : null;
        Object afterValue = (afterBean != null) ? field.get(afterBean) : null;


        DataChangeLabelAnnotation label = field.getAnnotation(DataChangeLabelAnnotation.class);
        if (label != null && StringUtils.isBlank(label.extension()) && StringUtils.isBlank(label.dictExtension())) {
            /**
             *  1、这部分为正常的逻辑
             *  2、需要排除DataChangeLabelAnnotation有扩展逻辑的部分
             */
            SDataChangeColumnVo vo1 = new SDataChangeColumnVo();
            vo1.setClm_name(field.getName());
            vo1.setClm_label(field.getAnnotation(DataChangeLabelAnnotation.class).value());
            boolean add_status_normal = false;
            switch (commandType) {
                case "INSERT":
                    vo1.setOld_value("");
                    vo1.setNew_value(afterValue);
                    if (!isObjectEmpty(afterValue)) {
                        add_status_normal = true;
                    }
                    break;
                case "UPDATE":
                    vo1.setOld_value(beforeValue);
                    vo1.setNew_value(afterValue);
                    if(!Objects.equals(afterValue, beforeValue)) {
                        add_status_normal = true;
                    }
                    break;
                case "DELETE":
                    vo1.setOld_value(beforeValue);
                    vo1.setNew_value("");
                    if (!isObjectEmpty(beforeValue)) {
                        add_status_normal = true;
                    }
                    break;
            }
            if (add_status_normal) {
                // 数据有变化
                columnList.add(vo1);
            }
        }
        if (label != null && StringUtils.isNotBlank(label.extension())) {
            /**
             *  1、DataChangeLabelAnnotation有扩展逻辑的部分
             */
            boolean add_status_extension = false;
            // 判断扩展属性:有扩展需求，此处通过反射调用
            SDataChangeColumnVo vo2 = new SDataChangeColumnVo();
            String _class_name = class_name;
            String _functionName = label.extension();
            SLogDataChangeDetailVo rtn_before = null;
            SLogDataChangeDetailVo rtn_after = null;
            String clm_name = field.getName();
            String clm_label = field.getAnnotation(DataChangeLabelAnnotation.class).value();
            
            log.debug("----------------开始获取判断扩展属性-start-----------");
            log.debug("获取的字段{}，中文{}", clm_name,clm_label);
            
            switch (commandType) {
                case "INSERT":
                    Object insertArg1 = afterValue;
                    String argInsertArg1 = (insertArg1 != null) ? insertArg1.toString() : null;
                    rtn_before = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            argInsertArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_before.getClm_name());
                    vo2.setClm_label(rtn_before.getClm_label());
                    vo2.setOld_value("");
                    vo2.setNew_value(rtn_before.getNew_value());
                    if(!isObjectEmpty(rtn_before.getNew_value())) {
                        add_status_extension = true;
                    }
                    break;
                case "UPDATE":
                    Object updateBeforeArg1 = beforeValue;
                    Object updateAfterArg1 = afterValue;
                    String argUpdateBeforeArg1 = (updateBeforeArg1 != null) ? updateBeforeArg1.toString() : null;
                    String argUpdateAfterArg1 = (updateAfterArg1 != null) ? updateAfterArg1.toString() : null;

                    rtn_before = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            argUpdateBeforeArg1,
                            JSONObject.toJSONString(beforeBean),
                            clm_name,
                            clm_label);
                    rtn_after = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            argUpdateAfterArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_before.getClm_name());
                    vo2.setClm_label(rtn_before.getClm_label());
                    vo2.setOld_value(rtn_before.getOld_value());
                    vo2.setNew_value(rtn_after.getNew_value());
                    if(!Objects.equals(rtn_before.getNew_value(), rtn_after.getNew_value())) {
                        add_status_extension = true;
                    }
                    break;
                case "DELETE":
                    Object deleteArg1 = beforeValue;
                    String argDeleteArg1 = (deleteArg1 != null) ? deleteArg1.toString() : null;
                    rtn_after = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            argDeleteArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_after.getClm_name());
                    vo2.setClm_label(rtn_after.getClm_label());
                    vo2.setOld_value(rtn_after.getOld_value());
                    vo2.setNew_value("");
                    if(!isObjectEmpty(rtn_before.getOld_value())) {
                        add_status_extension = true;
                    }
                    break;
            }

            log.debug("----------------开始获取判断扩展属性-end-----------");
            if(add_status_extension) {
                // 数据有变化
                columnList.add(vo2);
            }
        }
        /**
         * 按字典扩展逻辑
         */
        if (label != null && StringUtils.isNotBlank(label.dictExtension())) {
            /**
             *  1、DataChangeLabelAnnotation有扩展逻辑的部分
             */
            boolean add_status_extension = false;
            // 判断扩展属性:有扩展需求，此处通过反射调用
            SDataChangeColumnVo vo2 = new SDataChangeColumnVo();
            String _class_name = class_name;
            String _functionName = label.dictExtension();
            String _dict_type = label.dictExtensionType();
            SLogDataChangeDetailVo rtn_before = null;
            SLogDataChangeDetailVo rtn_after = null;
            String clm_name = field.getName();
            String clm_label = field.getAnnotation(DataChangeLabelAnnotation.class).value();

            log.debug("----------------开始获取判断扩展属性-start-----------");
            log.debug("获取的字段{}，中文{}", clm_name,clm_label);

            switch (commandType) {
                case "INSERT":
                    Object insertArg1 = afterValue;
                    String argInsertArg1 = (insertArg1 != null) ? insertArg1.toString() : null;
                    rtn_before = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            _dict_type,
                            argInsertArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_before.getClm_name());
                    vo2.setClm_label(rtn_before.getClm_label());
                    vo2.setOld_value("");
                    vo2.setNew_value(rtn_before.getNew_value());
                    if(!isObjectEmpty(rtn_before.getNew_value())) {
                        add_status_extension = true;
                    }
                    break;
                case "UPDATE":
                    Object updateBeforeArg1 = beforeValue;
                    Object updateAfterArg1 = afterValue;
                    String argUpdateBeforeArg1 = (updateBeforeArg1 != null) ? updateBeforeArg1.toString() : null;
                    String argUpdateAfterArg1 = (updateAfterArg1 != null) ? updateAfterArg1.toString() : null;

                    rtn_before = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            _dict_type,
                            argUpdateBeforeArg1,
                            JSONObject.toJSONString(beforeBean),
                            clm_name,
                            clm_label);
                    rtn_after = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            _dict_type,
                            argUpdateAfterArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_before.getClm_name());
                    vo2.setClm_label(rtn_before.getClm_label());
                    vo2.setOld_value(rtn_before.getOld_value());
                    vo2.setNew_value(rtn_after.getNew_value());
                    if(!Objects.equals(rtn_before.getNew_value(), rtn_after.getNew_value())) {
                        add_status_extension = true;
                    }
                    break;
                case "DELETE":
                    Object deleteArg1 = beforeValue;
                    String argDeleteArg1 = (deleteArg1 != null) ? deleteArg1.toString() : null;
                    rtn_after = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                            _class_name,
                            _functionName,
                            _dict_type,
                            argDeleteArg1,
                            JSONObject.toJSONString(afterBean),
                            clm_name,
                            clm_label);
                    vo2.setClm_name(rtn_after.getClm_name());
                    vo2.setClm_label(rtn_after.getClm_label());
                    vo2.setOld_value(rtn_after.getOld_value());
                    vo2.setNew_value("");
                    if(!isObjectEmpty(rtn_before.getOld_value())) {
                        add_status_extension = true;
                    }
                    break;
            }

            log.debug("----------------开始获取判断扩展属性-end-----------");
            if(add_status_extension) {
                // 数据有变化
                columnList.add(vo2);
            }
        }
        return columnList;
    }

    /**
     * isObjectEmpty
     * 判断参数object是否为空，如果返回为空则返回true
     * 然后调用StringUtil.isNotBlank
     */
    private boolean isObjectEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (StringUtils.isBlank(object.toString())){
            return true;
        }
        return false;
    }

    /**
     * 通过反射，获取到表格中最新的c_time，{getCTimeExtension}
     * @param paramVo
     * @return
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    private LocalDateTime getCTimeBySelectDb(SDataChangeLogDetailVo paramVo) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        try {
            // 获取Class实例
            Class<?> clazz = Class.forName(paramVo.getEntity_name());
            // 获取指定字段，使用getDeclaredField
            Field field = clazz.getDeclaredField("c_time");
            // 确保我们可以访问私有字段
            field.setAccessible(true);
            String _class_name = paramVo.getClass_name();
            String _functionName = "getCTimeExtension";
            String clm_name = "c_time";
            String clm_label = "创建时间";
            SLogDataChangeDetailVo c_time_vo = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                    _class_name,
                    _functionName,
                    null,
                    paramVo.getResult(),
                    clm_name,
                    clm_label);
            return (LocalDateTime) c_time_vo.getNew_value();
        } catch (Exception e) {
            log.error("动态获取c_time，失败", e);
            return null;
        }
    }

    /**
     * 通过反射，获取到表格中最新的u_time，{getUTimeExtension}
     * @param paramVo
     * @return
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    private LocalDateTime getUTimeBySelectDb(SDataChangeLogDetailVo paramVo) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        try {
            // 获取Class实例
            Class<?> clazz = Class.forName(paramVo.getEntity_name());
            // 获取指定字段，使用getDeclaredField
            Field field = clazz.getDeclaredField("u_time");
            // 确保我们可以访问私有字段
            field.setAccessible(true);
            String _class_name = paramVo.getClass_name();
            String _functionName = "getUTimeExtension";
            String clm_name = "u_time";
            String clm_label = "更新时间";
            SLogDataChangeDetailVo u_time_vo = (SLogDataChangeDetailVo) ReflectionUtil.invokex(
                    _class_name,
                    _functionName,
                    null,
                    paramVo.getResult(),
                    clm_name,
                    clm_label);
            return (LocalDateTime) u_time_vo.getNew_value();
        } catch (Exception e) {
            log.error("动态获取u_time，失败", e);
            return null;
        }
    }
}
