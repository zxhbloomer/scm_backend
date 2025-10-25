package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.annotation.JSONField;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataTextContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataNumberContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataBoolContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataFilesContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataOptionsContent;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 工作流节点输入输出参数定义 VO
 * 严格参考 aideepin 的 WfNodeIO 设计
 *
 * @author SCM-AI团队
 * @since 2025-10-24
 */
@Data
public class AiWfNodeIOVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 参数唯一标识
     */
    private String uuid;

    /**
     * 参数类型：1-TEXT, 2-NUMBER, 3-OPTIONS, 4-FILES, 5-BOOL
     */
    private Integer type;

    /**
     * 参数名称（程序内部使用）
     */
    private String name;

    /**
     * 参数标题（用户界面显示）
     */
    private String title;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 最大长度（TEXT 类型使用）
     * 使用 @JSONField 注解实现 JSON 序列化时驼峰转下划线
     */
    @JSONField(name = "max_length")
    private Integer maxLength;

    /**
     * 检查数据是否合规
     * 参考 aideepin: WfNodeIO.checkValue() 及其5个子类实现
     *
     * 由于 scm 使用扁平结构（单个VO类 + type字段），这里根据 type 字段分发到不同验证逻辑
     *
     * @param data 节点输入输出数据
     * @return 是否正确
     */
    public boolean checkValue(NodeIOData data) {
        if (data == null || data.getContent() == null) {
            return false;
        }

        // 根据 type 字段分发验证逻辑
        if (type == 1) {
            // TEXT 类型验证 - 参考 WfNodeIOText.checkValue() 第29-40行
            if (!(data.getContent() instanceof NodeIODataTextContent textContent)) {
                return false;
            }
            String value = textContent.getValue();
            if (required != null && required && value == null) {
                return false;
            }
            if (maxLength != null && value != null && value.length() > maxLength) {
                return false;
            }
            return true;

        } else if (type == 2) {
            // NUMBER 类型验证 - 参考 WfNodeIONumber.checkValue() 第24-29行
            if (!(data.getContent() instanceof NodeIODataNumberContent numberContent)) {
                return false;
            }
            return !(required != null && required) || numberContent.getValue() != null;

        } else if (type == 5) {
            // BOOL 类型验证 - 参考 WfNodeIOBool.checkValue() 第25-30行
            if (!(data.getContent() instanceof NodeIODataBoolContent)) {
                return false;
            }
            return !(required != null && required) || data.getContent().getValue() != null;

        } else if (type == 4) {
            // FILES 类型验证 - 参考 WfNodeIOFiles.checkValue() 第26-31行
            if (!(data.getContent() instanceof NodeIODataFilesContent filesContent)) {
                return false;
            }
            return !(required != null && required) || !CollectionUtils.isEmpty(filesContent.getValue());

        } else if (type == 3) {
            // OPTIONS 类型验证 - 参考 WfNodeIOOptions.checkValue() 第27-37行
            if (!(data.getContent() instanceof NodeIODataOptionsContent optionsContent)) {
                return false;
            }
            Map<String, Object> value = optionsContent.getValue();
            if (required != null && required && value == null) {
                return false;
            }
            // 注意：scm 的 AiWfNodeIOVo 没有 multiple 字段，默认允许多选
            // 如需限制单选，可后续添加 multiple 字段
            return true;

        } else {
            // 未知类型
            return false;
        }
    }
}
