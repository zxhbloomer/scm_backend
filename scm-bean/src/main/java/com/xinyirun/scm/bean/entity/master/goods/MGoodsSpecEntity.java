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
@TableName("m_goods_spec")
public class MGoodsSpecEntity implements Serializable {

    private static final long serialVersionUID = 561291941881359652L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    @TableField("code")
    private String code;

    /**
     * 商品编码
     */
    @TableField("goods_code")
    private String goods_code;

    /**
     * 物料名称
     */
    @TableField("name")
    private String name;

    /**
     * 单位(米/支、码/支)
     */
    @TableField("unit")
    private String unit;

    /**
     * 规格
     */
    @TableField("spec")
    private String spec;

    /**
     * 是否启用（1是0否）
     */
    @TableField("enable")
    private Boolean enable;

    /**
     * 重量
     */
    @TableField("weight")
    private BigDecimal weight;

    /**
     * 净重
     */
    @TableField("net_weight")
    private BigDecimal net_weight;

    /**
     * 毛重
     */
    @TableField("rough_weight")
    private BigDecimal rough_weight;

    /**
     * 体积
     */
    @TableField("volume")
    private BigDecimal volume;

    /**
     * 产地
     */
    @TableField("orgin")
    private String orgin;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 规格属性ID
     */
    @TableField("prop_id")
    private Integer prop_id;

    /**
     * 品名
     */
    @TableField("pm")
    private String pm;

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

    /**
     * 是否删除:false-未删除,true-已删除
     */
    @TableField("is_del")
    private Boolean is_del;


}
