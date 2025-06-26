package com.xinyirun.scm.bean.entity.master.goods.unit;

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
 * @since 2022-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_goods_unit_calc")
public class MGoodsUnitCalcEntity implements Serializable {

    private static final long serialVersionUID = 2659067692331453763L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物料规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 原计量单位id
     */
    @TableField("src_unit_id")
    private Integer src_unit_id;

    /**
     * 原计量单位code
     */
    @TableField("src_unit_code")
    private String src_unit_code;

    /**
     * 原计量单位
     */
    @TableField("src_unit")
    private String src_unit;

    /**
     * 换算后单位id
     */
    @TableField("tgt_unit_id")
    private Integer tgt_unit_id;

    /**
     * 换算后单位code
     */
    @TableField("tgt_unit_code")
    private String tgt_unit_code;

    /**
     * 换算后单位
     */
    @TableField("tgt_unit")
    private String tgt_unit;

    /**
     * 换算关系
     */
    @TableField("calc")
    private BigDecimal calc;

    /**
     * 是否删除
     */
    @TableField("enable")
    private Boolean enable;

    /**
     * 顺序
     */
    @TableField("idx")
    private String idx;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

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
    @TableField(value="dbversion")
    private Integer dbversion;

}
