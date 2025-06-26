package com.xinyirun.scm.controller.sys.config.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.excel.export.ExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/config")
@Slf4j
// @Api(tags = "系统参数相关")
public class ConfigController extends SystemBaseController {

    @Autowired
    private ISConfigService service;

    @SysLogAnnotion("根据查询条件，获取系统参数信息")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<SConfigVo>>> list(@RequestBody(required = false) SConfigVo searchCondition) {
        IPage<SConfigVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("系统参数数据更新保存")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SConfigVo>> save(@RequestBody(required = false) SConfigVo bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("系统参数数据新增保存")
    // @ApiOperation(value = "根据参数id，获取系统参数信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<SConfigVo>> insert(@RequestBody(required = false) SConfigVo bean) {
        // 默认启用
        bean.setIs_enable(true);
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("系统参数数据导出")
    // @ApiOperation(value = "根据选择的数据，系统参数数据导出")
    @PostMapping("/export_all")
    public void exportAll(@RequestBody(required = false) SConfigVo searchCondition, HttpServletResponse response) throws IOException {
        List<SConfigDataExportVo> searchResult = service.selectExportList(searchCondition);
        ExcelUtil<SConfigDataExportVo> util = new ExcelUtil<>(SConfigDataExportVo.class);
        util.exportExcel("系统参数数据导出", "系统参数数据", searchResult, response);
    }

    @SysLogAnnotion("系统参数数据导出")
    // @ApiOperation(value = "根据选择的数据，系统参数数据导出")
    @PostMapping("/export_selection")
    public void exportSelection(@RequestBody(required = false) List<SConfigVo> searchConditionList, HttpServletResponse response) throws IOException {
        List<SConfigDataExportVo> searchResult = service.selectIdsInForExport(searchConditionList);
        ExcelUtil<SConfigDataExportVo> util = new ExcelUtil<>(SConfigDataExportVo.class);
        util.exportExcel("系统参数数据导出", "系统参数数据", searchResult, response);
    }

    @SysLogAnnotion("根据选择的数据逻辑物理删除，部分数据")
    // @ApiOperation(value = "根据参数id，逻辑删除数据")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<SConfigVo> searchConditionList) {
        service.realDeleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("根据选择的数据启用禁用，部分数据")
    // @ApiOperation(value = "根据参数id，启用禁用数据")
    @PostMapping("/enabled")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> enabled(@RequestBody(required = false) List<SConfigVo> searchConditionList) {
        service.enabledByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }
}
