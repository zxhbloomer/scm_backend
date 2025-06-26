package com.xinyirun.scm.core.system.service.business.releaseorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
public interface IBReleaseOrderDetailService extends IService<BReleaseOrderDetailEntity> {

    /**
     * 根据 releaseId 查询
     * @param id release_order表ID
     * @return
     */
    List<BReleaseOrderDetailVo> selectByReleaseId(Integer id);
}
