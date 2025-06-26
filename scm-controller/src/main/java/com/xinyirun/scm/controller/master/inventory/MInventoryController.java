package com.xinyirun.scm.controller.master.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventorySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 库存表 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "库存")
@RestController
@RequestMapping(value = "/api/v1/inventory")
public class MInventoryController extends SystemBaseController {

    @Autowired
    private IMInventoryService service;

    @SysLogAnnotion("根据查询条件，获取库存信息")
    // @ApiOperation(value = "根据参数获取库存信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryVo>>> list(@RequestBody(required = false) MInventoryVo searchCondition) {
        IPage<MInventoryVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库存信息-按货主")
    // @ApiOperation(value = "根据查询条件，获取库存信息-按货主")
    @PostMapping("/owner/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryVo>>> listByOwner(@RequestBody(required = false) MInventoryVo searchCondition) {
        IPage<MInventoryVo> list = service.selectPageByOwner(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库存合计信息-按货主")
    // @ApiOperation(value = "根据查询条件，获取库存信息-按货主")
    @PostMapping("/owner/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventorySumVo>> listByOwnerSum(@RequestBody(required = false) MInventoryVo searchCondition) {
        MInventorySumVo vo = service.selectSumByOwner(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取库存信息-按货主规格")
    // @ApiOperation(value = "根据查询条件，获取库存信息-按货主规格")
    @PostMapping("owner/spec/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryVo>>> listByOwnerSpec(@RequestBody(required = false) MInventoryVo searchCondition) {
        IPage<MInventoryVo> list = service.selectPageByOwnerSpec(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库存合计信息-按货主规格")
    // @ApiOperation(value = "根据查询条件，获取库存信息-按货主规格")
    @PostMapping("owner/spec/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventorySumVo>> listByOwnerSpecSum(@RequestBody(required = false) MInventoryVo searchCondition) {
        MInventorySumVo vo = service.selectPageByOwnerSpecSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取库存信息")
    // @ApiOperation(value = "根据参数获取库存信息")
    @PostMapping("/alllist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MInventoryVo>>> allList(@RequestBody(required = false) MInventoryVo searchCondition) {
        List<MInventoryVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取库存信息")
    // @ApiOperation(value = "根据参数获取库存信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventoryVo>> getInventoryInfo(@RequestBody(required = false) MInventoryVo searchCondition) {
        MInventoryVo vo = service.getInventoryInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }


}
