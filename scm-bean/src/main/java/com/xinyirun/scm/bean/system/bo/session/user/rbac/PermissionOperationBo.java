package com.xinyirun.scm.bean.system.bo.session.user.rbac;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限页面操作表
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限页面操作表", description = "权限页面操作表")
@EqualsAndHashCode(callSuper=false)
public class PermissionOperationBo implements Serializable {

    private static final long serialVersionUID = 8300448389297059639L;

    private Long id;

    /**
     * 权限id
     */
    private Long permission_id;

    /**
     * operation_id，数据通过复制s_pages_function，为不破坏表连接关系，该字段记录s_pages_function.id
     */
    private Long operation_id;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    private Boolean is_enable;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 页面page_code
     */
    private String page_code;

    /**
     * 页面page_name
     */
    private String page_name;

    /**
     * 页面page_path
     */
    private String page_path;

    private String meta_title;

    /**
     * 类型：PAGE：主页面上，TABLE：表格上，POPUP：弹出框上
     */
    private String type;

    /**
     * 按钮id
     */
    private Long function_id;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 说明
     */
    private String descr;

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

    /**
     * 操作权限
     */
    private String operation_perms;

    /**
     * 操作权限说明
     */
    private String operation_descr;
}
