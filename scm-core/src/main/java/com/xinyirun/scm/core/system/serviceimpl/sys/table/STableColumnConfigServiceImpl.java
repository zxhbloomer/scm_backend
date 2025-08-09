package com.xinyirun.scm.core.system.serviceimpl.sys.table;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigDetailEntity;
import com.xinyirun.scm.bean.entity.sys.table.STableConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableConfigVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigOriginalMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigOriginalDetailMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigDetailMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableConfigMapper;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.STableAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Service
public class STableColumnConfigServiceImpl extends ServiceImpl<STableColumnConfigMapper, STableColumnConfigEntity> implements ISTableColumnConfigService {

    @Autowired
    private STableColumnConfigMapper mapper;

    @Autowired
    private STableColumnConfigOriginalMapper sTableColumnConfigOriginalMapper;

    @Autowired
    private STableColumnConfigOriginalDetailMapper originalDetailMapper;

    @Autowired
    private STableConfigMapper sTableConfigMapper;

    @Autowired
    private STableAutoCodeServiceImpl autoCode;

    @Autowired
    private STableColumnConfigDetailMapper detailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<STableColumnConfigVo> list(STableColumnConfigVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
        List<STableColumnConfigVo> list = mapper.list(vo);

        List<STableColumnConfigVo> originalList = sTableColumnConfigOriginalMapper.list(vo);
        // 若无数据，则初始化
        if (list == null || list.size() == 0) {
            // 第一次进入页面，初始化数据
            this.resetDatabase(vo);
        } else if (list.size() != originalList.size()) {
            // 原数据长度和当前数据长度不一致， 说明表s_table_column_config_original 有数据修改，需要重置数据
            this.resetDatabase(vo);
        }

        // 获取主表数据
        List<STableColumnConfigVo> result = mapper.listWithDetails(vo);
        
        // 为分组数据单独查询和设置详情数据
        for (STableColumnConfigVo item : result) {
            System.out.println("=== 检查主表项: ID=" + item.getId() + ", name=" + item.getName() + ", is_group=" + item.getIs_group());
            if (item.getIs_group() != null && item.getIs_group() == 1) {
                System.out.println("=== 查询分组详情: configId=" + item.getId() + ", staffId=" + vo.getStaff_id() + ", pageCode=" + vo.getPage_code());
                
                // 查询分组的详情数据
                List<STableColumnConfigVo> groupChildren = mapper.listGroupChildren(
                    vo.getStaff_id(), 
                    vo.getPage_code(), 
                    item.getId()
                );
                
                System.out.println("=== 查询到分组子项数量: " + (groupChildren != null ? groupChildren.size() : 0));
                if (groupChildren != null && !groupChildren.isEmpty()) {
                    for (STableColumnConfigVo child : groupChildren) {
                        System.out.println("  子项: ID=" + child.getId() + ", name=" + child.getName() + ", sort=" + child.getSort());
                    }
                }
                
                // 设置到主表记录中
                item.setGroupChildren(groupChildren);
                System.out.println("=== 已设置groupChildren到主表项 ID=" + item.getId());
            }
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<STableColumnConfigVo> reset(STableColumnConfigVo vo) {
        System.out.println("=== reset方法：获取原始配置数据用于前端预览");
        return getOriginalDataForReset(vo);
    }
    
    /**
     * 内部使用的数据库重置方法（保持原有逻辑）
     */
    private void resetDatabase(STableColumnConfigVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
        System.out.println("=== resetDatabase：执行数据库重置操作");
        
        // 删除旧表格配置信息
        mapper.delete(vo);

        STableConfigVo sTableConfigVo = sTableConfigMapper.get(vo);

        if (sTableConfigVo == null) {
            STableConfigEntity sTableConfigEntity = new STableConfigEntity();
            // 设置编号
            sTableConfigEntity.setCode(autoCode.autoCode().getCode());
            sTableConfigEntity.setName("");
            sTableConfigEntity.setPage_code(vo.getPage_code());
            sTableConfigEntity.setType("1");
            sTableConfigEntity.setStaff_id(SecurityUtil.getStaff_id().intValue());
            sTableConfigMapper.insert(sTableConfigEntity);
            vo.setTable_code(sTableConfigEntity.getCode());
            vo.setTable_id(sTableConfigEntity.getId());

        } else {
            vo.setTable_code(sTableConfigVo.getCode());
            vo.setTable_id(sTableConfigVo.getId());
        }

        List<STableColumnConfigVo> list = sTableColumnConfigOriginalMapper.list(vo);
        List<STableColumnConfigEntity> sTableColumnConfigEntities = BeanUtilsSupport.copyProperties(list, STableColumnConfigEntity.class, new String[]{"id"});
        if (!sTableColumnConfigEntities.isEmpty()) {
            super.saveBatch(sTableColumnConfigEntities);
        }
    }

    @Override
    public Boolean check(STableColumnConfigVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
        return mapper.check(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveList(List<STableColumnConfigVo> list) {
        System.out.println("=== saveList：开始全删全插保存，数据数量: " + list.size());
        
        if (list == null || list.isEmpty()) {
            System.out.println("=== saveList：数据为空，跳过保存");
            return;
        }
        
        // 获取第一个记录的基础信息用于删除条件
        STableColumnConfigVo firstVo = list.get(0);
        Integer staffId = SecurityUtil.getStaff_id().intValue();
        String pageCode = firstVo.getPage_code();
        
        System.out.println("=== saveList：删除条件 - staffId=" + staffId + ", pageCode=" + pageCode);
        
        // Step 1: 全删 - 删除当前用户页面的所有配置数据
        deleteAllConfigData(staffId, pageCode);
        
        // Step 2: 全插 - 按顺序插入新的配置数据
        insertAllConfigData(list, staffId, pageCode);
        
        System.out.println("=== saveList：全删全插完成");
    }
    
    /**
     * 删除指定用户页面的所有配置数据
     */
    private void deleteAllConfigData(Integer staffId, String pageCode) {
        System.out.println("=== 开始删除所有配置数据");
        
        // 先获取要删除的主表配置ID列表，用于删除详情表
        STableColumnConfigVo queryCondition = new STableColumnConfigVo();
        queryCondition.setStaff_id(staffId);
        queryCondition.setPage_code(pageCode);
        List<STableColumnConfigVo> existingConfigs = mapper.list(queryCondition);
        
        // 删除详情表数据（根据config_id）
        if (existingConfigs != null && !existingConfigs.isEmpty()) {
            for (STableColumnConfigVo config : existingConfigs) {
                if (config.getIs_group() != null && config.getIs_group() == 1) {
                    QueryWrapper<STableColumnConfigDetailEntity> detailWrapper = new QueryWrapper<>();
                    detailWrapper.eq("config_id", config.getId());
                    int detailDeleteCount = detailMapper.delete(detailWrapper);
                    System.out.println("=== 删除分组" + config.getId() + "的详情数据：" + detailDeleteCount + "条");
                }
            }
        }
        
        // 再删除主表
        STableColumnConfigVo deleteCondition = new STableColumnConfigVo();
        deleteCondition.setStaff_id(staffId);
        deleteCondition.setPage_code(pageCode);
        mapper.delete(deleteCondition);
        System.out.println("=== 删除主表数据完成");
    }
    
    /**
     * 插入所有配置数据
     */
    private void insertAllConfigData(List<STableColumnConfigVo> list, Integer staffId, String pageCode) {
        System.out.println("=== 开始插入所有配置数据");
        
        // 获取table配置信息
        STableConfigVo tableConfigVo = getOrCreateTableConfig(staffId, pageCode);
        
        // 重新分配主表sort值
        for (int i = 0; i < list.size(); i++) {
            STableColumnConfigVo vo = list.get(i);
            vo.setSort(i);
            vo.setStaff_id(staffId);
            vo.setTable_code(tableConfigVo.getCode());
            vo.setTable_id(tableConfigVo.getId());
            
            System.out.println("=== 准备插入主表数据: sort=" + i + ", name=" + vo.getName() + ", is_group=" + vo.getIs_group());
            
            // 插入主表数据
            STableColumnConfigEntity mainEntity = new STableColumnConfigEntity();
            mainEntity.setName(vo.getName());
            mainEntity.setLabel(vo.getLabel());
            mainEntity.setSort(vo.getSort());
            mainEntity.setFix(vo.getFix());
            mainEntity.setIs_enable(vo.getIs_enable());
            mainEntity.setIs_delete(vo.getIs_delete());
            mainEntity.setIs_group(vo.getIs_group());
            mainEntity.setTable_code(vo.getTable_code());
            mainEntity.setTable_id(vo.getTable_id());
            
            mapper.insert(mainEntity);
            
            // 插入分组的详情数据
            if (vo.getIs_group() != null && vo.getIs_group() == 1 && vo.getGroupChildren() != null && !vo.getGroupChildren().isEmpty()) {
                insertGroupChildrenData(vo.getGroupChildren(), mainEntity.getId(), tableConfigVo);
            }
        }
        
        System.out.println("=== 插入所有配置数据完成");
    }
    
    /**
     * 插入分组子项数据
     */
    private void insertGroupChildrenData(List<STableColumnConfigVo> children, Integer parentId, STableConfigVo tableConfig) {
        System.out.println("=== 开始插入分组子项数据，parentId=" + parentId + ", 子项数量=" + children.size());
        
        for (int i = 0; i < children.size(); i++) {
            STableColumnConfigVo childVo = children.get(i);
            
            STableColumnConfigDetailEntity childEntity = new STableColumnConfigDetailEntity();
            childEntity.setConfig_id(parentId);
            childEntity.setTable_code(tableConfig.getCode());
            childEntity.setTable_id(tableConfig.getId());
            childEntity.setName(childVo.getName());
            childEntity.setLabel(childVo.getLabel());
            childEntity.setSort(i); // 按数组顺序分配sort: 0,1,2,3...
            childEntity.setIs_enable(childVo.getIs_enable());
            childEntity.setIs_delete(childVo.getIs_delete());
            
            System.out.println("=== 插入子项数据: sort=" + i + ", name=" + childVo.getName() + ", config_id=" + parentId);
            detailMapper.insert(childEntity);
        }
        
        System.out.println("=== 分组子项数据插入完成");
    }
    
    /**
     * 获取或创建table配置
     */
    private STableConfigVo getOrCreateTableConfig(Integer staffId, String pageCode) {
        STableColumnConfigVo searchVo = new STableColumnConfigVo();
        searchVo.setStaff_id(staffId);
        searchVo.setPage_code(pageCode);
        
        STableConfigVo tableConfigVo = sTableConfigMapper.get(searchVo);
        
        if (tableConfigVo == null) {
            System.out.println("=== 创建新的table配置");
            STableConfigEntity tableConfigEntity = new STableConfigEntity();
            tableConfigEntity.setCode(autoCode.autoCode().getCode());
            tableConfigEntity.setName("");
            tableConfigEntity.setPage_code(pageCode);
            tableConfigEntity.setType("1");
            tableConfigEntity.setStaff_id(staffId);
            sTableConfigMapper.insert(tableConfigEntity);
            
            // 转换为VO
            tableConfigVo = new STableConfigVo();
            tableConfigVo.setId(tableConfigEntity.getId());
            tableConfigVo.setCode(tableConfigEntity.getCode());
            tableConfigVo.setName(tableConfigEntity.getName());
            tableConfigVo.setPage_code(tableConfigEntity.getPage_code());
            tableConfigVo.setType(tableConfigEntity.getType());
            tableConfigVo.setStaff_id(tableConfigEntity.getStaff_id());
        }
        
        return tableConfigVo;
    }
    
    /**
     * 获取原始配置数据用于重置功能
     * 返回与list方法相同的数据结构，包含分组和子项
     */
    @Override
    public List<STableColumnConfigVo> getOriginalDataForReset(STableColumnConfigVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
        System.out.println("=== 开始获取原始配置数据用于重置，staffId=" + vo.getStaff_id() + ", pageCode=" + vo.getPage_code());
        
        // 获取原始主表数据
        List<STableColumnConfigVo> result = sTableColumnConfigOriginalMapper.list(vo);
        System.out.println("=== 获取到原始主表数据数量: " + (result != null ? result.size() : 0));
        
        // 为分组数据单独查询和设置原始详情数据
        for (STableColumnConfigVo item : result) {
            System.out.println("=== 检查原始主表项: ID=" + item.getId() + ", name=" + item.getName() + ", is_group=" + item.getIs_group());
            if (item.getIs_group() != null && item.getIs_group() == 1) {
                System.out.println("=== 查询原始分组详情: originalId=" + item.getId());
                
                // 查询原始分组的详情数据
                List<STableColumnConfigVo> groupChildren = getOriginalGroupChildren(item.getId());
                
                System.out.println("=== 查询到原始分组子项数量: " + (groupChildren != null ? groupChildren.size() : 0));
                if (groupChildren != null && !groupChildren.isEmpty()) {
                    for (STableColumnConfigVo child : groupChildren) {
                        System.out.println("  原始子项: ID=" + child.getId() + ", name=" + child.getName() + ", sort=" + child.getSort());
                    }
                }
                
                // 设置到主表记录中
                item.setGroupChildren(groupChildren);
                System.out.println("=== 已设置原始groupChildren到主表项 ID=" + item.getId());
            }
        }
        
        System.out.println("=== 原始配置数据获取完成，返回数据数量: " + (result != null ? result.size() : 0));
        return result;
    }
    
    /**
     * 查询原始分组的详情数据
     */
    private List<STableColumnConfigVo> getOriginalGroupChildren(Integer originalId) {
        System.out.println("=== 开始查询原始分组详情数据，originalId=" + originalId);
        List<STableColumnConfigVo> children = originalDetailMapper.listByOriginalId(originalId);
        System.out.println("=== 查询到原始详情数据数量: " + (children != null ? children.size() : 0));
        return children;
    }
    
    /**
     * 重构版本：统一处理所有数据的保存和排序
     * 修复问题：
     * 1. 分组子项排序与数据库顺序不一致
     * 2. table_id空值问题  
     * 3. sort重复值问题
     * 4. 详情表sort都为0的问题
     */
    private void processAndSaveAllData(List<STableColumnConfigVo> list) {
        // Step 1: 重新计算所有主表项的sort值
        reorderMainTableSort(list);
        
        // Step 2: 统一保存主表数据
        for (STableColumnConfigVo vo : list) {
            updateMainTableRecord(vo);
            
            // Step 3: 处理分组子项（包含排序和保存）
            if (vo.getIs_group() == 1 && vo.getGroupChildren() != null && !vo.getGroupChildren().isEmpty()) {
                processAndSaveGroupChildren(vo.getGroupChildren(), vo.getId());
            }
        }
    }
    
    /**
     * 更新主表记录，确保table_id等字段完整
     */
    private void updateMainTableRecord(STableColumnConfigVo vo) {
        STableColumnConfigEntity entity = mapper.selectById(vo.getId());
        if (entity != null) {
            entity.setIs_enable(vo.getIs_enable());
            entity.setSort(vo.getSort());
            entity.setIs_group(vo.getIs_group());
            
            // 修复table_id空值问题：确保table_id有值
            if (entity.getTable_id() == null && vo.getTable_id() != null) {
                entity.setTable_id(vo.getTable_id());
            }
            
            mapper.updateById(entity);
        }
    }
    
    /**
     * 处理分组子项：重新排序并保存
     * 修复分组子项排序问题和详情表sort为0的问题
     */
    private void processAndSaveGroupChildren(List<STableColumnConfigVo> children, Integer parentId) {
        System.out.println("=== 开始处理分组子项，parentId: " + parentId + ", 子项数量: " + children.size());
        
        // 显示JSON上传的原始顺序
        System.out.println("=== JSON上传的原始顺序（按此顺序重新分配sort从0开始）：");
        for (int i = 0; i < children.size(); i++) {
            STableColumnConfigVo child = children.get(i);
            System.out.println("  位置" + i + ": ID=" + child.getId() + ", name=" + child.getName() + ", displayIndex=" + child.getDisplayIndex());
        }
        
        // 直接按JSON数组顺序重新分配sort值（从0开始连续）
        for (int i = 0; i < children.size(); i++) {
            STableColumnConfigVo childVo = children.get(i);
            
            // 查找并更新详情表记录
            STableColumnConfigDetailEntity childEntity = detailMapper.selectById(childVo.getId());
            if (childEntity != null) {
                Integer oldSort = childEntity.getSort();
                childEntity.setIs_enable(childVo.getIs_enable());
                childEntity.setSort(i); // 按JSON上传顺序分配sort: 0,1,2,3...
                
                // 确保config_id关联正确
                if (childEntity.getConfig_id() == null) {
                    childEntity.setConfig_id(parentId);
                }
                
                System.out.println("=== 更新子项: 位置" + i + ", ID=" + childVo.getId() + ", name=" + childVo.getName() + 
                                 ", oldSort=" + oldSort + ", newSort=" + i + ", config_id=" + parentId);
                
                int updateCount = detailMapper.updateById(childEntity);
                System.out.println("=== 数据库更新结果: " + (updateCount > 0 ? "成功" : "失败"));
            } else {
                System.out.println("=== 警告：找不到子项记录，ID=" + childVo.getId());
            }
        }
        System.out.println("=== 分组子项处理完成 - 按JSON上传顺序重新分配sort值");
    }
    
    /**
     * 重新计算主表sort值
     * 修复sort重复值问题：按前端传来的list顺序重新分配全局sort值
     */
    private void reorderMainTableSort(List<STableColumnConfigVo> list) {
        for (int i = 0; i < list.size(); i++) {
            STableColumnConfigVo vo = list.get(i);
            // 重新分配全局sort值：按list顺序 0, 1, 2, 3...
            vo.setSort(i);
        }
    }
}
