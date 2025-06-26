package com.xinyirun.scm.mq.rabbitmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * MQ自定义配置
 */
@ConfigurationProperties(prefix = "spring.rabbitmq.custom")
public class MQProperties {

    /**
     * 生产者
     */
    private Producer producer;

    /**
     * 消费者
     */
    private Consumer consumer;

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public static class Producer {

        /**
         * 是否开启生产者
         */
        private Boolean hasOpen = false;

        /**
         * MQ默认交换机
         */
        private String defaultExchange;

        /**
         * MQ默认队列
         */
        private String defaultQueue;

        /**
         * MQ默认路由关键字
         */
        private String defaultRoutingKey;

        public Boolean getHasOpen() {
            return hasOpen;
        }

        public void setHasOpen(Boolean hasOpen) {
            this.hasOpen = hasOpen;
        }

        public String getDefaultExchange() {
            return defaultExchange;
        }

        public void setDefaultExchange(String defaultExchange) {
            this.defaultExchange = defaultExchange;
        }

        public String getDefaultQueue() {
            return defaultQueue;
        }

        public void setDefaultQueue(String defaultQueue) {
            this.defaultQueue = defaultQueue;
        }

        public String getDefaultRoutingKey() {
            return defaultRoutingKey;
        }

        public void setDefaultRoutingKey(String defaultRoutingKey) {
            this.defaultRoutingKey = defaultRoutingKey;
        }
    }

    public static class Consumer {

        /**
         * 是否开启生产者
         */
        private Boolean hasOpen = false;

        /**
         * MQ默认队列
         */
        private String defaultQueue;

        public Boolean getHasOpen() {
            return hasOpen;
        }

        public void setHasOpen(Boolean hasOpen) {
            this.hasOpen = hasOpen;
        }

        public String getDefaultQueue() {
            return defaultQueue;
        }

        public void setDefaultQueue(String defaultQueue) {
            this.defaultQueue = defaultQueue;
        }
    }
}
