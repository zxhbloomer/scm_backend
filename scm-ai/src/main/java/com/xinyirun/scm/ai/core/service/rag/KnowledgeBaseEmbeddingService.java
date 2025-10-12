package com.xinyirun.scm.ai.core.service.rag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import com.xinyirun.scm.ai.bean.vo.rag.KbItemEmbeddingVo;
import com.xinyirun.scm.ai.core.repository.elasticsearch.AiKnowledgeBaseEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库嵌入向量服务类
 *
 * @author SCM AI Team
 * @since 2025-10-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseEmbeddingService {

    private final AiKnowledgeBaseEmbeddingRepository embeddingRepository;

    /**
     * 根据知识项UUID分页查询嵌入向量列表
     *
     * @param kbItemUuid 知识项UUID
     * @param currentPage 当前页码（从1开始）
     * @param pageSize 每页大小
     * @return 嵌入向量分页数据
     */
    public Page<KbItemEmbeddingVo> listByItemUuid(String kbItemUuid, int currentPage, int pageSize) {
        log.info("查询知识项嵌入向量列表: kbItemUuid={}, page={}, size={}", kbItemUuid, currentPage, pageSize);

        // 创建分页参数（Spring Data从0开始，MyBatis Plus从1开始）
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        // 从Elasticsearch查询
        org.springframework.data.domain.Page<AiKnowledgeBaseEmbeddingDoc> esPage =
            embeddingRepository.findByKbItemUuidOrderBySegmentIndexAsc(kbItemUuid, pageable);

        // 转换为MyBatis Plus Page对象
        Page<KbItemEmbeddingVo> result = new Page<>(currentPage, pageSize);
        result.setTotal(esPage.getTotalElements());

        // 转换为VO对象
        List<KbItemEmbeddingVo> records = new ArrayList<>();
        esPage.getContent().forEach(doc -> {
            KbItemEmbeddingVo vo = KbItemEmbeddingVo.builder()
                    .embeddingId(doc.getSegmentUuid())
                    .embedding(doc.getEmbedding())
                    .text(doc.getSegmentText())
                    .build();
            records.add(vo);
        });
        result.setRecords(records);

        log.info("查询完成: 总数={}, 当前页记录数={}", result.getTotal(), result.getRecords().size());
        return result;
    }
}
