package com.xinyirun.scm.bean.system.vo.business.aprefund;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 应付退款管理表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -790798399084984530L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出 id
     */
    private Integer[] ids;

    private Integer id;

    private Integer ap_id;

    /**
     * 应付退款id
     */
    private Integer ap_refund_id;

    private String no;


    /**
     * 应付款编号
     */
    private String code;

    /**
     * 1-应付、2-应付退款、3-预付、4-预付退款、5-其他支出、6-其他支出退款
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
     * 采购合同ID
     */
    private Integer po_contract_id;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单ID
     */
    private Integer po_order_id;

    /**
     * 采购订单编号
     */
    private String po_order_code;


    /**
     * 供应商ID
     */
    private Integer supplier_id;

    /**
     * 供应商编号
     */
    private String supplier_code;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 采购方ID
     */
    private Integer purchaser_id;

    /**
     * 采购方编号
     */
    private String purchaser_code;

    /**
     * 采购方名称
     */
    private String purchaser_name;

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
     * 作废附件
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 作废实例表ID
     */
    private Integer bpm_cancel_instance_id;

    /**
     * 作废流程实例code
     */
    private String bpm_cancel_instance_code;

    /**
     * 作废审批流程名称
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
     * 付款中总金额
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

    // ==================== 从 b_ap_refund_source_advance 表新增字段 ====================
    /**
     * 采购商品信息
     */
    private String po_goods;

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

    // ==================== 从 b_ap_refund_detail 表新增字段 ====================
    /**
     * 应付退款编号
     */
    private String ap_refund_code;

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
    private BApReFundDetailVo bankData;

    /**
     * 预付款已付金额
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
