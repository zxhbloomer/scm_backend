package com.xinyirun.scm.core.system.service.sys.workbench;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.workbench.SSysWorkbenchEntity;
import com.xinyirun.scm.bean.system.vo.workbench.BpmMatterVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmNoticeVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmRemindVo;
import com.xinyirun.scm.bean.system.vo.workbench.SSysWorkbenchVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-17
 */
public interface ISSysWorkbenchService extends IService<SSysWorkbenchEntity> {

    /**
     * 工作台配置-获取
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo getInfo(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-初始化
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo resetInfo(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-保存
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo saveInfo(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-快捷操作配置-保存
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo saveQuick(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-常用应用配置-保存
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo saveOfften(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-快捷操作配置
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo getQuickOperation(SSysWorkbenchVo searchCondition);

    /**
     * 工作台配置-常用应用
     * @param searchCondition
     * @return
     */
    SSysWorkbenchVo getOfftenOperation(SSysWorkbenchVo searchCondition);


    /**
     * 获取事项数据
     * @return
     */
    BpmMatterVo getMatterData();

    /**
     * 获取待办超时提醒
     * @return
     */
    BpmRemindVo getRemindData();

    /**
     * 获取通知list
     * @return
     */
    BpmNoticeVo getNoticeList();

}
