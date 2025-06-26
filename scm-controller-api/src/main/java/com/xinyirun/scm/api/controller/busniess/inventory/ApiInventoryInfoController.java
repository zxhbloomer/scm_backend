package com.xinyirun.scm.api.controller.busniess.inventory;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.inventory.ApiInventoryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 分页查询库存
 * </p>
 *
 * @author htt
 * @since 2021-10-30
 */
@Slf4j
// @Api(tags = "分页查询库存")
@RestController
@RequestMapping(value = "/api/service/v1/inventory")
public class ApiInventoryInfoController extends SystemBaseController {

    @Autowired
    private ApiInventoryService service;

    @SysLogApiAnnotion("11、分页查询库存")
    // @ApiOperation(value = "11、分页查询库存")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<List<ApiInventoryVo>>> getInventory(@RequestBody ApiInventoryVo vo){
        List<ApiInventoryVo> list = service.getInventory(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(list));
    }

}
