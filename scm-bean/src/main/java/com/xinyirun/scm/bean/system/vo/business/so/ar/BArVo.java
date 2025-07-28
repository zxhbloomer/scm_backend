package com.xinyirun.scm.bean.system.vo.business.so.ar;

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
 * 应收账款管理表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -790798399084984531L;

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
     * 应收款编号
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
     * 收款状态：0-未收款、1-部分收款、2-已收款、-1-中止收款
     */
    private String receive_status;
    private String receive_status_name;

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
     * 应收金额总计
     */
    private BigDecimal receivable_amount;

    /**
     * 收款中总金额
     */
    private BigDecimal receiving_amount;

    // ========== 收款账户信息汇总字段 ==========
    /**
     * 收款总金额：收款账户信息的收款金额汇总值
     */
    private BigDecimal detail_receivable_amount;
    // ========== 收款账户信息汇总字段 ==========

    /**
     * 未收款总金额
     */
    private BigDecimal unreceive_amount;

    /**
     * 中止收款总金额
     */
    private BigDecimal stopreceive_amount;

    /**
     * 实收总金额
     */
    private BigDecimal received_amount;

    // ========== 汇总字段 ==========
    
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
    private BigDecimal stopreceive_amount_total;

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
     * 业务单据信息 销售订单
     */
    private List<BArSourceAdvanceVo> soOrderListData;

    /**
     * 业务单据信息 arlist
     */
    private List<BArSourceAdvanceVo> arListData;

    /**
     * 收款信息 银行账户
     */
    private List<BArDetailVo> bankListData;

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
    private String stopReceive_reason;

    /**
     * 中止操作人ID
     */
    private Integer stopReceive_u_id;

    /**
     * 中止操作时间
     */
    private LocalDateTime stopReceive_u_time;

    /**
     * 单据状态列表
     */
    private String[] status_list;

    /**
     * 中止附件
     */
    private Integer stopreceive_file;
    private List<SFileInfoVo> stopreceive_files;

    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 作废附件
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废提交人
     */
    private String cancel_name;

    /**
     * 中止提交人
     */
    private String stop_name;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 收款账户类型
     */
    private String bank_type_name;

}