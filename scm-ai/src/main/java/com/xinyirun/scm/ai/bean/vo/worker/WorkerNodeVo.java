package com.xinyirun.scm.ai.bean.vo.worker;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作节点业务视图对象
 *
 * 用于业务逻辑处理的工作节点数据传输对象
 * 包含工作节点的详细信息和相关的业务数据
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class WorkerNodeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，自增
     */
    private Integer id;

    /**
     * 主机名
     */
    private String host_name;

    /**
     * 端口
     */
    private String port;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 启动日期
     */
    private LocalDateTime launch_date;

    /**
     * 修改时间
     */
    private LocalDateTime modified;

    /**
     * 创建时间
     */
    private LocalDateTime created;


    /**
     * 创建时间（审计字段）
     */
    private LocalDateTime c_time;

    /**
     * 修改时间（审计字段）
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}