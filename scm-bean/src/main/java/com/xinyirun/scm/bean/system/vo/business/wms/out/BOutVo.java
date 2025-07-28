package com.xinyirun.scm.bean.system.vo.business.wms.out;

import com.alibaba.fastjson2.JSONObject;
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
 * 出库单VO类
 * 
 * @author system
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6929551130597187253L;


    // ==================== 实体类字段 ====================
    
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库单号
     */
    private String code;

    /**
     * 出库类型
     */
    private String type;

    /**
     * 出库状态
     */
    private String status;

    /**
     * 合同id
     */
    private Integer contract_id;

    /**
     * 合同编号
     */
    private String contract_code;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单编号
     */
    private String order_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;

    /**
     * 收货方id
     */
    private Integer consignee_id;

    /**
     * 收货方编码
     */
    private String consignee_code;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 供应商id
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 出库计划id
     */
    private Integer out_plan_id;

    /**
     * 出库计划明细id
     */
    private Integer out_plan_detail_id;

    /**
     * 批次号
     */
    private String lot;

    /**
     * 备注
     */
    private String remark;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 库位id
     */
    private Integer location_id;

    /**
     * 货位id
     */
    private Integer bin_id;

    /**
     * 规格id
     */
    private Integer sku_id;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 计划数量
     */
    private BigDecimal plan_qty;

    /**
     * 计划重量
     */
    private BigDecimal plan_weight;

    /**
     * 计划体积
     */
    private BigDecimal plan_volume;

    /**
     * 实际数量
     */
    private BigDecimal actual_qty;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 实际重量
     */
    private BigDecimal actual_weight;

    /**
     * 实际体积
     */
    private BigDecimal actual_volume;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 单位id
     */
    private Integer unit_id;

    /**
     * 出库时间
     */
    private LocalDateTime outbound_time;

    /**
     * 处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    private BigDecimal processed_volume;

    /**
     * 取消数量
     */
    private BigDecimal cancel_qty;

    /**
     * 取消重量
     */
    private BigDecimal cancel_weight;

    /**
     * 取消体积
     */
    private BigDecimal cancel_volume;

    /**
     * 库存流水id
     */
    private Integer inventory_sequence_id;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 原始数量
     */
    private BigDecimal original_qty;

    /**
     * 车次数
     */
    private Integer cart_count;

    /**
     * 是否异常
     */
    private Boolean is_exception;

    /**
     * 异常备注
     */
    private String exception_comment;

    /**
     * 库存状态
     */
    private Boolean stock_status;

    /**
     * 下一审批人
     */
    private String next_approve_name;

    /**
     * BPM实例id
     */
    private Integer bpm_instance_id;

    /**
     * BPM实例编号
     */
    private String bpm_instance_code;

    /**
     * BPM流程名称
     */
    private String bpm_process_name;

    /**
     * BPM取消实例id
     */
    private Integer bpm_cancel_instance_id;

    /**
     * BPM取消实例编号
     */
    private String bpm_cancel_instance_code;

    /**
     * BPM取消流程名称
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
     * 是否删除
     */
    private Boolean is_del;

    // ==================== 扩展字段 ====================

    /**
     * 类型名称
     */
    private String type_name;

    /**
     * 状态名称
     */
    private String status_name;

    /**
     * 货主名称
     */
    private String owner_name;

    /**
     * 收货方名称
     */
    private String consignee_name;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 库位名称
     */
    private String location_name;

    /**
     * 货位名称
     */
    private String bin_name;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 创建人名称
     */
    private String c_name;

    /**
     * 修改人名称
     */
    private String u_name;

    // ==================== 查询条件字段 ====================

    /**
     * 检查类型
     */
    private String check_type;

    /**
     * 单据状态列表
     */
    private String[] status_list;

    /**
     * 类型列表
     */
    private String[] type_list;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

    // ==================== 统计汇总字段 ====================

    /**
     * 数量合计
     */
    private BigDecimal qty_total;

    /**
     * 金额合计
     */
    private BigDecimal amount_total;


    // ==================== BPM流程相关字段 ====================

    /**
     * 初始化流程节点key(用于启动流程节点)
     */
    private String initial_process;

    /**
     * 流程数据
     */
    private JSONObject form_data;

    /**
     * 自选数据
     */
    private Map<String, List<OrgUserVo>> process_users;

    /**
     * 组织用户信息
     */
    private OrgUserVo orgUserVo;

    // ==================== 附件相关字段 ====================

    /**
     * 附件1
     */
    private List<SFileInfoVo> one_file;

    /**
     * 附件2
     */
    private List<SFileInfoVo> two_file;

    /**
     * 附件3
     */
    private List<SFileInfoVo> three_file;

    /**
     * 附件4
     */
    private List<SFileInfoVo> four_file;

    // ==================== 关联计划字段 ====================

    /**
     * 计划编号
     */
    private String plan_code;

    /**
     * 计划流水号
     */
    private String plan_no;

    /**
     * 计划时间
     */
    private LocalDateTime plan_time;

    // ==================== 作废相关字段 ====================

    /**
     * 作废原因
     */
    private String cancel_reason;

    /**
     * 作废人名称
     */
    private String cancel_name;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

    /**
     * 作废文档附件
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废文件
     */
    private List<SFileInfoVo> cancel_files;

    // ==================== 附件ID字段 ====================

    /**
     * 文档附件1ID
     */
    private Integer doc_one_file;

    /**
     * 文档附件2ID
     */
    private Integer doc_two_file;

    /**
     * 文档附件3ID
     */
    private Integer doc_three_file;

    /**
     * 文档附件4ID
     */
    private Integer doc_four_file;

    // ============ 销售合同相关字段 ============
    
    /**
     * 销售合同类型
     */
    private String so_contract_type;
    
    /**
     * 销售合同类型名称
     */
    private String so_contract_type_name;
    
    /**
     * 主体企业（销售方名称）
     */
    private String seller_name;
    
    /**
     * 销售合同付款方式
     */
    private String so_contract_payment_type;
    
    /**
     * 付款方式名称
     */
    private String payment_type_name;
    
    /**
     * 销售合同结算方式
     */
    private String so_contract_settle_type;
    
    /**
     * 结算方式名称
     */
    private String settle_type_name;
    
    /**
     * 交货日期
     */
    private LocalDateTime delivery_date;
    
    /**
     * 交货地点
     */
    private String delivery_location;

    // ============ 销售订单相关字段 ============
    
    /**
     * 销售订单状态
     */
    private String so_order_status;
    
    /**
     * 销售订单状态名称
     */
    private String so_order_status_name;
    
    /**
     * 销售订单运输方式
     */
    private String so_order_delivery_type;
    
    /**
     * 运输方式名称
     */
    private String delivery_type_name;
}