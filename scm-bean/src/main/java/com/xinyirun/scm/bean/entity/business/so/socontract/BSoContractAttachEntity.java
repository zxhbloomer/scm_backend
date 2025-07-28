package com.xinyirun.scm.bean.entity.business.so.socontract;

import java.io.Serial;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 销售合同附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_contract_attach")
@DataChangeEntityAnnotation(value="销售合同附件表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.socontract.DataChangeStrategyBSoContractAttachEntityServiceImpl")
public class BSoContractAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -1523290648074695088L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    @DataChangeLabelAnnotation(value="销售合同")
    private Integer so_contract_id;

    /**
     * 营业执照
     */
    @TableField("one_file")
    @DataChangeLabelAnnotation(value="营业执照")
    private Integer one_file;

    /**
     * 法人身份证正面
     */
    @TableField("two_file")
    @DataChangeLabelAnnotation(value="法人身份证正面")
    private Integer two_file;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}