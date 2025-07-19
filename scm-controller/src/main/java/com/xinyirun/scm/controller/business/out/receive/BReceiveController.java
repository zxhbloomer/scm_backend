package com.xinyirun.scm.controller.business.out.receive;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.api.vo.business.ApiBAllAsyncVo;
import com.xinyirun.scm.bean.api.vo.business.ApiCanceledDataVo;
import com.xinyirun.scm.bean.api.vo.business.ApiCanceledVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckResultVo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveSumVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.excel.out.BReceiveExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.business.wms.out.IBOutPlanService;
import com.xinyirun.scm.core.system.service.business.wms.out.receive.IBReceiveService;
import com.xinyirun.scm.core.system.service.business.sync.IBSyncStatusErrorService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.mq.rabbitmq.producer.business.sync.ywzt.SyncPush2BusinessPlatformAllInOneMqProducter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 收货单 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2024-07-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/receive")
public class BReceiveController extends SystemBaseController{

    @Autowired
    private IBReceiveService ibReceiveService;

    @Autowired
    private IBSyncStatusErrorService syncErrorService;

    @Autowired
    private SyncPush2BusinessPlatformAllInOneMqProducter producter;

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private IBOutPlanService bOutPlanService;

    @SysLogAnnotion("查询提货单列表, 不分页")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BReceiveVo>>> list(@RequestBody(required = false) BReceiveVo searchCondition) {
        List<BReceiveVo> list = ibReceiveService.selectPageListNotCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询提货单列表, 不分页")
    @PostMapping("/list/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BReceiveVo>> selectListCount(@RequestBody(required = false) BReceiveVo searchCondition) {
        BReceiveVo result = ibReceiveService.selectListCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("查询提货单列表，获取代办信息")
    @PostMapping("/todo/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody(required = false) BReceiveVo searchCondition) {
        Integer count = ibReceiveService.selectTodoCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

    @SysLogAnnotion("根据查询条件，获取提货单列表合计信息")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BReceiveSumVo>> sum(@RequestBody(required = false) BReceiveVo searchCondition) {
        BReceiveSumVo vo = ibReceiveService.selectSumData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }
    @SysLogAnnotion("获取收货单信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BReceiveVo>> get(@RequestBody(required = false) BReceiveVo searchCondition) {
        BReceiveVo vo = ibReceiveService.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("收货单数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BReceiveVo>> save(@RequestBody(required = false) BReceiveVo bean) {

        if(ibReceiveService.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(ibReceiveService.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        // 业务中台出库超发校验接口
        callOutCheckExcessAppCode10Api(searchConditionList);

        ibReceiveService.submit(searchConditionList);

        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.RECEIVE_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        // 业务中台出库超发校验接口
        callOutCheckExcessAppCode10Api(searchConditionList);

        ibReceiveService.audit(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.RECEIVE_AUDIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    @PostMapping("/cancelaudit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelAudit(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        // 先进行check，调用外部api接口
        callOutCanceledAppCode10Api(searchConditionList);

        // 执行作废操作
        ibReceiveService.cancelAudit(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.RECEIVE_CANCEL);

        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    @PostMapping("/return")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> reject(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        ibReceiveService.reject(searchConditionList);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("根据选择的数据驳回，部分数据")
    @PostMapping("/cancelreturn")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancelReject(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        ibReceiveService.cancelReject(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据废除，部分数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {

        // 先进行check，调用外部api接口
        callOutCanceledAppCode10Api(searchConditionList);

        // 执行作废操作
        ibReceiveService.cancel(searchConditionList);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.OUT_CANCEL);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("根据选择的数据同步，部分数据")
    @PostMapping("/syncall")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> syncAll() {
        BReceiveVo vo = new BReceiveVo();
        List<BReceiveVo> list = ibReceiveService.selectList(vo);
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(list, SystemConstants.APP_URI_TYPE.RECEIVE_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据同步，部分数据")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> sync(@RequestBody(required = false) List<BReceiveVo> searchConditionList) {
        // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
        callAsyncApiController(searchConditionList, SystemConstants.APP_URI_TYPE.RECEIVE_SUBMIT);
        // 返回
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("收货单数据导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<BReceiveVo> searchCondition, HttpServletResponse response) throws Exception {
        List<BReceiveExportVo> list = ibReceiveService.selectExportList(searchCondition);
        EasyExcelUtil<BReceiveExportVo> util = new EasyExcelUtil<>(BReceiveExportVo.class);
        util.exportExcel("收货单"  + DateTimeUtil.getDate(), "收货单", list, response);
    }

    @SysLogAnnotion("收货单数据导出")
    @PostMapping("/export_all")
    public void exportAllData(@RequestBody(required = false) BReceiveVo searchCondition, HttpServletResponse response) throws Exception {
        List<BReceiveExportVo> list = ibReceiveService.selectExportAllList(searchCondition);
        EasyExcelUtil<BReceiveExportVo> util = new EasyExcelUtil<>(BReceiveExportVo.class);
        util.exportExcel("收货单"  + DateTimeUtil.getDate(), "收货单", list, response);
    }

    @SysLogAnnotion("7、查询出库单是否可以作废")
    private void callOutCanceledAppCode10Api(List<BReceiveVo> list) {
        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.RECEIVE_CANCELED);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        for (BReceiveVo vo : list) {
            BReceiveVo bReceiveVo = ibReceiveService.selectById(vo.getId());
            if (StringUtils.isEmpty(bReceiveVo.getExtra_code())) {
                continue;
            }

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> requestEntity = new HttpEntity(bReceiveVo.getCode(), headers);
            ResponseEntity<JSONObject> result = restTemplate.postForEntity(url, requestEntity, JSONObject.class);

            ApiCanceledVo apiCanceledVo = JSONObject.from(result.getBody().getJSONObject("data")).toJavaObject(ApiCanceledVo.class);

            if (apiCanceledVo.getData() != null) {
                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
                        throw new BusinessException("该收货单已结算,不能作废!");
                    }
                }
            }

        }
    }
    @SysLogAnnotion("业务中台出库超发校验接口")
    private void callOutCheckExcessAppCode10Api(List<BReceiveVo> beans) {
        log.debug("=============业务中台出库超发校验接口=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.RECEIVE_CHECK_EXCESS);
        SAppConfigDetailVo sAppConfigDetailBorrow = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, SystemConstants.APP_URI_TYPE.RECEIVE_CHECK_EXCESS_BORROW);
        String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
        String urlBorrow = getBusinessCenterUrl(sAppConfigDetailBorrow.getUri(), SystemConstants.APP_CODE.ZT);
        List<ApiOutCheckVo> apiOutCheckVos = bOutPlanService.selectReceiveCheckVoByOutBill(beans);
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
     * 调用API接口，同步出库信息
     * @param beans
     * @param app_config_type
     */
    private void callAsyncApiController(List<BReceiveVo> beans, String app_config_type) {
        log.debug("=============同步收货单信息start=============");
        if(beans == null || beans.size() == 0){
            return;
        }

        ApiBAllAsyncVo asyncVo = new ApiBAllAsyncVo();
        asyncVo.setBeans(beans);
        asyncVo.setApp_config_type(app_config_type);
        producter.mqSendMq(asyncVo, DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE);
        Set<Integer> collect = beans.stream().map(BReceiveVo::getId).collect(Collectors.toSet());
        syncErrorService.updateSyncErrorStatus(collect, DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE, "ING");
        log.debug("=============同步收货单信息end=============");
    }

}
