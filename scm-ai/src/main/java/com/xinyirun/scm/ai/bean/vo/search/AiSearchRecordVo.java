package com.xinyirun.scm.ai.bean.vo.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI搜索记录VO类
 * 对应实体类:AiSearchRecordEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AiSearchRecordVo {

    /**
     * 搜索UUID(业务主键)
     */
    private String searchUuid;

    /**
     * 搜索问题
     */
    private String question;

    /**
     * 搜索引擎响应
     */
    private Map<String, Object> searchEngineResponse;

    /**
     * LLM提示词
     */
    private String prompt;

    /**
     * 提示词token数
     */
    private Integer promptTokens;

    /**
     * LLM回答
     */
    private String answer;

    /**
     * 回答token数
     */
    private Integer answerTokens;

    /**
     * 用户UUID
     */
    private String userUuid;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;

    /**
     * AI模型ID
     */
    private Long aiModelId;

    /**
     * AI模型平台
     */
    private String aiModelPlatform;
}
