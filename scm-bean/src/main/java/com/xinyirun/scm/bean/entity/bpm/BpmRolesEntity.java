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
 * 标签表
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_roles")
public class BpmRolesEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -9041601546517360125L;

    @TableId(value = "label_id", type = IdType.AUTO)
    private Integer label_id;

    /**
     * 标签ID
     */
    @TableField("label_name")
    private String label_name;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;


}
