package com.xinyirun.scm.core.system.service.business.alarm;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmRulesGroupEntity;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
public interface IBAlarmRulesGroupService extends IService<BAlarmRulesGroupEntity> {

    /**
     * 根据 rules_id 查询人员
     * @param rules
     * @return
     */
//    Set<Integer> selectStaffListBy(Set<Integer> rules);


    /**
     * 预警人员设置
     * @param bean
     * @return
     */
    void setStaffTransfer(BAlarmStaffTransferVo bean);

    /**
     * 查询预警组下所有员工
     * @param bean
     * @return
     */
    BAlarmStaffGroupTransferVo getStaffTransferList(BAlarmStaffTransferVo bean);

    /**
     * 查询预警组穿梭框
     * @param bean
     * @return
     */
    BAlarmStaffGroupTransferVo getGroupTransferList(BAlarmStaffTransferVo bean);

    /**
     * 新增预警组
     * @param bean
     */
    void setGroupTransfer(BAlarmStaffTransferVo bean);
}
