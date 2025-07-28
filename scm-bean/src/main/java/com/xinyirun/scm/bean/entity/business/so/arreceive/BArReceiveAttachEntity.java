package com.xinyirun.scm.bean.entity.business.so.arreceive;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收单附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_receive_attach")
public class BArReceiveAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -1224735268870296378L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收单表id
     */
    @TableField("ar_receive_id")
    private Integer ar_receive_id;

    /**
     * 收款单表code
     */
    @TableField("ar_receive_code")
    private String ar_receive_code;

    /**
     * 应收单附件
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 凭证附件
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}