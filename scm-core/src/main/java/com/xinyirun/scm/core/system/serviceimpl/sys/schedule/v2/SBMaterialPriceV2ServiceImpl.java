package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import cn.hutool.core.net.url.UrlBuilder;
import com.xinyirun.scm.bean.api.vo.sync.ApiBMaterialPriceVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiMaterialPriceSyncVo;
import com.xinyirun.scm.bean.entity.busniess.price.BMaterialPriceEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v2.SBMaterialPriceV2Mappper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMaterialPriceV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.List;

/**
 * <p>
 *  每日平均单价
 * </p>
 * 废弃
 * @author wwl
 * @since 2022-03-21
 */
@Service
public class SBMaterialPriceV2ServiceImpl extends BaseServiceImpl<SBMaterialPriceV2Mappper, BMaterialPriceEntity> implements ISBMaterialPriceV2Service {

    @Autowired
    SBMaterialPriceV2Mappper mapper;

    @Autowired
    public WebClient webClient;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Value("${server.port}")
    private int port;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMaterialPrice(String parameterClass, String parameter) {
        log.debug("----------------每日商品单价start---------");

        // 删除数据
        // b_material_daily_price 保存每日数据
        mapper.deleteIntradayData00();
        // b_material_price 只保留最新数据
        mapper.deleteData01();

        /**
         *   插入转换后物料单价数据 b_material_convert_price
         *   source：转换前  target：转换后
          */
        mapper.insertMaterialPrice10();

        /**
         *   插入转换前物料单价数据 b_material_convert_price
         *   source：转换前  target：转换前
         */
        mapper.insertMaterialPrice11();

        /**
         * 原材料,辅料单价 b_material_convert_price
         *  跑批单价
         */
        mapper.insertMaterialPrice12();

        /**
         * 每日商品价格 b_material_daily_price
         * 不为转换数据，则取大宗商品单价
         *  为转换数据，则取转换后单价
         */
        mapper.insertMaterialPrice13();

        /**
         * 最新商品价格 b_material_price
         * 不为转换数据，则取大宗商品单价
         *  为转换数据，则取转换后单价
         */
        mapper.insertMaterialPrice14();

        // 同步每日商品价格
        List<ApiBMaterialPriceVo> list = mapper.getMaterialPriceList();
        callAsyncMaterialPriceApiController(list);

        log.debug("----------------每日商品单价start---------");
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

    /**
     * 拼接中台同步数据url
     * @param uri
     * @param appCode
     * @return
     */
    private String getBusinessCenterUrl(String uri, String appCode) {
        try {
            SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppCode(appCode);
            String app_key= sAppConfigEntity.getApp_key();
            String secret_key = sAppConfigEntity.getSecret_key();
            String host = InetAddress.getLocalHost().getHostAddress();

            String url = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .addPath(uri)
                    .addQuery("app_key", app_key)
                    .addQuery("secret_key", secret_key)
                    .build();
            return url.replaceAll("%2F", "/");
        } catch (Exception e) {
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }

    private void callback(String result) {
        log.debug(result);
    }

}
