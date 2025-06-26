package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 审批流实例-摘要
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_instance_summary")
public class BpmInstanceSummaryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 331626545845078038L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审批编号
     */
    @TableField("process_code")
    private String processCode;

    /**
     * 摘要数据
     */
    @TableField("summary")
    private String summary;

    /**
     * 流程名，业务定义：如（新增企业审批）
     */
    @TableField("process_definition_business_name")
    private String process_definition_business_name;
}
