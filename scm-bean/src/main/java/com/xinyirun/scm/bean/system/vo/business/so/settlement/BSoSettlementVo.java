package com.xinyirun.scm.bean.system.vo.business.so.settlement;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 销售结算表
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BSoSettlementVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6433973696302138091L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 销售结算类型：0-销售结算
     */
    private String type;

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    private String status;

    /**
     * 结算日期
     */
    private LocalDate settlement_date;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    private String settle_type;

    /**
     * 结算单据类型：1-实际发货结算；2-货转凭证结算
     */
    private String bill_type;

    /**
     * 收款方式：1-银行转账
     */
    private String payment_type;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 客户编号
     */
    private String customer_code;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 销售方id
     */
    private Integer seller_id;

    /**
     * 销售方编号
     */
    private String seller_code;

    /**
     * 销售方名称
     */
    private String seller_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 折扣金额
     */
    private BigDecimal discount_amount;

    /**
     * 其他金额
     */
    private BigDecimal other_amount;

    /**
     * 杂费金额
     */
    private BigDecimal misc_amount;

    /**
     * 违约金金额
     */
    private BigDecimal penalty_amount;

    /**
     * 自动冲抵开关：1-预收款冲抵；0-不冲抵
     */
    private Boolean is_offset;

    /**
     * 删除标识：0-正常 1-删除
     */
    private Integer is_del;

    /**
     * 下一审批人姓名
     */
    private String next_approve_name;

    /**
     * BPM实例id
     */
    private Long bpm_instance_id;

    /**
     * BPM实例编号
     */
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    private String bpm_process_name;

    /**
     * BPM作废审批实例id
     */
    private Long bpm_cancel_instance_id;

    /**
     * BPM作废审批实例编号
     */
    private String bpm_cancel_instance_code;

    /**
     * BPM作废审批流程名称
     */
    private String bpm_cancel_process_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    private String type_name;
    private String status_name;
    private String settle_type_name;
    private String bill_type_name;

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
     * 结算方式列表
     */
    private String[] settle_list;

    /**
     * 结算单据类型列表
     */
    private String[] bill_type_list;

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
    private List<BSoSettlementDetailSourceOutboundVo> detailListData;

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

    private String so_contract_code;
    private String so_order_code;
}