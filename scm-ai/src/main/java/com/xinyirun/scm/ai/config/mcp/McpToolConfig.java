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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

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

        // 处理工具调用:支持ToolContext传递tenantCode
        // 使用BiFunction<Map, ToolContext, String>支持从ToolContext获取租户编码
        BiFunction<Map<String, Object>, ToolContext, String> toolFunction =
            (inputMap, toolContext) -> {
                try {
                    // 优先从ToolContext获取tenantCode(通过toolContext()方法传递)
                    String tenantCode = null;
                    if (toolContext != null && toolContext.getContext().containsKey("tenantCode")) {
                        tenantCode = (String) toolContext.getContext().get("tenantCode");
                    }

                    // 如果ToolContext没有,从DataSourceHelper获取(兜底方案)
                    if (tenantCode == null) {
                        tenantCode = DataSourceHelper.getCurrentDataSourceName();
                    }

                    // 注入到inputMap,传递给MCP工具方法
                    if (tenantCode != null) {
                        inputMap.put("tenantCode", tenantCode);
                    }

                    Object[] args = extractParameters(inputMap, method);
                    Object result = method.invoke(bean, args);
                    return result != null ? result.toString() : "执行成功";
                } catch (Exception e) {
                    throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
                }
            };

        return FunctionToolCallback.builder(toolName, toolFunction)
                .description(description)
                .inputType(Map.class)
                .inputSchema(inputSchema)
                .build();
    }

    /**
     * 构建JSON Schema,自动排除tenantCode参数
     *
     * tenantCode会被自动注入,无需LLM提供,因此从Schema中移除
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
                // 跳过tenantCode参数,不加入Schema
                if ("tenantCode".equals(param.getName())) {
                    continue;
                }

                McpToolParam mcpParam = param.getAnnotation(McpToolParam.class);

                JSONObject propertySchema = new JSONObject();
                propertySchema.put("type", getJsonType(param.getType()));
                propertySchema.put("description", mcpParam.description());

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
    private static Object convertType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return value;
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
