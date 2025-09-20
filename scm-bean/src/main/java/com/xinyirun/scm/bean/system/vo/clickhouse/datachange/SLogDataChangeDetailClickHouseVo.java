package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;

import com.xinyirun.scm.bean.entity.mongo.log.datachange.SLogDataChangeDetailMongoEntity;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 数据变更详细日志表 VO
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogDataChangeDetailClickHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -737597325091147930L;

    private String id;

    /**
     * 操作业务名：entity的注解名称
     */
    private String name;

    /**
     * 数据操作类型：UPDATE|INSERT|DELETE
     */
    private String type;

    /**
     * SQL命令类型
     */
    private String sql_command_type;

    /**
     * 数据库表名
     */
    private String table_name;

    /**
     * 对应的实体类名
     */
    private String entity_name;

    /**
     * 单号
     */
    private String order_code;

    /**
     * 调用策略模式的数据变更类名
     */
    private String class_name;

    /**
     * 具体的变更前、变更后的数据
     */
    private List<SLogDataChangeDetailOldNewVo> details;

    /**
     * 数据库表对应的ID
     */
    private Integer table_id;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人名称
     */
    private String c_name;

    /**
     * 修改人名称
     */
    private String u_name;

    /**
     * 请求唯一标识ID
     */
    private String request_id;

    /**
     * 具体的变更前、变更后的数据
     */
    /**
     * 属性字段名称
     */
    private String clm_name;

    /**
     * 属性字段对应的注解DataChangeLabelAnnotation名
     */
    private String clm_label;

    /**
     * 旧值
     */
    private Object old_value;

    /**
     * 新值
     */
    private Object new_value;

    /**
     * 租户代码
     */
    private String tenant_code;

    /**
     * 开始时间（查询用）
     */
    private LocalDateTime start_time;

    /**
     * 结束时间（查询用）
     */
    private LocalDateTime over_time;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;
}