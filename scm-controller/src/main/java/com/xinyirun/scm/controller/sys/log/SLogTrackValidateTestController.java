//package com.xinyirun.scm.controller.sys.log;
//
////import com.xinyirun.scm.bean.app.vo.master.vehicle.AppMVehicleVo;
//import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
//import com.xinyirun.scm.bean.system.vo.business.track.BVehicleValidateVo;
//import com.xinyirun.scm.bean.system.vo.sys.log.STrackValidateTestVo;
//import com.xinyirun.scm.common.annotations.SysLogAnnotion;
//import com.xinyirun.scm.common.exception.system.BusinessException;
//import com.xinyirun.scm.core.system.service.track.bestfriend.IBTrackBestFriendService;
//import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackGsh56Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///**
// * @Author: Wqf
// * @Description: 轨迹、验车 测试
// * @CreateTime : 2023/11/7 14:51
// */
//
//@RestController
//@RequestMapping(value = "/api/v1/trackvalidate/log")
//public class SLogTrackValidateTestController {
//
//    @Autowired
//    private IBTrackBestFriendService bestFriendService;
//
//    @Autowired
//    private IBTrackGsh56Service ibTrackGsh56Service;
//
//
////    @SysLogAnnotion("查询验车信息")
////    @PostMapping("/validate")
////    @ResponseBody
////    public ResponseEntity<JsonResultAo<String>> validate(@RequestBody(required = false) STrackValidateTestVo param)  {
////        String result = "";
////        if ("1".equals(param.getService_type())) {
////            // teng hao
////            AppMVehicleVo appMVehicleVo = new AppMVehicleVo();
////            appMVehicleVo.setNo(param.getVehicle_no());
////            appMVehicleVo.setNo_color(param.getVehicle_color());
////            BVehicleValidateVo bVehicleValidateVo = ibTrackGsh56Service.checkVehicleExist(appMVehicleVo);
////            result = bVehicleValidateVo.getValidate_log();
////        } else if ("2".equals(param.getService_type())) {
////            // 好伙伴
////            result = bestFriendService.getValidateResult(param.getVehicle_no());
////
////        }
////        return ResponseEntity.ok().body(ResultUtil.OK(result));
////    }
//
//    @SysLogAnnotion("查询轨迹信息")
//    @PostMapping("/track")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> getTrack(@RequestBody(required = false) STrackValidateTestVo param)  {
//        if (param.getStart_date() == null || param.getEnd_date() == null) {
//            throw new BusinessException("日期不能为空");
//        }
//        String result = "";
//        if ("1".equals(param.getService_type())) {
//            // teng hao
//            result = ibTrackGsh56Service.getTrackMsg(param);
//        } else if ("2".equals(param.getService_type())) {
//            // 好伙伴
//            result = bestFriendService.getTrackMsg(param);
//
//        }
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
//    }
//
//}
