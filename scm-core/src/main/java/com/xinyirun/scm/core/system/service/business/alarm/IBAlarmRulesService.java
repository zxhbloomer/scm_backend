package com.xinyirun.scm.core.system.service.business.alarm;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmRulesEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmRulesVo;

import java.util.List;

/**
 * <p>
 * 预警规则清单 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
public interface IBAlarmRulesService extends IService<BAlarmRulesEntity> {

    /**
     * 查询事件(同步)预警原则
     * @param type 预警类型 0事件预警
     * @return
     */
    List<BAlarmRulesBo> selectStaffAlarm(String type);

    /**
     * 分页查询
     * @param vo
     * @return
     */
    IPage<BAlarmRulesVo> selectPageList(BAlarmRulesVo vo);

    /**
     * 新增
     * @param vo
     * @return
     */
    InsertResultAo<BAlarmRulesVo> insert(BAlarmRulesVo vo);


    /**
     * 更新
     * @param vo
     * @return
     */
    UpdateResultAo<BAlarmRulesVo> edit(BAlarmRulesVo vo);

    /**
     * 启用, 禁用
     * @param vo
     */
    void enable(List<BAlarmRulesVo> vo);
}
