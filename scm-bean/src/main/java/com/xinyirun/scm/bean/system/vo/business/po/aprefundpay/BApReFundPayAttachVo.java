package com.xinyirun.scm.bean.system.vo.business.po.aprefundpay;

import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
     * 退款单表id
     */
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    private String ap_refund_pay_code;

    /**
     * 附件1
     */
    private Integer one_file;
    private List<SFileInfoVo> one_files;

    /**
     * 附件2
     */
    private Integer two_file;
    private List<SFileInfoVo> two_files;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 创建名称
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 修改名称
     */
    private String u_name;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
