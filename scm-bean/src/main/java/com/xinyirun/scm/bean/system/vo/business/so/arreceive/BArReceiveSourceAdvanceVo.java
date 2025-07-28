package com.xinyirun.scm.bean.system.vo.business.so.arreceive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收来源预收表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReceiveSourceAdvanceVo implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1942835746820957163L;
    
    private Integer id;

    /**
     * 应收单主表id
     */
    private Integer ar_receive_id;

    /**
     * 应收主表code
     */
    private String ar_receive_code;

    /**
     * 应收账款主表id
     */
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    private String ar_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String so_goods;

    /**
     * 总数量
     */
    private BigDecimal so_qty;

    /**
     * 总金额
     */
    private BigDecimal so_amount;

    /**
     * 累计预收款金额
     */
    private BigDecimal so_advance_receive_amount;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * ar_receive 主表 状态
     */
    private String status;

    /**
     * 销售合同ID聚合字符串（GROUP_CONCAT）
     */
    private String so_contract_id_gc;

    /**
     * 销售合同编号聚合字符串（GROUP_CONCAT）
     */
    private String so_contract_code_gc;

    /**
     * 销售订单编号聚合字符串（GROUP_CONCAT）
     */
    private String so_order_code_gc;

    /**
     * 销售订单ID聚合字符串（GROUP_CONCAT）
     */
    private String so_order_id_gc;
}