package com.xinyirun.scm.core.system.serviceimpl.business.appay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPaySourceEntity;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPaySourceMapper;
import com.xinyirun.scm.core.system.service.business.appay.IBApPaySourceService;
import org.springframework.stereotype.Service;

/**
 * 付款来源表 服务实现类
 */
@Service
public class BApPaySourceServiceImpl extends ServiceImpl<BApPaySourceMapper, BApPaySourceEntity> implements IBApPaySourceService {
} 