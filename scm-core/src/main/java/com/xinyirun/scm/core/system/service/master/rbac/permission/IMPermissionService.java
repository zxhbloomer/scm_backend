package com.xinyirun.scm.core.system.service.master.rbac.permission;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MMenuRootNodeListVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
public interface IMPermissionService extends IService<MPermissionEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MPermissionVo> selectPage(MPermissionVo searchCondition) ;

    /**
     * 获取角色权限列表，页面查询
     */
    IPage<MPermissionVo> selectRolePermissionPage(MPermissionVo searchCondition) ;

    /**
     * 获取角色权限列表，不分页查询
     */
    List<MPermissionVo> selectCascaderList(MPermissionVo searchCondition) ;

    /**
     * 根据员工id获取权限
     */
    Integer selectPermissionsCountByStaffId(Long staff_id);

    /**
     * 获取角色已分配的权限ID列表
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Integer> getRoleAssignedPermissionIds(Long roleId);




//    /**
//     * 获取所有数据
//     */
//    List<MPermissionVo> select(MPermissionVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<MPermissionVo> selectIdsIn(List<MPermissionVo> searchCondition) ;


    /**
     * 设置/取消设置 为管理员
     * @param searchCondition
     * @return
     */
    void adminById(MPermissionVo searchCondition);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MPermissionVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @return
     */
    InsertResultAo<MPermissionVo> insert(MPermissionVo mPermissionVo, OperationMenuDataVo operationMenuDataVo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @return
     */
    void refresh(MPermissionVo mPermissionVo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @return
     */
    UpdateResultAo<MPermissionVo> update(MPermissionVo vo);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MPermissionVo selectByid(Long id);

    /**
     * 部门权限表数据获取系统菜单根节点
     * @param vo
     * @return
     */
    MMenuRootNodeListVo getSystemMenuRootList(MMenuRootNodeListVo vo);

//    /**
//     * 判断是否已经选择了菜单
//     * @param searchCondition
//     * @return
//     */
//    Boolean isAlreadySetMenuId(MPermissionVo searchCondition);
}
