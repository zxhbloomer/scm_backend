package com.xinyirun.scm.core.system.serviceimpl.business.poorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderDetailMapper;
import com.xinyirun.scm.core.system.service.business.poorder.IBPoOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 采购订单明细表-商品 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Service
public class BPoOrderDetailServiceImpl extends ServiceImpl<BPoOrderDetailMapper, BPoOrderDetailEntity> implements IBPoOrderDetailService {

}
