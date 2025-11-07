package com.xinyirun.scm.redis.config;

import com.alibaba.fastjson2.JSONFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class RedisCacheAutoConfiguration extends CachingConfigurerSupport {

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    /**
     * 自定义缓存管理器
     * 实现有效期、自定义key前缀、序列化方法
     *
     * @param lettuceConnectionFactory
     * @param jackson2JsonRedisSerializer
     * @return
     */
    @Bean
    public CacheManager cacheManager(
        @Qualifier("scm_lettuce_connection_factory") LettuceConnectionFactory lettuceConnectionFactory,
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

        RedisSerializationContext.SerializationPair keyPair =
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
        RedisSerializationContext.SerializationPair valuePair =
            RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);

        //以锁写入的方式创建RedisCacheWriter对象
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(lettuceConnectionFactory);

        int redisCacheExpiredMin = systemConfigProperies.getRedisCacheExpiredMin();
        RedisCacheConfiguration cacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(redisCacheExpiredMin)).serializeKeysWith(keyPair)
                .serializeValuesWith(valuePair);

        RedisCacheManager redisCacheManager = new RedisCacheManager(writer, cacheConfiguration);

        return redisCacheManager;

    }

    /**
     * 配置一个序列器, 将对象序列化为字符串存储, 和将对象反序列化为对象
     * 并注入到cacheManager和redisTemplate
     */
    @Bean
    public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 注册JavaTimeModule以支持Java 8日期时间类型（LocalDateTime等）
        om.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }

    /**
     * redisTemplate 操作模板，value 实现Jackson2JsonRedisSerializer
     *
     * @param lettuceConnectionFactory
     * @param jackson2JsonRedisSerializer
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(
        @Qualifier("scm_lettuce_connection_factory") LettuceConnectionFactory lettuceConnectionFactory,
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        //        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        // 全局开启AutoType，不建议使用
        // ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // 建议使用这种方式，小范围指定白名单
//        ParserConfig.getGlobalInstance().addAccept("com.xinyirun.scm.");
        JSONFactory.getDefaultObjectReaderProvider().addAutoTypeAccept("com.xinyirun.scm.");

        // 设置键（key）的序列化采用StringRedisSerializer。
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置值（value）的序列化采用FastJsonRedisSerializer。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        // Hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash value序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ChannelTopic expiredTopic() {
        ChannelTopic ot = new ChannelTopic("__keyevent@0__:expired");
        return ot;
    }

    /**
     * redis增加监听
     * @param
     * @return
     */
    @Bean("scm_redis_listener_container")
    RedisMessageListenerContainer keyExpirationListenerContainer(
        @Qualifier("scm_lettuce_connection_factory") LettuceConnectionFactory lettuceConnectionFactory) {

        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(lettuceConnectionFactory);
        return listenerContainer;
    }

    /**
     * 处理程序连接不上redis时或者超时连接，程序自动查询数据库，不抛异常
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {

        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("handleCacheGetError error", e);
                log.error("缓存key:【{}】出现异常:{}", key, e);

            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {

                log.error("handleCachePutError error", e);
                log.error("缓存key:【{}】加入出现异常:{}", key, e);

            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {

                log.error("handleCacheEvictError error", e);
                log.error("缓存key:【{}】置失效出现异常:{}", key, e);

            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {

                log.error("handleCacheClearError error", e);
                log.error("清除缓存出现异常:{}", e);

            }
        };
        return cacheErrorHandler;
    }

}
