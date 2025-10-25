package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataFilesContent;
import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 工作流节点IO数据工具类
 * 对齐AIDeepin: com.moyz.adi.common.workflow.WfNodeIODataUtil
 *
 * @author SCM-AI团队
 * @since 2025-10-23
 */
public class WfNodeIODataUtil {

    private static final String DEFAULT_INPUT_PARAM_NAME = "input";
    private static final String DEFAULT_OUTPUT_PARAM_NAME = "output";
    private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public static NodeIOData createNodeIOData(JSONObject data) {
        String name = data.getString("name");
        JSONObject content = data.getJSONObject("content");
        if (null == name || null == content) {
            throw new BusinessException("参数错误");
        }
        Integer type = content.getInteger("type");
        String title = content.getString("title");
        Object value = content.get("value");
        NodeIOData result = null;
        if (WfIODataTypeEnum.TEXT.getValue().equals(type)) {
            result = NodeIOData.createByText(name, title, String.valueOf(value));
        } else if (WfIODataTypeEnum.NUMBER.getValue().equals(type)) {
            result = NodeIOData.createByNumber(name, title, content.getDouble("value"));
        } else if (WfIODataTypeEnum.BOOL.getValue().equals(type)) {
            result = NodeIOData.createByBool(name, title, content.getBoolean("value"));
        } else if (WfIODataTypeEnum.FILES.getValue().equals(type)) {
            if (value instanceof JSONArray) {
                List<String> fileUrls = ((JSONArray) value).toJavaList(String.class);
                result = NodeIOData.createByFiles(name, title, fileUrls);
            }
        } else if (WfIODataTypeEnum.OPTIONS.getValue().equals(type)) {
            if (value instanceof JSONObject) {
                result = NodeIOData.createByOptions(name, title, ((JSONObject) value).toJavaObject(Map.class));
            }
        }
        return result;
    }

    /**
     * 1.如果没有名称为 output 的输出参数，则需要新增 <br/>
     * 2.判断是否已经有文本类型的输出参数，如果有，则复制该参数并将参数名改为 output <br/>
     * 3.如果没有文本类型的参数，则复制第一个参数，并将参数名改为 output
     *
     * @param inputs 输入参数列表
     * @return 输出参数列表
     */
    public static List<NodeIOData> changeInputsToOutputs(List<NodeIOData> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            return new ArrayList<>();
        }
        List<NodeIOData> result = deepCopy(inputs);

        boolean outputExist = false;
        NodeIOData defaultInputName = null, txtExist = null, first = null;
        for (NodeIOData nodeIOData : result) {
            if (null == first) {
                first = nodeIOData;
            }
            if (DEFAULT_OUTPUT_PARAM_NAME.equals(nodeIOData.getName())) {
                outputExist = true;
            } else if (DEFAULT_INPUT_PARAM_NAME.equals(nodeIOData.getName())) {
                defaultInputName = nodeIOData;
            } else if (null == txtExist && WfIODataTypeEnum.TEXT.getValue().equals(nodeIOData.getContent().getType())) {
                txtExist = nodeIOData;
            }
        }

        if (outputExist) {
            return result;
        }

        if (null != defaultInputName) {
            defaultInputName.setName(DEFAULT_OUTPUT_PARAM_NAME);
        } else if (null != txtExist) {
            txtExist.setName(DEFAULT_OUTPUT_PARAM_NAME);
        } else if (null != first) {
            first.setName(DEFAULT_OUTPUT_PARAM_NAME);
        }

        return result;
    }

    /**
     * 将输入输出中的文件url转成markdown格式的文件地址<br/>
     * 将变量渲染到模板时使用该方法，其他情况交由前端处理
     *
     * @param ioDataList 输入输出列表
     */
    public static void changeFilesContentToMarkdown(List<NodeIOData> ioDataList) {
        ioDataList.forEach(input -> {
            if (input.getContent() instanceof NodeIODataFilesContent filesContent) {
                List<String> newValues = new ArrayList<>();
                for (String s : filesContent.getValue()) {
                    String extension = s.substring(s.lastIndexOf(".") + 1);
                    if (IMAGE_EXTENSIONS.contains(extension)) {
                        newValues.add("![" + filesContent.getTitle() + "](" + s + ")");
                    } else {
                        newValues.add("[" + filesContent.getTitle() + "](" + s + ")");
                    }
                }
                filesContent.setValue(newValues);
            }
        });
    }

    /**
     * 深度复制NodeIOData列表
     */
    private static List<NodeIOData> deepCopy(List<NodeIOData> source) {
        if (source == null) {
            return null;
        }
        List<NodeIOData> result = new ArrayList<>(source.size());
        for (NodeIOData item : source) {
            try {
                // 使用JSON序列化进行深度复制
                String json = JsonUtil.toJson(item);
                NodeIOData copy = JsonUtil.fromJson(json, NodeIOData.class);
                result.add(copy);
            } catch (Exception e) {
                throw new BusinessException("深度复制失败: " + e.getMessage());
            }
        }
        return result;
    }
}
