package com.xinyirun.scm.core.system.service.mongobackup.monitor.v2;

import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;

/**
 * @Author: Wqf
 * @Description: mq 消费者 service
 * @CreateTime : 2023/6/26 16:48
 */


public interface IBMonitorConsumerV2Service {

    /**
     * 执行
     */
    void exec(BBkMonitorLogDetailVo vo);
}
