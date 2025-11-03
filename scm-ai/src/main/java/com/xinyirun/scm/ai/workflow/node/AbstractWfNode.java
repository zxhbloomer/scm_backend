package com.xinyirun.scm.ai.workflow.node;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeParamRefVo;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.exception.system.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.*;

/**
 * 工作流节点抽象基类
 *
 * 所有工作流节点的运行时基类,定义了节点执行的标准流程
 */
@Data
@Slf4j
public abstract class AbstractWfNode {

    protected AiWorkflowComponentEntity wfComponent;
    protected WfState wfState;
    @Getter
    protected WfNodeState state;
    protected AiWorkflowNodeVo node;

    public AbstractWfNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo node, WfState wfState, WfNodeState nodeState) {
        this.wfState = wfState;
        this.wfComponent = wfComponent;
        this.state = nodeState;
        this.node = node;
    }

    /**
     * 初始化节点输入参数
     *
     * 流程：
     * 1. 如果是开始节点,直接使用工作流的初始输入
     * 2. 否则,使用上游节点的输出作为当前节点的输入
     * 3. 处理引用类型的输入参数
     * 4. 根据节点的输入参数定义进行筛选
     * 5. 处理默认参数名转换(output -> input)
     */
    public void initInput() {
        AiWfNodeInputConfigVo inputConfig = node.getInputConfig();
        if (null == inputConfig) {
            log.info("节点输入参数没有配置");
            return;
        }

        if (wfState.getCompletedNodes().isEmpty()) {
            log.info("没有上游节点，当前节点为开始节点");
            state.getInputs().addAll(wfState.getInput());
            return;
        }

        List<NodeIOData> inputs = new ArrayList<>();

        // 将上游节点的输出转成当前节点的输入
        List<NodeIOData> upstreamOutputs = wfState.getLatestOutputs();
        if (!upstreamOutputs.isEmpty()) {
            inputs.addAll(new ArrayList<>(deepCopyList(upstreamOutputs)));
        }

        // 处理引用类型的输入参数
        List<AiWfNodeParamRefVo> refInputDefs = inputConfig.getRefInputs();
        inputs.addAll(changeRefersToNodeIODatas(refInputDefs));

        // 根据节点的输入参数定义，筛选出符合要求的输入参数
        List<String> defInputNames = inputConfig.getRefInputs().stream()
            .map(AiWfNodeParamRefVo::getName).collect(Collectors.toList());
        defInputNames.addAll(inputConfig.getUserInputs().stream()
            .map(AiWfNodeIOVo::getName).toList());

        List<NodeIOData> needInputs = inputs.stream().filter(item -> {
            String needInputName = item.getName();
            // 上游节点的默认输出参数(output)，改成input即可
            if (DEFAULT_OUTPUT_PARAM_NAME.equals(needInputName)) {
                item.setName(DEFAULT_INPUT_PARAM_NAME);
                return true;
            }
            return defInputNames.contains(needInputName);
        }).toList();

        state.getInputs().addAll(needInputs);
    }

    /**
     * 深度复制列表
     */
    private List<NodeIOData> deepCopyList(List<NodeIOData> list) {
        List<NodeIOData> result = new ArrayList<>();
        for (NodeIOData item : list) {
            result.add(SerializationUtils.clone(item));
        }
        return result;
    }

    /**
     * 将引用类型的输入参数定义转换为实际的输入数据
     *
     * @param refInputDefs 引用输入参数定义列表
     * @return 实际的输入数据列表
     */
    private List<NodeIOData> changeRefersToNodeIODatas(List<AiWfNodeParamRefVo> refInputDefs) {
        if (CollectionUtils.isEmpty(refInputDefs)) {
            return new ArrayList<>();
        }

        List<NodeIOData> result = new ArrayList<>();
        for (AiWfNodeParamRefVo refer : refInputDefs) {
            NodeIOData data = createByReferParam(refer);
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }

    /**
     * 根据引用参数创建输入数据
     *
     * @param refer 引用参数定义
     * @return 输入数据
     */
    private NodeIOData createByReferParam(AiWfNodeParamRefVo refer) {
        // 从已完成节点中获取指定节点的输入输出数据
        List<NodeIOData> ioDataList = wfState.getIOByNodeUuid(refer.getNodeUuid());

        // 查找匹配的参数
        Optional<NodeIOData> hitDataOpt = ioDataList.stream()
                .filter(ioData -> ioData.getName().equalsIgnoreCase(refer.getNodeParamName()))
                .findFirst();

        if (hitDataOpt.isEmpty()) {
            return null;
        }

        // 深拷贝并设置新名称
        NodeIOData item = SerializationUtils.clone(hitDataOpt.get());
        item.setName(refer.getName());
        return item;
    }

    /**
     * 根据引用节点UUID和参数名称创建输入参数
     *
     * @param refNodeUuid       引用节点的UUID
     * @param refNodeParamName  引用节点的参数名称
     * @return 创建的输入参数，如果找不到则返回null
     */
    public NodeIOData createByReferParam(String refNodeUuid, String refNodeParamName) {
        Optional<NodeIOData> hitDataOpt = wfState.getIOByNodeUuid(refNodeUuid)
                .stream()
                .filter(wfNodeIOData -> wfNodeIOData.getName().equalsIgnoreCase(refNodeParamName))
                .findFirst();
        return hitDataOpt.<NodeIOData>map(SerializationUtils::clone).orElse(null);
    }

    /**
     * 执行节点处理
     *
     * @param inputConsumer  输入参数处理回调
     * @param outputConsumer 输出结果处理回调
     * @return 节点处理结果
     */
    public NodeProcessResult process(Consumer<WfNodeState> inputConsumer, Consumer<WfNodeState> outputConsumer) {
        state.setProcessStatus(NODE_PROCESS_STATUS_DOING);
        initInput();

        // 处理人工反馈的情况
        Object humanFeedbackState = state.data().get(HUMAN_FEEDBACK_KEY);
        if (null != humanFeedbackState) {
            String userInput = humanFeedbackState.toString();
            if (StringUtils.isNotBlank(userInput)) {
                state.getInputs().add(NodeIOData.createByText(HUMAN_FEEDBACK_KEY, "default", userInput));
            }
        }

        if (null != inputConsumer) {
            inputConsumer.accept(state);
        }

        log.info("节点输入: {}", JsonUtil.toJson(state.getInputs()));

        NodeProcessResult processResult;
        try {
            processResult = onProcess();
        } catch (Exception e) {
            log.error("节点执行失败", e);
            state.setProcessStatus(NODE_PROCESS_STATUS_FAIL);
            state.setProcessStatusRemark("节点执行异常: " + e.getMessage());
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_FAIL);
            if (null != outputConsumer) {
                outputConsumer.accept(state);
            }
            throw new RuntimeException(e);
        }

        if (!processResult.getContent().isEmpty()) {
            state.setOutputs(processResult.getContent());
        }

        state.setProcessStatus(NODE_PROCESS_STATUS_SUCCESS);
        wfState.getCompletedNodes().add(this);

        if (null != outputConsumer) {
            outputConsumer.accept(state);
        }

        return processResult;
    }

    /**
     * 抽象方法：具体节点实现的处理逻辑
     *
     * @return 节点处理结果
     */
    protected abstract NodeProcessResult onProcess();

    /**
     * 获取第一个文本类型的输入参数
     *
     * @return 文本内容
     */
    protected String getFirstInputText() {
        if (state.getInputs().isEmpty()) {
            return "";
        }

        if (state.getInputs().size() == 1) {
            return state.getInputs().get(0).valueToString();
        }

        return state.getInputs()
                .stream()
                .filter(item -> !DEFAULT_INPUT_PARAM_NAME.equals(item.getName()))
                .map(NodeIOData::valueToString)
                .findFirst()
                .orElse("");
    }

    /**
     * 获取并验证节点配置
     *
     * @param clazz 配置类型
     * @param <T>   泛型类型
     * @return 反序列化后的配置对象
     */
    protected <T> T checkAndGetConfig(Class<T> clazz) {
        // 使用 Fastjson2 的 JSONObject
        JSONObject configObj = node.getNodeConfig();

        // 只检查 null,允许空对象 {}
        // 空对象 {} 可以反序列化为配置类,字段为 null 值
        if (null == configObj) {
            log.error("node config is null, node uuid: {}", state.getUuid());
            throw new BusinessException("节点配置不存在");
        }

        log.info("node config: {}", configObj);

        T nodeConfig;
        try {
            // 使用 Fastjson2 直接转换
            // 即使 configObj 是 {},也能反序列化为对象,字段为默认值
            nodeConfig = configObj.toJavaObject(clazz);
        } catch (Exception e) {
            log.error("节点配置反序列化失败, node uuid: {}, error: {}", state.getUuid(), e.getMessage());
            throw new BusinessException("节点配置反序列化失败: " + e.getMessage());
        }

        if (null == nodeConfig) {
            log.warn("找不到节点的配置, node uuid: {}", state.getUuid());
            throw new BusinessException("节点配置无效");
        }

        // 配置验证
        boolean configValid = true;
        try {
            LocalValidatorFactoryBean validatorFactoryBean = SpringUtil.getBean(LocalValidatorFactoryBean.class);
            if (validatorFactoryBean != null) {
                Validator validator = validatorFactoryBean.getValidator();
                Set<ConstraintViolation<T>> violations = validator.validate(nodeConfig);
                for (ConstraintViolation<T> violation : violations) {
                    log.error(violation.getMessage());
                    configValid = false;
                }
            }
        } catch (Exception e) {
            log.warn("节点配置验证异常, node uuid: {}, error: {}", state.getUuid(), e.getMessage());
        }

        if (!configValid) {
            log.warn("节点配置验证失败, node uuid: {}", state.getUuid());
            throw new BusinessException("节点配置验证失败");
        }

        return nodeConfig;
    }
}
