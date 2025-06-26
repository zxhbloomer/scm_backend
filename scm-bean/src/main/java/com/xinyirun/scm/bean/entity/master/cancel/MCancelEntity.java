package com.xinyirun.scm.bean.entity.master.cancel;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 导入数据日志
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_cancel")
public class MCancelEntity extends BaseEntity<MCancelEntity> implements Serializable {

    private static final long serialVersionUID = -7593023476897491348L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务表id
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 业务表类型
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 作废附件id
     */
    @TableField("file_id")
    private Integer file_id;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
