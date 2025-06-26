package com.xinyirun.scm.api.controller.test;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/service/v1/inventory")
@Slf4j
// @Api(tags = "测试入库")
public class TestInventoryController {

    @Autowired
    private ICommonInventoryLogicService iCommonInventoryLogicService;

    @SysLogApiAnnotion("测试入库")
    // @ApiOperation(value = "测试入库")
    @PostMapping("/in")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> in() {

        iCommonInventoryLogicService.updWmsStockByInBill(1);
        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
