/*
 * SCM AI Module - Custom Chat Client Observation Convention
 * Adapted from ByteDesk AI Module for SCM System
 */
package com.xinyirun.scm.ai.springai.observability;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationContext;
import org.springframework.ai.observation.conventions.AiOperationType;
import org.springframework.ai.observation.conventions.AiProvider;

/**
 * 自定义聊天客户端观察约定
 */
@Slf4j
public class CustomChatClientObservationConvention implements ChatClientObservationConvention {

    // 输出格式的键名常量，替代ChatClientAttributes.OUTPUT_FORMAT.getKey()
    private static final String OUTPUT_FORMAT_KEY = "spring.ai.chat.client.output.format";

    @Override
    public String getName() {
        return "scm.ai.chat.client";
    }

    @Override
    public String getContextualName(ChatClientObservationContext context) {
        return "scm.ai.chat.client." + getModelInfo(context);
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(ChatClientObservationContext context) {
        return KeyValues.of(
                KeyValue.of("model", getModelInfo(context)),
                KeyValue.of("provider", getProviderInfo(context)),
                KeyValue.of("format", getFormatFromContext(context)),
                KeyValue.of("stream", String.valueOf(context.isStream())),
                KeyValue.of("success", String.valueOf(context.getError() == null))
        );
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(ChatClientObservationContext context) {
        return KeyValues.of(
                KeyValue.of("messageCount", String.valueOf(getMessageCount(context))),
                KeyValue.of("operationType", AiOperationType.FRAMEWORK.value()),
                KeyValue.of("errorMessage", context.getError() != null ? context.getError().getMessage() : "")
        );
    }
    
    // 辅助方法，从上下文中获取格式信息，替代废弃的getFormat()方法和ChatClientAttributes类
    private String getFormatFromContext(ChatClientObservationContext context) {
        if (context.getRequest() != null && context.getRequest().context() != null) {
            Object format = context.getRequest().context().get(OUTPUT_FORMAT_KEY);
            if (format instanceof String) {
                return (String) format;
            }
        }
        return "";
    }
    
    // 辅助方法，从上下文中获取模型信息
    private String getModelInfo(ChatClientObservationContext context) {
        // 尝试从操作元数据或请求中获取模型信息
        // 这里使用一个默认值，实际应用中可能需要从请求对象中提取
        return "ollama";
    }
    
    // 辅助方法，从上下文中获取提供商信息
    private String getProviderInfo(ChatClientObservationContext context) {
        // AiOperationMetadata 没有 getProvider() 方法
        // 查看 ChatClientObservationContext 类中的初始化，使用了 AiProvider.SPRING_AI.value()
        return AiProvider.SPRING_AI.value(); // 返回默认值
        // 或者根据需要指定自定义值
        // return "scm-ai-provider";
    }
    
    // 辅助方法，获取消息数量
    private Integer getMessageCount(ChatClientObservationContext context) {
        try {
            if (context.getRequest() != null) {
                Object request = context.getRequest();
                
                // 尝试使用反射获取messages集合
                try {
                    java.lang.reflect.Method getMessagesMethod = request.getClass().getMethod("getMessages");
                    Object messages = getMessagesMethod.invoke(request);
                    if (messages instanceof java.util.Collection) {
                        return ((java.util.Collection<?>) messages).size();
                    }
                } catch (Exception reflectionEx) {
                    // 反射失败，尝试其他方式
                }
                
                // 尝试获取prompt并估算消息数
                try {
                    java.lang.reflect.Method getPromptMethod = request.getClass().getMethod("getPrompt");
                    Object prompt = getPromptMethod.invoke(request);
                    if (prompt != null) {
                        String promptStr = prompt.toString();
                        // 基于prompt内容长度估算消息数
                        return promptStr.length() > 0 ? Math.max(1, promptStr.split("\n").length / 10) : 1;
                    }
                } catch (Exception promptEx) {
                    // 获取prompt失败
                }
                
                // 如果所有尝试都失败，返回默认值
                return 1;
            }
        } catch (Exception e) {
            // 忽略所有异常，使用默认值
            log.debug("获取消息数量时发生异常，使用默认值: {}", e.getMessage());
        }
        return 0;
    }
}