package com.xinyirun.scm.bean.system.vo.business.wms.inplan;

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
 * <p>
 * 入库计划
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInPlanVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 7315520861753371430L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String status;

    /**
     * 计划时间
     */
    private LocalDateTime plan_time;

    /**
     * 超收比例
     */
    private BigDecimal over_receipt_rate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主编码
     */
    private String owner_code;
    private String owner_name;

    /**
     * 委托方id
     */
    private Integer consignor_id;

    /**
     * 委托方编码
     */
    private String consignor_code;
    private String consignor_name;

    /**
     * 是否删除
     */
    private Integer is_del;

    /**
     * 是否ERP模式
     */
    private Boolean is_erp_model;

    /**
     * 下一个审批人姓名
     */
    private String next_approve_name;

    /**
     * BPM实例id
     */
    private Integer bpm_instance_id;

    /**
     * BPM实例编码
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
     * BPM取消实例编码
     */
    private String bpm_cancel_instance_code;

    /**
     * BPM取消流程名称
     */
    private String bpm_cancel_process_name;

    /**
     * 数据版本
     */
    private Integer dbversion;

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

    // ========== BPM相关字段 ==========

    /**
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 作废附件信息
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废提交人姓名
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
     * 作废附件
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 状态名称
     */
    private String status_name;

    /**
     * 类型名称
     */
    private String type_name;

    /**
     * 入库计划明细数据
     */
    private List<BInPlanDetailVo> detailListData;

    // ========== 审批流程相关字段 ==========

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
     * 组织用户vo
     */
    private OrgUserVo orgUserVo;

    /**
     * 单据状态列表
     */
    private String[] status_list;

    /**
     * 类型列表
     */
    private String[] type_list;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 入库计划详情List
     */
    private List<BInPlanDetailVo> detailList;

    /**
     * 校验类型
     */
    private String check_type;


    /**
     * 物料编码或名称
     */
    private String goods_name;

    /**
     * 合同编号
     */
    private String contract_code;

    /**
     * 订单编号
     */
    private String order_code;

    /**
     * 仓库ID
     */
    private Integer warehouse_id;

    /**
     * 仓库编码
     */
    private String warehouse_code;

    /**
     * 仓库名称
     */
    private String warehouse_name;

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
     * 项目编码
     */
    private String project_code;

    /**
     * 计划时间起始、结束
     */
    private String[] plan_times;

    /**
     * 商品编码
     */
    private String goods_code;

    /**
     * SKU编码
     */
    private String sku_code;


    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;
}
