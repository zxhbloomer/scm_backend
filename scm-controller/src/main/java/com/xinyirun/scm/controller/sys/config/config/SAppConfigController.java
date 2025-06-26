package com.xinyirun.scm.controller.sys.config.config;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * app配置表 前端控制器
 * </p>
 *
 * @author xyr
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "app配置")
@RestController
@RequestMapping(value = "/api/v1/appconfig")
public class SAppConfigController extends SystemBaseController {

    @Autowired
    private ISAppConfigService service;

    @SysLogAnnotion("根据查询条件，获取系统参数信息")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SAppConfigVo>>> list(@RequestBody(required = false) SAppConfigVo searchCondition) {
        List<SAppConfigVo> vos = service.getListData(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vos));
    }
}
