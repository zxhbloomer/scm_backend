package com.xinyirun.scm.ai.workflow.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * 工作流节点输入输出数据内容抽象类
 *
 * @author zxh
 * @since 2025-10-21
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NodeIODataTextContent.class, name = "1"),
    @JsonSubTypes.Type(value = NodeIODataNumberContent.class, name = "2"),
    @JsonSubTypes.Type(value = NodeIODataOptionsContent.class, name = "3"),
    @JsonSubTypes.Type(value = NodeIODataFilesContent.class, name = "4"),
    @JsonSubTypes.Type(value = NodeIODataBoolContent.class, name = "5")
})
public abstract class NodeIODataContent<T> {

    private String title;

    private Integer type;

    private T value;
}
