package com.xinyirun.scm.core.system.serviceimpl.business.aprefund;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundSourceAdvanceEntity;
import com.xinyirun.scm.core.system.mapper.business.ap.BApSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundSourceAdvanceMapper;
import com.xinyirun.scm.core.system.service.business.ap.IBApSourceAdvanceService;
import com.xinyirun.scm.core.system.service.business.aprefund.IBApReFundSourceAdvanceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 应付账款关联单据表-源单-预收款 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApReFundSourceAdvanceServiceImpl extends ServiceImpl<BApReFundSourceAdvanceMapper, BApReFundSourceAdvanceEntity> implements IBApReFundSourceAdvanceService {

}
