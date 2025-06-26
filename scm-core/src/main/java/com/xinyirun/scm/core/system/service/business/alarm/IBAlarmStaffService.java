package com.xinyirun.scm.core.system.service.business.alarm;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmStaffEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
public interface IBAlarmStaffService extends IService<BAlarmStaffEntity> {

    /**
     * 新增 预警人员
     * @param vo 入参
     * @return InsertResultAo<BAlarmGroupVo>
     */
    InsertResultAo<BAlarmStaffVo> insert(BAlarmStaffVo vo);

    /**
     * 预警人员 列表查询
     * @param vo
     * @return
     */
    IPage<BAlarmStaffVo> selectPageList(BAlarmStaffVo vo);

    /**
     * 预警人员 更新
     * @param vo
     * @return
     */
    UpdateResultAo<BAlarmStaffVo> updateStaff(BAlarmStaffVo vo);
}
