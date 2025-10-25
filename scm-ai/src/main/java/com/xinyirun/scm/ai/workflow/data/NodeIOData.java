package com.xinyirun.scm.ai.workflow.data;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 工作流节点输入输出数据
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Builder
@Data
public class NodeIOData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected String name;

    protected NodeIODataContent<?> content;

    public String valueToString() {
        return content.getValue().toString();
    }

    public static NodeIOData createByText(String name, String title, String value) {
        NodeIODataTextContent dataContent = new NodeIODataTextContent();
        dataContent.setValue(value);
        dataContent.setTitle(title);
        return NodeIOData.builder().name(name).content(dataContent).build();
    }

    public static NodeIOData createByNumber(String name, String title, Double value) {
        NodeIODataNumberContent dataContent = new NodeIODataNumberContent();
        dataContent.setValue(value);
        dataContent.setTitle(title);
        return NodeIOData.builder().name(name).content(dataContent).build();
    }

    public static NodeIOData createByBool(String name, String title, Boolean value) {
        NodeIODataBoolContent dataContent = new NodeIODataBoolContent();
        dataContent.setValue(value);
        dataContent.setTitle(title);
        return NodeIOData.builder().name(name).content(dataContent).build();
    }

    public static NodeIOData createByFiles(String name, String title, List<String> value) {
        NodeIODataFilesContent dataContent = new NodeIODataFilesContent();
        dataContent.setValue(value);
        dataContent.setTitle(title);
        return NodeIOData.builder().name(name).content(dataContent).build();
    }

    public static NodeIOData createByOptions(String name, String title, Map<String, Object> value) {
        NodeIODataOptionsContent dataContent = new NodeIODataOptionsContent();
        dataContent.setValue(value);
        dataContent.setTitle(title);
        return NodeIOData.builder().name(name).content(dataContent).build();
    }
}
