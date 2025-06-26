package com.xinyirun.scm.core.system.serviceimpl.quartz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.ScheduleConstants;
import com.xinyirun.scm.common.exception.job.TaskException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.quartz.CronUtil;
import com.xinyirun.scm.common.utils.string.convert.Convert;
import com.xinyirun.scm.core.system.mapper.quartz.SJobMapper;
import com.xinyirun.scm.core.system.service.quartz.ISJobService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.quartz.util.ScheduleUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
@Service public class SJobServiceImpl extends BaseServiceImpl<SJobMapper, SJobEntity> implements ISJobService {

    @Autowired
    private SJobMapper jobMapper;

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器
     * 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException {
//        scheduler.clear();
//        List<SJobVo> jobList = jobMapper.selectJobAll();
//        for (SJobVo job : jobList) {
//            try {
//                ScheduleUtils.updateSchedulerJob(job, job.getJob_group_type(), scheduler);
//            } catch (Exception e) {
//                log.debug("初始化定时任务出错：");
//                log.debug(e.getMessage());
//            }
//            log.debug("定时任务启动成功");
//        }
    }

    /**
     * 获取quartz调度器的计划任务列表
     *
     * @param searchCondition 调度信息
     * @return
     */
    @Override
    public IPage<SJobVo> selectJobList(SJobVo searchCondition) {
        // 分页条件
        Page<SJobVo> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return jobMapper.selectJobList(pageCondition, searchCondition);
    }

    /**
     * 通过调度任务ID查询调度信息
     *
     * @param jobId 调度任务ID
     * @return 调度任务对象信息
     */
    @Override
    public SJobVo selectJobById(Long jobId) {
        return jobMapper.selectJobById(jobId);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pauseJob(SJobVo job) {
        job.setIs_effected(ScheduleConstants.Status.PAUSE.getValue());
        job.setC_id(null);
        job.setC_time(null);
        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);
        int rows = jobMapper.updateById(entity);
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int resumeJob(SJobVo job) {
        job.setIs_effected(ScheduleConstants.Status.NORMAL.getValue());
        job.setC_id(null);
        job.setC_time(null);

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        int rows = jobMapper.updateById(entity);
        return rows;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteJob(SJobVo job) {
        job.setIs_effected(ScheduleConstants.Status.PAUSE.getValue());
        job.setIs_del(true);
        job.setC_id(null);
        job.setC_time(null);

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        int rows = jobMapper.updateById(entity);
        return rows;
    }

    /**
     * 批量删除调度信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobByIds(String ids) {
        Long[] jobIds = Convert.toLongArray(ids);
        for (Long jobId : jobIds) {
            SJobVo job = jobMapper.selectJobById(jobId);
            deleteJob(job);
        }
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(SJobVo job) throws SchedulerException {
        int rows = 0;
        boolean status = job.getIs_effected();
        if (ScheduleConstants.Status.NORMAL.getValue() == status) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue() == status) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(SJobVo job) throws SchedulerException {
        Long jobId = job.getId();
        String jobGroup = job.getJob_group_type();
        SJobVo properties = selectJobById(job.getId());
        // 参数
//        JobDataMap dataMap = new JobDataMap();
//        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
//        scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, jobGroup), dataMap);
    }

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertJob(SJobVo job) {
        job.setIs_effected(ScheduleConstants.Status.PAUSE.getValue());

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        int rows = jobMapper.insert(entity);
        return rows;
    }

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateJob(SJobVo job) throws SchedulerException, TaskException {
        SJobVo properties = selectJobById(job.getId());
        job.setC_id(null);
        job.setC_time(null);

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        int rows = jobMapper.updateById(entity);
        if (rows > 0) {
            ScheduleUtils.updateSchedulerJob(job, properties.getJob_group_type(), scheduler, SecurityUtil.getTenantIdByRequest());
        }
        return rows;
    }

//    /**
//     * 更新任务
//     *
//     * @param job      任务对象
//     * @param jobGroup 任务组名
//     */
//    public void updateSchedulerJob(SJobVo job, String jobGroup) throws SchedulerException, TaskException {
//        Long jobId = job.getId();
//        // 判断是否存在
//        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
//        if (scheduler.checkExists(jobKey)) {
//            // 防止创建时存在数据问题 先移除，然后在执行创建操作
//            scheduler.deleteJob(jobKey);
//        }
//        if(job.getIs_cron()) {
//            // 表达式
//            ScheduleUtils.createScheduleJobCron(scheduler, job);
//        } else {
//            ScheduleUtils.createScheduleJobSimpleTrigger(scheduler, job);
//        }
//    }

    /**
     * 校验cron表达式是否有效
     *
     * @param cronExpression 表达式
     * @return 结果
     */
    @Override
    public boolean checkCronExpressionIsValid(String cronExpression) {
        return CronUtil.isValid(cronExpression);
    }

    /**
     * 查询调度任务
     *
     * @param serialId 编号
     * @param serialType 类型
     * @return 调度任务对象信息
     */
    @Override
    public SJobVo selectJobBySerialId(Long serialId, String serialType) {
        return jobMapper.selectJobBySerialId(serialId, serialType);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param job 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<SJobVo> insert(SJobVo job) throws SchedulerException, TaskException {
        // 插入前check
        CheckResultAo cr = checkLogic(job, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        int rtn = jobMapper.insert(entity);
        if(rtn == 0){
            throw new InsertErrorException("新增保存失败。");
        }

        SJobVo rtnVo = selectJobById(entity.getId());

        // 更新定时任务
        ScheduleUtils.updateSchedulerJob(rtnVo, rtnVo.getJob_group_type(), scheduler, SecurityUtil.getTenantIdByRequest());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtnVo);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param job 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(SJobVo job) throws SchedulerException, TaskException {
        // 插入前check
        CheckResultAo cr = checkLogic(job, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
        job.setC_id(null);
        job.setC_time(null);

        SJobEntity entity = (SJobEntity) BeanUtilsSupport.copyProperties(job, SJobEntity.class);

        // 更新定时任务
        ScheduleUtils.updateSchedulerJob(job, job.getJob_group_type(), scheduler, SecurityUtil.getTenantIdByRequest());
        return UpdateResultUtil.OK(jobMapper.updateById(entity));
    }

    /**
     * 删除定时任务
     *
     * @param bean 需要id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(SJobVo bean) {
        pauseJob(bean);
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(SJobVo sJobVo, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<SJobVo> task_name_insert = selectByName(sJobVo.getJob_name(), null);
                if (task_name_insert.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：任务名称【"+ sJobVo.getJob_name() +"】出现重复", sJobVo.getJob_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<SJobVo> task_name_update = selectByName(sJobVo.getJob_name(), sJobVo.getId());

                if (task_name_update.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：任务名称【"+ sJobVo.getJob_name() +"】出现重复！", sJobVo.getJob_name());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 获取列表，查询所有数据
     * @param name
     * @param equal_id
     * @return
     */
    public List<SJobVo> selectByName(String name, Long equal_id) {
        // 查询 数据
        List<SJobVo> list = jobMapper.selectByName(name, equal_id);
        return list;
    }
}
