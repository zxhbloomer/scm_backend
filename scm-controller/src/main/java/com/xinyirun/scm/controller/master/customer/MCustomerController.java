package com.xinyirun.scm.controller.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.excel.customer.MCustomerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  客户 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "客户")
@RestController
@RequestMapping(value = "/api/v1/customer")
public class MCustomerController extends SystemBaseController {

    @Autowired
    private IMCustomerService service;

    @SysLogAnnotion("根据查询条件，获取客户列表")
    // @ApiOperation(value = "根据参数获取客户列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MCustomerVo>>> pageList(@RequestBody(required = false) MCustomerVo searchCondition) {
        IPage<MCustomerVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取客户信息")
    // @ApiOperation(value = "根据参数获取客户数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MCustomerVo>>> list(@RequestBody(required = false) MCustomerVo searchCondition) {
        List<MCustomerVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("客户数据更新保存")
    // @ApiOperation(value = "根据参数id，获取客户信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCustomerVo>> insert(@RequestBody(required = false) MCustomerVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存失败。");
        }
    }

    @SysLogAnnotion("客户数据更新保存")
    // @ApiOperation(value = "根据参数id，获取客户信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MCustomerVo>> save(@RequestBody(required = false) MCustomerVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据选择的数据启用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MCustomerVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MCustomerVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MCustomerVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MCustomerVo searchConditionList, HttpServletResponse response) throws IOException {
        List<MCustomerExcelVo> list = service.export(searchConditionList);
        new EasyExcelUtil<>(MCustomerExcelVo.class).exportExcel("客户管理"  + DateTimeUtil.getDate(),"客户管理",list, response);
    }


}
