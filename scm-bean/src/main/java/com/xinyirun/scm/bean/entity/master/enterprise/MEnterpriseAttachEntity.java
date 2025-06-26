package com.xinyirun.scm.bean.entity.master.enterprise;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业信息附件表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_enterprise_attach")
@DataChangeEntityAnnotation(value="企业信息附件表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.master.enterprise.DataChangeStrategyMEnterpriseAttachEntityServiceImpl")
public class MEnterpriseAttachEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4701231430152638288L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 企业id
     */
    @TableField("enterprise_id")
    private Integer enterprise_id;

    /**
     * 营业执照附件id
     */
    @TableField("license_att_id")
    @DataChangeLabelAnnotation(value="营业执照附件", extension = "getAttachmentUrlExtension")
    private Integer license_att_id;

    /**
     * 法人身份证正面附件id
     */
    @TableField("lr_id_front_att_id")
    @DataChangeLabelAnnotation(value="法人身份证正面附件", extension = "getAttachmentUrlExtension")
    private Integer lr_id_front_att_id;

    /**
     * 法人身份证背面附件id
     */
    @TableField("lr_id_back_att_id")
    @DataChangeLabelAnnotation(value="法人身份证背面附件", extension = "getAttachmentUrlExtension")
    private Integer lr_id_back_att_id;

    /**
     * 其他材料附件id
     */
    @TableField("doc_att_id")
    @DataChangeLabelAnnotation(value="其他材料附件", extension = "getAttachmentUrlExtension")
    private Integer doc_att_id;

    /**
     * 公司logo附件id
     */
    @TableField("logo_id")
    @DataChangeLabelAnnotation(value="公司logo附件", extension = "getAttachmentUrlExtension")
    private Integer logo_id;

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
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

}
