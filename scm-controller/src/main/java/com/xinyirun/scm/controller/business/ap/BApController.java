package com.xinyirun.scm.controller.business.ap;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BapExportVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoExportUtilVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.business.ap.IBApService;
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
 * 应付账款管理表（Accounts Payable） 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@RestController
@RequestMapping("/api/v1/ap")
public class BApController {

    @Autowired
    private IBApService service;

    @SysLogAnnotion("应付账款管理,获取业务类型")
    @PostMapping("/gettype")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BApVo>>> getType(@RequestBody(required = false) BApVo searchCondition) {
        List<BApVo> list = service.getType();
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 应付账款管理表  新增
     */
    @PostMapping("/insert")
    @SysLogAnnotion("应付账款管理 新增")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BApVo>> insert(@RequestBody BApVo searchCondition) {
        InsertResultAo<BApVo> resultAo = service.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    @SysLogAnnotion("应付账款管理 更新保存")
    @PostMapping("/save")
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BApVo>> save(@RequestBody(required = false) BApVo searchCondition) {
        if(service.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应付账款管理，获取列表信息")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BApVo>>> selectPagelist(@RequestBody(required = false) BApVo searchCondition) {
        IPage<BApVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("应付账款管理，获取单条数据")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<BApVo>> get(@RequestBody(required = false) BApVo searchCondition) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId())));
    }

    @SysLogAnnotion("应付账款管理，校验")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) BApVo searchCondition) {
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
    public ResponseEntity<JsonResultAo<BApVo>> print(@RequestBody(required = false) BApVo searchCondition) {
        BApVo printInfo = service.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }


    @SysLogAnnotion("应付账款管理，逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BApVo>> delete(@RequestBody(required = false) List<BApVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }


    @SysLogAnnotion("应付账款管理，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApVo>> cancel(@RequestBody(required = false) BApVo searchCondition) {
        if(service.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应付账款管理，中止付款")
    @PostMapping("/suspend_payment")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApVo>> suspend_payment(@RequestBody(required = false) BApVo searchCondition) {
        if(service.suspendPayment(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("应付账款管理，汇总查询")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BApVo>> querySum(@RequestBody(required = false) BApVo searchCondition) {
        BApVo result = service.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    @PostMapping("/export")
    @SysLogAnnotion("应付账款管理 导出")
    public void export(@RequestBody(required = false) BApVo param, HttpServletResponse response) throws IOException {
        List<BApVo> result = service.selectExportList(param);
        // 创建导出的数据列表
        List<BapExportVo> exportDataList = new ArrayList<>();

        for (BApVo poContractVo : result) {
            List<BApSourceAdvanceVo> productList = JSON.parseArray(poContractVo.getPoOrderListData().toString(), BApSourceAdvanceVo.class);
            BApDetailVo bApDetailVos = JSON.parseArray(poContractVo.getBankListData().toString(), BApDetailVo.class).get(0);

            for (int i = 0; i < productList.size(); i++) {
                BApSourceAdvanceVo poOrderDetailVo = productList.get(i);
                BapExportVo poOrderExportVo = new BapExportVo();
                BeanUtils.copyProperties(poContractVo,poOrderExportVo);
                poOrderExportVo.setPo_contract_code(poOrderDetailVo.getPo_contract_code());
                poOrderExportVo.setPo_order_code(poOrderDetailVo.getPo_order_code());
                poOrderExportVo.setTotal_paid_amount(poContractVo.getPaid_amount());
                poOrderExportVo.setTotal_payable_amount(poContractVo.getPayable_amount());
                poOrderExportVo.setTotal_paying_amount(poContractVo.getPaying_amount());
                poOrderExportVo.setTotal_unpay_amount(poContractVo.getUnpay_amount());
                poOrderExportVo.setRemarks(poContractVo.getRemark());

                poOrderExportVo.setAccount_number(bApDetailVos.getAccount_number());
                poOrderExportVo.setAccounts_purpose_type_name(bApDetailVos.getAccounts_purpose_type_name());
                poOrderExportVo.setPayable_amount(bApDetailVos.getPayable_amount());
                poOrderExportVo.setRemark(bApDetailVos.getRemark());
                exportDataList.add(poOrderExportVo);
            }
        }

        List<String> strategy_1 = exportDataList.stream().map(BapExportVo::getCode).collect(Collectors.toList());
        List<BWoExportUtilVo> strategy_2 = exportDataList.stream().map(item -> new BWoExportUtilVo(item.getCode(), item.getAp_id())).collect(Collectors.toList());

        // 写sheet的时候注册相应的自定义合并单元格策略
        WriteSheet writeSheet = EasyExcel.writerSheet("应付账款管理").head(BapExportVo.class)
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
        new EasyExcelUtil<>(BapExportVo.class).exportExcel("应付账款管理" + DateTimeUtil.getDate(), exportDataList, response, writeSheet);
    }

}
