package com.xinyirun.scm.api.controller.busniess.borroworder;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.borroworder.ApiBBorrowOrderVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.borroworder.ApiIBBorrowOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 借货指令同步
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/borrow/order")
public class ApiBorrowOrderController extends SystemBaseController {

    @Autowired
    private ApiIBBorrowOrderService service;

    @SysLogApiAnnotion("借货指令同步")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiBBorrowOrderVo> list){
        service.sync(list);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
