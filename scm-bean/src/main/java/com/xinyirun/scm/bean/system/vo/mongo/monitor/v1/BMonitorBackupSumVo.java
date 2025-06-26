package com.xinyirun.scm.bean.system.vo.mongo.monitor.v1;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorBackupSumVo implements Serializable {

    private static final long serialVersionUID = 3003441621377932654L;

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
