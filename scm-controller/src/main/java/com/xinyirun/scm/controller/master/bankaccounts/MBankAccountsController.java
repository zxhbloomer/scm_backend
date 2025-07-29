package com.xinyirun.scm.controller.master.bankaccounts;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsExportVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsTypeVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.core.system.service.master.bankaccounts.IMBankAccountsService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 企业银行账户表 前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@RestController
@RequestMapping("/api/v1/bank/accounts")
public class MBankAccountsController {

    @Autowired
    private IMBankAccountsService service;

    @SysLogAnnotion("企业银行账户，获取分页列表")
    @PostMapping("/pagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MBankAccountsVo>>> pageList(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        IPage<MBankAccountsVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }


    @SysLogAnnotion("企业银行账户，弹窗获取分页列表")
    @PostMapping("/dialogpagelist")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MBankAccountsVo>>> dialogpageList(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        IPage<MBankAccountsVo> list = service.dialogpageList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("企业银行账户，数据更新保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> insert(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        if(service.insert(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存失败。");
        }
    }

    @SysLogAnnotion("企业银行账户，订单校验")
    @PostMapping("/validate")
    @ResponseBody
    public ResponseEntity<JsonResultAo<String>> checkLogic(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        CheckResultAo checkResultAo = service.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    @SysLogAnnotion("企业银行账户，数据更新保存")
    @PostMapping("/update")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> update(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        if(service.update(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存失败。");
        }
    }


    @SysLogAnnotion("根据查询条件，获取企业银行账户")
    @PostMapping("/get")
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> get(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        MBankAccountsVo vo = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("企业银行账户，逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> delete(@RequestBody(required = false) List<MBankAccountsVo> searchCondition) {
        if(service.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("企业银行账户，银行账户启用/禁用")
    @PostMapping("/update_status")
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> updateStatus(@RequestBody(required = false) List<MBankAccountsVo> searchCondition) {
        if(service.updateStatus(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("根据查询条件，获取企业银行默认账户")
    @PostMapping("/get_purchaser")
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> getPurchaser(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        MBankAccountsVo vo = service.getPurchaser(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取销售方企业银行默认账户")
    @PostMapping("/get_seller")
    public ResponseEntity<JsonResultAo<MBankAccountsVo>> getSeller(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        MBankAccountsVo vo = service.getSeller(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取款项类型")
    @PostMapping("/get_bankType")
    public ResponseEntity<JsonResultAo<List<MBankAccountsTypeVo>>> getBankType(@RequestBody(required = false) MBankAccountsTypeVo searchCondition) {
        List<MBankAccountsTypeVo> vo = service.getBankType(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("企业银行账户，获取银行收款账户下拉")
    @PostMapping("/get_bank_collection")
    public ResponseEntity<JsonResultAo<List<MBankAccountsVo>>> getBankCollection(@RequestBody(required = false) MBankAccountsVo searchCondition) {
        List<MBankAccountsVo> vo = service.getBankCollection(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @PostMapping("/export")
    @SysLogAnnotion("导出")
    public void export(@RequestBody(required = false) MBankAccountsVo searchCondition, HttpServletResponse response) throws IOException {
        // 创建导出的数据列表
        List<MBankAccountsExportVo> list = service.selectExportList(searchCondition);
        EasyExcelUtil<MBankAccountsExportVo> util = new EasyExcelUtil<>(MBankAccountsExportVo.class);
        util.exportExcel("银行账户管理" + DateTimeUtil.getDate(), "银行账户管理", list, response);
    }


}
