package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 员工岗位数量
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工岗位数量", description = "员工岗位数量")
@EqualsAndHashCode(callSuper=false)
public class MStaffPositionCountsVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 9022570492067210012L;

    private Long id;

    /**
     * 页面上激活的tabs:  0:全岗位,1:已设置岗位,2:未设置岗位
     */
    private int active_tabs_index;

    /**
     * 数量
     */
    private int count;

}
