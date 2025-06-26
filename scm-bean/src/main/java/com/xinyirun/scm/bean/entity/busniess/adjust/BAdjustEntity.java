package com.xinyirun.scm.bean.entity.busniess.adjust;

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
 * 库存调整
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_adjust")
@DataChangeEntityAnnotation(value="库存调整表", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.DataChangeStrategyBAdjustServiceImpl")
public class BAdjustEntity implements Serializable {


    private static final long serialVersionUID = 2741756310850613257L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DataChangeLabelAnnotation("id")
    private Integer id;

    /**
     * 调整单号
     */
    @TableField("code")
    @DataChangeLabelAnnotation("调整单号")
    private String code;

    /**
     * 类型 1、库存调整；2、盘盈调整；3、盘亏调整
     */
    @TableField("type")
    @DataChangeLabelAnnotation("类型 1、库存调整；2、盘盈调整；3、盘亏调整")
    private String type;

    /**
     * 货主code
     */
    @TableField("owner_code")
    @DataChangeLabelAnnotation("货主编号")
    private String owner_code;

    /**
     * 货主id
     */
    @TableField("owner_id")
    private Integer owner_id;

    /**
     * 调整原因
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("调整原因")
    private String remark;

    /**
     * 附件信息
     */
    @TableField("files_id")
    @DataChangeLabelAnnotation("附件id")
    private Integer files_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建人id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改人id")
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;


}
