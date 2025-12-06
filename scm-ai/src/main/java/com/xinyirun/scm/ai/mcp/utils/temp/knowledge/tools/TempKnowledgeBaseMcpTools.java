package com.xinyirun.scm.ai.mcp.utils.temp.knowledge.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.ai.mcp.utils.temp.knowledge.service.TempKnowledgeBaseAiService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 临时知识库MCP工具
 *
 * 提供自然语言访问临时知识库创建功能的MCP工具
 *
 * 主要功能：
 * 1. 创建临时知识库并同步完成向量索引
 *
 * 使用场景：
 * - 合同审批workflow：在流程开始时创建临时知识库
 * - 后续workflow节点可立即使用该知识库进行RAG检索
 *
 * 设计原则：
 * - 所有业务逻辑封装在TempKnowledgeBaseAiService中
 * - MCP工具层只负责参数接收、租户切换、JSON返回
 * - tenantCode和staffId通过ToolContext自动注入，LLM无需感知
 * - 返回JSON格式的结构化数据，便于AI理解和用户查看
 *
 * @author zzxxhh
 * @since 2025-12-03
 */
@Slf4j
@Component
public class TempKnowledgeBaseMcpTools {

    @Autowired
    private TempKnowledgeBaseAiService tempKnowledgeBaseAiService;

    /**
     * 创建临时知识库并同步执行向量索引
     *
     * 执行流程：
     * 1. 创建临时知识库记录（is_temp=1, expire_time=now+2h）
     * 2. 如果有text：创建文本类型item
     * 3. 如果有fileUrls：为每个文件创建item
     * 4. 同步执行向量索引（等待完成）
     * 5. 返回kbUuid供workflow使用
     *
     * 使用场景：
     * - "为这份合同创建临时知识库"
     * - "上传这些文件并创建临时知识库"
     * - "基于这段文本创建临时知识库"
     *
     * @param tenantCode 租户编码（框架自动注入）
     * @param staffId 员工ID（框架自动注入）
     * @param text 文本内容（可选）
     * @param fileUrls 文件URL数组（可选）
     * @param brief 简介（用于填充知识项的title和brief字段）
     * @return JSON格式的创建结果
     */
    @McpTool(description = """
        创建临时知识库并同步完成向量索引，供workflow后续节点立即使用。
        临时知识库2小时后自动清理。仅做向量索引，不做图谱索引。
        text和fileUrls参数是"或"的关系，只需提供其中一个即可。纯文本输入时fileUrls传空数组。
        """)
    public String createTempKnowledgeBase(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "员工ID") Long staffId,
            @McpToolParam(description = "文本内容，与fileUrls二选一即可", required = false) String text,
            @McpToolParam(description = "文件URL数组，与text二选一即可", required = false) List<String> fileUrls,
            @McpToolParam(description = "简介（用于填充知识项title和brief字段）", required = false) String brief) {

        log.info("MCP工具调用 - 创建临时知识库: 租户={}, 员工ID={}, 文本长度={}, 文件数量={}, 简介={}",
                tenantCode, staffId, (text != null ? text.length() : 0),
                (fileUrls != null ? fileUrls.size() : 0), brief);

        try {
            // 切换租户数据源
            DataSourceHelper.use(tenantCode);

            // 调用AI服务层执行业务逻辑
            // 重要：传入staffId，避免在异步线程中依赖SecurityUtil
            Map<String, Object> result = tempKnowledgeBaseAiService.createTempKnowledgeBase(text, fileUrls, brief, staffId);

            // 添加上下文信息到返回结果
            result.put("tenantCode", tenantCode);
            result.put("staffId", staffId);

            // 转换为JSON返回
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 创建临时知识库失败: 租户={}, 错误={}",
                    tenantCode, e.getMessage(), e);

            Map<String, Object> errorResult = Map.of(
                    "success", false,
                    "message", "创建临时知识库失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "staffId", staffId,
                    "error", e.getClass().getSimpleName()
            );

            return JSON.toJSONString(errorResult, JSONWriter.Feature.PrettyFormat);

        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }
}
