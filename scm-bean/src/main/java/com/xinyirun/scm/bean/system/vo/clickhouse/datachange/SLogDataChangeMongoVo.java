package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * api系统日志
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogDataChangeMongoVo implements Serializable {

    
    private static final long serialVersionUID = 6162655654516814092L;

    private String id;

    /**
     * 操作业务名：entity的注解名称
     */
    private String name;

    /**
     * 数据操作类型：SystemConstants.SQLCOMMANDTYPE.UPDATE|INSERT|DELETE
     */
    private String type;


    /**
     * 数据操作类型：SystemConstants.SQLCOMMANDTYPE.UPDATE|INSERT|DELETE
     */
    private String SqlCommandType;

    /**
     * 表名
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
     * 关系主键
     */
    private String order_main_id;

    /**
     * 调用策略模式_数据变更的类名
     */
    private String class_name;

    /**
     * 具体的变更前、变更后的数据
     */
    private List<SLogDataChangeDetailMongoVo> details;

    /**
     * 数据库表对应的id
     */
    private Integer table_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 请求id
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
}
