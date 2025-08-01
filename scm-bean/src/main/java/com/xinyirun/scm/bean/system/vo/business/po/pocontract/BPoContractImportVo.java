package com.xinyirun.scm.bean.system.vo.business.po.pocontract;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
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
 * @Description:
 * @CreateTime : 2025/1/14 16:05
 */


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BPoContractImportVo extends BaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -461753421942995729L;


    private Integer id;

    /**
     * 编号自动生成编号
     */
    private String code;

    /**
     * 类型：0：标准合同；1：框架合同
     */
    private String type;
    private String type_name;

    /**
     * 合同编号，为用户手写编号，如果页面未输入，等于code自动编号
     */
    private String contract_code;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 审批状态：0-待审批 1-审批中 2-审批通过 3-驳回
     */
    private String status;
    private String status_name;


    /**
     * 供应商id
     */
    private Integer supplier_id;
    private String supplier_name;

    /**
     * 购买方id
     */
    private Integer purchaser_id;
    private String purchaser_name;

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
    private Boolean auto_create_order;
    private String auto_create_name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 商品信息
     */
    private List<BPoContractDetailVo> detailListData;

    /**
     * 合同总金额
     */
    private BigDecimal contract_amount_sum;


    /**
     * 总采购数量
     */
    private BigDecimal contract_total;


    /**
     * 总税额
     */
    private BigDecimal tax_amount_sum;

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
     * 预付款金额
     */
    private BigDecimal advance_pay_price;

    /**
     * 累计实付
     */
    private BigDecimal accumulated_act_price;

    /**
     * 未付
     */
    private BigDecimal unpaid_amount;

    /**
     * 预付款可退金额
     */
    private BigDecimal advance_pay_rt_price;

    /**
     * 可开票金额
     */
    private BigDecimal already_invoice_price;

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
    private BigDecimal contract_amount_total;

    /**
     * 表头 预付款总金额
     */
    private BigDecimal advance_pay_total;

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
     * 实例表ID
     */
    private Integer bpm_instance_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 流程状态
     */
    private String next_approve_name;

    /**
     * 删除0-未删除，1-已删除
     */
    private Boolean is_del;

    /**
     * 标准合同是否存在订单
     */
    private Boolean existence_order;

    /**
     * 报表编号
     */
    private String print_url;

    /**
     * 二维码
     */
    private String qr_code;

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
     * 审批流程名称
     */
    private String bpm_process_name;

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
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 作废附件
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 客户名称
     */
    private String customer_name;
    private Integer customer_id;

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
     * 商品名称
     */
    private Integer goods_id;
    private String goods_code;
    private String goods_name;

    /**
     * 规格code、商品编码
     */
    private String sku_code;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 物料ID、商品ID
     */
    private Integer sku_id;

    /**
     * 单位ID
     */
    private Integer unit_id;

    /**
     * 产地
     */
    private String origin;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    private BigDecimal price;

    /**
     * 总额
     */
    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal tax_amount;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 税率
     */
    private BigDecimal tax_rate;

    /**
     * 页面code
     */
    private String page_code;
    /**
     * url
     */
    private String url;
}
