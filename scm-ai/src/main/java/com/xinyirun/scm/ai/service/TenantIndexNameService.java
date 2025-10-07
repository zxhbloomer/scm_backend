package com.xinyirun.scm.ai.service;

import org.springframework.stereotype.Service;

/**
 * 租户索引名称生成服务
 *
 * <p>用于 Elasticsearch 动态索引名称生成（多租户隔离）</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Service("tenantIndexNameService")
public class TenantIndexNameService {

    /**
     * 获取知识库嵌入向量索引名称
     *
     * <p>格式：tenant_{tenant_id}_kb_embeddings</p>
     *
     * @return 索引名称
     */
    public String getKbEmbeddingsIndexName() {
        // 从当前线程上下文获取租户ID（由拦截器设置）
        Long tenantId = getCurrentTenantId();
        if (tenantId == null) {
            return "kb_embeddings"; // 默认索引（开发环境）
        }
        return "tenant_" + tenantId + "_kb_embeddings";
    }

    /**
     * 获取知识库嵌入向量索引名称（指定租户ID）
     *
     * @param tenantId 租户ID
     * @return 索引名称
     */
    public String getKbEmbeddingsIndexName(Long tenantId) {
        if (tenantId == null) {
            return "kb_embeddings";
        }
        return "tenant_" + tenantId + "_kb_embeddings";
    }

    /**
     * 从当前线程上下文获取租户ID
     *
     * <p>实际实现应从 ThreadLocal 或 Spring Security Context 获取</p>
     *
     * @return 租户ID
     */
    private Long getCurrentTenantId() {
        // TODO: 从 DynamicDataSourceContextHolder 或 SecurityContextHolder 获取租户ID
        // 示例实现：
        // return DynamicDataSourceContextHolder.getTenantId();
        return null; // 临时返回null，实际应从上下文获取
    }
}
