package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_departments")
public class BpmDepartmentsEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 6039331305598312964L;
    /**
     * key
     */
    @TableId("id")
    private Integer id;

    /**
     * 部门编号
     */
    @TableField("code")
    private String code;

    /**
     * 部门名
     */
    @TableField("dept_name")
    private String dept_name;

    /**
     * 部门主管
     */
    @TableField("leader")
    private String leader;

    /**
     * 父部门编号
     */
    @TableField("parent_code")
    private String parent_code;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;

    /**
     * 租户编号
     */
    @TableField("tenant_code")
    private byte[] tenant_code;


}
