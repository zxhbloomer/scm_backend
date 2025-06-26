package com.xinyirun.scm.bean.system.vo.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

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
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工岗位", description = "员工岗位")
@EqualsAndHashCode(callSuper=false)
public class MStaffPositionVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -2807973727279079173L;

    /**
     * 员工id
     */
    private Long id;

    private String group_name;
    private String company_name;
    private String dept_name;

    /**
     * 岗位id
     */
    private Long position_id;
    private Boolean position_settled;

    /**
     * 页面上激活的tabs:  0:全岗位,1:已设置岗位,2:未设置岗位
     */
    private int active_tabs_index;

    /**
     * 岗位名称
     */
    private String position_name;

    /**
     * 岗位数据
     */
    private IPage<MPositionVo> list;

    /**
     * 全岗位
     */
    private int all;

    /**
     * 已设置岗位
     */
    private int settled;

    /**
     * 未设置岗位
     */
    private int unsettled;

    /**
     * 租户id
     */
//    private Long tenant_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
