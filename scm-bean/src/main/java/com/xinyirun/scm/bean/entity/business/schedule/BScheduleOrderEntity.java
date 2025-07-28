package com.xinyirun.scm.bean.entity.business.schedule;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 调度订单
 * </p>
 *
 * @author wwl
 * @since 2022-01-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_schedule_order")
public class BScheduleOrderEntity implements Serializable {


    private static final long serialVersionUID = -5401069068752647176L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String order_no;

    /**
     * 单据类型
     */
    @TableField("bill_type")
    private String bill_type;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 船名
     */
    @TableField("ship_name")
    private String ship_name;

    /**
     * 合同日期
     */
    @TableField("contract_dt")
    private LocalDateTime contract_dt;

    /**
     * 合同量
     */
    @TableField("contract_num")
    private BigDecimal contract_num;

    /**
     * 供应商
     */
    @TableField("supplier_id")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplier_code;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Integer client_id;

    /**
     * 客户编码
     */
    @TableField("client_code")
    private String client_code;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

}
