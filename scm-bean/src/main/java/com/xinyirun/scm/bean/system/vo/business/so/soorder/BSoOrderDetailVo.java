package com.xinyirun.scm.bean.system.vo.business.so.soorder;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description: 销售订单明细VO
 * @CreateTime : 2025/7/23 16:05
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoOrderDetailVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5653409309825181509L;

    /**
     * 明细表主键id
     */
    private Integer so_order_detail_id;


    /**
     * 商品id
     */
    private Integer goods_id;

    /**
     * 商品编号
     */
    private String goods_code;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 规格编号
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 单位ID
     */
    private Integer unit_id;


    /**
     * 产地
     */
    private String origin;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    private BigDecimal price;

    /**
     * 总额
     */
    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal tax_amount;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 税率
     */
    private BigDecimal tax_rate;



    /**
     * 创建时间
     */
    private LocalDateTime c_time;


    /**
     * 更新时间
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
     * 实际出库汇总
     */
    private BigDecimal inventory_out_total;

    /**
     * 待结算数量
     */
    private BigDecimal settle_can_qty_total;
}