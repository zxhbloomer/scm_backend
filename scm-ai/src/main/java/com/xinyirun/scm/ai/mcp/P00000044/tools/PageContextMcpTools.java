package com.xinyirun.scm.ai.mcp.P00000044.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面上下文MCP工具
 *
 * 提供获取用户当前所在页面上下文信息的能力
 * 用于AI Chat回答"我现在打开的页面是什么？"等问题
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
@Slf4j
@Component
public class PageContextMcpTools {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取用户当前所在页面的上下文信息
     *
     * pageContext由框架从ToolContext自动注入，包含前端传递的page_code、title、path
     * 当用户询问"我现在在哪个页面"、"当前页面是什么"等问题时，LLM会调用此工具
     *
     * @param pageContext 页面上下文（框架自动注入，包含page_code、title、path）
     * @return JSON格式的页面上下文信息
     */
    @McpTool(description = """
        获取用户当前正在访问的页面信息。
        当用户询问'我现在在哪个页面'、'当前页面是什么'、'这是什么页面'等问题时调用此工具。
        返回：
        1、用户当前所在页面的名称、路径和页面编码。
        2、咨询用户是否需要查询知识库方面的咨询？
        """)
    public String getCurrentPageInfo(
            @McpToolParam(description = "页面上下文（框架自动注入）") Map<String, Object> pageContext) {

        Map<String, Object> result = new HashMap<>();

        try {
            log.info("MCP工具调用 - 获取当前页面上下文: pageContext={}", pageContext);

            if (pageContext == null || pageContext.isEmpty()) {
                // 无页面上下文信息
                result.put("success", false);
                result.put("message", "无法获取当前页面信息");
                result.put("_aiHint", "前端未传递页面上下文信息，可能是用户未在具体业务页面，或页面信息传递异常。请友好告知用户当前无法确定所在页面。");
            } else {
                // 成功获取页面上下文
                result.put("success", true);
                result.put("pageContext", pageContext);

                String title = (String) pageContext.getOrDefault("title", "未知页面");
                String path = (String) pageContext.getOrDefault("path", "");
                String pageCode = (String) pageContext.getOrDefault("page_code", "");

                result.put("message", String.format("用户当前在「%s」页面", title));
                result.put("_aiHint", String.format(
                    """
                    可以把页面信息告诉用户，不能臆想，过度说明，产生不必要的幻觉。
                    用户正在访问的页面信息：页面名称=%s，路径=%s，页面编码=%s。
                    咨询用户是否想了解该页面的功能或操作说明，表示可以查询知识库问答。
                    """
                    ,title, path, pageCode
                ));
            }

            return objectMapper.writeValueAsString(result);

        } catch (Exception e) {
            log.error("获取页面上下文失败", e);
            result.put("success", false);
            result.put("message", "获取页面信息失败: " + e.getMessage());
            result.put("_aiHint", "系统错误，请稍后重试");

            try {
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException jsonEx) {
                return "{\"success\":false,\"message\":\"JSON序列化失败\"}";
            }
        }
    }
}
