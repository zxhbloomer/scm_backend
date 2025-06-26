package com.xinyirun.scm.core.system.service.master.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecPropEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMGoodsSpecPropService extends IService<MGoodsSpecPropEntity> {

    /**
     * 启用的属性列表下拉框
     * @param searchCondition 参数
     * @return List<MGoodsSpecPropVo>
     */
    List<MGoodsSpecPropVo> selectList(MGoodsSpecPropVo searchCondition);
}
