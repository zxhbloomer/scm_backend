package com.xinyirun.scm.common.utils.redis;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 *
 * 调用：boolean result = RedisLockTool.tryGetDistributedLock();
 *      boolean result = RedisLockTool.releaseDistributedLock();
 *
 * @author zxh
 */
@Component
@Slf4j
public final class RedisLockUtil {

    private static final Long SUCCESS = 1L;
    public static final String LOCK_SCRIPT_STR = "if redis.call('set',KEYS[1],ARGV[1],'EX',ARGV[2],'NX') then return 1 else return 0 end";
    public static final String UNLOCK_SCRIPT_STR = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    //default value
    // 默认的过期时间（秒）
    public static final Integer DEFAULT_EXPIRE_SECOND = 10;

    // 默认的循环尝试次数
    public static final Long DEFAULT_LOOP_TIMES = 5L;

    // 默认的睡眠间隔（毫秒）
    public static final Long DEFAULT_SLEEP_INTERVAL = 500L;

    // 用于分割类名的字符
    public static final String PACKAGE_NAME_SPLIT_STR = "\\.";

    // 类名和方法名的连接字符
    public static final String CLASS_AND_METHOD_CONCAT_STR = "->";

    private static RedisTemplate redisTemplate;

    @Autowired
    public void RedisLockUtil(RedisTemplate redisTemplate) {
        RedisLockUtil.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取分布式锁，使用调用者的类名作为key
     * 默认key：调用者类名
     *
     * @return
     * @throws InterruptedException
     */
    public static boolean tryGetDistributedLock() {
        String callerKey = getCurrentThreadCaller();
        String requestId = String.valueOf(Thread.currentThread().getId());
        return tryGetDistributedLock(callerKey, requestId);
    }

    /**
     * 尝试获取分布式锁，需要指定key和requestId
     * @param lockKey   锁名称
     * @param requestId 随机请求id
     * @return
     * @throws InterruptedException
     */
    public static boolean tryGetDistributedLock(String lockKey, String requestId) {
        return tryGetDistributedLock(lockKey, requestId, DEFAULT_EXPIRE_SECOND);
    }

    /**
     * 尝试获取分布式锁，可以设置锁的超时时间
     * @param lockKey      key
     * @param requestId    随机请求id
     * @param expireSecond 超时秒
     * @return
     * @throws InterruptedException
     */
    public static boolean tryGetDistributedLock(String lockKey, String requestId, Integer expireSecond) {
        return tryGetDistributedLock(lockKey, requestId, expireSecond, DEFAULT_LOOP_TIMES, DEFAULT_SLEEP_INTERVAL);
    }


    /**
     * 尝试获取分布式锁，可以设置循环次数和睡眠间隔
     *
     * @param lockKey       key
     * @param requestId     随机请求id
     * @param expireSecond  超时秒
     * @param loopTimes     循环次数
     * @param sleepInterval 等待间隔（毫秒）
     * @return
     */
    public static boolean tryGetDistributedLock(String lockKey, String requestId, Integer expireSecond, Long loopTimes, Long sleepInterval) {
        log.debug("======================尝试获取分布式锁, 开始================================");
        log.debug("lockKey         : " + lockKey);
        log.debug("requestId       : " + requestId);
        log.debug("expireSecond    : " + expireSecond);
        log.debug("loopTimes       : " + loopTimes);
        log.debug("sleepInterval   : " + sleepInterval);

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LOCK_SCRIPT_STR, Long.class);
        int times = 0;
        while (times++ <= loopTimes) {
            log.debug("第 {} 次获取锁, requestId: {}  : ", times, requestId);
            Object result = redisTemplate.execute(redisScript, Lists.newArrayList(lockKey), requestId, expireSecond);
            if (SUCCESS.equals(result)) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(sleepInterval);
            } catch (InterruptedException e) {
                log.error("tryGetDistributedLock error:", e);
            }
            continue;
        }
        log.debug("======================尝试获取分布式锁, 结束================================");
        return false;
    }


    /**
     * 释放分布式锁，使用调用者的类名作为key
     *
     * @return
     */
    public static boolean releaseDistributedLock() {
        String callerKey = getCurrentThreadCaller();
        String requestId = String.valueOf(Thread.currentThread().getId());
        return releaseDistributedLock(callerKey, requestId);
    }

    /**
     * 释放分布式锁，需要指定key和requestId
     *
     * @param lockKey   key
     * @param requestId 加锁的请求id
     * @return
     */
    public static boolean releaseDistributedLock(String lockKey, String requestId) {
        log.debug("======================释放分布式锁, 开始================================");
        log.debug("lockKey         : " + lockKey);
        log.debug("requestId       : " + requestId);
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT_STR, Long.class);
        Object result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        if (SUCCESS.equals(result)) {
            return true;
        }
        return false;
    }

    /**
     * 获取类的简短名称（不包含包名）
     * @param className
     * @return
     */
    private static String getSimpleClassName(String className) {
        String[] splits = className.split(PACKAGE_NAME_SPLIT_STR);
        return splits[splits.length - 1];
    }

    /**
     * 获取当前线程的调用者（类名和方法名）
     *
     * @return
     */
    private static String getCurrentThreadCaller() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        return getSimpleClassName(stackTraceElement.getClassName()) + CLASS_AND_METHOD_CONCAT_STR + stackTraceElement.getMethodName();
    }
}