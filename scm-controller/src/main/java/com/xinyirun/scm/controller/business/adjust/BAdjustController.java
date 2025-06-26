package com.xinyirun.scm.controller.business.adjust;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.common.annotations.DataChangeOperateAnnotation;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.ApiOperation;
// import io.swagger.annotations.Api;
// import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 库存调整 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Slf4j
// @Tag(name= "调整单")
@RestController
@RequestMapping(value = "/api/v1/adjust")
public class BAdjustController extends SystemBaseController {

    @Autowired
    private IBAdjustService service;

    @Autowired
    private IBAdjustDetailService adjustDetailService;

    @SysLogAnnotion("根据查询条件，获取调整单分页集合信息")
    // @ApiOperation(value = "根据参数获取调整单分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BAdjustVo>>> list(@RequestBody(required = false) BAdjustVo searchCondition) {
        IPage<BAdjustVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取调整单信息")
    // @ApiOperation(value = "根据参数获取调整单信息")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BAdjustVo>> get(@RequestBody(required = false) BAdjustVo vo) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.get(vo)));
    }

    @DataChangeOperateAnnotation(value = "新增库存调整单", page_name = "库存调整页面")
    @SysLogAnnotion("新增调整单")
    // @ApiOperation(value = "新增调整单")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<List<BAdjustVo>>> insert(@RequestBody(required = false) BAdjustVo bean) {
        if(adjustDetailService.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getAdjust_id()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("调整单数据更新保存")
    // @ApiOperation(value = "根据参数id，获取调整单信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<List<BAdjustVo>>> save(@RequestBody(required = false) BAdjustVo bean) {
        if(adjustDetailService.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getAdjust_id()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据提交，部分数据")
    // @ApiOperation(value = "根据参数id，提交数据")
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BAdjustVo> searchConditionList) {
        service.submit(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BAdjustVo> searchConditionList) {
        service.audit(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据删除，部分数据")
    // @ApiOperation(value = "根据参数id，删除数据")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<BAdjustVo> searchConditionList) {
        service.delete(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
