package com.xinyirun.scm.core.whapp.service.master.contact_list;

import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.whapp.vo.master.contact_list.WhAppContractListVo;
import com.xinyirun.scm.core.whapp.service.base.v1.WhAppIBaseService;

import java.util.List;

/**
 * <p>
 *  通讯录 服务
 * </p>
 *
 * @author zxh
 * @since 2024-12-31
 */
public interface WhAppIContactListService extends WhAppIBaseService<MStaffEntity> {

    /**
     * 获取通讯录列表，页面查询
     */
    List<WhAppContractListVo> list(WhAppContractListVo searchCondition) ;

    /**
     * 获取通讯录
     */
    WhAppContractListVo getDetail(WhAppContractListVo searchCondition) ;

}
