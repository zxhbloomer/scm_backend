package com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorBackupSumV2Vo implements Serializable {

    private static final long serialVersionUID = -2635341016043476736L;

    /**
     * 监管任务合计
     */
    private BigDecimal qty_loss;

    /**
     * 发货数量
     */
    private BigDecimal out_qty;

    /**
     * 收货数量
     */
    private BigDecimal in_qty;

}
