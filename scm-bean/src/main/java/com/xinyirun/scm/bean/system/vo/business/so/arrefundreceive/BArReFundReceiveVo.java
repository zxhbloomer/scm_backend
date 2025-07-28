package com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 应收退款单表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundReceiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2606383260790695157L;

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
     * 收款单编号
     */
    private String code;

    /**
     * 应收退款id
     */
    private Integer ar_refund_id;

    /**
     * 应收退款code
     */
    private String ar_refund_code;

    /**
     * 退款单状态：状态（0-待收款、1已收款、2-作废）
     */
    private String status;
    private String status_name;

    /**
     * type
     */
    private String type;
    private String type_name;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 客户ID
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 销售方ID
     */
    private Integer seller_id;

    /**
     * 销售方编码
     */
    private String seller_code;

    /**
     * 销售方名称
     */
    private String seller_name;

    /**
     * 退款日期
     */
    private LocalDateTime refund_date;

    /**
     * 退款金额
     */
    private BigDecimal refundable_amount_total;

    /**
     * 已退款
     */
    private BigDecimal refunded_amount_total;

    /**
     * 退款中
     */
    private BigDecimal refunding_amount_total;

    /**
     * 未退款
     */
    private BigDecimal unrefund_amount_total;

    /**
     * 退款取消
     */
    private BigDecimal cancelrefund_amount_total;

    /**
     * 退款方式：1-银行转账
     */
    private String refund_method;
    private String refund_method_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 凭证上传备注
     */
    private String voucher_remark;

    /**
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 作废附件
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 凭证上传附件
     */
    private Integer voucher_file;
    private List<SFileInfoVo> voucher_files;

    /**
     * 下推附件
     */
    private Integer push_file;
    private List<SFileInfoVo> push_files;

    /**
     * 创建名称
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改名称
     */
    private String u_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    private String bank_name;

    /**
     * 客户开户名
     */
    private String account_name;

    /**
     * 主体企业银行名称
     */
    private String seller_bank_name;

    /**
     * 企业银行账户id
     */
    private Integer bank_account_id;

    /**
     * 企业银行账户code
     */
    private String bank_account_code;

    /**
     * 交易编号（资金池、合同编号-转款专用））
     */
    private String trade_no;

    /**
     * 退款账户信息数据
     */
    private BArReFundReceiveDetailVo bankData;

    /**
     * 退款指令金额
     */
    private BigDecimal refund_order_amount;

    /**
     * 退款备注
     */
    private String refund_remark;

    /**
     * 表头：数据汇总
     */
    private BigDecimal sum_refundable_amount_total;
    private BigDecimal sum_refunded_amount_total;
    private BigDecimal sum_refunding_amount_total;
    private BigDecimal sum_unrefund_amount_total;
    private BigDecimal sum_cancelrefund_amount_total;
    private BigDecimal advance_refund_amount_total;
    private BigDecimal advance_paid_total;

    private String so_goods;
    private Integer so_order_id;
    private String so_order_code;
    private Integer so_contract_id;

    private BigDecimal source_order_amount;

}