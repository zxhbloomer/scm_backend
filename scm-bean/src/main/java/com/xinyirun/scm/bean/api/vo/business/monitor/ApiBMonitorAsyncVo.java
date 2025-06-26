package com.xinyirun.scm.bean.api.vo.business.monitor;

import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBMonitorAsyncVo implements Serializable {

    private static final long serialVersionUID = 5148965942677478952L;

    private List<BMonitorVo> beans;

    String app_config_type;
}
