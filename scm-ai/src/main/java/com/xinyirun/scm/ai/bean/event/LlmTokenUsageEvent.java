package com.xinyirun.scm.ai.bean.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * LLM Token使用事件
 * 用于异步处理Token统计和配额管理
 *
 * @author Claude AI Assistant
 * @createTime 2025-09-25
 */
@Getter
@Setter
public class LlmTokenUsageEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -3902968364021987317L;

    /**
     * 对话ID
     */
    private String conversationId;

    /**
     * 模型源ID
     */
    private String modelSourceId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 租户ID
     */
    private String tenant;

    /**
     * AI提供商名称
     */
    private String aiProvider;

    /**
     * AI模型类型
     */
    private String aiModelType;

    /**
     * 输入token数量
     */
    private Long promptTokens;

    /**
     * 输出token数量
     */
    private Long completionTokens;

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 响应时间（毫秒）
     */
    private Long responseTime;

    /**
     * Token单价（美元/1K tokens）
     */
    private BigDecimal tokenUnitPrice;

    /**
     * 费用（美元）
     */
    private BigDecimal cost;

    /**
     * 创建时间戳
     */
    private Long createTime;

    public LlmTokenUsageEvent(Object source) {
        super(source);
        this.createTime = System.currentTimeMillis();
        this.success = true;
        this.responseTime = 0L;
    }

    /**
     * 构造函数
     *
     * @param source 事件源
     * @param conversationId 对话ID
     * @param modelSourceId 模型源ID
     * @param userId 用户ID
     * @param tenant 租户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入token数
     * @param completionTokens 输出token数
     * @param success 是否成功
     * @param responseTime 响应时间
     * @param tokenUnitPrice Token单价
     * @param cost 费用
     */
    public LlmTokenUsageEvent(Object source, String conversationId, String modelSourceId,
                             String userId, String tenant, String aiProvider, String aiModelType,
                             Long promptTokens, Long completionTokens, Boolean success,
                             Long responseTime, BigDecimal tokenUnitPrice, BigDecimal cost) {
        super(source);
        this.conversationId = conversationId;
        this.modelSourceId = modelSourceId;
        this.userId = userId;
        this.tenant = tenant;
        this.aiProvider = aiProvider;
        this.aiModelType = aiModelType;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.success = success;
        this.responseTime = responseTime;
        this.tokenUnitPrice = tokenUnitPrice;
        this.cost = cost;
        this.createTime = System.currentTimeMillis();
    }

    /**
     * 简化构造函数
     */
    public LlmTokenUsageEvent(Object source, String conversationId, String userId, String tenant,
                             String aiProvider, String aiModelType, Long promptTokens, Long completionTokens) {
        super(source);
        this.conversationId = conversationId;
        this.userId = userId;
        this.tenant = tenant;
        this.aiProvider = aiProvider;
        this.aiModelType = aiModelType;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.success = true;
        this.responseTime = 0L;
        this.createTime = System.currentTimeMillis();
    }

    /**
     * 计算总Token数
     */
    public Long getTotalTokens() {
        if (promptTokens == null || completionTokens == null) {
            return 0L;
        }
        return promptTokens + completionTokens;
    }

    /**
     * 是否成功 - 为了兼容isSuccess()调用
     */
    public Boolean isSuccess() {
        return this.success;
    }

    /**
     * 获取总费用 - 为了兼容getTotalCost()调用
     */
    public BigDecimal getTotalCost() {
        return this.cost;
    }

    @Override
    public String toString() {
        return "LlmTokenUsageEvent{" +
                "conversationId='" + conversationId + '\'' +
                ", userId='" + userId + '\'' +
                ", tenant='" + tenant + '\'' +
                ", aiProvider='" + aiProvider + '\'' +
                ", aiModelType='" + aiModelType + '\'' +
                ", promptTokens=" + promptTokens +
                ", completionTokens=" + completionTokens +
                ", totalTokens=" + getTotalTokens() +
                ", success=" + success +
                ", cost=" + cost +
                '}';
    }
}