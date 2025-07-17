package com.xinyirun.scm.bean.system.vo.business.aprefund;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -6219136969784065223L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出 id
     */
    private Integer[] ids;

    private Integer id;

    /**
     * 主表id
     */
    private Integer ap_refund_id;

    /**
     * 文件ID
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