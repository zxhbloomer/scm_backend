package com.xinyirun.scm.ai.bean.entity.worker;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作节点实体类
 * 对应数据表：worker_node
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("worker_node")
public class WorkerNodeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -7052095200615436463L;    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("host_name")
    @DataChangeLabelAnnotation("主机名")
    private String hostName;

    @TableField("port")
    @DataChangeLabelAnnotation("端口")
    private String port;

    @TableField("type")
    @DataChangeLabelAnnotation("类型")
    private Integer type;

    @TableField("launch_date")
    @DataChangeLabelAnnotation("启动日期")
    private Long launchDate;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime createTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime updateTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
