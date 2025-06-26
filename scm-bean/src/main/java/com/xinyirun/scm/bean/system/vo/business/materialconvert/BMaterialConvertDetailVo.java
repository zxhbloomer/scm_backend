package com.xinyirun.scm.bean.system.vo.business.materialconvert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调拨明细
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMaterialConvertDetailVo implements Serializable {

    private static final long serialVersionUID = 6197728854335221642L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 状态 0制单，状态 1已提交，2审核通过，3审核驳回，4作废
     */
    private String status;

    /**
     * 单据状态
     */
    private Boolean is_effective;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 物料转换id
     */
    private Integer material_convert_id;

    /**
     * 原物料id
     */
    private Integer source_sku_id;

    /**
     * 原物料code
     */
    private String source_sku_code;

    /**
     * 原规格
     */
    private String source_spec;

    /**
     * 原物料名称
     */
    private String source_goods_name;

    /**
     * 新物料id
     */
    private Integer target_sku_id;

    /**
     * 新物料code
     */
    private String target_sku_code;

    /**
     * 新规格
     */
    private String target_spec;

    /**
     * 新物料名称
     */
    private String target_goods_name;

    /**
     * 转换后比例
     */
    private BigDecimal calc;
    private String relation;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
