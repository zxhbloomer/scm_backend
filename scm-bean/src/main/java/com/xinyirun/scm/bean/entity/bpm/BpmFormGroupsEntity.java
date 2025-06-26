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
 * form_groups
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_form_groups")
public class BpmFormGroupsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5422541414160444059L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组名
     */
    @TableField("group_name")
    private String group_name;

    /**
     * 排序号
     */
    @TableField("sort_num")
    private Integer sort_num;

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


}
