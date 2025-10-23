package com.xinyirun.scm.ai.bean.vo.draw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI绘图VO类
 * 对应实体类:AiDrawEntity
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
public class AiDrawVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 绘图UUID(业务主键)
     */
    private String drawUuid;

    /**
     * 绘图提示词
     */
    private String prompt;

    /**
     * 负面提示词
     */
    private String negativePrompt;

    /**
     * AI模型名称
     */
    private String aiModelName;

    /**
     * 生成尺寸
     */
    private String generateSize;

    /**
     * 交互方式(1-文本生图,2-编辑,3-图生图)
     */
    private Integer interactingMethod;

    /**
     * 是否公开(0-否,1-是)
     */
    private Integer isPublic;

    /**
     * 点赞数量
     */
    private Integer starCount;

    /**
     * 处理状态(1-处理中,2-失败,3-成功)
     */
    private Integer processStatus;

    /**
     * 状态描述
     */
    private String processStatusRemark;

    /**
     * 生成的图片(JSON格式,内部存储)
     */
    @JsonIgnore
    private String generatedImages;

    /**
     * 用户ID(内部字段)
     */
    @JsonIgnore
    private Long userId;

    /**
     * 动态参数(JSON格式)
     */
    private JsonNode dynamicParams;

    /**
     * 创建时间
     */
    private LocalDateTime cTime;

    // 非实体字段,用于前端展示

    /**
     * AI模型平台
     */
    private String aiModelPlatform;

    /**
     * 是否点赞
     */
    private Boolean isStar;

    /**
     * 原始图片UUID
     */
    private String originalImageUuid;

    /**
     * 原始图片URL
     */
    private String originalImageUrl;

    /**
     * 蒙版图片UUID
     */
    private String maskImageUuid;

    /**
     * 蒙版图片URL
     */
    private String maskImageUrl;

    /**
     * 用户UUID
     */
    private String userUuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 生成的图片UUID列表
     */
    private List<String> imageUuids;

    /**
     * 生成的图片URL列表
     */
    private List<String> imageUrls;
}
