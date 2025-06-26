package com.xinyirun.scm.core.bpm.service.business.notice;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.notice.BNoticeEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

/**
 * <p>
 * 通知表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
public interface IBpmBNoticeService extends IService<BNoticeEntity> {

    /**
     * 当前节点任务完成通知：
     * 通知发起下一个审批节点人员发起通知
     * 内容：
     * 【审批任务通知】
     * *标题*：2025年市场活动预算申请
     * *申请人*：张三（市场部）
     * *当前进度*：已通过部门经理审批 → 待您审批（财务审核）
     * *说明*：请核对费用明细，审批截止时间：2025-02-23 18:00
     * *操作*：点击查看详情 [审批链接]
     *
     */
    void sendBpmTodoNotice(TaskEntity task);

    /**
     * 审批流通过通知
     * @param processInstance
     */
    void sendBpmPassNotice(ProcessInstance processInstance);

    /**
     * 审批流撤销\拒绝消息通知
     * @param processInstance
     */
    void sendBpTerminateNotice(ProcessInstance processInstance );

    /**
     * 新增
     * @param param
     * @return
     */
    InsertResultAo<BNoticeVo> insert(BNoticeVo param);

    /**
     * 查询详情
     * @param id
     * @return
     */
    BNoticeVo selectById(Integer id);

}
