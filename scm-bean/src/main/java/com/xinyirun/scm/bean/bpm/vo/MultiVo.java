package com.xinyirun.scm.bean.bpm.vo;

//import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author:LoveMyOrange
 * @Description:
 * @Date:Created in 2022/10/22 15:33
 */
//@ApiModel("减签前一步操作")
@Data
public class MultiVo implements Serializable {
  @Serial
  private static final long serialVersionUID = 1116420256108393877L;
  private String taskId;
  private String processInstanceId;
  private String executionId;
  private String userId;
  private String userName;
}
