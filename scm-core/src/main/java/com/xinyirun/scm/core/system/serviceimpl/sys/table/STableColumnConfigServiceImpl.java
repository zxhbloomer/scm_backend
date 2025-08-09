package com.xinyirun.scm.core.system.serviceimpl.sys.table;

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
            this.reset(vo);
        } else if (list.size() != originalList.size()) {
            // 原数据长度和当前数据长度不一致， 说明表s_table_column_config_original 有数据修改，需要重置数据
            this.reset(vo);
        }

        // 获取主表数据
        List<STableColumnConfigVo> result = mapper.listWithDetails(vo);
        
        // 为分组数据单独查询和设置详情数据
        for (STableColumnConfigVo item : result) {
            if (item.getIs_group() != null && item.getIs_group() == 1) {
                // 查询分组的详情数据
                List<STableColumnConfigVo> groupChildren = mapper.listGroupChildren(
                    vo.getStaff_id(), 
                    vo.getPage_code(), 
                    item.getId()
                );
                // 设置到主表记录中
                item.setGroupChildren(groupChildren);
            }
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reset(STableColumnConfigVo vo) {
        vo.setStaff_id(SecurityUtil.getStaff_id().intValue());
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
        // 重构保存逻辑：统一处理排序和数据完整性
        processAndSaveAllData(list);
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
