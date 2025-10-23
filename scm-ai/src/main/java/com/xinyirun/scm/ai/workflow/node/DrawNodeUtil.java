package com.xinyirun.scm.ai.workflow.node;

import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataFilesContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流绘图节点工具类
 *
 * 提供图像生成和处理的通用方法，用于所有涉及图像生成的节点
 */
@Slf4j
public class DrawNodeUtil {

    /**
     * 创建图像节点的处理结果
     *
     * 将图像URL组装成NodeIOData的文件内容格式，作为节点的输出结果
     *
     * @param imageUrls 图像URL列表
     * @return 节点处理结果
     */
    public static NodeProcessResult createResultContent(List<String> imageUrls) {
        String imageUrl = "";
        if (CollectionUtils.isNotEmpty(imageUrls)) {
            imageUrl = imageUrls.get(0);
        }

        NodeIODataFilesContent datContent = new NodeIODataFilesContent();
        datContent.setValue(List.of(imageUrl));
        datContent.setTitle("");

        List<NodeIOData> result = List.of(
            NodeIOData.builder()
                .name(DEFAULT_OUTPUT_PARAM_NAME)
                .content(datContent)
                .build()
        );

        return NodeProcessResult.builder().content(result).build();
    }
}
