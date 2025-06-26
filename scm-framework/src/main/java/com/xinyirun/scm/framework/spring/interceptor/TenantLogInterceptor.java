package com.xinyirun.scm.framework.spring.interceptor;

import com.xinyirun.scm.common.utils.logging.TenantLogContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求中获取租户ID (可能来自请求头、参数或JWT令牌)
        String tenantId = extractTenantId(request);
        // 设置到日志上下文
        TenantLogContextHolder.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                               Object handler, Exception ex) {
        // 请求完成后清理上下文
        TenantLogContextHolder.clear();
    }

    private String extractTenantId(HttpServletRequest request) {
        // 1. 从请求头中获取
        String tenantId = request.getHeader("X-Tenant-ID");

        return tenantId;
    }
}