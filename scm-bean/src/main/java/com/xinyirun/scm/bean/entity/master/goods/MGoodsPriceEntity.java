package com.xinyirun.scm.bean.entity.master.goods;

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
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_goods_price")
public class MGoodsPriceEntity implements Serializable {

    private static final long serialVersionUID = -5142453945161143430L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单据号
     */
    @TableField("code")
    private String code;

    /**
     * 物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 物料名称
     */
    @TableField("goods_name")
    private String goods_name;

    /**
     * 物料编号
     */
    @TableField("goods_code")
    private String goods_code;

    /**
     * 规格型号
     */
    @TableField("spec")
    private String spec;

    /**
     * 产地
     */
    @TableField("origin")
    private String origin;

    /**
     * 生产厂家
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 所属片区
     */
    @TableField("region")
    private String region;

    /**
     * 所属省份
     */
    @TableField("province")
    private String province;

    /**
     * 所属城市
     */
    @TableField("city")
    private String city;

    /**
     * 所属区县
     */
    @TableField("district")
    private String district;

    /**
     * 起始日期
     */
    @TableField("begin_dt")
    private LocalDateTime begin_dt;

    /**
     * 终止日期
     */
    @TableField("end_dt")
    private LocalDateTime end_dt;

    /**
     * 采集日期
     */
    @TableField("collection_dt")
    private LocalDateTime collection_dt;

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
     * 创建人ID
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人ID
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
