package com.xinyirun.scm.bean.system.vo.business.wms.inplan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库计划附件
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInPlanAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -4123919247030151036L;

    /**
     * 主键id
     */
    private Integer id;    /**
     * 入库计划id
     */
    private Integer in_plan_id;

    /**
     * 入库计划附件
     */
    private Integer one_file;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
