package com.xinyirun.scm.core.system.serviceimpl.business.aprefund;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.ap.BApDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundDetailEntity;
import com.xinyirun.scm.core.system.mapper.business.ap.BApDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundDetailMapper;
import com.xinyirun.scm.core.system.service.business.ap.IBApDetailService;
import com.xinyirun.scm.core.system.service.business.aprefund.IBApReFundDetailService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 应付退款明细表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApReFundDetailServiceImpl extends ServiceImpl<BApReFundDetailMapper, BApReFundDetailEntity> implements IBApReFundDetailService {

}
