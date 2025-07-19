package com.xinyirun.scm.controller.business.returnrelation;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorOutDeliveryVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.returnrelation.BReturnRelationVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorOutMapper;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 退货表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-26
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/return/relation")
public class BReturnRelationController extends SystemBaseController {

//    @Autowired
//    private IBReturnRelationService service;

    @Autowired
    private IBMonitorService monitorService;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;

    @Autowired
    private BMonitorOutMapper monitorOutMapper;

    @SysLogAnnotion("新增退货单")
    @PostMapping("/insert_return_relation")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> insertReturnRelation(@RequestBody(required = false) BMonitorVo searchCondition) {
//        BMonitorVo returnRelationVo = service.insertReturnRelation(searchCondition);

        //同步出库单到业务中台（计算数量）
        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
        List<BOutVo> bOutVoList = new ArrayList<>();
        if (monitorOutVo != null && StringUtils.isNotEmpty(monitorOutVo.getOut_extra_code()) && monitorOutVo.getOut_id() != null) {
            BOutVo bOutVo = new BOutVo();
            bOutVo.setId(monitorOutVo.getOut_id());
            bOutVo.setOut_id(monitorOutVo.getOut_id());
            bOutVoList.add(bOutVo);
            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
            callAsyncApiController(bOutVoList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        }

        //同步监管任务到业务中台 筛选掉不能同步的监管任务
        List<BMonitorVo> searchConditionList = new ArrayList<>();
        searchConditionList.add(searchCondition);
        List<BMonitorVo> syncList = monitorService.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }
//        return ResponseEntity.ok().body(ResultUtil.OK(returnRelationVo));
        return null;
    }

    @SysLogAnnotion("修改退货单")
    @PostMapping("/update_return_relation")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BMonitorVo>> updateReturnRelation(@RequestBody(required = false) BMonitorVo searchCondition) {
//        BMonitorVo returnRelationVo = service.updateReturnRelation(searchCondition);

        //同步出库单到业务中台（计算数量）
        BMonitorOutDeliveryVo monitorOutVo = monitorOutMapper.selectOutDeliveryByMonitorId(searchCondition.getId());
        List<BOutVo> bOutVoList = new ArrayList<>();
        if (monitorOutVo != null && StringUtils.isNotEmpty(monitorOutVo.getOut_extra_code()) && monitorOutVo.getOut_id() != null) {
            BOutVo bOutVo = new BOutVo();
            bOutVo.setId(monitorOutVo.getOut_id());
            bOutVo.setOut_id(monitorOutVo.getOut_id());
            bOutVoList.add(bOutVo);
            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
            callAsyncApiController(bOutVoList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        }

        //同步监管任务到业务中台 筛选掉不能同步的监管任务
        List<BMonitorVo> searchConditionList = new ArrayList<>();
        searchConditionList.add(searchCondition);
        List<BMonitorVo> syncList = monitorService.selectSyncData(searchConditionList);
        // 调用业务中台同步接口
        if (!CollectionUtils.isEmpty(syncList)) {
            callAsyncMonitorApiController(syncList);
        }

//        return ResponseEntity.ok().body(ResultUtil.OK(returnRelationVo));
        return null;
    }

    /**
     *退货单 列表
     */
    @PostMapping("/pagelist")
    @SysLogAnnotion("退货单 列表")
    public ResponseEntity<JsonResultAo<IPage<BReturnRelationVo>>> selectPageList(@RequestBody BReturnRelationVo returnRelationVo) {
//        IPage<BReturnRelationVo> result = service.selectPageList(returnRelationVo);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
        return null;
    }

    /**
     * 退货单 详情
     */
    @PostMapping("/getDetail")
    @SysLogAnnotion("退货单 详情")
    public ResponseEntity<JsonResultAo<BReturnRelationVo>> getDetail(@RequestBody BReturnRelationVo returnRelationVo) {
//        BReturnRelationVo result = service.getDetail(returnRelationVo);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
        return null;
    }


    /**
     * 退货单 导出
     */
    @SysLogAnnotion("退货单 导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<BReturnRelationVo> searchCondition, HttpServletResponse response) throws Exception {
//        List<BReturnRelationExportVo> list = service.selectExportList(searchCondition);
//        new EasyExcelUtil<>(BReturnRelationExportVo.class).exportExcel("退货单"  + DateTimeUtil.getDate(), "退货单", list, response);
    }


    /**
     * 退货单 导出
     */
    @SysLogAnnotion("退货单 全部导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) BReturnRelationVo searchCondition, HttpServletResponse response) throws Exception {
//        List<BReturnRelationExportVo> list = service.selectExportAll(searchCondition);
//        new EasyExcelUtil<>(BReturnRelationExportVo.class).exportExcel("退货单"  + DateTimeUtil.getDate(), "退货单", list, response);
    }



    /**
     * 调用API接口，同步监管任务
     *
     * @param beans
     */
    private void callAsyncMonitorApiController(List<BMonitorVo> beans) {
        log.debug("=============同步监管任务start=============");
        if (beans == null || beans.size() == 0) {
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

    /**
     * 调用API接口，同步入库信息
     *
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BOutVo> beans, String app_config_type) {
        log.debug("=============同步出库信息start=============");
        if (beans == null || beans.size() == 0) {
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
}
