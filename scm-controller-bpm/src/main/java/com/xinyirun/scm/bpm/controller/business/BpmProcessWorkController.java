//package com.xinyirun.scm.bpm.controller.business;
//
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.xinyirun.scm.bean.bpm.dto.HandleDataDTO;
//import com.xinyirun.scm.bean.bpm.vo.HandleDataVO;
//import com.xinyirun.scm.bean.bpm.vo.HistoryProcessInstanceVO;
//import com.xinyirun.scm.bean.bpm.vo.TaskVO;
//import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
//import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
//import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
//import com.xinyirun.scm.common.annotations.SysLogAnnotion;
////import com.xinyirun.scm.core.bpm.service.business.IBpmProcessWorkService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * <p>
// * 审批中心 前端控制器
// * </p>
// *
// * @author xinyirun
// * @since 2024-10-11
// */
//@RestController
//@RequestMapping("/api/v1/bpm/process_work")
//public class BpmProcessWorkController {
//
//    @Autowired
//    private IBpmProcessWorkService service;
//
//    @SysLogAnnotion("查看我的待办")
//    @PostMapping("/toDoList")
//    public ResponseEntity<JsonResultAo<IPage<TaskVO>>> toDoList(@RequestBody BBpmProcessVo param){
//        IPage<TaskVO> list = service.toDoList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//
//    @SysLogAnnotion("查看我发起的流程")
//    @PostMapping("/applyList")
//    public ResponseEntity<JsonResultAo<IPage<HistoryProcessInstanceVO>>> applyList(@RequestBody BBpmProcessVo param){
//        IPage<HistoryProcessInstanceVO> list = service.applyList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//    @SysLogAnnotion("通过流程实例id查看详情")
//    @PostMapping("/instanceInfo")
//    public ResponseEntity<JsonResultAo<HandleDataVO>> instanceInfo(@RequestBody HandleDataDTO handleDataDTO) {
//        return ResponseEntity.ok().body(ResultUtil.OK(service.instanceInfo(handleDataDTO)));
//    }
//
//    @SysLogAnnotion("流程审批同意")
//    @PostMapping("/agree")
//    public ResponseEntity<JsonResultAo<Boolean>> agree(@RequestBody HandleDataDTO handleDataDTO) {
//        service.agree(handleDataDTO);
//        return ResponseEntity.ok().body(ResultUtil.OK(true));
//    }
//
//
//    @SysLogAnnotion("查看我的已办")
//    @PostMapping("/doneList")
//    public ResponseEntity<JsonResultAo<IPage<TaskVO>>> doneList(@RequestBody BBpmProcessVo param) {
//        IPage<TaskVO> list = service.doneList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//    @SysLogAnnotion("查看抄送我的")
//    @PostMapping("/ccList")
//    public ResponseEntity<JsonResultAo<IPage<TaskVO>>> ccList(@RequestBody BBpmProcessVo param) {
//        IPage<TaskVO> list = service.ccList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
//    }
//
//}
