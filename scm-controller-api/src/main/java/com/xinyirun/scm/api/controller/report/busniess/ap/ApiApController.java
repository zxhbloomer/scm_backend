package com.xinyirun.scm.api.controller.report.busniess.ap;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.ap.ApiApService;
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
@RequestMapping(value = "/api/service/v1/ap")
public class ApiApController extends SystemBaseController {

    @Autowired
    private ApiApService service;

    @SysLogApiAnnotion("应付账款管理-单据打印")
    @GetMapping("/print")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<BApVo>> print(@ModelAttribute BApVo searchCondition){
        BApVo rtn = service.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ApiResultUtil.OK(rtn));
    }


    @SysLogAnnotion("应付账款管理-业务单据信息")
    @GetMapping("/print/po_order")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BApSourceAdvanceVo>>> printPoOrder(@ModelAttribute BApVo searchCondition) {
        List<BApSourceAdvanceVo> poOrderListData = service.printPoOrder(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(poOrderListData));
    }


    @SysLogAnnotion("应付账款管理-付款信息")
    @GetMapping("/print/bank_accounts")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BApDetailVo>>> bankAccounts(@ModelAttribute BApVo searchCondition) {
        List<BApDetailVo> poOrderListData = service.bankAccounts(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(poOrderListData));
    }

}
