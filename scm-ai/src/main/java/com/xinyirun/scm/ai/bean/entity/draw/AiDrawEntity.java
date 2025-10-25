package com.xinyirun.scm.ai.bean.entity.draw;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI绘图主表实体类
 * 对应数据表：ai_draw
 *
 * 功能说明：存储AI图片生成任务的完整信息，包括提示词、生成参数、结果URL等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_draw")
public class AiDrawEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 绘图UUID(业务主键)
     */
    @TableField("draw_uuid")
    private String drawUuid;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * AI模型ID
     */
    @TableField("ai_model_id")
    private Long aiModelId;

    /**
     * 原始绘图UUID(用于编辑或变体)
     */
    @TableField("original_draw_uuid")
    private String originalDrawUuid;

    /**
     * 正向提示词
     */
    @TableField("prompt")
    private String prompt;

    /**
     * 反向提示词
     */
    @TableField("negative_prompt")
    private String negativePrompt;

    /**
     * 生成尺寸
     */
    @TableField("generate_size")
    private String generateSize;

    /**
     * 生成质量(standard/hd)
     */
    @TableField("generate_quality")
    private String generateQuality;

    /**
     * 生成数量
     */
    @TableField("generate_number")
    private Integer generateNumber;

    /**
     * 生成种子
     */
    @TableField("generate_seed")
    private Long generateSeed;

    /**
     * 交互方式(1-文本生图,2-编辑,3-图生图)
     */
    @TableField("interacting_method")
    private Integer interactingMethod;

    /**
     * 处理状态(1-处理中,2-失败,3-成功)
     */
    @TableField("process_status")
    private Integer processStatus;

    /**
     * 失败原因
     */
    @TableField("fail_remark")
    private String failRemark;

    /**
     * 生成图片URL
     */
    @TableField("img_url")
    private String imgUrl;

    /**
     * 遮罩图片URL
     */
    @TableField("mask_img_url")
    private String maskImgUrl;

    /**
     * 原始图片URL
     */
    @TableField("original_img_url")
    private String originalImgUrl;

    /**
     * 是否公开(0-私有,1-公开)
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 是否带水印(0-不带,1-带)
     */
    @TableField("with_watermark")
    private Boolean withWatermark;

    /**
     * 点赞数
     */
    @TableField("star_count")
    private Integer starCount;

    /**
     * 评论数
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
