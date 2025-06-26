package com.xinyirun.scm.bean.entity.master.org;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织机构关系表
 * </p>
 *
 * @author zxh
 * @since 2020-01-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_staff_org")
public class MStaffOrgEntity implements Serializable {

    private static final long serialVersionUID = 4616614733644481896L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工主表id
     */
    @TableField("staff_id")
    private Long staff_id;

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
