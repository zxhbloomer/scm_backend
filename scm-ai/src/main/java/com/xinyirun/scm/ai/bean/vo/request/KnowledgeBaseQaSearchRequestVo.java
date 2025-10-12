package com.xinyirun.scm.ai.bean.vo.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 知识库问答记录搜索请求VO
 * 用于查询历史问答记录
 * 对标：aideepin的问答历史查询
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
public class KnowledgeBaseQaSearchRequestVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识库UUID（可选，为空则查询所有知识库）
     */
    private String kbUuid;

    /**
     * 问题关键词（模糊搜索）
     */
    private String questionKeyword;

    /**
     * 答案关键词（模糊搜索）
     */
    private String answerKeyword;

    /**
     * AI模型ID（可选）
     */
    private String aiModelId;

    /**
     * 提问用户ID（可选，为空则查询所有用户）
     */
    private Long userId;

    /**
     * 开始时间（时间戳，毫秒）
     */
    private Long startTime;

    /**
     * 结束时间（时间戳，毫秒）
     */
    private Long endTime;

    /**
     * 启用状态（1-启用，0-禁用，null-全部）
     */
    private Integer enableStatus;

    /**
     * 排序字段
     * 可选值：create_time, prompt_tokens, answer_tokens
     * 默认：create_time
     */
    private String sortField = "create_time";

    /**
     * 排序方向
     * 可选值：ASC, DESC
     * 默认：DESC
     */
    private String sortOrder = "DESC";

    /**
     * 当前页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页数量
     * 默认：20，最大：100
     */
    private Integer pageSize = 20;
}
