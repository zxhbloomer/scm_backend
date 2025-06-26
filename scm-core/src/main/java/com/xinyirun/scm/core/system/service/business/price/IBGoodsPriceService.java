package com.xinyirun.scm.core.system.service.business.price;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.price.ApiGoodsPriceVo;
import com.xinyirun.scm.bean.entity.busniess.price.BGoodsPriceEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-18
 */
public interface IBGoodsPriceService extends IService<BGoodsPriceEntity> {

    /**
     * 数据同步
     */
    void syncAll(List<ApiGoodsPriceVo> list);

}
