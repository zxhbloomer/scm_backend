package com.xinyirun.scm.bean.system.vo.business.settlement;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 采购结算表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoSettlementVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 6433973696302138090L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 采购结算类型：0-采购结算
     */
    private String type;
    private String type_name;
    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    private String status;
    private String status_name;

    /**
     * 结算日期
     */
    private java.time.LocalDate settlement_date;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    private String settle_type;
    private String settle_type_name;
    /**
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    private String bill_type;
    private String bill_type_name;
    /**
     * 付款方式：1-银行转账
     */
    private String payment_type;


    /**
     * 供应商ID
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 购买方ID
     */
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    private String purchaser_code;

    /**
     * 采购方名称
     */
    private String purchaser_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 优惠金额
     */
    private BigDecimal discount_amount;

    /**
     * 自动冲抵开关：true-预付款冲抵；false-不冲抵
     */
    private Boolean is_offset;

    /**
     * 其他金额
     */
    private BigDecimal other_amount;

    /**
     * 杂项金额
     */
    private BigDecimal misc_amount;

    /**
     * 罚款金额
     */
    private BigDecimal penalty_amount;

    /**
     * 删除0-未删除，1-已删除
     */
    private Boolean is_del;

    /**
     * 流程状态
     */
    private String next_approve_name;

    /**
     * 流程实例ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 审批流程名称：采购订单新增审批
     */
    private String bpm_process_name;

    /**
     * 作废流程实例ID
     */
    private Integer bpm_cancel_instance_id;

    /**
     * 作废流程实例code
     */
    private String bpm_cancel_instance_code;

    /**
     * 作废审批流程名称：作废审批流程
     */
    private String bpm_cancel_process_name;

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

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

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
     * 单据状态列表
     */
    private String[] status_list;

    /**
     * 合同类型列表
     */
    private String[] type_list;

    /**
     * 作废附件
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废提交人
     */
    private String cancel_name;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;
    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

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
     * 组织用户vo
     */
    private OrgUserVo orgUserVo;

    /**
     * 校验类型
     */
    private String check_type;

    /**
     * 商品信息
     */
    private List<BPoSettlementDetailSourceInboundVo> detailListData;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 实际结算-结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 实际结算-结算金额
     */
    private BigDecimal settled_amount;

    private String po_contract_code;
    private String po_order_code ;
} 