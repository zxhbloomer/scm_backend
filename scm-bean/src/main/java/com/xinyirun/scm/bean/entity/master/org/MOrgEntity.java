package com.xinyirun.scm.bean.entity.master.org;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 组织主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_org")
public class MOrgEntity implements Serializable {

    private static final long serialVersionUID = -6491320122254160352L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 上级组织，null为根结点
     */
    @TableField("parent_id")
    private Long parent_id;

    /**
     * 租户id，根结点
     */
//    @TableField("tenant_id")
//    private Long tenant_id;

    /**
     * 关联单号
     */
    @TableField("serial_id")
    private Long serial_id;

    /**
     * 关联单号类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 编号，00010001..
     */
    @TableField("code")
    private String code;

    /**
     * 儿子个数
     */
    @TableField("son_count")
    private Integer son_count;

    /**
     * 类型：10（租户）、20（集团）、30（公司）、40（部门）、50（岗位）、60（人员）
     */
    @TableField("type")
    private String type;

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
