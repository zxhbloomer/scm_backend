package com.xinyirun.scm.core.system.service.quartz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import com.xinyirun.scm.common.exception.job.TaskException;
import org.quartz.SchedulerException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
public interface ISJobService extends IService<SJobEntity> {

    /**
     * 获取quartz调度器的计划任务
     *
     * @param searchCondition 调度信息
     * @return 调度任务集合
     */
    IPage<SJobVo> selectJobList(SJobVo searchCondition) ;

    /**
     * 通过调度任务ID查询调度信息
     *
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    SJobVo selectJobById(Long jobId);

    /**
     * 暂停任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int pauseJob(SJobVo job) throws SchedulerException;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int resumeJob(SJobVo job) throws SchedulerException;

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     * @return 结果
     */
    int deleteJob(SJobVo job) throws SchedulerException;

    /**
     * 批量删除调度信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    void deleteJobByIds(String ids) throws SchedulerException;

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     * @return 结果
     */
    int changeStatus(SJobVo job) throws SchedulerException;

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    void run(SJobVo job) throws SchedulerException;

    /**
     * 新增任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int insertJob(SJobVo job) throws SchedulerException, TaskException;

    /**
     * 更新任务
     *
     * @param job 调度信息
     * @return 结果
     */
    int updateJob(SJobVo job) throws SchedulerException, TaskException;

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    boolean checkCronExpressionIsValid(String cronExpression);

    /**
     * 查询调度任务
     *
     * @param serialId 编号
     * @param serialType 类型
     * @return 调度任务对象信息
     */
    SJobVo selectJobBySerialId(Long serialId, String serialType);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity
     * @return
     */
    InsertResultAo<SJobVo> insert(SJobVo entity) throws SchedulerException, TaskException;

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SJobVo entity) throws SchedulerException, TaskException;

    /**
     * 删除定时任务
     * @param bean 需要id
     */
    void delete(SJobVo bean);
}
