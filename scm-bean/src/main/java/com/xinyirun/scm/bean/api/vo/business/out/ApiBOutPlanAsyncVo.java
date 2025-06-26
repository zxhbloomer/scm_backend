package com.xinyirun.scm.bean.api.vo.business.out;

import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBOutPlanAsyncVo implements Serializable {

    private static final long serialVersionUID = 403644123068177207L;

    private List<BOutPlanListVo> beans;

    String app_config_type;
}
