package com.xinyirun.scm.bean.entity.master.org;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 部门主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_dept")
public class MDeptEntity implements Serializable {

    private static final long serialVersionUID = 6070916954377938613L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 全称
     */
    @TableField("name")
    private String name;

    /**
     * 简称
     */
    @TableField("simple_name")
    private String simple_name;

    /**
     * 部门主管
     */
    @TableField("handler_id")
    private Long handler_id;

    /**
     * 部门副主管
     */
    @TableField("sub_handler_id")
    private Long sub_handler_id;

    /**
     * 上级主管领导
     */
    @TableField("leader_id")
    private Long leader_id;

    /**
     * 上级分管领导
     */
    @TableField("response_leader_id")
    private Long response_leader_id;

    /**
     * 说明
     */
    @TableField("descr")
    private String descr;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 租户id
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

}
