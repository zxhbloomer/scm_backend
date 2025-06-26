package com.xinyirun.scm.common.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 租户日志上下文持有者
 * 用于管理特定租户的日志上下文
 * 
 * @author 
 */
public class TenantLogContextHolder {
    
    private static final Logger log = LoggerFactory.getLogger(TenantLogContextHolder.class);
    private static final String TENANT_ID_KEY = "tenant_code";
    
    /**
     * 设置租户ID到MDC，用于分离租户日志
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = "default";
        }
        MDC.put(TENANT_ID_KEY, tenantId);
        log.debug("设置租户日志上下文: {}", tenantId);
    }
    
    /**
     * 获取当前MDC中的租户ID
     * @return 当前租户ID，若未设置则返回null
     */
    public static String getTenantId() {
        return MDC.get(TENANT_ID_KEY);
    }
    
    /**
     * 清除租户日志上下文
     */
    public static void clear() {
        MDC.remove(TENANT_ID_KEY);
        log.debug("清除租户日志上下文");
    }
    
    /**
     * 使用指定的租户ID执行操作
     * 执行完毕后恢复原来的租户ID
     * 
     * @param tenantId 临时租户ID
     * @param runnable 要执行的操作
     */
    public static void withTenant(String tenantId, Runnable runnable) {
        String previousTenant = getTenantId();
        try {
            setTenantId(tenantId);
            runnable.run();
        } finally {
            if (previousTenant != null) {
                setTenantId(previousTenant);
            } else {
                clear();
            }
        }
    }
}
