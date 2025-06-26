package com.xinyirun.scm.bean.system.vo.business.inventory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Wang Qianfeng
 * @date 2022/9/1 9:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDirectlyWarehouseFileJsonVo implements Serializable {


    private static final long serialVersionUID = -7221270848122271837L;

    /**
     * 到期日
     */
    private String contract_expire_dt;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 待出库量
     */
    private BigDecimal pending_count;


}
