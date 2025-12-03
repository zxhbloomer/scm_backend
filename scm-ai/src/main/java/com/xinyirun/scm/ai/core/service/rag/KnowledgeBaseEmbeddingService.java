package com.xinyirun.scm.ai.core.service.rag;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.vo.rag.KbEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.KbItemEmbeddingVo;
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final MilvusVectorRetrievalService milvusVectorRetrievalService;

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

        // 从Milvus查询
        List<KbEmbeddingVo> embeddingList = milvusVectorRetrievalService.listEmbeddings(kbItemUuid, currentPage, pageSize);

        // 转换为MyBatis Plus Page对象
        Page<KbItemEmbeddingVo> result = new Page<>(currentPage, pageSize);
        // Milvus不支持精确总数统计,使用当前返回数量
        result.setTotal(embeddingList.size());

        // 转换为VO对象
        List<KbItemEmbeddingVo> records = new ArrayList<>();
        embeddingList.forEach(doc -> {
            KbItemEmbeddingVo vo = KbItemEmbeddingVo.builder()
                    .embeddingId(doc.getId())
                    .embedding(null)  // Milvus VectorStore不返回原始向量
                    .text(doc.getContent())
                    .build();
            records.add(vo);
        });
        result.setRecords(records);

        log.info("查询完成: 当前页记录数={}", result.getRecords().size());
        return result;
    }
}
