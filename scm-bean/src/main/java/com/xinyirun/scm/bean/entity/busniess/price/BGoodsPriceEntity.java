package com.xinyirun.scm.bean.entity.busniess.price;

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
 * @since 2022-02-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_goods_price")
public class BGoodsPriceEntity implements Serializable {

    private static final long serialVersionUID = -1832760579840456596L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

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
     * 产地
     */
    @TableField("origin")
    private String origin;

    /**
     * 厂家
     */
    @TableField("company")
    private String company;

    /**
     * 片区
     */
    @TableField("area")
    private String area;

    /**
     * 省
     */
    @TableField("province")
    private String province;

    /**
     * 省
     */
    @TableField("province_code")
    private String province_code;

    /**
     * 市
     */
    @TableField("city")
    private String city;

    /**
     * 市
     */
    @TableField("city_code")
    private String city_code;

    /**
     * 区/县
     */
    @TableField("district")
    private String district;

    /**
     * 区/县
     */
    @TableField("district_code")
    private String district_code;

    /**
     * 开始日期
     */
    @TableField("start_dt")
    private String startDt;

    /**
     * 结束日期
     */
    @TableField("end_dt")
    private String endDt;

    /**
     * 价格日期
     */
    @TableField("price_dt")
    private String priceDt;

    /**
     * 生成时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;


}
