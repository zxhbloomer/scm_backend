package com.xinyirun.scm.bean.entity.master.enterprise;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业类型类型表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_enterprise_types")
@DataChangeEntityAnnotation(value="企业类型从表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.master.enterprise.DataChangeStrategyMEnterpriseTypeEntityServiceImpl")
public class MEnterpriseTypesEntity implements Serializable {

    private static final long serialVersionUID = 6372415182665941914L;

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
     * 类型:5-加工厂，4-承运商，3-仓储方，2-供应商，1-客户，0-主体企业
     */
    @TableField("type")
    @DataChangeLabelAnnotation(value = "企业类型 1客户 2供应商 3仓储方 4承运商 5加工厂")
    private String type;


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
