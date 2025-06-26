package com.xinyirun.scm.bean.entity.busniess.aprefundpay;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

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
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 退款单表code
     */
    @TableField("ap_refund_code")
    private String ap_refund_code;

    /**
     * 附件
     */
    @TableField("files")
    private Integer files;

    /**
     * 凭证上传附件
     */
    @TableField("voucher_files")
    private Integer voucher_files;


}
