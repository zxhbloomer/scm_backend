package com.xinyirun.scm.core.system.service.business.price;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.sync.ApiBMaterialPriceVo;
import com.xinyirun.scm.bean.entity.busniess.price.BMaterialPriceEntity;
import com.xinyirun.scm.bean.system.vo.business.price.BMaterialPriceVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-15
 */
public interface IBMaterialPriceService extends IService<BMaterialPriceEntity> {

    /**
     * 查询页面
     * @param searchCondition 参数
     * @return IPage<BMaterialPriceVo>
     */
    IPage<BMaterialPriceVo> selectPage(BMaterialPriceVo searchCondition);

    /**
     * 全部同步
     * @param searchCondition
     */
    void sync(List<BMaterialPriceVo> searchCondition);

    /**
     * 部分同步, 选择ID
     * @param searchCondition
     */
    void syncAll(BMaterialPriceVo searchCondition);

    List<ApiBMaterialPriceVo> getMaterialPriceList();
}
