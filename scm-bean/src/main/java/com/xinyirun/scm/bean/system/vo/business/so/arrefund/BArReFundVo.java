package com.xinyirun.scm.bean.system.vo.business.so.arrefund;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
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
import java.util.Map;

/**
 * <p>
 * 应收退款管理表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8472951368037429158L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出 id
     */
    private Integer[] ids;

    private Integer id;

    private Integer ar_id;

    /**
     * 应收退款id
     */
    private Integer ar_refund_id;

    private String no;

    /**
     * 应收退款编号
     */
    private String code;

    /**
     * 1-应收、2-应收退款、3-预收、4-预收退款、5-其他收入、6-其他收入退款
     */
    private String type;
    private String type_name;

    /**
     * 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
     */
    private String status;
    private String status_name;

    /**
     * 退款状态：0-未退款、1-部分退款、2-已退款、-1-中止退款
     */
    private String refund_status;
    private String refund_status_name;

    /**
     * 实例表ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 审批流程名称
     */
    private String bpm_process_name;

    /**
     * 流程状态
     */
    private String next_approve_name;

    /**
     * 报表编号
     */
    private String print_url;

    /**
     * 二维码
     */
    private String qr_code;

    /**
     * 关联项目编号
     */
    private String project_code;

    /**
     * 销售合同ID
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;

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
     * 账户退款总金额
     */
    private BigDecimal detail_refund_amount;

    /**
     * 未退款总金额
     */
    private BigDecimal not_pay_amount;

    /**
     *
     */
    private BigDecimal return_pay_amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 组件返回—企业类型id
     */
    private String dict_id;

    /**
     * 组件返回—企业类型名称
     */
    private String dict_label;

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

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 校验类型
     */
    private String check_type;

    /**
     * 初始化审批流程
     */
    private String initial_process;

    /**
     * 表单数据
     */
    private JSONObject form_data;

    /**
     * 自选数据
     */
    private Map<String, List<OrgUserVo>> process_users;


    /**
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 作废附件 ID
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 作废 实例表ID
     */
    private Integer bpm_cancel_instance_id;

    /**
     * 作废 流程实例code
     */
    private String bpm_cancel_instance_code;

    /**
     * 作废 审批流程名称
     */
    private String bpm_cancel_process_name;

    /**
     * 申请退款总金额汇总
     */
    private BigDecimal refund_amount_total;

    /**
     * 已退款总金额汇总
     */
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额汇总
     */
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额汇总
     */
    private BigDecimal unrefund_amount_total;

    /**
     * 单据状态列表
     */
    private String[] status_list;

    /**
     * 退款中总金额
     */
    private BigDecimal refund_amount;

    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 作废提交人
     */
    private String cancel_name;
    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    // ==================== 从 b_ar_refund_source_advance 表新增字段 ====================
    /**
     * 销售商品信息
     */
    private String so_goods;

    /**
     * 数量总计
     */
    private BigDecimal qty_total;

    /**
     * 总金额
     */
    private BigDecimal amount_total;

    /**
     * 来源订单金额
     */
    private BigDecimal source_order_amount;

    /**
     * 来源备注
     */
    private String source_remark;

    /**
     * 可退款总金额
     */
    private BigDecimal refundable_amount_total;

    /**
     * 取消退款总金额
     */
    private BigDecimal cancelrefund_amount_total;

    // ==================== 从 b_ar_refund_detail 表新增字段 ====================
    /**
     * 应收退款编号
     */
    private String ar_refund_code;

    /**
     * 银行账户ID
     */
    private Integer bank_accounts_id;

    /**
     * 银行账户编号
     */
    private String bank_accounts_code;

    /**
     * 银行账户类型ID
     */
    private Integer bank_accounts_type_id;

    /**
     * 银行账户类型编号
     */
    private String bank_accounts_type_code;

    /**
     * 可退款金额
     */
    private BigDecimal refundable_amount;

    /**
     * 已退款金额
     */
    private BigDecimal refunded_amount;

    /**
     * 退款中金额
     */
    private BigDecimal refunding_amount;

    /**
     * 未退款金额
     */
    private BigDecimal unrefund_amount;

    /**
     * 明细订单金额
     */
    private BigDecimal detail_order_amount;

    /**
     * 明细备注
     */
    private String detail_remark;

    // ==================== 关联表字段 ====================
    /**
     * 银行账户名称
     */
    private String name;

    /**
     * 银行名称
     */
    private String bank_name;

    /**
     * 账户号码
     */
    private String account_number;

    /**
     * 账户用途类型名称
     */
    private String accounts_purpose_type_name;

    /**
     * 企业银行账户分类表id
     */
    private BArReFundDetailVo bankData;

    /**
     * 预收款已收金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 可退金额
     */
    private BigDecimal advance_refund_amount_total;

    /**
     * 退款账户类型
     */
    private String bank_type_name;
}