package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 审批流抄送表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_cc")
public class BpmCcEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4701633378390487963L;

    /**
     * 主键
     */
    @TableId("id")
    private String id;

    /**
     * 用户编号，与租户编号一起唯一
     */
    @TableField("user_code")
    private String user_code;

    /**
     * 租户编号
     */
    @TableField("tenant_code")
    private byte[] tenant_code;

    /**
     * 流程实例id
     */
    @TableField("process_instance_id")
    private String process_instance_id;

    /**
     * 节点id
     */
    @TableField("node_id")
    private String node_id;

    /**
     * 节点名称
     */
    @TableField("node_name")
    private String node_name;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

}
