package com.xinyirun.scm.ai.workflow;

import org.apache.commons.collections4.map.PassiveExpiringMap;

/**
 * 已中断正在等待用户输入的流程
 *
 * <p>注意：当前使用内存存储，分布式部署时需改用Redis存储</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
public class InterruptedFlow {

    /**
     * 30分钟超时(KISS优化: 从10分钟延长到30分钟)
     * 原因: 用户可能需要更长时间思考(查资料/接电话等)
     */
    private static final PassiveExpiringMap.ExpirationPolicy<String, WorkflowEngine> EXPIRATION_POLICY =
            new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<>(60 * 1000 * 30);

    /**
     * runtime_uuid -> WorkflowEngine映射
     */
    public static final PassiveExpiringMap<String, WorkflowEngine> RUNTIME_TO_GRAPH =
            new PassiveExpiringMap<>(EXPIRATION_POLICY);
}
