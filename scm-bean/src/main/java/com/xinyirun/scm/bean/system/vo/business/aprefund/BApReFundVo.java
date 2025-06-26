package com.xinyirun.scm.bean.system.vo.business.aprefund;

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
     * 付款状态：0-未付款、1-部分付款、2-已付款、-1-中止付款
     */
    private String pay_status;
    private String pay_status_name;

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
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    private String po_code;

    /**
     * 供应商企业编号
     */
    private String supplier_enterprise_code;

    /**
     * 供应商企业版本号
     */
    private Integer supplier_enterprise_version;

    /**
     * 供应商企业名称
     */
    private String supplier_enterprise_name;

    /**
     * 主体企业买家企业编号
     */
    private String buyer_enterprise_code;

    /**
     * 主体企业买家企业版本号
     */
    private Integer buyer_enterprise_version;

    /**
     * 主体企业买家企业名称
     */
    private String buyer_enterprise_name;

    /**
     * 计划退款总金额
     */
    private BigDecimal refund_amount;

    /**
     * 退款中总金额
     */
    private BigDecimal refunding_amount;
    private BigDecimal detail_payable_amount;

    /**
     * 未退款总金额
     */
    private BigDecimal not_pay_amount;

    /**
     * 已退款总金额
     */
    private BigDecimal refunded_amount;

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
     * 业务单据信息 采购订单
     */
    private List<BApReFundSourceAdvanceVo> poOrderListData;

    /**
     * 付款信息 银行账户
     */
    private List<BApReFundDetailVo> bankListData;

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
     * 中止理由
     */
    private String stop_reason;


    /**
     * 中止附件
     */
    private Integer stop_file;
    private List<SFileInfoVo> stop_files;

}
