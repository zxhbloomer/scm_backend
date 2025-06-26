//package com.xinyirun.scm.api.controller.steel;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.api.controller.steel.util.ApiSteelInUtilController;
//import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
//import com.xinyirun.scm.bean.api.bo.steel.ApiDeliveryConfirmBo;
//import com.xinyirun.scm.bean.api.bo.steel.ApiInPlanResultBo;
//import com.xinyirun.scm.bean.api.bo.steel.ApiOutPlanResultBo;
//import com.xinyirun.scm.bean.api.vo.business.ApiCanceledDataVo;
//import com.xinyirun.scm.bean.api.vo.business.ApiCanceledVo;
//import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
//import com.xinyirun.scm.bean.api.vo.business.monitor.ApiMonitorVo;
//import com.xinyirun.scm.bean.api.vo.business.orderdoc.ApiDeliveryConfirmVo;
//import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckResultVo;
//import com.xinyirun.scm.bean.api.vo.business.out.ApiOutCheckVo;
//import com.xinyirun.scm.bean.api.vo.sync.*;
//import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
//import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.ApiSteelConstants;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.common.exception.api.ApiBusinessException;
//import com.xinyirun.scm.common.utils.RuntimeEnvUtil;
//import com.xinyirun.scm.core.api.service.business.v1.in.ApiIInService;
//import com.xinyirun.scm.core.api.service.business.v1.in.deliveryconfirm.ApiIBInOrderGoodsDeliveryConfirmService;
//import com.xinyirun.scm.core.api.service.business.v1.out.ApiIOutService;
//import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping(value = "/api/service/v1/steel")
//@Slf4j
//public class ApiSteelController extends ApiBaseController {
//
//    @Autowired
//    ApiIBSyncStatusService apiIBSyncStatusService;
//
//    @Autowired
//    ApiIInService apiInService;
//
//    @Autowired
//    ApiIOutService apiOutService;
//
//    @Autowired
//    ApiSteelInUtilController apiSteelInUtilController;
//
//    @Autowired
//    ApiIBInOrderGoodsDeliveryConfirmService confirmService;
//
//    @SysLogApiAnnotion("5、返回中台入库数据")
//    @PostMapping("/plan/in")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> in(@RequestBody ApiInPlanIdCodeVo bean) {
//
//        // 查询数据
//        ApiInPlanResultBo rtn = apiInService.getSyncInResultAppCode10(bean).get(0);
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getPlan_id());
//        bsyncStatus.setSerial_detail_id(rtn.getPlan_detail_id());
//        bsyncStatus.setSerial_code(rtn.getPlan_code());
//        bsyncStatus.setSerial_detail_code(rtn.getPlan_detail_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN_PLAN);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_IN_URI);
//
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台入库数据 /plan/in:", e);
//            throw new ApiBusinessException("入库计划数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("5、返回中台入库单数据")
//    @PostMapping("/plan/in/bill")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> inbill(@RequestBody ApiInPlanIdCodeVo bean) {
//
//        if(!inSettled(bean)) {
//            return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//        }
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getIn_id());
//        bsyncStatus.setSerial_code(bean.getIn_code());
//        bsyncStatus.setSerial_detail_id(bean.getIn_id());
//        bsyncStatus.setSerial_detail_code(bean.getIn_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_IN);
//
//        // 查询数据
//        ApiInPlanResultBo rtn = apiInService.getSyncInResultAppCode10(bean).get(0);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_IN_BILL_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台入库单数据 /plan/in/bill:", e);
//            throw new ApiBusinessException("入库单数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("9、返回中台出库数据")
//    @PostMapping("/plan/out")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> out(@RequestBody ApiOutPlanIdCodeVo bean) {
//        // 查询数据
//        ApiOutPlanResultBo rtn = apiInService.getSyncOutResultAppCode10(bean).get(0);
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getPlan_id());
//        bsyncStatus.setSerial_detail_id(rtn.getPlan_detail_id());
//        bsyncStatus.setSerial_detail_code(rtn.getPlan_detail_code());
//        bsyncStatus.setSerial_code(rtn.getPlan_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
//
//        if (bean.getDiscontinue() != null && bean.getDiscontinue() == true) {
//            rtn.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
//        }
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台出库数据 /plan/out:", e);
//            throw new ApiBusinessException("出库计划数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    @SysLogApiAnnotion("返回中台出库计划作废数据")
//    @PostMapping("/plan/out/cancel")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> cancelOut(@RequestBody ApiOutPlanIdCodeVo bean) {
//        // 查询数据
//        ApiOutPlanResultBo rtn = apiInService.getSyncOutResultAppCode10(bean).get(0);
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getPlan_id());
//        bsyncStatus.setSerial_code(rtn.getPlan_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_PLAN_CANCEL_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台出库计划作废数据 /plan/out/cancel", e.getMessage());
//            throw new ApiBusinessException("出库计划作废数据同步至中台发生了错误");
//        }
//
//        // 定义执行条件，记录执行是否成功
//        bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getPlan_id());
//        bsyncStatus.setSerial_code(rtn.getPlan_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT_PLAN);
//
//        if (bean.getDiscontinue() != null && bean.getDiscontinue() == true) {
//            rtn.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
//        }
//
//        // 定义同步bean
//        apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台出库计划作废数据错误 /plan/out/cancel", e.getMessage());
//            throw new ApiBusinessException("出库计划数据同步至中台发生了错误");
//        }
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    @SysLogApiAnnotion("9、返回中台出库数据")
//    @PostMapping("/plan/out/bill")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> outbill(@RequestBody ApiOutPlanIdCodeVo bean) {
//
//        if(!outSettled(bean)) {
//            return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//        }
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getOut_id());
//        bsyncStatus.setSerial_code(bean.getOut_code());
//        bsyncStatus.setSerial_detail_id(bean.getOut_id());
//        bsyncStatus.setSerial_detail_code(bean.getOut_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OUT);
//
//        // 查询数据
//        ApiOutPlanResultBo rtn = apiInService.getSyncOutResultAppCode10(bean).get(0);
//
//        if (bean.getDiscontinue() != null && bean.getDiscontinue() == true) {
//            rtn.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
//        }
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_BILL_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台出库数据 /plan/out/bill", e.getMessage());
//            throw new ApiBusinessException("出库单数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    @SysLogApiAnnotion("6、查询入库单是否能作废结算")
//    @PostMapping("/in/canceled")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<ApiCanceledVo>> inCanceled(@RequestBody String code) {
//        code = code.replaceAll("\"", "");
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("code", code);
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_IN_CANCELED_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardForm(apisync);
//        } catch (Exception e) {
//            // 保存数据
//            log.error("查询入库单是否能作废结算 /in/canceled", e.getMessage());
//            throw new ApiBusinessException("调用业务中台出库单是否被结算发生了错误");
//        }
//
////        ApiCanceledVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiCanceledVo.class);
//        ApiCanceledVo result = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("7、查询出库单是否能作废结算")
//    @PostMapping("/out/canceled")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<ApiCanceledVo>> outCanceled(@RequestBody String code) {
//        code = code.replaceAll("\"", "");
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("code", code);
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_CANCELED_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardForm(apisync);
//        } catch (Exception e) {
//            // 保存数据
//            log.error("查询出库单是否能作废结算 /out/canceled", e.getMessage());
//            throw new ApiBusinessException("调用业务中台出库单是否被结算发生了错误");
//        }
//
////        ApiCanceledVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiCanceledVo.class);
//        ApiCanceledVo result = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("7、查询出库单是否能作废结算")
//    @PostMapping("/out/settlemented")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<List<ApiCanceledVo>>> outSettlemented(@RequestBody List<String> codeList) {
//        List<ApiCanceledVo> result = new ArrayList<>();
//        for (String code : codeList) {
//            // 定义同步bean
//            ApiSyncVo apisync = new ApiSyncVo();
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//            params.add("code", code);
//            apisync.setData(params);
//            apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_CANCELED_URI);
//
//            ResponseEntity<JSONObject> response;
//            // 调用外部系统的url
//            try {
//                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//                response = apiSteelInUtilController.doForwardForm(apisync);
//            } catch (Exception e) {
//                // 保存数据
//                log.error("查询出库单是否能作废结算 /out/settlemented", e.getMessage());
//                throw new ApiBusinessException("调用业务中台出库单是否被结算发生了错误");
//            }
//
////        ApiCanceledVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiCanceledVo.class);
//            ApiCanceledVo vo = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//            result.add(vo);
//        }
//
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("40、出库超发校验接口")
//    @PostMapping("/out/check/excess")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<ApiOutCheckResultVo>> checkExcess(@RequestBody ApiOutCheckVo vo) {
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
//        params.add("houseOutPlanCode", vo.getHouseOutPlanCode());
//        params.add("houseOutDirectCode", vo.getHouseOutDirectCode());
//        params.add("outNum", vo.getOutNum());
//
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_CHECK_EXCESS_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardFormGet(apisync);
//        } catch (Exception e) {
//            // 保存数据
//            log.error("出库超发校验接口 /out/check/excess", e.getMessage());
//            throw new ApiBusinessException("查询出库单是否能作废结算发生了错误");
//        }
//
////        ApiOutCheckResultVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiOutCheckResultVo.class);
//        ApiOutCheckResultVo result = JSONObject.from(response.getBody()).toJavaObject(ApiOutCheckResultVo.class);
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("41、出库超发校验接口-借货")
//    @PostMapping("/out/check/excess/borrow")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<ApiOutCheckResultVo>> checkBorrowExcess(@RequestBody ApiOutCheckVo vo) {
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
//        params.add("houseOutPlanCode", vo.getHouseOutPlanCode());
//        params.add("houseOutDirectCode", vo.getHouseOutDirectCode());
//        params.add("outNum", vo.getOutNum());
//
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_CHECK_EXCESS_BORROW_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardFormGet(apisync);
//        } catch (Exception e) {
//            // 保存数据
//            log.error("出库超发校验接口-借货 /out/check/excess/borrow", e.getMessage());
//            throw new ApiBusinessException("查询出库单是否能作废结算发生了错误");
//        }
//
////        ApiOutCheckResultVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiOutCheckResultVo.class);
//        ApiOutCheckResultVo result = JSONObject.from(response.getBody()).toJavaObject(ApiOutCheckResultVo.class);
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("25、收货确认函")
//    @PostMapping("/delivery/confirm")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> deliveryConfirm(@RequestBody ApiDeliveryConfirmBo bo) {
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_DELIVERY_CONFIRM);
//
//        // 查询数据
//        List<ApiDeliveryConfirmVo> vos = confirmService.getDeliveryConfirmLists(bo);
//        for (ApiDeliveryConfirmVo vo : vos) {
//            apisync.setData(vo);
//            ResponseEntity<JSONObject> response;
//            // 调用外部系统的url
//            try {
//                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//                response = apiSteelInUtilController.doForward(apisync);
//                log.debug("25、收货确认函" + response);
//            } catch (Exception e) {
//                // 保存数据
//                log.error("收货确认函 /delivery/confirm", e.getMessage());
//                throw new ApiBusinessException("同步收货确认函发生了错误！");
//            }
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
//    }
//
//    @SysLogApiAnnotion(value = "同步每日库存数据到业务中台", noParam = true)
//    @PostMapping("/inventory/daily/sync")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncDailyInventory(@RequestBody ApiDailyInventorySyncVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
////        apisync.setData(bean.getList());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_DAILY_INVENTORY_START_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForwardArray(apisync);
//        } catch (Exception e) {
//            log.error("同步每日库存数据到业务中台 /inventory/daily/sync", e.getMessage());
//            throw new ApiBusinessException("每日库存开始错误");
//        }
//
//
//        List<List<ApiDailyInventoryVo>> result = new ArrayList<List<ApiDailyInventoryVo>>();
//        // 每次同步1000条数据
//        int len = 1000;
//        int size = bean.getList().size();
//        int count = (size + len - 1) / len;
//
//        for (int i = 0; i < count; i++) {
//            List<ApiDailyInventoryVo> subList = bean.getList().subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
//            result.add(subList);
//        }
//
//        for (List<ApiDailyInventoryVo> voList : result) {
//            // 定义同步bean
//            apisync = new ApiSyncVo();
//            apisync.setStatusBean(bsyncStatus);
//            apisync.setData(voList);
//            apisync.setSync_type(ApiSteelConstants.API_STEEL_DAILY_INVENTORY_URI);
//
//            // 调用外部系统的url，并记录执行是否成功
//            try {
//                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//                apiSteelInUtilController.doForwardArray(apisync);
//            } catch (Exception e) {
//                log.error("同步每日库存数据到业务中台 /inventory/daily/sync error", e);
//                throw new ApiBusinessException("每日库存同步错误");
//            }
//        }
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    @SysLogApiAnnotion("同步物料转换商品价格数据到业务中台")
//    @PostMapping("/materialconvert/price/sync")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncMaterialConvertPrice(@RequestBody ApiMaterialConvertPriceSyncVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MATERIAL_CONVERT);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(bean.getList());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_MATERIAL_CONVERT_PRICE_START_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            log.error("同步物料转换商品价格数据到业务中台 /materialconvert/price/sync error", e);
//            throw new ApiBusinessException("同步物料转换商品价格开始错误");
//        }
//
//        // 定义同步bean
//        bsyncStatus.setId(null);
//        apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(bean.getList());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_MATERIAL_CONVERT_PRICE_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            log.error("同步物料转换商品价格数据到业务中台 /materialconvert/price/sync error", e);
//            throw new ApiBusinessException("同步物料转换商品价格同步错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//
//    @SysLogApiAnnotion("同步商品价格数据到业务中台")
//    @PostMapping("/material/price/sync")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncMaterialPrice(@RequestBody ApiMaterialPriceSyncVo vo) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MATERIAL_PRICE);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(vo.getList());
////        apisync.setData(bean.getList());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_MATERIAL_PRICE_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            log.error("同步商品价格数据到业务中台 /material/price/sync error", e);
//            throw new ApiBusinessException("物料单价同步错误");
//        }
//
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("同步每日货值数据到业务中台")
//    @PostMapping("/inventory/price/sync")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncDailyInventoryPrice(@RequestBody ApiDailyInventoryPriceSyncVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY_PRICE);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(bean.getList());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_DAILY_INVENTORY_PRICE_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            log.error("同步每日货值数据到业务中台 /inventory/price/sync error", e);
//            throw new ApiBusinessException("同步每日货值同步错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("同步每日货值数据到业务中台")
//    @PostMapping("/inventory/price/sync/all")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncDailyInventoryPriceAll(@RequestBody ApiDailyInventoryPriceSyncVo bean) {
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY_PRICE);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(new ArrayList<>());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_DAILY_INVENTORY_PRICE_START_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            log.error("同步每日货值数据到业务中台 /inventory/price/sync error", e);
//            throw new ApiBusinessException("全量删除每日货值错误");
//        }
//
//
//        List<List<ApiDailyInventoryPriceVo>> result = new ArrayList<List<ApiDailyInventoryPriceVo>>();
//        // 每次同步1000条数据
//        int len = 1000;
//        int size = bean.getList().size();
//        int count = (size + len - 1) / len;
//
//        for (int i = 0; i < count; i++) {
//            List<ApiDailyInventoryPriceVo> subList = bean.getList().subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
//            result.add(subList);
//        }
//
//        for (List<ApiDailyInventoryPriceVo> voList : result) {
//            // 定义执行条件，记录执行是否成功
//            bsyncStatus = new BSyncStatusVo();
//            bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_DAILY_INVENTORY_PRICE);
//
//            // 定义同步bean
//            apisync = new ApiSyncVo();
//            apisync.setStatusBean(bsyncStatus);
//            apisync.setData(voList);
//            apisync.setSync_type(ApiSteelConstants.API_STEEL_DAILY_INVENTORY_PRICE_URI);
//
//            // 调用外部系统的url，并记录执行是否成功
//            try {
//                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//                ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//                bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//                // 保存数据，记录是否成功
//                apiIBSyncStatusService.save(bsyncStatus);
//            } catch (Exception e) {
//                bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//                // 保存数据，记录是否成功
//                apiIBSyncStatusService.save(bsyncStatus);
//                log.error("同步每日货值数据到业务中台 /inventory/price/sync error", e);
//                throw new ApiBusinessException("同步每日货值all同步错误");
//            }
//        }
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("查询监管任务是否能作废结算")
//    @PostMapping("/monitor/canceled")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<ApiCanceledVo>> monitorCanceled(@RequestBody String code) {
//        code = code.replaceAll("\"", "");
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("wmsCode", code);
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_MONITOR_CANCEL_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardForm(apisync);
//        } catch (Exception e) {
//            // 保存数据
//            log.error("查询监管任务是否能作废结算 /monitor/canceled error", e);
//            throw new ApiBusinessException("调用业务中台监管任务是否被结算发生了错误");
//        }
//
////        ApiCanceledVo result = JSONObject.toJavaObject((JSONObject)JSONObject.toJSON(response.getBody()), ApiCanceledVo.class);
//        ApiCanceledVo result = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
//    }
//
//    @SysLogApiAnnotion("同步监管任务数据")
//    @PostMapping("/monitor/sync")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> monitorsync(@RequestBody ApiMonitorVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getId());
//        bsyncStatus.setSerial_code(bean.getWmsCode());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR);
////        List<ApiMonitorVo> list = new ArrayList<>();
////        list.add(bean);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_MONITOR_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            if (RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.ZHONGLIN) || RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.DEV) || RuntimeEnvUtil.getEnv().equals(RuntimeEnvUtil.ENV.YS)) {
//                apisync.setApiSteelUrl(ApiSteelConstants.API_STEEL_URL);
//                bean.setType(1);
//                bean.setTypeName("承运");
//                apisync.setData(List.of(bean));
//                apiSteelInUtilController.doForwardRetryableList(apisync);
//
//                // 只有承运商是上海青润盛禾农业有限公司，社会统一信用代码91310120MA7EYB0AXE 传给青润
//                if (SystemConstants.CUSTOMER_CREDIT_NO.SHHRSHYYXGS.equals(bean.getCreditNo())) {
//                    // url 2 是 托运
//                    bean.setType(2);
//                    bean.setTypeName("托运");
//                    bean.setOrderCode(null);
//                    apisync.setData(List.of(bean));
//                    apisync.setApiSteelUrl(ApiSteelConstants.API_STEEL_URL2);
//                    apiSteelInUtilController.doForwardRetryableList(apisync);
//                }
//            } else {
//                bean.setType(1);
//                bean.setTypeName("承运");
//                apisync.setData(List.of(bean));
//                apisync.setApiSteelUrl(ApiSteelConstants.API_STEEL_URL);
//                apiSteelInUtilController.doForwardRetryableList(apisync);
//            }
//        } catch (Exception e) {
//            log.error("同步监管任务数据 /monitor/sync error", e);
//            throw new ApiBusinessException("监管任务数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("同步物流订单数据到业务中台")
//    @PostMapping("/schedule/sync/all")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> syncScheduleAll(@RequestBody ApiScheduleSyncVo bean) {
//
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SCHEDULE);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(new ArrayList<>());
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_B_SCHEDULE_START_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//        } catch (Exception e) {
//            bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//            // 保存数据，记录是否成功
//            apiIBSyncStatusService.save(bsyncStatus);
//            log.error("同步物流订单数据到业务中台 /schedule/sync/all error", e);
//            throw new ApiBusinessException("全量删除每日货值错误");
//        }
//
//
//        List<List<ApiScheduleVo>> result = new ArrayList<List<ApiScheduleVo>>();
//        // 每次同步1000条数据
//        int len = 1000;
//        int size = bean.getList().size();
//        int count = (size + len - 1) / len;
//
//        for (int i = 0; i < count; i++) {
//            List<ApiScheduleVo> subList = bean.getList().subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
//            result.add(subList);
//        }
//
//        for (List<ApiScheduleVo> voList : result) {
//            // 定义执行条件，记录执行是否成功
//            bsyncStatus = new BSyncStatusVo();
//            bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SCHEDULE);
//
//            // 定义同步bean
//            apisync = new ApiSyncVo();
//            apisync.setStatusBean(bsyncStatus);
//            apisync.setData(voList);
//            apisync.setSync_type(ApiSteelConstants.API_STEEL_B_SCHEDULE_URI);
//
//            // 调用外部系统的url，并记录执行是否成功
//            try {
//                // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//                ResponseEntity<JSONObject> response = apiSteelInUtilController.doForwardArray(apisync);
//                bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.SUCCESS);
//                // 保存数据，记录是否成功
//                apiIBSyncStatusService.save(bsyncStatus);
//            } catch (Exception e) {
//                bsyncStatus.setStatus(SystemConstants.B_SYNC_STATUS.FAILED);
//                // 保存数据，记录是否成功
//                apiIBSyncStatusService.save(bsyncStatus);
//                log.error("同步物流订单数据到业务中台 /schedule/sync/all error", e);
//                throw new ApiBusinessException("同步每日货值all同步错误");
//            }
//        }
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("5、返回中台提货单数据")
//    @PostMapping("/plan/delivery/bill")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> deliveryBill(@RequestBody ApiDeliveryPlanIdCodeVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getDelivery_id());
//        bsyncStatus.setSerial_code(bean.getDelivery_code());
//        bsyncStatus.setSerial_detail_id(bean.getDelivery_id());
//        bsyncStatus.setSerial_detail_code(bean.getDelivery_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_DELIVERY);
//
//        // 查询数据
//        ApiInPlanResultBo rtn = apiInService.getSyncDeliveryResultAppCode10(bean).get(0);
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_IN_BILL_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台入库单数据 /plan/delivery/bill:", e);
//            throw new ApiBusinessException("入库单数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//    }
//
//    @SysLogApiAnnotion("9、返回中台收货单数据")
//    @PostMapping("/plan/receive/bill")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> receiveBill(@RequestBody ApiReceivePlanIdCodeVo bean) {
//        // 定义执行条件，记录执行是否成功
//        BSyncStatusVo bsyncStatus = new BSyncStatusVo();
//        bsyncStatus.setSerial_id(bean.getReceive_id());
//        bsyncStatus.setSerial_code(bean.getReceive_code());
//        bsyncStatus.setSerial_detail_id(bean.getReceive_id());
//        bsyncStatus.setSerial_detail_code(bean.getReceive_code());
//        bsyncStatus.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE);
//
//        // 查询数据
//        ApiOutPlanResultBo rtn = apiInService.getSyncReceiveResultAppCode10(bean).get(0);
//
//        if (bean.getDiscontinue() != null && bean.getDiscontinue() == true) {
//            rtn.setStatusCode(SystemConstants.API_STATUS_DISCONTINUE);
//        }
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        apisync.setStatusBean(bsyncStatus);
//        apisync.setData(rtn);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_BILL_URI);
//
//        // 调用外部系统的url，并记录执行是否成功
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            apiSteelInUtilController.doForward(apisync);
//        } catch (Exception e) {
//            log.error("返回中台出库数据 /plan/receive/bill", e.getMessage());
//            throw new ApiBusinessException("出库单数据同步至中台发生了错误");
//        }
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    /**
//     * 查询出库单是否已经结算
//     * @param apiOutPlanIdCodeVo
//     * @return
//     */
//    public Boolean outSettled(ApiOutPlanIdCodeVo apiOutPlanIdCodeVo) {
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("code", apiOutPlanIdCodeVo.getOut_code());
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_OUT_CANCELED_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardForm(apisync);
//            ApiCanceledVo apiCanceledVo = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//
//            if (apiCanceledVo.getData() != null) {
//                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
//                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
//                        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/plan/out/sync/skip", SystemConstants.APP_CODE.ZT);
//
//                        HttpHeaders headers = new HttpHeaders();
//                        HttpEntity<String> requestEntity = new HttpEntity(apiOutPlanIdCodeVo, headers);
//                        response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//                        System.out.println(response.getBody());
//
//                        return Boolean.FALSE;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("查询出库单是否结算", e.getMessage());
//        }
//
//        return Boolean.TRUE;
//    }
//
//    /**
//     * 查询入库单是否已经结算
//     * @param apiInPlanIdCodeVo
//     * @return
//     */
//    public Boolean inSettled(ApiInPlanIdCodeVo apiInPlanIdCodeVo) {
//
//        // 定义同步bean
//        ApiSyncVo apisync = new ApiSyncVo();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
//        params.add("code", apiInPlanIdCodeVo.getIn_code());
//        apisync.setData(params);
//        apisync.setSync_type(ApiSteelConstants.API_STEEL_IN_CANCELED_URI);
//
//        ResponseEntity<JSONObject> response;
//        // 调用外部系统的url
//        try {
//            // 调用外部接口，同步数据，注入当前Bean使得调用内部方法也被SpringAOP拦截
//            response = apiSteelInUtilController.doForwardForm(apisync);
//            ApiCanceledVo apiCanceledVo = JSONObject.from(response.getBody()).toJavaObject(ApiCanceledVo.class);
//
//            if (apiCanceledVo.getData() != null) {
//                for(ApiCanceledDataVo apiCanceledDataVo: apiCanceledVo.getData()) {
//                    if (apiCanceledDataVo.getCancel() != null && !apiCanceledDataVo.getCancel()) {
//
//                        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/plan/in/sync/skip", SystemConstants.APP_CODE.ZT);
//
//                        HttpHeaders headers = new HttpHeaders();
//                        HttpEntity<String> requestEntity = new HttpEntity(apiInPlanIdCodeVo, headers);
//                        response = restTemplate.postForEntity(url, requestEntity, JSONObject.class);
//                        System.out.println(response.getBody());
//
//                        return Boolean.FALSE;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error("查询入库单是否结算", e.getMessage());
//        }
//
//        return Boolean.TRUE;
//    }
//
//    @SysLogApiAnnotion("出库单手动同步跳过")
//    @PostMapping("/plan/out/sync/skip")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> outSyncSkip(@RequestBody ApiOutPlanIdCodeVo bean) {
//
//        // 仅做记录日志用，无其他逻辑
//        log.debug("出库单手动同步跳过" + bean.getOut_code());
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//    @SysLogApiAnnotion("出库单手动同步跳过")
//    @PostMapping("/plan/in/sync/skip")
//    @ResponseBody
//    public ResponseEntity<ApiJsonResultAo<String>> inSyncSkip(@RequestBody ApiOutPlanIdCodeVo bean) {
//
//        // 仅做记录日志用，无其他逻辑
//        log.debug("入库单手动同步跳过" + bean.getOut_code());
//
//        return ResponseEntity.ok().body(ApiResultUtil.OK("ok"));
//
//    }
//
//}
