//package com.xinyirun.scm.api.controller.steel.async;
//
//
//import com.alibaba.fastjson2.JSONObject;
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.bean.api.vo.business.in.ApiBDeliveryAsyncVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiDeliveryPlanIdCodeVo;
//import com.xinyirun.scm.bean.system.vo.business.in.delivery.BDeliveryVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.core.system.service.business.in.delivery.IBDeliveryService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Objects;
//
///**
// * <p>
// * 提货单 前端控制器
// * </p>
// *
// * @author htt
// * @since 2021-09-24
// */
//@Slf4j
//// @Api(tags = "入库计划")
//@RestController
//@RequestMapping(value = "/api/service/v1/steel/async/delivery")
//public class ApiBDeliveryAsyncController extends ApiBaseController {
//
//    @Autowired
//    private IBDeliveryService deliveryService;
//
//    @Autowired
//    private ISAppConfigDetailService isAppConfigDetailService;
//
//
//
//    /**
//     * 调用API接口，同步入库信息
//     */
//    @SysLogApiAnnotion("提货单数据同步")
//    @PostMapping("/execute")
//    @ResponseBody
//    public void execute(@RequestBody ApiBDeliveryAsyncVo asyncVo) {
//        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
//            return;
//        }
//        for (BDeliveryVo vo : asyncVo.getBeans()) {
//            log.debug("=============同步提货单信息start=========");
//            BDeliveryVo bDeliveryVo = deliveryService.selectById(vo.getId());
//
//            // extra_code为空或者状态为作废审核中的不同步
//            if(bDeliveryVo.getExtra_code() == null || Objects.equals(bDeliveryVo.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_SAVED) || Objects.equals(bDeliveryVo.getStatus(), DictConstant.DICT_B_DELIVERY_STATUS_CANCEL_BEING_AUDITED)){
//                continue;
//            }
//
//            ApiDeliveryPlanIdCodeVo deliveryPlanIdCodeVo = new ApiDeliveryPlanIdCodeVo();
//            deliveryPlanIdCodeVo.setPlan_code(bDeliveryVo.getPlan_code());
//            deliveryPlanIdCodeVo.setPlan_id(bDeliveryVo.getPlan_id());
//            deliveryPlanIdCodeVo.setDelivery_code(bDeliveryVo.getCode());
//            deliveryPlanIdCodeVo.setDelivery_id(bDeliveryVo.getId());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, deliveryPlanIdCodeVo, JSONObject.class);
//            log.debug("=============同步提货单信息result============="+response.getBody());
//
//            log.debug("=============同步提货单信息end=============");
//        }
//    }
//
//
//}
