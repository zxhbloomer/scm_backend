package com.xinyirun.scm.ai.bean.vo.search;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI搜索请求VO
 * 对齐AIDeepin: AiSearchReq
 *
 * @author SCM-AI团队
 * @since 2025-10-23
 */
@Data
public class AiSearchReqVo {

    /**
     * 搜索文本
     */
    @NotBlank(message = "搜索文本不能为空")
    private String searchText;

    /**
     * 搜索引擎名称
     */
    private String engineName;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 是否简洁搜索
     */
    private boolean briefSearch;

    /**
     * 用户ID(scm-ai特有字段,aideepin从ThreadContext获取)
     */
    private Long userId;
}
