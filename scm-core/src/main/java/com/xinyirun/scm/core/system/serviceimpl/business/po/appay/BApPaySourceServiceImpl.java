package com.xinyirun.scm.core.system.serviceimpl.business.po.appay;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPaySourceEntity;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPaySourceMapper;
import com.xinyirun.scm.core.system.service.business.po.appay.IBApPaySourceService;
import org.springframework.stereotype.Service;

/**
 * 付款来源表 服务实现类
 */
@Service
public class BApPaySourceServiceImpl extends ServiceImpl<BApPaySourceMapper, BApPaySourceEntity> implements IBApPaySourceService {
} 