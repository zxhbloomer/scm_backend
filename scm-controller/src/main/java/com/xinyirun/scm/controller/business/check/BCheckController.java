package com.xinyirun.scm.controller.business.check;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.service.business.check.IBCheckDetailService;
import com.xinyirun.scm.core.system.service.business.check.IBCheckService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 盘点 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Slf4j
// @Api(tags = "盘点单")
@RestController
@RequestMapping(value = "/api/v1/check")
public class BCheckController extends SystemBaseController {

    @Autowired
    private IBCheckService ibCheckService;

    @Autowired
    private IBCheckDetailService ibCheckDetailService;

    @SysLogAnnotion("获取盘点单分页集合信息")
    // @ApiOperation(value = "获取盘点单分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BCheckVo>>> list(@RequestBody(required = false) BCheckVo searchCondition) {
        IPage<BCheckVo> list = ibCheckService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("新增盘点单")
    // @ApiOperation(value = "新增盘点单")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> insert(@RequestBody(required = false) BCheckVo bean) {
        ibCheckService.insert(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("编辑盘点单")
    // @ApiOperation(value = "编辑盘点单")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> save(@RequestBody(required = false) BCheckVo bean) {
        ibCheckService.update(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
    // @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BCheckVo> searchConditionList) {
        ibCheckService.audit(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据作废部分数据")
    // @ApiOperation(value = "根据选择的数据作废部分数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BCheckVo> searchConditionList) {
        ibCheckService.cancel(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
