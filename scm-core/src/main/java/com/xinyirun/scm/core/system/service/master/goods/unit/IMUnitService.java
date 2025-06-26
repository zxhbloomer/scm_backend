package com.xinyirun.scm.core.system.service.master.goods.unit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitSelectVo;

/**
 * <p>
 * 单位 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMUnitService extends IService<MUnitEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MUnitVo> selectPage(MUnitVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    MUnitVo selectByCode(String code) ;

    /**
     * 获取列表，页面查询
     */
    MUnitSelectVo getUnitSelectData(MUnitVo searchCondition) ;

}
