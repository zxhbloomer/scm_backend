package com.xinyirunscm.scm.clickhouse.service.datachange;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailOldNewVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMongoVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateMongoVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateClickHouseVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.SLogDataChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.SDataChangeLogVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnsVo;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.reflection.ReflectionUtil;
import com.xinyirunscm.scm.clickhouse.entity.datachange.SLogDataChangeDetailClickHouseEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.repository.datachange.SLogDataChangeDetailClickHouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 数据变更详细日志 ClickHouse 服务类
 * 专门处理 s_log_data_change_detail 表的所有业务逻辑
 * 
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Slf4j
@Service
public class SLogDataChangeDetailClickHouseService {

    private final SLogDataChangeDetailClickHouseRepository sLogDataChangeDetailRepository;

    public SLogDataChangeDetailClickHouseService(SLogDataChangeDetailClickHouseRepository sLogDataChangeDetailRepository) {
        this.sLogDataChangeDetailRepository = sLogDataChangeDetailRepository;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入数据变更详细日志 - 接受 SLogDataChangeDetailClickHouseVo 参数
     * 在方法内部转换为 SLogDataChangeDetailClickHouseEntity
     */
    public void insert(SDataChangeLogVo detailLogVo) {
        try {
            SLogDataChangeDetailClickHouseVo vo = getDataChangeVo((SDataChangeLogVo) detailLogVo);

            // 转换为 ClickHouse 实体
            SLogDataChangeDetailClickHouseEntity detailLogEntity = convertVoToEntity(vo);
            detailLogEntity.setTenant_code(detailLogVo.getTenant_code());
            // 执行插入
            sLogDataChangeDetailRepository.insert(detailLogEntity);
            
            log.info("插入数据变更详细日志成功，request_id: {}, 业务名: {}, 操作类型: {}, 表名: {}", 
                    detailLogEntity.getRequest_id(), detailLogEntity.getName(), 
                    detailLogEntity.getType(), detailLogEntity.getTable_name());
                       
        } catch (Exception e) {
            log.error("插入数据变更详细日志失败，request_id: {}", 
                     detailLogVo != null ? detailLogVo.getRequest_id() : "null", e);
            throw new ClickHouseException("插入数据变更详细日志失败", e);
        }
    }

    /**
     * 异步插入数据变更详细日志 - 高性能场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> insertAsync(SDataChangeLogVo detailLogVo) {
        try {
            insert(detailLogVo);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步插入数据变更详细日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入数据变更详细日志 - 最佳性能方案
     */
    public void batchInsert(List<SLogDataChangeDetailClickHouseVo> detailLogVos) {
        if (detailLogVos == null || detailLogVos.isEmpty()) {
            log.warn("批量插入数据变更详细日志数据为空，跳过操作");
            return;
        }

        try {
            // 转换为 ClickHouse 实体列表
            List<SLogDataChangeDetailClickHouseEntity> detailLogEntities = detailLogVos.stream()
                    .map(this::convertVoToEntity)
                    .toList();
            
            // 执行批量插入
            sLogDataChangeDetailRepository.batchInsert(detailLogEntities);
            
            log.info("批量插入数据变更详细日志成功，数量: {}", detailLogVos.size());
            
        } catch (Exception e) {
            log.error("批量插入数据变更详细日志失败，数量: {}", detailLogVos.size(), e);
            throw new ClickHouseException("批量插入数据变更详细日志失败", e);
        }
    }

    /**
     * 异步批量插入数据变更详细日志 - 高吞吐量场景
     */
    @Async("clickhouseTaskExecutor")
    public CompletableFuture<Void> batchInsertAsync(List<SLogDataChangeDetailClickHouseVo> detailLogVos) {
        try {
            batchInsert(detailLogVos);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("异步批量插入数据变更详细日志失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 根据订单编码查询数据变更记录及其详情
     * 严格按照MongoDB版本的逻辑实现，使用Stream API处理详情展开
     *
     * @param order_code 订单编码
     * @return 数据变更操作记录及其详情的VO对象
     */
    public SLogDataChangeOperateMongoVo findMainByOrderCode(String order_code) {
        try {
            // 1. 获取租户代码用于数据隔离
            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            
            // 2. 查询所有相关记录（对应MongoDB中的主记录+详情记录查询）
            List<SLogDataChangeDetailClickHouseVo> changeEntities = 
                sLogDataChangeDetailRepository.findByOrderCode(order_code, tenantCode);
            
            // 如果记录不存在，返回空对象（对应MongoDB版本行143-146逻辑）
            if (changeEntities == null || changeEntities.isEmpty()) {
                log.debug("未找到订单编码对应的记录: {}", order_code);
                return new SLogDataChangeOperateMongoVo();
            }
            
            // 3. 创建返回VO对象（对应MongoDB版本行148-150逻辑）
            SLogDataChangeOperateMongoVo operateVo = new SLogDataChangeOperateMongoVo();
            
            // 4. 处理查询结果 - 严格按照MongoDB版本的Stream API逻辑（行162-183）
            List<SLogDataChangeMongoVo> changeVoList = changeEntities.stream()
                .flatMap(entity -> {
                    // 获取ClickHouse中已解析的details列表（Repository层已完成JSON解析）
                    List<SLogDataChangeDetailOldNewVo> details = entity.getDetails() != null ? entity.getDetails() : new ArrayList<>();
                    
                    // 为每个详情创建一个变更VO对象（对应MongoDB版本的details.stream().map逻辑）
                    return details.stream().map(detail -> {
                        SLogDataChangeMongoVo changeVo = new SLogDataChangeMongoVo();
                        // 复制ClickHouse实体的基本属性到changeVo
                        BeanUtilsSupport.copyProperties(entity, changeVo);
                        
                        // 设置详情属性（对应MongoDB版本行174-178）
                        changeVo.setClm_name(detail.getClm_name());
                        changeVo.setClm_label(detail.getClm_label());
                        changeVo.setOld_value(detail.getOld_value());
                        changeVo.setNew_value(detail.getNew_value());
                        
                        return changeVo;
                    });
                })
                .collect(Collectors.toList());
            
            // 5. 设置变更列表并返回结果（对应MongoDB版本行186）
            operateVo.setDataChangeMongoVoList(changeVoList);
            
            log.debug("根据订单编码查询数据变更记录成功: order_code={}, 记录数={}", 
                     order_code, changeVoList.size());
            
            return operateVo;
            
        } catch (Exception e) {
            log.error("根据订单编码查询数据变更记录失败: order_code={}", order_code, e);
            throw new ClickHouseException("根据订单编码查询数据变更记录失败", e);
        }
    }

    /**
     * 根据请求ID查询数据变更操作记录及其详情
     * 严格按照MongoDB版本LogChangeMongoServiceImpl.findOperationByRequestId()业务逻辑实现
     * 使用新增的dataChangeList字段，数据结构已完全对齐
     *
     * @param request_id 请求ID
     * @return 数据变更操作记录及其详情的VO对象
     */
    public SLogDataChangeOperateClickHouseVo findOperationByRequestId(String request_id) {
        try {
            // 1. 获取租户代码用于数据隔离
            String tenantCode = DataSourceHelper.getCurrentDataSourceName();
            
            // 2. 调用Repository层的模糊查询方法（对应MongoDB的regex查询）
            List<SLogDataChangeDetailClickHouseVo> changeEntities = 
                sLogDataChangeDetailRepository.findByRequestIdLike(request_id, tenantCode);
            
            // 3. 创建返回VO对象
            SLogDataChangeOperateClickHouseVo operateVo = new SLogDataChangeOperateClickHouseVo();
            
            // 4. 设置操作主记录信息（从第一条记录提取相关属性）
            if (!changeEntities.isEmpty()) {
                SLogDataChangeDetailClickHouseVo firstEntity = changeEntities.get(0);
                operateVo.setRequest_id(firstEntity.getRequest_id());
                operateVo.setTenant_code(firstEntity.getTenant_code());
                // 其他操作相关属性可根据业务需要设置
            }
            
            // 5. 严格按照MongoDB版本的for循环处理详情（行210-225逻辑）
            List<SLogDataChangeDetailClickHouseVo> dataChangeList = new ArrayList<>();
            for (SLogDataChangeDetailClickHouseVo entity : changeEntities) {
                // 获取ClickHouse中已解析的details列表（Repository层已完成JSON解析）
                List<SLogDataChangeDetailOldNewVo> details = entity.getDetails() != null ? entity.getDetails() : new ArrayList<>();
                
                // 为每个detail创建变更VO对象（对应MongoDB版本的for (SLogDataChangeDetailMongoVo detail : details)）
                for (SLogDataChangeDetailOldNewVo detail : details) {
                    SLogDataChangeDetailClickHouseVo changeVo = new SLogDataChangeDetailClickHouseVo();
                    // 复制ClickHouse实体的基本属性到changeVo
                    BeanUtilsSupport.copyProperties(entity, changeVo);
                    
                    // 设置详情属性（对应MongoDB版本行218-221）
                    changeVo.setClm_name(detail.getClm_name());
                    changeVo.setClm_label(detail.getClm_label());
                    changeVo.setOld_value(detail.getOld_value());
                    changeVo.setNew_value(detail.getNew_value());
                    
                    dataChangeList.add(changeVo);
                }
            }
            
            // 6. 设置到新增的dataChangeList字段（对应MongoDB版本行227）
            operateVo.setDataChangeList(dataChangeList);
            
            log.debug("根据请求ID查询数据变更操作记录成功: request_id={}, 详情记录数={}", 
                     request_id, dataChangeList.size());
            
            return operateVo;
            
        } catch (Exception e) {
            log.error("根据请求ID查询数据变更操作记录失败: request_id={}", request_id, e);
            throw new ClickHouseException("根据请求ID查询数据变更操作记录失败", e);
        }
    }

    /**
     * 分页查询数据变更详细日志 - 支持条件查询和排序
     * Controller层的核心方法，提供综合查询能力
     * 
     * @param searchCondition 查询条件（包含分页参数）
     * @return 分页结果包含VO列表
     */
    public IPage<SLogDataChangeDetailClickHouseVo> selectPage(SLogDataChangeDetailClickHouseVo searchCondition) {
        try {
            
            IPage<SLogDataChangeDetailClickHouseVo> result = sLogDataChangeDetailRepository.selectPageWithParams(searchCondition);
            
            log.info("分页查询数据变更详细日志成功，页号: {}, 页大小: {}, 总记录数: {}, 查询条件: [业务名: {}, 操作类型: {}, SQL命令类型: {}, 表名: {}, 实体类名: {}, 单号: {}, 类名: {}]", 
                    searchCondition.getPageCondition().getCurrent(),
                    searchCondition.getPageCondition().getSize(),
                    result.getTotal(),
                    searchCondition.getName(),
                    searchCondition.getType(),
                    searchCondition.getSql_command_type(),
                    searchCondition.getTable_name(),
                    searchCondition.getEntity_name(),
                    searchCondition.getOrder_code(),
                    searchCondition.getClass_name());
            
            return result;
            
        } catch (Exception e) {
            log.error("分页查询数据变更详细日志失败，查询条件: [业务名: {}, 操作类型: {}, SQL命令类型: {}, 表名: {}, 实体类名: {}, 单号: {}, 类名: {}]", 
                    searchCondition.getName(), 
                    searchCondition.getType(), 
                    searchCondition.getSql_command_type(),
                    searchCondition.getTable_name(),
                    searchCondition.getEntity_name(),
                    searchCondition.getOrder_code(),
                    searchCondition.getClass_name(), e);
            throw new ClickHouseException("分页查询数据变更详细日志失败", e);
        }
    }

    /**
     * 根据ID查询单条数据变更详细日志记录
     * Controller层的核心方法，提供根据ID的精确查询
     * 
     * @param searchCondition 查询条件VO（包含id字段）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogDataChangeDetailClickHouseVo getById(SLogDataChangeDetailClickHouseVo searchCondition) {
        try {
            // 验证输入参数
            if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
                log.warn("根据ID查询数据变更详细日志失败：查询条件为空或ID为空");
                return null;
            }
            
            String id = searchCondition.getId().trim();
            
            // 传递完整的查询条件（包含tenant_code）到Repository层
            SLogDataChangeDetailClickHouseVo result = sLogDataChangeDetailRepository.getById(searchCondition);
            
            if (result != null) {
                log.info("根据ID查询数据变更详细日志成功，ID: {}, 表名: {}, 操作类型: {}", 
                        id, result.getTable_name(), result.getType());
            } else {
                log.info("根据ID查询数据变更详细日志未找到记录，ID: {}", id);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("根据ID查询数据变更详细日志失败，ID: {}", 
                    searchCondition != null ? searchCondition.getId() : "null", e);
            throw new ClickHouseException("根据ID查询数据变更详细日志失败", e);
        }
    }

    // ==================== 私有辅助方法 ====================


    /**
     * 转换Vo对象到Entity对象，处理特殊字段类型转换
     */
    private SLogDataChangeDetailClickHouseEntity convertVoToEntity(SLogDataChangeDetailClickHouseVo vo) {
        // 基础属性拷贝
        SLogDataChangeDetailClickHouseEntity entity = (SLogDataChangeDetailClickHouseEntity) BeanUtilsSupport.copyProperties(vo, SLogDataChangeDetailClickHouseEntity.class);
        
        // 特殊处理：List<SLogDataChangeDetailOldNewVo> → JSON String 转换
        if (vo.getDetails() != null && !vo.getDetails().isEmpty()) {
            try {
                String detailsJson = JSON.toJSONString(vo.getDetails());
                entity.setDetails(detailsJson);
            } catch (Exception e) {
                log.error("转换details列表为JSON失败，request_id: {}", vo.getRequest_id(), e);
                entity.setDetails(null);  // 转换失败时设置为null
            }
        } else {
            entity.setDetails(null);  // 明确设置为null
        }
        
        // 如果需要设置默认值或特殊处理，在这里添加
        if (entity.getC_time() == null) {
            entity.setC_time(LocalDateTime.now());
        }
        
        return entity;
    }

    /**
     * 根据SDataChangeLogVo对象生成SLogDataChangeDetailClickHouseVo对象
     *
     * @param vo SDataChangeLogVo对象
     * @return SLogDataChangeDetailClickHouseVo对象
     */
    private SLogDataChangeDetailClickHouseVo getDataChangeVo(SDataChangeLogVo vo) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {

        List<SLogDataChangeDetailOldNewVo> details = new ArrayList<>();

        switch (vo.getSqlCommandType()) {
            case "INSERT":
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

        SLogDataChangeDetailClickHouseVo entity = new SLogDataChangeDetailClickHouseVo();
        entity.setName(vo.getName());
        entity.setType(getTypeBySqlCommandType(vo.getSqlCommandType()));
        entity.setSql_command_type(vo.getSqlCommandType());
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
     * 将SDataChangeColumnsVo对象转换为SLogDataChangeDetailOldNewVo对象的列表。
     *
     * @param columnsVo List<SDataChangeColumnVo>，用于转换细节列表
     * @return 转换后的SLogDataChangeDetailOldNewVo对象的列表
     */
    private List<SLogDataChangeDetailOldNewVo> convertToDetailList(List<SDataChangeColumnVo> columnsVo) {
        List<SLogDataChangeDetailOldNewVo> details = new ArrayList<>();
        for (SDataChangeColumnVo columnVo : columnsVo) {
            SLogDataChangeDetailOldNewVo detail = new SLogDataChangeDetailOldNewVo();
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
        if (label != null && com.xinyirun.scm.common.utils.string.StringUtils.isBlank(label.extension()) && com.xinyirun.scm.common.utils.string.StringUtils.isBlank(label.dictExtension())) {
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
        if (label != null && com.xinyirun.scm.common.utils.string.StringUtils.isNotBlank(label.extension())) {
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
        if (label != null && com.xinyirun.scm.common.utils.string.StringUtils.isNotBlank(label.dictExtension())) {
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
        if (com.xinyirun.scm.common.utils.string.StringUtils.isBlank(object.toString())){
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