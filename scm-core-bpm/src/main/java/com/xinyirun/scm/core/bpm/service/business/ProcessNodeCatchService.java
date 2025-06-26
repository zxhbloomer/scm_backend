package com.xinyirun.scm.core.bpm.service.business;

import com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessNodeVo;

import java.util.List;
import java.util.Map;

/**
 * 流程节点缓存，快速获取某个流程的某节点设置项
 * @author : willian fu
 * @date : 2022/8/24
 */
public interface ProcessNodeCatchService {

    BpmProcessNodeVo<?> getProcessNodeById(String code, String nodeId);

    <T> BpmProcessNodeVo<T> getProcessNodeById(Class<T> clazz, String code, String nodeId);

    List<BpmProcessNodeVo<?>> getTaskNodesByCode(String code);

    BpmProcessNodeVo<?> findSubNodeByIdFromRoot(String nodeId, BpmProcessNodeVo<?> root);

//    BpmProcessNodeVo<?> findSubNodeByIdFromRoot(String nodeId, String code);
//
//    BpmProcessNodeVo<?> findSubNodeByDefIdFromRoot(String nodeId, String defId);

    void setProcessNodes(String code, Map<String, BpmProcessNodeVo<?>> nodeMap);

//    Map<String, BpmProcessNodeVo<?>> reloadProcessByCode(String code);

    Map<String, BpmProcessNodeVo<?>> reloadProcessByStr(String process);

    void unloadProcessByCode(String code);
}
