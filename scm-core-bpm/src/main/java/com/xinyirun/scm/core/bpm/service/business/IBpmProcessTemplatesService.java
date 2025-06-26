package com.xinyirun.scm.core.bpm.service.business;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppBBpmProcessJson;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmGroupVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * process_templates 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
public interface IBpmProcessTemplatesService extends IService<BpmProcessTemplatesEntity> {

    IPage<BBpmProcessVo> selectPage(BBpmProcessVo param);

    /**
     * 获取详情
     */
    BBpmProcessVo selectById(Integer id);

    /**
     * 获取模板分组
     */
    List<BBpmGroupVo> getGroup();



    /**
     * 获取审批流程数据
     */
    BBpmProcessVo getBpmFlow(BBpmProcessVo param);

    /**
     * 获取审批流程数据
     * @param type
     * @return
     */
    String getBpmFLowCodeByType(String type);

    /**
     * 获取审批流程模型数据
     * @param param
     * @return
     */
    AppBBpmProcessJson getAppProcessModel(BBpmProcessVo param);


//    /**
//     * 通过流程定义id启动流程
//     */
//    void createStartProcess(BBpmProcessVo param);

    /**
     * 通过流程定义id启动流程
     */
    void startProcess(BBpmProcessVo param);

    /**
     * 校验审批流程表单数据
     */
    JSONObject checkFormItem(String bpmProcessBOutPlan, Map mapVo);

    /**
     * 模板发布
     */
    UpdateResultAo<BBpmProcessVo> deployBom(BBpmProcessVo param);

    /**
     * 获取节点审批人，被flowable uel表达式自动调用
     * @param execution 执行实例
     * @return 该任务的审批人
     */
    List<String> getNodeApprovalUsers(ExecutionEntity execution);    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    AppStaffUserBpmInfoVo getBpmDataByStaffid(AppStaffUserBpmInfoVo vo) ;

    /**
     * 根据页面代码获取BPM数据
     * @param param
     * @return
     */
    List<BBpmProcessVo> getBpmDataByPageCode(BBpmProcessVo param);
}
