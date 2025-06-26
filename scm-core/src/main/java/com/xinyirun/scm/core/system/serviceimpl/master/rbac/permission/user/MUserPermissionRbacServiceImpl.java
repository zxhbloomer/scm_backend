package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.xinyirun.scm.bean.entity.master.menu.MMenuEntity;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.*;
import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.master.menu.MMenuMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.user.MUserPermissionRbacMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.user.IMUserPermissionRbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 用户权限获取的逻辑实现
 * @ClassName: MUserPermissionService
 * @Description:
 * @Author: zxh
 * @date: 2020/8/26
 * @Version: 1.0
 */
@Service
@Slf4j
public class MUserPermissionRbacServiceImpl implements IMUserPermissionRbacService {

    @Autowired
    private MUserPermissionRbacMapper mapper;

    @Autowired
    private MMenuMapper mMenuMapper;

    /**
     * 菜单权限数据，操作权限数据，顶部导航栏数据
     */
    @Override
    public PermissionAndTopNavBo getPermissionMenuTopNav(String pathOrIndex, String type, Long staff_id, String topNavCode) {
        PermissionAndTopNavBo permissionAndTopNavBo = new PermissionAndTopNavBo();
        /** 第一步：获取permission_menu的menu_root_id */
        Long menu_root_id = getPermissionMenuRootId(staff_id);

        /** 获取顶部导航栏数据 */
        PermissionTopNavBo permissionTopNavBo = getTopNavData( pathOrIndex, type, menu_root_id, staff_id, topNavCode);
        permissionAndTopNavBo.setTop_nav_data(permissionTopNavBo);

        /** 获取菜单数据 */
        List<PermissionMenuBo> permissionMenuBoList = getPermissionMenu(staff_id,  permissionTopNavBo.getActive_code(), menu_root_id);
        permissionAndTopNavBo.setUser_permission_menu(permissionMenuBoList);

        /** 获取所有nodes的id，为页面上展开所有的菜单服务 */
        List<String> allNodesId = getAllNodesId(menu_root_id);
        permissionAndTopNavBo.setNodes_id(allNodesId.toArray(new String[allNodesId.size()]));

        /** 获取所有路由数据 */
        List<PermissionMenuBo> all_routers = getAllRoutersBean(staff_id, menu_root_id);
        permissionAndTopNavBo.setAll_routers(all_routers);

        /** 获取操作权限数据 */
        List<PermissionOperationBo> permissionOperationBoList = getPermissionOperation(staff_id);
        permissionAndTopNavBo.setUser_permission_operation(permissionOperationBoList);

        /** 获取redirect数据 */
        PermissionMenuBo redirect = getRedirectBean(all_routers);
        permissionAndTopNavBo.setRedirect(redirect);

        permissionAndTopNavBo.setSession_id(SecurityUtil.getSessionId());
        permissionAndTopNavBo.setStaff_id(SecurityUtil.getStaff_id());

        return permissionAndTopNavBo;
    }

    /**
     * 获取所有路由数据
     * @param menu_root_id
     * @return
     */
    private List<PermissionMenuBo> getAllRoutersBean(Long staff_id, Long menu_root_id){
        return mapper.getAllRouters(staff_id, menu_root_id);
    }

    /**
     * 获取所有nodes的id，为页面上展开所有的菜单服务
     * @param menu_root_id
     * @return
     */
    private List<String> getAllNodesId(Long menu_root_id){
        return mapper.getAllNodesId(menu_root_id);
    }

    /**
     * 获取redirect的数据
     * @param all_routers
     * @return
     */
    private PermissionMenuBo getRedirectBean(List<PermissionMenuBo> all_routers){
        PermissionMenuBo redirect = mapper.getRedirectData();
        redirect.setRedirect(redirect.getPath());

        // 查找redirect的active top nav 的code
        for(PermissionMenuBo bo : all_routers){
            if(bo.getCode().equals(redirect.getCode())) {
                // 找到了数据
                redirect.getMeta().setActive_topnav_index(bo.getMeta().getActive_topnav_index());
                break;
            }
        }
        return redirect;
    }

    /**
     * 菜单权限数据
     */
    @Override
    public List<PermissionMenuBo> getPermissionMenu(Long staff_id, String top_nav_code, Long menu_root_id) {
        /** 获取该员工的权限： 角色权限  */
        // 角色权限
        List<PermissionMenuBo> roles_permission_menu = mapper.getPermissionMenu(staff_id, top_nav_code, menu_root_id);
        /** 权限合并 */
//        for(PermissionMenuBo vo:sysMenus) {
//            // 部门权限
//            PermissionMenuBo dept_permission_menu_results = filterData(dept_permission_menu, vo);
//            // 岗位权限
//            PermissionMenuBo position_permission_menu_results = filterData(position_permission_menu, vo);
//            // 员工权限
//            PermissionMenuBo staff_permission_menu_results = filterData(staff_permission_menu, vo);
//            // 角色权限
//            PermissionMenuBo roles_permission_menu_results = filterData(roles_permission_menu, vo);
//            // 排除权限
//            PermissionMenuBo remove_permission_menu_results = filterData(remove_permission_menu, vo);
//            /** 判断权限：（部门权限+ 岗位权限+ 员工权限+ 角色权限）- 排除权限 */
//            vo.setIs_enable(getPermissionValue(dept_permission_menu_results,
//                    position_permission_menu_results,
//                    staff_permission_menu_results,
//                    roles_permission_menu_results,
//                    remove_permission_menu_results));
//        }
//
        /** 设置菜单树bean，并返回 */
        List<PermissionMenuBo> rtnList = TreeUtil.getTreeList(roles_permission_menu, "menu_id");
//
//        /** 递归菜单树，设置默认菜单 */

        return rtnList;
    }

//    /**
//     * 菜单权限数据
//     */
//    @Override
//    public List<PermissionMenuBo> getPermissionMenu(Long staff_id, String top_nav_code) {
//        /** 判断是否有自定义菜单 */
//
//        /** 如果没有，获取该员工的权限：（部门权限+ 岗位权限+ 员工权限+ 角色权限）- 排除权限 */
//        // 获取系统菜单
//        List<PermissionMenuBo> sysMenus = mapper.getSystemMenu(top_nav_code);
//        // 部门权限defaultActive
//        List<PermissionMenuBo> dept_permission_menu = mapper.getPermissionMenu(staff_id, top_nav_code);
//        // 岗位权限
//        List<PermissionMenuBo> position_permission_menu = null;
//        // 员工权限
//        List<PermissionMenuBo> staff_permission_menu = null;
//        // 角色权限
//        List<PermissionMenuBo> roles_permission_menu = null;
//        // 排除权限
//        List<PermissionMenuBo> remove_permission_menu = null;
//        /** 权限合并 */
//        for(PermissionMenuBo vo:sysMenus) {
//            // 部门权限
//            PermissionMenuBo dept_permission_menu_results = filterData(dept_permission_menu, vo);
//            // 岗位权限
//            PermissionMenuBo position_permission_menu_results = filterData(position_permission_menu, vo);
//            // 员工权限
//            PermissionMenuBo staff_permission_menu_results = filterData(staff_permission_menu, vo);
//            // 角色权限
//            PermissionMenuBo roles_permission_menu_results = filterData(roles_permission_menu, vo);
//            // 排除权限
//            PermissionMenuBo remove_permission_menu_results = filterData(remove_permission_menu, vo);
//            /** 判断权限：（部门权限+ 岗位权限+ 员工权限+ 角色权限）- 排除权限 */
//            vo.setIs_enable(getPermissionValue(dept_permission_menu_results,
//                position_permission_menu_results,
//                staff_permission_menu_results,
//                roles_permission_menu_results,
//                remove_permission_menu_results));
//        }
//
//        /** 如果有
//         * TODO：暂时未实现
//         * */
//
//
//        /** 设置菜单树bean，并返回 */
//        List<PermissionMenuBo> rtnList = TreeUtil.getTreeList(sysMenus, "menu_id");
//
//        /** 递归菜单树，设置默认菜单 */
//
//        return rtnList;
//    }

    /**
     * 获取默认页面
     * TODO:有些问题，没有这个字段
     * @return
     */
    @Override
    public String getPermissionMenuDefaultPage() {
        /** 判断是否有自定义菜单 */
        /** 如果没有，获取default */
        MMenuEntity mMenuEntity = mMenuMapper.selectOne(new QueryWrapper<MMenuEntity>()
            .eq("default_open", true)
            .last("LIMIT 1")
        );
        /** 如果有
         * TODO：暂时未实现
         * */
        return mMenuEntity.getPath();
    }

    /**
     * 操作权限数据
     * @param staff_id
     * @return
     */
    @Override
    public List<PermissionOperationBo> getPermissionOperation(Long staff_id) {
        /** 获取操作权限数据 */
        List<PermissionOperationBo> list = mapper.getPermissionOperation(staff_id);
        return list;
    }

    /**
     * 查找集合中的数据，并返回
     * @param data
     * @param target_data
     * @return
     */
    private PermissionMenuBo filterData(List<PermissionMenuBo> data, PermissionMenuBo target_data){
        if(data == null) {
            return null;
        }
        Collection<PermissionMenuBo> filter = Collections2.filter(data, item -> item.getMenu_id().equals(target_data.getMenu_id()));
        return Iterables.getOnlyElement(filter);
    }

    /**
     * 判断权限：（部门权限+ 岗位权限+ 员工权限+ 角色权限）- 排除权限
     * @param dept_permission_menu          部门权限
     * @param position_permission_menu      岗位权限
     * @param staff_permission_menu         员工权限
     * @param roles_permission_menu         角色权限
     * @param remove_permission_menu        排除权限
     * @return
     */
    private boolean getPermissionValue(PermissionMenuBo dept_permission_menu,
        PermissionMenuBo position_permission_menu,
        PermissionMenuBo staff_permission_menu,
        PermissionMenuBo roles_permission_menu,
        PermissionMenuBo remove_permission_menu
        ){
        boolean rtn = false;
        if(dept_permission_menu != null){
            rtn = rtn || dept_permission_menu.getIs_enable();
        }
        if(position_permission_menu != null){
            rtn = rtn || position_permission_menu.getIs_enable();
        }
        if(staff_permission_menu != null){
            rtn = rtn || staff_permission_menu.getIs_enable();
        }
        if(roles_permission_menu != null){
            rtn = rtn || roles_permission_menu.getIs_enable();
        }
        if(remove_permission_menu != null){
            rtn = rtn & remove_permission_menu.getIs_enable();
        }

        return  rtn;
    }

    /**
     * 获取permission_menu的菜单root_id
     * @param staff_id
     * @return
     */
    private Long getPermissionMenuRootId(Long staff_id){
        return mapper.getPermissionMenuRootId(staff_id);
    }

    /**
     * 设置顶部导航栏数据
     * @param pathOrIndex
     * @param type
     * @return
     */
    private PermissionTopNavBo getTopNavData(String pathOrIndex, String type, Long menu_root_id, Long staff_id, String topNavCode){

        PermissionTopNavBo permissionTopNavBo = new PermissionTopNavBo();

        List<PermissionTopNavDetailBo> topList;

        /** 根据参数获取顶部导航栏数据 */
        switch (type) {
            case SystemConstants.TOP_NAV.TOP_NAV_FIND_BY_PATH:
                /** 按路径查询 */
                /** 获取导航栏数据 */
                topList = mapper.getTopNavByPath(menu_root_id, pathOrIndex, staff_id, topNavCode);
                if(topList.size() ==0){
                    // 没找到数据
                    throw new BusinessException("该账号没有分配权限，登录失败！");
                }
                permissionTopNavBo.setData(topList);
                /** 设置activeindex */
                try{
                    Collection<PermissionTopNavDetailBo> filter1 = Collections2.filter(topList, new Predicate<PermissionTopNavDetailBo>(){
                        @Override
                        public boolean apply(PermissionTopNavDetailBo input) {
                            if(input.getCode().equals(input.getActive_code())){
                                return true;
                            }else {
                                return false;
                            }
                        }
                    });

                    PermissionTopNavDetailBo bo1 = Iterables.getOnlyElement(filter1);
                    if (filter1 != null){
                        permissionTopNavBo.setActive_index(bo1.getIndex());
                        permissionTopNavBo.setActive_code(bo1.getCode());
                    }

                } catch (Exception e) {
                    log.debug("没有找到数据，找到第一个topnav");
                    permissionTopNavBo.setActive_index(topList.get(0).getIndex());
                    permissionTopNavBo.setActive_code(topList.get(0).getCode());
                }
                break;
            case SystemConstants.TOP_NAV.TOP_NAV_FIND_BY_INDEX:
                /** 按排序查询 */
                /** 获取导航栏数据 */
                topList = mapper.getTopNav(menu_root_id, staff_id, topNavCode);
                permissionTopNavBo.setData(topList);
                Collection<PermissionTopNavDetailBo> filter2 = Collections2.filter(topList, item -> item.getCode().equals(topNavCode));
                PermissionTopNavDetailBo bo2 = Iterables.getOnlyElement(filter2);
                permissionTopNavBo.setActive_index(bo2.getIndex());
                permissionTopNavBo.setActive_code(bo2.getCode());
                break;
            default:
                break;
        }

        return permissionTopNavBo;
    }
}
