package com.xinyirun.scm.common.utils.logging;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 支持异步日志上下文传递的工具类
 * 
 * 在多线程或异步任务中保持租户上下文
 * 
 * @author 
 */
public final class MDCTaskDecorator {

    private MDCTaskDecorator() {
        // 工具类，私有构造函数
    }
    
    /**
     * 获取当前线程的MDC上下文快照
     * @return MDC上下文映射
     */
    public static Map<String, String> getCopyOfContextMap() {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return contextMap != null ? new HashMap<>(contextMap) : new HashMap<>();
    }
    
    /**
     * 装饰Runnable，使其在执行时包含当前线程的MDC上下文
     * @param runnable 要执行的Runnable
     * @param contextMap MDC上下文映射
     * @return 装饰后的Runnable
     */
    public static Runnable wrap(Runnable runnable, Map<String, String> contextMap) {
        return () -> {
            Map<String, String> previousContext = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                if (previousContext != null) {
                    MDC.setContextMap(previousContext);
                } else {
                    MDC.clear();
                }
            }
        };
    }
    
    /**
     * 装饰Callable，使其在执行时包含当前线程的MDC上下文
     * @param <V> Callable返回类型
     * @param callable 要执行的Callable
     * @param contextMap MDC上下文映射
     * @return 装饰后的Callable
     */
    public static <V> Callable<V> wrap(Callable<V> callable, Map<String, String> contextMap) {
        return () -> {
            Map<String, String> previousContext = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                return callable.call();
            } finally {
                if (previousContext != null) {
                    MDC.setContextMap(previousContext);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}
