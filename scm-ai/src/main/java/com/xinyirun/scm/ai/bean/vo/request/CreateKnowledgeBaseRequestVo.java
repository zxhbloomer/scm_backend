package com.xinyirun.scm.ai.bean.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建知识库请求VO
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class CreateKnowledgeBaseRequestVo {

    /**
     * 知识库标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 是否公开（true-公开，false-私有）
     */
    @NotNull(message = "is_public不能为空")
    private Boolean is_public;
}
