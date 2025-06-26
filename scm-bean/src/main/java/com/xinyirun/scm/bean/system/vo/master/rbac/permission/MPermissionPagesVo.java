package com.xinyirun.scm.bean.system.vo.master.rbac.permission;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权限页面表
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "权限页面表", description = "权限页面表")
@EqualsAndHashCode(callSuper=false)
public class MPermissionPagesVo implements Serializable {

    private static final long serialVersionUID = 3970592572301939064L;

    private Long id;

    /**
     * 权限id
     */
    private Long permission_id;

    /**
     * 权限菜单id
     */
    private Long permission_menu_id;

    /**
     * page_id，数据通过复制s_pages，为不破坏表连接关系，该字段记录s_pages.id
     */
    private Long page_id;

    /**
     * 是否启用(0:false-已禁用,1:true-已启用)
     */
    private Boolean is_enable;

    /**
     * 配置vue export default  name时所使用的type：constants_program.P_VUE_SETTING
     */
    private String code;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 模块地址：@/views/10_system/vuesetting/vue
     */
    private String component;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 页面的名称
     */
    private String meta_title;

    /**
     * 菜单中显示的icon
     */
    private String meta_icon;

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
}
