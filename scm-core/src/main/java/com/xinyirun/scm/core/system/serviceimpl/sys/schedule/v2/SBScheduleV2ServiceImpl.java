package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.api.vo.sync.ApiScheduleSyncVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiScheduleVo;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyAveragePriceEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v2.SBDailyAveragePriceV2Mappper;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v2.SBScheduleV2Mapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBScheduleV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.util.List;

/**
 * <p>
 *  物流订单同步service
 * </p>
 * 废弃
 * @author wwl
 * @since 2024-07-02
 */
@Service
public class SBScheduleV2ServiceImpl extends BaseServiceImpl<SBDailyAveragePriceV2Mappper, BDailyAveragePriceEntity> implements ISBScheduleV2Service {

    @Autowired
    private SBScheduleV2Mapper mapper;

    @Autowired
    private WebClient webClient;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Value("${server.port}")
    private int port;

    @Override
    public void createSchedule() {
        createScheduleAll(null, null);
    }

    @Override
    public void createScheduleAll(String parameterClass , String parameter) {
        log.debug("----------------物流订单同步start---------");

        ApiScheduleVo condition = null;
        if (parameterClass == null || parameter == null ) {
            condition = new ApiScheduleVo();
        } else {
            condition = JSON.parseObject(parameter ,ApiScheduleVo.class);
        }
        // 物流订单
        List<ApiScheduleVo> list = mapper.selectScheduleList(condition);
        callAsyncScheduleApiController(list);
        log.debug("----------------物流订单同步end---------");
    }

    /**
     * 调用API接口，同步每日库存
     * @param beans
     */
    private void callAsyncScheduleApiController(List<ApiScheduleVo> beans) {
        log.debug("=============同步每日商品单价start=============");


        ApiScheduleSyncVo asyncVo = new ApiScheduleSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/schedule/sync/all", SystemConstants.APP_CODE.ZT);
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
