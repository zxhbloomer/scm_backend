package com.xinyirun.scm.ai.config.workflow;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务执行器配置
 *
 * <p>提供多个异步执行器用于不同业务场景：</p>
 * <ul>
 *   <li>ragTaskExecutor: RAG流式问答</li>
 *   <li>imagesExecutor: AI绘图任务异步执行</li>
 *   <li>mainExecutor: 通用异步任务(如网页抓取)</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Configuration
public class AsyncConfig {

    /**
     * RAG任务执行器
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

    /**
     * AI绘图任务执行器
     *
     * <p>用于DrawService的异步图片生成任务</p>
     * <p>配置说明：</p>
     * <ul>
     *   <li>核心线程数：3 - 绘图任务较重，核心线程数较少</li>
     *   <li>最大线程数：5 - 控制并发绘图数量，避免资源耗尽</li>
     *   <li>队列容量：50 - 绘图请求排队等待</li>
     *   <li>线程名前缀：images-async- - 便于区分绘图任务</li>
     * </ul>
     *
     * @return 绘图任务执行器
     */
    @Bean(name = "imagesExecutor")
    public Executor imagesExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("images-async-");
        executor.setKeepAliveSeconds(120);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        executor.initialize();
        return executor;
    }

    /**
     * 通用异步任务执行器
     *
     * <p>用于SearchService的网页抓取等通用异步任务</p>
     * <p>配置说明：</p>
     * <ul>
     *   <li>核心线程数：10 - 网页抓取并发量大</li>
     *   <li>最大线程数：20 - 高并发时支持更多线程</li>
     *   <li>队列容量：200 - 较大队列容量支持批量任务</li>
     *   <li>线程名前缀：main-async- - 通用任务标识</li>
     * </ul>
     *
     * @return 通用任务执行器
     */
    @Bean(name = "mainExecutor")
    public Executor mainExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("main-async-");
        executor.setKeepAliveSeconds(60);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
