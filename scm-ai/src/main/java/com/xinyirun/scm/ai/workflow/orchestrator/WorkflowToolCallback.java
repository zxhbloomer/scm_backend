package com.xinyirun.scm.ai.workflow.orchestrator;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Workflow的ToolCallback工厂类
 *
 * 将Workflow包装为ToolCallback接口,使其能被Orchestrator动态调用
 * 关键设计:
 * 1. 使用FunctionToolCallback.builder(name, biFunction)构建ToolCallback
 * 2. 处理Workflow的Flux<>异步返回,阻塞等待完成
 * 3. 将Map<String,Object>转换为List<JSONObject>格式传递给WorkflowStarter
 * 4. 错误处理转换为JSON响应
 * 5. 使用WorkflowCallSource.AI_CHAT_ORCHESTRATOR标识来源
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
@Slf4j
public class WorkflowToolCallback {

    /**
     * 创建Workflow的ToolCallback
     *
     * @param workflowUuid     workflow唯一标识
     * @param workflowTitle    workflow标题
     * @param workflowDesc     workflow描述
     * @param inputSchemaJson  workflow的inputConfig JSON字符串(可选)
     * @param workflowStarter  workflow启动器
     * @return ToolCallback实例
     */
    public static ToolCallback create(
            String workflowUuid,
            String workflowTitle,
            String workflowDesc,
            String inputSchemaJson,
            WorkflowStarter workflowStarter) {

        String toolName = "workflow_" + workflowUuid;
        String description = workflowTitle + ": " + (workflowDesc != null ? workflowDesc : "");
        String inputSchema = inputSchemaJson != null ? inputSchemaJson : buildDefaultSchema();

        // 创建BiFunction处理工具调用
        BiFunction<Map<String, Object>, ToolContext, String> toolFunction =
            (inputMap, toolContext) -> {
                try {
                    log.info("【WorkflowToolCallback】开始执行: workflowUuid={}, inputMap={}",
                            workflowUuid, inputMap);

                    // 1. 从ToolContext提取必要参数
                    String tenantCode = null;
                    String conversationId = null;
                    Map<String, Object> pageContext = null;

                    if (toolContext != null && toolContext.getContext() != null) {
                        tenantCode = (String) toolContext.getContext().get("tenantCode");
                        conversationId = (String) toolContext.getContext().get("conversationId");
                        pageContext = (Map<String, Object>) toolContext.getContext().get("pageContext");
                    }

                    log.info("【WorkflowToolCallback】参数提取: tenantCode={}, conversationId={}",
                            tenantCode, conversationId);

                    // 2. 将Map<String,Object>转换为List<JSONObject>格式
                    // WorkflowStarter.streaming()方法需要List<JSONObject>作为输入
                    List<JSONObject> userInputs = convertToUserInputs(inputMap);

                    // 3. 调用workflow执行引擎
                    Flux<WorkflowEventVo> events = workflowStarter.streaming(
                            workflowUuid,
                            userInputs,
                            tenantCode,
                            WorkflowCallSource.AI_CHAT_ORCHESTRATOR,
                            conversationId,
                            pageContext
                    );

                    // 4. 阻塞等待workflow完成,收集最终结果
                    StringBuilder result = new StringBuilder();
                    events.doOnNext(event -> {
                        if ("done".equals(event.getEvent())) {
                            result.append(event.getData());
                        }
                    }).blockLast();  // 关键: 阻塞直到Flux完成

                    String finalResult = result.toString();
                    log.info("【WorkflowToolCallback】执行完成: workflowUuid={}, resultLength={}",
                            workflowUuid, finalResult.length());

                    return finalResult.isEmpty() ? "{\"success\": true}" : finalResult;

                } catch (Exception e) {
                    log.error("【WorkflowToolCallback】执行失败: workflowUuid={}", workflowUuid, e);
                    return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
                }
            };

        // 使用FunctionToolCallback.builder(name, biFunction)构建ToolCallback
        return FunctionToolCallback.builder(toolName, toolFunction)
                .description(description)
                .inputType(Map.class)
                .inputSchema(inputSchema)
                .build();
    }

    /**
     * 将Map<String,Object>转换为List<JSONObject>格式
     *
     * WorkflowStarter.streaming()需要List<JSONObject>作为用户输入
     * 每个JSONObject代表一个输入项,格式必须符合WfNodeIODataUtil.createNodeIOData()的要求:
     * {
     *   "name": "参数名",
     *   "content": {
     *     "type": 1,      // 1=文本类型(WfIODataTypeEnum.TEXT)
     *     "title": "参数标题",
     *     "value": "实际值"
     *   }
     * }
     *
     * @param inputMap LLM传递的参数Map
     * @return 转换后的List<JSONObject>
     */
    private static List<JSONObject> convertToUserInputs(Map<String, Object> inputMap) {
        List<JSONObject> userInputs = new ArrayList<>();

        if (inputMap == null || inputMap.isEmpty()) {
            return userInputs;
        }

        for (Map.Entry<String, Object> entry : inputMap.entrySet()) {
            JSONObject input = new JSONObject();
            input.put("name", entry.getKey());

            // 构建content对象,符合WfNodeIODataUtil.createNodeIOData()的要求
            JSONObject content = new JSONObject();
            // TEXT类型(WfIODataTypeEnum.TEXT)
            content.put("type", WfIODataTypeEnum.TEXT.getValue());
            content.put("title", entry.getKey());
            content.put("value", entry.getValue());

            input.put("content", content);
            userInputs.add(input);

            log.debug("【WorkflowToolCallback】参数转换: name={}, value={}", entry.getKey(), entry.getValue());
        }

        return userInputs;
    }

    /**
     * 构建默认的JSON Schema
     *
     * 当workflow没有配置inputConfig时使用此默认Schema
     * 提供一个通用的user_input参数
     *
     * @return 默认JSON Schema字符串
     */
    private static String buildDefaultSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");

        JSONObject properties = new JSONObject();
        JSONObject userInput = new JSONObject();
        userInput.put("type", "string");
        userInput.put("description", "用户输入内容");
        properties.put("user_input", userInput);

        schema.put("properties", properties);

        return schema.toJSONString();
    }
}
