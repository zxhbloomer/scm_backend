package com.xinyirun.scm.ai.bean.entity.model;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_prompt")
public class AiPromptEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6571905350596439442L;
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


    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}