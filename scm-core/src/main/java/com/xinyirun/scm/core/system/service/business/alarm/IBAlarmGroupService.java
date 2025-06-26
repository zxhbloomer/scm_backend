package com.xinyirun.scm.core.system.service.business.alarm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmGroupEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;

/**
 * <p>
 * 预警组一级分类 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
public interface IBAlarmGroupService extends IService<BAlarmGroupEntity> {

    /**
     * 预警分组
     * @param vo
     * @return
     */
    IPage<BAlarmGroupVo> selectPageList(BAlarmGroupVo vo);


    /**
     * 新增
     * @param vo
     * @return
     */
    InsertResultAo<BAlarmGroupVo> insert(BAlarmGroupVo vo);

    /**
     * 更新
     * @param vo
     * @return
     */
    UpdateResultAo<BAlarmGroupVo> updateAlarm(BAlarmGroupVo vo);

    /**
     * 添加员工 穿梭框
     * @param bean
     * @return
     */
    BAlarmStaffGroupTransferVo getStaffTransferList(BAlarmStaffTransferVo bean);

    /**
     * 新增员工
     * @param bean
     * @return
     */
    BAlarmStaffGroupTransferVo setStaffTransfer(BAlarmStaffTransferVo bean);


}
