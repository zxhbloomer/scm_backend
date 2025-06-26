package com.xinyirun.scm.bean.app.vo.master.enterprise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业调整表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AppMEnterpriseHisVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 4069263065194400201L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 企业id
     */
    private Integer enterprise_id;

    /**
     * 调整信息json
     */
    private String  adjust_info_json;


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
