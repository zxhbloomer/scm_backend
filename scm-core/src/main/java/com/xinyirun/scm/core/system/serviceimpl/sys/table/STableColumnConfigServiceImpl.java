package com.xinyirun.scm.core.system.serviceimpl.sys.table;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.entity.sys.table.STableConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.table.STableConfigVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigMapper;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigOriginalMapper;
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
        list = mapper.list(vo);

        return list;
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
        // 重新计算sort和group_sort，确保数据一致性
        reorderSortAndGroupSort(list);
        
        for (STableColumnConfigVo vo: list) {
            STableColumnConfigEntity entity = mapper.selectById(vo.getId());
            // 基础字段更新
            entity.setIs_enable(vo.getIs_enable());
            entity.setSort(vo.getSort());
            
            // 分组相关字段更新
            entity.setGroup_name(vo.getGroup_name());
            entity.setGroup_sort(vo.getGroup_sort());
            entity.setIs_group_header(vo.getIs_group_header());
            
            mapper.updateById(entity);
        }
    }
    
    /**
     * 重新计算sort和group_sort值
     * 业务规则：
     * 1. 按前端传来的list顺序重新分配全局sort值：0, 1, 2, 3...
     * 2. 组按一个元素处理，组内group_sort从1开始
     * 3. 组头的group_sort保持null
     */
    private void reorderSortAndGroupSort(List<STableColumnConfigVo> list) {
        // 按前端传来的顺序重新分配sort值和group_sort值
        Map<String, Integer> groupCounters = new HashMap<>();
        
        for (int i = 0; i < list.size(); i++) {
            STableColumnConfigVo vo = list.get(i);
            
            // 重新分配全局sort值：按list顺序 0, 1, 2, 3...
            vo.setSort(i);
            
            // 处理分组元素的group_sort
            String groupName = vo.getGroup_name();
            if (groupName != null && !groupName.trim().isEmpty()) {
                if (vo.getIs_group_header() != null && vo.getIs_group_header() == 1) {
                    // 组头：group_sort保持null，重置该组的计数器
                    vo.setGroup_sort(null);
                    groupCounters.put(groupName, 0);
                } else {
                    // 组内元素：从1开始编号
                    int groupCounter = groupCounters.getOrDefault(groupName, 0) + 1;
                    vo.setGroup_sort(groupCounter);
                    groupCounters.put(groupName, groupCounter);
                }
            } else {
                // 普通元素：不设置group_sort
                vo.setGroup_sort(null);
            }
        }
    }
}
