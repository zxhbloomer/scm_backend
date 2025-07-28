package com.xinyirun.scm.bean.entity.business.releaseorder;

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
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_release_order")
public class BWkReleaseOrderEntity implements Serializable {

    private static final long serialVersionUID = 5726333172209018794L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 业务中台出库计划单号
     */
    @TableField("extra_code")
    private String extra_code;

    /**
     * 类型名称
     */
    @TableField("type_name")
    private String type_name;

    /**
     * 业务板块
     */
    @TableField("business_plate_name")
    private String business_plate_name;

    /**
     * 业务类型名称
     */
    @TableField("business_type_name")
    private String business_type_name;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contract_code;

    /**
     * 订单编号
     */
    @TableField("order_code")
    private String order_code;

    /**
     * 采购退货单编号
     */
    @TableField("purchase_order_return_code")
    private String purchase_order_return_code;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customer_name;

    /**
     * 客户code
     */
    @TableField("customer_code")
    private String customer_code;

    /**
     * 委托方名称
     */
    @TableField("consignor_name")
    private String consignor_name;

    /**
     * 委托方code
     */
    @TableField("consignor_code")
    private String consignor_code;

    /**
     * 货主名称
     */
    @TableField("owner_name")
    private String owner_name;

    /**
     * 货主code
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 放货指令信息
     */
    @TableField("direct_info")
    private String direct_info;

    /**
     * 日期
     */
    @TableField("out_time")
    private LocalDateTime out_time;

    /**
     * 计划时间
     */
    @TableField("plan_time")
    private LocalDateTime plan_time;

    /**
     * 是否配置数量浮动
     */
    @TableField("float_controled")
    private Boolean float_controled;

    /**
     * 上浮百分比
     */
    @TableField("float_up")
    private BigDecimal float_up;

    /**
     * 下浮百分比
     */
    @TableField("float_down")
    private BigDecimal float_down;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal total_amount;

    /**
     * 账户余额(企业预收款余额)
     */
    @TableField("balance")
    private BigDecimal balance;

    /**
     * 是否已用印上传(0否 1是)
     */
    @TableField("use_sealed")
    private Boolean use_sealed;

    /**
     * 状态名称
     */
    @TableField("status_name")
    private String status_name;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人
     */
    @TableField("c_name")
    private String c_name;

    /**
     * 更新人
     */
    @TableField("u_name")
    private String u_name;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;


}
