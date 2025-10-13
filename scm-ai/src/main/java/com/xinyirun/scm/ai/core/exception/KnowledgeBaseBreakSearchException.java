package com.xinyirun.scm.ai.core.exception;

import java.io.Serial;

/**
 * 知识库严格模式异常
 *
 * <p>对标aideepin：ErrorEnum.B_BREAK_SEARCH("B0013", "中断搜索")</p>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>严格模式判断点1：用户问题Token过长，无空间检索文档</li>
 *   <li>严格模式判断点2：向量检索结果为空，知识库中无相关答案</li>
 *   <li>严格模式判断点2：图谱检索结果为空，知识库图谱中无相关答案</li>
 * </ul>
 *
 * <p>异常处理：</p>
 * <ul>
 *   <li>该异常由AiExceptionHandler统一捕获</li>
 *   <li>返回HTTP 200（业务规则，非系统错误）</li>
 *   <li>错误消息直接返回给前端展示</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-13
 */
public class KnowledgeBaseBreakSearchException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 知识库UUID
     */
    private final String kbUuid;

    /**
     * 构造函数
     *
     * @param message 异常消息（直接展示给用户）
     * @param kbUuid 知识库UUID（用于日志记录和问题诊断）
     */
    public KnowledgeBaseBreakSearchException(String message, String kbUuid) {
        super(message);
        this.kbUuid = kbUuid;
    }

    /**
     * 获取知识库UUID
     *
     * @return 知识库UUID
     */
    public String getKbUuid() {
        return kbUuid;
    }
}
