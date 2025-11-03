package com.xinyirun.scm.ai.workflow.node.keywordextractor;

/**
 * 工作流关键词提取节点提示词
 *
 * 功能：
 * - 生成关键词提取的提示词
 * - 指导LLM提取重要关键词
 * - 输出逗号分隔的关键词列表
 *
 * @author zxh
 * @since 2025-10-27
 */
public class KeywordExtractorPrompt {

    /**
     * 生成关键词提取提示词
     *
     * @param topN 提取前N个关键词
     * @param userQuestion 用户输入的问题/文本
     * @return 格式化的提示词
     */
    public static String getPrompt(int topN, String userQuestion) {
        return """
                - Role: You're a question analyzer.
                - Requirements:
                  - Summarize user's question, and give top %d important keyword.
                  - Use comma as a delimiter to separate keywords.
                - Answer format: (in language of user's question)
                - keyword example: a, b, c

                ### User question: %s
                """.formatted(topN, userQuestion);
    }
}
