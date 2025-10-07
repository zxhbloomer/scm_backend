package com.xinyirun.scm.ai.service.splitter;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 支持重叠的文本分割器
 *
 * <p>功能说明：</p>
 * Spring AI的TokenTextSplitter不支持overlap参数，此类扩展实现了overlap功能
 * 严格对应aideepin的DocumentSplitters.recursive(maxSegmentSizeInTokens, overlap, tokenEstimator)
 *
 * <p>核心逻辑：</p>
 * <ol>
 *   <li>按token数量分割文本（maxSegmentSizeInTokens，如300 tokens）</li>
 *   <li>相邻分段之间保留overlap（maxOverlapSizeInTokens，如50 tokens）</li>
 *   <li>使用滑动窗口实现：start = end - overlap</li>
 * </ol>
 *
 * <p>参考实现：</p>
 * LangChain4j: DocumentSplitters.recursive(300, 50, tokenEstimator)
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\rag\GraphRAG.java#54
 *
 * <p>使用示例：</p>
 * <pre>
 * OverlappingTokenTextSplitter splitter = new OverlappingTokenTextSplitter(300, 50);
 * Document document = new Document("长文本内容...");
 * List&lt;Document&gt; segments = splitter.apply(Collections.singletonList(document));
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
public class OverlappingTokenTextSplitter extends TokenTextSplitter {

    /**
     * 最大段落大小（tokens数量）
     * 对应aideepin的RAG_MAX_SEGMENT_SIZE_IN_TOKENS = 300
     */
    private final int maxSegmentSizeInTokens;

    /**
     * 最大重叠大小（tokens数量）
     * 对应aideepin的overlap参数，默认值为kb.getIngestMaxOverlap()或50
     */
    private final int maxOverlapSizeInTokens;

    /**
     * 每个token约4个字符（英文单词平均长度）
     * 用于将token数量转换为字符数量的估算
     */
    private static final int APPROX_CHARS_PER_TOKEN = 4;

    /**
     * 构造函数
     *
     * @param maxSegmentSizeInTokens 最大段落大小（tokens），对应aideepin的300
     * @param maxOverlapSizeInTokens 最大重叠大小（tokens），对应aideepin的overlap参数（默认50）
     */
    public OverlappingTokenTextSplitter(int maxSegmentSizeInTokens, int maxOverlapSizeInTokens) {
        // 调用父类构造函数
        super();

        this.maxSegmentSizeInTokens = maxSegmentSizeInTokens;
        this.maxOverlapSizeInTokens = maxOverlapSizeInTokens;
    }

    /**
     * 应用分割逻辑
     *
     * <p>aideepin的分割逻辑：</p>
     * <pre>
     * DocumentSplitter documentSplitter = DocumentSplitters.recursive(
     *     RAG_MAX_SEGMENT_SIZE_IN_TOKENS,  // 300
     *     graphIngestParams.getOverlap(),  // 50
     *     TokenEstimatorFactory.create(graphIngestParams.getTokenEstimator())
     * );
     * </pre>
     *
     * <p>scm-ai的实现：</p>
     * 使用滑动窗口算法，实现token级别的overlap
     *
     * @param documents 输入文档列表
     * @return 分割后的文档列表
     */
    @Override
    public List<Document> apply(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }

        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            String text = doc.getText();
            if (text == null || text.isEmpty()) {
                continue;
            }

            // 使用滑动窗口分割文本
            List<String> segments = splitWithOverlap(text);

            // 将分割后的文本段转换为Document对象
            for (String segment : segments) {
                // 保留原始文档的metadata
                Document segmentDoc = new Document(segment, doc.getMetadata());
                result.add(segmentDoc);
            }
        }

        return result;
    }

    /**
     * 使用滑动窗口实现overlap分割
     *
     * <p>算法说明：</p>
     * <pre>
     * 假设文本长度1000字符，maxSegmentSize=300, overlap=50
     * 第1段：[0, 300)     -> "文本内容..."
     * 第2段：[250, 550)   -> overlap 50字符 = (300-50=250起始)
     * 第3段：[500, 800)   -> overlap 50字符
     * 第4段：[750, 1000)  -> 最后一段
     * </pre>
     *
     * <p>对应aideepin的逻辑：</p>
     * LangChain4j的RecursiveCharacterTextSplitter会自动处理overlap
     * scm-ai使用Spring AI需要手动实现
     *
     * @param text 待分割的文本
     * @return 分割后的文本段列表
     */
    private List<String> splitWithOverlap(String text) {
        List<String> segments = new ArrayList<>();

        // 将token数量转换为字符数量（粗略估算）
        int maxChars = maxSegmentSizeInTokens * APPROX_CHARS_PER_TOKEN;
        int overlapChars = maxOverlapSizeInTokens * APPROX_CHARS_PER_TOKEN;

        // 如果文本长度小于等于maxChars，直接返回整个文本
        if (text.length() <= maxChars) {
            segments.add(text);
            return segments;
        }

        int start = 0;
        while (start < text.length()) {
            // 计算当前段落的结束位置
            int end = Math.min(start + maxChars, text.length());

            // 优化：尝试在单词边界处分割（避免切断单词）
            if (end < text.length()) {
                // 向后查找空格或标点符号
                int lastSpace = text.lastIndexOf(' ', end);
                int lastPunct = Math.max(
                        text.lastIndexOf('。', end),
                        Math.max(
                                text.lastIndexOf('，', end),
                                Math.max(
                                        text.lastIndexOf('！', end),
                                        text.lastIndexOf('？', end)
                                )
                        )
                );
                int boundary = Math.max(lastSpace, lastPunct);

                // 如果找到合适的边界且不会导致段落过小，使用边界位置
                if (boundary > start + (maxChars / 2)) {
                    end = boundary + 1; // +1 包含标点符号
                }
            }

            // 提取当前段落
            String segment = text.substring(start, end).trim();
            if (!segment.isEmpty()) {
                segments.add(segment);
            }

            // 计算下一个段落的起始位置（实现overlap）
            start = end - overlapChars;

            // 防止无限循环：如果overlap导致start没有前进，强制前进
            if (start >= end) {
                start = end;
            }

            // 如果已经到达文本末尾，结束循环
            if (start >= text.length()) {
                break;
            }
        }

        return segments;
    }

    /**
     * 获取配置信息（用于日志和调试）
     *
     * @return 配置描述
     */
    @Override
    public String toString() {
        return String.format(
                "OverlappingTokenTextSplitter[maxSegmentSize=%d tokens, overlap=%d tokens]",
                maxSegmentSizeInTokens,
                maxOverlapSizeInTokens
        );
    }
}
