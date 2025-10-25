package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 输入参数-引用类型参数定义 VO
 * 严格参考 aideepin 的 WfNodeParamRef 设计
 *
 * 说明：
 * 1. 该参数的值是另一个节点的输出/或输入参数
 * 2. 该类型参数只在非开始节点中使用
 * 3. 通常做为输入参数使用
 *
 * @author SCM-AI团队
 * @since 2025-10-24
 */
@Data
public class AiWfNodeParamRefVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 引用的节点 UUID
     * 使用 @JSONField 注解实现 JSON 序列化时驼峰转下划线
     */
    @JSONField(name = "node_uuid")
    private String nodeUuid;

    /**
     * 引用的节点参数名称
     * 使用 @JSONField 注解实现 JSON 序列化时驼峰转下划线
     */
    @JSONField(name = "node_param_name")
    private String nodeParamName;

    /**
     * 当前参数名称
     */
    private String name;
}
