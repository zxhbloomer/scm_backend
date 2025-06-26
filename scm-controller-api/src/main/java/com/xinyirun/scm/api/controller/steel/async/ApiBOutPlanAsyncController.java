//package com.xinyirun.scm.api.controller.steel.async;
//
//
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutPlanAsyncVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiOutPlanIdCodeVo;
//import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanDetailVo;
//import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInService;
//import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
//import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
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
//@RequestMapping(value = "/api/service/v1/steel/async/outplan")
//public class ApiBOutPlanAsyncController extends ApiBaseController {
//
//    @Autowired
//    private IBOutPlanDetailService outPlanDetailService;
//
//    @Autowired
//    private IBInService ibInService;
//
//    @Autowired
//    private ISAppConfigDetailService isAppConfigDetailService;
//
//
//    /**
//     * 调用API接口，同步入库信息
//     */
//    @SysLogApiAnnotion("出库计划数据同步")
//    @PostMapping("/execute")
//    @ResponseBody
//    public void execute(@RequestBody ApiBOutPlanAsyncVo asyncVo) {
//        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
//            return;
//        }
//        for (BOutPlanListVo vo : asyncVo.getBeans()) {
//            log.debug("=============同步出库计划信息start=============");
//            BOutPlanDetailVo data = outPlanDetailService.selectById(vo.getId());
//            // extra_code为空或者状态为作废审核中的不同步
//            if(data.getExtra_code() == null || DictConstant.DICT_B_OUT_PLAN_STATUS_CANCEL_BEING_AUDITED.equals(data.getStatus())){
//                continue;
//            }
//            ApiOutPlanIdCodeVo apiOutPlanIdCodeBo = new ApiOutPlanIdCodeVo();
//            apiOutPlanIdCodeBo.setPlan_code(data.getPlan_code());
//            apiOutPlanIdCodeBo.setPlan_id(data.getPlan_id());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<ApiOutPlanIdCodeVo> response = restTemplate.postForEntity(url, apiOutPlanIdCodeBo, ApiOutPlanIdCodeVo.class);
//            System.out.println(response.getBody());
//            log.debug("=============同步出库计划信息end=============");
//        }
//    }
//
//
//}
