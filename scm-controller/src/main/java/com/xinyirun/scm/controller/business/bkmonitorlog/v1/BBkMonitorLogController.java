package com.xinyirun.scm.controller.business.bkmonitorlog.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorLogDetailService;
import com.xinyirun.scm.core.system.service.business.bkmonitor.v1.IBBkMonitorSyncLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/4/4 15:31
 */

@RestController
@RequestMapping("/api/v1/backup/log")
public class BBkMonitorLogController {

    @Autowired
    private IBBkMonitorLogDetailService service;

    @Autowired
    private IBBkMonitorSyncLogService syncLogService;

    @PostMapping("page_list")
    @SysLogAnnotion("同步日志分页查询")
    public ResponseEntity<JsonResultAo<IPage<BBkMonitorLogDetailVo>>> list(@RequestBody(required = false) BBkMonitorLogDetailVo param) {
//        IPage<BBkMonitorLogDetailVo> list = service.selectPage(param);
        IPage<BBkMonitorLogDetailVo> list = syncLogService.selectPage(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


}
