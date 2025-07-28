package com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive;

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
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundReceiveSourceAdvanceVo implements Serializable {

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
    private Integer ar_refund_receive_id;

    /**
     * 退款单表code
     */
    private String ar_refund_receive_code;

    /**
     * 退款管理id
     */
    private Integer ar_refund_id;

    /**
     * 退款管理code
     */
    private String ar_refund_code;

    /**
     * 1-应收退款、2-预收退款、3-其他收入退款
     */
    private String type;

    /**
     * 类型名称
     */
    private String type_name;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售合同名称
     */
    private String so_contract_name;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 销售订单名称
     */
    private String so_order_name;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String so_goods;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    /**
     * 预收款已付总金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 预收款退款总金额
     */
    private BigDecimal advance_refund_amount_total;

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