package com.xinyirun.scm.ai.workflow.data;

import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 工作流节点文件列表类型数据
 *
 * @author zxh
 * @since 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeIODataFilesContent extends NodeIODataContent<List<String>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private Integer type = WfIODataTypeEnum.FILES.getValue();

    private List<String> value;
}
