package com.xinyirun.scm.ai.config.mcp;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;

/**
 * MCP工具配置类
 *
 * 功能:
 * 1. 扫描所有带@McpTool注解的方法
 * 2. 将它们注册为Spring AI的ToolCallback
 * 3. 创建ToolCallbackProvider Bean供workflowDomainChatClient使用
 *
 * 工作原理:
 * - AiChatMemoryConfig中的workflowDomainChatClient会自动注入这个ToolCallbackProvider
 * - LLM通过Function Calling机制,自动选择和调用合适的工具
 * - 工具执行失败时,错误消息会发送给LLM,LLM生成友好的解释
 *
 * @author zzxxhh
 * @since 2025-11-19
 */
@Slf4j
@Configuration
public class McpToolConfig {

    /**
     * 创建MCP工具回调提供者
     *
     * 该Bean会被AiChatMemoryConfig.workflowDomainChatClient自动注入
     * 启用LLM的Function Calling能力,实现智能工具选择和调用
     *
     * @param context Spring应用上下文,用于扫描所有Bean
     * @return ToolCallbackProvider实例,包含所有@McpTool方法的ToolCallback
     */
    @Bean
    public ToolCallbackProvider mcpToolCallbackProvider(ApplicationContext context) {
        log.info("开始初始化MCP工具回调提供者,扫描所有@McpTool注解的方法");

        Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);
        List<ToolCallback> toolCallbacks = new ArrayList<>();

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            Class<?> beanClass = bean.getClass();

            for (Method method : beanClass.getMethods()) {
                if (method.isAnnotationPresent(McpTool.class)) {
                    try {
                        ToolCallback callback = McpToolCallbackAdapter.createToolCallback(bean, method);
                        toolCallbacks.add(callback);

                        String toolName = beanClass.getSimpleName() + "." + method.getName();
                        log.info("注册MCP工具: {} - {}", toolName, method.getAnnotation(McpTool.class).description());

                    } catch (Exception e) {
                        log.error("注册MCP工具失败: {}.{}", beanClass.getSimpleName(), method.getName(), e);
                    }
                }
            }
        }

        log.info("MCP工具回调提供者初始化完成,共注册 {} 个工具", toolCallbacks.size());

        return ToolCallbackProvider.from(toolCallbacks);
    }
}

/**
 * MCP工具到Spring AI ToolCallback的适配器
 *
 * 职责:
 * 1. 将@McpTool方法包装为Spring AI的ToolCallback
 * 2. 从@McpToolParam注解构建JSON Schema(自动排除tenantCode参数)
 * 3. 使用Map<String,Object>作为输入类型,支持动态参数
 * 4. 提取并转换参数,调用实际的Java方法
 * 5. 自动注入tenantCode参数,无需LLM提供
 */
class McpToolCallbackAdapter {

    /**
     * 创建ToolCallback
     * 此处是大模型，llm 回调 mcp的入口
     *
     * @param bean 包含@McpTool方法的Bean实例
     * @param method @McpTool注解的方法
     * @return ToolCallback实例
     */
    static ToolCallback createToolCallback(Object bean, Method method) {
        McpTool mcpTool = method.getAnnotation(McpTool.class);

        String toolName = bean.getClass().getSimpleName() + "." + method.getName();
        String description = mcpTool.description();
        // 构建JSON Schema,排除tenantCode参数(LLM看不到它)
        String inputSchema = buildJsonSchemaWithoutTenantCode(method);

        // 处理工具调用:支持ToolContext传递tenantCode和staffId,并记录MCP调用留痕
        // 使用BiFunction<Map, ToolContext, String>支持从ToolContext获取租户编码和用户ID
        BiFunction<Map<String, Object>, ToolContext, String> toolFunction =
            (inputMap, toolContext) -> {
                // 1. 记录MCP工具调用开始时间
                String callStartTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                boolean callSuccess = false;
                String errorMessage = null;
                Object result = null;  // ✅ 在try块之前声明,供finally块使用

                try {
                    // 2. 原有逻辑: 提取 tenantCode 和 staffId
                    // 优先从ToolContext获取tenantCode(通过toolContext()方法传递)
                    String tenantCode = null;
                    if (toolContext != null && toolContext.getContext().containsKey("tenantCode")) {
                        tenantCode = (String) toolContext.getContext().get("tenantCode");
                    }

                    // 如果ToolContext没有,从DataSourceHelper获取(兜底方案)
                    if (tenantCode == null) {
                        tenantCode = DataSourceHelper.getCurrentDataSourceName();
                    }

                    // 注入tenantCode到inputMap,传递给MCP工具方法
                    if (tenantCode != null) {
                        inputMap.put("tenantCode", tenantCode);
                    }

                    // 从ToolContext获取staffId并注入(用于权限查询等需要用户身份的场景)
                    if (toolContext != null && toolContext.getContext().containsKey("staffId")) {
                        Object staffId = toolContext.getContext().get("staffId");
                        if (staffId != null) {
                            inputMap.put("staffId", staffId);
                        }
                    }

                    // 从ToolContext获取pageContext并注入(用于获取用户当前页面信息的场景)
                    if (toolContext != null && toolContext.getContext().containsKey("pageContext")) {
                        Object pageContext = toolContext.getContext().get("pageContext");
                        if (pageContext != null) {
                            inputMap.put("pageContext", pageContext);
                        }
                    }

                    // 3. 执行MCP工具
                    Object[] args = extractParameters(inputMap, method);
                    result = method.invoke(bean, args);  // ✅ 去掉Object声明,使用外层变量
                    callSuccess = true;
                    return result != null ? result.toString() : "执行成功";
                } catch (Exception e) {
                    callSuccess = false;
                    errorMessage = e.getMessage();
                    throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
                } finally {
                    // 4. 记录MCP工具调用到 nodeState (无论成功或失败都记录)
                    if (toolContext != null && toolContext.getContext().containsKey("nodeState")) {
                        WfNodeState nodeState = (WfNodeState) toolContext.getContext().get("nodeState");

                        // 创建MCP调用记录
                        Map<String, Object> mcpCallRecord = new HashMap<>();
                        mcpCallRecord.put("toolName", toolName);
                        mcpCallRecord.put("description", description);
                        mcpCallRecord.put("callTime", callStartTime);
                        mcpCallRecord.put("success", callSuccess);
                        mcpCallRecord.put("error", errorMessage);

                        // ✅ 关键修复: 添加工具的实际返回值
                        // 如果工具返回的是JSON字符串,解析为Map对象;否则保持原样
                        if (result != null && callSuccess) {
                            try {
                                String resultStr = result.toString();
                                // 尝试解析JSON字符串为Map
                                if (resultStr.startsWith("{") && resultStr.endsWith("}")) {
                                    Map<String, Object> resultMap = JSON.parseObject(resultStr, Map.class);
                                    // 将解析后的Map合并到mcpCallRecord根级别
                                    mcpCallRecord.putAll(resultMap);
                                } else {
                                    // 非JSON格式,直接存储字符串
                                    mcpCallRecord.put("resultValue", resultStr);
                                }
                            } catch (Exception e) {
                                // JSON解析失败,保存原始字符串
                                mcpCallRecord.put("resultValue", result.toString());
                            }
                        }

                        // 使用 createByOptions 创建 NodeIOData
                        NodeIOData mcpCallData = NodeIOData.createByOptions(
                            "mcp_tool_call_" + System.currentTimeMillis(),  // 唯一key
                            "MCP工具调用",  // title
                            mcpCallRecord   // value
                        );

                        nodeState.getOutputs().add(mcpCallData);
                    }
                }
            };

        return FunctionToolCallback.builder(toolName, toolFunction)
                .description(description)
                .inputType(Map.class)
                .inputSchema(inputSchema)
                .build();
    }

    /**
     * 构建JSON Schema,自动排除tenantCode、staffId和pageContext参数
     *
     * 这些参数会被自动从ToolContext注入,无需LLM提供,因此从Schema中移除
     *
     * @param method @McpTool方法
     * @return JSON Schema字符串
     */
    private static String buildJsonSchemaWithoutTenantCode(Method method) {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        List<String> required = new ArrayList<>();

        for (Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(McpToolParam.class)) {
                // 跳过tenantCode、staffId和pageContext参数,不加入Schema(由框架自动注入)
                String paramName = param.getName();
                if ("tenantCode".equals(paramName) || "staffId".equals(paramName) || "pageContext".equals(paramName)) {
                    continue;
                }

                McpToolParam mcpParam = param.getAnnotation(McpToolParam.class);

                JSONObject propertySchema = new JSONObject();
                String jsonType = getJsonType(param.getType());
                propertySchema.put("type", jsonType);
                propertySchema.put("description", mcpParam.description());

                // 如果是数组类型，需要添加items定义
                if ("array".equals(jsonType)) {
                    JSONObject itemsSchema = new JSONObject();
                    itemsSchema.put("type", "string");  // 默认数组元素为string类型
                    propertySchema.put("items", itemsSchema);
                }

                properties.put(param.getName(), propertySchema);

                if (mcpParam.required()) {
                    required.add(param.getName());
                }
            }
        }

        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema.toJSONString();
    }

    /**
     * 从Map中提取并转换为Java方法参数
     *
     * @param inputMap LLM传递的参数Map
     * @param method 目标方法
     * @return 方法参数数组
     */
    private static Object[] extractParameters(Map<String, Object> inputMap, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = param.getName();
            Class<?> paramType = param.getType();

            Object value = inputMap.get(paramName);
            args[i] = convertType(value, paramType);
        }

        return args;
    }

    /**
     * 将Java类型映射为JSON Schema类型
     *
     * @param javaType Java类型
     * @return JSON Schema类型字符串
     */
    private static String getJsonType(Class<?> javaType) {
        if (javaType == String.class) {
            return "string";
        } else if (javaType == Integer.class || javaType == int.class ||
                   javaType == Long.class || javaType == long.class) {
            return "integer";
        } else if (javaType == Double.class || javaType == double.class ||
                   javaType == Float.class || javaType == float.class) {
            return "number";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return "boolean";
        } else if (List.class.isAssignableFrom(javaType)) {
            // List类型映射为JSON Schema的array类型
            return "array";
        } else {
            return "string";
        }
    }

    /**
     * 类型转换
     *
     * 将Map中的值转换为Java方法参数类型
     *
     * @param value Map中的值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private static Object convertType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
        }

        // 处理List类型：LLM返回的数组会是List或Collection类型
        if (List.class.isAssignableFrom(targetType)) {
            if (value instanceof List) {
                return value;
            } else if (value instanceof String) {
                // 如果是JSON字符串，尝试解析
                String strValue = (String) value;
                if (strValue.startsWith("[") && strValue.endsWith("]")) {
                    return JSON.parseArray(strValue, String.class);
                }
                // 单个字符串转为List
                return List.of(strValue);
            }
            return new ArrayList<>();
        }

        String strValue = value.toString();

        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(strValue);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(strValue);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(strValue);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(strValue);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(strValue);
        } else {
            return strValue;
        }
    }
}
