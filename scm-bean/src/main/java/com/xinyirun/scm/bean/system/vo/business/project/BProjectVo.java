package com.xinyirun.scm.bean.system.vo.business.project;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 项目管理表
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Schema(description = "项目管理VO")
public class BProjectVo extends BaseVo {


    @Serial
    private static final long serialVersionUID = -3852244245388952509L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 编号
     */
    @Schema(description = "项目编号")
    private String code;

    /**
     * 名称
     */
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String name;

    /**
     * 类型 0全托 1代采 2代销
     */
    @Schema(description = "类型（0全托 1代采 2代销）")
    private Integer type;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称")
    private String type_name;

    /**
     * 状态 0待审批 1审批中 2执行中 3驳回 4作废审批中 5已作废 6已完成
     */
    @Schema(description = "状态（0待审批 1审批中 2执行中 3驳回 4作废审批中 5已作废 6已完成）")
    private String status;

    @Schema(description = "状态列表，用于查询")
    private String[] status_list;

    /**
     * 状态名称
     */
    @Schema(description = "状态名称")
    private String status_name;

    /**
     * 融资主体id
     */
    @Schema(description = "融资主体ID")
    private Integer finance_id;

    @Schema(description = "融资主体名称")
    private String finance_name;

    /**
     * 运输方式 0公路 1铁路 2多式联运
     */
    @Schema(description = "运输方式（0公路 1铁路 2多式联运）")
    private String delivery_type;

    @Schema(description = "运输方式名称")
    private String delivery_type_name;

    /**
     * 交货地点
     */
    @Schema(description = "交货地点", maxLength = 200)
    private String delivery_location;

    /**
     * 备注
     */
    @Schema(description = "备注", maxLength = 500)
    private String remark;

    /**
     * 其他附件
     */
    @Schema(description = "附件材料ID")
    private Integer doc_att_file;

    @Schema(description = "附件材料列表")
    private List<SFileInfoVo> doc_att_files;

    /**
     * 付款方式 0依据合同 1预付款 2先款后货
     */
    @Schema(description = "付款方式（0依据合同 1预付款 2先款后货）")
    private String payment_method;

    /**
     * 付款方式名称
     */
    @Schema(description = "付款方式名称")
    private String payment_method_name;

    /**
     * 是否有账期/天数
     */
    @Schema(description = "是否有账期/天数")
    private Integer payment_days;

    /**
     * 项目周期
     */
    @Schema(description = "项目周期")
    private Integer project_cycle;

    /**
     * 额度
     */
    @Schema(description = "融资额度")
    private BigDecimal amount;

    /**
     * 费率
     */
    @Schema(description = "费率")
    private BigDecimal rate;

    /**
     * 删除0-未删除，1-已删除
     */
    @Schema(description = "删除标记（0-未删除，1-已删除）")
    private Boolean is_del;

    /**
     * 实例表ID
     */
    @Schema(description = "BPM实例表ID")
    private Integer bpm_instance_id;

    /**
     * 实例编码
     */
    @Schema(description = "BPM实例编码")
    private String bpm_instance_code;

    /**
     * 下一个审批人姓名
     */
    @Schema(description = "审批情况/下一个审批人姓名")
    private String next_approve_name;

    /**
     * 流程名称
     */
    @Schema(description = "BPM流程名称")
    private String bpm_process_name;

    /**
     * 撤销实例ID
     */
    @Schema(description = "撤销BPM实例ID")
    private Integer bpm_cancel_instance_id;

    /**
     * 撤销实例编码
     */
    @Schema(description = "撤销BPM实例编码")
    private String bpm_cancel_instance_code;

    /**
     * 撤销流程名称
     */
    @Schema(description = "撤销BPM流程名称")
    private String bpm_cancel_process_name;

    /**
     * 项目说明
     */
    @Schema(description = "项目说明", maxLength = 500)
    private String project_remark;

    /**
     * 作废理由
     */
    @Schema(description = "作废理由")
    private String cancel_reason;

    /**
     * 作废附件
     */
    @Schema(description = "作废附件列表")
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废提交人
     */
    @Schema(description = "作废提交人")
    private String cancel_name;

    /**
     * 作废时间
     */
    @Schema(description = "作废时间")
    private LocalDateTime cancel_time;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @Schema(description = "创建人ID")
    private Integer c_id;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String c_name;

    /**
     * 修改人id
     */
    @Schema(description = "修改人ID")
    private Integer u_id;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    @Schema(description = "数据版本，乐观锁使用")
    private Integer dbversion;

    /**
     * 供应商id
     */
    @Schema(description = "供应商ID")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @Schema(description = "供应商编码")
    private String supplier_code;

    @Schema(description = "上游供应商", requiredMode = Schema.RequiredMode.REQUIRED)
    private String supplier_name;

    /**
     * 购买方id
     */
    @Schema(description = "下游客户ID（主体企业）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    @Schema(description = "采购方编码")
    private String purchaser_code;

    @Schema(description = "下游客户（主体企业）")
    private String purchaser_name;

    /**
     * 客户查询条件 - 对应purchaser字段
     */
    @Schema(description = "客户ID，查询条件")
    private Integer customer_id;

    @Schema(description = "客户名称，查询条件")
    private String customer_name;

    /**
     * 商品列表
     */
    @Schema(description = "商品列表")
    private List<BProjectGoodsVo> goods_list;

    /**
     * 分页参数
     */
    @Schema(description = "分页参数")
    private PageCondition pageCondition;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间，查询条件")
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间，查询条件")
    private LocalDateTime over_time;


    /**
     * 表单数据
     */
    @Schema(description = "BPM表单数据")
    private JSONObject form_data;

    /**
     * 初始化审批流程
     */
    @Schema(description = "初始化审批流程")
    private String initial_process;

    /**
     * 自选数据
     */
    @Schema(description = "BPM自选审批人")
    private Map<String, List<OrgUserVo>> process_users;

    /**
     * 审批流实例化code
     */
    @Schema(description = "BPM审批流实例化code")
    private String process_code;

    /**
     * 任务ID
     */
    @Schema(description = "BPM任务ID")
    private String task_id;


    /**
     * 校验类型
     */
    @Schema(description = "校验类型")
    private String check_type;

    /**
     * 项目商品明细金额合计
     */
    @Schema(description = "总金额")
    private BigDecimal amount_sum;

    /**
     * 详情列表数据
     */
    @Schema(description = "商品明细列表")
    List<BProjectGoodsVo> detailListData;

    /**
     * 报表编号
     */
    @Schema(description = "报表编号")
    private String print_url;

    /**
     * 二维码
     */
    @Schema(description = "二维码")
    private String qr_code;
}
