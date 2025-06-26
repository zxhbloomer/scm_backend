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
 * 企业调整表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_enterprise_his")
public class MEnterpriseHisEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6812543806374732333L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 统一社会信用代码
     */
    @TableField("uscc")
    @DataChangeLabelAnnotation("统一社会信用代码")
    private String uscc;

    /**
     * 企业id
     */
    @TableField(value = "enterprise_id")
    private Integer enterprise_id;

    /**
     * 版本，0开始每次审批通过后累加1
     */
    @TableField("version")
    private Integer version;

    /**
     * 修改理由，在单据完成审批后，修改时需要记录修改理由
     */
    @TableField("modify_reason")
    private String modify_reason;

    /**
     * 企业名称
     */
    @TableField("enterprise_name")
    private String enterprise_name;

    /**
     * 调整信息json
     */
    @TableField(value = "adjust_info_json")
    private String  adjust_info_json;


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
    @TableField(value="dbversion")
    private Integer dbversion;

}
