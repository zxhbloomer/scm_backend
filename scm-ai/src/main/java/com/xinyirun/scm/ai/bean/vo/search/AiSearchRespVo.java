package com.xinyirun.scm.ai.bean.vo.search;

import lombok.Data;

import java.util.List;

/**
 * AI搜索响应VO
 * 对齐AIDeepin: AiSearchResp
 *
 * @author SCM-AI团队
 * @since 2025-10-23
 */
@Data
public class AiSearchRespVo {

    /**
     * 最小ID(用于增量查询)
     */
    private Long minId;

    /**
     * 搜索记录列表
     */
    private List<AiSearchRecordVo> records;
}
