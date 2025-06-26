package com.xinyirun.scm.controller.business.price;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.api.vo.sync.ApiBMaterialPriceVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiMaterialPriceSyncVo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.price.BMaterialPriceVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.service.business.price.IBMaterialPriceService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMaterialPriceV2Service;
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
 *  前端控制器
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-15
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/materialprice")
public class BMaterialPriceController extends SystemBaseController {

    @Autowired
    private IBMaterialPriceService service;

    @Autowired
    private ISBMaterialPriceV2Service isbMaterialPriceV2Service;

    @PostMapping("/page_list")
    @SysLogAnnotion("根据查询条件查询列表")
    public ResponseEntity<JsonResultAo<IPage<BMaterialPriceVo>>> list(@RequestBody(required = false) BMaterialPriceVo searchCondition) {
        IPage<BMaterialPriceVo> list = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @GetMapping("/sync_all")
    @SysLogAnnotion("商品价格全部同步")
    public ResponseEntity<String> syncAll(@RequestBody(required = false) BMaterialPriceVo searchCondition) {
//        service.syncAll(searchCondition);
        List<ApiBMaterialPriceVo> list = service.getMaterialPriceList();
        callAsyncMaterialPriceApiController(list);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/sync")
    @SysLogAnnotion("商品价格同步")
    public ResponseEntity<String> sync(@RequestBody(required = false) List<BMaterialPriceVo> searchCondition) {
//        service.sync(searchCondition);
        isbMaterialPriceV2Service.createMaterialPrice(null, null);
        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("/recreate")
    @SysLogAnnotion("重新生成")
    public ResponseEntity<String> reCreate() {
        isbMaterialPriceV2Service.createMaterialPrice(null, null);
        return ResponseEntity.ok().body("OK");
    }

    /**
     * 调用API接口，同步每日库存
     * @param beans
     */
    private void callAsyncMaterialPriceApiController(List<ApiBMaterialPriceVo> beans) {
        log.debug("=============同步每日商品单价start=============");


        ApiMaterialPriceSyncVo asyncVo = new ApiMaterialPriceSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/material/price/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);


        log.debug("=============同步每日商品单价end=============");
    }

    private void callback(String result) {
        log.debug(result);
    }

}
