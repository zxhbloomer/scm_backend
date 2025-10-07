package com.xinyirun.scm.ai.service.rag;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefGraphEntity;
import com.xinyirun.scm.ai.bean.vo.rag.RefGraphVo;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseQaRefGraphMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 知识库问答-图谱引用服务类
 *
 * <p>对应 aideepin 服务：KnowledgeBaseQaRefGraphService</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeBaseQaRefGraphService extends ServiceImpl<AiKnowledgeBaseQaRefGraphMapper, AiKnowledgeBaseQaRefGraphEntity> {

    private final AiKnowledgeBaseQaService qaService;

    /**
     * 根据问答记录UUID查询图谱引用
     *
     * <p>对应 aideepin 方法：getByQaUuid</p>
     *
     * @param qaRecordId 问答记录ID
     * @return 图谱引用VO
     */
    public RefGraphVo getByQaUuid(String qaRecordId) {
        // 查询该问答记录的图谱引用
        LambdaQueryWrapper<AiKnowledgeBaseQaRefGraphEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiKnowledgeBaseQaRefGraphEntity::getQaRecordId, qaRecordId);
        List<AiKnowledgeBaseQaRefGraphEntity> list = this.list(wrapper);

        if (list.isEmpty()) {
            return RefGraphVo.builder()
                    .vertices(Collections.emptyList())
                    .edges(Collections.emptyList())
                    .entitiesFromQuestion(Collections.emptyList())
                    .build();
        }

        AiKnowledgeBaseQaRefGraphEntity refGraph = list.get(0);

        // 解析图谱数据（JSON格式）
        RefGraphVo result = new RefGraphVo();
        String graphStr = refGraph.getGraphFromStore();
        if (StringUtils.isNotBlank(graphStr)) {
            try {
                result = JSON.parseObject(graphStr, RefGraphVo.class);
            } catch (Exception e) {
                log.error("解析图谱数据失败, qaRecordId: {}, graphStr: {}", qaRecordId, graphStr, e);
            }
        }

        // 解析从问题中提取的实体（逗号分隔）
        String entitiesStr = refGraph.getEntitiesFromQuestion();
        if (StringUtils.isNotBlank(entitiesStr)) {
            List<String> entities = Arrays.stream(entitiesStr.split(","))
                    .filter(StringUtils::isNotBlank)
                    .toList();
            result.setEntitiesFromQuestion(entities);
        } else {
            result.setEntitiesFromQuestion(Collections.emptyList());
        }

        // 确保vertices和edges不为null
        if (result.getVertices() == null) {
            result.setVertices(Collections.emptyList());
        }
        if (result.getEdges() == null) {
            result.setEdges(Collections.emptyList());
        }

        return result;
    }

    /**
     * 保存图谱引用记录
     *
     * <p>对应 aideepin 方法：KnowledgeBaseQaService.createGraphRefs()</p>
     *
     * <p>aideepin实现：</p>
     * <pre>
     * public void createGraphRefs(User user, Long qaRecordId, RefGraphDto graphDto) {
     *     String entities = String.join(",", graphDto.getEntitiesFromQuestion());
     *     Map<String, Object> graphFromStore = new HashMap<>();
     *     graphFromStore.put("vertices", graphDto.getVertices());
     *     graphFromStore.put("edges", graphDto.getEdges());
     *     KnowledgeBaseQaRefGraph refGraph = new KnowledgeBaseQaRefGraph();
     *     refGraph.setQaRecordId(qaRecordId);
     *     refGraph.setUserId(user.getId());
     *     refGraph.setEntitiesFromQuestion(entities);
     *     refGraph.setGraphFromStore(JsonUtil.toJson(graphFromStore));
     *     knowledgeBaseQaRecordRefGraphService.save(refGraph);
     * }
     * </pre>
     *
     * @param qaUuid 问答记录UUID
     * @param graphRef 图谱引用VO（包含entities, vertices, edges）
     * @param userId 用户ID
     */
    public void saveRefGraphs(String qaUuid, RefGraphVo graphRef, Long userId) {
        if (graphRef == null) {
            log.warn("graphRef为空，跳过保存图谱引用记录，qaUuid: {}", qaUuid);
            return;
        }

        // 查询qa_record_id
        AiKnowledgeBaseQaEntity qa = qaService.getByQaUuid(qaUuid);
        if (qa == null) {
            log.error("问答记录不存在，无法保存图谱引用，qaUuid: {}", qaUuid);
            return;
        }

        // 将entitiesFromQuestion列表转为逗号分隔字符串（对应aideepin的String.join(",", ...)）
        String entitiesStr = "";
        if (graphRef.getEntitiesFromQuestion() != null && !graphRef.getEntitiesFromQuestion().isEmpty()) {
            entitiesStr = String.join(",", graphRef.getEntitiesFromQuestion());
        }

        // 构建graphFromStore JSON（对应aideepin的Map<String, Object>）
        Map<String, Object> graphFromStore = new HashMap<>();
        graphFromStore.put("vertices", graphRef.getVertices() != null ? graphRef.getVertices() : Collections.emptyList());
        graphFromStore.put("edges", graphRef.getEdges() != null ? graphRef.getEdges() : Collections.emptyList());

        // 构建实体
        AiKnowledgeBaseQaRefGraphEntity entity = new AiKnowledgeBaseQaRefGraphEntity();
        entity.setQaRecordId(qa.getId());
        entity.setUserId(userId);
        entity.setEntitiesFromQuestion(entitiesStr);
        entity.setGraphFromStore(JSON.toJSONString(graphFromStore));

        // 保存（对应aideepin的save()）
        this.save(entity);

        log.info("保存图谱引用完成，qaUuid: {}, vertices数量: {}, edges数量: {}",
                qaUuid, graphRef.getVertices().size(), graphRef.getEdges().size());
    }
}
