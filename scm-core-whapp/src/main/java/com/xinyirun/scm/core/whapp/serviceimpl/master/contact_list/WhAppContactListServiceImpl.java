package com.xinyirun.scm.core.whapp.serviceimpl.master.contact_list;

import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.whapp.vo.master.contact_list.WhAppContractListVo;
import com.xinyirun.scm.core.whapp.mapper.master.contact_list.WhAppContactListMapper;
import com.xinyirun.scm.core.whapp.service.master.contact_list.WhAppIContactListService;
import com.xinyirun.scm.core.whapp.serviceimpl.base.v1.WhAppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2024-12-31
 */
@Service
public class WhAppContactListServiceImpl extends WhAppBaseServiceImpl<WhAppContactListMapper, MStaffEntity> implements WhAppIContactListService {

    @Autowired
    private WhAppContactListMapper mapper;


    @Override
    public List<WhAppContractListVo> list(WhAppContractListVo searchCondition) {
        List<WhAppContractListVo> list = mapper.list(searchCondition);
        return list;
    }

    @Override
    public WhAppContractListVo getDetail(WhAppContractListVo searchCondition) {
        return mapper.get(searchCondition);
    }
}
