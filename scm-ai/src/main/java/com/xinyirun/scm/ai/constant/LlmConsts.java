/*
 * SCM AI Module - LLM Constants
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team  
 * Description: AI模块相关常量定义
 */
package com.xinyirun.scm.ai.constant;

public class LlmConsts {
    private LlmConsts() {}
    
    // 已上线
    public static final String OLLAMA = "ollama";
    public static final String ZHIPUAI = "zhipuai";
    public static final String DEEPSEEK = "deepseek";
    public static final String DASHSCOPE = "dashscope";
    public static final String SILICONFLOW = "silicon";
    public static final String GITEE = "gitee";
    public static final String TENCENT = "tencent";
    public static final String BAIDU = "baidu";
    public static final String VOLCENGINE = "volcengine";
    public static final String MINIMAX = "minimax";

    // 开发中
    public static final String XINGHUO = "xinghuo";
    public static final String MOONSHOT = "moonshot";
    public static final String BAICHUAN = "baichuan";
    public static final String YI = "yi";
    public static final String STEPFUN = "stepfun";
    public static final String OPENROUTER = "openrouter";
    public static final String GROQ = "groq";
    public static final String ANTHROPIC = "anthropic";
    public static final String OPENAI = "openai";
    public static final String GEMINI = "gemini";
    public static final String AIHUBMIX = "aihubmix";

    // 自定义模型提供商
    public static final String CUSTOM = "custom";

    // 第三方知识库
    public static final String COZE = "coze";
    public static final String DIFY = "dify";
    public static final String N8N = "n8n";
    public static final String MAXKB = "maxkb";
    public static final String RAGFLOW = "ragflow";

    // 默认 智谱AI
    // 默认文字对话模型提供商
    public static final String DEFAULT_TEXT_PROVIDER = ZHIPUAI;
    // 默认文字对话模型
    public static final String DEFAULT_TEXT_MODEL = "glm-4-flash";
    // 默认Vision提供商
    public static final String DEFAULT_VISION_PROVIDER = ZHIPUAI;
    // 默认Vision模型
    public static final String DEFAULT_VISION_MODEL = "llava:latest";
    // 默认Speech提供商
    public static final String DEFAULT_AUDIO_PROVIDER = ZHIPUAI;
    // 默认Speech模型
    public static final String DEFAULT_AUDIO_MODEL = "mxbai-tts:latest";
    // 默认Embedding提供商
    public static final String DEFAULT_EMBEDDING_PROVIDER = ZHIPUAI;
    // 默认Embedding模型
    public static final String DEFAULT_EMBEDDING_MODEL = "embedding-2";
    // 默认Rerank提供商
    public static final String DEFAULT_RERANK_PROVIDER = ZHIPUAI;
    // 默认Rerank模型
    public static final String DEFAULT_RERANK_MODEL = "linux6200/bge-reranker-v2-m3:latest";
    // 默认rewrite提供商
    public static final String DEFAULT_REWRITE_PROVIDER = ZHIPUAI;
    // 默认rewrite模型
    public static final String DEFAULT_REWRITE_MODEL = "glm-4-flash";
    
    // ========== 向量存储相关常量 ==========
    
    /** 知识库UID字段 */
    public static final String VECTOR_KB_UID = "kb_uid";
    
    /** 文件UID字段 */
    public static final String VECTOR_FILE_UID = "file_uid";
    
    /** 启用状态字段 */
    public static final String VECTOR_ENABLED = "enabled";
    
    /** 开始日期字段 */
    public static final String VECTOR_START_DATE = "start_date";
    
    /** 结束日期字段 */
    public static final String VECTOR_END_DATE = "end_date";
    
    /** 文档类型字段 */
    public static final String VECTOR_DOC_TYPE = "doc_type";
    
    /** 来源字段 */
    public static final String VECTOR_SOURCE = "source";
}