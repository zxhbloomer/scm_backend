//package com.xinyirun.scm.api.controller.steel.async;
//
//
//import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
//import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutAsyncVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
//import com.xinyirun.scm.bean.api.vo.sync.ApiOutPlanIdCodeVo;
//import com.xinyirun.scm.bean.system.vo.business.out.BOutVo;
//import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
//import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
//import com.xinyirun.scm.common.constant.DictConstant;
//import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.core.system.mapper.business.returnrelation.BReturnRelationMapper;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInPlanDetailService;
//import com.xinyirun.scm.core.system.service.business.in.v1.IBInService;
//import com.xinyirun.scm.core.system.service.business.out.IBOutService;
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
//@RequestMapping(value = "/api/service/v1/steel/async/out")
//public class ApiBOutAsyncController extends ApiBaseController {
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
//    private IBOutService ibOutService;
//
//    @Autowired
//    private BReturnRelationMapper bReturnRelationMapper;
//
//    /**
//     * 调用API接口，同步入库信息
//     */
//    @SysLogApiAnnotion("出库单数据同步")
//    @PostMapping("/execute")
//    @ResponseBody
//    public void execute(@RequestBody ApiBOutAsyncVo asyncVo) {
//        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
//            return;
//        }
//        for (BOutVo vo : asyncVo.getBeans()) {
//            log.debug("=============同步出库单信息start=============");
//            BOutVo bOutVo = ibOutService.selectById(vo.getId());
//            // extra_code为空或者状态为作废审核中或状态为制单的不同步
//            if(bOutVo.getExtra_code() == null || Objects.equals(bOutVo.getStatus(), DictConstant.DICT_B_OUT_STATUS_SAVED) || Objects.equals(bOutVo.getStatus(), DictConstant.DICT_B_OUT_STATUS_CANCEL_BEING_AUDITED)){
//                continue;
//            }
//
//            // 计算退货数量
//           /* BReturnRelationEntity returnRelationEntity = bReturnRelationMapper.selectByOutIdAndSerialType(vo.getId());
//            if (returnRelationEntity != null) {
//                bOutVo.setActual_count(bOutVo.getActual_count().subtract(returnRelationEntity.getQuantity()));
//                bOutVo.setActual_weight(bOutVo.getActual_weight().subtract(returnRelationEntity.getQuantity().multiply(bOutVo.getCalc())));
//            }*/
//
//            ApiOutPlanIdCodeVo apiOutPlanIdCodeBo = new ApiOutPlanIdCodeVo();
//            apiOutPlanIdCodeBo.setPlan_code(bOutVo.getPlan_code());
//            apiOutPlanIdCodeBo.setPlan_id(bOutVo.getPlan_id());
//            apiOutPlanIdCodeBo.setOut_code(bOutVo.getCode());
//            apiOutPlanIdCodeBo.setOut_id(bOutVo.getId());
//            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
//            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
//            ResponseEntity<ApiInPlanIdCodeVo> response = restTemplate.postForEntity(url, apiOutPlanIdCodeBo, ApiInPlanIdCodeVo.class);
//            log.debug("=============同步出库单信息返回============="+response.getBody());
//
//            log.debug("=============同步出库单信息end=============");
//        }
//    }
//
//
//}
