package com.xinyirun.scm.mq.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;


/**
 * RabbitTemplate配置类
 *
 * @author zxh
 * @date 2019年 10月19日 15:28:14
 */
//@ConditionalOnProperty(prefix = "spring.rabbitmq", name = "enable", havingValue = "true")
@Configuration
@Slf4j
public class RabbitTemplateConfiguration implements Serializable {

    private static final long serialVersionUID = 8001020656125574343L;

    // rabbitmq服务器地址
    @Value("${spring.rabbitmq.host}")
    private String host;

    // rabbitmq服务器端口号
    @Value("${spring.rabbitmq.port}")
    private int port;

    // rabbitmq登录名称
    @Value("${spring.rabbitmq.username}")
    private String userName;

    // rabbitmq登录密码
    @Value("${spring.rabbitmq.password}")
    private String password;

    // rabbitmq虚拟主机
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    //连接超时时间
    @Value("${spring.rabbitmq.connection-timeout}")
    private Integer connectionTimeout;

    /**
     * NONE值是禁用发布确认模式，是默认值
     * CORRELATED值是发布消息成功到交换器后会触发回调方法，如1示例
     * SIMPLE值经测试有两种效果，其一效果和CORRELATED值一样会触发回调方法，
     * 其二在发布消息成功后使用rabbitTemplate调用waitForConfirms或waitForConfirmsOrDie方法等待broker节点返回发送结果，
     * 根据返回结果来判定下一步的逻辑，要注意的点是waitForConfirmsOrDie方法如果返回false则会关闭channel，则接下来无法发送消息到broker;
     */
    @Value("${spring.rabbitmq.publisher-confirm-type}")
    private CachingConnectionFactory.ConfirmType publisherConfirmType;

    @Value("${spring.rabbitmq.publisher-returns}")
    private boolean publisherReturns;


    // 连接设置数
    @Value("${spring.rabbitmq.cache.connection.size}")
    private int connectionCacheSize;

    // 缓存设置数
    @Value("${spring.rabbitmq.cache.channel.size}")
    private int channelCacheSize;


    /**
     * spring AMQP默认使用CachingConnectionFactory创建一个应用程序共享的连接工厂，也是用途最广泛的ConnectionFactory构建方法
     *  这里改变rabbitmq默认配置
     * 与AMQP通信的工作单元实际上是channel信道，tcp连接可以共享；
     * connectionFactory分为两种模式，一种是缓存channel，一种是缓存connection（同时也缓存该connection的channel），默认是缓存channel的模式
     * 注意：高可用集群场景下（镜像队列），通过负载均衡器连接至集群中不同的实例时，可以通过setCacheMode设置为缓存connection的模式
     *
     * 在缓存connection模式下，不支持自动声明队列、exchange、binding等，
     * rabbitmq-client默认只提供了5个线程处理connection，因此，当connection较多时，应该自定义线程池，并配置到CachingConnectionFactory中
     * 自定义的线程池将会被所有connection共享，建议线程池的最大线程数设置的与预期connection数相等，因为可能存在对于大部分connection都有多个channel的情况
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        log.info("====================连接工厂设置开始，连接地址为：{}====================",host);
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(host);
        cachingConnectionFactory.setPort(port);
        cachingConnectionFactory.setUsername(userName);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setVirtualHost(virtualHost);
        cachingConnectionFactory.setConnectionTimeout(connectionTimeout);
        cachingConnectionFactory.setPublisherConfirmType(publisherConfirmType);
        cachingConnectionFactory.setPublisherReturns(publisherReturns);

        //设置连接工厂缓存模式：CONNECTION
        cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        //设置缓存连接数
        cachingConnectionFactory.setConnectionCacheSize(connectionCacheSize);
        //设置缓存信道数
        cachingConnectionFactory.setChannelCacheSize(channelCacheSize);

        // 打开rabbitmq的消息确认机制(Confirm)
//        cachingConnectionFactory.setPublisherConfirms(true);
        // 打开rabbitmq的消息确认的返回机制(Return)
        cachingConnectionFactory.setPublisherReturns(true);

        log.info("====================连接工厂设置完成，连接地址为：{}====================",host);
        return cachingConnectionFactory;
    }

    @Bean("scm_RabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 修改为false，实现异步发送，不阻塞等待路由确认
        rabbitTemplate.setMandatory(true);
        
        log.info("RabbitTemplate配置为异步发送模式，不阻塞等待路由确认");
        return rabbitTemplate;
    }
}
