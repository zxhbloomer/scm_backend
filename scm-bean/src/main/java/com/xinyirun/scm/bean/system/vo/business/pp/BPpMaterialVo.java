package com.xinyirun.scm.bean.system.vo.business.pp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 生产计划_原材料
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BPpMaterialVo implements Serializable {


    
    private static final long serialVersionUID = 1384767323253593031L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 生产计划id
     */
    private Integer pp_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 配比
     */
    private BigDecimal pp_router;

    /**
     * 数量
     */
    private BigDecimal qty  = BigDecimal.ZERO;

    private BigDecimal wo_qty = BigDecimal.ZERO;

    /**
     *待领取
     */
    private BigDecimal wo_unclaimed = BigDecimal.ZERO;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 仓名称
     */
    private String warehouse_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库区code
     */
    private String location_code;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 库位code
     */
    private String bin_code;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 更新人id
     */
    private Integer u_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 商品属性
     */
    private String goods_prop;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    /**
     * 规格
     */
    private Integer goods_id;

    /**
     * 型规
     */
    private String type_gauge;

    /**
     * 可用库存
     */
    private BigDecimal qty_avaible;
}
