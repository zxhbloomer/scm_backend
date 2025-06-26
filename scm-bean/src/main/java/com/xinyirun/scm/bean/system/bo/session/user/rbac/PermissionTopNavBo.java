package com.xinyirun.scm.bean.system.bo.session.user.rbac;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 顶部导航栏数据
 * @ClassName: PermissionTopNavBo
 * @Description:
 * @Author: zxh
 * @date: 2019/11/14
 * @Version: 1.0
 */
@Data
// @ApiModel(value = "顶部导航栏数据", description = "顶部导航栏数据")
@EqualsAndHashCode(callSuper=false)
public class PermissionTopNavBo implements Serializable {

    private static final long serialVersionUID = 1207488879093213595L;

    /**
     * 顶部导航栏数据
     */
    private List<PermissionTopNavDetailBo> data;

    /**
     * 默认高亮的顶部导航栏
     */
    private String active_index;
    private String active_code;

}
