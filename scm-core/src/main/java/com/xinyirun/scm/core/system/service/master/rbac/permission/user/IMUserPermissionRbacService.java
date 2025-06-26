package com.xinyirun.scm.core.system.service.master.rbac.permission.user;

import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionAndTopNavBo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionMenuBo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionOperationBo;

import java.util.List;

/**
 * @ClassName:
 * @Description: 获取用户的权限
 * @Author: zxh
 * @date: 2020/8/26
 * @Version: 1.0
 */
public interface IMUserPermissionRbacService {

    /**
     * 菜单权限数据，顶部导航栏
     */
    PermissionAndTopNavBo getPermissionMenuTopNav(String pathOrIndex, String type, Long staff_id, String topNavCode);

    /**
     * 菜单权限数据
     */
    List<PermissionMenuBo> getPermissionMenu(Long staff_id, String top_nav_code, Long menu_root_id);

    /**
     * 操作权限数据
     */
    List<PermissionOperationBo> getPermissionOperation(Long staff_id);

    /**
     * 获取默认页面
     * @return
     */
    String getPermissionMenuDefaultPage();

}
