package com.xinyirun.scm.controller.business.monitor;


import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000068Vo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000158Vo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorFileExportSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 监管任务文件导出 配置 前端控制器
 *
 * @author xinyirun
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/api/v1/monitor_file")
public class BMonitorFileExportSettingsController {

    @Autowired
    private IBMonitorFileExportSettingsService service;

    @SysLogAnnotion("监管任务 附件导出 配置 查询")
    @GetMapping("get")
    public ResponseEntity<JsonResultAo<P00000068Vo>> getByStaffId() {
        P00000068Vo p68 = service.getByStaffId();
        return ResponseEntity.ok().body(ResultUtil.OK(p68));
    }

    @SysLogAnnotion("监管任务 附件导出 配置 保存")
    @PostMapping("save")
    public ResponseEntity<JsonResultAo<String>> save(@RequestBody JSONObject jsonObject) {
        service.saveAndFlush(jsonObject);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


    @SysLogAnnotion("监管任务(直销直采) 附件导出 配置 查询")
    @GetMapping("getByDirect")
    public ResponseEntity<JsonResultAo<P00000158Vo>> getByDirectStaffId() {
        P00000158Vo p158 = service.getByDirectStaffId();
        return ResponseEntity.ok().body(ResultUtil.OK(p158));
    }

    @SysLogAnnotion("监管任务 附件导出 配置 保存")
    @PostMapping("save_direct")
    public ResponseEntity<JsonResultAo<String>> saveDirect(@RequestBody JSONObject jsonObject) {
        service.saveDirect(jsonObject);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }


}
