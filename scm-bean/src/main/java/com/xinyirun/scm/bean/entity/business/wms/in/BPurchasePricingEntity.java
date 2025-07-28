package com.xinyirun.scm.bean.entity.business.wms.in;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("b_purchase_pricing")
public class BPurchasePricingEntity implements Serializable {

    private static final long serialVersionUID = 3703791032927390134L;

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
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

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
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;


}
