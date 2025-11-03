package com.xinyirun.scm.ai.workflow.data;

import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 工作流节点布尔类型数据
 *
 * @author zxh
 * @since 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeIODataBoolContent extends NodeIODataContent<Boolean> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private Integer type = WfIODataTypeEnum.BOOL.getValue();

    private Boolean value;
}
