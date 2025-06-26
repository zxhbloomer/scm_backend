package com.xinyirun.scm.controller.business.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDailyInventorySyncVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiMaterialConvertPriceSyncVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
//import com.xinyirun.scm.core.api.service.business.v1.inventory.ApiInventoryService;
import com.xinyirun.scm.core.system.service.business.inventory.IBDailyInventoryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>
 * 每日库存表 前端控制器
 * </p>
 *
 * @author wwl
 * @since 2022-01-23
 */
@Slf4j
// @Api(tags = "每日库存表")
@RestController
@RequestMapping(value = "/api/v1/inventory")
public class BDailyInventoryController extends SystemBaseController {

    @Autowired
    private IBDailyInventoryService service;

//    @Autowired
//    private ApiInventoryService apiInventoryService;

    @SysLogAnnotion("根据查询条件，获取每日入库信息")
    @PostMapping("/daily/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<BDailyInventoryVo>>> list(@RequestBody(required = false) BDailyInventoryVo searchCondition) {
//        IPage<BDailyInventoryVo> list = service.selectPage(searchCondition);
        IPage<BDailyInventoryVo> list = service.selectPageNew(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

//    @SysLogAnnotion("根据查询条件，获取每日入库信息")
//    @GetMapping("/daily/sync")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> sync() {
//        List<ApiDailyInventoryVo> apiDailyInventoryVos = apiInventoryService.getDailyInventory();
//
//
//        callAsyncDailyInventoryApiController(apiDailyInventoryVos);
//
//        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
//    }
//
//    @SysLogAnnotion("同步物料转换商品价格")
//    @GetMapping("/price/sync")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<String>> syncPrice() {
//
//        List<ApiMaterialConvertPriceVo> apiMaterialConvertPriceVos = apiInventoryService.getMaterialConvertPrice();
//
//        callAsyncMaterialConvertPriceApiController(apiMaterialConvertPriceVos);
//
//        return ResponseEntity.ok().body(ResultUtil.OK("ok"));
//    }



    /**
     * 调用API接口，同步每日库存
     * @param beans
     */
    private void callAsyncDailyInventoryApiController(List<ApiDailyInventoryVo> beans) {
        log.debug("=============同步每日库存start=============");


        ApiDailyInventorySyncVo asyncVo = new ApiDailyInventorySyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/inventory/daily/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);




        log.debug("=============同步每日库存end=============");
    }

    /**
     * 调用API接口，同步物料转换单价
     * @param beans
     */
    private void callAsyncMaterialConvertPriceApiController(List<ApiMaterialConvertPriceVo> beans) {
        log.debug("=============同步物料转换单价start=============");

        ApiMaterialConvertPriceSyncVo asyncVo = new ApiMaterialConvertPriceSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/materialconvert/price/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步物料转换单价end=============");
    }

    private void callback(String result) {
        log.debug(result);
    }

}
