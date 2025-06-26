package com.xinyirun.scm.bean.system.vo.master.org;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织机构关系，成员名称
 * </p>
 *
 * @author zxh
 * @since 2020-01-09
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "用户组织机构关系，成员名称", description = "用户组织机构关系，成员名称")
@EqualsAndHashCode(callSuper=false)
public class MStaffPositionOperationVo implements Serializable {

    private static final long serialVersionUID = -5753235810765558658L;

    private Long id;

    /**
     * 员工名称
     */
    private String staff_name;

    /**
     * 岗位名称
     */
    private String position_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

}
