package com.xinyirun.scm.bean.system.bo.fund.monit.out;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 资金使用情况，减少资金的bo
 */
@Data
@Builder
public class FundOutBo implements Serializable {
    @Serial
    private static final long serialVersionUID = -889291433033018301L;

}
