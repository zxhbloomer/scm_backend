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
 * 3. 菜单路径查询 - 根据页面编码查询用户可访问的菜单路径
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
            // ❌ 删除: result.put("toolName", "check_page_access"); - toolName由框架管理
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
            // ❌ 删除: result.put("toolName", "get_page_button_permissions"); - toolName由框架管理

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

    /**
     * 获取用户可访问的页面菜单路径
     *
     * 根据页面编码查询用户有权访问的所有菜单路径。
     * 同一个页面可能在不同菜单位置下有多个路径。
     *
     * 使用场景：
     * - "P00000013页面在哪个菜单下？"
     * - "如何访问出库计划页面？"
     * - "这个页面的菜单路径是什么？"
     *
     * @param tenantCode 租户编码（框架自动注入）
     * @param staffId 员工ID（框架自动注入）
     * @param pageCode 页面编码，如"P00000013"、"P00000001"
     * @return JSON格式的菜单路径列表
     */
    @McpTool(description = """
       获取用户可访问的页面菜单路径，需要传入页面编码(page_code)。
       返回用户可访问的所有菜单路径，包括page_code、name、meta_title、path。
       同一个页面可能在不同菜单位置有多个路径，会返回所有可用路径。
       严格遵守要求：
       - 不可以臆想、推测
       - 不可以过度回复、不可以过度推测
       - 如果你对任何方面不确定，或者无法取得必要信息，请说"我没有足够的信息来自信地评估这一点"
       - 如果找不到相关引用，请说明"未找到相关引用"
       - 找不到相关回答，请说明"未找到相关回答"
       - 在回答找不到的情况时，不要过多拓展回复和过度回复
       """)
    public String getPageMenuPaths(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "员工ID") Long staffId,
            @McpToolParam(description = "页面编码，如'P00000013'、'P00000001'等，可从checkPageAccess结果中获取") String pageCode) {

        log.info("MCP工具调用 - 获取页面菜单路径: 租户={}, 员工ID={}, 页面编码={}",
                tenantCode, staffId, pageCode);

        try {
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = permissionAiService.getPageMenuPaths(staffId, pageCode);

            result.put("tenantCode", tenantCode);
            result.put("staffId", staffId);
            // ❌ 删除: result.put("toolName", "get_page_menu_paths"); - toolName由框架管理

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 获取页面菜单路径: 租户={}, 页面编码={}, 错误={}",
                    tenantCode, pageCode, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "获取页面菜单路径失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "staffId", staffId,
                    "pageCode", pageCode,
                    "toolName", "get_page_menu_paths",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 打开指定URL的页面
     *
     * 根据提供的页面路径,生成前端可识别的页面跳转指令。
     * 返回JSON格式的响应,包含action和url信息。
     *
     * 使用场景：
     * - "帮我打开入库单页面"
     * - "跳转到采购订单列表"
     * - "显示库存报表"
     *
     * @param tenantCode 租户编码（框架自动注入）
     * @param staffId 员工ID（框架自动注入）
     * @param pagePath 页面路径,如"/20_master/goods"、"/10_system/user"
     * @return JSON格式的页面跳转指令
     */
    @McpTool(description = """
       P00000044
       生成打开指定页面的跳转指令,需要传入页面路径(page_path)。
       返回包含action='openPage'和url的JSON指令,供前端执行页面跳转。
       通常在查询到页面菜单路径后调用,将path传入此工具。
       严格遵守要求：
       - 不可以臆想、推测页面路径
       - 页面路径必须从getPageMenuPaths工具的返回结果中获取
       - 如果未查询到有效的页面路径,请说明"未找到可访问的页面路径"
       - 不可以过度回复、不可以过度推测
       - 找到数据，返回包含action='openPage'和url的JSON指令,供前端执行页面跳转。
       """)
    public String openPage(
            @McpToolParam(description = "租户编码") String tenantCode,
            @McpToolParam(description = "员工ID") Long staffId,
            @McpToolParam(description = "页面路径,如'/20_master/goods'、'/10_system/user',必须从getPageMenuPaths结果中获取") String pagePath) {

        log.info("MCP工具调用 - 打开页面: 租户={}, 员工ID={}, 页面路径={}",
                tenantCode, staffId, pagePath);

        try {
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = permissionAiService.generateOpenPageInstruction(staffId, pagePath);

            result.put("tenantCode", tenantCode);
            result.put("staffId", staffId);
            // ❌ 删除: result.put("toolName", "open_page"); - toolName由框架管理

            String jsonResult = JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);
            log.info("【DEBUG-openPage】MCP工具返回: {}", jsonResult);

            return jsonResult;

        } catch (Exception e) {
            log.error("MCP工具异常 - 打开页面: 租户={}, 页面路径={}, 错误={}",
                    tenantCode, pagePath, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                    "success", false,
                    "message", "打开页面失败: " + e.getMessage(),
                    "tenantCode", tenantCode,
                    "staffId", staffId,
                    "pagePath", pagePath,
                    "toolName", "open_page",
                    "error", e.getClass().getSimpleName()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }
}
