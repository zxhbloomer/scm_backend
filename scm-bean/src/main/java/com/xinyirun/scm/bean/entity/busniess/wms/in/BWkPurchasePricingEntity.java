package com.xinyirun.scm.bean.entity.busniess.wms.in;

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
 * @since 2022-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_wk_purchase_pricing")
public class BWkPurchasePricingEntity implements Serializable {

    private static final long serialVersionUID = 7542888529647707931L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 商品规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 商品规格编号
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contract_no;

    /**
     * 新价格
     */
    @TableField("new_price")
    private BigDecimal new_price;

    /**
     * 启用时间
     */
    @TableField("start_time")
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime end_time;

    /**
     * 是否删除
     */
    @TableField("is_deleted")
    private Boolean is_deleted;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField("c_id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField("u_id")
    private Long u_id;


}
