package com.xinyirun.scm.controller.query.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryDetailExportVo;
import com.xinyirun.scm.bean.system.vo.excel.query.MInventoryStagnationWarningExportVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQuerySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryDetailQueryVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.query.inventory.IMInventoryDetailQueryService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * <p>
 * 查询库存明细
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/query/inventory")
public class MInventoryDetailQueryController extends SystemBaseController {

    @Autowired
    private IMInventoryDetailQueryService service;

    @SysLogAnnotion("查询库存明细")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryDetailQueryVo>>> queryInventoryDetails(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition) {
        IPage<MInventoryDetailQueryVo> list = service.queryInventoryDetails(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询库存明细合计数据")
    @PostMapping("/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventoryDetailQuerySumVo>> queryInventoryDetailsSum(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition) {
        MInventoryDetailQuerySumVo vo = service.queryInventoryDetailsSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("库存明细数据导出")
    @PostMapping("/export")
    public void exportData(@RequestBody(required = false) List<MInventoryDetailQueryVo> searchCondition, HttpServletResponse response) throws Exception {
        List<MInventoryDetailExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<MInventoryDetailExportVo> util = new EasyExcelUtil<>(MInventoryDetailExportVo.class);
        util.exportExcel("库存明细" + DateTimeUtil.getDate(), "库存明细", list, response);
    }

    @SysLogAnnotion("库存明细数据导出")
    @PostMapping("/export_all")
    public void exportAllData(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition, HttpServletResponse response) throws Exception {
        List<MInventoryDetailExportVo> list = service.selectExportAllList(searchCondition);
        EasyExcelUtil<MInventoryDetailExportVo> util = new EasyExcelUtil<>(MInventoryDetailExportVo.class);
        util.exportExcel("库存明细" + DateTimeUtil.getDate(), "库存明细", list, response);
    }

    @SysLogAnnotion("出库计划新增查询库存明细")
    @PostMapping("/outplan/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryDetailQueryVo>>> selectListByOrderId(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition) {
        IPage<MInventoryDetailQueryVo> list = service.selectListByOrderId(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("查询港口中转停滞预警")
    @PostMapping("/list_warning")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryDetailQueryVo>>> queryInventoryByWarning(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition) {
        IPage<MInventoryDetailQueryVo> list = service.queryInventoryByWarning(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("查询港口中转停滞预警合计数据")
    @PostMapping("/list/sum_warning")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MInventoryDetailQuerySumVo>> queryInventoryByWarningSum(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition) {
        MInventoryDetailQuerySumVo vo = service.queryInventoryByWarningSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("港口中转停滞预警数据导出")
    @PostMapping("/export_warning")
    public void exportDataWarning(@RequestBody(required = false) List<MInventoryDetailQueryVo> searchCondition, HttpServletResponse response) throws Exception {
        List<MInventoryStagnationWarningExportVo> list = service.selectExportDataWarning(searchCondition);
        EasyExcelUtil<MInventoryStagnationWarningExportVo> util = new EasyExcelUtil<>(MInventoryStagnationWarningExportVo.class);
        util.exportExcel("港口中转滞留预警" + DateTimeUtil.getDate(), "港口中转滞留预警", list, response);
    }

    @SysLogAnnotion("港口中转停滞预警导出")
    @PostMapping("/export_all_warning")
    public void exportAllDataWarning(@RequestBody(required = false) MInventoryDetailQueryVo searchCondition, HttpServletResponse response) throws Exception {
        List<MInventoryStagnationWarningExportVo> list = service.selectExportAllDataWarning(searchCondition);
        EasyExcelUtil<MInventoryStagnationWarningExportVo> util = new EasyExcelUtil<>(MInventoryStagnationWarningExportVo.class);
        util.exportExcel("港口中转滞留预警" + DateTimeUtil.getDate(), "港口中转滞留预警", list, response);
    }
}
