package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MOrgCountsVo
 * @Description: 记录组织架构下的数量
 * @Author: zxh
 * @date: 2019/12/17
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "记录组织架构下的数量", description = "记录组织架构下的数量")
@EqualsAndHashCode(callSuper=false)
public class MOrgCountsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6103601680477614084L;

    /** 组织机构 数量 */
    private Long orgs_count;
    /** 集团信息 数量 */
    private Long group_count;
    /** 企业信息 数量 */
    private Long company_count;
    /** 部门信息 数量 */
    private Long dept_count;
    /** 岗位信息 数量 */
    private Long position_count;
    /** 员工信息 数量 */
    private Long staff_count;
}
