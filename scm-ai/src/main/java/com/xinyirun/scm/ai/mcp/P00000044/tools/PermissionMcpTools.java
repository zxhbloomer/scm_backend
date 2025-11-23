package com.xinyirun.scm.ai.mcp.P00000044.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.ai.mcp.P00000044.service.PermissionAiService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 权限管理MCP工具集
 *
 * 提供自然语言访问权限管理功能的MCP工具，对应页面代码 P00000044
 *
 * 主要功能包括：
 * 1. 页面访问权限查询 - 根据页面名称查询用户可访问的页面编码
 * 2. 按钮权限查询 - 根据页面编码查询用户的按钮操作权限
 *
 * 设计原则：
 * - 所有工具都是只读操作，不进行数据修改
 * - tenantCode和staffId通过ToolContext自动注入，LLM无需感知
 * - 支持LLM自动链式调用：先查页面编码 → 再查按钮权限
 *
 * @author zzxxhh
 * @since 2025-11-21
 */
@Slf4j
@Component
public class PermissionMcpTools {

    @Autowired
    private PermissionAiService permissionAiService;

    /**
     * 检查用户的页面访问权限
     *
     * 根据页面名称模糊查询用户可访问的页面列表。
     * 如果匹配到多个页面，返回列表供用户确认。
     *
     * 使用场景：
     * - "我能访问入库单页面吗？"
     * - "查询用户的采购订单权限"
     * - "用户能看到哪些销售相关页面？"
     *
     * @param tenantCode 租户编码（框架自动注入）
     * @param staffId 员工ID（框架自动注入）
     * @param pageName 页面名称，支持模糊匹配，如"入库"、"采购订单"
     * @return JSON格式的页面访问权限结果
     */
    @McpTool(description = """
       获取用户在指定页面的按钮权限列表，需要传入页面编码(page_code)。
       返回用户可执行的按钮操作列表，包括perms(权限标识)和descr(权限描述)。
       严格遵守要求：
       - 不可以臆想、推测
       - 不可以过度回复、不可以过度推测
       - 如果你对任何方面不确定，或者无法取得必要信息，请说"我没有足够的信息来自信地评估这一点"
       - 如果找不到相关引用，请说明"未找到相关引用"。
       - 找不到相关回答，请说明"未找到相关回答"。
       - 在回答找不到的情况时，不要过多拓展回复和过度回复
       """)
    public String checkPageAccess(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "员工ID") Long staffId,
            @McpToolParam(description = "页面名称，支持模糊匹配，如'入库'、'采购订单'、'销售'等") String pageName) {

        log.info("MCP工具调用 - 检查页面访问权限: 租户={}, 员工ID={}, 页面名称={}",
                tenantCode, staffId, pageName);

        try {
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = permissionAiService.checkPageAccess(staffId, pageName);

            result.put("tenantCode", tenantCode);
            result.put("staffId", staffId);
            result.put("toolName", "check_page_access");
            result.put("searchName", pageName);

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 检查页面访问权限: 租户={}, 员工ID={}, 错误={}",
                    tenantCode, staffId, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "检查页面访问权限失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "staffId", staffId,
                    "toolName", "check_page_access",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 获取用户在指定页面的按钮权限
     *
     * 根据页面编码查询用户的按钮操作权限列表。
     * 通常在checkPageAccess返回页面编码后调用。
     *
     * 使用场景：
     * - "用户在P00000011页面有哪些按钮权限？"
     * - "查询入库单页面的操作权限"
     * - "用户能在这个页面做什么操作？"
     *
     * @param tenantCode 租户编码（框架自动注入）
     * @param staffId 员工ID（框架自动注入）
     * @param pageCode 页面编码，如"P00000011"、"P00000001"
     * @return JSON格式的按钮权限列表
     */
    @McpTool(description = "获取用户在指定页面的按钮权限列表，需要传入页面编码(page_code)。返回用户可执行的按钮操作列表，包括perms(权限标识)和descr(权限描述)")
    public String getPageButtonPermissions(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "员工ID") Long staffId,
            @McpToolParam(description = "页面编码，如'P00000011'、'P00000001'等，可从checkPageAccess结果中获取") String pageCode) {

        log.info("MCP工具调用 - 获取按钮权限: 租户={}, 员工ID={}, 页面编码={}",
                tenantCode, staffId, pageCode);

        try {
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = permissionAiService.getPageButtonPermissions(staffId, pageCode);

            result.put("tenantCode", tenantCode);
            result.put("staffId", staffId);
            result.put("toolName", "get_page_button_permissions");

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 获取按钮权限: 租户={}, 页面编码={}, 错误={}",
                    tenantCode, pageCode, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "获取按钮权限失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "staffId", staffId,
                    "pageCode", pageCode,
                    "toolName", "get_page_button_permissions",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }
}
