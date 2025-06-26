package com.xinyirun.scm.api.controller.busniess.logistics;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.logistics.LogisticsContractVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.api.service.business.v1.logistics.ApiIBLogisticsOrderService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping(value = "/api/service/v1/logistics")
public class ApiLogisticsOrderController extends SystemBaseController {

    @Autowired
    private ApiIBLogisticsOrderService service;

    @SysLogApiAnnotion("查询是否创建物流订单")
    @PostMapping("/contract/relevance")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<LogisticsContractVo>> relevance(@RequestBody LogisticsContractVo vo){
        LogisticsContractVo result = service.isContractRelevance(vo);
        return ResponseEntity.ok().body(ApiResultUtil.OK(result));
    }
}
