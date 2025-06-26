package com.xinyirun.scm.core.api.service.master.v1.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiOwnerVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
public interface ApiOwnerService extends IService<MOwnerEntity> {
    /**
     * 货主下拉
     */
    List<ApiOwnerVo> getOwner(ApiOwnerVo vo);
}
