package com.xinyirun.scm.common.properies;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zxh
 */
@ConfigurationProperties(prefix = "wms.config")
public class SystemConfigProperies {

    private boolean simpleModel;

    private boolean logSaveDb;

    private boolean logPrint;

    private boolean sysLog;

    private boolean operateLog;

    private boolean operateLogAll;

    /**
     * 上传url
     */
    private String fsUrl;

    /**
     * 文件备份 webclient baseurl 设置
     */
    private String fsWebclientBaseurl;

    /**
     *  本系统环境
     */
    private String env;

    /**
     *  本系统域名
     */
    private String domainName;

    private String app_key;
    private String secret_key;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public boolean isSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(boolean simpleModel) {
        this.simpleModel = simpleModel;
    }

    public boolean isLogSaveDb() {
        return logSaveDb;
    }

    public void setLogSaveDb(boolean logSaveDb) {
        this.logSaveDb = logSaveDb;
    }

    public boolean isLogPrint() {
        return logPrint;
    }

    public void setLogPrint(boolean logPrint) {
        this.logPrint = logPrint;
    }

    public boolean isSysLog() {
        return sysLog;
    }

    public void setSysLog(boolean sysLog) {
        this.sysLog = sysLog;
    }

    public String getFsUrl() {
        return fsUrl;
    }

    public void setFsUrl(String fsUrl) {
        this.fsUrl = fsUrl;
    }

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public int getRedisCacheExpiredMin() {
        return redisCacheExpiredMin;
    }

    public void setRedisCacheExpiredMin(int redisCacheExpiredMin) {
        this.redisCacheExpiredMin = redisCacheExpiredMin;
    }

    public boolean isOperateLog() {
        return operateLog;
    }

    public void setOperateLog(boolean operateLog) {
        this.operateLog = operateLog;
    }

    public boolean isOperateLogAll() {
        return operateLogAll;
    }

    public void setOperateLogAll(boolean operateLogAll) {
        this.operateLogAll = operateLogAll;
    }

    public String getFsWebclientBaseurl() {
        return fsWebclientBaseurl;
    }

    public void setFsWebclientBaseurl(String fsWebclientBaseurl) {
        this.fsWebclientBaseurl = fsWebclientBaseurl;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    /**
     * redis 过期时间
     */
    private int redisCacheExpiredMin = 30;


}
