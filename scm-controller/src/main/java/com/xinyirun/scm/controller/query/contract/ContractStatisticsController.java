package com.xinyirun.scm.controller.query.contract;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.query.contract.ContractStatisticsService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Wqf
 * @Description: 合同統計表
 * @CreateTime : 2023/9/19 15:32
 */

@RestController
@RequestMapping(value = "/api/v1/report")
public class ContractStatisticsController {

    @Autowired
    private ContractStatisticsService service;

    @PostMapping("/purchase/page_list")
    @SysLogAnnotion("采购合同统计表")
    public ResponseEntity<JsonResultAo<IPage<PurchaseContractStatisticsVo>>> queryPageList(@RequestBody(required = false) PurchaseContractStatisticsVo param) {
        IPage<PurchaseContractStatisticsVo> list = service.queryPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @PostMapping("/purchase/sum")
    @SysLogAnnotion("采购合同统计表 合计")
    public ResponseEntity<JsonResultAo<PurchaseContractStatisticsVo>> getListSum(@RequestBody(required = false) PurchaseContractStatisticsVo param) {
        PurchaseContractStatisticsVo result = service.getListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/purchase/export")
    @SysLogAnnotion("采购合同统计表 导出")
    public void export(@RequestBody(required = false) PurchaseContractStatisticsVo param, HttpServletResponse response) throws IOException {
        List<PurchaseContractStatisticsExportVo> list = service.getExportList(param);
        new EasyExcelUtil<>(PurchaseContractStatisticsExportVo.class).exportExcel("采购合同统计表" + DateTimeUtil.getDate(),"采购合同统计表", list, response);
    }

    @PostMapping("/sales/page_list")
    @SysLogAnnotion("销售合同统计表")
    public ResponseEntity<JsonResultAo<IPage<SalesContractStatisticsVo>>> selectSalesPageList(@RequestBody(required = false) SalesContractStatisticsVo param) {
        IPage<SalesContractStatisticsVo> result = service.selectSalesPageList(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/sales/sum")
    @SysLogAnnotion("销售合同统计表")
    public ResponseEntity<JsonResultAo<SalesContractStatisticsVo>> selectSalesPageListSum(@RequestBody(required = false) SalesContractStatisticsVo param) {
        SalesContractStatisticsVo result = service.selectSalesPageListSum(param);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/sales/export")
    @SysLogAnnotion("销售合同统计表 导出")
    public void selectSalesListExport(@RequestBody(required = false) SalesContractStatisticsVo param, HttpServletResponse response) throws IOException {
        List<SalesContractStatisticsExportVo> result = service.selectSalesListExport(param);

        List<String> strategy_1 = result.stream().map(SalesContractStatisticsExportVo::getOrder_no).collect(Collectors.toList());
        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("销售合同统计表").head(SalesContractStatisticsExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 7))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 13))
                .build();

        new EasyExcelUtil<>(SalesContractStatisticsExportVo.class).exportExcel("销售合同统计表" + DateTimeUtil.getDate(), result, response, writeSheet);
    }

}
