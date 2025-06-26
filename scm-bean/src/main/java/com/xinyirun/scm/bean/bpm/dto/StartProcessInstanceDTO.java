package com.xinyirun.scm.bean.bpm.dto;

import com.alibaba.fastjson2.JSONObject;
//import com.dingding.mid.dto.json.UserInfo;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author LoveMyOrange
 * @create 2022-10-14 23:27
 */
@Data
//@ApiModel("启动流程实例需要传入的参数")
public class StartProcessInstanceDTO {
//    @ApiModelProperty("流程定义id")
    private String processDefinitionId;
//    @ApiModelProperty("表单数据")
    private JSONObject formData;
//    @ApiModelProperty("发起人自选的信息, key 是nodeId, value是用户信息")
    private Map<String, List<UserInfo>> processUsers;
//    @ApiModelProperty("当前人用户信息,(因为本项目没有做登录功能,所以就是直接传递用户信息就行 简单起见)")
    private UserInfo startUserInfo;
}
