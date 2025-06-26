package com.xinyirun.scm.bean.entity.busniess.project;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Accessors(chain = true)
@TableName("b_project")
@DataChangeEntityAnnotation(value="项目管理表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.project.DataChangeStrategyBProjectEntityServiceImpl")
public class BProjectEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8662552940034554159L;


    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;    /**
     * 编号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("编号-系统自动生成")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    @DataChangeLabelAnnotation("项目名称")
    private String name;

    /**
     * 类型 0全托 1代采 2代销
     */
    @TableField("type")
    @DataChangeLabelAnnotation(value="类型", dictExtension = "getDictExtension", dictExtensionType = "com.xinyirun.scm.common.constant.DictConstant.DICT_B_PROJECT_TYPE")
    private Integer type;    /**
     * 状态 0待审批 1执行中 2驳回 3完成
     */
    @TableField("status")
    @DataChangeLabelAnnotation(value="状态", dictExtension = "getDictExtension", dictExtensionType = "com.xinyirun.scm.common.constant.DictConstant.DICT_B_PROJECT_STATUS")
    private String status;

    /**
     * 融资主体id
     */
    @TableField("finance_id")
    private Integer finance_id;

    /**
     * 供应商id
     */
    @TableField("supplier_id")
    @DataChangeLabelAnnotation(value="供应商", extension = "getSupplierNameExtension")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    @DataChangeLabelAnnotation("供应商编码")
    private String supplier_code;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    @DataChangeLabelAnnotation("供应商名称")
    private String supplier_name;

    /**
     * 购买方id
     */
    @TableField("purchaser_id")
    @DataChangeLabelAnnotation(value="主体企业", extension = "getSupplierNameExtension")
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    @TableField("purchaser_code")
    @DataChangeLabelAnnotation("采购方编码")
    private String purchaser_code;

    /**
     * 采购方名称
     */
    @TableField("purchaser_name")
    @DataChangeLabelAnnotation("采购方名称")
    private String purchaser_name;

    /**
     * 0公路 1铁路 2多式联运
     */
    @TableField("delivery_type")
    private String delivery_type;

    /**
     * 交货地点
     */
    @TableField("delivery_location")
    private String delivery_location;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 付款方式 0依据合同 1预付款 2先款后货
     */
    @TableField("payment_method")
    private String payment_method;

    /**
     * 是否有账期/天数
     */
    @TableField("payment_days")
    private Integer payment_days;

    /**
     * 项目周期
     */
    @TableField("project_cycle")
    private Integer project_cycle;    /**
     * 额度
     */
    @TableField("amount")
    @DataChangeLabelAnnotation("项目额度")
    private BigDecimal amount;

    /**
     * 费率
     */
    @TableField("rate")
    @DataChangeLabelAnnotation("费率")
    private BigDecimal rate;

    /**
     * 删除0-未删除，1-已删除
     */
    @TableField(value="is_del")
    @DataChangeLabelAnnotation("删除0-未删除，1-已删除")
    private Boolean is_del;

    /**
     * 实例表ID
     */
    @TableField(value="bpm_instance_id")
    private Integer bpm_instance_id;

    /**
     * 实例编码
     */
    @TableField(value="bpm_instance_code")
    private String bpm_instance_code;

    /**
     * 下一个审批人姓名
     */
    @TableField(value="next_approve_name")
    private String next_approve_name;

    /**
     * 流程名称
     */
    @TableField(value="bpm_process_name")
    private String bpm_process_name;

    /**
     * 撤销实例ID
     */
    @TableField(value="bpm_cancel_instance_id")
    private Integer bpm_cancel_instance_id;

    /**
     * 撤销实例编码
     */
    @TableField(value="bpm_cancel_instance_code")
    private String bpm_cancel_instance_code;

    /**
     * 撤销流程名称
     */
    @TableField(value="bpm_cancel_process_name")
    private String bpm_cancel_process_name;

    /**
     * 项目说明
     */
    @TableField(value="project_remark")
    private String project_remark;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}
