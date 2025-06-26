package com.xinyirun.scm.core.system.serviceimpl.releaseorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;
import com.xinyirun.scm.core.system.mapper.business.releaseorder.BReleaseOrderDetailMapper;
import com.xinyirun.scm.core.system.service.business.releaseorder.IBReleaseOrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Service
public class BReleaseOrderDetailServiceImpl extends ServiceImpl<BReleaseOrderDetailMapper, BReleaseOrderDetailEntity> implements IBReleaseOrderDetailService {

    /**
     * 根据 releaseId 查询
     *
     * @param id release_order表ID
     * @return
     */
    @Override
    public List<BReleaseOrderDetailVo> selectByReleaseId(Integer id) {
        return baseMapper.selectByReleaseId(id);
    }
}
