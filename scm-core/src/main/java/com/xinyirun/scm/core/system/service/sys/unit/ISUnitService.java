package com.xinyirun.scm.core.system.service.sys.unit;

import com.xinyirun.scm.bean.entity.sys.unit.SUnitEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;

/**
 * <p>
 * 单位 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-03
 */
public interface ISUnitService extends IService<SUnitEntity> {

    /**
     * 获取列表，页面查询
     */
    SUnitVo selectByCode(String code) ;
}
