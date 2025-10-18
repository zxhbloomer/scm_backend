package com.xinyirun.scm.ai.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 图谱索引完成事件
 * 当文档的图谱化索引完成（成功或失败）时触发此事件
 *
 * @author SCM System
 */
@Getter
public class GraphIndexCompletedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 75950521641085295L;

    /**
     * 知识库UUID
     */
    private final String kb_uuid;

    /**
     * 文档UUID
     */
    private final String kb_item_uuid;

    /**
     * 是否成功
     */
    private final boolean success;

    /**
     * 错误信息（失败时）
     */
    private final String error_message;

    /**
     * 提取的实体数量
     */
    private final Integer entity_count;

    /**
     * 提取的关系数量
     */
    private final Integer relation_count;

    /**
     * 租户代码
     */
    private final String tenant_code;

    /**
     * 构造函数
     *
     * @param source 事件源对象
     * @param kb_uuid 知识库UUID
     * @param kb_item_uuid 文档UUID
     * @param success 是否成功
     * @param error_message 错误信息
     * @param entity_count 实体数量
     * @param relation_count 关系数量
     * @param tenant_code 租户代码
     */
    public GraphIndexCompletedEvent(Object source, String kb_uuid, String kb_item_uuid,
                                    boolean success, String error_message,
                                    Integer entity_count, Integer relation_count,
                                    String tenant_code) {
        super(source);
        this.kb_uuid = kb_uuid;
        this.kb_item_uuid = kb_item_uuid;
        this.success = success;
        this.error_message = error_message;
        this.entity_count = entity_count;
        this.relation_count = relation_count;
        this.tenant_code = tenant_code;
    }
}
