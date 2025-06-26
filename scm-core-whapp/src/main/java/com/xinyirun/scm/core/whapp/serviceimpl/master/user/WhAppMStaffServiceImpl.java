package com.xinyirun.scm.core.whapp.serviceimpl.master.user;

import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.whapp.vo.master.user.WhAppStaffUserBpmInfoVo;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.whapp.mapper.master.user.WhAppMStaffMapper;
import com.xinyirun.scm.core.whapp.service.master.user.WhAppIMStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Service
public class WhAppMStaffServiceImpl extends BaseServiceImpl<WhAppMStaffMapper, MStaffEntity> implements WhAppIMStaffService {

    @Autowired
    private WhAppMStaffMapper mapper;

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MStaffVo selectByid(Long id){
        MStaffVo searchCondition = new MStaffVo();
        searchCondition.setId(id);
        MStaffVo vo = mapper.selectByid(searchCondition);
        return vo;
    }

    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    @Override
    public WhAppStaffUserBpmInfoVo getBpmDataByStaffid(WhAppStaffUserBpmInfoVo vo) {
        return mapper.getBpmDataByStaffid(vo.getId());
    }
}
