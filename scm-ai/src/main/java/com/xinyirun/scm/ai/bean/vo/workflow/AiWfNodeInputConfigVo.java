package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 节点的输入参数配置 VO
 * 严格参考 aideepin 的 WfNodeInputConfig 设计
 *
 * @author SCM-AI团队
 * @since 2025-10-24
 */
@Data
public class AiWfNodeInputConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户输入参数列表
     * 使用 @JSONField 注解实现 JSON 序列化时驼峰转下划线
     */
    @JSONField(name = "user_inputs")
    private List<AiWfNodeIOVo> userInputs = new ArrayList<>();

    /**
     * 引用输入参数列表
     * 使用 @JSONField 注解实现 JSON 序列化时驼峰转下划线
     */
    @JSONField(name = "ref_inputs")
    private List<AiWfNodeParamRefVo> refInputs = new ArrayList<>();
}
