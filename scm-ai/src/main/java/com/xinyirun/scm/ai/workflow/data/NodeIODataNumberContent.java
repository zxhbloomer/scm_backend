package com.xinyirun.scm.ai.workflow.data;

import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 工作流节点数字类型数据
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeIODataNumberContent extends NodeIODataContent<Double> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private Integer type = WfIODataTypeEnum.NUMBER.getValue();

    private Double value;
}
