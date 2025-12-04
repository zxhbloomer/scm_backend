package com.xinyirun.scm.bean.system.vo.business.ai;

import lombok.Data;

import java.io.Serializable;

/**
 * 临时知识库清理任务参数
 * 用于Quartz任务调度时传递参数（JSON序列化/反序列化）
 */
@Data
public class TempKbCleanupParamVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 临时知识库UUID
     */
    private String kbUuid;

    /**
     * 租户代码
     */
    private String tenantCode;
}
