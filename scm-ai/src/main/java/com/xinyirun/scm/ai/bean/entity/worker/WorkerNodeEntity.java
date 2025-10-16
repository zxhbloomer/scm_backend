package com.xinyirun.scm.ai.bean.entity.worker;

import com.baomidou.mybatisplus.annotation.*;
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
    private String hostName;

    @TableField("port")
    private String port;

    @TableField("type")
    private Integer type;

    @TableField("launch_date")
    private Long launchDate;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime createTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
