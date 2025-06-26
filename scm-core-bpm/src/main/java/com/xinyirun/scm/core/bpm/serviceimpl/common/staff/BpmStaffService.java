package com.xinyirun.scm.core.bpm.serviceimpl.common.staff;

import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MPositionInfoVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.core.bpm.mapper.common.staff.BpmStaffMapper;
import com.xinyirun.scm.core.bpm.service.common.staff.IBpmStaffService;
import com.xinyirun.scm.core.bpm.serviceimpl.base.v1.BpmBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmStaffService extends BpmBaseServiceImpl<BpmStaffMapper, MStaffEntity> implements IBpmStaffService {


    @Autowired
    private BpmStaffMapper mapper;

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
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MStaffVo selectByCode(String code){
        MStaffVo searchCondition = new MStaffVo();
        searchCondition.setCode(code);
        MStaffVo vo = mapper.selectByCode(searchCondition);
        return vo;
    }
}
