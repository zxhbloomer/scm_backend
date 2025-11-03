package com.xinyirun.scm.ai.workflow.data;

import lombok.Data;

/**
 * 工作流节点输入输出数据内容抽象类
 *
 * @author zxh
 * @since 2025-10-21
 */
@Data
public abstract class NodeIODataContent<T> {

    private String title;

    private Integer type;

    private T value;
}
