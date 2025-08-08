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

        // 使用新的JSON_ARRAYAGG方法一次性获取完整数据，性能优化
        return mapper.listWithDetails(vo);
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
        // 重新计算sort值，确保数据一致性
        reorderSort(list);
        
        for (STableColumnConfigVo vo: list) {
            // 更新主表数据
            STableColumnConfigEntity entity = mapper.selectById(vo.getId());
            entity.setIs_enable(vo.getIs_enable());
            entity.setSort(vo.getSort());
            entity.setIs_group(vo.getIs_group());
            mapper.updateById(entity);
            
            // 处理分组子项数据
            if (vo.getIs_group() == 1 && vo.getGroupChildren() != null && !vo.getGroupChildren().isEmpty()) {
                saveGroupChildren(vo.getGroupChildren());
            }
        }
    }
    
    /**
     * 保存分组子项数据
     * @param children 分组子项列表
     */
    private void saveGroupChildren(List<STableColumnConfigVo> children) {
        for (STableColumnConfigVo childVo : children) {
            if (childVo.getId() != null) {
                // 根据子项的字段名查找对应的detail记录进行更新
                // 注意：这里childVo.getId()实际上可能是detail表的ID，需要直接更新detail表
                STableColumnConfigDetailEntity childEntity = detailMapper.selectById(childVo.getId());
                if (childEntity != null) {
                    childEntity.setIs_enable(childVo.getIs_enable());
                    detailMapper.updateById(childEntity);
                }
            }
        }
    }
    
    /**
     * 重新计算sort值
     * 业务规则：按前端传来的list顺序重新分配全局sort值：0, 1, 2, 3...
     */
    private void reorderSort(List<STableColumnConfigVo> list) {
        for (int i = 0; i < list.size(); i++) {
            STableColumnConfigVo vo = list.get(i);
            // 重新分配全局sort值：按list顺序 0, 1, 2, 3...
            vo.setSort(i);
        }
    }
}
