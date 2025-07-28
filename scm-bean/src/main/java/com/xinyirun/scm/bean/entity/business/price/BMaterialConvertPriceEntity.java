package com.xinyirun.scm.bean.entity.business.price;

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
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_material_convert_price")
public class BMaterialConvertPriceEntity implements Serializable {

    private static final long serialVersionUID = -1023062179570365383L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("code")
    private String code;

    @TableField("dt")
    private LocalDateTime dt;

    /**
     * 物料id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 物料code
     */
    @TableField("goods_code")
    private String goods_code;

    /**
     * 物料名称
     */
    @TableField("goods_name")
    private String goods_name;

    /**
     * 规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格code
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 生成时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 物料id
     */
    @TableField("source_goods_id")
    private Integer source_goods_id;

    /**
     * 物料code
     */
    @TableField("source_goods_code")
    private String source_goods_code;

    /**
     * 物料名称
     */
    @TableField("source_goods_name")
    private String source_goods_name;

    /**
     * 规格id
     */
    @TableField("source_sku_id")
    private Integer source_sku_id;

    /**
     * 规格code
     */
    @TableField("source_sku_code")
    private String source_sku_code;

    /**
     * 规格名称
     */
    @TableField("source_sku_name")
    private String source_sku_name;


}
