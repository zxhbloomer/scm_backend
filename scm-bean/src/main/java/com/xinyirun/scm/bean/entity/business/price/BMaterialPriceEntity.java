package com.xinyirun.scm.bean.entity.business.price;

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
@TableName("b_material_price")
public class BMaterialPriceEntity implements Serializable {


    private static final long serialVersionUID = -1192606222852233997L;

    @TableId("id")
    private Integer id;

    @TableField("goods_id")
    private Integer goods_id;

    @TableField("goods_code")
    private String goods_code;

    @TableField("goods_name")
    private String goods_name;

    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编号 业务中台和wms唯一关联code
     */
    @TableField("sku_code")
    private String sku_code;

    @TableField("sku_name")
    private String sku_name;

    @TableField("type")
    private String type;

    @TableField("query_code")
    private String query_code;

    @TableField("price")
    private BigDecimal price;

    @TableField("c_time")
    private LocalDateTime c_time;

}
