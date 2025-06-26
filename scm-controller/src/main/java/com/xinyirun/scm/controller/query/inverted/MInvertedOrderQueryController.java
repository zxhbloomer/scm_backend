package com.xinyirun.scm.controller.query.inverted;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedExportVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.order.IBOrderInvertedService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xtj
 * @Description:
 * @CreateTime : 2024/8/12 14:03
 */
@RestController
@RequestMapping("/api/v1/query/")
public class MInvertedOrderQueryController {

    @Autowired
    private IBOrderInvertedService ibOrderService;

    @SysLogAnnotion("稻谷出库计划倒排表")
    @PostMapping("/inverted_order_query/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BOrderInvertedVo>>> queryInvertedOrderOutPlan(@RequestBody(required = false) BOrderInvertedVo searchCondition) {
        List<BOrderInvertedVo> list = ibOrderService.queryInvertedOrderOutPlan(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("稻谷出库计划倒排表")
    @PostMapping("/inverted_order_query/getBadgeDate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOrderInvertedVo>> getBadgeDate(@RequestBody(required = false) BOrderInvertedVo searchCondition) {
        BOrderInvertedVo invertedVo = ibOrderService.getBadgeDate(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(invertedVo));
    }

    @SysLogAnnotion("稻谷出库计划倒排表导出全部")
    @PostMapping("inverted_order_query/export_all")
    public void queryInvertedExportAll(@RequestBody(required = false) BOrderInvertedVo param, HttpServletResponse response) throws IOException {
        List<BOrderInvertedExportVo> list = ibOrderService.queryInvertedExportAll(param);
        new EasyExcelUtil<>(BOrderInvertedExportVo.class).exportExcel("稻谷出库计划倒排表"  + DateTimeUtil.getDate(),"稻谷出库计划倒排表", list, response);
    }

    @SysLogAnnotion("稻谷出库计划倒排表导出部分")
    @PostMapping("inverted_order_query/export")
    public void queryInvertedExport(@RequestBody(required = false) BOrderInvertedVo param, HttpServletResponse response) throws IOException {
        List<BOrderInvertedExportVo> list = ibOrderService.queryInvertedExport(param);
        new EasyExcelUtil<>(BOrderInvertedExportVo.class).exportExcel("稻谷出库计划倒排表"  + DateTimeUtil.getDate(),"稻谷出库计划倒排表", list, response);
    }

    @SysLogAnnotion("稻谷出库计划倒排表获取竞拍下拉列表")
    @PostMapping("/inverted_order_query/getAuctionDateList")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BOrderInvertedVo>> getAuctionDateList(@RequestBody(required = false) BOrderInvertedVo searchCondition) {
        BOrderInvertedVo invertedVo = ibOrderService.getAuctionDateList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(invertedVo));
    }
}
