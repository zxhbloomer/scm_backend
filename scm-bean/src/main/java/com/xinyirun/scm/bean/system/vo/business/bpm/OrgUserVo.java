package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrgUserVo extends BaseVo implements Serializable {

  @Serial
  private static final long serialVersionUID = -4924871680718625271L;
  /**
   * 用户id
   */
  private String id;

  /**
   * 用户code
   */
  private String code;

  /**
   * 用户名称
   */
  private String name;

  /**
   * 用户头像
   */
  private String avatar;

  /**
   * 用户类型
   */
  private String type;

}
