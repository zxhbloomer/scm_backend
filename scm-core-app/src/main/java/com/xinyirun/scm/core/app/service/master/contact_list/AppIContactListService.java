package com.xinyirun.scm.core.app.service.master.contact_list;


import com.xinyirun.scm.bean.app.vo.master.contact_list.AppContractListVo;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

import java.util.List;

/**
 * <p>
 *  通讯录 服务
 * </p>
 *
 * @author zxh
 * @since 2024-12-31
 */
public interface AppIContactListService extends AppIBaseService<MStaffEntity> {

    /**
     * 获取通讯录列表，页面查询
     */
    List<AppContractListVo> list(AppContractListVo searchCondition) ;

    /**
     * 获取通讯录
     */
    AppContractListVo getDetail(AppContractListVo searchCondition) ;

}
