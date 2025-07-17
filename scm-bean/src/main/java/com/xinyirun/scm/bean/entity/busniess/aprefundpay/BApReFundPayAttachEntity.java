package com.xinyirun.scm.bean.entity.busniess.aprefundpay;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款单附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay_attach")
public class BApReFundPayAttachEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5936011150996074275L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款单表id
     */
    @TableField("ap_refund_pay_id")
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    @TableField("ap_refund_pay_code")
    private String ap_refund_pay_code;

    /**
     * 附件1
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 附件2
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Integer c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Integer u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}
