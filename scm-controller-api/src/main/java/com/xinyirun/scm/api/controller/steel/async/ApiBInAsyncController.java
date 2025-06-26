//package com.xinyirun.scm.api.controller.steel.async;
//
//
//import com.alibaba.fastjson2.JSONObject;
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.bean.api.vo.business.in.ApiBInAsyncVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
//import com.xinyirun.scm.bean.system.vo.business.in.v1.BInVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.core.api.service.business.v1.sync.ApiIBSyncStatusService;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInPlanDetailService;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInService;
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
// * 入库计划 前端控制器
// * </p>
// *
// * @author htt
// * @since 2021-09-24
// */
//@Slf4j
//// @Api(tags = "入库计划")
//@RestController
//@RequestMapping(value = "/api/service/v1/steel/async/in")
//public class ApiBInAsyncController extends ApiBaseController {
//
//    @Autowired
//    private IBInPlanDetailService inPlanDetailService;
//
//    @Autowired
//    private IBInService ibInService;
//
//    @Autowired
//    private ISAppConfigDetailService isAppConfigDetailService;
//
//    @Autowired
//    private ApiIBSyncStatusService apiIBSyncStatusService;
//
//
//    /**
//     * 调用API接口，同步入库信息
//     */
//    @SysLogApiAnnotion("入库单数据同步")
//    @PostMapping("/execute")
//    @ResponseBody
//    public void execute(@RequestBody ApiBInAsyncVo asyncVo) {
//        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
//            return;
//        }
//        for (BInVo vo : asyncVo.getBeans()) {
//            log.debug("=============同步入库单信息start=========");
//            BInVo bInVo = ibInService.selectById(vo.getId());
//
//            // extra_code为空或者状态为作废审核中的不同步
//            if(bInVo.getExtra_code() == null || Objects.equals(bInVo.getStatus(), DictConstant.DICT_B_IN_STATUS_SAVED) || Objects.equals(bInVo.getStatus(), DictConstant.DICT_B_IN_STATUS_CANCEL_BEING_AUDITED)){
//                continue;
//            }
//            ApiInPlanIdCodeVo apiInPlanIdCodeBo = new ApiInPlanIdCodeVo();
//            apiInPlanIdCodeBo.setPlan_code(bInVo.getPlan_code());
//            apiInPlanIdCodeBo.setPlan_id(bInVo.getPlan_id());
//            apiInPlanIdCodeBo.setIn_code(bInVo.getCode());
//            apiInPlanIdCodeBo.setIn_id(bInVo.getId());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<JSONObject> response = restTemplate.postForEntity(url, apiInPlanIdCodeBo, JSONObject.class);
//            log.debug("=============同步入库单信息result============="+response.getBody());
//
//            log.debug("=============同步入库单信息end=============");
//        }
//    }
//
//
//}
