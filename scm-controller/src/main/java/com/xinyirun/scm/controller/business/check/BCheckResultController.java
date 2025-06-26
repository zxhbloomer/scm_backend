package com.xinyirun.scm.controller.business.check;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckResultVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.service.business.check.IBCheckResultDetailService;
import com.xinyirun.scm.core.system.service.business.check.IBCheckResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 盘盈盘亏 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Slf4j
//@Api(tags = "盘盈盘亏单")
@RestController
@RequestMapping(value = "/api/v1/check/result")
public class BCheckResultController {


    @Autowired
    private IBCheckResultService ibCheckResultService;

    @Autowired
    private IBCheckResultDetailService ibCheckResultDetailService;

    @SysLogAnnotion("获取盘盈盘亏单分页集合信息")
//    @ApiOperation(value = "获取盘盈盘亏单分页集合信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BCheckResultVo>>> list(@RequestBody(required = false) BCheckResultVo searchCondition) {
        IPage<BCheckResultVo> list = ibCheckResultService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("编辑盘盈盘亏单")
//    @ApiOperation(value = "编辑盘盈盘亏单")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> save(@RequestBody(required = false) BCheckResultVo bean) {
        ibCheckResultService.update(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据审核，部分数据")
//    @ApiOperation(value = "根据参数id，审核数据")
    @PostMapping("/audit")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> audit(@RequestBody(required = false) List<BCheckResultVo> searchConditionList) {
        ibCheckResultService.audit(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据作废部分数据")
//    @ApiOperation(value = "根据选择的数据作废部分数据")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> cancel(@RequestBody(required = false) List<BCheckResultVo> searchConditionList) {
        ibCheckResultService.cancel(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
