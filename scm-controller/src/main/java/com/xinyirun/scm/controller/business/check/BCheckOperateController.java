package com.xinyirun.scm.controller.business.check;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.business.check.IBCheckOperateService;
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
 * @since 2021-12-29
 */
@Slf4j
// @Api(tags = "盘点操作")
@RestController
@RequestMapping(value = "/api/v1/check/operate")
public class BCheckOperateController {

    @Autowired
    private IBCheckOperateService service;

    @SysLogAnnotion("根据查询条件，获取盘点操作单信息")
    // @ApiOperation(value = "根据参数获取盘点操作单信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BCheckOperateVo>>> list(@RequestBody(required = false) BCheckOperateVo searchCondition) {
        IPage<BCheckOperateVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("获取盘点操作单详情")
    // @ApiOperation(value = "获取盘点操作单详情")
    @PostMapping("/getDetailInfo")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BCheckOperateVo>> getDetailInfo(@RequestBody(required = false) BCheckOperateVo searchCondition) {
        BCheckOperateVo detail = service.selectDetail(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

    @SysLogAnnotion("盘点录入")
    // @ApiOperation(value = "盘点录入")
    @PostMapping("/update")
    @ResponseBody
        public ResponseEntity<JsonResultAo<String>> update(@RequestBody(required = false) BCheckOperateVo vo) {
        service.update(vo);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启动盘点")
    // @ApiOperation(value = "盘点启动")
    @PostMapping("/start")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> submit(@RequestBody(required = false) List<BCheckOperateVo> searchConditionList) {
        service.start(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据完成盘点")
    // @ApiOperation(value = "盘点完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> finish(@RequestBody(required = false) List<BCheckOperateVo> searchConditionList) {
        service.finish(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

}
