package com.xinyirun.scm.app.controller.bpm;


import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppResultUtil;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppBBpmProcessJson;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceProgressVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.core.app.service.master.user.AppIMStaffService;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import com.xinyirun.scm.core.bpm.service.business.IBpmTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * process_templates 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@RestController
@RequestMapping("/api/app/v1/bpm/process")
public class AppBpmProcessTemplatesController {


    @Autowired
    private IBpmProcessTemplatesService service;

    @Autowired
    private IBpmTodoService bpmTodoService;

    @Autowired
    private AppIMStaffService appIMStaffService;

    @SysLogAppAnnotion("获取审批流程模型数据")
    @PostMapping("/get_json_app")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppBBpmProcessJson>> getAppProcessJson(@RequestBody(required = false) BBpmProcessVo param) {
        AppBBpmProcessJson rtn = service.getAppProcessModel(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(rtn));
    }

    @SysLogAppAnnotion("获取审批流程模型数据")
    @PostMapping("/get_process_node_by_staffid")
    @ResponseBody
    public ResponseEntity<AppJsonResultAo<AppStaffUserBpmInfoVo>> getProcessNodeByStaffid(@RequestBody(required = false) AppStaffUserBpmInfoVo param) {
        AppStaffUserBpmInfoVo rtn = appIMStaffService.getBpmDataByStaffid(param);
        return ResponseEntity.ok().body(AppResultUtil.OK(rtn));
    }

    @SysLogAnnotion("通过流程实例id查看流程图业务数据")
    @PostMapping("/getinstanceprogressapp")
    public ResponseEntity<JsonResultAo<BBpmInstanceProgressVo>> getInstanceProgressapp(@RequestBody BBpmInstanceProgressVo param) {
        return ResponseEntity.ok().body(ResultUtil.OK(bpmTodoService.getInstanceProgressapp(param)));
    }

}
