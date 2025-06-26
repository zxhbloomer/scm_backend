package com.xinyirun.scm.bean.bpm.vo;

import com.alibaba.fastjson2.JSONObject;
//import com.dingding.mid.dto.json.ChildNode;
//import com.xinyirun.scm.bean.entity.ProcessTemplates;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import com.xinyirun.scm.bean.bpm.dto.json.ChildNode;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author LoveMyOrange
 * @create 2022-10-15 16:27
 */
@Data
//@ApiModel("详情VO")
public class HandleDataVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2730612741208989301L;
    //    @ApiModelProperty("任务id")
    private String taskId;
//    @ApiModelProperty("流程实例id")
    private String processInstanceId;
//    @ApiModelProperty("表单数据")
    private JSONObject formData;
//    @ApiModelProperty("前端是否打开 签名板")
    private Boolean signFlag;
//    @ApiModelProperty("流程模板")
    private BpmProcessTemplatesEntity processTemplates;
//    @ApiModelProperty("当前节点json数据 如果有taskId的话才返回")
    private ChildNode currentNode;
//    @ApiModelProperty("任务详情")
    private Map<String,List<TaskDetailVo>> detailVOList;
//    @ApiModelProperty("已经结束的节点")
    List<String> endList;
//    @ApiModelProperty("正在运行的节点")
    List<String> runningList;
//    @ApiModelProperty("还没运行的节点")
    List<String> noTakeList;
}
