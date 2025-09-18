package com.xinyirun.scm.security.properties;
import lombok.Data;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jovan
 * @create 2019/8/5
 */
@Configuration
@Data
public class WxOpenConfig {
    /**
     * appid
     */
    @Value("${scm.wx.open.config.appid}")
    private String appid;

    /**
     * app secret
     */
    @Value("${scm.wx.open.config.secret}")
    private String secret;

    @Bean
    public WxMpService wxMpService() {
        WxMpService service = new WxMpServiceImpl();

        WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
        configStorage.setAppId(appid);
        configStorage.setSecret(secret);

        service.setWxMpConfigStorage(configStorage);
        return service;
    }
}
