package com.xinyirun.scm.core.api.service.master.v1.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecPropVo;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecPropEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwl
 * @since 2022-01-27
 */
public interface ApiGoodsSpecPropService extends IService<MGoodsSpecPropEntity> {

    /**
     * 数据同步
     */
    void syncAll(List<ApiGoodsSpecPropVo> vo);
}
