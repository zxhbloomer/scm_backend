package com.xinyirun.scm.controller.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.inventory.*;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/query/report/")
public class MInventoryWarehouseGoodsQueryController {

    @Autowired
    private IBInService inService;

    @Autowired
    private IBOutService outService;

    @Autowired
    private IBAdjustDetailService adjustDetailService;

    @SysLogAnnotion("按仓库类型仓库商品-入库")
    @PostMapping("/in/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGoodsVo>>> queryInInventory(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
//        IPage<BWarehouseGoodsVo> list = inService.queryInInventory(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
        return null;
    }

    @SysLogAnnotion("按仓库类型仓库商品-入库合计")
    @PostMapping("/in/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BWarehouseGoodsVo>> queryInInventorySum(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
//        BWarehouseGoodsVo result = inService.queryInInventorySum(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
        return null;
    }

    @SysLogAnnotion("按仓库类型仓库商品-入库导出")
    @PostMapping("/in/list/export")
    @ResponseBody
    public void queryInInventoryExport(@RequestBody(required = false) List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
//        List<BWarehouseGoodsInExportVo> list = inService.queryInInventoryExport(searchCondition);
//        new EasyExcelUtil<>(BWarehouseGoodsInExportVo.class).exportExcel("按仓库类型仓库商品-入库" + DateTimeUtil.getDate(),"按仓库类型仓库商品-入库", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-入库导出全部")
    @PostMapping("/in/list/export_all")
    @ResponseBody
    public void queryInInventoryExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
//        List<BWarehouseGoodsInExportVo> list = inService.queryInInventoryExportAll(searchCondition);
//        new EasyExcelUtil<>(BWarehouseGoodsInExportVo.class).exportExcel("按仓库类型仓库商品-入库" + DateTimeUtil.getDate(),"按仓库类型仓库商品-入库", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-出库")
    @PostMapping("/out/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGoodsVo>>> queryOutInventory(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        IPage<BWarehouseGoodsVo> list = outService.queryOutInventory(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按仓库类型仓库商品-出库合计")
    @PostMapping("/out/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BWarehouseGoodsVo>> queryOutInventorySum(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        BWarehouseGoodsVo result = outService.queryOutInventorySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("按仓库类型仓库商品-出库导出")
    @PostMapping("/out/list/export_all")
    @ResponseBody
    public void queryOutInventoryExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsOutExportVo> list = outService.queryOutInventoryExportAll(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsOutExportVo.class).exportExcel("按仓库类型仓库商品-出库" + DateTimeUtil.getDate(),"按仓库类型仓库商品-出库", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-出库导出")
    @PostMapping("/out/list/export")
    @ResponseBody
    public void queryOutInventoryExport(@RequestBody(required = false) List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsOutExportVo> list = outService.queryOutInventoryExport(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsOutExportVo.class).exportExcel("按仓库类型仓库商品-出库" + DateTimeUtil.getDate(),"按仓库类型仓库商品-出库", list, response);
    }


    @SysLogAnnotion("按仓库类型仓库商品-调整")
    @PostMapping("/adjust/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGoodsVo>>> queryAdjustInventory(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        IPage<BWarehouseGoodsVo> list = adjustDetailService.queryAdjustInventory(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按仓库类型仓库商品-调整合计")
    @PostMapping("/adjust/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BWarehouseGoodsVo>> queryAdjustInventorySum(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        BWarehouseGoodsVo result = adjustDetailService.queryAdjustInventorySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("按仓库类型仓库商品-调整导出全部")
    @PostMapping("/adjust/list/export_all")
    @ResponseBody
    public void queryAdjustInventoryExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsAdjustExportVo> list = adjustDetailService.queryAdjustInventoryExportAll(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsAdjustExportVo.class).exportExcel("按仓库类型仓库商品-调整" + DateTimeUtil.getDate(),"按仓库类型仓库商品-调整", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-调整导出")
    @PostMapping("/adjust/list/export")
    @ResponseBody
    public void queryAdjustInventoryExport(@RequestBody(required = false) List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsAdjustExportVo> list = adjustDetailService.queryAdjustInventoryExport(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsAdjustExportVo.class).exportExcel("按仓库类型仓库商品-调整" + DateTimeUtil.getDate(),"按仓库类型仓库商品-调整", list, response);
    }


    @SysLogAnnotion("按仓库类型商品")
    @PostMapping("/inventory/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGoodsVo>>> queryReportInventoryList(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        IPage<BWarehouseGoodsVo> list = adjustDetailService.queryInventoryList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按仓库类型商品")
    @PostMapping("/inventory/list/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BWarehouseGoodsVo>> queryReportInventorySum(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        BWarehouseGoodsVo vo = adjustDetailService.queryReportInventorySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("按仓库类型商品")
    @PostMapping("/inventory/list/export")
    @ResponseBody
    public void queryReportInventoryExport(@RequestBody(required = false)List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsExportVo> list = adjustDetailService.queryReportInventoryExport(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsExportVo.class).exportExcel("按仓库类型商品报表" + DateTimeUtil.getDate(),"按仓库类型商品报表", list, response);
    }

    @SysLogAnnotion("按仓库类型商品")
    @PostMapping("/inventory/list/export_all")
    @ResponseBody
    public void queryReportInventoryExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsExportVo> list = adjustDetailService.queryReportInventoryExportAll(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsExportVo.class).exportExcel("按仓库类型商品报表" + DateTimeUtil.getDate(),"按仓库类型商品报表", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-存货")
    @PostMapping("/total/list/export_all")
    @ResponseBody
    public void queryReportTotalExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsTotalExportVo> list = adjustDetailService.queryReportTotalExportAll(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsTotalExportVo.class).exportExcel("按仓库类型仓库商品-存货" + DateTimeUtil.getDate(),"按仓库类型仓库商品-存货", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-存货")
    @PostMapping("/total/list/export")
    @ResponseBody
    public void queryReportTotalExportAll(@RequestBody(required = false)List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseGoodsTotalExportVo> list = adjustDetailService.queryReportTotalExport(searchCondition);
        new EasyExcelUtil<>(BWarehouseGoodsTotalExportVo.class).exportExcel("按仓库类型仓库商品-存货" + DateTimeUtil.getDate(),"按仓库类型仓库商品-存货", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品-存货 列表查询")
    @PostMapping("/total/page_list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BWarehouseGoodsVo>>> selectTotalPageList(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        IPage<BWarehouseGoodsVo> list = adjustDetailService.selectTotalPageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按仓库类型仓库商品-存货 合计")
    @PostMapping("/total/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BWarehouseGoodsVo>> selectTotalPageListSum(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        BWarehouseGoodsVo result = adjustDetailService.selectTotalPageListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("按仓库类型仓库商品")
    @PostMapping("/list/export_all")
    @ResponseBody
    public void queryReportExportAll(@RequestBody(required = false) BWarehouseGoodsVo searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseInventoryExportVo> list = adjustDetailService.queryReportExportAll(searchCondition);
        new EasyExcelUtil<>(BWarehouseInventoryExportVo.class).exportExcel("按仓库类型仓库商品" + DateTimeUtil.getDate(),"按仓库类型仓库商品", list, response);
    }

    @SysLogAnnotion("按仓库类型仓库商品")
    @PostMapping("/list/export")
    @ResponseBody
    public void queryReportExportAll(@RequestBody(required = false)List<BWarehouseGoodsVo> searchCondition, HttpServletResponse response) throws IOException {
        List<BWarehouseInventoryExportVo> list = adjustDetailService.queryReportExport(searchCondition);
        new EasyExcelUtil<>(BWarehouseInventoryExportVo.class).exportExcel("按仓库类型仓库商品" + DateTimeUtil.getDate(),"按仓库类型仓库商品", list, response);
    }

    @SysLogAnnotion("按仓库类型 汇总")
    @PostMapping("/warehouse_type/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BWarehouseGoodsVo>>> queryWarehouseTypeList(@RequestBody(required = false) BWarehouseGoodsVo searchCondition) {
        List<BWarehouseGoodsVo> list = adjustDetailService.queryWarehouseTypeList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

}
