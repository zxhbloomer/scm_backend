package com.xinyirun.scm.core.system.service.sys.areas;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaCitiesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreaProvincesVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasCascaderTreeVo;
import com.xinyirun.scm.bean.system.vo.sys.areas.SAreasVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-09-24
 */
public interface ICommonAreasService extends IService<NameAndValueVo> {

    /**
     * 获取省
     * @return
     */
    List<SAreaProvincesVo> getProvinces(SAreaProvincesVo condition);

    /**
     * 获取市
     * @return
     */
    List<SAreaCitiesVo> getCities(SAreaCitiesVo condition);

    /**
     * 获取区
     * @return
     */
    List<SAreasVo> getAreas(SAreasVo condition);


    /**
     * 获取省市区级联
     * @return
     */
    List<SAreasCascaderTreeVo> getAreasCascaderTreeVo();

}
