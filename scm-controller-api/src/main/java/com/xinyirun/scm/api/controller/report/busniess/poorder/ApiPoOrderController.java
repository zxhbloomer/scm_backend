package com.xinyirun.scm.api.controller.report.busniess.poorder;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.poorder.ApiPoOrderService;
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
@RequestMapping(value = "/api/service/v1/poorder")
public class ApiPoOrderController extends SystemBaseController {

    @Autowired
    private ApiPoOrderService service;

    @SysLogApiAnnotion("采购订单单据打印")
    @GetMapping("/print")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<PoOrderVo>> print(@ModelAttribute PoOrderVo vo){
        PoOrderVo rtn = service.selectById(vo.getId());
        return ResponseEntity.ok().body(ApiResultUtil.OK(rtn));
    }


    @SysLogAnnotion("获取合同附件打印")
    @GetMapping("/print/license")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<SFileInfoVo>>> printEnterpriseLicense(@ModelAttribute PoOrderVo searchCondition) {
        List<SFileInfoVo> sFileInfoVoList = service.getprintEnterpriseLicense(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(sFileInfoVoList));
    }

    @SysLogApiAnnotion("采购订单商品打印")
    @GetMapping("/print/goods")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<PoOrderDetailVo>>> printGoods(@ModelAttribute PoOrderDetailVo vo){
        List<PoOrderDetailVo> poContractDetailVos = service.selectGoodsById(vo.getPo_order_id());
        return ResponseEntity.ok().body(ResultUtil.OK(poContractDetailVos));
    }

}
