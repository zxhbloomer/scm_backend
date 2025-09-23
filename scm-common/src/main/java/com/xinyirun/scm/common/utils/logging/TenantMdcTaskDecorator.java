package com.xinyirun.scm.common.utils.logging;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * 租户MDC上下文传递装饰器
 * 用于在异步线程中传递租户上下文信息，解决多租户日志分离问题
 * 
 * @author 系统生成
 */
public class TenantMdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // 获取当前线程的MDC上下文（包含租户ID等信息）
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String currentTenantId = TenantLogContextHolder.getTenantId();
        
        return () -> {
            try {
                // 在新线程中恢复完整的MDC上下文
                if (contextMap != null && !contextMap.isEmpty()) {
                    MDC.setContextMap(contextMap);
                }
                
                // 双重保险：确保租户ID正确设置
                if (currentTenantId != null) {
                    TenantLogContextHolder.setTenantId(currentTenantId);
                }
                
                // 执行原始任务
                runnable.run();
            } finally {
                // 清理MDC上下文，避免线程池复用时的内存泄露
                MDC.clear();
            }
        };
    }
}