package com.xinyirun.scm.bean.entity.busniess.materialconvert;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_material_convert")
public class BMaterialConvertEntity implements Serializable {

    private static final long serialVersionUID = -6485879152806394977L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单号
     */
    @TableField("code")
    private String code;

    /**
     * 转换类型
     */
    @TableField("type")
    private String type;

    /**
     * 单据状态
     */
    @TableField("is_effective")
    private Integer is_effective;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 货主code
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    @TableField("warehouse_code")
    private String warehouse_code;

    /**
     * 是否转换成相同物料
     */
    @TableField("is_sku")
    private Boolean is_sku;

    /**
     * 新物料id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 新物料code
     */
    @TableField("sku_code")
    private String sku_code;

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

    /**
     * 数据版本
     */
    @TableField(value="data_version")
    private Integer data_version;


    /**
     * 是否未最新数据
     */
    @TableField(value="is_latested")
    private Boolean is_latested;

    /**
     * 转换时间
     */
    @TableField(value="convert_time")
    private LocalDateTime convert_time;

    /**
     * 转换状态 0：失败 1：成功
     */
    @TableField(value="convert_status")
    private Boolean convert_status;

}
