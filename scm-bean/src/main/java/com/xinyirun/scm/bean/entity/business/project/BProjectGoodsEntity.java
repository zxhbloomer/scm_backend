package com.xinyirun.scm.bean.entity.business.project;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理-商品明细
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_project_goods")
@DataChangeEntityAnnotation(value="项目管理-商品明细", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.project.DataChangeStrategyBProjectGoodsEntityServiceImpl")
public class BProjectGoodsEntity implements Serializable {

    private static final long serialVersionUID = 5153511778172675103L;

    /**
     * 主键ID
     */
    @TableId("id")
    private Integer id;

    /**
     * 项目管理id
     */
    @TableField("project_id")
    private Integer project_id;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 商品编号
     */
    @TableField("goods_code")
    @DataChangeLabelAnnotation("商品编号")
    private String goods_code;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    @DataChangeLabelAnnotation("商品名称")
    private String goods_name;

    /**
     * 规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 规格编号
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("规格编号")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    @DataChangeLabelAnnotation("规格名称")
    private String sku_name;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 产地
     */
    @TableField("origin")
    private String origin;

    /**
     * 数量
     */
    @TableField("qty")
    @DataChangeLabelAnnotation("数量")
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    @TableField("price")
    @DataChangeLabelAnnotation("单价（含税）")
    private BigDecimal price;

    /**
     * 金额
     */
    @TableField("amount")
    @DataChangeLabelAnnotation("金额")
    private BigDecimal amount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal tax_amount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal tax_rate;

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
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
