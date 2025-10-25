package com.xinyirun.scm.ai.bean.vo.draw;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI绘图点赞VO类
 * 对应实体类:AiDrawStarEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiDrawStarVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户UUID
     */
    private String userUuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 绘图ID
     */
    private Long drawId;

    /**
     * 绘图UUID
     */
    private String drawUuid;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;
}
