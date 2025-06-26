package com.xinyirun.scm.core.bpm.service.business;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceVo;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
public interface IBpmInstanceService extends IService<BpmInstanceEntity> {

    /**
     *  保存待办任务数据 以及 更新流程节点信息
     */
    void saveTaskCreated(TaskEntity task) throws Exception;

    /**
     *  审批流任务已完成
     */
    void saveTaskCompleted(TaskEntity task) throws Exception;

    /**
     * 查看我发起的实例（流程）
     */
    IPage<BBpmInstanceVo> selectPageList(BBpmInstanceVo param);

    /**
     * 审批流程实例完成事件
     */
    void saveProcessInstanceCompleted(ProcessInstance processInstance);

    /**
     * 审批流程实例完成事件
     */
    void processStarted(ProcessInstance processInstance);

    /**
     * 审批流任务拒绝
     */
    void saveTaskCancelled(TaskEntity task);

    /**
     * 流程实例取消
     */
    void saveProcessInstanceCancelled(String taskId) throws Exception;

    /**
     * 审批流任务转交
     */
    void saveTaskTransfer(TaskEntity task) throws Exception;
}
