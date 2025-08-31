package com.xinyirun.scm.core.system.serviceimpl.sys.rbac.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMapper;
import com.xinyirun.scm.core.system.mapper.sys.rbac.role.SRoleMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.rbac.role.ISRoleService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.SRoleAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.role.MRolePositionServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-11
 */
@Service
public class SRoleServiceImpl extends BaseServiceImpl<SRoleMapper, SRoleEntity> implements ISRoleService {

    @Autowired
    private SRoleMapper sRoleMapper;

    @Autowired
    private MPermissionMapper mPermissionMapper;

    @Autowired
    private MRolePositionServiceImpl rolePositionService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SRoleAutoCodeServiceImpl sRoleAutoCodeService;

    /**
     * 获取列表，页面查询
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SRoleVo> selectPage(SRoleVo searchCondition) {
        // 分页条件
        Page<SRoleEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        // 查询数据，TypeHandler会自动处理permissionList的JSON转换
        IPage<SRoleVo> list = sRoleMapper.selectPage(pageCondition, searchCondition);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public List<SRoleExportVo> selectExportAll(SRoleVo searchCondition){
        return selectExportList(searchCondition);
    }

    /**
     * 导出专用查询方法，支持动态排序
     * 
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @return
     */
    @Override
    public List<SRoleExportVo> selectExportList(SRoleVo searchCondition){
        // 处理动态排序
        String orderByClause = "";
        if (searchCondition.getPageCondition() != null && StringUtils.isNotEmpty(searchCondition.getPageCondition().getSort())) {
            String sort = searchCondition.getPageCondition().getSort();
            String field = sort.startsWith("-") ? sort.substring(1) : sort;
            
            // 正则验证：只允许字母、数字、下划线，防止SQL注入
            if (!field.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                throw new BusinessException("非法的排序字段格式");
            }
            
            if (sort.startsWith("-")) {
                // 降序：去掉前缀-，添加DESC
                orderByClause = " ORDER BY " + field + " DESC";
            } else {
                // 升序：直接使用字段名，添加ASC
                orderByClause = " ORDER BY " + sort + " ASC";
            }
        }
        
        return sRoleMapper.selectExportList(searchCondition, orderByClause);
    }

    /**
     * 获取列表，根据id查询所有数据
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public List<SRoleEntity> selectIdsIn(List<SRoleVo> searchCondition) {
        // 查询 数据
        List<SRoleEntity> list = sRoleMapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 批量导入逻辑
     * 
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatches(List<SRoleEntity> entityList) {
        // 为批量保存中编码为空的记录自动生成编码
        for (SRoleEntity entity : entityList) {
            if (StringUtils.isEmpty(entity.getCode())) {
                entity.setCode(sRoleAutoCodeService.autoCode().getCode());
            }
        }
        return super.saveBatch(entityList, 500);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, allEntries=true)
    public void deleteByIdsIn(List<SRoleVo> searchCondition) {
        List<SRoleEntity> list = sRoleMapper.selectIdsIn(searchCondition);
        list.forEach(
            bean -> {
                bean.setIs_del(!bean.getIs_del());
            }
        );
        saveOrUpdateBatch(list, 500);
    }







    /**
     * 重写save方法，添加AutoCode逻辑
     * @param entity
     * @return
     */
    @Override
    public boolean save(SRoleEntity entity) {
        // 如果角色编码为空，则自动生成
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(sRoleAutoCodeService.autoCode().getCode());
        }
        return super.save(entity);
    }

    /**
     * 获取角色选择弹窗列表（无分页，用于角色选择弹窗）
     * @param searchCondition 查询条件
     * @return List<SRoleVo>
     */
    @Override
    public List<SRoleVo> selectListForDialog(SRoleVo searchCondition) {
        return sRoleMapper.selectListForDialog(searchCondition);
    }

}
