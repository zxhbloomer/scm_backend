package com.xinyirun.scm.controller.sys.workbench;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.workbench.BpmMatterVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmNoticeVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmRemindVo;
import com.xinyirun.scm.bean.system.vo.workbench.SSysWorkbenchVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.workbench.ISSysWorkbenchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-17
 */
@RestController
@RequestMapping("/api/v1/workbench")
public class SSysWorkbenchController {

    @Autowired
    ISSysWorkbenchService sSysWorkbenchService;

    @SysLogAnnotion("获取工作台配置")
    @PostMapping("/info")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getInfo(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {

        SSysWorkbenchVo vo = sSysWorkbenchService.getInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-初始化")
    @PostMapping("/info/reset")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> resetInfo(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.resetInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-快捷操作配置")
    @PostMapping("/quick")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getQuickOperation(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.getQuickOperation(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-常用应用配置")
    @PostMapping("/offten")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getOfftenOperation(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.getOfftenOperation(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-保存")
    @PostMapping("/info/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> saveInfo(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.saveInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("工作台配置-快捷操作配置-保存")
    @PostMapping("/quick/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> saveQuick(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.saveQuick(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("工作台配置-常用应用配置-保存")
    @PostMapping("/offten/save")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> saveOfften(@RequestBody(required = false) SSysWorkbenchVo searchCondition) throws Exception {
        SSysWorkbenchVo vo = sSysWorkbenchService.saveOfften(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-待办事项")
    @PostMapping("/todo")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getTodo() throws Exception {
        SSysWorkbenchVo vo = new SSysWorkbenchVo();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

//    @SysLogAnnotion("获取工作台配置-待办超时提醒")
//    @PostMapping("/todo/overtime")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getTodoOverTime() throws Exception {
//        SSysWorkbenchVo vo = new SSysWorkbenchVo();
//        return ResponseEntity.ok().body(ResultUtil.OK(vo));
//    }

    @SysLogAnnotion("获取工作台配置-预警")
    @PostMapping("/alarm")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getAlarmData() throws Exception {
        SSysWorkbenchVo vo = new SSysWorkbenchVo();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取工作台配置-当月统计信息")
    @PostMapping("/current/month")
    @ResponseBody
    public ResponseEntity<JsonResultAo<SSysWorkbenchVo>> getCurrentByMonth() throws Exception {
        SSysWorkbenchVo vo = new SSysWorkbenchVo();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取审批事项")
    @PostMapping("/matter/info")
    public ResponseEntity<JsonResultAo<BpmMatterVo>> getMatterData(@RequestBody(required = false) BpmMatterVo param){
        BpmMatterVo vo = sSysWorkbenchService.getMatterData();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取待办超时提醒")
    @PostMapping("/remind/info")
    public ResponseEntity<JsonResultAo<BpmRemindVo>> getRemindData(@RequestBody(required = false) BpmRemindVo param){
        BpmRemindVo vo = sSysWorkbenchService.getRemindData();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取通知list")
    @PostMapping("/note/info")
    public ResponseEntity<JsonResultAo<BpmNoticeVo>> getNoticeList(@RequestBody(required = false) BpmRemindVo param){
        BpmNoticeVo vo = sSysWorkbenchService.getNoticeList();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }


}
