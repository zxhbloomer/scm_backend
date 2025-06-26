package com.xinyirun.scm.quartz.util;

import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.entity.quartz.SJobLogEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobManagerBo;
import com.xinyirun.scm.common.constant.ScheduleConstants;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.logging.TenantLogContextHolder;
import com.xinyirun.scm.common.utils.spring.SpringUtils;
import com.xinyirun.scm.quartz.service.tenant.ISJobLogQuartzService;
import com.xinyirun.scm.quartz.service.tenant.ISJobQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 抽象quartz调用
 *
 * @author
 */
@Slf4j
@DisallowConcurrentExecution
public abstract class AbstractQuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SJobManagerBo sysJob = new SJobManagerBo();
        BeanUtilsSupport.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), sysJob);
        DataSourceHelper.use(sysJob.getTenant_code());
        try {

            before(context, sysJob);
            // 获取最新的数据

            if (sysJob != null) {
                sysJob.setFire_time(LocalDateTimeUtils.convertDateToLDT(context.getFireTime()));
                sysJob.setScheduled_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getScheduledFireTime()));
                sysJob.setPrev_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getPreviousFireTime()));
                sysJob.setNext_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getNextFireTime()));
                SJobEntity sJobEntity = new SJobEntity();
                BeanUtilsSupport.copyProperties(sysJob, sJobEntity);
                // 写入数据库当中
                SpringUtils.getBean(ISJobQuartzService.class).updateJob(sJobEntity);

                log.debug("AbstractQuartzJob 开始执行定时任务-------start-----");

                // 执行任务
                doExecute(context, sysJob);
                log.debug("AbstractQuartzJob 开始执行定时任务-------end-----");
            }
            after(context, sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);

            throw new JobExecutionException(e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SJobManagerBo sysJob) {
        // 设置租户日志上下文
        TenantLogContextHolder.setTenantId(sysJob.getTenant_code());
        DataSourceHelper.use(sysJob.getTenant_code());
    }

    /**
     * 执行后，更新日志
     *
     */
    protected void after(JobExecutionContext context, SJobManagerBo sysJob, Exception e) {
        // 设置租户日志上下文
        TenantLogContextHolder.setTenantId(sysJob.getTenant_code());
        DataSourceHelper.use(sysJob.getTenant_code());

        SJobLogEntity sysJobLog = new SJobLogEntity();
        BeanUtilsSupport.copyProperties(sysJob, sysJobLog);
        sysJobLog.setJob_id(sysJob.getId());
        sysJobLog.setId(null);
        sysJobLog.setFire_time(LocalDateTimeUtils.convertDateToLDT(context.getFireTime()));
        sysJobLog.setScheduled_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getScheduledFireTime()));
        sysJobLog.setPrev_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getPreviousFireTime()));
        sysJobLog.setNext_fire_time(LocalDateTimeUtils.convertDateToLDT(context.getNextFireTime()));
        if(e != null) {
            // 执行出错
            sysJobLog.setMsg("NG：" + e.getMessage());
        } else {
            // 执行成功
            sysJobLog.setMsg("OK");
        }

        // 写入数据库当中
        SpringUtils.getBean(ISJobLogQuartzService.class).addJobLog(sysJobLog);
        // 清理租户上下文
        TenantLogContextHolder.clear();
        // 清理数据源上下文
        DataSourceHelper.close();
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SJobManagerBo sysJob) throws Exception;
}
