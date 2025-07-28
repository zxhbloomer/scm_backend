package com.xinyirun.scm.bean.system.vo.business.so.ar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收账款附件表（Accounts Receivable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5403616284249020172L;

    private Integer id;

    /**
     * b_ar id
     */
    private Integer ar_id;

    /**
     * 附件文件
     */
    private Integer one_file;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}