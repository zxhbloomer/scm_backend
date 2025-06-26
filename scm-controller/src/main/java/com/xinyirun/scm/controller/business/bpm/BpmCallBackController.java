package com.xinyirun.scm.controller.business.bpm;

import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.pocontract.IBPoContractService;
import com.xinyirun.scm.core.system.service.business.socontract.IBSoContractService;
import com.xinyirun.scm.core.system.service.master.enterprise.IMEnterpriseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Author:
 * @Description: Bpm 回调接口
 * @CreateTime : 2024/12/31 15:08
 */


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/bpm/callback")
public class BpmCallBackController {

    @Autowired
    private IMEnterpriseService iMEnterpriseService;


    @Autowired
    private IBPoContractService ibPoContractService;

    @Autowired
    private IBSoContractService ibSoContractService;


//    /**
//     *  企业管理审批流程回调
//     *  审批流程通过 更新审核状态通过
//     */
//    @PostMapping("/enterprise/approve")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> bpmCallBackApprove(@RequestBody(required = false) MEnterpriseVo searchCondition) {
//        if(iMEnterpriseService.approveProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }

//    /**
//     *  企业管理审批流程回调
//     * 审批流程拒绝 更新审核状态驳回
//     */
//    @PostMapping("/enterprise/refuse")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> bpmCallBackRefuse(@RequestBody(required = false) MEnterpriseVo searchCondition) {
//        if(iMEnterpriseService.rejectProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }

//    /**
//     *  企业管理审批流程回调
//     *  审批流程撤销 更新审核状态通过
//     */
//    @PostMapping("/enterprise/cancel")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> bpmCancelCallBack(@RequestBody(required = false) MEnterpriseVo searchCondition) {
//        if(iMEnterpriseService.revokeProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }

//    /**
//     *  企业管理审批流程回调
//     *  审批流程更新  更新企业审批人
//     */
//    @PostMapping("/enterprise/save")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> bpmSaveCallBack(@RequestBody(required = false) MEnterpriseVo searchCondition) {
//        if(iMEnterpriseService.processCallBack(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }



//    /**
//     *  采购合同审批流程回调
//     *  审批流程通过 更新审核状态通过
//     */
//    @PostMapping("/pocontract/approve_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<PoContractVo>> poContractApproveAudit(@RequestBody(required = false) PoContractVo searchCondition) {
//        if(ibPoContractService.approveProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  采购合同审批流程回调
//     * 审批流程拒绝 更新审核状态驳回
//     */
//    @PostMapping("/pocontract/refuse_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<PoContractVo>> poContractRefuseAudit(@RequestBody(required = false) PoContractVo searchCondition) {
//        if(ibPoContractService.rejectProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  采购合同审批流程回调
//     *  审批流程撤销 更新审核状态通过
//     */
//    @PostMapping("/pocontract/cancel_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<PoContractVo>> poContractCancelAudit(@RequestBody(required = false) PoContractVo searchCondition) {
//        if(ibPoContractService.revokeProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  采购合同审批流程回调
//     *  审批流程更新  更新审批人
//     */
//    @PostMapping("/pocontract/save_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<PoContractVo>> poContractSaveAudit(@RequestBody(required = false) PoContractVo searchCondition) {
//        if(ibPoContractService.processCallBack(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }




//    /**
//     *  销售合同审批流程回调
//     *  审批流程通过 更新审核状态通过
//     */
//    @PostMapping("/socontract/approve_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<SoContractVo>> soContractApproveAudit(@RequestBody(required = false) SoContractVo searchCondition) {
//        if(ibSoContractService.approveProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  销售合同审批流程回调
//     * 审批流程拒绝 更新审核状态驳回
//     */
//    @PostMapping("/socontract/refuse_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> soContractRefuseAudit(@RequestBody(required = false) SoContractVo searchCondition) {
//        if(ibSoContractService.rejectProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  销售合同审批流程回调
//     *  审批流程撤销 更新审核状态通过
//     */
//    @PostMapping("/socontract/cancel_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> soContractCancelAudit(@RequestBody(required = false) SoContractVo searchCondition) {
//        if(ibSoContractService.revokeProcess(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
//
//    /**
//     *  销售合同审批流程回调
//     *  审批流程更新  更新审批人
//     */
//    @PostMapping("/socontract/save_audit")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MEnterpriseVo>> soContractSaveAudit(@RequestBody(required = false) SoContractVo searchCondition) {
//        if(ibSoContractService.processCallBack(searchCondition).isSuccess()){
//            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
//        } else {
//            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
//        }
//    }
}
