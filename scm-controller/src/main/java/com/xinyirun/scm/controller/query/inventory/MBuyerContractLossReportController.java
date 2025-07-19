package com.xinyirun.scm.controller.query.inventory;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.*;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.monitor.IBMonitorService;
import com.xinyirun.scm.core.system.service.business.wms.out.order.IBOutOrderService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @since 2022-08-31
 */
@RestController
@RequestMapping("/api/v1/query/")
public class MBuyerContractLossReportController extends SystemBaseController {

//    @Resource
//    private IBInOrderService inService;

    @Resource
    private IBOutOrderService outOrderService;

    @Resource
    private IBMonitorService monitorService;

    @SysLogAnnotion("采购合同汇总")
    @PostMapping("in/list")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryBuyerContractList(@RequestBody(required = false) BContractReportVo param) {
//        IPage<BContractReportVo> list = inService.queryBuyerContractList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
        return null;
    }

    @SysLogAnnotion("采购合同汇总求和")
    @PostMapping("in/list/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryBuyerContractListSum(@RequestBody(required = false) BContractReportVo param) {
//        BContractReportVo result = inService.queryBuyerContractListSum(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(result));
        return null;
    }

    @SysLogAnnotion("采购 销售合同量汇总")
    @PostMapping("in_out/list")
    public ResponseEntity<JsonResultAo<List<BContractReportVo>>> queryInOutList(@RequestBody(required = false) BContractReportVo param) {
//        List<BContractReportVo> list = inService.queryInOutList(param);
//        return ResponseEntity.ok().body(ResultUtil.OK(list));
        return null;
    }

    @SysLogAnnotion("采购合同导出全部")
    @PostMapping("in/export_all")
    public void queryBuyerContractListExport(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
//        List<BBuyContractReportExportVo> list = inService.queryBuyerContractListExportAll(param);
//        new EasyExcelUtil<>(BBuyContractReportExportVo.class).exportExcel("采购合同汇总"  + DateTimeUtil.getDate(),"采购合同汇总", list, response);
    }

    @SysLogAnnotion("采购合同导出部分")
    @PostMapping("in/export")
    public void queryBuyerContractListExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
//        List<BBuyContractReportExportVo> list = inService.queryBuyerContractListExport(param);
//        new EasyExcelUtil<>(BBuyContractReportExportVo.class).exportExcel("采购合同汇总"  + DateTimeUtil.getDate(),"采购合同汇总", list, response);
    }

    @SysLogAnnotion("销售合同汇总")
    @PostMapping("out/list")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryOutContractList(@RequestBody(required = false) BContractReportVo param) {
        IPage<BContractReportVo> list = outOrderService.queryOutContractList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("销售合同汇总 求和")
    @PostMapping("out/list/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryOutContractListSum(@RequestBody(required = false) BContractReportVo param) {
        BContractReportVo result = outOrderService.queryOutContractListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("销售合同汇总 全部导出")
    @PostMapping("out/list/export_all")
    public void queryOutContractListExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BOutContractReportExportVo> list = outOrderService.queryOutContractListExportAll(param);
        new EasyExcelUtil<>(BOutContractReportExportVo.class).exportExcel("销售合同汇总"  + DateTimeUtil.getDate(),"销售合同汇总", list, response);
    }

    @SysLogAnnotion("销售合同汇总 部分导出")
    @PostMapping("out/list/export")
    public void queryOutContractListExportAll(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BOutContractReportExportVo> list = outOrderService.queryOutContractListExport(param);
        new EasyExcelUtil<>(BOutContractReportExportVo.class).exportExcel("销售合同汇总"  + DateTimeUtil.getDate(),"销售合同汇总", list, response);
    }

    @SysLogAnnotion("损耗报表详情  and 在途报表详情")
    @PostMapping("qty_loss/list")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryQtyLossList(@RequestBody(required = false) BContractReportVo param) {
        IPage<BContractReportVo> list = monitorService.queryQtyLossList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("损耗报表详情 and 在途报表详情 求和")
    @PostMapping("qty_loss/list/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryQtyLossListSum(@RequestBody(required = false) BContractReportVo param) {
        BContractReportVo result = monitorService.queryQtyLossListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("损耗报表详情 全部导出")
    @PostMapping("qty_loss/list/export_all")
    public void queryQtyLossListExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyLossReportExportVo> list = monitorService.queryQtyLossListExportAll(param);
        new EasyExcelUtil<>(BQtyLossReportExportVo.class).exportExcel("损耗报表明细"  + DateTimeUtil.getDate(),"损耗报表明细", list, response);
    }

    @SysLogAnnotion("损耗报表详情 部分导出")
    @PostMapping("qty_loss/list/export")
    public void queryQtyLossListExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyLossReportExportVo> list = monitorService.queryQtyLossListExport(param);
        new EasyExcelUtil<>(BQtyLossReportExportVo.class).exportExcel("损耗报表明细"  + DateTimeUtil.getDate(),"损耗报表明细", list, response);
    }

    @SysLogAnnotion("物流订单损耗， 在途明细")
    @PostMapping("schedule/list")
    public ResponseEntity<JsonResultAo<IPage<BQtyLossScheduleReportVo>>> queryScheduleList(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        IPage<BQtyLossScheduleReportVo> list = monitorService.queryScheduleList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("物流订单损耗明细 合计")
    @PostMapping("schedule/list/sum")
    public ResponseEntity<JsonResultAo<BQtyLossScheduleReportVo>> queryScheduleListSum(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        BQtyLossScheduleReportVo result = monitorService.queryScheduleListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("物流订单损耗明细 全部导出")
    @PostMapping("schedule/list/export_all")
    public void queryScheduleListExportAll(@RequestBody(required = false) BQtyLossScheduleReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyLossScheduleDetailExportVo> list = monitorService.queryScheduleListExportAll(param);
        new EasyExcelUtil<>(BQtyLossScheduleDetailExportVo.class).exportExcel("物流订单损耗明细"  + DateTimeUtil.getDate(),"物流订单损耗明细", list, response);
    }

    @SysLogAnnotion("物流订单损耗明细 部分导出")
    @PostMapping("schedule/list/export")
    public void queryScheduleListExport(@RequestBody(required = false) List<BQtyLossScheduleReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyLossScheduleDetailExportVo> list = monitorService.queryScheduleListExport(param);
        new EasyExcelUtil<>(BQtyLossScheduleDetailExportVo.class).exportExcel("物流订单损耗明细"  + DateTimeUtil.getDate(),"物流订单损耗明细", list, response);
    }

    @SysLogAnnotion("监管任务损耗, 在途明细")
    @PostMapping("monitor/list")
    public ResponseEntity<JsonResultAo<IPage<BQtyLossScheduleReportVo>>> queryMonitorList(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        IPage<BQtyLossScheduleReportVo> list = monitorService.queryMonitorList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("监管任务损耗明细 合计")
    @PostMapping("monitor/list/sum")
    public ResponseEntity<JsonResultAo<BQtyLossScheduleReportVo>> queryMonitorListSum(@RequestBody(required = false) BQtyLossScheduleReportVo param) {
        BQtyLossScheduleReportVo result = monitorService.queryMonitorListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("监管任务损耗明细 全部导出")
    @PostMapping("monitor/list/export_all")
    public void queryMonitorListExportAll(@RequestBody(required = false) BQtyLossScheduleReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyLossMonitorDetailExportVo> list = monitorService.queryMonitorListExportAll(param);
        new EasyExcelUtil<>(BQtyLossMonitorDetailExportVo.class).exportExcel("物流订单损耗明细"  + DateTimeUtil.getDate(),"物流订单损耗明细", list, response);
    }

    @SysLogAnnotion("监管任务损耗明细 部分导出")
    @PostMapping("monitor/list/export")
    public void queryMonitorListExport(@RequestBody(required = false) List<BQtyLossScheduleReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyLossMonitorDetailExportVo> list = monitorService.queryMonitorListExport(param);
        new EasyExcelUtil<>(BQtyLossMonitorDetailExportVo.class).exportExcel("物流订单损耗明细"  + DateTimeUtil.getDate(),"物流订单损耗明细", list, response);
    }

    @SysLogAnnotion("在途报表 全部导出")
    @PostMapping("in_transit/list/export_all")
    public void queryOnWayListExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BInTransitReportExportVo> list = monitorService.queryOnWayListExportAll(param);
        new EasyExcelUtil<>(BInTransitReportExportVo.class).exportExcel("在途报表明细"  + DateTimeUtil.getDate(),"在途报表明细", list, response);
    }

    @SysLogAnnotion("在途报表 部分导出")
    @PostMapping("in_transit/list/export")
    public void queryOnWayListExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BInTransitReportExportVo> list = monitorService.queryOnWayListExport(param);
        new EasyExcelUtil<>(BInTransitReportExportVo.class).exportExcel("在途报表明细" + DateTimeUtil.getDate(),"在途报表明细", list, response);
    }

    @SysLogAnnotion("物流订单在途明细 全部导出")
    @PostMapping("schedule_in_transit/list/export_all")
    public void queryScheduleListWayExportAll(@RequestBody(required = false) BQtyLossScheduleReportVo param, HttpServletResponse response) throws IOException {
        List<BScheduleLossInTransitExportVo> list = monitorService.queryScheduleListWayExportAll(param);
        new EasyExcelUtil<>(BScheduleLossInTransitExportVo.class).exportExcel("物流订单在途明细" + DateTimeUtil.getDate(),"物流订单在途明细", list, response);
    }

    @SysLogAnnotion("物流订单在途明细 部分导出")
    @PostMapping("schedule_in_transit/list/export")
    public void queryScheduleListWayExport(@RequestBody(required = false) List<BQtyLossScheduleReportVo> param, HttpServletResponse response) throws IOException {
        List<BScheduleLossInTransitExportVo> list = monitorService.queryScheduleListWayExport(param);
        new EasyExcelUtil<>(BScheduleLossInTransitExportVo.class).exportExcel("物流订单在途明细" + DateTimeUtil.getDate(),"物流订单在途明细", list, response);
    }

    @SysLogAnnotion("监管任务在途明细 全部导出")
    @PostMapping("monitor_in_transit/export_all")
    public void queryMonitorWayListExportAll(@RequestBody(required = false) BQtyLossScheduleReportVo param, HttpServletResponse response) throws IOException {
        List<BMonitorLossInTransitExportVo> list = monitorService.queryMonitorWayListExportAll(param);
        new EasyExcelUtil<>(BMonitorLossInTransitExportVo.class).exportExcel("监管任务在途明细" + DateTimeUtil.getDate(),"监管任务在途明细", list, response);
    }

    @SysLogAnnotion("监管任务在途明细 部分导出")
    @PostMapping("monitor_in_transit/export")
    public void queryMonitorWayListExport(@RequestBody(required = false) List<BQtyLossScheduleReportVo> param, HttpServletResponse response) throws IOException {
        List<BMonitorLossInTransitExportVo> list = monitorService.queryMonitorWayListExport(param);
        new EasyExcelUtil<>(BMonitorLossInTransitExportVo.class).exportExcel("监管任务在途明细" + DateTimeUtil.getDate(),"监管任务在途明细", list, response);
    }

    @SysLogAnnotion("损耗报表 and 在途报表 汇总")
    @PostMapping("qty_total/list")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryQtyTotalList(@RequestBody(required = false) BContractReportVo param) {
        IPage<BContractReportVo> list = monitorService.queryQtyTotalList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("损耗报表 and 在途报表 汇总 求和")
    @PostMapping("qty_total/list/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryQtyTotalSumList(@RequestBody(required = false) BContractReportVo param) {
        BContractReportVo list = monitorService.queryQtyTotalSumList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("损耗报表汇总 全部导出")
    @PostMapping("loss/export_all")
    public void queryQtyLossAllExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyLossExportVo> list = monitorService.queryQtyLossAllExportAll(param);
        new EasyExcelUtil<>(BQtyLossExportVo.class).exportExcel("损耗报表汇总" + DateTimeUtil.getDate(),"损耗报表汇总", list, response);
    }

    @SysLogAnnotion("损耗报表汇总 部分导出")
    @PostMapping("loss/export")
    public void queryQtyLossExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyLossExportVo> list = monitorService.queryQtyLossExport(param);
        new EasyExcelUtil<>(BQtyLossExportVo.class).exportExcel("损耗报表汇总" + DateTimeUtil.getDate(),"损耗报表汇总", list, response);
    }

    @SysLogAnnotion("在途报表汇总 全部导出")
    @PostMapping("in_transit/export_all")
    public void queryQtyOnWayAllExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyInTransitExportVo> list = monitorService.queryQtyOnWayAllExportAll(param);
        new EasyExcelUtil<>(BQtyInTransitExportVo.class).exportExcel("在途报表汇总" + DateTimeUtil.getDate(),"在途报表汇总", list, response);
    }

    @SysLogAnnotion("在途报表汇总 部分导出")
    @PostMapping("in_transit/export")
    public void queryQtyOnWayExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyInTransitExportVo> list = monitorService.queryQtyOnWayExport(param);
        new EasyExcelUtil<>(BQtyInTransitExportVo.class).exportExcel("在途报表汇总" + DateTimeUtil.getDate(),"在途报表汇总", list, response);
    }


    @SysLogAnnotion("在途报表包含铁路港口码头虚拟库 汇总")
    @PostMapping("qty_total/list_and_inventor")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryQtyInventorTotalList(@RequestBody(required = false) BContractReportVo param) {
        IPage<BContractReportVo> list = monitorService.queryQtyInventorTotalList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("在途报表包含铁路港口码头虚拟库 汇总 求和")
    @PostMapping("qty_total/list_and_inventor/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryQtyInventorSumList(@RequestBody(required = false) BContractReportVo param) {
        BContractReportVo list = monitorService.queryQtyInventorSumList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("在途报表汇总包含铁路港口码头虚拟库 全部导出")
    @PostMapping("in_transit/inventor/export_all")
    public void queryQtyOnWayByInventorAllExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BQtyInTransitExportVo> list = monitorService.queryQtyOnWayByInventorAllExportAll(param);
        new EasyExcelUtil<>(BQtyInTransitExportVo.class).exportExcel("在途报表汇总" + DateTimeUtil.getDate(),"在途报表汇总", list, response);
    }

    @SysLogAnnotion("在途报表汇总包含铁路港口码头虚拟库 部分导出")
    @PostMapping("in_transit/inventor/export")
    public void queryQtyOnWayByInventorExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BQtyInTransitExportVo> list = monitorService.queryQtyOnWayByInventorExport(param);
        new EasyExcelUtil<>(BQtyInTransitExportVo.class).exportExcel("在途报表汇总" + DateTimeUtil.getDate(),"在途报表汇总", list, response);
    }

    @SysLogAnnotion("在途报表明细包含铁路港口码头虚拟库")
    @PostMapping("qty_loss/list_inventor")
    public ResponseEntity<JsonResultAo<IPage<BContractReportVo>>> queryQtyInventorLossList(@RequestBody(required = false) BContractReportVo param) {
        IPage<BContractReportVo> list = monitorService.queryQtyInventorLossList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("在途报表明细包含铁路港口码头虚拟库 求和")
    @PostMapping("qty_loss/list_inventor/sum")
    public ResponseEntity<JsonResultAo<BContractReportVo>> queryQtyInventorLossListSum(@RequestBody(required = false) BContractReportVo param) {
        BContractReportVo result = monitorService.queryQtyInventorLossListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }


    @SysLogAnnotion("在途报表明细包含铁路港口码头虚拟库 全部导出")
    @PostMapping("in_transit/inventor/list/export_all")
    public void queryOnWayByInventorListExportAll(@RequestBody(required = false) BContractReportVo param, HttpServletResponse response) throws IOException {
        List<BInTransitReportExportVo> list = monitorService.queryOnWayByInventorListExportAll(param);
        new EasyExcelUtil<>(BInTransitReportExportVo.class).exportExcel("在途报表明细"  + DateTimeUtil.getDate(),"在途报表明细", list, response);
    }

    @SysLogAnnotion("在途报表明细包含铁路港口码头虚拟库 部分导出")
    @PostMapping("in_transit/inventor/list/export")
    public void queryOnWayByInventorListExport(@RequestBody(required = false) List<BContractReportVo> param, HttpServletResponse response) throws IOException {
        List<BInTransitReportExportVo> list = monitorService.queryOnWayByInventorListExport(param);
        new EasyExcelUtil<>(BInTransitReportExportVo.class).exportExcel("在途报表明细" + DateTimeUtil.getDate(),"在途报表明细", list, response);
    }

}
