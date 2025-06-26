package com.xinyirun.scm.bean.system.vo.business.aprefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 退款单附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundPayAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 5755330437113213633L;

    private Integer id;

    /**
     * 付款单表id
     */
    private Integer ap_refund_id;

    /**
     * 付款单表code
     */
    private String ap_refund_code;

    /**
     * 附件
     */
    private Integer files;

    /**
     * 凭证上传附件
     */
    private String voucher_files;


}
