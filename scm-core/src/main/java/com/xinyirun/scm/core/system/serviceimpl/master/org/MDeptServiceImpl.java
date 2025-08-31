package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MDeptEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptVo;
import com.xinyirun.scm.bean.system.vo.master.org.MDeptExportVo;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MDeptAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.mapper.master.org.MDeptMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgDeptPositionMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgMapper;
import com.xinyirun.scm.core.system.service.master.org.IMDeptService;
import com.xinyirun.scm.core.system.service.master.org.IMOrgService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  部门主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
@Slf4j
public class MDeptServiceImpl extends BaseServiceImpl<MDeptMapper, MDeptEntity> implements IMDeptService {

    @Autowired
    private MDeptMapper mapper;
    @Autowired
    private MDeptAutoCodeServiceImpl autoCode;
    @Autowired
    private MOrgDeptPositionMapper orgDeptPositionMapper;
    @Autowired
    private MOrgMapper orgMapper;
    @Autowired
    private IMOrgService orgService;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MDeptVo> selectPage(MDeptVo searchCondition) {
//        searchCondition.setTenant_id(getUserSessionTenantId());
        // 分页条件
        Page<MDeptEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MDeptVo> select(MDeptVo searchCondition) {
//        searchCondition.setTenant_id(getUserSessionTenantId());
        // 查询 数据
        List<MDeptVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MDeptEntity> selectIdsIn(List<MDeptVo> searchCondition) {
        // 查询 数据
        List<MDeptEntity> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据,导出用
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MDeptVo> selectIdsInForExport(List<MDeptVo> searchCondition) {
        // 查询 数据
        List<MDeptVo> list = mapper.selectIdsInForExport(searchCondition);
        return list;
    }

    /**
     * 逐个校验删除（单向删除，不支持恢复）
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<MDeptVo> searchCondition) {
        List<MDeptEntity> list = mapper.selectIdsIn(searchCondition);
        for(MDeptEntity entity : list) {
            // 只处理删除操作，不支持恢复
            if(entity.getIs_del()){
                // 已经删除的记录跳过
                continue;
            }
            
            // 执行删除前业务校验
            CheckResultAo cr = checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);
            if (cr.isSuccess() == false) {
                throw new BusinessException(cr.getMessage());
            }
            
            // 设置为已删除状态
            entity.setIs_del(true);
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 从组织架构删除部门（同时删除部门实体和组织关联关系）
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsFromOrg(List<MDeptVo> searchCondition) {
        // 简化处理：只取第一个部门
        if (searchCondition == null || searchCondition.isEmpty()) {
            throw new BusinessException("请选择要删除的部门");
        }
        
        MDeptVo deptVo = searchCondition.get(0);
        MDeptEntity entity = mapper.selectById(deptVo.getId());
        
        if (entity == null) {
            throw new BusinessException("部门不存在");
        }
        
        if (entity.getIs_del()) {
            // 部门已逻辑删除，但仍需清理组织架构关联关系
            log.info("部门[{}]已逻辑删除，清理组织架构关联关系", entity.getName());
            int deletedRows = orgMapper.deleteBySerialIdAndType(entity.getId(), "40");
            log.info("清理了{}条组织架构关联记录", deletedRows);
            
            // 清理组织架构相关缓存
            orgService.clearAllOrgCaches();
            log.info("已清理组织架构相关缓存");
            return;
        }
        
        // 执行删除前业务校验
        CheckResultAo cr = checkLogicForOrgDeletion(entity);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 1. 更新部门实体状态
        entity.setIs_del(true);
        boolean success = this.updateById(entity);
        if (!success) {
            throw new BusinessException("部门删除失败，数据已被修改，请刷新后重试");
        }
        
        // 2. 删除组织架构关联关系
        orgMapper.deleteBySerialIdAndType(entity.getId(), "40");
        
        // 3. 清理组织架构相关缓存（参考MOrgServiceImpl的缓存清理模式）
        orgService.clearAllOrgCaches();
        log.info("部门删除成功，已清理组织架构相关缓存");
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MDeptEntity entity) {
        // 编码如果为空，自动生成编码
        if(StringUtils.isEmpty(entity.getCode())){
            entity.setCode(autoCode.autoCode().getCode());
        }
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);
//        entity.setTenantCheckResult_id(getUserSessionTenantId());
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MDeptEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        return UpdateResultUtil.OK(mapper.updateById(entity));
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MDeptVo selectByid(Long id){
        return mapper.selectByid(id);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param code
     * @return
     */
    public List<MDeptEntity> selectByCode(String code, Long equal_id) {
        // 查询 数据
        List<MDeptEntity> list = mapper.selectByCode(code, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MDeptEntity> selectByName(String name, Long equal_id) {
        // 查询 数据
        List<MDeptEntity> list = mapper.selectByName(name, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MDeptEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        List<MDeptEntity> list = mapper.selectBySimpleName(name, equal_id);
        return list;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MDeptEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<MDeptEntity> codeList_insertCheck = selectByCode(entity.getCode(), null);
//                List<MDeptEntity> nameList_insertCheck = selectByName(entity.getName(), null, null);
//                List<MDeptEntity> simple_name_insertCheck = selectBySimpleName(entity.getSimple_name(), null, null);
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：部门编号【" + entity.getCode() + "】出现重复", entity.getCode());
                }
//                if (nameList_insertCheck.size() >= 1) {
//                    return CheckResultUtil.NG("新增保存出错：部门全称【" + entity.getName() + "】出现重复", entity.getName());
//                }
//                if (simple_name_insertCheck.size() >= 1) {
//                    return CheckResultUtil.NG("新增保存出错：部门简称【" + entity.getSimple_name() + "】出现重复", entity.getSimple_name());
//                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<MDeptEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
//                List<MDeptEntity> nameList_updCheck = selectByName(entity.getName(), null, entity.getId());
//                List<MDeptEntity> simple_name_updCheck = selectBySimpleName(entity.getSimple_name(), null, entity.getId());

                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：部门编号【" + entity.getCode() + "】出现重复！", entity.getCode());
                }
//                if (nameList_updCheck.size() >= 1) {
//                    return CheckResultUtil.NG("更新保存出错：部门全称【" + entity.getName() + "】出现重复！", entity.getName());
//                }
//                if (simple_name_updCheck.size() >= 1) {
//                    return CheckResultUtil.NG("更新保存出错：部门简称【" + entity.getSimple_name() + "】出现重复！", entity.getSimple_name());
//                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为false，表示为：页面点击了删除操作 */
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // L1: 组织架构依赖校验 - 检查部门是否在组织架构中使用
                int orgCount = mapper.isExistsInOrg(entity);
                if (orgCount > 0) {
                    return CheckResultUtil.NG(
                        "删除失败：该部门在组织架构中正在使用，请先从组织架构中移除");
                }
                
                // L2: 子部门校验 - 检查是否存在子部门
                Long subDeptCount = mapper.countSubDepts(entity.getId());
                if (subDeptCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该部门下有%d个子部门，请先删除或迁移子部门", subDeptCount));
                }
                
                // L3: 岗位校验 - 检查部门下是否配置了岗位
                Long positionCount = orgDeptPositionMapper.countByDeptId(entity.getId());
                if (positionCount > 0) {
                    return CheckResultUtil.NG(String.format(
                        "删除失败：该部门下配置了%d个岗位，请先删除相关岗位配置", positionCount));
                }
                break;
            case CheckResultAo.UNDELETE_CHECK_TYPE:
//                /** 如果逻辑删除为true，表示为：页面点击了删除操作 */
//                if(!entity.getIs_del()) {
//                    return CheckResultUtil.OK();
//                }
//                // 更新场合，不能重复设置
//                List<MDeptEntity> codeList_undel_Check = selectByCode(entity.getCode(), entity.getId());
////                List<MDeptEntity> nameList_undel_updCheck = selectByName(entity.getName(), null, entity.getId());
////                List<MDeptEntity> simple_name_undel_updCheck = selectBySimpleName(entity.getSimple_name(), null, entity.getId());
//
//                if (codeList_undel_Check.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：部门编号【" + entity.getCode() + "】出现重复", entity.getCode());
//                }
////                if (nameList_undel_updCheck.size() >= 1) {
////                    return CheckResultUtil.NG("复原出错：部门全称【" + entity.getName() + "】出现重复", entity.getName());
////                }
////                if (simple_name_undel_updCheck.size() >= 1) {
////                    return CheckResultUtil.NG("复原出错：部门简称【" + entity.getSimple_name() + "】出现重复", entity.getSimple_name());
////                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 组织删除专用校验逻辑（跳过L1组织架构依赖校验）
     * @param entity 实体对象
     * @return 校验结果
     */
    public CheckResultAo checkLogicForOrgDeletion(MDeptEntity entity){
        /** 如果逻辑删除为true，表示已经删除，无需再次删除 */
        if(entity.getIs_del()) {
            return CheckResultUtil.OK();
        }
        
        // 跳过L1组织架构依赖校验，因为删除目的就是从组织架构中移除
        
        // L2: 子部门校验 - 检查是否存在子部门
        Long subDeptCount = mapper.countSubDepts(entity.getId());
        if (subDeptCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该部门下有%d个子部门，请先删除或迁移子部门", subDeptCount));
        }
        
        // L3: 岗位校验 - 检查部门下是否配置了岗位
        Long positionCount = orgDeptPositionMapper.countByDeptId(entity.getId());
        if (positionCount > 0) {
            return CheckResultUtil.NG(String.format(
                "删除失败：该部门下配置了%d个岗位，请先删除相关岗位配置", positionCount));
        }
        
        return CheckResultUtil.OK();
    }

    /**
     * 导出专用查询方法，支持动态排序
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MDeptExportVo> selectExportList(MDeptVo searchCondition) {
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
        
        return mapper.selectExportList(searchCondition, orderByClause);
    }
}
