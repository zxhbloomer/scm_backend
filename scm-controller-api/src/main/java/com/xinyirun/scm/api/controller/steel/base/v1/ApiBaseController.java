package com.xinyirun.scm.api.controller.steel.base.v1;

import cn.hutool.core.net.url.UrlBuilder;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

/**
 * controller父类
 * 
 * @author zhangxh
 */
@Slf4j
@Component
public class ApiBaseController {

    @Value("${server.port}")
    private int port;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Autowired
    protected RestTemplate restTemplate;

    /**
     * 拼接中台同步数据url
     * @param uri
     * @param appCode
     * @return
     */
    protected String getBusinessCenterUrl(String uri, String appCode) {
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
            log.error("getBusinessCenterUrl error", e.getMessage());
        }
        return "";
    }
}
