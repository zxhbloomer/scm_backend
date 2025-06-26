package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmCcEntity;

import com.xinyirun.scm.bean.entity.bpm.BpmInstanceApproveEntity;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceProcessEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmCcVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.bpm.mapper.business.BpmCcMapper;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceApproveMapper;
import com.xinyirun.scm.core.bpm.mapper.business.BpmInstanceProcessMapper;
import com.xinyirun.scm.core.bpm.service.business.IBpmCcService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 抄送 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Service
public class BpmCcServiceImpl extends ServiceImpl<BpmCcMapper, BpmCcEntity> implements IBpmCcService {


    @Autowired
    private BpmCcMapper bpmCcMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private BpmInstanceApproveMapper bpmInstanceApproveMapper;

    @Autowired
    private BpmInstanceProcessMapper bpmInstanceProcessMapper;

    /**
     * 查看抄送我的
     *
     * @param param
     */
    @Override
    public IPage<BBpmCcVo> selectPageList(BBpmCcVo param) {

        // 分页条件
        Page<BBpmCcVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());

        param.setUser_code(SecurityUtil.getUserSession().getStaff_info().getCode());

        return  bpmCcMapper.selectPages(pageCondition, param);
    }


    /**
     * 更新节点抄送信息 更新抄送表信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCc(DelegateExecution execution) {

        // 查询审批流节点下抄送的信息
        List<BpmInstanceApproveEntity> bpmInstanceApproveEntities = bpmInstanceApproveMapper.selByProcessInstanceIdAndNodeIdAndAssigneeCode(execution.getCurrentActivityId(), execution.getProcessInstanceId(), DictConstant.DICT_SYS_CODE_BPM_INSTANCE_APPROVE_TWO);

        List<BpmCcEntity> bpmCcEntities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(bpmInstanceApproveEntities)) {
            for (BpmInstanceApproveEntity bpmInstanceApproveEntity : bpmInstanceApproveEntities) {
                BpmCcEntity cc = new BpmCcEntity();
                cc.setId(IdWorker.getIdStr());
                cc.setUser_code(bpmInstanceApproveEntity.getAssignee_code());
                cc.setProcess_instance_id(execution.getProcessInstanceId());
                cc.setNode_id(execution.getCurrentActivityId());
                cc.setNode_name(execution.getCurrentActivityName());
                bpmCcEntities.add(cc);

                // 更新对应的抄送人完成信息
                bpmInstanceApproveEntity.setApprove_time(LocalDateTime.now());
                bpmInstanceApproveEntity.setStatus(DictConstant.B_BPM_TODO_STATUS_ONE);
                bpmInstanceApproveEntity.setApprove_type(DictConstant.DICT_SYS_CODE_BPM_APPROVE_ONE);
                bpmInstanceApproveEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_AGREE);
                bpmInstanceApproveMapper.updateById(bpmInstanceApproveEntity);
            }

            // 更新节点完成信息
            BpmInstanceProcessEntity bpmInstanceProcessEntity = bpmInstanceProcessMapper.selectBpmInstance(execution.getCurrentActivityId(), bpmInstanceApproveEntities.get(0).getProcess_code());
            bpmInstanceProcessEntity.setFinish_time(LocalDateTime.now());
            bpmInstanceProcessEntity.setStart_time(LocalDateTime.now());
            bpmInstanceProcessEntity.setResult(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_RESULT_COMPLETE);
            bpmInstanceProcessMapper.updateById(bpmInstanceProcessEntity);
        }

        // 更新节点下的操作信息
        if (CollectionUtils.isNotEmpty(bpmCcEntities)) {
            bpmCcEntities.stream().forEach(k -> {
                bpmCcMapper.insert(k);
            });
        }
    }


    private String getCurrentName(String processInstanceId, Boolean flag, String processDefinitionId) {
        if (flag) {
            return "流程已结束";
        }
        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId).activityType("userTask").unfinished().orderByActivityInstanceStartTime().desc().list();
        if (CollUtil.isEmpty(list)) {
            return "";
        } else {
            String activityId = list.get(0).getActivityId();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(activityId);
            return flowElement.getName();
        }
    }
}
