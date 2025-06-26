package com.xinyirun.scm.bean.system.vo.business.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理-企业
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BProjectEnterpriseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8926780922938637624L;
    /**
     * 主键id
     * */
    private Integer id;

    /**
     * 企业id
     */
    private Integer enterprise_id;

    /**
     * 企业code
     */
    private String enterprise_code;

    /**
     * 企业名称
     */
    private Integer enterprise_name;

    /**
     * 项目管理id
     */
    private Integer project_id;

    /**
     * 类型 0 供应商 1 客户
     */
    private String type;

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
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
