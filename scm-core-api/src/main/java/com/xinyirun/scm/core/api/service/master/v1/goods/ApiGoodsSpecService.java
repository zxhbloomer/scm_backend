package com.xinyirun.scm.core.api.service.master.v1.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiGoodsSpecService extends IService<MGoodsSpecEntity> {

    /**
     * 首次所有数据同步
     */
    void syncAll(List<ApiGoodsSpecVo> vo);

    /**
     * 新增同步
     */
    void syncNewOnly(List<ApiGoodsSpecVo> vo);

    /**
     * 修改同步
     */
    void syncUpdateOnly(List<ApiGoodsSpecVo> vo);
}
