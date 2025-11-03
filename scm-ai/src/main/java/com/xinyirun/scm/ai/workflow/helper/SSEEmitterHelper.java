package com.xinyirun.scm.ai.workflow.helper;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * SSE连接管理器 - 实现并发控制和限流
 *
 * @author zxh
 * @since 2025-10-28
 */
@Slf4j
@Component
public class SSEEmitterHelper {

    @Resource
    private RedisUtil redisUtil;

    // Redis Key常量
    private static final String USER_ASKING_KEY = "ai:workflow:user:asking:%s";  // 用户是否正在请求
    private static final String USER_REQUEST_COUNT_KEY = "ai:workflow:user:request:count:%s";  // 用户请求次数

    // 限流配置 - 每小时最多100次请求
    private static final int MAX_REQUEST_PER_HOUR = 100;
    private static final int RATE_LIMIT_MINUTES = 60;

    // Guava Cache缓存已完成的SSE
    private static final Cache<SseEmitter, Boolean> COMPLETED_SSE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    /**
     * 检查用户是否可以发起新请求,如果不可以则完成SSE并返回false
     *
     * @param userId 用户ID
     * @param sseEmitter SSE发射器
     * @return true-可以请求, false-不可以请求
     */
    public boolean checkOrComplete(Long userId, SseEmitter sseEmitter) {
        String userIdStr = userId.toString();

        // 1. 检查是否正在请求(并发控制)
        String askingKey = String.format(USER_ASKING_KEY, userIdStr);
        boolean isAsking = redisUtil.checkKeyExisted(askingKey);
        if (isAsking) {
            sendErrorAndComplete(userId, sseEmitter, "您有正在进行的请求,请稍后再试");
            return false;
        }

        // 2. 检查请求次数限制(限流)
        String countKey = String.format(USER_REQUEST_COUNT_KEY, userIdStr);
        String countStr = redisUtil.getString(countKey);
        int count = StringUtils.isBlank(countStr) ? 0 : Integer.parseInt(countStr);
        if (count >= MAX_REQUEST_PER_HOUR) {
            sendErrorAndComplete(userId, sseEmitter, "请求过于频繁,请稍后再试");
            return false;
        }

        return true;
    }

    /**
     * 开始SSE流式传输,设置Redis标记
     *
     * @param userId 用户ID
     * @param sseEmitter SSE发射器
     */
    public void startSse(Long userId, SseEmitter sseEmitter) {
        this.startSse(userId, sseEmitter, null);
    }

    /**
     * 开始SSE流式传输,设置Redis标记,可携带数据
     *
     * @param userId 用户ID
     * @param sseEmitter SSE发射器
     * @param data 携带的数据（可选）
     */
    public void startSse(Long userId, SseEmitter sseEmitter, String data) {
        String userIdStr = userId.toString();

        // 设置正在请求标记,30分钟过期
        String askingKey = String.format(USER_ASKING_KEY, userIdStr);
        redisUtil.set(askingKey, "1", 30 * 60);  // 30分钟 = 1800秒

        // 增加请求计数
        String countKey = String.format(USER_REQUEST_COUNT_KEY, userIdStr);
        long expireTime = redisUtil.getExpire(countKey);
        if (expireTime == -1) {
            // 键不存在或无过期时间,设置初始值
            redisUtil.set(countKey, "1", RATE_LIMIT_MINUTES * 60);
        } else if (expireTime > 3) {
            // 过期时间充足,递增计数
            redisUtil.increase(countKey);
        }

        // 发送START事件，可携带数据
        try {
            SseEmitter.SseEventBuilder builder = SseEmitter.event().name("start");
            if (StringUtils.isNotBlank(data)) {
                builder.data(data);
            }
            sseEmitter.send(builder);
        } catch (IOException e) {
            log.error("startSse error", e);
            sseEmitter.completeWithError(e);
            COMPLETED_SSE.put(sseEmitter, Boolean.TRUE);
            delSseRequesting(userId);
        }
    }

    /**
     * 完成SSE,清理Redis状态
     *
     * @param userId 用户ID
     * @param sseEmitter SSE发射器
     */
    public void sendComplete(Long userId, SseEmitter sseEmitter) {
        // 检查是否已完成
        if (Boolean.TRUE.equals(COMPLETED_SSE.getIfPresent(sseEmitter))) {
            log.warn("sseEmitter already completed,userId:{}", userId);
            delSseRequesting(userId);
            return;
        }

        try {
            // 发送DONE事件
            sseEmitter.send(SseEmitter.event().name("done"));
            sseEmitter.complete();
        } catch (Exception e) {
            log.warn("sendComplete error", e);
        } finally {
            // 标记为已完成并清理Redis
            COMPLETED_SSE.put(sseEmitter, Boolean.TRUE);
            delSseRequesting(userId);
        }
    }

    /**
     * 发送错误信息并完成SSE
     *
     * @param userId 用户ID
     * @param sseEmitter SSE发射器
     * @param errorMsg 错误消息
     */
    public void sendErrorAndComplete(Long userId, SseEmitter sseEmitter, String errorMsg) {
        // 检查是否已完成
        if (Boolean.TRUE.equals(COMPLETED_SSE.getIfPresent(sseEmitter))) {
            log.warn("sseEmitter already completed,ignore error:{}", errorMsg);
            delSseRequesting(userId);
            return;
        }

        try {
            // 发送ERROR事件
            sseEmitter.send(SseEmitter.event().name("error").data(errorMsg != null ? errorMsg : ""));
        } catch (IOException e) {
            log.warn("sendErrorAndComplete userId:{},errorMsg:{}", userId, errorMsg);
            throw new RuntimeException(e);
        } finally {
            // 标记为已完成并清理Redis
            COMPLETED_SSE.put(sseEmitter, Boolean.TRUE);
            delSseRequesting(userId);
            sseEmitter.complete();
        }
    }

    /**
     * 删除用户请求标记
     *
     * @param userId 用户ID
     */
    private void delSseRequesting(Long userId) {
        String askingKey = String.format(USER_ASKING_KEY, userId.toString());
        redisUtil.delete(askingKey);
    }
}
