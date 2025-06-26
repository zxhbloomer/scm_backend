package com.xinyirun.scm.core.api.service.business.v1.releaseorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.releaseorder.ApiBReleaseOrderVo;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderEntity;

import java.util.List;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:26
 */
public interface ApiIBReleaseOrderService extends IService<BReleaseOrderEntity> {

    /**
     * 同步数据
     * @param list
     */
    void sync(List<ApiBReleaseOrderVo> list);
}
