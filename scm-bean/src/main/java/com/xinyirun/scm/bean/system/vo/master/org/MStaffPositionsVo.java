package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 员工岗位
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工岗位bean", description = "员工岗位bean")
@EqualsAndHashCode(callSuper=false)
public class MStaffPositionsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4739350823318840931L;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 岗位id
     */
    private Long position_id;

    /**
     * 岗位名称
     */
    private String position_name;

    /**
     * 岗位简称
     */
    private String position_simple_name;

}
