package com.xinyirun.scm.api.controller.master.goods;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiCategoryVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiCategoryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 同步类别
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Slf4j
// @Api(tags = "同步类别")
@RestController
@RequestMapping(value = "/api/service/v1/category")
public class ApiCategoryController extends SystemBaseController {

    @Autowired
    private ApiCategoryService service;

    @SysLogApiAnnotion("16、同步类别")
    // @ApiOperation(value = "16、同步类别")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiCategoryVo> vo){
        service.syncAll(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
