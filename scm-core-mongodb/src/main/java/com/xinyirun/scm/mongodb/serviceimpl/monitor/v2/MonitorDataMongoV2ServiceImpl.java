package com.xinyirun.scm.mongodb.serviceimpl.monitor.v2;

import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.*;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.mongodb.repository.v2.MonitorMongoRepositoryV2;
import com.xinyirun.scm.mongodb.service.monitor.v2.IMonitorDataMongoV2Service;
import jakarta.annotation.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xinyirun.scm.common.utils.pattern.PatternUtils.regexPattern;

@Service
public class MonitorDataMongoV2ServiceImpl implements IMonitorDataMongoV2Service {

    @Resource
    private MonitorMongoRepositoryV2 repository;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存(单条)
     *
     * @param vo
     * @return
     */
    @Override
    public BMonitorDataMongoEntity saveAndFlush(BMonitorDataMongoEntity vo) {
        // 查询mongo, by monitor_id
        BMonitorDataMongoEntity entityByMonitorId = getEntityByMonitorId(vo.getMonitor_id());
        if (null != entityByMonitorId) {
            vo.setId(entityByMonitorId.getId());
        }
        vo.setIs_show(DictConstant.DICT_B_MONITOR_MONGO_IS_SHOW_T);
        vo.setIs_restore(DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_F);
        return mongoTemplate.save(vo);
    }

    public BMonitorDataMongoEntity getEntityByMonitorId(Integer monitorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("monitor_id").is(monitorId));
        return mongoTemplate.findOne(query, BMonitorDataMongoEntity.class);
    }

    /**
     * 根据 id 查询
     *
     * @param ids
     * @return
     */
    @Override
    public List<BBkMonitorLogDetailVo> selectLogDetailListByIds(List<String> ids) {
        List<BMonitorDataMongoEntity> list = selectListByIds(ids);
        return list.stream().map(item -> new BBkMonitorLogDetailVo(item.getMonitor_id(), item.getMonitor_delivery_id_bk()
                , item.getMonitor_in_id_bk(), item.getMonitor_out_id_bk(), item.getMonitor_unload_id_bk()
                , item.getCode())).collect(Collectors.toList());
    }

    /**
     * 更新是否恢复状态
     *
     * @param monitorId 监管任务 ID
     */
    @Override
    public void updateRestoreStatus(Integer monitorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("monitor_id").is(monitorId));
        Update update = new Update();
        update.set("is_restore", DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_T)
                .set("isShow", DictConstant.DICT_B_MONITOR_MONGO_IS_SHOW_F);
        mongoTemplate.upsert(query, update, BMonitorDataMongoEntity.class);
    }

    /**
     * 根据 id 更新是否可见状态
     *
     * @param ids
     * @param isShow 是否可见 0不可见, 1/null 可见
     */
    @Override
    public void updateVisibilityStatusByIds(List<String> ids, String isShow) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        Update update = new Update();
        update.set("is_show", isShow);
        mongoTemplate.updateMulti(query, update, BMonitorDataMongoEntity.class);
    }

    /**
     * 根据 monitor_id 更新是否可见状态
     *
     * @param id
     * @param isShow 是否可见 0不可见, 1/null 可见
     */
    @Override
    public void updateVisibilityStatusByMonitorId(Integer id, String isShow) {
        Query query = new Query();
        query.addCriteria(Criteria.where("monitor_id").is(id));
        Update update = new Update();
        update.set("is_show", isShow);
        mongoTemplate.upsert(query, update, BMonitorDataMongoEntity.class);
    }

    /**
     * 查询 monitor Id
     *
     * @param scheduleId
     * @return
     */
    @Override
    public List<BMonitorDataMongoEntity> selectByScheduleId(Integer scheduleId) {
        // 查询条件
        Criteria criteria = new Criteria();
        criteria.and("schedule_id").is(scheduleId);
        criteria.and("is_restore").is(DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_F);

        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, BMonitorDataMongoEntity.class);
    }

    /**
     * 查询文件
     *
     * @param param
     * @return
     */
    @Override
    public BBkMonitorVo getFiles(BBkMonitorVo param) {
        BBkMonitorVo result = new BBkMonitorVo();
        Criteria criteria = new Criteria();
        criteria.and("code").is(param.getCode());
        Query query = new Query();
        // 只查询detail VO, 数据保存时已组装
        query.fields().include("detailVo");
        query.addCriteria(criteria);
        BMonitorDataMongoEntity entity = mongoTemplate.findOne(query, BMonitorDataMongoEntity.class);
        if (null != entity) {
            result.setPreview_data(entity.getDetailVo().getPreviewFiles());
        }
        return result;
    }

    private List<BMonitorDataMongoEntity> selectListByIds(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        return mongoTemplate.find(query, BMonitorDataMongoEntity.class);
    }

    /**
     * 保存(多条)
     *
     * @param userSaveParam
     * @return
     */
    @Override
    public List<BMonitorDataMongoEntity> saveAll(List<BMonitorDataMongoV2Vo> userSaveParam) {
        List<BMonitorDataMongoEntity> list = BeanUtilsSupport.copyProperties(userSaveParam
                , BMonitorDataMongoEntity.class, new String[]{"id"});
        list = repository.saveAll(list);
        return list;
    }

    /**
     * 分页查询
     *
     * @param param
     */
    @Override
    public org.springframework.data.domain.Page<BMonitorBackupV2Vo> selectPageList(BMonitorBackupV2Vo param) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        concatParam(criteria, param);
        // 分页查询
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, BMonitorDataMongoEntity.class);
        // mongodb 分页从 0 开始
        Pageable pageParam = PageRequest.of((int) param.getPageCondition().getCurrent() - 1,
                (int) param.getPageCondition().getSize(), Sort.by(Sort.Direction.DESC, "c_time"));
        List<BMonitorDataMongoEntity> list = mongoTemplate.find(query.with(pageParam), BMonitorDataMongoEntity.class);
        List<BMonitorBackupV2Vo> resultList = BeanUtilsSupport.copyProperties(list, BMonitorBackupV2Vo.class);
        org.springframework.data.domain.Page<BMonitorBackupV2Vo> page = PageableExecutionUtils.getPage(resultList, pageParam, () -> count);
        return page;
    }

    /**
     * 数据求和
     *
     * @param param
     * @return
     */
    @Override
    public BMonitorBackupSumV2Vo selectSumData(BMonitorBackupV2Vo param) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        concatParam(criteria, param);
        MatchOperation match = Aggregation.match(criteria);
        GroupOperation group = Aggregation.group()
                .sum("qty_loss").as("qty_loss")
                .sum("in_qty").as("in_qty")
                .sum("out_qty").as("out_qty");
        // 查询结果
        AggregationResults<BMonitorBackupSumV2Vo> results = mongoTemplate.aggregate(Aggregation.newAggregation(BMonitorDataMongoEntity.class,
                match, group), BMonitorBackupSumV2Vo.class);
        return results.getUniqueMappedResult();
    }

    /**
     * 返回详情
     *
     * @param param
     * @return
     */
    @Override
    public BMonitorBackupDetailV2Vo getDetail(BMonitorBackupV2Vo param) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(param.getId()));
        BMonitorDataMongoEntity vo = mongoTemplate.findOne(query, BMonitorDataMongoEntity.class);
        BMonitorBackupDetailV2Vo result = (BMonitorBackupDetailV2Vo) BeanUtilsSupport.copyProperties(vo.getDetailVo(), BMonitorBackupDetailV2Vo.class);
        return result;
    }

    /**
     * 附件导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BMonitorFileDownloadMongoV2Vo> exportFile(List<BMonitorBackupV2Vo> searchCondition) {
        // 获取ID
        Set<String> ids = searchCondition.stream().map(BMonitorBackupV2Vo::getId).collect(Collectors.toSet());
        // 根据 ID 查询
        Criteria criteria = Criteria.where("id").in(ids);
        // 查询字段
        Query query = Query.query(criteria);
        query.fields().include("detailVo", "c_time", "vehicle_no", "code","schedule_type");
        List<BMonitorDataMongoEntity> entities = mongoTemplate.find(query, BMonitorDataMongoEntity.class);
        List<BMonitorFileDownloadMongoV2Vo> result = new ArrayList<>();
        entities.forEach(item -> {
            List<BMonitorFileDownloadMongoV2Vo> list = new ArrayList<>();
            if (item.getSchedule_type() != null
                    && (item.getSchedule_type().equals(DictConstant.DICT_B_SCHEDULE_TYPE_4)
                    || item.getSchedule_type().equals(DictConstant.DICT_B_SCHEDULE_TYPE_5))) {
                list = getBMonitorDirectFileVo(item.getDetailVo().getPreviewFiles(),
                        item.getC_time(), item.getVehicle_no(), item.getCode(), item.getId());
            } else {
                list = getBMonitorFileVo(item.getDetailVo().getPreviewFiles(),
                        item.getC_time(), item.getVehicle_no(), item.getCode(), item.getId());
            }
            result.addAll(list);
        });
        return result;
    }

    /**
     * 数据导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BMonitorMongoExportV2Vo> selectExportList(List<BMonitorBackupV2Vo> searchCondition) {
        // 获取ID
        Set<String> ids = searchCondition.stream().map(BMonitorBackupV2Vo::getId).collect(Collectors.toSet());
        // 根据 ID 查询
        Criteria criteria = Criteria.where("id").in(ids);
        // 查询字段
        Query query = Query.query(criteria);
        query.fields().exclude("detailVo");
        List<BMonitorDataMongoEntity> entities = mongoTemplate.find(query, BMonitorDataMongoEntity.class);
        List<BMonitorMongoExportV2Vo> list = BeanUtilsSupport.copyProperties(entities, BMonitorMongoExportV2Vo.class);
        return list;
    }

    /**
     * 数据导出,全部
     *
     * @param param
     * @return
     */
    @Override
    public List<BMonitorMongoExportV2Vo> selectExportAllList(BMonitorBackupV2Vo param) {
        // 查询条件
        Criteria criteria = new Criteria();
        // 拼接模糊查询参数
        concatParam(criteria, param);
        // 分页查询
        Query query = Query.query(criteria);
        query.fields().exclude("detailVo");
        List<BMonitorDataMongoEntity> entities = mongoTemplate.find(query, BMonitorDataMongoEntity.class);
        List<BMonitorMongoExportV2Vo> list = BeanUtilsSupport.copyProperties(entities, BMonitorMongoExportV2Vo.class);
        return list;
    }

    /**
     * 拼接查询参数
     */
    private void concatParam(Criteria criteria, BMonitorBackupV2Vo param) {
        // 只查询没有恢复的数据
        criteria.and("is_restore").is(DictConstant.DICT_B_MONITOR_MONGO_IS_RESTORE_F);
        // 查询可见的数据
        criteria.and("is_show").is(DictConstant.DICT_B_MONITOR_MONGO_IS_SHOW_T);
        // 任务单号模糊查询
        if (StringUtils.isNotEmpty(param.getCode())) {
            criteria.and("code").regex(regexPattern(param.getCode()));
        }
        // 物流单号
        if (StringUtils.isNotEmpty(param.getSchedule_code())) {
            criteria.and("schedule_code").regex(regexPattern(param.getSchedule_code()));
        }
        // 合同号
        if (StringUtils.isNotEmpty(param.getContract_no())) {
            criteria.and("contract_no").regex(regexPattern(param.getContract_no()));
        }
        // 出库计划单号
        if (StringUtils.isNotEmpty(param.getOut_plan_code())) {
            criteria.and("out_plan_code").regex(regexPattern(param.getOut_plan_code()));
        }
        // 入库计划单号
        if (StringUtils.isNotEmpty(param.getIn_plan_code())) {
            criteria.and("in_plan_code").regex(regexPattern(param.getIn_plan_code()));
        }
        // 运单号
        if (StringUtils.isNotEmpty(param.getWaybill_code())) {
            criteria.and("waybill_code").regex(regexPattern(param.getWaybill_code()));
        }
        // 物料名称 or 编码
        if (StringUtils.isNotEmpty(param.getGoods_name())) {
            Pattern pattern = regexPattern(param.getGoods_name());
            // 多字段模糊查询
            criteria.orOperator(
                    Criteria.where("goods_name").regex(pattern),
                    Criteria.where("goods_code").regex(pattern),
                    Criteria.where("sku_code").regex(pattern),
                    Criteria.where("sku_name").regex(pattern)
            );
        }
        // 发货地
        if (param.getOut_warehouse_id() != null) {
            criteria.and("out_warehouse_id").is(param.getOut_warehouse_id());
        }
        // 发货仓库类型, 多选
        if (StringUtils.isNotEmpty(param.getOut_warehouse_types())) {
            criteria.and("out_warehouse_type").in(Arrays.asList(param.getOut_warehouse_types()));
        }
        // 收货地
        if (param.getIn_warehouse_id() != null) {
            criteria.and("in_warehouse_id").is(param.getIn_warehouse_id());
        }
        // 收货地类型, 多选
        if (StringUtils.isNotEmpty(param.getIn_warehouse_types())) {
            criteria.and("in_warehouse_type").in(Arrays.asList(param.getIn_warehouse_types()));
        }
        // 承运商
        if (StringUtils.isNotEmpty(param.getCustomer_name())) {
            criteria.and("customer_name").regex(regexPattern(param.getCustomer_name()));
        }
        // 车牌号
        if (StringUtils.isNotEmpty(param.getVehicle_no())) {
            criteria.and("vehicle_no").regex(regexPattern(param.getVehicle_no()));
        }
        // 发货类型 精准
        if (StringUtils.isNotEmpty(param.getOut_type())) {
            criteria.and("out_type").is(param.getOut_type());
        }
        // 收货类型 精准
        if (StringUtils.isNotEmpty(param.getIn_type())) {
            criteria.and("in_type").is(param.getIn_type());
        }
        // 出库提货时间, 区间
        if (null != param.getStart_time() && null != param.getOver_time()) {
            criteria.andOperator(
                    Criteria.where("out_time").gte(LocalDateTimeUtils.getDayStart(param.getStart_time())),
                    Criteria.where("out_time").lte(LocalDateTimeUtils.getDayEnd(param.getOver_time()))
            );
        }
        // 入库卸货时间, 区间
        if (null != param.getIn_time_start() && null != param.getIn_time_end()) {
            criteria.andOperator(
                    Criteria.where("in_time").gte(LocalDateTimeUtils.getDayStart(param.getIn_time_start())),
                    Criteria.where("in_time").lte(LocalDateTimeUtils.getDayEnd(param.getIn_time_end()))
            );
        }
        // 年份
        if (param.getAudit_year() != null) {
            criteria.and("audit_year").is(param.getAudit_year());
        }
        // 物流合同号
        if (StringUtils.isNotEmpty(param.getWaybill_contract_no())) {
            criteria.and("waybill_contract_no").regex(regexPattern(param.getWaybill_contract_no()));
        }

        // 查询备份 null,0=历史备份v2 1=直采直销v2
        if (StringUtils.isNotEmpty(param.getIf_schedule_type())
                && param.getIf_schedule_type().equals("1")) {
            criteria.and("schedule_type").in(DictConstant.DICT_B_SCHEDULE_TYPE_4, DictConstant.DICT_B_SCHEDULE_TYPE_5);
        } else {
            criteria.orOperator(
                    Criteria.where("schedule_type").is(null),
                    Criteria.where("schedule_type").nin(DictConstant.DICT_B_SCHEDULE_TYPE_4, DictConstant.DICT_B_SCHEDULE_TYPE_5)
            );
        }
    }

    private List<BMonitorFileDownloadMongoV2Vo> getBMonitorFileVo(List<BPreviewBackupDataV2Vo> previewFiles, LocalDateTime c_time, String vehicle_no, String code, String id) {
        List<BMonitorFileDownloadMongoV2Vo> list = new ArrayList<>();
        String dirName = "/" + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + "/" + vehicle_no + "-" + LocalDateTimeUtils.formatTime(c_time, DateTimeUtil.YYYYMMDD) + "-" + code;
        // 判断 previewFiles file_num中是否存在 38, 39, 如果不存在,集合add进去两个
        insertPreviewFiles(previewFiles);
        for (BPreviewBackupDataV2Vo vo : previewFiles) {
            BMonitorFileDownloadMongoV2Vo bMonitorFileVo;
            switch (vo.getFile_num()) {
                case 1:
                    // 空车过磅 start
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 2:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-司机车尾照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 40:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-车厢情况照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-车厢情况照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 3:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-司机承诺书
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-司机承诺书" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 4:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-司机身份证
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-司机身份证" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 38:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-司机驾驶证
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    } else {
                        // 由于之前同步漏掉 38, 39两个文件, 然后从 detailVo.monitorOutVo 中获取
                        BMonitorOutDeliveryDataMongoV2Vo fileVo = getDetailFileVo(id);
                        if (fileVo != null && fileVo.getTwelve_fileVo() != null && StringUtils.isNotEmpty(fileVo.getTwelve_fileVo().getUrl())) {
                            bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                            bMonitorFileVo.setDirName(dirName);
                            bMonitorFileVo.setUrl(fileVo.getTwelve_fileVo().getUrl());
                            bMonitorFileVo.setFileName("空车过磅-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
                            list.add(bMonitorFileVo);
                        }
                    }
                    break;
                case 39:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 空车过磅-车辆行驶证
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车过磅-车辆行驶证" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }else {
                        // 由于之前同步漏掉 38, 39两个文件, 然后从 detailVo.monitorOutVo 中获取
                        BMonitorOutDeliveryDataMongoV2Vo fileVo = getDetailFileVo(id);
                        if (fileVo != null && fileVo.getThirteen_fileVo() != null && StringUtils.isNotEmpty(fileVo.getThirteen_fileVo().getUrl())) {
                            bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                            bMonitorFileVo.setDirName(dirName);
                            bMonitorFileVo.setUrl(fileVo.getThirteen_fileVo().getUrl());
                            bMonitorFileVo.setFileName("空车过磅-车辆行驶证" + lastName(bMonitorFileVo.getUrl()));
                            list.add(bMonitorFileVo);
                        }
                    }
                    break;
                // 空车过磅 end

                case 5:
                    // 正在装货 start
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;

                case 6:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-司机车尾照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 7:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-车侧身照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-车侧身照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 8:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-装货视频" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 正在装货 end

                // 发货集装箱 start
                case 9:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱箱号照片1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 10:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱内部空箱照片1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 11:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱装货视频1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 12:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-磅单1(司机签字)" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 13:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱箱号照片2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 14:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_14().getUrl().replaceAll("\\\\","/")));
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱内部空箱照片2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 15:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-集装箱装货视频2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 16:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在装货-磅单2(司机签字)" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 发货集装箱 end


                // 重车出库 start
                case 17:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 18:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
//            bMonitorFileVo.setUrl(getPath(vo.getFile_18().getUrl().replaceAll("\\\\","/")));
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 19:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-磅单
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 重车出库 end

                // 重车过磅 start
                case 20:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车过磅-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 21:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车尾照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车过磅-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 22:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车尾照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车过磅-行车轨迹" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 重车过磅 end


                // 正在卸货 start
                case 23:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在卸货-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 24:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在卸货-司机车尾照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;

                case 25:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在卸货-车侧身照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-车侧身照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 26:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在卸货-卸货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-卸货视频" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 正在卸货 end

                // 收货集装箱 start
                case 27:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱箱号照片1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 28:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱内部空箱照片1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 29:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱卸货视频1" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 30:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-磅单1(司机签字)" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 31:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱箱号照片2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 32:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱内部空箱照片2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 33:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-集装箱卸货视频2" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 34:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("正在卸货-磅单2(司机签字)" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                // 收货集装箱 end

                // 空车出库 start
                case 35:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-车侧身照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 36:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 正在装货-装货视频
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-司机车尾照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 37:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        // 重车出库-司机车头照片
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
//                case 41:
//                    if (StringUtils.isNotEmpty(vo.getUrl())) {
//                        //  空车出库 直采入库 司机行驶证
//                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
//                        bMonitorFileVo.setDirName(dirName);
//                        bMonitorFileVo.setUrl(vo.getUrl());
//                        bMonitorFileVo.setFileName("空车出库-司机行驶证" + lastName(bMonitorFileVo.getUrl()));
//                        list.add(bMonitorFileVo);
//                    }
//                    break;
//                case 42:
//                    if (StringUtils.isNotEmpty(vo.getUrl())) {
//                        // 空车出库 直采入库 商品近照
//                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
//                        bMonitorFileVo.setDirName(dirName);
//                        bMonitorFileVo.setUrl(vo.getUrl());
//                        bMonitorFileVo.setFileName("空车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
//                        list.add(bMonitorFileVo);
//                    }
//                    break;
//                case 43:
//                    if (StringUtils.isNotEmpty(vo.getUrl())) {
//                        // 重车出库-直销出库 商品近照
//                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
//                        bMonitorFileVo.setDirName(dirName);
//                        bMonitorFileVo.setUrl(vo.getUrl());
//                        bMonitorFileVo.setFileName("重车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
//                        list.add(bMonitorFileVo);
//                    }
//                    break;
                // 空车出库 end
                default:
                    break;
            }
        }
        if (list.size() == 0) {
            BMonitorFileDownloadMongoV2Vo bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
            bMonitorFileVo.setDirName(dirName);
            list.add(bMonitorFileVo);
        }
        return list;
    }

    private void insertPreviewFiles(List<BPreviewBackupDataV2Vo> previewFiles) {
        Set<Integer> nums = previewFiles.stream().map(BPreviewBackupDataV2Vo::getFile_num).collect(Collectors.toSet());
        if (!nums.contains(38)) {
            previewFiles.add(new BPreviewBackupDataV2Vo(38));
        }
        if (!nums.contains(39)) {
            previewFiles.add(new BPreviewBackupDataV2Vo(39));
        }
    }

    /**
     * 从 monitorOutVo 中获取文件
     * @param id
     * @return
     */
    private BMonitorOutDeliveryDataMongoV2Vo getDetailFileVo(String id) {
        BMonitorBackupV2Vo param = new BMonitorBackupV2Vo();
        param.setId(id);
        BMonitorBackupDetailV2Vo detail = this.getDetail(param);
        if (detail == null || detail.getMonitorOutVo() == null) {
            return null;
        }
        return detail.getMonitorOutVo();
    }

    // 获取后缀名
    private String lastName(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return "";//文件没有后缀名的情况
        }
        //此时返回的是带有 . 的后缀名，
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private List<BMonitorFileDownloadMongoV2Vo> getBMonitorDirectFileVo(List<BPreviewBackupDataV2Vo> previewFiles, LocalDateTime c_time, String vehicle_no, String code, String id) {
        List<BMonitorFileDownloadMongoV2Vo> list = new ArrayList<>();
        String dirName = "/" + LocalDateTimeUtils.formatTime(LocalDateTime.now(), DateTimeUtil.YYYYMMDD) + "/" + vehicle_no + "-" + LocalDateTimeUtils.formatTime(c_time, DateTimeUtil.YYYYMMDD) + "-" + code;

        for (BPreviewBackupDataV2Vo vo : previewFiles) {
            BMonitorFileDownloadMongoV2Vo bMonitorFileVo;

            switch (vo.getFile_num()) {
                case 35: //  空车出库 直采入库 司机行驶证
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 41: //  空车出库 直采入库 司机行驶证
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-司机行驶证" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 42:    // 空车出库 直采入库 商品近照
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("空车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 37:
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        if (StringUtils.isNotEmpty(vo.getUrl())) {
                            bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                            bMonitorFileVo.setDirName(dirName);
                            bMonitorFileVo.setUrl(vo.getUrl());
                            bMonitorFileVo.setFileName("空车出库-磅单" + lastName(bMonitorFileVo.getUrl()));
                            list.add(bMonitorFileVo);
                        }
                    }
                    break;

                case 17: // （直销）重车出库-司机车头照片
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-司机车头照片" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                        break;
                case 38:// （直销）重车出库-司机驾驶证
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                    bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                    bMonitorFileVo.setDirName(dirName);
                    bMonitorFileVo.setUrl(vo.getUrl());
                    bMonitorFileVo.setFileName("重车出库-司机驾驶证" + lastName(bMonitorFileVo.getUrl()));
                    list.add(bMonitorFileVo);
                    }
                    break;
                case 43:// 重车出库-直销出库 商品近照
                    if (StringUtils.isNotEmpty(vo.getUrl())) {

                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-商品近照" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;
                case 19:  // 重车出库-直销出库 榜单
                    if (StringUtils.isNotEmpty(vo.getUrl())) {
                        bMonitorFileVo = new BMonitorFileDownloadMongoV2Vo();
                        bMonitorFileVo.setDirName(dirName);
                        bMonitorFileVo.setUrl(vo.getUrl());
                        bMonitorFileVo.setFileName("重车出库-榜单" + lastName(bMonitorFileVo.getUrl()));
                        list.add(bMonitorFileVo);
                    }
                    break;

                default:
                    break;
            }
        }

        if (list.size() == 0) {
            BMonitorFileDownloadMongoV2Vo bMonitorFileVos = new BMonitorFileDownloadMongoV2Vo();
            bMonitorFileVos.setDirName(dirName);
            list.add(bMonitorFileVos);
        }
        return list;
    }


}
