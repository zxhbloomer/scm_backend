package com.xinyirun.scm.controller.query.ledger;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.report.ledger.*;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.query.ledger.ProcessingLedgerService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 中林加工掺混点入库台账
 * @CreateTime : 2023/8/1 14:58
 */

@RestController
@RequestMapping(value = "/api/v1/ledger")
public class ProcessingLedgerController {

    @Autowired
    private ProcessingLedgerService service;

    @PostMapping("/rice_in/page_list")
    @SysLogAnnotion("稻谷入库进度表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingRiceWarehouseInProgressVo>>> queryRicePageList(@RequestBody(required = false) ProcessingRiceWarehouseInProgressVo searchCondition) {
        IPage<ProcessingRiceWarehouseInProgressVo> list = service.queryRicePageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/rice_in/sum")
    @SysLogAnnotion("稻谷入库进度表 合计")
    public ResponseEntity<JsonResultAo<ProcessingRiceWarehouseInProgressVo>> queryRicePageListSum(@RequestBody(required = false) ProcessingRiceWarehouseInProgressVo searchCondition) {
        ProcessingRiceWarehouseInProgressVo result = service.queryRicePageListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/rice_in/export")
    @SysLogAnnotion("稻谷入库进度表 导出")
    public void queryRicePageListExport(@RequestBody(required = false) ProcessingRiceWarehouseInProgressVo param, HttpServletResponse response) throws IOException {
        List<ProcessingRiceWarehouseInProgressExportVo> list = service.queryRicePageListExport(param);
        new EasyExcelUtil<>(ProcessingRiceWarehouseInProgressExportVo.class).exportExcel("稻谷入库进度表" + DateTimeUtil.getDate(),"稻谷入库进度表", list, response);
    }

    @PostMapping("/maize_in/page_list")
    @SysLogAnnotion("玉米入库进度表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingMaizeAndWheatWarehouseInProgressVo>>> queryMaizePageList(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> list = service.queryMaizePageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/maize_in/sum")
    @SysLogAnnotion("玉米入库进度表 合计")
    public ResponseEntity<JsonResultAo<ProcessingMaizeAndWheatWarehouseInProgressVo>> queryMaizePageListSum(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        ProcessingMaizeAndWheatWarehouseInProgressVo result = service.queryMaizePageListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/maize_in/export")
    @SysLogAnnotion("玉米入库进度表 导出")
    public void queryMaizePageListExport(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition, HttpServletResponse response) throws IOException {
        List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> list = service.queryMaizePageListExport(searchCondition);
        new EasyExcelUtil<>(ProcessingMaizeAndWheatWarehouseInProgressExportVo.class).exportExcel("玉米入库进度表" + DateTimeUtil.getDate(),"玉米入库进度表", list, response);
    }

    @PostMapping("/wheat_in/page_list")
    @SysLogAnnotion("小麦入库进度表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingMaizeAndWheatWarehouseInProgressVo>>> queryWheatPageList(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        IPage<ProcessingMaizeAndWheatWarehouseInProgressVo> list = service.queryWheatPageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/wheat_in/sum")
    @SysLogAnnotion("小麦入库进度表 合计")
    public ResponseEntity<JsonResultAo<ProcessingMaizeAndWheatWarehouseInProgressVo>> queryWheatPageListSum(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition) {
        ProcessingMaizeAndWheatWarehouseInProgressVo list = service.queryWheatPageListSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/wheat_in/export")
    @SysLogAnnotion("小麦入库进度表 导出")
    public void queryWheatListExport(@RequestBody(required = false) ProcessingMaizeAndWheatWarehouseInProgressVo searchCondition, HttpServletResponse response) throws IOException {
        List<ProcessingMaizeAndWheatWarehouseInProgressExportVo> list = service.queryWheatListExport(searchCondition);
        new EasyExcelUtil<>(ProcessingMaizeAndWheatWarehouseInProgressExportVo.class).exportExcel("小麦入库进度表" + DateTimeUtil.getDate(),"小麦入库进度表", list, response);
    }

    @PostMapping("/ricehull_out/page_list")
    @SysLogAnnotion("稻壳出库明细表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingRiceHullWarehouseOutDetailVo>>> queryRiceHullPageList(@RequestBody(required = false) ProcessingRiceHullWarehouseOutDetailVo param) {
        IPage<ProcessingRiceHullWarehouseOutDetailVo> list = service.queryRiceHullPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/ricehull_out/export")
    @SysLogAnnotion("稻壳出库明细表 导出")
    public void exportRicehullOutList(@RequestBody(required = false) ProcessingRiceHullWarehouseOutDetailVo param, HttpServletResponse response) throws IOException {
        List<ProcessingRiceHullWarehouseOutDetailExportVo> list = service.exportRicehullOutList(param);
        new EasyExcelUtil<>(ProcessingRiceHullWarehouseOutDetailExportVo.class).exportExcel("稻壳出库明细表" + DateTimeUtil.getDate(),"稻壳出库明细表", list, response);
    }

    @PostMapping("/grain_out/page_list")
    @SysLogAnnotion("糙米出库进度表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingGrainWarehouseInOutDetailVo>>> queryGrainOutPageList(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param) {
        IPage<ProcessingGrainWarehouseInOutDetailVo> list = service.queryGrainOutPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/grain_out/sum")
    @SysLogAnnotion("糙米出库进度表 合计")
    public ResponseEntity<JsonResultAo<ProcessingGrainWarehouseInOutDetailVo>> queryGrainOutPageListSum(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param) {
        ProcessingGrainWarehouseInOutDetailVo result = service.queryGrainOutPageListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/grain_out/export")
    @SysLogAnnotion("糙米出库进度表 导出")
    public void queryGrainOutPageListExport(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param, HttpServletResponse response) throws IOException {
        List<ProcessingGrainWarehouseOutDetailExportVo> list = service.queryGrainOutPageListExport(param);
        new EasyExcelUtil<>(ProcessingGrainWarehouseOutDetailExportVo.class).exportExcel("糙米出库进度表" + DateTimeUtil.getDate(),"糙米出库进度表", list, response);
    }

    @PostMapping("/grain_in/page_list")
    @SysLogAnnotion("糙米入库进度表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingGrainWarehouseInOutDetailVo>>> queryGrainInPageList(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param) {
        IPage<ProcessingGrainWarehouseInOutDetailVo> list = service.queryGrainInPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/grain_in/sum")
    @SysLogAnnotion("糙米入库进度表")
    public ResponseEntity<JsonResultAo<ProcessingGrainWarehouseInOutDetailVo>> queryGrainInListSum(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param) {
        ProcessingGrainWarehouseInOutDetailVo result = service.queryGrainInListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/grain_in/export")
    @SysLogAnnotion("糙米入库进度表")
    public void exportGrainInList(@RequestBody(required = false) ProcessingGrainWarehouseInOutDetailVo param, HttpServletResponse response) throws IOException {
        List<ProcessingGrainWarehouseInDetailExportVo> list = service.exportGrainInList(param);
        new EasyExcelUtil<>(ProcessingGrainWarehouseInDetailExportVo.class).exportExcel("糙米入库进度表" + DateTimeUtil.getDate(),"糙米入库进度表", list, response);
    }


    @PostMapping("/combo_out/page_list")
    @SysLogAnnotion("混合物出库进度表 列表")
    public ResponseEntity<JsonResultAo<IPage<ProcessingComboWarehouseOutProgressVo>>> queryComboPageList(@RequestBody(required = false) ProcessingComboWarehouseOutProgressVo param) {
        IPage<ProcessingComboWarehouseOutProgressVo> list = service.queryComboOutPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/combo_out/sum")
    @SysLogAnnotion("混合物出库进度表 合计")
    public ResponseEntity<JsonResultAo<ProcessingComboWarehouseOutProgressVo>> queryComboListSum(@RequestBody(required = false) ProcessingComboWarehouseOutProgressVo param) {
        ProcessingComboWarehouseOutProgressVo result = service.queryComboOutListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/combo_out/export")
    @SysLogAnnotion("混合物出库进度表 导出")
    public void exportComboList(@RequestBody(required = false) ProcessingComboWarehouseOutProgressVo param, HttpServletResponse response) throws IOException {
        List<ProcessingComboWarehouseOutProgressExportVo> list = service.exportComboList(param);
        new EasyExcelUtil<>(ProcessingComboWarehouseOutProgressExportVo.class).exportExcel("混合物出库进度表" + DateTimeUtil.getDate(),"混合物出库进度表", list, response);
    }

}
