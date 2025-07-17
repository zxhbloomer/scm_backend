package com.xinyirun.scm.bean.system.vo.business.aprefundpay;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 退款单关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundPaySourceAdvanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8460471971126288633L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出 id
     */
    private Integer[] ids;

    /**
     * id
     */
    private Integer id;

    /**
     * 退款单表id
     */
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    private String ap_refund_pay_code;

    /**
     * 退款管理id
     */
    private Integer ap_refund_id;

    /**
     * 退款管理code
     */
    private String ap_refund_code;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    private String type;

    /**
     * 类型名称
     */
    private String type_name;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购合同名称
     */
    private String po_contract_name;

    /**
     * 采购订单编号
     */
    private String po_order_code;

    /**
     * 采购订单名称
     */
    private String po_order_name;

    /**
     * 采购订单id
     */
    private Integer po_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String po_goods;

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
     * 创建人名称
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 修改人名称
     */
    private String u_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    // 查询条件字段
    /**
     * 创建时间范围查询 - 开始时间
     */
    private LocalDateTime c_time_start;

    /**
     * 创建时间范围查询 - 结束时间
     */
    private LocalDateTime c_time_end;

    /**
     * 修改时间范围查询 - 开始时间
     */
    private LocalDateTime u_time_start;

    /**
     * 修改时间范围查询 - 结束时间
     */
    private LocalDateTime u_time_end;

}