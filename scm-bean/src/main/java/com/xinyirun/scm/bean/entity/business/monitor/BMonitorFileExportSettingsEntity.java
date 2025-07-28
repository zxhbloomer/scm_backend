package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 监管任务文件导出 配置
 *
 * @author xinyirun
 * @since 2023-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_monitor_file_export_settings")
public class BMonitorFileExportSettingsEntity implements Serializable {

    private static final long serialVersionUID = 338308243229713866L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 员工id
     */
    @TableField("staff_id")
    private Long staff_id;

    /**
     * 配置
     */
    @TableField("config_json")
    private String config_json;

    /**
     * 0=监管任务 1=直销直采
     */
    @TableField("type")
    private String type;

    /**
     * 创建人
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;


}
