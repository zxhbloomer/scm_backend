package com.xinyirun.scm.bean.bpm.vo;

import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import lombok.Data;
import org.flowable.task.api.DelegationState;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LoveMyOrange
 * @create 2022-10-14 23:47
 */
@Data
//@ApiModel("待办 需要返回给前端的VO")
public class TaskVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -293250553025393861L;
    //    @ApiModelProperty("任务id")
    private String taskId;
//    @ApiModelProperty("任务名称")
    private String taskName;
//    @ApiModelProperty("任务id")
    private String processInstanceId;
//    @ApiModelProperty("审批类型")
    private String processDefinitionName;
//    @ApiModelProperty("发起人")
    private UserInfo startUser;
//    @ApiModelProperty("提交时间")
    private Date startTime;
//    @ApiModelProperty("任务到达时间")
    private Date taskCreatedTime;
//    @ApiModelProperty("当前节点")
    private String currentActivityName;
//    @ApiModelProperty("审批状态")
    private String businessStatus;
//    @ApiModelProperty("结束时间")
    private Date endTime;
//    @ApiModelProperty("耗时")
    private String duration;
//    @ApiModelProperty("委派状态")
    private DelegationState delegationState;
//    @ApiModelProperty("发起人(带icon)")
    private BpmUsersEntity users;
//    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 委派状态名称
     */
    private String delegationStateName;

    private String serial_id;
}
