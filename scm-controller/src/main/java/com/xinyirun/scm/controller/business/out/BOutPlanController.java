package com.xinyirun.scm.controller.business.out;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckResultVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.out.*;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutPlanExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 出库计划 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/outplan")
public class BOutPlanController extends SystemBaseController {

    @Autowired
    private IBOutPlanService service;

    @Autowired
    private IBOutPlanDetailService outPlanDetailService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;


    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @SysLogAnnotion("根据查询条件，获取出库计划信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOutPlanListVo>>> selectPageList(@RequestBody(required = false) BOutPlanListVo searchCondition) {
        IPage<BOutPlanListVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("出库计划列表 查询, 不查询总条数")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BOutPlanListVo>>> selectPageListNotCount(@RequestBody(required = false) BOutPlanListVo searchCondition) {
        List<BOutPlanListVo> list = service.selectPageListNotCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("出库计划 查询总条数")
    @PostMapping("/list/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanListVo>> selectPageListCount(@RequestBody(required = false) BOutPlanListVo searchCondition) {
        BOutPlanListVo result = service.selectPageListCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取出库单信息")
    @PostMapping("/todo/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody(required = false) BOutPlanListVo searchCondition) {
        Integer count = service.selectTodoCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

    @SysLogAnnotion("根据查询条件，获取出库计划合计信息")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanSumVo>> sum(@RequestBody(required = false) BOutPlanListVo searchCondition) {
        BOutPlanSumVo vo = service.selectSumData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取出库计划信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanSaveVo>> get(@RequestBody(required = false) BOutPlanSaveVo vo) {
        BOutPlanSaveVo plan = service.get(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(plan));
    }

    @SysLogAnnotion("根据查询条件，获取出库计划信息")
    @PostMapping("/getplandetail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanDetailVo>> getPlanDetail(@RequestBody(required = false) BOutPlanDetailVo vo) {
        BOutPlanDetailVo plan = service.getPlanDetail(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(plan));
    }

    @SysLogAnnotion("出库计划数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<List<BOutPlanListVo>>> insert(@RequestBody(required = false) BOutPlanSaveVo bean) {
        if(outPlanDetailService.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectBySaveId(bean.getPlan_id()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("出库计划数据更新保存")
    // @ApiOperation(value = "根据参数id，获取出库计划信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutPlanVo>> save(@RequestBody(required = false) BOutPlanSaveVo bean) {
        if(outPlanDetailService.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    // @ApiOperation(value = "根据参数id，提交数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.submit(searchConditionList);
        log.debug("出库计划页面提交按钮 同步出库信息到业务中台");
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_SUBMIT);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.audit(searchConditionList);
        log.debug("出库计划页面审核按钮 同步出库信息到业务中台");

        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_AUDIT);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/cancelaudit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelAudit(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.cancelAudit(searchConditionList);
        log.debug("出库计划页面作废审核按钮 同步出库信息到业务中台");
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_CANCEL);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("出库计划 -> 出库操作")
    @PostMapping("/operate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutPlanOperateVo>> operate(@RequestBody(required = false) BOutPlanOperateVo bean) {

        // 校验是否可以出库
        callOutCheckExcessAppCode10Api(bean.getId(), bean.getActual_weight());

        if(service.operate(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByOperateId(bean.getId()),"出库操作成功"));
        } else {
            throw new UpdateErrorException("出库操作失败，请查询后重新编辑出库。");
        }

    }

    @SysLogAnnotion("根据选择的数据废除，部分数据")
    // @ApiOperation(value = "根据参数id，废除数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.cancel(searchConditionList);
        // log.debug("出库计划页面作废按钮 同步出库信息到业务中台");
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
         callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_CANCEL);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    // @ApiOperation(value = "根据参数id，驳回数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.reject(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//        callSyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_RETURN);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    // @ApiOperation(value = "根据参数id，驳回数据")
    @PostMapping("/cancelreturn")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelReject(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.cancelReject(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//        callSyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_RETURN);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据完成数据")
    // @ApiOperation(value = "根据参数id，驳回数据")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> finish(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        service.finish(searchConditionList);
        log.debug("出库计划页面完成按钮 同步出库信息到业务中台");
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_FINISH);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据同步数据")
    @PostMapping("/syncall")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncAll() {
        BOutPlanListVo vo = new BOutPlanListVo();
        List<BOutPlanListVo> list = service.selectList(vo);
        log.debug("出库计划页面同步按钮 同步出库信息到业务中台");
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(list, SystemConstants.APP_URI_TYPE.OUT_PLAN_SUBMIT);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据同步数据")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> sync(@RequestBody(required = false) List<BOutPlanListVo> searchConditionList) {
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        log.debug("出库计划页面同步按钮 同步出库信息到业务中台");
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_PLAN_SUBMIT);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("出库划数据导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<BOutPlanListVo> searchCondition, HttpServletResponse response) throws Exception {
        List<BOutPlanExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<BOutPlanExportVo> util = new EasyExcelUtil<>(BOutPlanExportVo.class);
        util.exportExcel("出库计划"  + DateTimeUtil.getDate(), "出库计划", list, response);
    }

    @SysLogAnnotion("出库划数数据导出")
    @PostMapping("/export_all")
    public void exportAllData(@RequestBody(required = false) BOutPlanListVo searchCondition, HttpServletResponse response) throws Exception {
        List<BOutPlanExportVo> list = service.selectExportAllList(searchCondition);
        EasyExcelUtil<BOutPlanExportVo> util = new EasyExcelUtil<>(BOutPlanExportVo.class);
        util.exportExcel("出库计划"  + DateTimeUtil.getDate(), "出库计划", list, response);
    }

    @SysLogAnnotion("查询出库计划包含几条出库计划详情")
    @PostMapping("/get_detail_count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> getDetailCount(@RequestBody(required = false) List<BOutPlanListVo> searchCondition) {
        Integer count = service.getDetailCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

    @SysLogAnnotion("查询出库 超发是否开启")
    @PostMapping("/over_release/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Map<String, String>>> getConfig(@RequestBody(required = false) List<BInPlanVo> searchCondition) {
        SConfigEntity pc = configService.selectByKey(SystemConstants.OVER_RELEASE);
        SConfigEntity app = configService.selectByKey(SystemConstants.APP_OVER_RELEASE);
        Map<String, String> map = new HashMap<>();
        if ("1".equals(pc.getValue()) || "1".equals(app.getValue())) {
            map.put("config_value", "1");
        } else {
            map.put("config_value", "0");
        }
        return ResponseEntity.ok().body(ResultUtil.OK(map));
    }

    /**
     *
     * @param plan_detail_id
     * @param app_config_type
     */
    private void callAsyncApiController(Integer plan_detail_id, String app_config_type) {
        BOutPlanListVo vo = new BOutPlanListVo();
        vo.setId(plan_detail_id);
        callAsyncApiController(service.selectList(vo), app_config_type);
    }

//    /**
//     * 调用API接口，同步出库信息
//     * @param beans
//     * @param app_config_type
//     */
//    private void callAsyncApiController(List<BOutPlanListVo> beans, String app_config_type) {
//        if(beans == null || beans.size() == 0){
//            return;
//        }
//        for (BOutPlanListVo vo : beans) {
//            log.debug("=============同步出库信息start=============");
//            BOutPlanDetailVo data = outPlanDetailService.selectById(vo.getId());
//            if(data.getExtra_code() == null){
//                continue;
//            }
//            ApiOutPlanIdCodeVo apiOutPlanIdCodeBo = new ApiOutPlanIdCodeVo();
//            apiOutPlanIdCodeBo.setPlan_code(data.getPlan_code());
//            apiOutPlanIdCodeBo.setPlan_id(data.getPlan_id());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, app_config_type);
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
////            ResponseEntity<ApiOutPlanIdCodeVo> response = restTemplate.postForEntity(url, apiOutPlanIdCodeBo, ApiOutPlanIdCodeVo.class);
////            System.out.println(response.getBody());
//
//            String jsonString = JSON.toJSONString(apiOutPlanIdCodeBo);
//            Mono<String> mono = webClient
//                    .post() // 发送POST 请求
//                    .uri(url) // 服务请求路径，基于baseurl
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(BodyInserters.fromValue(jsonString))
//                    .retrieve() // 获取响应体
//                    .bodyToMono(String.class); // 响应数据类型转换
//
//            //异步非阻塞处理响应结果
//            mono.subscribe(this::callback);
//
//            log.debug("=============同步出库信息end=============");
//
//        }
//    }

    /**
     * 调用API接口，同步出库计划信息
     * @param beans
     * @param app_config_type
     */
  /*  private void callAsyncApiController(List<BOutPlanListVo> beans, String app_config_type) {
        log.debug("=============同步出库计划信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBOutPlanAsyncVo asyncVo = new ApiBOutPlanAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/async/outplan/execute", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步出库计划信息end=============");
    }*/


    /**
     * 响应结果处理回调方法
     * @param result
     */
    public void callback(String result) {
        log.debug("=============同步出库信息result=============" + result);
    }

    /**
     * 调用API接口，同步出库计划信息 rabbit mq
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BOutPlanListVo> beans, String app_config_type) {
        log.debug("=============同步出库计划信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
        Set<Integer> collect = beans.stream().map(BOutPlanListVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN, "ING");
        log.debug("=============同步出库计划信息end=============");
    }

    @SysLogAnnotion("业务中台出库超发校验接口")
    private void callOutCheckExcessAppCode10Api(Integer out_plan_detail_id, BigDecimal qty) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CHECK_EXCESS);
        SAppConfigDetailVo sAppConfigDetailBorrow = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CHECK_EXCESS_BORROW);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        String urlBorrow = getBusinessCenterUrl(sAppConfigDetailBorrow.getUri(), SystemConstants.APP_CODE.ZT);

        ApiOutCheckVo apiOutCheckVo = service.selectOutCheckVo(out_plan_detail_id);


        if (apiOutCheckVo == null) {
            log.debug("apiOutCheckVo为空");
            return;
        }

        String postUlr = url;
        if ("1".equals(apiOutCheckVo.getOrderType())) {
            postUlr = urlBorrow;
        }

        apiOutCheckVo.setOutNum(qty);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity(apiOutCheckVo, headers);
        ResponseEntity<JSONObject> result = restTemplate.postForEntity(postUlr, requestEntity, JSONObject.class);

        System.out.println(result);

//        ApiOutCheckResultVo apiOutCheckResultVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiOutCheckResultVo.class);
        ApiOutCheckResultVo apiOutCheckResultVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiOutCheckResultVo.class);

        if (apiOutCheckResultVo != null && !"true".equals(apiOutCheckResultVo.getData())) {
            throw new BusinessException("出库超发校验失败，不可出库");
        }
    }

    @SysLogAnnotion("出库计划 -> 收货操作")
    @PostMapping("/operateReceive")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutPlanOperateVo>> operateDelivery(@RequestBody(required = false) BOutPlanOperateVo bean) {
        if(service.operateDelivery(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByOperateId(bean.getId()),"收货操作成功"));
        } else {
            throw new UpdateErrorException("出库操作失败，请查询后重新编辑出库。");
        }
    }


    @SysLogAnnotion("出库计划数据新增保存 启动审批流")
    @PostMapping("/new/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<List<BOutPlanListVo>>> newInsert(@RequestBody(required = false) BOutPlanSaveVo bean) {
        if(outPlanDetailService.newInsert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectBySaveId(bean.getPlan_id()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("根据查询条件，获取出库计划信息")
    @PostMapping("/new/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutPlanSaveVo>> newGet(@RequestBody(required = false) BOutPlanSaveVo vo) {
        BOutPlanSaveVo plan = service.newGet(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(plan));
    }
}
