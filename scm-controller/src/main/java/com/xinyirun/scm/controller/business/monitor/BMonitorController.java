package com.xinyirun.scm.controller.business.monitor;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.ApiCanceledVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.wms.in.delivery.BDeliveryVo;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.*;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.bean.system.vo.business.schedule.AppScheduleSendMqData;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorInMapper;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorOutMapper;
import com.xinyirun.scm.core.system.service.business.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.schedule.ScheduleCalcProducer;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单管理 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-03-03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/monitor")
public class BMonitorController extends SystemBaseController {

    @Autowired
    private IBMonitorService service;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private BMonitorOutMapper monitorOutMapper;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private ScheduleCalcProducer scheduleCalcProducer;

    @Autowired
    private BMonitorInMapper inMapper;

    @Autowired
    private IBInService inService;

    @Autowired
    private IBOutService outService;

    @Autowired
    private ISBDailyProductV2Service dailyProductV2Service;

    @SysLogAnnotion("根据查询条件，获取监管分页列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BMonitorVo>>> pageList(@RequestBody(required = false) BMonitorVo searchCondition) {
        IPage<BMonitorVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取监管列表 数据")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BMonitorVo>>> getList(@RequestBody(required = false) BMonitorVo searchCondition) {
        List<BMonitorVo> list = service.selectListByParam(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取监管列表 数据")
    @PostMapping("/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> selectCount(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorVo result = service.selectCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取监管明细")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> get(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorVo bMonitorVo = service.get(searchCondition);

        // 业务中台是否结算
        bMonitorVo.setBusiness_settlement_status(callMonitorCanceledAppCode120Api(List.of(searchCondition))?"1":"0");

        return ResponseEntity.ok().body(ResultUtil.OK(bMonitorVo));
    }

    @SysLogAnnotion("根据查询条件，获取监管明细上一条")
    @PostMapping("/get/prev")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> prev(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorVo bMonitorVo = service.getPrevData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(bMonitorVo));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务司机维护页面", value = "数据保存")
    @SysLogAnnotion("维护监管任务司机")
    @PostMapping("/driver/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveMonitorDriver(@RequestBody(required = false) BMonitorVo vo) {
        service.saveMonitorDriver(vo);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务车牌维护页面", value = "数据保存")
    @SysLogAnnotion("维护监管任务车牌")
    @PostMapping("/vehicle/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveMonitorVehicle(@RequestBody(required = false) BMonitorVo vo) {
        service.saveMonitorVehicle(vo);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据查询条件，获取监管明细下一条")
    @PostMapping("/get/next")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> next(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorVo bMonitorVo = service.getNextData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(bMonitorVo));
    }

    @SysLogAnnotion("根据查询条件，获取监管明细")
    @PostMapping("/getfiles")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> getFiles(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorVo bMonitorVo = service.getFiles(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(bMonitorVo));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "数据作废")
    @SysLogAnnotion("根据选择的数据废除，部分数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        if (callMonitorCanceledAppCode120Api(searchConditionList)) {
            throw new BusinessException("此监管任务已结算，不可作废");
        }
        // 执行作废操作
        service.cancel(searchConditionList);

        List<BOutVo> bOutVoList = new ArrayList<>();
        List<BInVo> inVoList = new ArrayList<>();

        for (BMonitorVo vo: searchConditionList) {
            // 获取监管出库详情
            BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(vo.getId());
            if (monitorOutVo != null && StringUtils.isNotEmpty(monitorOutVo.getOut_extra_code()) && monitorOutVo.getOut_id() != null) {
                BOutVo bOutVo = new BOutVo();
                bOutVo.setId(monitorOutVo.getOut_id());
                bOutVo.setOut_id(monitorOutVo.getOut_id());
                bOutVoList.add(bOutVo);
                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
                callAsyncApiController(bOutVoList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
            } else if (monitorOutVo != null && StringUtils.isNotEmpty(monitorOutVo.getOut_extra_code())&& monitorOutVo.getReceive_id() != null) {
                BReceiveVo bReceiveVo = new BReceiveVo();
                bReceiveVo.setId(monitorOutVo.getReceive_id());
                log.debug("监管_收货单 同步收货信息到业务中台");
                callAsyncReceiveApiController(List.of(bReceiveVo), SystemConstants.APP_URI_TYPE.RECEIVE_CANCEL);
            }

            // 调用监管入库详情
            BMonitorInUnloadVo monitorInVo = inMapper.selectMonitorInUnloadByMonitorId(vo.getId());
            if (monitorInVo != null && !StringUtils.isEmpty(monitorInVo.getIn_extra_code()) && monitorInVo.getIn_id() != null) {
                BInVo inVo = new BInVo();
                inVo.setId(monitorInVo.getIn_id());
                inVoList.add(inVo);
                callInAsyncApiController(inVoList, SystemConstants.APP_URI_TYPE.IN_CANCEL);
            } else if (!Objects.isNull(monitorInVo) && !StringUtils.isEmpty(monitorInVo.getIn_extra_code()) && monitorInVo.getDelivery_id() != null) {
                BDeliveryVo bDeliveryVo = new BDeliveryVo();
                bDeliveryVo.setId(monitorInVo.getDelivery_id());
                log.debug("监管_提货单 同步提货信息到业务中台");
                callAsyncDeliveryApiController(List.of(bDeliveryVo), SystemConstants.APP_URI_TYPE.DELIVERY_AUDIT);
            }


            BMonitorEntity byId = service.getById(vo.getId());
            // 调用 mq , 执行 物流数据更新
            sendMq2ScheduleQty(byId.getSchedule_id(), SystemConstants.SCHEDULE_CALC_TYPE.IN, "0");

        }
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }

        // 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
        recreateProductDaily(bOutVoList, inVoList);

        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "入库审核")
    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/in/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> auditIn(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行作废操作
        service.auditIn(searchConditionList);
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "出库审核")
    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/out/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> auditOut(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行作废操作
        service.auditOut(searchConditionList);
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "数据驳回")
    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行作废操作
        service.reject(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "数据结算")
    @SysLogAnnotion("根据选择的数据结算，部分数据")
    @PostMapping("/settle")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> settle(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行作废操作
        service.settlement(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务维护页面", value = "出库数据保存")
    @SysLogAnnotion("维护监管出库数据")
    @PostMapping("/out/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveMonitorOutDelivery(@RequestBody(required = false) BMonitorVo searchCondition) {
        // 判断是否可以维护
        if (callMonitorCanceledAppCode120Api(List.of(searchCondition))) {
            throw new BusinessException("此监管任务已结算，不可维护");
        }
        // 执行维护操作
        service.saveMonitorOutDelivery(searchCondition);

        // 调用mq, 计算物流订单已发货, 已收货数量
        sendMq2ScheduleQty(searchCondition.getSchedule_id(), SystemConstants.SCHEDULE_CALC_TYPE.OUT, "0");

        BMonitorOutDeliveryVo vo = service.selectByOutId(searchCondition.getMonitorOutVo().getId(), searchCondition.getMonitorOutVo().getType());
        if (!Objects.isNull(vo) && StringUtils.isNotEmpty(vo.getOut_extra_code()) && vo.getOut_id() != null) {
            List<BOutVo> searchConditionList = new ArrayList<>();
            BOutVo bOutVo = new BOutVo();
            bOutVo.setId(vo.getOut_id());
            searchConditionList.add(bOutVo);
            log.debug("监管_出库保存提交 同步出库信息到业务中台");
            callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_AUDIT);
        } else if (!Objects.isNull(vo) && StringUtils.isNotEmpty(vo.getOut_extra_code()) && vo.getReceive_id() != null) {
            BReceiveVo bReceiveVo = new BReceiveVo();
            bReceiveVo.setId(vo.getReceive_id());
            log.debug("监管_收货保存提交 同步收货信息到业务中台");
            callAsyncReceiveApiController(List.of(bReceiveVo), SystemConstants.APP_URI_TYPE.RECEIVE_AUDIT);
        }

        // 重新同步到业务中台
        List<BMonitorVo> syncList = service.selectSyncData(List.of(searchCondition));
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }

        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务维护页面", value = "入库数据保存")
    @SysLogAnnotion("维护监管入库数据")
    @PostMapping("/in/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveMonitorInUnload(@RequestBody(required = false) BMonitorVo vo) {
        // 判断是否可以维护
        if (callMonitorCanceledAppCode120Api(List.of(vo))) {
            throw new BusinessException("此监管任务已结算，不可维护");
        }

        // 执行维护操作
        service.saveMonitorInUnload(vo);

        // 调用mq, 计算物流订单已发货, 已收货数量
        sendMq2ScheduleQty(vo.getSchedule_id(), SystemConstants.SCHEDULE_CALC_TYPE.IN, "0");

        // 重新同步到业务中台
        List<BMonitorVo> syncList = service.selectSyncData(List.of(vo));
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }

        BMonitorVo monitorVo = service.selectInByMonitorId(vo.getId());
        // 手动选择入库计划 , 外部关联单号不为空, 同步入库单
        if (!Objects.isNull(monitorVo) && DictConstant.DICT_B_LOGISTICS_IN_RULE_1.equals(monitorVo.getSchedule_in_rule())
                && !StringUtils.isEmpty(monitorVo.getIn_extra_code())
                &&monitorVo.getIn_id()!=null) {
            BInVo inVo = new BInVo();
            inVo.setId(monitorVo.getIn_id());
            callAsyncInApiController(List.of(inVo), SystemConstants.APP_URI_TYPE.IN_AUDIT);
        }else if (!Objects.isNull(monitorVo) && DictConstant.DICT_B_LOGISTICS_IN_RULE_1.equals(monitorVo.getSchedule_in_rule())
                && !StringUtils.isEmpty(monitorVo.getIn_extra_code())
                &&monitorVo.getDelivery_id()!=null){
            BDeliveryVo bDeliveryVo = new BDeliveryVo();
            bDeliveryVo.setId(monitorVo.getDelivery_id());
            callAsyncDeliveryApiController(List.of(bDeliveryVo), SystemConstants.APP_URI_TYPE.DELIVERY_AUDIT);
        }

        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("导出监管任务附件信息")
    @PostMapping("/export")
    public ResponseEntity<JsonResultAo<String>> export(@RequestBody(required = false) List<BMonitorVo> searchCondition) throws Exception {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.PAGE_MONITOR);
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        if (Objects.equals(pagesVo.getExport_processing(), Boolean.TRUE)) {
            throw new BusinessException("还有未完成的导出任务，请稍后重试");
        }
        // 执行导出操作
        try {
            isPagesService.updateExportProcessingTrue(pagesVo);

            List<BMonitorFileApiVo> list = service.export(searchCondition);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("items", list);

            SConfigEntity exportUrl = isConfigService.selectByKey(SystemConstants.EXPORT_URL);

            String downloadUrl = (String) executeFileSystemDownloadUrlLogic(paramMap, exportUrl.getValue());

            return ResponseEntity.ok().body(ResultUtil.OK(downloadUrl));
        } catch (Exception e) {
            throw e;
        } finally {
            isPagesService.updateExportProcessingFalse(pagesVo);
        }
    }

    @SysLogAnnotion("导出监管任务附件信息")
    @PostMapping("/exportall")
    @ResponseBody
    public void exportAll(HttpServletResponse response) throws Exception {

        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.PAGE_MONITOR);
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        // 执行作废操作
        try {

            if (Objects.equals(pagesVo.getImport_processing(), Boolean.TRUE)) {
                throw new BusinessException("还有未完成的导出任务，请稍后重试");
            }

            isPagesService.updateExportProcessingTrue(pagesVo);

            service.exportAll(response);
        } catch (Exception e) {
            throw e;
        } finally {
            isPagesService.updateExportProcessingFalse(pagesVo);
        }
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务维护页面", value = "附件维护")
    @SysLogAnnotion("维护监管任务附件")
    @PostMapping("/file/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SFileInfoVo>> saveMonitorFile(@RequestBody(required = false) BMonitorFileSaveVo vo) {
        SFileInfoVo fileInfoVo = service.saveMonitorFile(vo);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK(fileInfoVo));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务维护页面", value = "重新生成轨迹")
    @SysLogAnnotion("重新生成轨迹")
    @PostMapping("/track/refresh")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> refreshTrack(@RequestBody(required = false) BMonitorVo vo) {

        service.refreshTrack(vo);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("监管任务数据导出")
    @PostMapping("/exportdata")
    public void exportData(@RequestBody(required = false) BMonitorVo searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<BMonitorExportVo> util = new EasyExcelUtil<>(BMonitorExportVo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }

    @SysLogAnnotion("监管任务数据导出")
    @PostMapping("/exportalldata")
    public void exportAllData(@RequestBody(required = false) BMonitorVo searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorExportVo> list = service.selectExportAllList(searchCondition);
        EasyExcelUtil<BMonitorExportVo> util = new EasyExcelUtil<>(BMonitorExportVo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }

    @SysLogAnnotion("根据查询条件，汇总")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorSumVo>> selectSum(@RequestBody(required = false) BMonitorVo searchCondition) {
        BMonitorSumVo list = service.selectSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务维护页面", value = "更新验车状态")
    @SysLogAnnotion("更新验车状态")
    @PostMapping("/validatVehicle/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> saveValidatVehicle(@RequestBody(required = false) BMonitorVo vo) {
        // 判断是否可以维护
        if (callMonitorCanceledAppCode120Api(List.of(vo))) {
            throw new BusinessException("此监管任务已结算，不可维护");
        }

        service.saveValidatVehicle(vo);

        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(List.of(vo));
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "数据删除")
    @SysLogAnnotion("监管任务 删除")
    @PostMapping("/delete")
    @ResponseBody
    public void delete(@RequestBody(required = false) List<BMonitorVo> vo) {
        service.delete(vo);
    }

    @SysLogAnnotion("监管任务 业务中台是否结算")
    @GetMapping("/selectsettle")
    public ResponseEntity<JsonResultAo<Map<String, Boolean>>> selectOutIsSettle(@RequestParam Integer id) {
        BMonitorVo vo = new BMonitorVo();
        vo.setId(id);
        boolean b = callMonitorCanceledAppCode120Api(List.of(vo));
        Map<String, Boolean> result = Map.of("is_settle", b);
//        List<Map<String, String>> result = new ArrayList<>();
//        List<BMonitorVo> codeList = new ArrayList<>();
//        for (BMonitorVo bMonitorVo : list) {
//            if (StringUtils.isEmpty(bMonitorVo.getOut_extra_code())) {
//                Map<String, String> map = Map.of("is_settle", "未结算", "code", bMonitorVo.getCode());
//                result.add(map);
//            } else {
//                codeList.add(bMonitorVo);
//            }
//        }
//        List<Map<String, String>> maps = callOutIsSettledAppCode10Api(codeList);
//        result.addAll(maps);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }


    @SysLogAnnotion("根据查询条件，获取监管明细")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> sync(@RequestBody(required = false) List<BMonitorVo> vo) {
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(vo);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("根据查询条件，获取监管明细")
    @GetMapping("/syncall")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncall() {
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(null);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("监管任务 验车日志")
    @PostMapping("/validate_track/log")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> selectValidateAndTrackLog(@RequestBody(required = false) BMonitorVo param) {
        BMonitorVo vo = service.selectValidateAndTrackLog(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    /**
     * 调用API接口，同步入库信息
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BOutVo> beans, String app_config_type) {
        log.debug("=============同步出库信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
        Set<Integer> collect = beans.stream().map(BOutVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_OUT, "ING");
        log.debug("=============同步出库信息end=============");
    }

    /**
     * true, 已结算, false 未结算
     * @param list
     * @return
     */
    @SysLogAnnotion("查询监管任务是否 结算")
    private boolean callMonitorCanceledAppCode120Api(List<BMonitorVo> list) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.MONITOR_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BMonitorVo vo : list) {
            BMonitorVo bMonitorVo = service.selectById(vo.getId());

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bMonitorVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

//            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);
            if ("0".equals(apiCanceledVo.getCode())) {
                // 未结算
                return false;
            } else {
                // 业务中台不想改, 就先判断报错信息了
                if (StringUtils.isNotBlank(apiCanceledVo.getMsg()) && apiCanceledVo.getMsg().contains("不存在")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 调用API接口，同步监管任务
     * @param beans
     */
    private void callAsyncMonitorApiController(List<BMonitorVo> beans) {
        log.debug("=============同步监管任务start=============");
        if(beans == null || beans.size() == 0){
            return;
        }
        // 将 beans 拆分为多个
        List<List<BMonitorVo>> partition = Lists.partition(beans, 500);
        for (List<BMonitorVo> bMonitorVos : partition) {
            ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
            asyncVo.setApp_config_type(SystemConstants.APP_URI_TYPE.MONITOR_SYNC);
            asyncVo.setBeans(bMonitorVos);
            producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR);
            Set<Integer> collect = beans.stream().map(BMonitorVo::getId).collect(Collectors.toSet());
            syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR, "ING");
        }
        log.debug("=============同步监管任务end=============");
    }

//    private List<Map<String, String>> callOutIsSettledAppCode10Api(List<BMonitorVo> codes) {
//        List<Map<String, String>> result = new ArrayList<>();
//        if (codes.size() > 0) {
//            for (BMonitorVo code : codes) {
//                SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_IS_SETTLED);
//                String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//                HttpHeaders headers = new HttpHeaders();
//
//                HttpEntity<String> requestEntity = new HttpEntity(code.getOut_code(), headers);
//                ResponseEntity<com.alibaba.fastjson2.JSONObject> response = restTemplate.postForEntity(url, requestEntity, com.alibaba.fastjson2.JSONObject.class);
//                ApiCanceledVo resultData = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//                for ( ApiCanceledDataVo dataVo: resultData.getData()) {
//                    if (dataVo.getCancel() != null && !dataVo.getCancel()) {
//                        Map<String, String> map = Map.of("is_settle", "已结算", "code", code.getCode());
//                        result.add(map);
//                    } else {
//                        Map<String, String> map = Map.of("is_settle", "未结算", "code", code.getCode());
//                        result.add(map);
//                    }
//                }
//            }
//        }
//        return result;
//    }

    /**
     * 计算 物流订单已发货, 已收货数量
     * @param id
     * @param type
     * @param consumer_status
     */
    private void sendMq2ScheduleQty(Integer id, String type, String consumer_status) {
//        try {
//            if (!DictConstant.DICT_B_SCHEDULE_IS_CONSUMER_0.equals(consumer_status)) {
        // 更新 schedule 消费状态
//        scheduleService.update(new LambdaUpdateWrapper<BScheduleEntity>().eq(BScheduleEntity::getId, id)
//                .set(BScheduleEntity::getIs_consumer, DictConstant.DICT_B_SCHEDULE_IS_CONSUMER_0));
        scheduleCalcProducer.mqSendMq(new AppScheduleSendMqData(id, type));
//            }
//        } catch (Exception e) {
//            scheduleService.update(new LambdaUpdateWrapper<BScheduleEntity>().eq(BScheduleEntity::getId, id)
//                    .set(BScheduleEntity::getIs_consumer, DictConstant.DICT_B_SCHEDULE_IS_CONSUMER_1));
//            log.error("重新计算物流已发货/已收货数量 发送到mq 异常");
//            e.printStackTrace();
//        }

    }

    /**
     * 调用API接口，同步入库单
     * @param beans
     * @param app_config_type
     */
    private void callAsyncInApiController(List<BInVo> beans, String app_config_type) {
        log.debug("=============同步入库信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_IN);
        Set<Integer> collect = beans.stream().map(BInVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_IN, "ING");
        log.debug("=============同步入库信息start=============");
    }

    /**
     * 调用API接口，同步入库信息
     * @param beans
     * @param app_config_type
     */
    public void callInAsyncApiController(List<BInVo> beans, String app_config_type) {
        if(beans == null || beans.size() == 0){
            return;
        }
        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        // 初始化要发生mq的bean
//        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(asyncVo, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_01);
        // 发送消息
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_IN);
        // 如果是之前同步过得, 发送玩之后,更新日志状态为ING
        Set<Integer> collect = beans.stream().map(BInVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_IN, "ING");


     /*   log.debug("=============同步入库信息start=============");
//        // 初始化要发生mq的bean
//        MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(beans, MqSenderEnum.SYNC_BUSINESS_PLATFORM_ALL_IN_ONE_MQ_01);
//        // 发送消息
//        mqProducer.send(ao, MQEnum.MQ_SYNC_ERROR_MSG_QUEUE_OUT_PLAN);

        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBInAsyncVo asyncVo = new ApiBInAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/async/in/execute", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步入库信息end=============");*/
    }

    /**
     * 异步 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
     */
    private void recreateProductDaily(List<BOutVo> outList, List<BInVo> inList) {
        List<String> allCode = Lists.newArrayList("zlsd-0100509", "zlsd-0100510", "zlsd-0100508", "19", "CM-001",
                "zlsd-0100511", "zlsd-0100505", "zlsd-0100506","zlsd-0100507-3");
        BOutVo outVo = new BOutVo();
        BInVo bInVo = new BInVo();
        if (!CollectionUtils.isEmpty(outList)) {
            outVo = outService.selectEdtAndGoodsCode(outList.get(0).getId());
        }
        if (!CollectionUtils.isEmpty(inList)) {
//            bInVo = inService.selectEdtAndGoodsCode(inList.get(0).getId());
        }
//        if (allCode.contains(outVo.getGoods_code()) && allCode.contains(bInVo.getGoods_code())) {
//            // 获取最早的时间
//            LocalDateTime earlier = LocalDateTimeUtils.getEarlier(bInVo.getE_dt(), outVo.getE_dt());
//            if (earlier != null) {
//                recreate2Cancel(new BProductDailyVo(), bInVo.getE_dt());
//            }
//        } else if (allCode.contains(outVo.getGoods_code())) {
//            recreate2Cancel(new BProductDailyVo(), outVo.getE_dt());
//        } else if (allCode.contains(bInVo.getGoods_code())) {
//            recreate2Cancel(new BProductDailyVo(), bInVo.getE_dt());
//        }
    }

    private void recreate2Cancel(BProductDailyVo vo, LocalDateTime earlier) {
        vo.setInit_time(earlier.format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
        try {
            dailyProductV2Service.recreate2Cancel(vo);
        } catch (Exception e) {
            log.error("作废重置日加共报表出错, 参数--> {}", JSONObject.toJSONString(vo));
            log.error("recreate2Cancel error", e);
        }
    }

    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "反审核")
    @SysLogAnnotion("根据选择的数据反审核，回滚待审核数据")
    @PostMapping("/statusRollback")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> statusRollback(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行审核状态回滚待审核
        service.statusRollback(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    /**
     * 同步收货信息
     */
    private void callAsyncReceiveApiController(List<BReceiveVo> beans, String app_config_type) {
        log.debug("=============同步收货信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE);
        Set<Integer> collect = beans.stream().map(BReceiveVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE, "ING");
        log.debug("=============同步收货信息end=============");
    }

    /**
     * 同步提货单信息
     * @param beans
     * @param app_config_type
     */
    private void callAsyncDeliveryApiController(List<BDeliveryVo> beans, String app_config_type) {
        log.debug("=============同步提货单信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY);
        Set<Integer> collect = beans.stream().map(BDeliveryVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY, "ING");
        log.debug("=============同步提货单信息end=============");
    }



    @DataChangeOperateAnnotation(page_name = "PC监管任务列表页面", value = "直采直销审核")
    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/direct/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> auditDirect(@RequestBody(required = false) List<BMonitorVo> searchConditionList) {
        // 执行作废操作
        service.auditDirect(searchConditionList);
        // 筛选掉不能同步的监管任务
        List<BMonitorVo> syncList = service.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("导出直销直采监管任务附件信息")
    @PostMapping("/export_direct")
    public ResponseEntity<JsonResultAo<String>> exportDirect(@RequestBody(required = false) List<BMonitorVo> searchCondition) throws Exception {
        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.P_MONITOR_DIRECT);
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        if (Objects.equals(pagesVo.getExport_processing(), Boolean.TRUE)) {
            throw new BusinessException("还有未完成的导出任务，请稍后重试");
        }
        // 执行导出操作
        try {
            isPagesService.updateExportProcessingTrue(pagesVo);

            List<BMonitorFileApiVo> list = service.exportDirect(searchCondition);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("items", list);

            SConfigEntity exportUrl = isConfigService.selectByKey(SystemConstants.EXPORT_URL);

            String downloadUrl = (String) executeFileSystemDownloadUrlLogic(paramMap, exportUrl.getValue());

            return ResponseEntity.ok().body(ResultUtil.OK(downloadUrl));
        } catch (Exception e) {
            throw e;
        } finally {
            isPagesService.updateExportProcessingFalse(pagesVo);
        }
    }

    @SysLogAnnotion("导出直销直采监管任务附件信息")
    @PostMapping("/export_direct_all")
    @ResponseBody
    public void exportDirectAll(HttpServletResponse response) throws Exception {

        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(PageCodeConstant.P_MONITOR_DIRECT);
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        // 执行作废操作
        try {

            if (Objects.equals(pagesVo.getImport_processing(), Boolean.TRUE)) {
                throw new BusinessException("还有未完成的导出任务，请稍后重试");
            }

            isPagesService.updateExportProcessingTrue(pagesVo);

            service.exportAll(response);
        } catch (Exception e) {
            throw e;
        } finally {
            isPagesService.updateExportProcessingFalse(pagesVo);
        }
    }


    @SysLogAnnotion("监管任务数据导出")
    @PostMapping("/export_direct_data")
    public void exportDirectData(@RequestBody(required = false) BMonitorVo searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorDirectExportVo> list = service.exportDirectData(searchCondition);
        EasyExcelUtil<BMonitorDirectExportVo> util = new EasyExcelUtil<>(BMonitorDirectExportVo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }

    @SysLogAnnotion("监管任务数据导出")
    @PostMapping("/export_direct_data_all")
    public void exportDirectDataAll(@RequestBody(required = false) BMonitorVo searchCondition, HttpServletResponse response) throws Exception {
        List<BMonitorDirectExportVo> list = service.exportDirectDataAll(searchCondition);
        EasyExcelUtil<BMonitorDirectExportVo> util = new EasyExcelUtil<>(BMonitorDirectExportVo.class);
        util.exportExcel("监管任务单" + DateTimeUtil.getDate(), "监管任务单", list, response);
    }
}
