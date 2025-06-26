package com.xinyirun.scm.bean.api.vo.business.in;

import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBInPlanAsyncVo implements Serializable {

    private static final long serialVersionUID = 5352208633700592906L;

    private List<BInPlanVo> beans;

    String app_config_type;
}
