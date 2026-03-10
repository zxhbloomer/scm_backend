package com.xinyirun.scm.ai.workflow.node.openpage;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;

/**
 * 打开前端页面节点
 *
 * 支持两种模式：
 * 1. dialog模式（默认）：调用LLM生成业务JSON，通过ai_open_dialog_para触发前端弹窗
 * 2. route模式：构建导航指令，通过open_page_command触发前端RouterTab导航
 *    可选配置prompt调用LLM动态解析page_mode/form_data等参数
 */
@Slf4j
public class OpenPageNode extends AbstractWfNode {

    public OpenPageNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo nodeDef,
                        WfState wfState, WfNodeState nodeState) {
        super(wfComponent, nodeDef, wfState, nodeState);
    }

    @Override
    public NodeProcessResult onProcess() {
        OpenPageNodeConfig nodeConfig = checkAndGetConfig(OpenPageNodeConfig.class);
        String openMode = nodeConfig.getOpenMode();

        // 默认dialog模式，保持向后兼容
        if (openMode == null || "dialog".equals(openMode)) {
            return processDialogMode(nodeConfig);
        } else if ("route".equals(openMode)) {
            return processRouteMode(nodeConfig);
        }

        log.warn("OpenPage节点未知的open_mode: {}, 回退到dialog模式", openMode);
        return processDialogMode(nodeConfig);
    }

    /**
     * dialog模式：调用LLM生成JSON，触发前端弹窗（原有逻辑）
     */
    private NodeProcessResult processDialogMode(OpenPageNodeConfig nodeConfig) {
        String prompt = WorkflowUtil.renderTemplate(nodeConfig.getPrompt(), state.getInputs());
        log.info("OpenPage节点[dialog模式]开始调用LLM，prompt长度: {}", prompt != null ? prompt.length() : 0);

        NodeIOData output = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
        state.getOutputs().add(output);

        String result = output.valueToString();
        wfState.setAi_open_dialog_para(result);
        log.info("OpenPage节点[dialog模式]完成，LLM输出长度: {}", result != null ? result.length() : 0);

        return new NodeProcessResult();
    }

    /**
     * route模式：构建导航指令
     * 如果配置了prompt，先调用LLM动态解析参数（page_mode/form_data等）
     * 否则从上游输入变量或MCP output JSON中读取
     */
    private NodeProcessResult processRouteMode(OpenPageNodeConfig nodeConfig) {
        // 优先从上游输入读取路由，fallback到上游MCP output JSON
        String path = extractInputValue("path");
        if (path == null) {
            path = extractFromRoutesJson("path");
        }

        log.info("OpenPage节点[route模式]开始，path={}", path);

        String pageMode = null;
        JSONObject formData = null;
        JSONObject queryParams = null;
        String recordId = null;

        // 如果配置了prompt，调用LLM动态解析参数
        String promptTemplate = nodeConfig.getPrompt();
        if (promptTemplate != null && !promptTemplate.trim().isEmpty()) {
            String prompt = WorkflowUtil.renderTemplate(promptTemplate, state.getInputs());
            log.info("OpenPage节点[route模式]调用LLM，prompt长度: {}", prompt.length());

            NodeIOData llmOutput = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
            state.getOutputs().add(llmOutput);

            String llmResult = llmOutput.valueToString();
            log.info("OpenPage节点[route模式]LLM输出: {}", llmResult);

            // 解析LLM输出的JSON
            if (llmResult != null && !llmResult.isEmpty()) {
                try {
                    int start = llmResult.indexOf('{');
                    int end = llmResult.lastIndexOf('}');
                    if (start >= 0 && end > start) {
                        JSONObject llmJson = JSONObject.parseObject(llmResult.substring(start, end + 1));
                        if (llmJson.getString("page_mode") != null) {
                            pageMode = llmJson.getString("page_mode");
                        }
                        if (llmJson.getJSONObject("form_data") != null) {
                            formData = llmJson.getJSONObject("form_data");
                        }
                        if (llmJson.getJSONObject("query_params") != null) {
                            queryParams = llmJson.getJSONObject("query_params");
                        }
                        if (llmJson.getString("record_id") != null) {
                            recordId = llmJson.getString("record_id");
                        }
                    }
                } catch (Exception e) {
                    log.warn("OpenPage节点解析LLM输出JSON失败: {}", e.getMessage());
                }
            }
        }

        // LLM未提供的字段，fallback到上游输入/routes JSON/静态配置
        if (pageMode == null) {
            pageMode = extractInputValue("page_mode");
        }
        if (pageMode == null) {
            pageMode = extractFromRoutesJson("page_mode");
        }
        if (pageMode == null) {
            pageMode = nodeConfig.getPageMode();
        }
        if (formData == null) {
            formData = extractInputAsJson("form_data");
        }
        if (queryParams == null) {
            queryParams = extractInputAsJson("query_params");
        }
        if (recordId == null) {
            recordId = extractInputValue("record_id");
        }

        JSONObject command = new JSONObject();
        command.put("route", path);
        if (pageMode != null) command.put("page_mode", pageMode);
        command.put("query_params", queryParams);
        command.put("form_data", formData);
        command.put("record_id", recordId);

        String commandJson = command.toJSONString();
        wfState.setOpen_page_command(commandJson);
        log.info("OpenPage节点[route模式]导航指令: {}", commandJson);

        state.getOutputs().add(NodeIOData.createByText("open_page_command", "页面导航指令", commandJson));

        return new NodeProcessResult();
    }

    /**
     * 从上游输入中提取指定名称的JSON对象
     */
    private JSONObject extractInputAsJson(String name) {
        for (NodeIOData input : state.getInputs()) {
            if (name.equals(input.getName())) {
                String value = input.valueToString();
                if (value != null && !value.isEmpty()) {
                    try {
                        return JSONObject.parseObject(value);
                    } catch (Exception e) {
                        log.warn("解析输入参数{}为JSON失败: {}", name, value);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 从上游输入中提取指定名称的字符串值
     */
    private String extractInputValue(String name) {
        for (NodeIOData input : state.getInputs()) {
            if (name.equals(input.getName())) {
                return input.valueToString();
            }
        }
        return null;
    }

    /**
     * 从上游所有已完成节点的 output 中查找包含 routes[0] 的 JSON，返回指定字段值
     * 适用于 MCP 节点输出 {"found_count":1,"routes":[{"path":"/po/project","page_mode":"new",...}]} 的场景
     */
    private String extractFromRoutesJson(String field) {
        for (AbstractWfNode completedNode : wfState.getCompletedNodes()) {
            for (NodeIOData ioData : completedNode.getState().getOutputs()) {
                if (!"output".equals(ioData.getName())) continue;
                String value = ioData.valueToString();
                if (value == null || value.isEmpty()) continue;
                try {
                    int start = value.indexOf('{');
                    int end = value.lastIndexOf('}');
                    if (start < 0 || end < 0) continue;
                    JSONObject json = JSONObject.parseObject(value.substring(start, end + 1));
                    com.alibaba.fastjson2.JSONArray routes = json.getJSONArray("routes");
                    if (routes != null && !routes.isEmpty()) {
                        JSONObject first = routes.getJSONObject(0);
                        String result = first != null ? first.getString(field) : null;
                        if (result != null && !result.isEmpty()) {
                            log.info("从上游output JSON解析到{}={}", field, result);
                            return result;
                        }
                    }
                } catch (Exception e) {
                    log.debug("解析output JSON失败，跳过: {}", e.getMessage());
                }
            }
        }
        return null;
    }
}
