package com.xinyirun.scm.bpm.controller.business;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceProgressVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmTodoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.bpm.service.business.IBpmTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * @since 2024-10-11
 */
@RestController
@RequestMapping("/api/v1/bpm/todo")
public class BpmTodoController {

    @Autowired
    private IBpmTodoService service;


    @SysLogAnnotion("查看我的待办")
    @PostMapping("/list")
    public ResponseEntity<JsonResultAo<IPage<BBpmTodoVo>>> selectPageList(@RequestBody BBpmTodoVo param){
        IPage<BBpmTodoVo> list = service.selectPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查看我的待办，10条数据")
    @PostMapping("/list/ten")
    public ResponseEntity<JsonResultAo<List<BBpmTodoVo>>> getListTen(@RequestBody(required = false) BBpmTodoVo param){
        List<BBpmTodoVo> list = service.getListTen(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("根据查询条件，获取待办数据条数")
    @PostMapping("/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> selectTodoCount(@RequestBody(required = false) BBpmTodoVo searchCondition) {
        Integer count = service.selectTodoCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(count));
    }
//    @SysLogAnnotion("通过流程实例id查看详情")
//    @PostMapping("/instanceInfo")
//    public ResponseEntity<JsonResultAo<HandleDataVO>> instanceInfo(@RequestBody BBpmTodoVo param) {
//        return ResponseEntity.ok().body(ResultUtil.OK(service.instanceInfo(param)));
//    }

    @SysLogAnnotion("任务审批同意")
    @PostMapping("/agree")
    public ResponseEntity<JsonResultAo<Boolean>> agree(@RequestBody BBpmTodoVo param) {
        service.agree(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }


    @SysLogAnnotion("通过流程实例id查看流程图业务数据")
    @PostMapping("/getinstanceprogress")
    public ResponseEntity<JsonResultAo<BBpmInstanceProgressVo>> getInstanceProgress(@RequestBody BBpmInstanceProgressVo param) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.getInstanceProgress(param)));
    }


    @SysLogAnnotion("任务审批拒绝")
    @PostMapping("/refuse")
    public ResponseEntity<JsonResultAo<Boolean>> refuse(@RequestBody BBpmTodoVo param) {
        service.refuse(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    @SysLogAnnotion("任务审批撤销")
    @PostMapping("/cancel")
    public ResponseEntity<JsonResultAo<Boolean>> cancel(@RequestBody BBpmTodoVo param) throws Exception {
        service.cancel(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }


    @SysLogAnnotion("任务审批转办")
    @PostMapping("/transfer")
    public ResponseEntity<JsonResultAo<Boolean>> transfer(@RequestBody BBpmTodoVo param) {
        service.transfer(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }


    /**
     * 退回
     */

    @SysLogAnnotion("任务审批后加签")
    @PostMapping("/afterAdd")
    public ResponseEntity<JsonResultAo<Boolean>> afterAdd(@RequestBody BBpmTodoVo param) {
        service.afterAdd(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

    /**
     * 查到签上的人
     */

    /**
     * 减签
     */

    /**
     * 评论
     */
    @SysLogAnnotion("任务评论")
    @PostMapping("/comments")
    public ResponseEntity<JsonResultAo<Boolean>> updateComments(@RequestBody BBpmTodoVo param) {
        service.updateComments(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }

}
