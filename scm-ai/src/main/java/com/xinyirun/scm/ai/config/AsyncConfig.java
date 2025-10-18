package com.xinyirun.scm.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务执行器配置
 *
 * <p>用于RagService的SSE流式问答异步执行</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Configuration
public class AsyncConfig {

    /**
     * 创建异步任务执行器Bean
     *
     * <p>配置说明：</p>
     * <ul>
     *   <li>核心线程数：5 - 常驻线程，处理常规RAG请求</li>
     *   <li>最大线程数：10 - 高并发时最多创建10个线程</li>
     *   <li>队列容量：100 - 超过核心线程数时，任务进入队列等待</li>
     *   <li>线程名前缀：rag-async- - 便于日志追踪和问题定位</li>
     * </ul>
     *
     * @return 配置好的异步任务执行器
     */
    @Bean(name = "ragTaskExecutor")
    public Executor ragTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数（常驻线程）
        executor.setCorePoolSize(5);

        // 最大线程数（高峰期最多创建的线程数）
        executor.setMaxPoolSize(10);

        // 任务队列容量（超过核心线程数时，任务进入队列）
        executor.setQueueCapacity(100);

        // 线程名称前缀（便于日志追踪）
        executor.setThreadNamePrefix("rag-async-");

        // 线程空闲时间（秒）超过此时间，非核心线程会被回收
        executor.setKeepAliveSeconds(60);

        // 拒绝策略：由调用线程处理（CallerRunsPolicy）
        // 当队列满且线程数达到最大时，由提交任务的线程执行
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 最大等待时间（秒）
        executor.setAwaitTerminationSeconds(60);

        // 初始化线程池
        executor.initialize();

        return executor;
    }
}
