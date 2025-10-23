package com.xinyirun.scm.ai.workflow.data;

import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 工作流节点下拉选项类型数据
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeIODataOptionsContent extends NodeIODataContent<Map<String, Object>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private Integer type = WfIODataTypeEnum.OPTIONS.getValue();

    private Map<String, Object> value;
}
