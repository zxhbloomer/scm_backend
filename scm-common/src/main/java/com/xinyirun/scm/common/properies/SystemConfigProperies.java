package com.xinyirun.scm.common.properies;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zxh
 */
@ConfigurationProperties(prefix = "scm.config")
public class SystemConfigProperies {

    private boolean simpleModel;

    private boolean logSaveDb;

    private boolean logPrint;

    private boolean sysLog;

    private boolean operateLog;

    private boolean operateLogAll;

    /**
     *  本系统环境
     */
    private String env;

    /**
     *  本系统域名
     */
    private String domainName;

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
