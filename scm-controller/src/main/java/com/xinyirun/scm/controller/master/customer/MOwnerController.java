package com.xinyirun.scm.controller.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerExportVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
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
 *  货主 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/owner")
public class MOwnerController extends SystemBaseController {

    @Autowired
    private IMOwnerService service;

    @SysLogAnnotion("根据查询条件，获取货主列表")
    // @ApiOperation(value = "根据参数获取货主列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MOwnerVo>>> pageList(@RequestBody(required = false) MOwnerVo searchCondition) {
        IPage<MOwnerVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取货主信息")
    // @ApiOperation(value = "根据参数获取货主数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MOwnerVo>>> list(@RequestBody(required = false) MOwnerVo searchCondition) {
        List<MOwnerVo> list = service.selectList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("货主数据更新保存")
    // @ApiOperation(value = "根据参数id，获取货主信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MOwnerVo>> insert(@RequestBody(required = false) MOwnerVo bean) {

        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("货主数据更新保存")
    // @ApiOperation(value = "根据参数id，获取货主信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MOwnerVo>> save(@RequestBody(required = false) MOwnerVo bean) {

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
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<MOwnerVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据禁用，部分数据")
    // @ApiOperation(value = "根据参数id，禁用数据")
    @PostMapping("/disabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> disabled(@RequestBody(required = false) List<MOwnerVo> searchConditionList) {
        service.disSabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用/停用，部分数据")
    // @ApiOperation(value = "根据参数id，启用数据")
    @PostMapping("/enable")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enable(@RequestBody(required = false) List<MOwnerVo> searchConditionList) {
        service.enableByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("导出")
    @PostMapping("/export")
    public void exportAll(@RequestBody(required = false) MOwnerVo searchCondition, HttpServletResponse response) throws IOException {
        List<MOwnerExportVo> list = service.export(searchCondition);
        new EasyExcelUtil<>(MOwnerExportVo.class).exportExcel("仓库库位"  + DateTimeUtil.getDate(), "仓库库位", list, response);
    }
}
