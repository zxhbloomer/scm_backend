package com.xinyirun.scm.bean.system.vo.business.wms.outplan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 出库计划附件表
 * </p>
 *
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutPlanAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 4461891518579466720L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库计划id
     */
    private Integer out_plan_id;

    /**
     * 出库计划附件
     */
    private Integer one_file;

    /**
     * 创建人id
     */
    private Long c_id;
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;
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