package com.xinyirun.scm.ai.core.util;

import com.knuddels.jtokkit.api.EncodingType;
import com.xinyirun.scm.ai.bean.vo.rag.InputAdaptorMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;

/**
 * Token计算工具类
 *
 * <p>使用O200K_BASE编码（2025标准，200K词汇量）</p>
 * <p>适配现代OpenAI兼容模型（DeepSeek、GPT-4o等）</p>
 *
 * <p>对标aideepin：</p>
 * <ul>
 *   <li>aideepin使用：langchain4j的TokenCountEstimator（OpenAiTokenCountEstimator、HuggingFaceTokenCountEstimator）</li>
 *   <li>scm-ai使用：Spring AI的JTokkitTokenCountEstimator（O200K_BASE编码）</li>
 * </ul>
 *
 * <p>O200K_BASE vs CL100K_BASE：</p>
 * <ul>
 *   <li>O200K_BASE：200K词汇量，GPT-4o/o1系列，2025主流标准，更高效</li>
 *   <li>CL100K_BASE：100K词汇量，GPT-3.5/GPT-4，逐渐淘汰</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-13
 */
@Slf4j
public class TokenCalculator {

    /**
     * Token估算器（使用O200K_BASE编码）
     *
     * <p>技术说明：</p>
     * <ul>
     *   <li>JTokkitTokenCountEstimator内部使用tiktoken库（OpenAI官方tokenizer）</li>
     *   <li>O200K_BASE：200K词汇量，GPT-4o/o1系列，2025主流标准</li>
     *   <li>静态初始化，全局复用，线程安全</li>
     * </ul>
     */
    private static final JTokkitTokenCountEstimator TOKEN_ESTIMATOR =
        new JTokkitTokenCountEstimator(EncodingType.O200K_BASE);

    /**
     * 估算文本的token数量
     *
     * <p>对标aideepin方法：TokenCountEstimator.estimateTokenCountInText()</p>
     *
     * @param text 要计算的文本
     * @return token数量（0表示空文本）
     */
    public static int estimateTokenCount(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        try {
            return TOKEN_ESTIMATOR.estimate(text);
        } catch (Exception e) {
            log.error("Token估算失败，text长度: {}", text.length(), e);
            // 降级方案：按平均每4个字符1个token估算（英文和中文平均）
            return text.length() / 4;
        }
    }

    /**
     * 验证用户问题是否有效（未超过最大输入token限制）
     *
     * <p>对标aideepin方法：InputAdaptor.isQuestionValid()</p>
     * <p>对应aideepin判断点1：用户问题Token过长检查</p>
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>RAG问答前：验证用户问题是否超过模型输入限制</li>
     *   <li>严格模式：问题过长时直接返回错误，不调用LLM</li>
     *   <li>宽松模式：问题过长时仍调用LLM，但无法检索知识库</li>
     * </ul>
     *
     * @param question 用户问题
     * @param maxInputTokens 模型的最大输入token数（从ai_model_source.max_input_tokens获取）
     * @return 验证结果消息（包含token数量和超限标志）
     */
    public static InputAdaptorMsg isQuestionValid(String question, int maxInputTokens) {
        InputAdaptorMsg result = new InputAdaptorMsg();

        // 计算问题的token数量
        int questionTokens = estimateTokenCount(question);
        result.setUserQuestionTokenCount(questionTokens);

        // 判断是否超过限制
        if (questionTokens > maxInputTokens) {
            result.setTokenTooMuch(InputAdaptorMsg.TOKEN_TOO_MUCH_QUESTION);
            log.warn("用户问题Token过长：questionTokens={}, maxInputTokens={}",
                questionTokens, maxInputTokens);
        } else {
            result.setTokenTooMuch(InputAdaptorMsg.TOKEN_TOO_MUCH_NOT);
            log.debug("用户问题Token验证通过：questionTokens={}, maxInputTokens={}",
                questionTokens, maxInputTokens);
        }

        return result;
    }
}
