package com.xinyirun.scm.bean.entity.business.po.ap;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应付账款附件表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_attach")
public class BApAttachEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -5403616284249020170L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * b_ap id
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 合同附件
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField("u_time")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField("c_id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField("u_id")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;


}
