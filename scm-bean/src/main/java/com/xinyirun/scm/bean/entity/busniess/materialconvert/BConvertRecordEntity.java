package com.xinyirun.scm.bean.entity.busniess.materialconvert;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @date 2022/11/23 16:04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_convert_record")
public class BConvertRecordEntity implements Serializable {

    private static final long serialVersionUID = 7835970225445843323L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 转换单号
     */
    @TableField("convert_code")
    private String convert_code;

    /**
     * 版本号
     */
    @TableField("data_version")
    private Integer data_version;

    /**
     * 转换名称
     */
    @TableField("convert_name")
    private String convert_name;

    /**
     * 货主ID
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主code
     */
    @TableField("owner_code")
    private String owner_code;


    /**
     * 货主名称
     */
    @TableField("owner_name")
    private String owner_name;


    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private String warehouse_id;

    /**
     * 仓库Code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouse_name;

    /**
     * 单据状态
     */
    @TableField("is_effective")
    private Boolean is_effective;

    /**
     * 0-单次任务 1-定时任务
     */
    @TableField("type")
    private String type;

    /**
     * 源物料ID
     */
    @TableField("source_sku_id")
    private Integer source_sku_id;

    /**
     * 源物料code
     */
    @TableField("source_sku_code")
    private String source_sku_code;

    /**
     * 源物料规格名称
     */
    @TableField("source_sku_name")
    private String source_sku_name;

    /**
     * 源物料名称
     */
    @TableField("source_goods_name")
    private String source_goods_name;

    /**
     * 新物料ID
     */
    @TableField("target_sku_id")
    private Integer target_sku_id;

    /**
     * 新物料规格名称
     */
    @TableField("target_sku_name")
    private String target_sku_name;

    /**
     * 新物料规格编码
     */
    @TableField("target_sku_code")
    private String target_sku_code;

    /**
     * 新物料名称
     */
    @TableField("target_goods_name")
    private String target_goods_name;

    /**
     * 转换后比例
     */
    @TableField("calc")
    private BigDecimal calc;

    /**
     * 转换源物料可用库存
     */
    @TableField("source_qty")
    private BigDecimal source_qty;

    /**
     * 转换源物料转换库存
     */
    @TableField("target_qty")
    private BigDecimal target_qty;

    @TableField(value="c_id", fill = FieldFill.INSERT_UPDATE)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

}
