package com.xinyirun.scm.bean.system.vo.master.org;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织机构关系表
 * </p>
 *
 * @author zxh
 * @since 2020-01-09
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "用户组织机构关系表", description = "用户组织机构关系表")
public class MUserOrgVo implements Serializable {

    private static final long serialVersionUID = -432721807027517188L;
    private Long id;

    /**
     * 员工主表id
     */
    private Long staff_id;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
