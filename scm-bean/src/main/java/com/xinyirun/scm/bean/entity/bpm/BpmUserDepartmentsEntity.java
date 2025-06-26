package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * 用户部门关系表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_user_departments")
public class BpmUserDepartmentsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -3270074730507733646L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户编号
     */
    @TableField("user_code")
    private String user_code;

    /**
     * 部门编号
     */
    @TableField("dept_code")
    private byte[] dept_code;

    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 租户编号
     */
    @TableField("tenant_code")
    private byte[] tenant_code;


}
