package com.xinyirun.scm.core.bpm.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.xinyirun.scm.bean.bpm.dto.json.ChildNode;
import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import com.xinyirun.scm.bean.entity.bpm.BpmCcEntity;
import com.xinyirun.scm.core.bpm.service.business.IBpmCcService;
import com.xinyirun.scm.core.bpm.utils.BeanUtil;
import jakarta.annotation.Resource;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.common.bpm.WorkFlowConstants.*;
import static com.xinyirun.scm.core.bpm.utils.BpmnModelUtils.getChildNode;


/**
 * @author LoveMyOrange
 * @create 2022-10-15 19:47
 */
public class CcListener implements JavaDelegate{

    private IBpmCcService ccService;

    public CcListener() {
        ccService = BeanUtil.getBean(IBpmCcService.class);
    }

    @Override
    public void execute(DelegateExecution execution) {
        ccService.insertCc(execution);
    }
}
