package com.xinyirun.scm.controller.business.warehouse;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 仓库组一级分类 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@RestController
@RequestMapping("/api/v1/warehouse/group")
public class BWarehouseGroupController {

    @Autowired
    private IBWarehouseGroupService service;

    @SysLogAnnotion("根据查询条件，获取仓库组信息")
    // @ApiOperation(value = "根据参数获取库区数信息")
    @PostMapping("/page")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGroupVo>>> page(@RequestBody(required = false) BWarehouseGroupVo searchCondition) {
        IPage<BWarehouseGroupVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库组信息")
    // @ApiOperation(value = "根据参数获取库区数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BWarehouseGroupVo>>> list(@RequestBody(required = false) BWarehouseGroupVo searchCondition) {
        List<BWarehouseGroupVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("仓库组数据更新保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWarehouseGroupVo>> insert(@RequestBody(required = false) BWarehouseGroupVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("仓库组据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWarehouseGroupVo>> save(@RequestBody(required = false) BWarehouseGroupVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("仓库组数据更新保存")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) BWarehouseGroupVo bean) {
        service.delete(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
    }

    @SysLogAnnotion("仓库组1据更新保存")
    @PostMapping("/id")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BWarehouseGroupVo>> getDataByIdApi(@RequestBody(required = false) BWarehouseGroupVo bean) {
        BWarehouseGroupVo rtn = service.selectById(bean.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }
}
