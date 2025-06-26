package com.xinyirun.scm.core.system.serviceimpl.sys.areas;

import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaCitiesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaProvincesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasCascaderTreeVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.sys.areas.SAreasMapper;
import com.xinyirun.scm.core.system.service.sys.areas.ICommonAreasService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class CommonAreasServiceImpl extends BaseServiceImpl<SAreasMapper, NameAndValueVo> implements ICommonAreasService {

    @Autowired
    private SAreasMapper mapper;

    /**
     * 市
     * @param condition
     * @return
     */
    @Override
    public List<SAreaProvincesVo> getProvinces(SAreaProvincesVo condition) {
        return mapper.getProvinces(condition);
    }

    /**
     * 市
     * @param condition
     * @return
     */
    @Override
    public List<SAreaCitiesVo> getCities(SAreaCitiesVo condition) {
        return mapper.getCities(condition);
    }

    /**
     * 区
     * @param condition
     * @return
     */
    @Override
    public List<SAreasVo> getAreas(SAreasVo condition) {
        return mapper.getAreas(condition);
    }

    /**
     * 获取省市区级联
     * @return
     */
    @Cacheable(value = SystemConstants.CACHE_PC.CACHE_AREAS_CASCADER ,
            key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() ")
    @Override
    public List<SAreasCascaderTreeVo> getAreasCascaderTreeVo() {
        List<SAreasCascaderTreeVo> listVo = mapper.getCascaderList();
        return TreeUtil.getTreeList(listVo);
    }
}
