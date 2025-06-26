package com.xinyirun.scm.core.bpm.serviceimpl.business;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import com.xinyirun.scm.core.bpm.mapper.business.BpmUsersMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Service
public class BpmUsersServiceImpl extends ServiceImpl<BpmUsersMapper, BpmUsersEntity> implements IBpmUsersService {


    @Autowired
    private BpmUsersMapper mapper;

    /**
     * 根据用户编码查询用户信息
     */
    @Override
    public BpmUsersEntity selectByCode(String assignee) {
        return mapper.selectByCode(assignee);
    }
}
