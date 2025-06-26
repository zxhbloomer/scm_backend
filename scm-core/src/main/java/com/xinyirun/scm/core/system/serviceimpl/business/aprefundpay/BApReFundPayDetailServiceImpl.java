package com.xinyirun.scm.core.system.serviceimpl.business.aprefundpay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPayDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayDetailEntity;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayDetailMapper;
import com.xinyirun.scm.core.system.service.business.appay.IBApPayDetailService;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPayDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款单明细表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApReFundPayDetailServiceImpl extends ServiceImpl<BApReFundPayDetailMapper, BApReFundPayDetailEntity> implements IBApReFundPayDetailService {

}
