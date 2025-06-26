package com.xinyirun.scm.quartz.util;

import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobManagerBo;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（禁止并发执行）
 *
 * 定时任务执行
 * 
 */
@Slf4j
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(JobExecutionContext context, SJobManagerBo sysJob) throws Exception {
        log.debug("QuartzDisallowConcurrentExecution 开始执行定时任务-------start-----");
        JobInvokeUtil.invokeMethod(sysJob);
        log.debug("QuartzDisallowConcurrentExecution 开始执行定时任务-------end-----");
    }
}
