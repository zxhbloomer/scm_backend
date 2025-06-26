package com.xinyirun.scm.api.controller.master.goods;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiBusinessTypeVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiBusinessTypeService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 同步板块
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Slf4j
// @Api(tags = "同步板块")
@RestController
@RequestMapping(value = "/api/service/v1/businesstype")
public class ApiBusinessTypeController extends SystemBaseController {

    @Autowired
    private ApiBusinessTypeService service;

    @SysLogApiAnnotion("14、同步板块")
    // @ApiOperation(value = "14、同步板块")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiBusinessTypeVo> vo){
        service.syncAll(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }


}
