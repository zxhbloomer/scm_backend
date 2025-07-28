package com.xinyirun.scm.bean.system.vo.business.so.ar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收账款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501393719592926761L;

    private Integer id;

    /**
     * 应收账款主表ID
     */
    private Integer ar_id;

    /**
     * 应收金额总计
     */
    private BigDecimal receivable_amount_total;

    /**
     * 已收款总金额
     */
    private BigDecimal received_amount_total;

    /**
     * 收款中总金额
     */
    private BigDecimal receiving_amount_total;

    /**
     * 未收款总金额
     */
    private BigDecimal unreceive_amount_total;

    /**
     * 中止收款总金额
     */
    private BigDecimal stopReceive_amount_total;

    /**
     * 取消收款总金额
     */
    private BigDecimal cancelReceive_amount_total;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}