package com.xinyirun.scm.bean.bpm.vo;

//import com.dingding.mid.dto.json.UserInfo;
//import com.xinyirun.scm.bean.entity.Users;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LoveMyOrange
 * @create 2022-10-14 23:47
 */
@Data
//@ApiModel("我发起页面 需要返回给前端的VO")
public class HistoryProcessInstanceVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -5910377353204866959L;
    //    @ApiModelProperty("流程实例id")
    private String processInstanceId;
//    @ApiModelProperty("审批类型")
    private String processDefinitionName;
//    @ApiModelProperty("发起人")
    private UserInfo startUser;
//    @ApiModelProperty("发起人(带icon)")
    private BpmUsersEntity users;
//    @ApiModelProperty("提交时间")
    private Date startTime;
//    @ApiModelProperty("结束时间")
    private Date endTime;
//    @ApiModelProperty("当前节点")
    private String currentActivityName;
//    @ApiModelProperty("审批状态")
    private String businessStatus;
//    @ApiModelProperty("耗时")
    private String duration;
}
