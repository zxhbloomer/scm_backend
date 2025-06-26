package com.xinyirun.scm.bean.api.vo.business.in;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 采购调价函
 * </p>
 *
 * @author wwl
 * @since 2022-11-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBPurchasePricingVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 1811422739375692351L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 调价函编号
     */
    private String code;

    /**
     * 商品规格id
     */
    private Integer sku_id;

    /**
     * 商品规格编号
     */
    private String sku_code;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 新价格
     */
    private BigDecimal new_price;

    /**
     * 启用时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime end_time;

    /**
     * 是否删除
     */
    private Boolean is_deleted;


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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改人
     */
    private String u_name;
}
