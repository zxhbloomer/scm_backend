package com.xinyirun.scm.security.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * security配置文件读取
 */
@EnableConfigurationProperties({SystemSecurityProperties.class})
@ConfigurationProperties(prefix = "scm.security")
@Component
public class SystemSecurityProperties {
    /** session过期后自动获取 */
    private boolean createNewSession;
    /** 登录 URL */
    private String loginUrl;
    /** 免认证静态资源路径 */
    private String anonResourcesUrl;
    /** 记住我超时时间 */
    private int rememberMeTimeout;
    /** 登出 URL */
    private String logoutUrl;
    /** 主页 URL */
    private String indexUrl;
    /** 开发者模式，可以跳过验证码 */
    private Boolean developModel;
    /** 错误日志输出简易模式 */
    private Boolean logSampleModel;


    @Value("${scm.security.session.maximum-sessions}")
    private Integer MAX_SESSIONS;

    public Integer getMAX_SESSIONS() {
        return MAX_SESSIONS;
    }

    public Boolean getLogSampleModel() {
        return logSampleModel;
    }

    public void setLogSampleModel(Boolean logSampleModel) {
        this.logSampleModel = logSampleModel;
    }
    public Boolean getDevelopModel() {
        return developModel;
    }

    public void setDevelopModel(Boolean developModel) {
        this.developModel = developModel;
    }



    private ValidateCodeProperties code = new ValidateCodeProperties();

//    private SocialProperties social = new SocialProperties();

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getAnonResourcesUrl() {
        return anonResourcesUrl;
    }

    public void setAnonResourcesUrl(String anonResourcesUrl) {
        this.anonResourcesUrl = anonResourcesUrl;
    }

    public ValidateCodeProperties getCode() {
        return code;
    }

    public void setCode(ValidateCodeProperties code) {
        this.code = code;
    }

    public int getRememberMeTimeout() {
        return rememberMeTimeout;
    }

    public void setRememberMeTimeout(int rememberMeTimeout) {
        this.rememberMeTimeout = rememberMeTimeout;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIndexUrl(String indexUrl) {
        this.indexUrl = indexUrl;
    }


    public boolean getCreateNewSession() {
        return createNewSession;
    }

    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }

}
