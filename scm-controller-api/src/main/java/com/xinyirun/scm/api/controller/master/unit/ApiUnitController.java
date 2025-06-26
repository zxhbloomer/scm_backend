package com.xinyirun.scm.api.controller.master.unit;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.unit.ApiUnitVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.unit.ApiUnitService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 同步单位
 * </p>
 *
 */
@Slf4j
// @Api(tags = "同步单位")
@RestController
@RequestMapping(value = "/api/service/v1/unit")
public class ApiUnitController extends SystemBaseController {

    @Autowired
    private ApiUnitService service;

    @SysLogApiAnnotion("19、同步单位")
    // @ApiOperation(value = "19、同步单位")
    @PostMapping("/sync/all")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiUnitVo> vo){
        service.syncAll(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
