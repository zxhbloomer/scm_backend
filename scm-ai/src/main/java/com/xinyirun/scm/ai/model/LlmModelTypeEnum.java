/*
 * SCM AI Module - LLM Model Type Enum
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: 定义LLM模型类型的枚举类
 */
package com.xinyirun.scm.ai.model;

/**
 * LLM模型类型枚举
 * 定义系统支持的各种AI模型类型
 */
public enum LlmModelTypeEnum {
    TEXT("text", "文本对话模型"),
    EMBEDDING("embedding", "向量嵌入模型"),
    RERANK("rerank", "重排序模型"),
    VISION("vision", "视觉理解模型"),
    CODE("code", "代码生成模型"),
    REASONING("reasoning", "推理模型"),
    FUNCTION("function", "函数调用模型"),
    TEXT2IMAGE("text2image", "文本生成图像模型"),
    IMAGE2TEXT("image2text", "图像转文本模型"),
    AUDIO2TEXT("audio2text", "语音转文本模型"),
    TEXT2AUDIO("text2audio", "文本转语音模型"),
    TEXT2VIDEO("text2video", "文本生成视频模型"),
    VIDEO2TEXT("video2text", "视频理解模型");

    private final String type;
    private final String description;

    LlmModelTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据类型字符串获取枚举值
     * 
     * @param type 类型字符串
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果类型字符串无效
     */
    public static LlmModelTypeEnum fromType(String type) {
        for (LlmModelTypeEnum modelType : LlmModelTypeEnum.values()) {
            if (modelType.getType().equalsIgnoreCase(type)) {
                return modelType;
            }
        }
        throw new IllegalArgumentException("未知的模型类型: " + type);
    }

    /**
     * 检查是否为有效的模型类型
     * 
     * @param type 类型字符串
     * @return 是否有效
     */
    public static boolean isValidType(String type) {
        try {
            fromType(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}