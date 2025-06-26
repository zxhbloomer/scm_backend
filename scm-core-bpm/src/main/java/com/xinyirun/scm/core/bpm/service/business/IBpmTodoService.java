package com.xinyirun.scm.core.bpm.service.business;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmTodoEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmInstanceProgressVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmTodoVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-30
 */
public interface IBpmTodoService extends IService<BpmTodoEntity> {

    /**
     * 查看我的待办
     */
    IPage<BBpmTodoVo> selectPageList(BBpmTodoVo param);

    /**
     * 查看我的待办
     */
    List<BBpmTodoVo> getListTen(BBpmTodoVo param);

//    /**
//     * 查看详情
//     */
//    HandleDataVO instanceInfo(BBpmTodoVo param);

    /**
     * 同意
     */
    void agree(BBpmTodoVo param);

    /**
     * 流程审批拒绝
     */
    void refuse(BBpmTodoVo param);

    /**
     * 更新任务评论
     */
    void updateComments(BBpmTodoVo param);

    /**
     * 通过流程实例id查看详情
     */
    BBpmInstanceProgressVo getInstanceProgress(BBpmInstanceProgressVo param);

    /**
     * 通过流程实例id查看详情
     */
    BBpmInstanceProgressVo getInstanceProgressapp(BBpmInstanceProgressVo param);

    /**
     * 流程撤销
     */
    void cancel(BBpmTodoVo param) throws Exception;

    /**
     * 任务审批转办
     */
    void transfer(BBpmTodoVo param);

    /**
     * 任务审批后加签
     */
    void afterAdd(BBpmTodoVo param);

    /**
     * 审批流待办数量条数
     */
    Integer selectTodoCount(BBpmTodoVo param);
}
