package com.xinyirun.scm.quartz.util;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.system.vo.business.ai.KnowledgeBaseStatisticsParamVo;
import com.xinyirun.scm.bean.system.bo.tenant.manager.quartz.SJobManagerBo;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import com.xinyirun.scm.common.constant.ScheduleConstants;
import com.xinyirun.scm.common.exception.job.TaskException;
import com.xinyirun.scm.common.utils.LocalDateTimeUtils;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.spring.SpringUtils;
import com.xinyirun.scm.quartz.service.master.ISJobManagerQuartzService;
import com.xinyirun.scm.quartz.service.tenant.ISJobQuartzService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.time.LocalDateTime;

/**
 * 定时任务工具类
 *
 */
@Slf4j
public class ScheduleUtils {

    public static String CRON_MINUTE = " 0 * * * * ?";
    public static String CRON_HOUR = " 0 0 * * * ?";
    public static String CRON_DAY = "0 0 %s * * ?";

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     * @param scheduler
     */
    public static void updateSchedulerJob(SJobVo job, String jobGroup, Scheduler scheduler, String tenant_code) throws SchedulerException, TaskException {
        Long jobId = job.getId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            log.debug("更新任务，进行删除--jobname:" + job.getJob_name());
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        /**
         * 生成SJobManagerBo对象，并且保存到数据库当中
         */
        SJobManagerBo jobManagerBo = new SJobManagerBo();
        BeanUtilsSupport.copyProperties(job, jobManagerBo);
        jobManagerBo.setTenant_job_id(job.getId());
        jobManagerBo.setTenant_code(tenant_code);
        // 写入数据库当中
        SpringUtils.getBean(ISJobManagerQuartzService.class).insert(jobManagerBo);
        if(job.getIs_cron()) {
            // 表达式
            ScheduleUtils.createScheduleJobCron(scheduler, jobManagerBo);
        } else {
            ScheduleUtils.createScheduleJobSimpleTrigger(scheduler, jobManagerBo);
        }
        scheduler.start();
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务:每日库存差量
     */
    public static boolean createJobDailyInventoryDIff(Scheduler scheduler, String job_serial_type, Long job_serial_id ,String param) throws SchedulerException {
        SJobManagerBo job = new SJobManagerBo();
        job.setJob_name(SchedulerConstants.DAILY_INVENTORY_DIFF.JOB_NAME);
        job.setJob_group_type(SchedulerConstants.DAILY_INVENTORY_DIFF.JOB_GROUP_TYPE);
        job.setJob_desc(SchedulerConstants.DAILY_INVENTORY_DIFF.JOB_DESC);
        job.setMisfire_policy(SchedulerConstants.DAILY_INVENTORY_DIFF.MISFIRE_POLICY);
        job.setConcurrent(SchedulerConstants.DAILY_INVENTORY_DIFF.CONCURRENT);
        job.setIs_cron(SchedulerConstants.DAILY_INVENTORY_DIFF.IS_CRON);
        job.setIs_del(SchedulerConstants.DAILY_INVENTORY_DIFF.IS_DEL);
        job.setIs_effected(SchedulerConstants.DAILY_INVENTORY_DIFF.IS_EFFECTED);
        job.setNext_fire_time(LocalDateTime.now().plusSeconds(SchedulerConstants.DAILY_INVENTORY_DIFF.NEXT_FIRE_SECONDS));
        job.setClass_name(SchedulerConstants.DAILY_INVENTORY_DIFF.CLASS_NAME);
        job.setMethod_name(SchedulerConstants.DAILY_INVENTORY_DIFF.METHOD_NAME);
        job.setParam_class(SchedulerConstants.DAILY_INVENTORY_DIFF.PARAM_CLASS);
        job.setParam_data(param);
        job.setJob_serial_type(job_serial_type);
        job.setJob_serial_id(job_serial_id);
        // 写入数据库当中
        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job,SJobEntity.class);
        SpringUtils.getBean(ISJobQuartzService.class).save(entity);
        job.setId(entity.getId());
        return createScheduleJobSimpleTrigger(scheduler, job);
    }

    /**
     * 创建定时任务：AI知识库统计
     *
     * @param scheduler Quartz调度器
     * @param tenantCode 租户code（关键参数，用于AbstractQuartzJob自动切换数据源）
     * @param kbUuid 知识库UUID（格式：{tenantCode}::{uuid}）
     * @param triggerReason 触发原因（可选，用于日志）
     * @return 是否创建成功
     * @throws SchedulerException 调度异常
     */
    public static boolean createJobKnowledgeBaseStatistics(
            Scheduler scheduler,
            String tenantCode,
            String kbUuid,
            String triggerReason
    ) throws SchedulerException {

        SJobManagerBo job = new SJobManagerBo();

        // 任务名包含kbUuid，确保唯一性
        job.setJob_name(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.JOB_NAME + "-" + kbUuid);
        job.setJob_group_type(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.JOB_GROUP_TYPE);
        job.setJob_desc(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.JOB_DESC + " - " + kbUuid);
        job.setMisfire_policy(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.MISFIRE_POLICY);
        job.setConcurrent(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.CONCURRENT);
        job.setIs_cron(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.IS_CRON);
        job.setIs_del(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.IS_DEL);
        job.setIs_effected(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.IS_EFFECTED);
        job.setNext_fire_time(LocalDateTime.now().plusSeconds(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.NEXT_FIRE_SECONDS));
        job.setClass_name(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.CLASS_NAME);
        job.setMethod_name(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.METHOD_NAME);
        job.setParam_class(SchedulerConstants.KNOWLEDGE_BASE_STATISTICS.PARAM_CLASS);

        // 设置租户code（关键：AbstractQuartzJob会自动切换数据源）
        job.setTenant_code(tenantCode);

        // 构建参数对象（JSON格式）
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            KnowledgeBaseStatisticsParamVo paramVo = new KnowledgeBaseStatisticsParamVo();
            paramVo.setKbUuid(kbUuid);
            paramVo.setTenantCode(tenantCode);
            paramVo.setTriggerReason(triggerReason);
            job.setParam_data(objectMapper.writeValueAsString(paramVo));
        } catch (Exception e) {
            log.error("构建AI知识库统计任务参数失败，kbUuid: {}", kbUuid, e);
            throw new SchedulerException("参数序列化失败", e);
        }

        job.setJob_serial_type("ai_kb_statistics");
        job.setJob_serial_id(0L); // 全局任务，不关联具体业务ID

        // 写入租户库
        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);
        SpringUtils.getBean(ISJobQuartzService.class).save(entity);
        job.setId(entity.getId());

        // 写入Master库（关键：包含tenant_code）
        job.setTenant_job_id(entity.getId());
        SpringUtils.getBean(ISJobManagerQuartzService.class).insert(job);

        // 创建调度任务（SimpleTrigger单次执行）
        return createScheduleJobSimpleTrigger(scheduler, job);
    }



//    /**
//     * 创建定时任务:每日库存差量
//     */
//    @SysLogAnnotion("创建定时任务：物料转换")
//    public static boolean createJobMaterialConvert(Scheduler scheduler, String job_serial_type, Long job_serial_id, String type, String frequency, String time, String param) throws SchedulerException {
//
//        SJobVo job = new SJobVo();
//        job.setJob_name(SchedulerConstants.MATERIAL_CONVERT.JOB_NAME);
//        job.setJob_group_type(SchedulerConstants.MATERIAL_CONVERT.JOB_GROUP_TYPE);
//        job.setJob_desc(SchedulerConstants.MATERIAL_CONVERT.JOB_DESC);
//        job.setConcurrent(SchedulerConstants.MATERIAL_CONVERT.CONCURRENT);
//        job.setIs_del(SchedulerConstants.MATERIAL_CONVERT.IS_DEL);
//        job.setIs_effected(SchedulerConstants.MATERIAL_CONVERT.IS_EFFECTED);
//        job.setClass_name(SchedulerConstants.MATERIAL_CONVERT.CLASS_NAME);
//        job.setMethod_name(SchedulerConstants.MATERIAL_CONVERT.METHOD_NAME);
//        job.setParam_class(SchedulerConstants.MATERIAL_CONVERT.PARAM_CLASS);
//        job.setNext_fire_time(LocalDateTime.now().plusSeconds(SchedulerConstants.MATERIAL_CONVERT.NEXT_FIRE_SECONDS));
//        job.setParam_data(param);
//        job.setJob_serial_type(job_serial_type);
//        job.setJob_serial_id(job_serial_id);
//
//        if (Objects.equals(type, DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ZERO)) {
//            //单次任务
//            job.setMisfire_policy(SchedulerConstants.MATERIAL_CONVERT.MISFIRE_POLICY2);
//            job.setIs_cron(SchedulerConstants.MATERIAL_CONVERT.IS_CRON_FALSE);
//
//        } else if (Objects.equals(type, DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE)) {
//            // 定时任务
//            job.setMisfire_policy(SchedulerConstants.MATERIAL_CONVERT.MISFIRE_POLICY1);
//            job.setIs_cron(SchedulerConstants.MATERIAL_CONVERT.IS_CRON_TRUE);
//            if (Objects.equals(frequency, DictConstant.DICT_B_MATERIAL_CONVERT_FREQUENCY_ZERO)) {
//                // 每分钟
//                job.setCron_expression(ScheduleUtils.CRON_MINUTE);
//            } else if (Objects.equals(frequency, DictConstant.DICT_B_MATERIAL_CONVERT_FREQUENCY_ONE)) {
//                // 每小时
//                job.setCron_expression(ScheduleUtils.CRON_HOUR);
//            } else if (Objects.equals(frequency, DictConstant.DICT_B_MATERIAL_CONVERT_FREQUENCY_TWO)) {
//                // 每天
//
//                job.setCron_expression(String.format(ScheduleUtils.CRON_DAY, time));
//            }
//        }
//
//        // 写入数据库当中
//        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job,SJobEntity.class);
//        SpringUtils.getBean(ISJobQuartzService.class).save(entity);
//        job.setId(entity.getId());
//        return createScheduleJobSimpleTrigger(scheduler, job);
//    }


    /**
     * 创建定时任务:SimpleTrigger
     */
//    @SysLogAnnotion("创建定时任务SimpleTrigger")
    public static boolean createScheduleJobSimpleTrigger(Scheduler scheduler, SJobManagerBo job) throws SchedulerException {
        log.debug("创建定时任务开始SimpleTrigger");

        Class<? extends Job> jobClass = getQuartzJobClass(job);

        // 构建job信息
        Long jobId = job.getId();
        String jobGroup = job.getJob_group_type();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();
        // 设置一个用于触发的时间
        SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        simpleScheduleBuilder = handleSimpleScheduleMisfirePolicy(simpleScheduleBuilder);
        // 新的Simpletrigger
        SimpleTrigger trigger ;
        if (job.getNext_fire_time().isBefore(LocalDateTime.now())) {
            trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(simpleScheduleBuilder)
                .startNow()
                .build();
        } else {
            trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(simpleScheduleBuilder)
                .startAt(LocalDateTimeUtils.convertLDTToDate(job.getNext_fire_time()))
                .build();
        }

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            log.debug("定时任务已存在，进行删除--jobname:" + job.getJob_name());
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
            log.debug("定时任务已存在，删除成功--jobname:" + job.getJob_name());
        }

        log.debug("创建定时任务：启动--jobname:" + job.getJob_name());
        scheduler.scheduleJob(jobDetail, trigger);
        log.debug("创建定时任务：成功--jobname:" + job.getJob_name());

        // 暂停任务
        if (job.getIs_effected() != null && job.getIs_effected() == ScheduleConstants.Status.PAUSE.getValue()) {
            log.debug("定时任务，进行暂停：开始--jobname:" + job.getJob_name());
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            log.debug("定时任务，进行暂停：成功--jobname:" + job.getJob_name());
        } else {
            log.debug("定时任务，暂停恢复：开始--jobname:" + job.getJob_name());
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            log.debug("定时任务，暂停恢复：结束--jobname:" + job.getJob_name());
        }
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建定时任务:Cron表达式
     */
//    @SysLogAnnotion("创建定时任务CroTrigger")
    public static boolean createScheduleJobCron(Scheduler scheduler, SJobManagerBo job) throws SchedulerException, TaskException {
        log.debug("创建定时任务开始CronTrigger");

        Class<? extends Job> jobClass = getQuartzJobClass(job);

        // 构建job信息
        Long jobId = job.getId();
        String jobGroup = job.getJob_group_type();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCron_expression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger =
            TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup)).withSchedule(cronScheduleBuilder)
                .build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            log.debug("定时任务已存在，进行删除--jobname:" + job.getJob_name());
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
            log.debug("定时任务已存在，删除成功--jobname:" + job.getJob_name());
        }
        log.debug("创建定时任务：启动--jobname:" + job.getJob_name());
        scheduler.scheduleJob(jobDetail, trigger);
        log.debug("创建定时任务：成功--jobname:" + job.getJob_name());

        // 暂停任务
        if (job.getIs_effected().equals(ScheduleConstants.Status.PAUSE.getValue())) {
            log.debug("定时任务，需要进行暂停：开始");
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            log.debug("定时任务，需要进行暂停：成功");
        } else {
            log.debug("定时任务，暂停恢复：开始--jobname:" + job.getJob_name());
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
            log.debug("定时任务，暂停恢复：结束--jobname:" + job.getJob_name());
        }
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置定时任务策略：Cron
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SJobManagerBo job, CronScheduleBuilder cb)
        throws TaskException {
        switch (job.getMisfire_policy()) {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new TaskException(
                    "The task misfire policy '" + job.getMisfire_policy() + "' cannot be used in cron schedule tasks",
                    TaskException.Code.CONFIG_ERROR);
        }
    }

    /**
     * 设置定时任务策略：Cron
     */
    public static SimpleScheduleBuilder handleSimpleScheduleMisfirePolicy(SimpleScheduleBuilder sb) {
        return sb.withMisfireHandlingInstructionIgnoreMisfires();
    }

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
//    private static Class<? extends Job> getQuartzJobClass(SJobVo sysJob) {
//        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
//        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
//    }
    private static Class<? extends Job> getQuartzJobClass(SJobManagerBo sysJob) {
        return QuartzDisallowConcurrentExecution.class;
    }
}