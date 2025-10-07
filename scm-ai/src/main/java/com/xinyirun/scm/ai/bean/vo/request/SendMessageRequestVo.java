package com.xinyirun.scm.ai.bean.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送消息请求VO（RAG问答）
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class SendMessageRequestVo {

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 知识库UUID（用于RAG检索）
     */
    private String kb_uuid;
}
