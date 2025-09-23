package com.xinyirun.scm.framework.spring.thread;

import com.xinyirun.scm.common.utils.logging.TenantMdcTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadConfig {

    /**
     * 日志异步保存输出线程池
     * 增加MDC上下文传递装饰器，解决多租户日志分离问题
     * @return 返回线程池
     */
    @Bean("logExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(30);
        // 设置最大线程数
        executor.setMaxPoolSize(200);
        // 设置队列容量
        executor.setQueueCapacity(1000);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("logExecutor-");
        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 该方法用来设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。
        executor.setAwaitTerminationSeconds(60);
        
        // ✨ 关键修复：设置MDC上下文传递装饰器
        // 确保异步任务能够继承主线程的租户上下文，实现多租户日志正确分离
        executor.setTaskDecorator(new TenantMdcTaskDecorator());
        
        return executor;
    }
}