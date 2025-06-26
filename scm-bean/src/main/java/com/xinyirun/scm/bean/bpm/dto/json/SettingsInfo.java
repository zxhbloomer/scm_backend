package com.xinyirun.scm.bean.bpm.dto.json;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @Author:LoveMyOrange
 * @Description:
 * @Date:Created in 2022/10/9 16:06
 */
@Data
public class SettingsInfo {
  private List<String> commiter;
  private List<UserInfo> admin;
  private Boolean sign;
  private JSONObject notify;
}
