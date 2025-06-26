package com.xinyirun.scm.api.controller.busniess.out;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBOutOrderVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.out.ApiIBOutOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 添加入库计划
 * </p>
 *
 * @author wwl
 * @since 2022-03-08
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/out/order")
public class ApiOutOrderController extends SystemBaseController {

    @Autowired
    private ApiIBOutOrderService service;

    @SysLogApiAnnotion("24、同步销售合同")
    @PostMapping("/sync")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> syncAll(@RequestBody List<ApiBOutOrderVo> list){
        service.sync(list);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
