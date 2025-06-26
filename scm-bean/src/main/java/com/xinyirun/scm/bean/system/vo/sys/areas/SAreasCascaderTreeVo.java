package com.xinyirun.scm.bean.system.vo.sys.areas;

import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 省市区
 * </p>
 *
 * @author zxh
 * @since 2019-10-31
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "省市区", description = "省市区")
@EqualsAndHashCode(callSuper=false)
public class SAreasCascaderTreeVo extends TreeNode implements Serializable {

    private static final long serialVersionUID = 8330843713725719097L;

    /**
     * 级联控件使用，父结点id
     */
    private Long value;

}
