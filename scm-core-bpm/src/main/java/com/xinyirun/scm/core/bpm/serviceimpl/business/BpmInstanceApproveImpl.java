package com.xinyirun.scm.core.bpm.serviceimpl.business;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceApproveEntity;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceApproveMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceApproveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
@Service
public class BpmInstanceApproveImpl extends ServiceImpl<BpmInstanceApproveMapper, BpmInstanceApproveEntity> implements IBpmInstanceApproveService {

    @Autowired
    private BpmInstanceApproveMapper mapper;

}
