package com.xinyirun.scm.bean.system.vo.business.ownerchange;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
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
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOwnerChangeDetailVo implements Serializable {

    private static final long serialVersionUID = -7909299114371332912L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 货权转移id
     */
    private Integer owner_change_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料id
     */
    private String sku_code;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

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


    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 物料code
     */
    private String spec_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料名称
     */
    private String sku_name;

    /**
     * 规格
     */
    private String spec;


}
