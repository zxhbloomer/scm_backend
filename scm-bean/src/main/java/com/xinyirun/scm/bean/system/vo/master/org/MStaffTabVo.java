package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 员工页签
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "员工页签", description = "员工页签")
@EqualsAndHashCode(callSuper=false)
public class MStaffTabVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -4436276336523014155L;

    /**
     * 页签内的table数据
     */
    private List<MStaffTabDataVo> list;

    /**
     * 当组织下所有员工count
     */
    private Integer currentOrgStaffCount;

    /**
     * 所有员工count
     */
    private Integer allOrgStaffCount;
}
