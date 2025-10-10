package com.xinyirun.scm.ai.bean.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建对话会话请求VO（RAG专用）
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class CreateConversationRequestVo {

    /**
     * 所属知识库UUID
     */
    @NotBlank(message = "知识库UUID不能为空")
    private String kb_uuid;

    /**
     * 对话标题
     */
    private String title;
}
