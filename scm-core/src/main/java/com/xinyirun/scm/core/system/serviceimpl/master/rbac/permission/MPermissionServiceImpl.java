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
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionRoleTransferVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionTransferVo;
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
    public MPermissionRoleTransferVo getPermissionTransferList(MPermissionTransferVo condition) {
        MPermissionRoleTransferVo rtn = new MPermissionRoleTransferVo();
        // 获取全部用户
        rtn.setPermission_all(mapper.getAllPermissionTransferList(new MPermissionTransferVo()));
        // 获取该岗位已经设置过得用户
        List<Integer> rtnList = mapper.getUsedPermissionTransferList(condition);
        rtn.setRole_permission(rtnList.toArray(new Integer[rtnList.size()]));
        return rtn;
    }

    @Override
    public MPermissionRoleTransferVo setPermissionTransfer(MPermissionTransferVo bean) {
        // 操作日志bean初始化
        CustomOperateBo cobo = new CustomOperateBo();
        cobo.setName(SystemConstants.OPERATION.M_PERMISSION_ROLE.OPER_ROLE_STAFF);
        cobo.setPlatform(SystemConstants.PLATFORM.PC);
        cobo.setType(OperationEnum.BATCH_UPDATE_INSERT_DELETE);


        // 查询出需要剔除的权限list
        List<MPermissionRoleOperationVo> deleteMemberList = mapper.selectDeleteMember(bean);
        // 查询出需要添加的权限list
        List<MPermissionRoleOperationVo> insertMemberList = mapper.selectInsertMember(bean);

        // 执行保存逻辑，并返回权限数量
        return this.saveMemberList(deleteMemberList, insertMemberList, cobo, bean);
    }

    /**
     * 保存员工关系，删除剔除的员工，增加选择的员工
     * @param deleteMemberList
     * @param insertMemberList
     * @param cobo
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MPermissionRoleTransferVo saveMemberList(List<MPermissionRoleOperationVo> deleteMemberList, List<MPermissionRoleOperationVo> insertMemberList,CustomOperateBo cobo, MPermissionTransferVo bean) {

        List<CustomOperateDetailBo> detail = new ArrayList<>();

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 操作日志：记录删除前数据
        for(MPermissionRoleOperationVo vo : deleteMemberList) {
            CustomOperateDetailBo<MPermissionRoleOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.DELETE);
            bo.setTable_name(SystemConstants.OPERATION.M_STAFF_ORG.TABLE_NAME);
            bo.setNewData(null);
            bo.setOldData(vo);
            setColumnsMap(bo);
            detail.add(bo);
        }
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 删除剔除的权限
        List<MPermissionRoleEntity> delete_list = BeanUtilsSupport.copyProperties(deleteMemberList, MPermissionRoleEntity.class, new String[] {"c_time", "u_time"});
        List<Integer> ids = Lists.newArrayList();
        delete_list.forEach(beans -> {
            ids.add(beans.getId());
        });
        if (ArrayPfUtil.isNotEmpty(ids)) {
            permissionRoleService.removeByIds(ids);
        }

        // 增加选择的权限
        Integer[] role_permissions = new Integer[insertMemberList.size()];
        int i = 0;
        List<MPermissionRoleEntity> mPermissionRoleEntities = new ArrayList<>();
        for( MPermissionRoleOperationVo vo : insertMemberList ) {
            MPermissionRoleEntity mPermissionRoleEntity = new MPermissionRoleEntity();
            mPermissionRoleEntity.setPermission_id(vo.getId());
            mPermissionRoleEntity.setRole_id(bean.getRole_id());
            mPermissionRoleEntities.add(mPermissionRoleEntity);

            role_permissions[i] = vo.getId();
            i = i + 1;
        }

        permissionRoleService.saveBatch(mPermissionRoleEntities);

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 记录更新后数据
        MPermissionTransferVo condition = new MPermissionTransferVo();
        condition.setRole_id(bean.getRole_id());
        condition.setRole_permissions(role_permissions);
        List<MPermissionRoleOperationVo> selectMemberList = mapper.selectMember(bean);
        for(MPermissionRoleOperationVo vo: selectMemberList) {
            // 操作日志：记录新增数据
            CustomOperateDetailBo<MPermissionRoleOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.ADD);
            bo.setTable_name(SystemConstants.OPERATION.M_PERMISSION_ROLE.TABLE_NAME);
            bo.setNewData(vo);
            bo.setOldData(new MPermissionRoleOperationVo());
            setColumnsMap(bo);
            detail.add(bo);
        }
        cobo.setDetail(detail);
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 保存操作日志
//        sLogOperService.save(cobo);

        // 查询最新数据并返回
        // 获取该岗位已经设置过得用户
        List<Integer> rtnList = mapper.getUsedPermissionTransferList(condition);
        MPermissionRoleTransferVo mPermissionRoleTransferVo = new MPermissionRoleTransferVo();
        mPermissionRoleTransferVo.setRole_permission_count(rtnList.size());
        return mPermissionRoleTransferVo;
    }

    /**
     * 设置列相对应的列名称
     */
    private void setColumnsMap(CustomOperateDetailBo<MPermissionRoleOperationVo> bean){
        Map<String, String> columns = new ConcurrentHashMap<>();
        columns.put("permission_name", "权限名称");
        columns.put("role_name", "角色名称");
        columns.put("c_id", "新增人id");
        columns.put("c_time", "新增时间");
        columns.put("u_id", "更新人id");
        columns.put("u_time", "更新时间");
        bean.setColumns(columns);
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

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, allEntries=true)
    public void enableById(MPermissionVo searchCondition) {
        MPermissionVo vo = mapper.selectByid(searchCondition.getId());
        vo.setIs_enable(!vo.getIs_enable());
        MPermissionEntity entity = (MPermissionEntity)BeanUtilsSupport.copyProperties(vo, MPermissionEntity.class);
        saveOrUpdate(entity);
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
        MMenuEntity mMenuEntity = menuMapper.selectOne(new QueryWrapper<MMenuEntity>()
            .select("distinct root_id")
//            .eq("tenant_id",operationMenuDataVo.getTenant_id())
        );
        operationMenuDataVo.setPermission_id(entity.getId());
        operationMenuDataVo.setRoot_id(mMenuEntity.getRoot_id());
        imPermissionDeptOperationService.setSystemMenuData2PermissionData(operationMenuDataVo);

        /** 最后更新m_permission中menu_id */
        entity.setMenu_id(mMenuEntity.getRoot_id());
        int count_update = mapper.updateById(entity);

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
        MMenuEntity mMenuEntity = menuMapper.selectOne(new QueryWrapper<MMenuEntity>()
                        .select("distinct root_id")
        );
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
