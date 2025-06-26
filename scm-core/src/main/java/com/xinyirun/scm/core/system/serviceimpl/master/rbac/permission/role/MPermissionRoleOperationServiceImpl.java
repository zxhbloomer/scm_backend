package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.role;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionMenuEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionOperationEntity;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPagesEntity;
import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionMenuOperationVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionMenuVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionOperationVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMenuMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionOperationMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionPagesMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.role.MPermissionRoleOperationMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionMenuService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionOperationService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.role.IMPermissionRoleOperationService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
@Service
public class MPermissionRoleOperationServiceImpl extends BaseServiceImpl<MPermissionMapper, MPermissionEntity> implements IMPermissionRoleOperationService {

    @Autowired
    private MPermissionRoleOperationMapper mapper;

    @Autowired
    private MPermissionMenuMapper mPermissionMenuMapper;

    @Autowired
    private MPermissionPagesMapper mPermissionPagesMapper;

    @Autowired
    private MPermissionOperationMapper mPermissionOperationMapper;

    @Autowired
    private IMPermissionService imPermissionService;

    @Autowired
    private IMPermissionOperationService imPermissionOperationService;

    @Autowired
    private IMPermissionMenuService imPermissionMenuService;

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public OperationMenuVo getTreeData(OperationMenuDataVo searchCondition) {
        OperationMenuVo mMenuVo = new OperationMenuVo();
        // 查询 菜单 数据
        List<OperationMenuDataVo> list = mapper.select(searchCondition);
        // 设置数据条数
        mMenuVo.setData_count(list.size());
        // 格式化depthid
        setDepthId(list);

        // 设置树bean
        List<OperationMenuDataVo> rtnList = TreeUtil.getTreeList(list, "menu_id");

        mMenuVo.setMenu_data(rtnList);
        return mMenuVo;
    }

    /**
     * 格式化depth_id，parent_depth_id成数组
     * @param list
     */
    private void setDepthId(List<OperationMenuDataVo> list){
        // 循环结果，格式化depth_id，parent_depth_id成数组
        for (OperationMenuDataVo vo:list) {
            // 格式化depth_id
            if(vo.getDepth_id() != null) {
                String[] split_depth_id = vo.getDepth_id().split(",");
                List<Long> depth_id_array = new ArrayList<>();
                for (int i = 0; i < split_depth_id.length; i++) {
                    depth_id_array.add(Long.valueOf(split_depth_id[i]));
                }
                vo.setDepth_id_array(depth_id_array);
            }
            // 格式化parent_depth_id
            if(vo.getParent_depth_id() != null) {
                String[] split_parent_depth_id = vo.getParent_depth_id().split(",");
                List<Long> parent_depth_id_array = new ArrayList<>();
                for (int i = 0; i < split_parent_depth_id.length; i++) {
                    parent_depth_id_array.add(Long.valueOf(split_parent_depth_id[i]));
                }
                vo.setParent_depth_id_array(parent_depth_id_array);
            }
        }
    }

    /**
     * 复制选中的菜单
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setSystemMenuData2PermissionData(OperationMenuDataVo searchCondition) {

        // 表复制，m_menu->m_permission_menu
        copyMmenu2MPermissionMenu(searchCondition);
        // 表复制，s_pages->m_permission_pages
        copySpages2MPermissionPages(searchCondition);
        // 表复制，s_pages_function->m_permission_operation
        copyMPermissionOperation2MPermissionOperation(searchCondition);
        // 以上复制完成后，更新m_permission.menu_id
        MPermissionVo vo = imPermissionService.selectByid(searchCondition.getPermission_id());
        vo.setMenu_id(searchCondition.getRoot_id());
        imPermissionService.update(vo);
    }

    /**
     * 表复制，m_menu->m_permission_menu
     * @param searchCondition
     * @return
     */
    private int copyMmenu2MPermissionMenu(OperationMenuDataVo searchCondition) {
        // m_menu --copy-->m_permission_menu
        MPermissionMenuEntity entity = new MPermissionMenuEntity();
//        entity.setTenant_id(searchCondition.getTenant_id());
        entity.setC_id(searchCondition.getC_id());
        entity.setU_id(searchCondition.getU_id());
        entity.setC_time(LocalDateTime.now());
        entity.setU_time(LocalDateTime.now());
        entity.setDbversion(0);
        entity.setMenu_id(searchCondition.getRoot_id());
        entity.setPermission_id(searchCondition.getPermission_id());
        entity.setIs_enable(false);
        int count = mPermissionMenuMapper.copyMMenu2MPermissionMenu(entity);
        return count;
    }

    /**
     * 表复制，s_pages->m_permission_pages
     * @param searchCondition
     * @return
     */
    private int copySpages2MPermissionPages(OperationMenuDataVo searchCondition) {
        // m_menu --copy-->m_permission_menu
        MPermissionPagesEntity entity = new MPermissionPagesEntity();
//        entity.setTenant_id(searchCondition.getTenant_id());
        entity.setC_id(searchCondition.getC_id());
        entity.setU_id(searchCondition.getU_id());
        entity.setC_time(LocalDateTime.now());
        entity.setU_time(LocalDateTime.now());
        entity.setPermission_id(searchCondition.getPermission_id());
        entity.setDbversion(0);
        int count = mPermissionPagesMapper.copySPages2MPermissionPages(entity, searchCondition.getRoot_id());
        return count;
    }

    /**
     * 表复制，s_pages_function->m_permission_operation
     * @param searchCondition
     * @return
     */
    private int copyMPermissionOperation2MPermissionOperation(OperationMenuDataVo searchCondition) {
        // m_menu --copy-->m_permission_menu
        MPermissionOperationEntity entity = new MPermissionOperationEntity();
        entity.setC_id(searchCondition.getC_id());
        entity.setU_id(searchCondition.getU_id());
        entity.setC_time(LocalDateTime.now());
        entity.setU_time(LocalDateTime.now());
        entity.setPermission_id(searchCondition.getPermission_id());
        entity.setDbversion(0);
        entity.setIs_enable(false);
        int count = mPermissionOperationMapper.copyMPermissionOperation2MPermissionOperation(entity, searchCondition.getRoot_id());
        return count;
    }

    /**
     * 保存权限操作数据和菜单权限, 清楚用户查询菜单缓存
     * @param condition
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, allEntries=true)
    public boolean savePermission(MPermissionMenuOperationVo condition) {
        // 获取菜单权限数据
        List<MPermissionMenuVo> menu_data = condition.getMenu_data();
        // 获取功能操作的数据
        List<MPermissionOperationVo> operation_data = condition.getOperation_data();

        // 更新菜单逻辑
        boolean rtn_menu_update = saveMenuPermissionData(menu_data);
        // 更新操作权限逻辑
        boolean rtn_operation_update = saveOperationPermissionData(operation_data);

        // 更新 m_permission 时间
        if (!CollectionUtils.isEmpty(operation_data)) {
            Long permissionId = operation_data.get(0).getPermission_id();
            imPermissionService.update(new LambdaUpdateWrapper<MPermissionEntity>()
                    .eq(MPermissionEntity::getId, permissionId)
                    .set(MPermissionEntity::getU_time, LocalDateTime.now())
            );
        }

        return rtn_menu_update & rtn_operation_update;
    }

    /**
     * 更新菜单权限
     * @param menu_data
     * @return
     */
    private boolean saveMenuPermissionData(List<MPermissionMenuVo> menu_data){
        List<Long> idList = new ArrayList<>();
        menu_data.forEach(bean -> {
            idList.add(bean.getId());
        });

        List<MPermissionMenuEntity> menuEntities = imPermissionMenuService.listByIds(idList);
        // 转化成map
        Map<Long, MPermissionMenuVo> menuVoMap =  Maps.uniqueIndex(menu_data, new Function <MPermissionMenuVo,Long>() {
            @Override
            public Long apply(MPermissionMenuVo vo) {
                return vo.getId();
            }});
        // 设置值
        menuEntities.forEach(bean -> {
            bean.setIs_enable(menuVoMap.get(bean.getId()).getIs_enable());
        });

        boolean rtn = imPermissionMenuService.updateBatchById(menuEntities);
        return rtn;
    }

    /**
     * 更新操作权限
     * @param operation_data
     * @return
     */
    private boolean saveOperationPermissionData(List<MPermissionOperationVo> operation_data){
        // 先去重，保留每个id最后一个对象
        Map<Long, MPermissionOperationVo> dedupMap = new java.util.HashMap<>();
        for (MPermissionOperationVo vo : operation_data) {
            dedupMap.put(vo.getId(), vo);
            System.out.println("id: " + vo.getId()+ ", perms"+vo.getPerms() + ", is_enable: " + vo.getIs_enable());
        }
        List<Long> idList = new ArrayList<>(dedupMap.keySet());
        List<MPermissionOperationEntity> operationEntities = imPermissionOperationService.listByIds(idList);
        // 转化成map
        Map<Long, MPermissionOperationVo> operationVoMap = dedupMap;
        // 设置值
        operationEntities.forEach(bean -> {
            if (operationVoMap.containsKey(bean.getId())) {
                bean.setIs_enable(operationVoMap.get(bean.getId()).getIs_enable());
            }
        });
        boolean rtn = imPermissionOperationService.updateBatchById(operationEntities);
        return rtn;
    }
}
