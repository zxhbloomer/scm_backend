package com.xinyirun.scm.quartz.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

/**
 * Job完成监听器，用于在Job执行完成后执行特定的操作。
 */
@Component
@Slf4j
public class SystemJobListener implements JobListener {

    /**
     * 返回监听器的名称。
     * @return 监听器的名称
     */
    @Override
    public String getName() {
        return "JobCompletionListener";
    }

    /**
     * 在Job即将被执行时调用，此处未进行任何操作。
     * @param context Job执行的上下文
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("任务 {} 即将被执行。", context.getJobDetail().getKey());
    }

    /**
     * 在Job的执行被否决时调用，此处未进行任何操作。
     * @param context Job执行的上下文
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("任务 {} 的执行被否决。", context.getJobDetail().getKey());
    }

    /**
     * 在Job被执行后调用。如果触发器是SimpleTrigger类型，并且触发次数已达到重复次数上限，
     * 则记录一条信息日志。如果触发器不是SimpleTrigger类型，记录一条警告日志。
     * @param context Job执行的上下文
     * @param jobException Job执行过程中抛出的异常
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        // 添加日志，显示已经执行调用的结果
        if (jobException != null) {
            log.debug("任务 {} 执行出错，错误信息：{}", context.getJobDetail().getKey(), jobException.getMessage());
            log.error("job执行异常", jobException);
        } else {
            log.debug("任务 {} 执行成功。", context.getJobDetail().getKey());
        }
    }
}