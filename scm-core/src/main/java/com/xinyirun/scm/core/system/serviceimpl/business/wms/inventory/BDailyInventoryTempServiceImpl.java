package com.xinyirun.scm.core.system.serviceimpl.business.wms.inventory;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyInventoryTempEntity;
import com.xinyirun.scm.core.system.mapper.business.inventory.BDailyInventoryTempMapper;
import com.xinyirun.scm.core.system.service.business.wms.inventory.IBDailyInventoryTempService;
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
public class BDailyInventoryTempServiceImpl extends ServiceImpl<BDailyInventoryTempMapper, BDailyInventoryTempEntity> implements IBDailyInventoryTempService {

    @Autowired
    private BDailyInventoryTempMapper mapper;

}
