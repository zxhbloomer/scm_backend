package com.xinyirun.scm.controller.business.poorder;


import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.poorder.IBPoOrderService;
import com.xinyirun.scm.excel.export.CustomMergeStrategy;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购订单表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@RestController
@RequestMapping("/api/v1/poorder")
public class BPoOrderController {

    @Autowired
    private IBPoOrderService service;

    /**
     * 采购合同  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("采购订单 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<PoOrderVo>> insert(@RequestBody PoOrderVo searchCondition) {
        InsertResultAo<PoOrderVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("采购订单校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) PoOrderVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("根据查询条件，获取采购订单集合信息")
    @PostMapping("/pagelist")
    public ResponseEntity<JsonResultAo<IPage<PoOrderVo>>> list(@RequestBody(required = false) PoOrderVo searchCondition) {
        IPage<PoOrderVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("采购订单管理，按退款条件获取列表信息")
    @PostMapping("/pagelist/byaprefund")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<PoOrderVo>>> selectPagelistByAprefund(@RequestBody(required = false) PoOrderVo searchCondition) {
        IPage<PoOrderVo> list = service.selectPageByAprefund(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按采购订单合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> querySum(@RequestBody(required = false) PoOrderVo searchCondition) {
        PoOrderVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("采购订单管理，按退款条件汇总查询")
    @PostMapping("/sum/aprefund")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> querySumByAprefund(@RequestBody(required = false) PoOrderVo searchCondition) {
        PoOrderVo result = service.querySumByAprefund(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取采购订单集合信息-结算信息")
    @PostMapping("/settle/pagelist")
    public ResponseEntity<JsonResultAo<IPage<PoOrderVo>>> selectOrderListWithSettlePage(@RequestBody(required = false) PoOrderVo searchCondition) {
        IPage<PoOrderVo> list = service.selectOrderListWithSettlePage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("按采购订单合计-结算信息")
    @PostMapping("/settle/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> queryOrderListWithSettlePageSum(@RequestBody(required = false) PoOrderVo searchCondition) {
        PoOrderVo result = service.queryOrderListWithSettlePageSum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @SysLogAnnotion("根据查询条件，获取采购订单信息")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<PoOrderVo>> get(@RequestBody(required = false) PoOrderVo searchCondition) {
        PoOrderVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("采购合同更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<PoOrderVo>> save(@RequestBody(required = false) PoOrderVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，采购订单逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<PoOrderVo>> delete(@RequestBody(required = false) List<PoOrderVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> print(@RequestBody(required = false) PoOrderVo searchCondition) {
        PoOrderVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    @SysLogAnnotion("采购订单，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> cancel(@RequestBody(required = false) PoOrderVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }



    @SysLogAnnotion("采购合同，完成")
    @PostMapping("/finish")
    @ResponseBody
    public ResponseEntity<JsonResultAo<PoOrderVo>> finish(@RequestBody(required = false) PoOrderVo searchCondition) {
        if(service.finish(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) PoOrderVo param, HttpServletResponse response) throws IOException {
        List<PoOrderVo> result = service.selectExportList(param);
        // 创建导出的数据列表
        List<PoOrderExportVo> exportDataList = new ArrayList<>();

        for (PoOrderVo poContractVo : result) {
            List<PoOrderDetailVo> productList = JSON.parseArray(poContractVo.getDetailListData().toString(), PoOrderDetailVo.class);

            for (int i = 0; i < productList.size(); i++) {
                PoOrderDetailVo poOrderDetailVo = productList.get(i);
                PoOrderExportVo poOrderExportVo = new PoOrderExportVo();
                BeanUtils.copyProperties(poContractVo,poOrderExportVo);
                BeanUtils.copyProperties(poOrderDetailVo,poOrderExportVo);
                exportDataList.add(poOrderExportVo);
            }
        }

        List<String> strategy_1 = exportDataList.stream().map(PoOrderExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getSku_code())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("采购订单").head(PoOrderExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 7))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 8))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 9))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 10))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 11))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 12))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 13))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 14))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 15))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 16))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 17))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 18))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 19))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 20))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 21))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 22))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 23))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 24))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 25))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 26))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 27,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 28,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 29,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 30,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 31,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 32,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 33,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 34))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 35))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 36))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 37))
                .build();
        new EasyExcelUtil<>(PoOrderExportVo.class).exportExcel("采购订单" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);
    }


}
