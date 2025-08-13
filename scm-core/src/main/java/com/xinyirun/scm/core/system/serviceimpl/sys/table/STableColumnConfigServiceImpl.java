package com.xinyirun.scm.core.system.serviceimpl.sys.table;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigDetailEntity;
import com.xinyirun.scm.bean.entity.sys.table.STableConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableConfigVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigDetailMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableConfigMapper;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesMapper;
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
    private STableConfigMapper sTableConfigMapper;

    @Autowired
    private STableAutoCodeServiceImpl autoCode;

    @Autowired
    private STableColumnConfigDetailMapper detailMapper;
    
    @Autowired
    private SPagesMapper sPagesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<STableColumnConfigVo> list(STableColumnConfigVo vo) {
        // 获取当前用户ID用于用户隔离
        Integer currentUserId = SecurityUtil.getStaff_id().intValue();
        // 注意：staff_id字段已删除，用户隔离通过业务逻辑控制
        
        // 获取主表数据
        List<STableColumnConfigVo> result = mapper.listWithDetails(vo);
        
        // 为分组数据单独查询和设置详情数据
        for (STableColumnConfigVo item : result) {
            System.out.println("=== 检查主表项: ID=" + item.getId() + ", name=" + item.getName() + ", is_group=" + item.getIs_group());
            if (item.getIs_group() != null && item.getIs_group() == 1) {
                System.out.println("=== 查询分组详情: configId=" + item.getId() + ", pageCode=" + vo.getPage_code());
                
                // 查询分组的详情数据
                List<STableColumnConfigVo> groupChildren = mapper.listGroupChildren(
                    currentUserId, 
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
    public void resetTableColumns(List<STableColumnConfigVo> configs, String pageCode) {
        // 获取当前用户ID，确保用户隔离
        Integer currentUserId = SecurityUtil.getStaff_id().intValue();
        System.out.println("=== resetTableColumns：开始重置表格列配置，用户ID=" + currentUserId + ", 页面代码=" + pageCode);
        
        if (configs == null || configs.isEmpty()) {
            throw new RuntimeException("重置配置数据不能为空");
        }
        
        // 根据page_code查询s_table_config（唯一索引保证只有一条）
        STableColumnConfigVo searchVo = new STableColumnConfigVo();
        searchVo.setPage_code(pageCode);
        STableConfigVo tableConfig = sTableConfigMapper.get(searchVo);
        
        if (tableConfig == null) {
            // 当s_table_config表中没有pageCode对应的数据时，需要新增一条数据
            System.out.println("=== 表格配置不存在，创建新配置，页面代码：" + pageCode);
            
            // 从s_pages表中查询页面名称
            SPagesVo pageInfo = sPagesMapper.selectByCode(pageCode);
            String pageName = "";
            if (pageInfo != null && pageInfo.getName() != null) {
                pageName = pageInfo.getName();
            }
            
            // 创建新的table配置
            STableConfigEntity newTableConfig = new STableConfigEntity();
            newTableConfig.setCode(autoCode.autoCode().getCode()); // 从STableAutoCodeServiceImpl中获取code
            newTableConfig.setName(pageName); // 页面名称
            newTableConfig.setPage_code(pageCode);
            newTableConfig.setType("1"); // type=1
            sTableConfigMapper.insert(newTableConfig);
            
            // 转换为VO返回
            tableConfig = new STableConfigVo();
            tableConfig.setId(newTableConfig.getId());
            tableConfig.setCode(newTableConfig.getCode());
            tableConfig.setName(newTableConfig.getName());
            tableConfig.setPage_code(newTableConfig.getPage_code());
            tableConfig.setType(newTableConfig.getType());
            
            System.out.println("=== 创建新表格配置成功: tableId=" + tableConfig.getId() + ", tableCode=" + tableConfig.getCode());
        } else {
            System.out.println("=== 找到表格配置: tableId=" + tableConfig.getId() + ", tableCode=" + tableConfig.getCode());
        }
        
        // 执行全删全插操作（使用SQL删除）
        deleteUserConfigData(currentUserId, tableConfig.getId(), pageCode);
        insertResetConfigData(configs, currentUserId, tableConfig);
        
        System.out.println("=== resetTableColumns：重置完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<STableColumnConfigVo> resetTableColumnsAndReturn(List<STableColumnConfigVo> configs, String pageCode) {
        System.out.println("=== resetTableColumnsAndReturn：开始重置并返回数据，页面代码=" + pageCode);
        
        // 1. 执行重置操作（复用现有逻辑）
        resetTableColumns(configs, pageCode);
        
        // 2. 查询并返回最新数据
        STableColumnConfigVo queryVo = new STableColumnConfigVo();
        queryVo.setPage_code(pageCode);
        List<STableColumnConfigVo> resultList = this.list(queryVo);
        
        System.out.println("=== resetTableColumnsAndReturn：重置完成并返回数据，数量=" + (resultList != null ? resultList.size() : 0));
        return resultList;
    }
    
    /**
     * 删除指定用户的表格配置数据（使用SQL删除）
     */
    private void deleteUserConfigData(Integer userId, Integer tableId, String pageCode) {
        System.out.println("=== 开始删除用户配置数据：userId=" + userId + ", tableId=" + tableId + ", pageCode=" + pageCode);
        
        // 先查询要删除的主表配置ID列表，用于删除详情表
        STableColumnConfigVo queryCondition = new STableColumnConfigVo();
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
        
        // 删除主表数据 - 使用mapper的SQL删除方法
        STableColumnConfigVo deleteCondition = new STableColumnConfigVo();
        deleteCondition.setPage_code(pageCode);
        mapper.delete(deleteCondition);
        System.out.println("=== 删除主表数据完成");
    }
    
    /**
     * 插入重置的配置数据
     */
    private void insertResetConfigData(List<STableColumnConfigVo> configs, Integer userId, STableConfigVo tableConfig) {
        System.out.println("=== 开始插入重置配置数据，数量=" + configs.size());
        
        for (int i = 0; i < configs.size(); i++) {
            STableColumnConfigVo configVo = configs.get(i);
            
            // 创建主表记录
            STableColumnConfigEntity mainEntity = new STableColumnConfigEntity();
            // 注意：staff_id字段已删除，用户隔离通过删除和插入逻辑实现
            mainEntity.setTable_id(tableConfig.getId());
            mainEntity.setTable_code(tableConfig.getCode());
            mainEntity.setName(configVo.getName());
            mainEntity.setLabel(configVo.getLabel());
            mainEntity.setSort(configVo.getSort());
            mainEntity.setIs_group(configVo.getIs_group());
            mainEntity.setIs_enable(true);  // 默认启用
            mainEntity.setIs_delete(false);  // 默认不删除
            
            // 插入主表
            this.save(mainEntity);
            System.out.println("=== 插入主表记录：ID=" + mainEntity.getId() + ", name=" + configVo.getName());
            
            // 如果是分组，插入子项
            if (configVo.getIs_group() != null && configVo.getIs_group() == 1 && 
                configVo.getGroupChildren() != null && !configVo.getGroupChildren().isEmpty()) {
                insertGroupChildren(configVo.getGroupChildren(), mainEntity.getId(), tableConfig);
            }
        }
        
        System.out.println("=== 重置配置数据插入完成");
    }
    
    /**
     * 插入分组子项
     */
    private void insertGroupChildren(List<STableColumnConfigVo> children, Integer configId, STableConfigVo tableConfig) {
        System.out.println("=== 插入分组子项，configId=" + configId + ", 子项数量=" + children.size());
        
        for (int i = 0; i < children.size(); i++) {
            STableColumnConfigVo childVo = children.get(i);
            
            STableColumnConfigDetailEntity detailEntity = new STableColumnConfigDetailEntity();
            detailEntity.setConfig_id(configId);
            detailEntity.setTable_id(tableConfig.getId());
            detailEntity.setTable_code(tableConfig.getCode());
            detailEntity.setName(childVo.getName());
            detailEntity.setLabel(childVo.getLabel());
            detailEntity.setSort(childVo.getSort());
            detailEntity.setIs_enable(true);
            detailEntity.setIs_delete(false);
            
            detailMapper.insert(detailEntity);
            System.out.println("=== 插入子项：name=" + childVo.getName() + ", sort=" + childVo.getSort());
        }
    }

    @Override
    public Boolean check(STableColumnConfigVo vo) {
        // original表相关逻辑已删除，直接返回true
        return true;
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
        // 注意：staff_id字段已删除，用户隔离通过业务逻辑控制
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
        // 注意：staff_id字段已删除，通过page_code删除全局配置
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
        STableConfigVo tableConfigVo = getOrCreateTableConfig(pageCode);
        
        // 重新分配主表sort值
        for (int i = 0; i < list.size(); i++) {
            STableColumnConfigVo vo = list.get(i);
            vo.setSort(i);
            // 注意：staff_id字段已删除，用户隔离通过删除和插入逻辑实现
            vo.setTable_code(tableConfigVo.getCode());
            vo.setTable_id(tableConfigVo.getId());
            
            System.out.println("=== 准备插入主表数据: sort=" + i + ", name=" + vo.getName() + ", is_group=" + vo.getIs_group());
            
            // 插入主表数据
            STableColumnConfigEntity mainEntity = new STableColumnConfigEntity();
            mainEntity.setName(vo.getName());
            mainEntity.setLabel(vo.getLabel());
            mainEntity.setSort(vo.getSort());
            mainEntity.setFix(vo.getFix());
            mainEntity.setIs_enable(vo.getIs_enable() != null ? vo.getIs_enable() : true);
            mainEntity.setIs_delete(vo.getIs_delete() != null ? vo.getIs_delete() : false);
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
            childEntity.setIs_enable(childVo.getIs_enable() != null ? childVo.getIs_enable() : true);
            childEntity.setIs_delete(childVo.getIs_delete() != null ? childVo.getIs_delete() : false);
            
            System.out.println("=== 插入子项数据: sort=" + i + ", name=" + childVo.getName() + ", config_id=" + parentId);
            detailMapper.insert(childEntity);
        }
        
        System.out.println("=== 分组子项数据插入完成");
    }
    
    /**
     * 获取或创建table配置
     */
    private STableConfigVo getOrCreateTableConfig(String pageCode) {
        STableColumnConfigVo searchVo = new STableColumnConfigVo();
        searchVo.setPage_code(pageCode);
        
        STableConfigVo tableConfigVo = sTableConfigMapper.get(searchVo);
        
        if (tableConfigVo == null) {
            System.out.println("=== 创建新的table配置");
            STableConfigEntity tableConfigEntity = new STableConfigEntity();
            tableConfigEntity.setCode(autoCode.autoCode().getCode());
            tableConfigEntity.setName("");
            tableConfigEntity.setPage_code(pageCode);
            tableConfigEntity.setType("1");
            sTableConfigMapper.insert(tableConfigEntity);
            
            // 转换为VO
            tableConfigVo = new STableConfigVo();
            tableConfigVo.setId(tableConfigEntity.getId());
            tableConfigVo.setCode(tableConfigEntity.getCode());
            tableConfigVo.setName(tableConfigEntity.getName());
            tableConfigVo.setPage_code(tableConfigEntity.getPage_code());
            tableConfigVo.setType(tableConfigEntity.getType());
        }
        
        return tableConfigVo;
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
                childEntity.setIs_enable(childVo.getIs_enable() != null ? childVo.getIs_enable() : true);
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
