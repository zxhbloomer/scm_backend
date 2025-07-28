package com.xinyirun.scm.controller.business.so.ar;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArExportVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.so.ar.IBArService;
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
 * 应收账款管理表（Accounts Receivable） 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ar")
public class BArController {

    @Autowired
    private IBArService service;

    @SysLogAnnotion("应收账款管理,获取业务类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BArVo>>> getType(@RequestBody(required = false) BArVo searchCondition) {
        List<BArVo> list = service.getType();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 应收账款管理表  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("应收账款管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArVo>> insert(@RequestBody BArVo searchCondition) {
        InsertResultAo<BArVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("应收账款管理 更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BArVo>> save(@RequestBody(required = false) BArVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应收账款管理，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BArVo>>> selectPagelist(@RequestBody(required = false) BArVo searchCondition) {
        IPage<BArVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("应收账款管理，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BArVo>> get(@RequestBody(required = false) BArVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("应收账款管理，校验")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BArVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArVo>> print(@RequestBody(required = false) BArVo searchCondition) {
        BArVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }


    @SysLogAnnotion("应收账款管理，逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BArVo>> delete(@RequestBody(required = false) List<BArVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("应收账款管理，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArVo>> cancel(@RequestBody(required = false) BArVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应收账款管理，中止收款")
    @PostMapping("/suspend_receive")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArVo>> suspend_receive(@RequestBody(required = false) BArVo searchCondition) {
        if(service.suspendReceive(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应收账款管理，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BArVo>> querySum(@RequestBody(required = false) BArVo searchCondition) {
        BArVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("应收账款管理 导出")
    public void export(@RequestBody(required = false) BArVo param, HttpServletResponse response) throws IOException {
        List<BArVo> result = service.selectExportList(param);
        // 创建导出的数据列表
        List<BArExportVo> exportDataList = new ArrayList<>();

        for (BArVo soContractVo : result) {
            List<BArSourceAdvanceVo> productList = JSON.parseArray(soContractVo.getSoOrderListData().toString(), BArSourceAdvanceVo.class);
            BArDetailVo bArDetailVos = JSON.parseArray(soContractVo.getBankListData().toString(), BArDetailVo.class).get(0);

            for (int i = 0; i < productList.size(); i++) {
                BArSourceAdvanceVo soOrderDetailVo = productList.get(i);
                BArExportVo soOrderExportVo = new BArExportVo();
                BeanUtils.copyProperties(soContractVo,soOrderExportVo);
                soOrderExportVo.setSo_contract_code(soOrderDetailVo.getSo_contract_code());
                soOrderExportVo.setSo_order_code(soOrderDetailVo.getSo_order_code());
                soOrderExportVo.setTotal_received_amount(soContractVo.getReceived_amount());
                soOrderExportVo.setTotal_receivable_amount(soContractVo.getReceivable_amount());
                soOrderExportVo.setTotal_receiving_amount(soContractVo.getReceiving_amount());
                soOrderExportVo.setTotal_unreceive_amount(soContractVo.getUnreceive_amount());
                soOrderExportVo.setRemarks(soContractVo.getRemark());

                soOrderExportVo.setAccount_number(bArDetailVos.getAccount_number());
                soOrderExportVo.setReceivable_amount(bArDetailVos.getReceivable_amount());
                soOrderExportVo.setRemark(bArDetailVos.getRemark());
                exportDataList.add(soOrderExportVo);
            }
        }

        List<String> strategy_1 = exportDataList.stream().map(BArExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getAr_id())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("应收账款管理").head(BArExportVo.class)
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 0))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 1))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 2))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 3))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 4))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 5))
                .registerWriteHandler(new CustomMergeStrategy(strategy_1, 6))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 7,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 8,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 9,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 10,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 11,"1"))
                .registerWriteHandler(new CustomMergeStrategy(strategy_2, 12,"1"))
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
                .build();
        new EasyExcelUtil<>(BArExportVo.class).exportExcel("应收账款管理" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);
    }

}