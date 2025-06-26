package com.xinyirun.scm.api.controller.master.goods;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecPropVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiGoodsSpecPropService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 同步商品
 * </p>
 *
 * @author wwl
 * @since 2022-01-27
 */
@Slf4j
// @Api(tags = "同步商品属性")
@RestController
@RequestMapping(value = "/api/service/v1/goodsprop")
public class ApiGoodsSpecPropController extends SystemBaseController {

    @Autowired
    private ApiGoodsSpecPropService service;

    @SysLogApiAnnotion("20、同步商品属性")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiGoodsSpecPropVo> vo){
        service.syncAll(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
