package com.xinyirun.scm.ai.core.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

/**
 * 向量索引完成事件
 * 当文档的向量化索引完成（成功或失败）时触发此事件
 *
 * @author SCM System
 */
@Getter
public class VectorIndexCompletedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -9058724695206475777L;

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
     * 索引的分段数量
     */
    private final Integer segment_count;

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
     * @param segment_count 分段数量
     * @param tenant_code 租户代码
     */
    public VectorIndexCompletedEvent(Object source, String kb_uuid, String kb_item_uuid,
                                     boolean success, String error_message,
                                     Integer segment_count, String tenant_code) {
        super(source);
        this.kb_uuid = kb_uuid;
        this.kb_item_uuid = kb_item_uuid;
        this.success = success;
        this.error_message = error_message;
        this.segment_count = segment_count;
        this.tenant_code = tenant_code;
    }
}
