package com.xinyirun.scm.bean.bpm.dto.json;

import lombok.Data;

import java.util.List;

/**
 * @Author:LoveMyOrange
 * @Description:
 * @Date:Created in 2022/10/9 18:57
 */
@Data
public class GroupsInfo {
    private String groupType;
    private List<ConditionInfo> conditions;
    private List<String> cids;
}
