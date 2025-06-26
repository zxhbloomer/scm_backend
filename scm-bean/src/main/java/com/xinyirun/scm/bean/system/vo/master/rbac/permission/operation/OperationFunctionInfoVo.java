package com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 页面按钮vo
 * </p>
 *
 * @author zxh
 * @since 2019-11-01
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "页面按钮vo", description = "页面按钮vo")
@EqualsAndHashCode(callSuper=false)
public class OperationFunctionInfoVo implements Serializable {

    private static final long serialVersionUID = 7072150562389901473L;

    private Long id;

    /**
     * 权限主键
     */
    private Long permission_id;

    /**
     * 按钮编号：字典表过来
     */
    private String code;

    /**
     * 按钮名称
     */
    private String name;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 值
     */
    private Boolean is_enable;
}
