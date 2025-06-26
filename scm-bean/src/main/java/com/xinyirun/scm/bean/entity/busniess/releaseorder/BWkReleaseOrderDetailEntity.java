package com.xinyirun.scm.bean.entity.busniess.releaseorder;

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
@TableName("b_wk_release_order_detail")
public class BWkReleaseOrderDetailEntity implements Serializable {

    private static final long serialVersionUID = 1679073437690413802L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 放货指令编号
     */
    @TableField("release_order_code")
    private String release_order_code;

    /**
     * 序号
     */
    @TableField("no")
    private Integer no;

    /**
     * 放货指令id
     */
    @TableField("release_order_id")
    private Integer release_order_id;

    /**
     * 商品编号
     */
    @TableField("commodity_code")
    private String commodity_code;

    /**
     * 商品名称
     */
    @TableField("commodity_name")
    private String commodity_name;

    /**
     * 商品规格
     */
    @TableField("commodity_spec")
    private String commodity_spec;

    /**
     * 规格code
     */
    @TableField("commodity_spec_code")
    private String commodity_spec_code;

    /**
     * 商品别称
     */
    @TableField("commodity_nickname")
    private String commodity_nickname;

    /**
     * 型规
     */
    @TableField("type_gauge")
    private String type_gauge;

    /**
     * 放货数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 单价(含税)(订单商品单价)
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 实时单价(大宗商品实时单价)
     */
    @TableField("real_price")
    private BigDecimal real_price;

    /**
     * 金额(含税)=单价*放货数量
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 收款日期
     */
    @TableField("collection_date")
    private LocalDateTime collection_date;

    /**
     * 单位名称
     */
    @TableField("unit_name")
    private String unit_name;

    /**
     * 仓库code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouse_name;

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
