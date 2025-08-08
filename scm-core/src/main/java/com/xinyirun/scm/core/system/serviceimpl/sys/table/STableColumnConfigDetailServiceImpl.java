package com.xinyirun.scm.core.system.serviceimpl.sys.table;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigDetailVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.sys.table.STableColumnConfigDetailMapper;
import com.xinyirun.scm.core.system.service.sys.table.ISTableColumnConfigDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 表格列配置详情表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-08
 */
@Service
public class STableColumnConfigDetailServiceImpl extends ServiceImpl<STableColumnConfigDetailMapper, STableColumnConfigDetailEntity> implements ISTableColumnConfigDetailService {

    @Autowired
    private STableColumnConfigDetailMapper detailMapper;

    @Override
    public List<STableColumnConfigDetailVo> listByConfigId(Integer configId) {
        return detailMapper.listByConfigId(configId);
    }

    @Override
    public List<STableColumnConfigDetailVo> listByConfigIds(List<Integer> configIds) {
        if (configIds == null || configIds.isEmpty()) {
            return List.of();
        }
        return detailMapper.listByConfigIds(configIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDetailList(List<STableColumnConfigDetailVo> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return;
        }

        List<STableColumnConfigDetailEntity> entities = BeanUtilsSupport.copyProperties(detailList, STableColumnConfigDetailEntity.class);
        
        for (STableColumnConfigDetailEntity entity : entities) {
            if (entity.getId() != null) {
                // 更新现有记录
                this.updateById(entity);
            } else {
                // 新增记录
                this.save(entity);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByConfigId(Integer configId) {
        if (configId == null) {
            return;
        }
        
        QueryWrapper<STableColumnConfigDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("config_id", configId);
        this.remove(queryWrapper);
    }
}