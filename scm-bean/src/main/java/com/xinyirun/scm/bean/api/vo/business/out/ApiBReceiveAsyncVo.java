package com.xinyirun.scm.bean.api.vo.business.out;

import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBReceiveAsyncVo implements Serializable {

    private static final long serialVersionUID = 5352208633700592906L;

    private List<BReceiveVo> beans;

    String app_config_type;
}
