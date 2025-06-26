package com.xinyirun.scm.bpm.controller.business;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * process_templates 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/bpm/process")
public class BpmProcessTemplatesController {

    @Autowired
    private IBpmProcessTemplatesService service;

    @SysLogAnnotion("根据查询条件，查询流程定义列表")
    @PostMapping("/page_list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BBpmProcessVo>>> list(@RequestBody(required = false) BBpmProcessVo param) {
        IPage<BBpmProcessVo> list = service.selectPage(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取详情")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BBpmProcessVo>> get(@RequestBody(required = false) BBpmProcessVo param) {
        BBpmProcessVo vo = service.selectById(param.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取分组")
    @GetMapping("/getgroup")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BBpmGroupVo>>> getGroup() {
        List<BBpmGroupVo> vo = service.getGroup();
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("获取审批流程数据")
    @PostMapping("/get_json")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BBpmProcessVo>> getProcessJson(@RequestBody(required = false) BBpmProcessVo param) {
        BBpmProcessVo vo = service.getBpmFlow(param);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

//    @SysLogAnnotion("启动审批流程")
    @PostMapping("/createstartprocess")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Boolean>> createStartProcess(@RequestBody(required = false) BBpmProcessVo param) {
        service.startProcess(param);
        return ResponseEntity.ok().body(ResultUtil.OK(true));
    }


    @SysLogAnnotion("模板发布")
    @PostMapping("/deploy")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BBpmProcessVo>> deploy(@RequestBody(required = false) BBpmProcessVo param) {
        UpdateResultAo<BBpmProcessVo> vo = service.deployBom(param);
        return ResponseEntity.ok().body(ResultUtil.OK(vo.getData()));
    }

    @SysLogAnnotion("流程管理-获取流程模型数据")
    @PostMapping("/get_bpm_data")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BBpmProcessVo>>> getBpmDataByPageCode(@RequestBody(required = false) BBpmProcessVo param) {
        List<BBpmProcessVo> vo = service.getBpmDataByPageCode(param);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }
}
