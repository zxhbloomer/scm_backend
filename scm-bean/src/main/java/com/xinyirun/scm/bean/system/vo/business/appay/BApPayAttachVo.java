package com.xinyirun.scm.bean.system.vo.business.appay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

/**
 * <p>
 * 付款单附件表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApPayAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5936011150996074275L;

    private Integer id;

    /**
     * 付款单表id
     */
    private Integer ap_pay_id;

    /**
     * 付款单表code
     */
    private String ap_pay_code;

    /**
     * 付款单附件
     */
    private Integer one_file;

    /**
     * 凭证附件
     */
    private Integer two_file;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 作废原因
     */
    private String cancel_reason;

    /**
     * 作废附件
     */
    private Integer cancel_file;

    /**
     * 作废附件文件列表
     */
    private List<SFileInfoVo> cancel_files;

}
