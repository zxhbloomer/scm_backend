package com.xinyirun.scm.bean.bpm.dto;

import com.xinyirun.scm.bean.bpm.dto.json.UserInfo;
import lombok.Data;

/**
 * @author LoveMyOrange
 * @create 2022-10-14 23:47
 */
@Data
//@ApiModel("我发起流程 需要返回给前端的DTO")
public class ApplyDTO extends PageDTO {
//    @ApiModelProperty("当前人用户信息,(因为本项目没有做登录功能,所以就是直接传递用户信息就行 简单起见)")
    private UserInfo currentUserInfo;
}
