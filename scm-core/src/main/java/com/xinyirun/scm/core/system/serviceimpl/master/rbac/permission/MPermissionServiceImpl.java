package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.entity.master.menu.MMenuEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionOperationEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionRoleEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateDetailBo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionRoleOperationVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.*;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.ArrayPfUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.menu.MMenuMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMenuMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionOperationMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionPagesMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.dept.IMPermissionDeptOperationService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.log.operate.SLogOperServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
@Service
public class MPermissionServiceImpl extends BaseServiceImpl<MPermissionMapper, MPermissionEntity> implements IMPermissionService {

    @Autowired
    private MPermissionMapper mapper;

    @Autowired
    private MMenuMapper menuMapper;

    @Autowired
    private IMPermissionDeptOperationService imPermissionDeptOperationService;

    @Autowired
    private MPermissionRoleServiceImpl permissionRoleService;

    @Autowired
    private SLogOperServiceImpl sLogOperService;

    @Autowired
    private MPermissionPagesMapper permissionPagesMapper;

    @Autowired
    private MPermissionOperationMapper permissionOperationMapper;

    @Autowired
    private MPermissionMenuMapper mPermissionMenuMapper;

    @Autowired
    private MPermissionPagesMapper mPermissionPagesMapper;

    @Autowired
    private MPermissionOperationMapper mPermissionOperationMapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MPermissionVo> selectPage(MPermissionVo searchCondition) {
        // 分页条件
        Page<MPermissionVo> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取角色权限列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MPermissionVo> selectRolePermissionPage(MPermissionVo searchCondition) {
        // 分页条件
        Page<MPermissionVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectRolePermissionPage(pageCondition, searchCondition);
    }

    @Override
    public List<MPermissionVo> selectCascaderList(MPermissionVo searchCondition) {
        return mapper.selectCascaderList(searchCondition);
    }

    @Override
    public Integer selectPermissionsCountByStaffId(Long staff_id) {
        return mapper.selectPermissionsByStaffId(staff_id);
    }

    @Override
    public List<Integer> getRoleAssignedPermissionIds(Long roleId) {
        return mapper.selectPermissionIdsByRoleId(roleId);
    }






    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MPermissionVo> selectIdsIn(List<MPermissionVo> searchCondition) {
        // 查询 数据
        List<MPermissionVo> list = mapper.selectIdsIn(searchCondition);
        return list;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminById(MPermissionVo searchCondition) {
        MPermissionEntity entity = mapper.selectById(searchCondition.getId());
        if (null == entity.getIs_admin() || Boolean.FALSE == entity.getIs_admin()) {
            entity.setIs_admin(Boolean.TRUE);
        } else {
            entity.setIs_admin(Boolean.FALSE);
        }
        mapper.updateById(entity);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, allEntries=true)
    public void deleteByIdsIn(List<MPermissionVo> searchCondition) {
        // 提取权限ID列表
        List<Long> permissionIds = searchCondition.stream()
                .map(MPermissionVo::getId)
                .collect(Collectors.toList());
        
        // 删除前校验
        CheckResultAo checkResult = validatePermissionDeletion(permissionIds);
        if (!checkResult.isSuccess()) {
            throw new BusinessException(checkResult.getMessage());
        }
        
        // 执行逻辑删除
        List<MPermissionVo> list = mapper.selectIdsIn(searchCondition);
        list.forEach(
            bean -> {
                bean.setIs_del(!bean.getIs_del());
            }
        );
        List<MPermissionEntity> entities = BeanUtilsSupport.copyProperties(list, MPermissionEntity.class);
        saveOrUpdateBatch(entities, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param mPermissionVo
     * @param operationMenuDataVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<MPermissionVo> insert(MPermissionVo mPermissionVo, OperationMenuDataVo operationMenuDataVo) {

        /** 插入到权限表中 */
        // 插入前check
        CheckResultAo cr = checkLogic(mPermissionVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        MPermissionEntity entity = (MPermissionEntity)BeanUtilsSupport.copyProperties(mPermissionVo, MPermissionEntity.class);
        mPermissionVo.setIs_del(false);
        int count_insert = mapper.insert(entity);

        /** 复制选中系统菜单，和操作权限  */
        // 根据用户选择的menu_id获取对应的根节点ID
        Long rootId = null;
        if (entity.getMenu_id() != null) {
            MMenuEntity selectedMenu = menuMapper.selectById(entity.getMenu_id());
            if (selectedMenu != null) {
                rootId = selectedMenu.getRoot_id() != null ? selectedMenu.getRoot_id() : entity.getMenu_id();
            }
        } else {
            // 如果用户未选择菜单，保持原有默认逻辑
            MMenuEntity mMenuEntity = menuMapper.selectOne(new QueryWrapper<MMenuEntity>()
                .select("distinct root_id")
//                .eq("tenant_id",operationMenuDataVo.getTenant_id())
            );
            if (mMenuEntity != null) {
                rootId = mMenuEntity.getRoot_id();
                entity.setMenu_id(rootId);
                mapper.updateById(entity);
            }
        }
        
        // 复制选中系统菜单和操作权限
        if (rootId != null) {
            operationMenuDataVo.setPermission_id(entity.getId());
            operationMenuDataVo.setRoot_id(rootId);
            imPermissionDeptOperationService.setSystemMenuData2PermissionData(operationMenuDataVo);
        }

        if(count_insert == 0 ){
            throw new InsertErrorException("保存失败，请查询后重新再试。");
        }
        return InsertResultUtil.OK(selectByid(entity.getId()));
    }

    /**
     * 刷新权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refresh(MPermissionVo mPermissionVo) {
        // 查询旧数据
        List<MPermissionMenuVo> originalPermissionMenuVos = mapper.selectPermissionMenu(mPermissionVo.getId());

        List<MPermissionOperationVo> originalPermissionOperationVos = permissionOperationMapper.selectByPermissionId(mPermissionVo.getId());
        // 删除旧数据
        mapper.deletePermissionMenu(mPermissionVo.getId());
        permissionPagesMapper.deleteByPermissionId(mPermissionVo.getId());
        permissionOperationMapper.deleteByPermissionId(mPermissionVo.getId());
        /** 复制选中系统菜单，和操作权限  */
        MMenuEntity mMenuEntity = mapper.selectMenuByPermissionId(mPermissionVo.getId());
        if (mMenuEntity == null) {
            throw new BusinessException("权限未关联有效菜单，无法重置");
        }
        OperationMenuDataVo operationMenuDataVo = new OperationMenuDataVo();
        operationMenuDataVo.setPermission_id(mPermissionVo.getId());
        operationMenuDataVo.setRoot_id(mMenuEntity.getRoot_id());
        imPermissionDeptOperationService.setSystemMenuData2PermissionData(operationMenuDataVo);

        if (originalPermissionMenuVos != null && originalPermissionMenuVos.size()>0) {
            mPermissionMenuMapper.updateMPermissionMenu(originalPermissionMenuVos, mPermissionVo.getId());
        }

        if (originalPermissionOperationVos != null && originalPermissionOperationVos.size()>0) {
            permissionOperationMapper.updatePermissionOperation(originalPermissionOperationVos, mPermissionVo.getId());
        }

    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<MPermissionVo> update(MPermissionVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        MPermissionEntity entity = (MPermissionEntity) BeanUtilsSupport.copyProperties(vo, MPermissionEntity.class);
        vo.setC_id(null);
        vo.setC_time(null);
        int count = mapper.updateById(entity);
        if(count == 0){
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(selectByid(entity.getId()));
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MPermissionVo selectByid(Long id){
        return mapper.selectByid(id);
    }

    /**
     * 权限删除前校验方法
     * @param permissionIds 要删除的权限ID列表
     * @return 校验结果
     */
    private CheckResultAo validatePermissionDeletion(List<Long> permissionIds) {
        for (Long permissionId : permissionIds) {
            // 获取权限信息
            MPermissionEntity permission = getById(permissionId);
            if (permission == null || permission.getIs_del()) {
                continue; // 跳过不存在或已删除的权限
            }
            
            // 1. 检查是否为系统管理员权限
            if (permission.getIs_admin() != null && permission.getIs_admin()) {
                return CheckResultUtil.NG(
                    String.format("系统管理员权限'%s'不能删除", permission.getName()));
            }
            
            // 2. 检查角色关联
            int roleCount = countPermissionRoleRelations(permissionId);
            if (roleCount > 0) {
                return CheckResultUtil.NG(
                    String.format("权限'%s'被%d个角色使用，请先解除关联", 
                    permission.getName(), roleCount));
            }
            
            // 3. 检查员工直接关联
            int staffCount = countPermissionStaffRelations(permissionId);
            if (staffCount > 0) {
                return CheckResultUtil.NG(
                    String.format("权限'%s'被%d个员工直接使用，请先解除关联", 
                    permission.getName(), staffCount));
            }
            
            // 4. 检查岗位关联
            int positionCount = countPermissionPositionRelations(permissionId);
            if (positionCount > 0) {
                return CheckResultUtil.NG(
                    String.format("权限'%s'被%d个岗位使用，请先解除关联", 
                    permission.getName(), positionCount));
            }
        }
        return CheckResultUtil.OK();
    }
    
    /**
     * 统计权限-角色关联数量
     */
    private int countPermissionRoleRelations(Long permissionId) {
        return mapper.countPermissionRoleRelations(permissionId);
    }
    
    /**
     * 统计权限-员工关联数量
     */
    private int countPermissionStaffRelations(Long permissionId) {
        return mapper.countPermissionStaffRelations(permissionId);
    }
    
    /**
     * 统计权限-岗位关联数量
     */
    private int countPermissionPositionRelations(Long permissionId) {
        return mapper.countPermissionPositionRelations(permissionId);
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MPermissionVo vo, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 部门权限表数据获取系统菜单根节点
     * @param vo
     * @return
     */
    @Override
    public MMenuRootNodeListVo getSystemMenuRootList(MMenuRootNodeListVo vo) {
        // 获取根节点list
        List<MMenuRootNodeVo> list = mapper.getSystemMenuRootList(vo);

        // 获取默认值
        MMenuEntity entity = menuMapper.selectOne(new QueryWrapper<MMenuEntity>()
            .eq("is_default", true)
            .eq("type", DictConstant.DICT_SYS_MENU_TYPE_ROOT)
        );

        MMenuRootNodeVo default_vo = new MMenuRootNodeVo();
        default_vo.setId(entity.getId());
        default_vo.setValue(entity.getId());
        default_vo.setLabel(entity.getName());
        MMenuRootNodeListVo rtn = new MMenuRootNodeListVo();
        rtn.setNodes(list);
        rtn.setDefault_node(default_vo);

        return rtn;
    }

//    /**
//     * 判断是否已经选择了菜单
//     * @param searchCondition
//     * @return
//     */
//    @Override
//    public Boolean isAlreadySetMenuId(MPermissionVo searchCondition){
//        // 获取默认值
//        Integer count = mapper.selectCount(new QueryWrapper<MPermissionEntity>()
//            .eq("tenant_id",searchCondition.getTenant_id())
//            .eq("id",searchCondition.getId())
//            .isNotNull("menu_id" )
//        );
//        if(count == 0) {
//            return false;
//        } else {
//            return true;
//        }
//    }

}
