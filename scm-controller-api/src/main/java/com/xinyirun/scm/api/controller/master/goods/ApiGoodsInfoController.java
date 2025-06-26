package com.xinyirun.scm.api.controller.master.goods;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiGoodsSpecService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 同步商品规格
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Slf4j
// @Api(tags = "同步商品规格")
@RestController
@RequestMapping(value = "/api/service/v1/goods")
public class ApiGoodsInfoController extends SystemBaseController {

    @Autowired
    private ApiGoodsSpecService service;

    @SysLogApiAnnotion("18、首次同步所有商品规格数据")
    // @ApiOperation(value = "18、首次同步所有商品规格数据")
    @PostMapping("/sync/all")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiGoodsSpecVo> vo){
        service.syncAll(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    @SysLogApiAnnotion("18、新增同步商品规格数据")
    // @ApiOperation(value = "18、新增同步商品规格数据")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncNewOnly(@RequestBody List<ApiGoodsSpecVo> vo){
        service.syncNewOnly(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

    @SysLogApiAnnotion("18、修改同步商品规格数据")
    // @ApiOperation(value = "18、修改同步商品规格数据")
    @PostMapping("/sync/update")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncUpdateOnly(@RequestBody List<ApiGoodsSpecVo> vo){
        service.syncUpdateOnly(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }

}
