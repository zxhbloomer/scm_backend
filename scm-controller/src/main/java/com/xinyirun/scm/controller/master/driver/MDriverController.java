package com.xinyirun.scm.controller.master.driver;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverExportVo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.driver.IMDriverService;
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
 *  司机管理接口
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Slf4j
// @Api(tags = "司机管理接口")
@RestController
@RequestMapping(value = "/api/v1/driver")
public class MDriverController extends SystemBaseController {

    @Autowired
    private IMDriverService service;

    @SysLogAnnotion("根据查询条件，获取司机管理分页列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MDriverVo>>> pageList(@RequestBody(required = false) MDriverVo searchCondition) {
        IPage<MDriverVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取司机明细")
    @PostMapping("/get")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MDriverVo>> getDetail(@RequestBody(required = false) MDriverVo searchCondition) {
        MDriverVo list = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("司机数据新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MDriverVo>> insert(@RequestBody(required = false) MDriverVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("新增失败。");
        }
    }

    @SysLogAnnotion("司机数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MDriverVo>> save(@RequestBody(required = false) MDriverVo bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("删除司机")
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MDriverVo searchCondition) {
        service.delete(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MDriverVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MDriverVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MDriverVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("司机数据导出")
    @PostMapping("/export")
    public void exportSelection(@RequestBody(required = false) MDriverVo param, HttpServletResponse response) throws IOException {
        List<MDriverExportVo> rtnList = service.selectExportList(param);
        EasyExcelUtil<MDriverExportVo> util = new EasyExcelUtil<>(MDriverExportVo.class);
        util.exportExcel("司机" + DateTimeUtil.getDate(), "司机", rtnList, response);
    }
}
