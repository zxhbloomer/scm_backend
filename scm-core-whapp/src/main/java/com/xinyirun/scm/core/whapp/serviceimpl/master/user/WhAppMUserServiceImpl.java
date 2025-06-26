package com.xinyirun.scm.core.whapp.serviceimpl.master.user;

import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.whapp.mapper.master.user.WhAppMUserMapper;
import com.xinyirun.scm.core.whapp.service.master.user.WhAppIMUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-05-17
 */
@Slf4j
@Service
public class WhAppMUserServiceImpl extends BaseServiceImpl<WhAppMUserMapper, MUserEntity> implements WhAppIMUserService {

    @Autowired
    private WhAppMUserMapper mUserMapper;

    /**
     * 获取数据byid
     *
     * @param id
     * @return
     */
    @Override
    public MUserVo selectUserById(Long id) {
        return mUserMapper.selectUserById(id);
    }

}