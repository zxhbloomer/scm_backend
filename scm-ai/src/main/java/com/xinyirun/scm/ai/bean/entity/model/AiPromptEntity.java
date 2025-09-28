package com.xinyirun.scm.ai.bean.entity.model;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_prompt")
public class AiPromptEntity implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 编号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("编号")
    private String code;

    /**
     * 简称
     */
    @TableField("nickname")
    @DataChangeLabelAnnotation("简称")
    private String nickname;

    /**
     * 描述
     */
    @TableField("desc")
    @DataChangeLabelAnnotation("描述")
    private String desc;

    /**
     * 提示词类型：1-客服提示词，2-知识库提示词
     */
    @TableField("type")
    @DataChangeLabelAnnotation("提示词类型")
    private Integer type;

    /**
     * 提示词内容，文本格式存储
     */
    @TableField("prompt")
    @DataChangeLabelAnnotation("提示词内容")
    private String prompt;

    private static final long serialVersionUID = 1L;
}