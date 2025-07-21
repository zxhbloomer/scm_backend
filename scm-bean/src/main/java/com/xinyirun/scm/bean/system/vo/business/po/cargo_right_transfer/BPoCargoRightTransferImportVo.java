package com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer;

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
 * @Description: 货权转移导入VO
 * @CreateTime : 2025/7/20 16:05
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BPoCargoRightTransferImportVo extends BaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -1965849642903122060L;

    private Integer id;

    /**
     * 编号自动生成编号
     */
    private String code;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 审批状态：0-待审批 1-审批中 2-已审批 3-驳回 4-作废审批中 5-已作废 6-已完成
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
     * 转移日期
     */
    private LocalDateTime transfer_date;

    /**
     * 转移地点
     */
    private String transfer_location;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关联的采购订单ID
     */
    private Integer po_order_id;
    private String po_order_code;
    private String po_order_name;

    /**
     * 关联的采购合同ID
     */
    private Integer po_contract_id;
    private String po_contract_code;
    private String po_contract_name;

    /**
     * 转移总数量
     */
    private BigDecimal total_qty;

    /**
     * 转移总金额
     */
    private BigDecimal total_amount;

    /**
     * 其他附件
     */
    private Integer doc_att_file;
    private List<SFileInfoVo> doc_att_files;

    /**
     * 货权转移明细信息
     */
    private List<BPoCargoRightTransferDetailVo> detailListData;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

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
    private List<SFileInfoVo> cancel_files;
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
     * 删除0-未删除，1-已删除
     */
    private Boolean is_del;

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
     * 单据状态列表
     */
    private String[] status_list;

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
     * 订单数量
     */
    private BigDecimal order_qty;

    /**
     * 订单单价
     */
    private BigDecimal order_price;

    /**
     * 订单总额
     */
    private BigDecimal order_amount;

    /**
     * 转移数量
     */
    private BigDecimal transfer_qty;

    /**
     * 转移单价
     */
    private BigDecimal transfer_price;

    /**
     * 转移总额
     */
    private BigDecimal transfer_amount;

    /**
     * 质量状态
     */
    private String quality_status;

    /**
     * 批次号
     */
    private String batch_no;

    /**
     * 生产日期
     */
    private LocalDateTime production_date;

    /**
     * 到期日期
     */
    private LocalDateTime expiry_date;

    /**
     * 货权转移id
     */
    private Integer cargo_right_transfer_id;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * url
     */
    private String url;
}