package com.xinyirun.scm.bean.entity.busniess.pocontract;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 采购合同附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_contract_attach")
@DataChangeEntityAnnotation(value="采购合同附件表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.business.pocontract.DataChangeStrategyBPoContractAttachEntityServiceImpl")
public class BPoContractAttachEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5545275066201925598L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购合同id
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 合同附件
     */
    @TableField("one_file")
    @DataChangeLabelAnnotation(value="合同附件", extension = "getAttachmentUrlExtension")
    private Integer one_file;

    /**
     * 其他材料
     */
    @TableField("two_file")
    @DataChangeLabelAnnotation(value="其他材料", extension = "getAttachmentUrlExtension")
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
