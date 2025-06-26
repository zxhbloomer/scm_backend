package com.xinyirun.scm.core.api.service.master.v1.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiCategoryVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiCategoryService extends IService<MCategoryEntity> {

    /**
     * 数据同步
     */
    void syncAll(List<ApiCategoryVo> vo);
}
