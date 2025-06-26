//package com.xinyirun.scm.core.bpm.service.business;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.xinyirun.scm.bean.bpm.dto.HandleDataDTO;
//import com.xinyirun.scm.bean.bpm.vo.HandleDataVO;
//import com.xinyirun.scm.bean.bpm.vo.HistoryProcessInstanceVO;
//import com.xinyirun.scm.bean.bpm.vo.TaskVO;
//import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
//import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmGroupVo;
//import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
//
//import java.util.List;
//
///**
// * <p>
// * process_templates 服务类
// * </p>
// *
// * @author xinyirun
// * @since 2024-10-11
// */
//public interface IBpmProcessWorkService extends IService<BpmProcessTemplatesEntity> {
//
//    IPage<BBpmProcessVo> selectPage(BBpmProcessVo param);
//
//    /**
//     * 获取详情
//     */
//    BBpmProcessVo selectById(Integer id);
//
//    /**
//     * 获取模板分组
//     */
//    List<BBpmGroupVo> getGroup();
//
//
//    /**
//     * 查看我的代办
//     */
//    IPage<TaskVO> toDoList(BBpmProcessVo param);
//
//    /**
//     * 查看我发起的流程
//     */
//    IPage<HistoryProcessInstanceVO> applyList(BBpmProcessVo param);
//
//    /**
//     * 同意
//     */
//    void agree(HandleDataDTO handleDataDTO);
//
//    /**
//     * 通过id实例查看详情
//     */
//    HandleDataVO instanceInfo(HandleDataDTO handleDataDTO);
//
//    /**
//     * 我的已办
//     */
//    IPage<TaskVO> doneList(BBpmProcessVo param);
//
//    /**
//     * 我的抄送
//     */
//    IPage<TaskVO> ccList(BBpmProcessVo param);
//}
