package com.xinyirun.scm.api.controller.report.busniess.socontract;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.SoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.SoContractVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.socontract.ApiSoContractService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/socontract")
public class ApiSoContractController extends SystemBaseController {

    @Autowired
    private ApiSoContractService service;

    @SysLogApiAnnotion("采购合同单据打印")
    @GetMapping("/print")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<SoContractVo>> print(@ModelAttribute SoContractVo vo){
        SoContractVo rtn = service.selectById(vo.getId());
        return ResponseEntity.ok().body(ApiResultUtil.OK(rtn));
    }


    @SysLogAnnotion("获取合同附件打印")
    @GetMapping("/print/license")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFileInfoVo>>> printEnterpriseLicense(@ModelAttribute SoContractVo searchCondition) {
        List<SFileInfoVo> sFileInfoVoList = service.getprintEnterpriseLicense(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(sFileInfoVoList));
    }

    @SysLogApiAnnotion("销售合同商品打印")
    @GetMapping("/print/goods")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SoContractDetailVo>>> printGoods(@ModelAttribute SoContractDetailVo vo){
        List<SoContractDetailVo> soContractDetailVos = service.selectGoodsById(vo.getSo_contract_id());
        return ResponseEntity.ok().body(ResultUtil.OK(soContractDetailVos));
    }

}
