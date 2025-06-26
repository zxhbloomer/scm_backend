package com.xinyirun.scm.core.api.service.master.v1.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiBusinessTypeVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface ApiBusinessTypeService extends IService<MBusinessTypeEntity> {

    /**
     * 数据同步
     */
    void syncAll(List<ApiBusinessTypeVo> vo);
}
