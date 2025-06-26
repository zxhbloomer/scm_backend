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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_forms")
public class BpmFormsEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -7910589581357061069L;
    /**
     * 表单ID
     */
    @TableId("form_id")
    private String form_id;

    /**
     * 表单名称
     */
    @TableField("form_name")
    private String form_name;

    /**
     * 图标配置
     */
    @TableField("logo")
    private String logo;

    /**
     * 设置项
     */
    @TableField("settings")
    private String settings;

    /**
     * 表单设置内容
     */
    @TableField("form_items")
    private String form_items;

    /**
     * 流程设置内容
     */
    @TableField("process")
    private String process;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @TableField("sort")
    private Integer sort;

    @TableField("is_delete")
    private Boolean is_delete;

    /**
     * 0 正常 1=停用 2=已删除
     */
    @TableField("is_stop")
    private Boolean is_stop;

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
