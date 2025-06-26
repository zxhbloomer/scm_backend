package com.xinyirun.scm.mqconsumer.base;

import cn.hutool.core.net.url.UrlBuilder;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author Wang Qianfeng
 * @Description TODO
 * @date 2023/2/21 17:02
 */
@Slf4j
@Component
public class BaseMqConsumer {

    @Value("${server.port}")
    private int port;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

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
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }


    /**
     * 拼接中台同步数据url
     * @param uri
     * @return
     */
    protected String getFileUrl(String uri, String url) {
        try {
            String app_key= systemConfigProperies.getApp_key();
            String secret_key = systemConfigProperies.getSecret_key();

            return url + uri + "?app_key="+app_key+"&secret_key="+secret_key;

        } catch (Exception e) {
            log.error("getFileUrl error", e);
        }
        return "";
    }

    protected String getApp_key() {
        return systemConfigProperies.getApp_key();
    }

    /**
     * 设置租户数据源
     */
    protected void setTenantDataSource(MqSenderAo ao) {
        String tenantCode = ao.getTenant_code();
        DataSourceHelper.use(tenantCode);
    }
}
