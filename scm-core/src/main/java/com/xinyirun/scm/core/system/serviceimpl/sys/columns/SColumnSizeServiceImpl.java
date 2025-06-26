package com.xinyirun.scm.core.system.serviceimpl.sys.columns;

import com.xinyirun.scm.bean.entity.sys.columns.SColumnSizeEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.sys.columns.SColumnSizeVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.sys.columns.SColumnSizeMapper;
import com.xinyirun.scm.core.system.service.sys.columns.ISColumnSizeService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 表格列宽 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-06-09
 */
@Service
public class SColumnSizeServiceImpl extends BaseServiceImpl<SColumnSizeMapper, SColumnSizeEntity> implements
        ISColumnSizeService {

    @Autowired
    private SColumnSizeMapper mapper;

    /**
     * 获取列表，页面查询
     */
    @Cacheable(value = SystemConstants.CACHE_PC.CACHE_COLUMNS_TYPE,
            key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #searchCondition.cache_key")
    @Override
    public List<SColumnSizeVo> getData(SColumnSizeVo searchCondition) {
        List<SColumnSizeVo> rtnBean =  mapper.getData(searchCondition);
        return rtnBean;
    }

    /**
     * 插入or更新
     * @param searchCondition
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_COLUMNS_TYPE, key = "#searchCondition.cache_key")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Boolean> saveColumnsSize(SColumnSizeVo searchCondition) {
        SColumnSizeEntity entity = new SColumnSizeEntity();
        entity.setPage_code(searchCondition.getPage_code());
        entity.setType(searchCondition.getType());
        entity.setStaff_id(SecurityUtil.getStaff_id());
        entity.setColumn_property(searchCondition.getColumn_property());
        entity.setColumn_label(searchCondition.getColumn_label());
        entity.setColumn_index(searchCondition.getColumn_index());
        entity.setReal_width(searchCondition.getReal_width());
        entity.setMin_width(searchCondition.getMin_width());

        if(entity.getReal_width() == null){
            UpdateResultUtil.NG(false, "更新的列长度为0，更新错误");
        }

        // 尝试更新
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        int updCount = mapper.saveColumnsSize(searchCondition);
        // 更新失败则插入
        if(updCount < 1){
            mapper.insert(entity);
        }
        return UpdateResultUtil.OK(true);
    }

    /**
     * 插入or更新
     * @param searchCondition
     */
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_COLUMNS_TYPE, key = "#searchCondition.cache_key")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Boolean> deleteColumnsSize(SColumnSizeVo searchCondition) {

        // 尝试更新
        searchCondition.setStaff_id(SecurityUtil.getStaff_id());
        mapper.deleteColumnsSize(searchCondition);

        return DeleteResultUtil.OK(true);
    }
}
