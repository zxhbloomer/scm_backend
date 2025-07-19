package com.xinyirun.scm.bean.entity.busniess.wms.out.order.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库订单
 * </p>
 *
 * @author htt
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_so")
public class BWkSoEntity implements Serializable {

    private static final long serialVersionUID = -5698319231668596108L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 状态 0执行中 1正常 -1停用
     */
    @TableField("status")
    private String status;

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
     * 单据类型值
     */
    @TableField("bill_type_name")
    private String bill_type_name;

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
     * 合同截止日期
     */
    @TableField("contract_expire_dt")
    private LocalDateTime contract_expire_dt;

    /**
     * 合同量
     */
    @TableField("contract_num")
    private BigDecimal contract_num;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Integer client_id;

    /**
     * 客户信用代码
     */
    @TableField("client_credit_no")
    private String client_credit_no;

    /**
     * 供应商名称
     */
    @TableField("client_name")
    private String client_name;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主信用代码
     */
    @TableField("owner_credit_no")
    private String owner_credit_no;

    /**
     * 货主名称
     */
    @TableField("owner_name")
    private String owner_name;

    /**
     * 业务板块ID
     */
    @TableField("business_type_id")
    private Integer business_type_id;

    /**
     * 业务板块code
     */
    @TableField("business_type_code")
    private String business_type_code;

    /**
     * 业务板块名称
     */
    @TableField("business_type_name")
    private String business_type_name;

    /**
     * 是否数量浮动管控
     */
    @TableField("over_inventory_policy")
    private Boolean over_inventory_policy;

    /**
     * 上浮百分比
     */
    @TableField("over_inventory_upper")
    private BigDecimal over_inventory_upper;

    /**
     * 下浮百分比
     */
    @TableField("over_inventory_lower")
    private BigDecimal over_inventory_lower;

    /**
     * 运输方式id
     */
    @TableField("mode_transport_id")
    private Integer mode_transport_id;

    /**
     * 运输方式
     */
    @TableField("mode_transport_name")
    private String mode_transport_name;

}
