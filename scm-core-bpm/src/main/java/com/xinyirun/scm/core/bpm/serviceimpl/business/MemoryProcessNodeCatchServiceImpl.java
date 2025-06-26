package com.xinyirun.scm.core.bpm.serviceimpl.business;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessNodeVo;
import com.xinyirun.scm.core.bpm.service.business.ProcessNodeCatchService;
import com.xinyirun.scm.core.bpm.utils.WFlowToBpmnCreator;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存实现流程节点缓存服务
 *
 * @author : willian fu
 * @date : 2022/8/24
 */
@Slf4j
@Service
public class MemoryProcessNodeCatchServiceImpl implements ProcessNodeCatchService {

    //缓存流程ID -> (节点ID -> 节点) 快速取数据
    public static final Map<String, Map<String, BpmProcessNodeVo<?>>> processModelNodeMap = new ConcurrentHashMap<>();


    @Override
    public BpmProcessNodeVo<?> getProcessNodeById(String code, String nodeId) {
        Map<String, BpmProcessNodeVo<?>> nodeMap = processModelNodeMap.get(code);
        if (null != nodeMap) {
            return nodeMap.get(nodeId);
        }
        return null;
    }

    @Override
    public <T> BpmProcessNodeVo<T> getProcessNodeById(Class<T> clazz, String code, String nodeId) {
        BpmProcessNodeVo<?> node = getProcessNodeById(code, nodeId);
        if (null != node) {
            return (BpmProcessNodeVo<T>) node;
        }
        return null;
    }

    @Override
    public List<BpmProcessNodeVo<?>> getTaskNodesByCode(String code) {
        try {
            return processModelNodeMap.get(code).values().stream().filter(n ->
                    NodeTypeEnum.ROOT.equals(n.getType())
                            || NodeTypeEnum.APPROVAL.equals(n.getType())
                            || NodeTypeEnum.CC.equals(n.getType())).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public BpmProcessNodeVo<?> findSubNodeByIdFromRoot(@NotNull String nodeId, BpmProcessNodeVo<?> node) {
        //TODO 此处应当利用LRU缓存实现高效取，日后再修改
        if (null != node && null != node.getId()){
            if (nodeId.equals(node.getId())){
                WFlowToBpmnCreator.coverProps(node);
                return node;
            }else if (NodeTypeEnum.CONCURRENTS.equals(node.getType()) || NodeTypeEnum.CONDITIONS.equals(node.getType())) {
                for (BpmProcessNodeVo<?> branch : node.getBranchs()) {
                    BpmProcessNodeVo<?> subNode = findSubNodeByIdFromRoot(nodeId, branch);
                    if (ObjectUtil.isNotNull(subNode)) {
                        WFlowToBpmnCreator.coverProps(node);
                        return subNode;
                    }
                }
            }
            return findSubNodeByIdFromRoot(nodeId, node.getChildren());
        }
        return null;
    }

//    @Override
//    public BpmProcessNodeVo<?> findSubNodeByIdFromRoot(String nodeId, String code) {
//        return findSubNodeByIdFromRoot(nodeId,
//                JSONObject.parseObject(modelsMapper.selectById(code)
//                        .getProcess(), BpmProcessNodeVo.class));
//    }
//
//    @Override
//    public BpmProcessNodeVo<?> findSubNodeByDefIdFromRoot(String nodeId, String defId) {
//        return findSubNodeByIdFromRoot(nodeId,
//                JSONObject.parseObject(modelsMapper.selectOne(new LambdaQueryWrapper<WflowModels>()
//                        .eq(WflowModels::getProcessDefId, defId)).getProcess(), BpmProcessNodeVo.class));
//    }

    @Override
    public void setProcessNodes(String code, Map<String, BpmProcessNodeVo<?>> nodeMap) {
        processModelNodeMap.put(code, nodeMap);
    }

//    @Override
//    public Map<String, BpmProcessNodeVo<?>> reloadProcessByCode(String code) {
//        Map<String, BpmProcessNodeVo<?>> nodeMap = new LinkedHashMap<>();
//        loadProcess(JSONObject.parseObject(modelsMapper.selectById(code).getProcess(), BpmProcessNodeVo.class), nodeMap);
//        processModelNodeMap.put(code, nodeMap);
//        return nodeMap;
//    }

    @Override
    public Map<String, BpmProcessNodeVo<?>> reloadProcessByStr(String process) {
        Map<String, BpmProcessNodeVo<?>> nodeMap = new LinkedHashMap<>();
        loadProcess(JSONObject.parseObject(process, BpmProcessNodeVo.class), nodeMap);
        return nodeMap;
    }

    @Override
    public void unloadProcessByCode(String code) {
        processModelNodeMap.remove(code);
    }

    private void loadProcess(BpmProcessNodeVo<?> node, Map<String, BpmProcessNodeVo<?>> nodeMap) {
        if (null != node && null != node.getId()) {
            WFlowToBpmnCreator.coverProps(node);
            nodeMap.put(node.getId(), node);
            if (NodeTypeEnum.CONCURRENTS.equals(node.getType()) || NodeTypeEnum.CONDITIONS.equals(node.getType())) {
                node.getBranchs().forEach(n -> loadProcess(n, nodeMap));
            }
            loadProcess(node.getChildren(), nodeMap);
        }
    }
}
