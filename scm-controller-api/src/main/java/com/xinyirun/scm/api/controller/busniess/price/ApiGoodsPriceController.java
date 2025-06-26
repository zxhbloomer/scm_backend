package com.xinyirun.scm.api.controller.busniess.price;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiGoodsPriceVo;
import com.xinyirun.scm.bean.system.result.utils.v1.ApiResultUtil;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.core.system.service.business.price.IBGoodsPriceService;
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
 * @author htt
 * @since 2021-10-29
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/service/v1/price")
public class ApiGoodsPriceController extends SystemBaseController {

    @Autowired
    private IBGoodsPriceService service;


    @SysLogApiAnnotion("22、同步商品价格")
    @PostMapping("/sync/new")
    @ResponseBody
    public ResponseEntity<ApiJsonResultAo<String>> sync(@RequestBody List<ApiGoodsPriceVo> list){
        service.syncAll(list);

        return ResponseEntity.ok().body(ApiResultUtil.OK("OK"));
    }
}
