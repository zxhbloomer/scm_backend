package com.xinyirun.scm.bean.entity.busniess.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单
 * </p>
 *
 * @author wwl
 * @since 2022-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_order")
public class BOrderEntity implements Serializable {

    private static final long serialVersionUID = -1015970178431107247L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 业务类型
     */
    @TableField("serial_type")
    private String serial_type;

    @TableField("contract_no")
    private String contract_no;

    @TableField("order_no")
    private String order_no;

    @TableField("bill_type")
    private String bill_type;

    @TableField("ship_name")
    private String ship_name;

    @TableField("contract_dt")
    private LocalDateTime contract_dt;

    @TableField("contract_num")
    private BigDecimal contract_num;

    @TableField("customer_id")
    private Integer customer_id;

    @TableField("mode_transport_id")
    private Integer mode_transport_id;

    @TableField("mode_transport_name")
    private String mode_transport_name;

    @TableField("business_type_id")
    private Integer business_type_id;

    @TableField("business_type_code")
    private String business_type_code;

    @TableField("source_type")
    private String source_type;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
}
