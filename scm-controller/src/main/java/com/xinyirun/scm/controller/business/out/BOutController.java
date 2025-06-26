package com.xinyirun.scm.controller.business.out;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.api.vo.business.*;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckResultVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.out.BOutImportVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutSumVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BOutExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.log.SLogImportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.service.log.sys.ISLogImportService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.excel.upload.SystemExcelReader;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 出库单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "出库单")
@RestController
@RequestMapping(value = "/api/v1/out")
public class BOutController extends SystemBaseController {

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private IBOutService service;

    @Autowired
    private IBOutPlanService bOutPlanService;

    @Autowired
    private ISLogImportService isLogImportService;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;

    @Autowired
    private ISBDailyProductV2Service dailyProductV2Service;

    @SysLogAnnotion("根据查询条件，获取出库单信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BOutVo>>> selectPageList(@RequestBody(required = false) BOutVo searchCondition) {
        IPage<BOutVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("出库单列表查询 不查询总数量")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BOutVo>>> selectPageListNotCount(@RequestBody(required = false) BOutVo searchCondition) {
        List<BOutVo> list = service.selectPageListNotCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("出库单列表查询 不查询总数量")
    @PostMapping("/list/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> selectPageListCount(@RequestBody(required = false) BOutVo searchCondition) {
        BOutVo result = service.selectPageListCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取出库单信息")
    @PostMapping("/todo/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody(required = false) BOutVo searchCondition) {
        Integer count = service.selectTodoCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

    @SysLogAnnotion("根据查询条件，获取出库单合计信息")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutSumVo>> sum(@RequestBody(required = false) BOutVo searchCondition) {
        BOutSumVo vo = service.selectSumData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取出库单信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> get(@RequestBody(required = false) BOutVo searchCondition) {
        BOutVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("出库单数据新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BOutVo>> insert(@RequestBody(required = false) BOutVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        // 业务中台出库超发校验接口
        callOutCheckExcessAppCode10Api(searchConditionList);

        service.submit(searchConditionList);

        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        // 业务中台出库超发校验接口
        callOutCheckExcessAppCode10Api(searchConditionList);

        service.audit(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_AUDIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "审核数据")
    @PostMapping("/cancelaudit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelAudit(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        // 先进行check，调用外部api接口
        callOutCanceledAppCode10Api(searchConditionList);

        // 执行作废操作
        service.cancelAudit(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        // 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
        recreateProductDaily(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    // @ApiOperation(value = "驳回数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        service.reject(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//        callSyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_RETURN);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    // @ApiOperation(value = "驳回数据")
    @PostMapping("/cancelreturn")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelReject(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        service.cancelReject(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//        callSyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_RETURN);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据废除，部分数据")
    // @ApiOperation(value = "废除数据")
//    @LimitAnnotion(key = "BOutController.cancel", period = 1, count = 1, name = "出库单作废", prefix = "limit")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BOutVo> searchConditionList) {

        // 先进行check，调用外部api接口
//        callOutIsSettledAppCode10Api(searchConditionList);
        callOutCanceledAppCode10Api(searchConditionList);

        // 执行作废操作
        service.cancel(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        // 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
        //recreateProductDaily(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据废除，部分数据")
    // @ApiOperation(value = "废除数据")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> finish(@RequestBody(required = false) List<BOutVo> searchConditionList) {
         service.finish(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_FINISH);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据同步，部分数据")
    @PostMapping("/syncall")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncAll() {
        BOutVo vo = new BOutVo();
        List<BOutVo> list = service.selectList(vo);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(list, SystemConstants.APP_URI_TYPE.OUT_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据同步，部分数据")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> sync(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        // 校验是否已结算
//        callOutSyncedAppCode10Api(searchConditionList);

        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("出库单 更新")
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOutVo>> update(@RequestBody(required = false) BOutVo bean) {
        UpdateResultAo<BOutVo> result = service.updateOut(bean);
        return null;
        // 返回
//        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("监管任务处出库单 直接作废")
    @PostMapping("/canceldirect")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelDirect(@RequestBody(required = false) List<BOutVo> searchConditionList) {
        // 先进行check，调用外部api接口
        callOutCanceledAppCode10Api(searchConditionList);
        // 执行作废操作
        service.cancelDirect(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        // 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
        recreateProductDaily(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("6、查询出库单是否结算")
    private void callOutIsSettledAppCode10Api(List<BOutVo> list) {
        List<String> codes = new ArrayList<String>();
        for (BOutVo vo : list) {
            if (StringUtils.isNotEmpty(vo.getExtra_code())) {
                codes.add(vo.getCode());
            }
        }

        if (codes.size() > 0) {
            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_IS_SETTLED);
            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<String> requestEntity = new HttpEntity(codes, headers);
            ResponseEntity<com.alibaba.fastjson2.JSONObject> response = restTemplate.postForEntity(url, requestEntity, com.alibaba.fastjson2.JSONObject.class);
            System.out.println(response.getBody());
//            ApiSettlementedVo result = com.alibaba.fastjson.JSONObject.toJavaObject((com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.toJSON(response.getBody()), ApiSettlementedVo.class);
            ApiSettlementedVo result = JSONObject.from(response.getBody()).toJavaObject(ApiSettlementedVo.class);
            for ( ApiSettlementedDataVo dataVo: result.getData()) {
                if (dataVo.getIsSettlement() != null && !dataVo.getIsSettlement()) {
                    throw new BusinessException(dataVo.getCode() + ":已结算，不可作废");
                }
            }
        }
    }

    @SysLogAnnotion("7、查询出库单是否可以作废")
    private void callOutCanceledAppCode10Api(List<BOutVo> list) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BOutVo vo : list) {
            BOutVo bOutVo = service.selectById(vo.getId());
            if (StringUtils.isEmpty(bOutVo.getExtra_code())) {
                continue;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bOutVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

//            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);

            if (apiCanceledVo.getData() != null) {
                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
                        throw new BusinessException("该出库单已结算,不能作废!");
                    }
                }
            }

        }
    }

    @SysLogAnnotion("8、查询出库单是否可以作废")
    private void callOutSyncedAppCode10Api(List<BOutVo> list) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BOutVo vo : list) {
            BOutVo bOutVo = service.selectById(vo.getId());
            if (StringUtils.isEmpty(bOutVo.getExtra_code())) {
                continue;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bOutVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

//            ApiCanceledVo apiCanceledVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiCanceledVo.class);
            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);

            if (apiCanceledVo.getData() != null) {
                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
                        throw new BusinessException("该出库单已结算,不能同步!");
                    }
                }
            }

        }
    }

    /**
     *
     * @param plan_detail_id
     * @param app_config_type
     */
    private void callAsyncApiController(Integer plan_detail_id, String app_config_type) {
        BOutVo vo = new BOutVo();
        vo.setId(plan_detail_id);
        callAsyncApiController(service.selectList(vo), app_config_type);
    }

//    /**
//     * 调用API接口，同步出库信息
//     * @param beans
//     * @param app_config_type
//     */
//    private void callAsyncApiController(List<BOutVo> beans, String app_config_type) {
//
//        if(beans == null || beans.size() == 0){
//            return;
//        }
//        for (BOutVo vo : beans) {
//            log.debug("=============同步出库信息start=============");
//            BOutVo bOutVo = service.selectById(vo.getId());
//            if(bOutVo.getExtra_code() == null || Objects.equals(bOutVo.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED)){
//                continue;
//            }
//            ApiOutPlanIdCodeVo apiOutPlanIdCodeBo = new ApiOutPlanIdCodeVo();
//            apiOutPlanIdCodeBo.setPlan_code(bOutVo.getPlan_code());
//            apiOutPlanIdCodeBo.setPlan_id(bOutVo.getPlan_id());
//            apiOutPlanIdCodeBo.setOut_code(bOutVo.getCode());
//            apiOutPlanIdCodeBo.setOut_id(bOutVo.getId());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, app_config_type);
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
////            ResponseEntity<ApiInPlanIdCodeVo> response = restTemplate.postForEntity(url, apiOutPlanIdCodeBo, ApiInPlanIdCodeVo.class);
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
//        }
//    }

    /**
     * 调用API接口，同步出库信息
     * @param beans
     * @param app_config_type
     */
  /*  private void callAsyncApiController(List<BOutVo> beans, String app_config_type) {
        log.debug("=============同步出库信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBOutAsyncVo asyncVo = new ApiBOutAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/async/out/execute", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步出库信息end=============");
    }*/

    /**
     * 响应结果处理回调方法
     * @param result
     */
    public void callback(String result) {
        log.debug("=============同步出库信息result=============" + result);
    }



    @SysLogAnnotion("出库单数据导入")
    @PostMapping("/import")
    public ResponseEntity<JsonResultAo<Object>> importData(@RequestBody(required = false) BOutImportVo vo, HttpServletResponse response) throws Exception {

        SPagesVo sPagesVo = new SPagesVo();
        sPagesVo.setCode(vo.getPage_code());
        SPagesVo pagesVo = isPagesService.get(sPagesVo);
        if (Objects.equals(pagesVo.getImport_processing(), Boolean.TRUE)) {
            throw new BusinessException("还有未完成的导入任务，请稍后重试");
        }

        try{
            SLogImportVo sLogImportVo = new SLogImportVo();

            sLogImportVo.setImport_json(pagesVo.getImport_json());
            sLogImportVo.setPage_code(pagesVo.getCode());
            sLogImportVo.setPage_name(pagesVo.getName());
            sLogImportVo.setUpload_url(vo.getUrl());



            isPagesService.updateImportProcessingTrue(pagesVo);
            // file bean 保存数据库

            // 文件下载并check类型
            // 1、获取模板配置类
            String json = pagesVo.getImport_json();
//        "{\"dataRows\":{\"dataCols\":[{\"index\":0,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"idx\"},{\"index\":1,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"order_no\"},{\"index\":2,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"plan_code\"},{\"index\":3,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"sku_code\"},{\"index\":4,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"goods_name\"},{\"index\":5,\"name\":\"pm\"},{\"index\":6,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"spec\"},{\"convertor\":\"datetime\",\"index\":7,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"outbound_time\"},{\"index\":8,\"listValidators\":[{\"param\":[],\"validtorName\":\"num\"},{\"param\":[],\"validtorName\":\"required\"}],\"name\":\"actual_weight\"},{\"index\":9,\"listValidators\":[{\"param\":[],\"validtorName\":\"required\"},{\"param\":[{\"name\":\"className\",\"value\":\"com.xinyirun.scm.core.system.serviceimpl.business.out.BOutServiceImpl\"},{\"name\":\"functionName\",\"value\":\"checkBOutPlan\"},{\"name\":\"errorMsg\",\"value\":\"出库计划单号有误\"}],\"validtorName\":\"reflection\"}],\"name\":\"unit_name\"}]},\"titleRows\":[{\"cols\":[{\"colSpan\":1,\"title\":\"序号(必填)\"},{\"colSpan\":1,\"title\":\"订单编号(必填)\"},{\"colSpan\":1,\"title\":\"出库计划单号(必填)\"},{\"colSpan\":1,\"title\":\"货物规格编码(必填)\"},{\"colSpan\":1,\"title\":\"货物名称(必填)\"},{\"colSpan\":1,\"title\":\"品名\"},{\"colSpan\":1,\"title\":\"规格(必填)\"},{\"colSpan\":1,\"title\":\"出库日期(必填)\"},{\"colSpan\":1,\"title\":\"实际出库数量(必填)\"},{\"colSpan\":1,\"title\":\"出库单位(必填)\"}]}]}";
            SystemExcelReader pr = super.downloadExcelAndImportData(vo.getUrl(), json);
            List<BOutImportVo> beans = pr.readBeans(BOutImportVo.class);

            if (pr.isDataValid()) {
                pr.closeAll();

                if (beans.size() == 0) {
                    isPagesService.updateImportProcessingFalse(pagesVo);

                    throw new BusinessException("导入失败,导入文件无数据");
                }

                // 读取没有错误，开始插入
                List<BOutVo> searchCondition = service.importBOut(beans);
                if (Objects.equals(pagesVo.getAuto_audit(), Boolean.TRUE)) {
                    // 自动提交
                    service.submit(searchCondition);
                    // 自动审核
                    service.audit(searchCondition);
                } else {
                    // 自动提交
                    service.submit(searchCondition);
                }

                isPagesService.updateImportProcessingFalse(pagesVo);

                sLogImportVo.setType(SystemConstants.LOG_FLG.OK);
                isLogImportService.insert(sLogImportVo);
                return ResponseEntity.ok().body(ResultUtil.OK(beans));
            } else {
                // 读取失败，需要返回错误
                File rtnFile = pr.getValidateResultsInFile(pr.getFileName());
                BOutImportVo errorInfo = super.uploadFile(rtnFile.getAbsolutePath(), BOutImportVo.class);
                pr.closeAll();

                isPagesService.updateImportProcessingFalse(pagesVo);

                sLogImportVo.setType(SystemConstants.LOG_FLG.NG);
                sLogImportVo.setError_url(errorInfo.getUrl());
                isLogImportService.insert(sLogImportVo);
                return ResponseEntity.ok().body(ResultUtil.OK(errorInfo, ResultEnum.IMPORT_DATA_ERROR));
            }
        } catch (Exception e) {
            throw e;
        } finally{
            isPagesService.updateImportProcessingFalse(pagesVo);
        }
    }

    @SysLogAnnotion("出库单数据导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<BOutVo> searchCondition, HttpServletResponse response) throws Exception {
        List<BOutExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<BOutExportVo> util = new EasyExcelUtil<>(BOutExportVo.class);
        util.exportExcel("出库单"  + DateTimeUtil.getDate(), "出库单", list, response);
    }

    @SysLogAnnotion("出库单数据导出")
    @PostMapping("/export_all")
    public void exportAllData(@RequestBody(required = false) BOutVo searchCondition, HttpServletResponse response) throws Exception {
        List<BOutExportVo> list = service.selectExportAllList(searchCondition);
        EasyExcelUtil<BOutExportVo> util = new EasyExcelUtil<>(BOutExportVo.class);
        util.exportExcel("出库单"  + DateTimeUtil.getDate(), "出库单", list, response);
    }

    /**
     * 调用API接口，同步出库信息
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

    @SysLogAnnotion("业务中台出库超发校验接口")
    private void callOutCheckExcessAppCode10Api(List<BOutVo> beans) {
        log.debug("=============业务中台出库超发校验接口=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CHECK_EXCESS);
        SAppConfigDetailVo sAppConfigDetailBorrow = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.OUT_CHECK_EXCESS_BORROW);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        String urlBorrow = getBusinessCenterUrl(sAppConfigDetailBorrow.getUri(), SystemConstants.APP_CODE.ZT);
        List<ApiOutCheckVo> apiOutCheckVos = bOutPlanService.selectOutCheckVoByOutBill(beans);
        for (ApiOutCheckVo apiOutCheckVo:apiOutCheckVos) {
            if (apiOutCheckVo == null) {
                continue;
            }
            String postUlr = url;
            if ("1".equals(apiOutCheckVo.getOrderType())) {
                postUlr = urlBorrow;
            }



            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(apiOutCheckVo, headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(postUlr, requestEntity, JSONObject.class);

            System.out.println(result);

//            ApiOutCheckResultVo apiOutCheckResultVo = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(result.getBody().getJSONObject("data")), ApiOutCheckResultVo.class);
            ApiOutCheckResultVo apiOutCheckResultVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiOutCheckResultVo.class);

            if (apiOutCheckResultVo != null && !"true".equals(apiOutCheckResultVo.getData())) {
                throw new BusinessException("出库超发校验失败，不可出库");
            }
        }

        log.debug("=============业务中台出库超发校验接口=============");
    }

    /**
     * 异步 重新计算 生产日报表 从当前作废数据的审核通过时间到 t-1 的数据
     * @param searchConditionList
     */
    private void recreateProductDaily(List<BOutVo> searchConditionList) {
        List<String> allCode = Lists.newArrayList("zlsd-0100509", "zlsd-0100510", "zlsd-0100508", "19", "CM-001",
                "zlsd-0100511", "zlsd-0100505", "zlsd-0100506","zlsd-0100507-3");
        BOutVo outVo = service.selectEdtAndGoodsCode(searchConditionList.get(0).getId());
        BProductDailyVo vo = new BProductDailyVo();
        if (outVo.getE_dt() != null && allCode.contains(outVo.getGoods_code())) {
            vo.setInit_time(outVo.getE_dt().format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD)));
            try {
                dailyProductV2Service.recreate2Cancel(vo);
            } catch (Exception e) {
                log.error("作废重置日加共报表出错, 参数--> {}", JSONObject.toJSONString(vo));
                log.error("recreateProductDaily error", e);
            }
        }
    }

}
