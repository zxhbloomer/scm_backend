package com.xinyirun.scm.core.system.serviceimpl.business.wms.inventory;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyInventoryWorkEntity;
import com.xinyirun.scm.core.system.mapper.business.inventory.BDailyInventoryWorkMapper;
import com.xinyirun.scm.core.system.service.business.wms.inventory.IBDailyInventoryWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-08
 */
@Service
public class BDailyInventoryWorkServiceImpl extends ServiceImpl<BDailyInventoryWorkMapper, BDailyInventoryWorkEntity> implements IBDailyInventoryWorkService {

    @Autowired
    private BDailyInventoryWorkMapper mapper;

}
