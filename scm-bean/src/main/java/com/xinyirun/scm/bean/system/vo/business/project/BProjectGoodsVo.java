package com.xinyirun.scm.bean.system.vo.business.project;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
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
@NoArgsConstructor
@Schema(description = "项目管理-商品明细VO")
public class BProjectGoodsVo extends BaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 6336029355371652596L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 项目管理id
     */
    @Schema(description = "项目管理ID")
    private Integer project_id;

    /**
     * 商品id
     */
    @Schema(description = "商品ID")
    private Integer goods_id;

    /**
     * 商品编号
     */
    @Schema(description = "商品编号")
    private String goods_code;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String goods_name;

    /**
     * 规格id
     */
    @Schema(description = "规格ID")
    private Integer sku_id;

    /**
     * 规格编号
     */
    @Schema(description = "商品编号")
    private String sku_code;

    /**
     * 规格名称
     */
    @Schema(description = "规格")
    private String sku_name;

    /**
     * 单位ID
     */
    @Schema(description = "单位ID")
    private Integer unit_id;

    /**
     * 产地
     */
    @Schema(description = "产地")
    private String origin;

    /**
     * 数量
     */
    @Schema(description = "合同数量")
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    @Schema(description = "单价（含税）")
    private BigDecimal price;

    /**
     * 金额
     */
    @Schema(description = "金额")
    private BigDecimal amount;

    /**
     * 税额
     */
    @Schema(description = "税额")
    private BigDecimal tax_amount;

    /**
     * 税率
     */
    @Schema(description = "税率（%）")
    private BigDecimal tax_rate;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @Schema(description = "创建人ID")
    private Long c_id;

    /**
     * 修改人id
     */
    @Schema(description = "修改人ID")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Schema(description = "数据版本，乐观锁使用")
    private Integer dbversion;

    /**
     * 虚拟SKU编码名称（计算字段）
     */
    private String virtual_sku_code_name;

}
