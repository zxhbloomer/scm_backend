package com.xinyirun.scm.core.app.serviceimpl.master.contact_list;


import com.xinyirun.scm.bean.app.vo.master.contact_list.AppContractListVo;
import com.xinyirun.scm.core.app.mapper.master.contact_list.AppContactListMapper;
import com.xinyirun.scm.core.app.service.master.contact_list.AppIContactListService;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
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
public class AppContactListServiceImpl extends AppBaseServiceImpl<AppContactListMapper, MStaffEntity> implements AppIContactListService {

    @Autowired
    private AppContactListMapper mapper;


    @Override
    public List<AppContractListVo> list(AppContractListVo searchCondition) {
        List<AppContractListVo> list = mapper.list(searchCondition);
        return list;
    }

    @Override
    public AppContractListVo getDetail(AppContractListVo searchCondition) {
        return mapper.get(searchCondition);
    }
}
