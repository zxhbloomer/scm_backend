package com.xinyirun.scm.controller.sys.server;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.server.ServerInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 * 
 * @author xinyirun
 */
@RestController
@RequestMapping(value = "/api/v1/monitor/server")
@Slf4j
public class SServerController extends SystemBaseController {

    @SysLogAnnotion("获取系统信息")
    @PostMapping("/info")
    @ResponseBody
    public ResponseEntity<JsonResultAo<ServerInfoVo>> getInfo() throws Exception {
        ServerInfoVo server = new ServerInfoVo();
        server.copyTo();
        return ResponseEntity.ok().body(ResultUtil.OK(server));
    }
}
