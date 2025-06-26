package com.xinyirun.scm.bean.api.vo.business.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 同步状态
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBSyncStatusVo implements Serializable {

    private static final long serialVersionUID = -8003938031605880962L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 关联单号
     */
    private String serial_code;

    /**
     * 状态
     */
    private String status;

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
