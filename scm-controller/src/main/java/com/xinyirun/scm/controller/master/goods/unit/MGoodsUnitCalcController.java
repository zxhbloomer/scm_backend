package com.xinyirun.scm.controller.master.goods.unit;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMGoodsUnitCalcService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "物料单位换算")
@RestController
@RequestMapping(value = "/api/v1/goods/unit/calc")
public class MGoodsUnitCalcController extends SystemBaseController {

    @Autowired
    private IMGoodsUnitCalcService service;

    @SysLogAnnotion("根据查询条件，获取单位换算列表")
    // @ApiOperation(value = "根据参数获取单位换算列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGoodsUnitCalcVo>>> pagelist(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        IPage<MGoodsUnitCalcVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取单位换算列表")
    // @ApiOperation(value = "根据参数获取单位换算列表")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MGoodsUnitCalcVo>>> list(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        List<MGoodsUnitCalcVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取单位换算一条数据")
    @PostMapping("/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> count(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        Integer _int = service.getCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(_int));
    }

    @SysLogAnnotion("根据查询条件，获取单位换算一条数据")
    @PostMapping("/one")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MGoodsUnitCalcVo>> getOne(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        MGoodsUnitCalcVo vo = service.selectOne(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取单位换算列表")
    // @ApiOperation(value = "根据参数获取单位换算列表")
    @PostMapping("/get/unused/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MUnitVo>>> selectUnusedUnitsList(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        List<MUnitVo> list = service.selectUnusedUnitsList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("单位换算数据更新保存")
    // @ApiOperation(value = "单位换算数据更新保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGoodsUnitCalcVo>> insert(@RequestBody(required = false) MGoodsUnitCalcVo vo) {
        InsertResultAo<MGoodsUnitCalcVo> execute = service.insert(vo);
        if(execute.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(execute.getData()));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("单位换算数据更新保存")
    // @ApiOperation(value = "单位换算数据更新保存")
    @PostMapping("/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGoodsUnitCalcVo>> update(@RequestBody(required = false) MGoodsUnitCalcVo vo) {
        UpdateResultAo<MGoodsUnitCalcVo> execute = service.update(vo);
        if(execute.isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(execute.getData()));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("单位换算数据删除")
    // @ApiOperation(value = "单位换算数据更新保存")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MGoodsUnitCalcVo vo) {
        service.delete(vo);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据id获取详情单条数据")
    @PostMapping("/detail")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MGoodsUnitCalcVo>> detail(@RequestBody(required = false) MGoodsUnitCalcVo searchCondition) {
        MGoodsUnitCalcVo vo = service.detail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

}
