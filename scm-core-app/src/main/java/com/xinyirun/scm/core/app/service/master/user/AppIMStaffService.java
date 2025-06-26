package com.xinyirun.scm.core.app.service.master.user;

import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;

public interface AppIMStaffService extends AppIBaseService<MStaffEntity> {

    /**
     * 获取明细，页面查询
     */
    AppMStaffVo getDetail() ;

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    AppUpdateResultAo<Integer> update(AppMStaffVo entity);

    /**
     * 更新手机号（选择字段，策略更新）
     */
    AppMStaffVo updatePhone(String mobile_phone);

    /**
     * 更新头像
     */
    void saveAvatar(String url);

    /**
     * 获取数据byid
     */
    AppMStaffVo selectById(Long id);

    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    AppStaffUserBpmInfoVo getBpmDataByStaffid(AppStaffUserBpmInfoVo vo);
}
