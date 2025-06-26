package com.xinyirun.scm.app.controller.bpm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceProgressVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmTodoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.bpm.service.business.IBpmTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *
 * @since 2024-10-11
 */
@RestController
@RequestMapping("/api/app/v1/bpm/todo")
public class AppBpmTodoController {

    @Autowired
    private IBpmTodoService service;

    @Autowired
    private IBpmTodoService bpmTodoService;


    @SysLogAppAnnotion("查看我的代办，我的已办")
    @PostMapping("/list")
    public ResponseEntity<AppJsonResultAo<IPage<BBpmTodoVo>>> selectPageList(@RequestBody BBpmTodoVo param){
        IPage<BBpmTodoVo> list = service.selectPageList(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(list));
    }

//    @SysLogAppAnnotion("通过流程实例id查看详情")
//    @PostMapping("/instanceInfo")
//    public ResponseEntity<AppJsonResultAo<HandleDataVO>> instanceInfo(@RequestBody BBpmTodoVo param) {
//        return ResponseEntity.ok().body(AppResultUtil.OK(service.instanceInfo(param)));
//    }

    @SysLogAppAnnotion("任务审批同意")
    @PostMapping("/agree")
    public ResponseEntity<AppJsonResultAo<Boolean>> agree(@RequestBody BBpmTodoVo param) {
        service.agree(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }


    @SysLogAppAnnotion("通过流程实例id查看流程图业务数据")
    @PostMapping("/getinstanceprogress")
    public ResponseEntity<AppJsonResultAo<BBpmInstanceProgressVo>> getInstanceProgress(@RequestBody BBpmInstanceProgressVo param) {
        return ResponseEntity.ok().body(AppResultUtil.OK(service.getInstanceProgress(param)));
    }


    @SysLogAppAnnotion("任务审批拒绝")
    @PostMapping("/refuse")
    public ResponseEntity<AppJsonResultAo<Boolean>> refuse(@RequestBody BBpmTodoVo param) {
        service.refuse(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }

    @SysLogAppAnnotion("任务审批撤销")
    @PostMapping("/cancel")
    public ResponseEntity<AppJsonResultAo<Boolean>> cancel(@RequestBody BBpmTodoVo param) throws Exception {
        service.cancel(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }


    @SysLogAppAnnotion("任务审批转办")
    @PostMapping("/transfer")
    public ResponseEntity<AppJsonResultAo<Boolean>> transfer(@RequestBody BBpmTodoVo param) {
        service.transfer(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }


    /**
     * 退回
     */

    @SysLogAppAnnotion("任务审批后加签")
    @PostMapping("/afterAdd")
    public ResponseEntity<AppJsonResultAo<Boolean>> afterAdd(@RequestBody BBpmTodoVo param) {
        service.afterAdd(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }

    /**
     * 查到签上的人
     */

    /**
     * 减签
     */

    /**
     * 更新评论
     */
    @SysLogAppAnnotion("任务评论")
    @PostMapping("/comments")
    public ResponseEntity<AppJsonResultAo<Boolean>> updateComments(@RequestBody BBpmTodoVo param) {
        service.updateComments(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(true));
    }

    @SysLogAppAnnotion("通过流程实例id查看流程图业务数据")
    @PostMapping("/getinstanceprogressapp")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<BBpmInstanceProgressVo>> getInstanceProgressapp(@RequestBody BBpmInstanceProgressVo param) {
        return ResponseEntity.ok().body(AppResultUtil.OK(bpmTodoService.getInstanceProgressapp(param)));
    }

}
