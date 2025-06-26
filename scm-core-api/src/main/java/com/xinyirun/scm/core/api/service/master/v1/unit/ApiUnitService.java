package com.xinyirun.scm.core.api.service.master.v1.unit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.api.vo.master.unit.ApiUnitVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiUnitService extends IService<MUnitEntity> {

    /**
     * 数据同步
     */
    void syncAll(List<ApiUnitVo> vo);
}
