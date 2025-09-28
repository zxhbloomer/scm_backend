package com.xinyirun.scm.ai.bean.entity.worker;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

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
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("host_name")
    @DataChangeLabelAnnotation("主机名")
    private String host_name;

    @TableField("port")
    @DataChangeLabelAnnotation("端口")
    private String port;

    @TableField("type")
    @DataChangeLabelAnnotation("类型")
    private Integer type;

    @TableField("launch_date")
    @DataChangeLabelAnnotation("启动日期")
    private Long launch_date;

    @TableField("modified")
    @DataChangeLabelAnnotation("修改时间")
    private Long modified;

    @TableField("created")
    @DataChangeLabelAnnotation("创建时间")
    private Long created;

    @TableField("dbversion")
    private Integer dbversion;
}
