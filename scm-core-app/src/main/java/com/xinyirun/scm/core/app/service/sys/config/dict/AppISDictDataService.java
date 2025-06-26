package com.xinyirun.scm.core.app.service.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppNutuiNameAndValue;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppSDictDataVo;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

import java.util.List;

public interface AppISDictDataService extends AppIBaseService<SDictDataEntity> {
    /**
     * 获取列表，页面查询
     */
    List<AppSDictDataVo> selectPage(AppSDictDataVo searchCondition) ;

    /**
     * 获取能在Nutui显示数据的字典数据
     * @param searchCondition
     * @return
     */
    List<AppNutuiNameAndValue> selectNutuiNameAndValue(AppNutuiNameAndValue searchCondition) ;
}
