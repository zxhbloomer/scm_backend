package com.xinyirun.scm.core.system.serviceimpl.sys.rbac.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRolePositionEntity;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateDetailBo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.ArrayPfUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = sRoleMapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        // 查询 数据
        List<SRoleExportVo> list = sRoleMapper.selectExportAll(searchCondition);
        return list;
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
     * 部分导出
     *
     * @param searchConditionList 导出id
     * @return List<SRoleExportVo>
     */
    @Override
    public List<SRoleExportVo> selectExportList(List<SRoleVo> searchConditionList) {
        return sRoleMapper.selectExportList(searchConditionList);
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
