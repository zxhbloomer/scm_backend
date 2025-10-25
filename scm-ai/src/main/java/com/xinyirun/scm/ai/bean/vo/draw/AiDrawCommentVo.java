package com.xinyirun.scm.ai.bean.vo.draw;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI绘图评论VO类
 * 对应实体类:AiDrawCommentEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@Builder
public class AiDrawCommentVo {

    /**
     * 评论UUID(业务主键)
     */
    private String commentUuid;

    /**
     * 用户UUID
     */
    private String userUuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 绘图UUID
     */
    private String drawUuid;

    /**
     * 评论内容
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;
}
