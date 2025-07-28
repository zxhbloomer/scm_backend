package com.xinyirun.scm.bean.system.vo.business.so.soorder;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description: 销售订单VO
 * @CreateTime : 2025/7/23 16:05
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoOrderVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3555505017977494453L;

    private Integer id;
    private Integer so_order_id;

    /**
     * 订单编号，自动生成
     */
    private String code;
    private String so_order_code;

    /**
     * 类型：0：标准合同；1：框架合同
     */
    private String type;
    private String type_name;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 销售合同ID
     */
    private Integer so_contract_id;

    /**
     * 客户ID
     */
    private Integer customer_id;
    private String customer_name;
    private String customer_code;

    /**
     * 销售方ID
     */
    private Integer seller_id;
    private String seller_name;
    private String seller_code;

    /**
     * 订单日期
     */
    private LocalDateTime order_date;

    /**
     * 签约日期
     */
    private LocalDateTime sign_date;

    /**
     * 到期日期
     */
    private LocalDateTime expiry_date;

    /**
     * 交货日期
     */
    private LocalDateTime delivery_date;

    /**
     * 运输方式：1-公路；2-铁路；3-多式联运；
     */
    private String delivery_type;
    private String delivery_type_name;

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
    private String payment_type_name;

    /**
     * 交货地点
     */
    private String delivery_location;

    /**
     * 审批后自动生成订单：默认true
     */
    private String auto_create_order;
    private String auto_create_name;

    /**
     * 商品名称
     */
    private String goods_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     */
    private String status;
    private String status_name;

    /**
     * 删除0-未删除，1-已删除
     */
    private Integer is_del;

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
     * 审批流程名称
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
     * 作废审批流程名称
     */
    private String bpm_cancel_process_name;


    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 商品信息
     */
    private List<BSoOrderDetailVo> detailListData;

    /**
     * 销售总金额
     */
    private BigDecimal order_amount_sum;

    /**
     * 总销售数量
     */
    private BigDecimal qty_total;

    /**
     * 实际出库合计
     */
    private BigDecimal inventory_out_total_sum;

    /**
     * 总税额
     */
    private BigDecimal tax_amount_sum;

    /**
     * 税额合计
     */
    private BigDecimal tax_amount_total;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 已结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 结算金额
     */
    private BigDecimal settled_price;

    /**
     * 预收款可退金额
     */
    private BigDecimal advance_receive_refundable_price;

    /**
     * 累计实收
     */
    private BigDecimal received_total;

    /**
     * 可下推预收款金额
     */
    private BigDecimal advance_amount_total;

    /**
     * 可下推预收款退款金额
     */
    private BigDecimal advance_refund_amount_total;

    /**
     * 预收款已收总金额
     */
    private BigDecimal advance_received_total;

    /**
     * 预收款作废总金额
     */
    private BigDecimal advance_cancelreceive_total;

    /**
     * 已开票金额
     */
    private BigDecimal invoiced_price;

    /**
     * 订单量
     */
    private Integer order_volume;

    /**
     * 执行进度
     */
    private Integer execution_progress;

    /**
     * 表头 合同总金额
     */
    private BigDecimal amount_total;

    /**
     * 表头 预收已收总金额
     */
    private BigDecimal advance_receive_total;

    /**
     * 表头 预收未收总金额
     */
    private BigDecimal advance_unreceive_total;

    /**
     * 表头 结算总金额
     */
    private BigDecimal settle_amount_total;

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
     * 校验类型
     */
    private String check_type;

    /**
     * 审批流程code
     */
    private String process_code;


    /**
     * 报表编号
     */
    private String print_url;

    /**
     * 二维码
     */
    private String qr_code;

    /**
     * 预收款可退金额
     */
    private BigDecimal advance_receive_rt_price;

    /**
     * 可开票金额
     */
    private BigDecimal already_invoice_price;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 导出 id
     */
    private Integer[] ids;

    /**
     * 导出 id
     */
    private Integer no;

    /**
     * 审核通过时间
     */
    private LocalDateTime approve_time;

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
     * 作废提交人
     */
    private String cancel_name;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

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
     * 作废附件
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 是否预收款
     */
    private Boolean is_advance_receive; // 是否预收款

    /**
     * 项目数据
     */
    private BProjectVo project;

    /**
     * 销售合同信息
     */
    private BSoContractVo so_contract;

    /**
     * 实际结算-数量汇总
     */
    private BigDecimal settled_qty_total;

    /**
     * 待结算数量（吨）
     */
    private BigDecimal settle_can_qty_total;
}