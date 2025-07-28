package com.xinyirun.scm.core.system.serviceimpl.business.so.soorder;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderDetailEntity;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderDetailMapper;
import com.xinyirun.scm.core.system.service.business.so.soorder.IBSoOrderDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 销售订单明细表-商品 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Service
public class BSoOrderDetailServiceImpl extends ServiceImpl<BSoOrderDetailMapper, BSoOrderDetailEntity> implements IBSoOrderDetailService {

}