package com.xinyirun.scm.ai.workflow.node.documentextractor;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.core.service.DocumentParsingService;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataFilesContent;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流文档解析节点
 *
 * 功能：
 * - 解析文档文件内容（PDF、TXT、Office文档）
 * - 提取文档中的纯文本内容
 * - 支持批量文档解析
 *
 * @author zxh
 * @since 2025-10-27
 */
@Slf4j
public class DocumentExtractorNode extends AbstractWfNode {

    public DocumentExtractorNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    /**
     * 节点处理逻辑
     *
     * 处理流程：
     * 1. 从inputs中提取文件信息（URL和文件名）
     * 2. 使用DocumentParsingService解析每个文件
     * 3. 合并所有文件的文本内容
     * 4. 输出合并后的文本
     *
     * @return 解析后的文档内容
     */
    @Override
    public NodeProcessResult onProcess() {
        StringBuilder documentText = new StringBuilder();

        // 1. 从inputs中提取文件URL列表
        List<NodeIOData> inputList = state.getInputs();
        List<String> fileUrls = new ArrayList<>();

        for (NodeIOData nodeIOData : inputList) {
            // 检查是否为文件类型的输入
            if (nodeIOData.getContent() instanceof NodeIODataFilesContent filesContent) {
                // scm-ai的NodeIODataFilesContent.getValue()返回List<String> fileUrls
                fileUrls.addAll(filesContent.getValue());
            }
        }

        // 2. 获取DocumentParsingService
        DocumentParsingService documentParsingService = SpringUtil.getBean(DocumentParsingService.class);

        // 3. 解析文档
        try {
            for (String fileUrl : fileUrls) {
                // 从URL中提取文件名
                String fileName = extractFileNameFromUrl(fileUrl);
                log.info("开始解析文档: {}, URL: {}", fileName, fileUrl);

                // 使用DocumentParsingService解析文档
                String content = documentParsingService.parseDocumentFromUrl(fileUrl, fileName);

                if (content == null || content.isEmpty()) {
                    log.warn("{}的文件类型无法解析，忽略", fileName);
                    continue;
                }

                documentText.append(content);
            }
        } catch (Exception e) {
            log.error("解析文档失败", e);
        }

        // 4. 构建输出
        NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", documentText.toString());
        List<NodeIOData> result = List.of(output);

        return NodeProcessResult.builder().content(result).build();
    }

    /**
     * 从文件URL中提取文件名
     *
     * @param fileUrl 文件URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return "unknown.txt";
        }

        // 从URL中提取文件名（最后一个'/'后的内容）
        int lastSlashIndex = fileUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastSlashIndex + 1);
        }

        return "unknown.txt";
    }

}
