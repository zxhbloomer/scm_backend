package com.xinyirun.scm.bean.api.vo.business.in;

import com.xinyirun.scm.bean.system.vo.wms.in.delivery.BDeliveryVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBDeliveryAsyncVo implements Serializable {

    private static final long serialVersionUID = 2532468659330765679L;

    private List<BDeliveryVo> beans;

    String app_config_type;
}
