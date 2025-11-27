package com.xinyirun.scm.ai.bean.vo.chat;

import lombok.Data;

/**
 * 节点Token消耗VO
 *
 * <p>用于传输AI Chat执行节点的Token使用情况</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-27
 */
@Data
public class NodeTokenUsageVo {
    /**
     * 输入Token数
     */
    private Long promptTokens;

    /**
     * 输出Token数
     */
    private Long completionTokens;

    /**
     * 总Token数
     */
    private Long totalTokens;
}
