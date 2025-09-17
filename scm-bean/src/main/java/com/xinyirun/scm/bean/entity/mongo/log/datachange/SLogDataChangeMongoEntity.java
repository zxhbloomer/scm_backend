package com.xinyirun.scm.bean.entity.mongo.log.datachange;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
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
@Document("s_log_data_change_detail")
public class SLogDataChangeMongoEntity implements Serializable {

    
    private static final long serialVersionUID = -8014827636558958400L;

    @Id
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
    @Indexed
    private String order_code;


    /**
     * 调用策略模式_数据变更的类名
     */
    private String class_name;

    /**
     * 具体的变更前、变更后的数据
     */
    private List<SLogDataChangeDetailMongoEntity> details;

    /**
     * 数据库表对应的id
     */
    private Integer table_id;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
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
     * 修改人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 请求id
     */
    @Indexed
    private String request_id;

    /**
     * 租户code
     */
    private String tenant_code;
}
