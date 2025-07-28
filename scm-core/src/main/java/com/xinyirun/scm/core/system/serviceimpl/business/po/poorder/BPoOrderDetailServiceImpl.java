package com.xinyirun.scm.core.system.serviceimpl.business.po.poorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderDetailMapper;
import com.xinyirun.scm.core.system.service.business.po.poorder.IBPoOrderDetailService;
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
