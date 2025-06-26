package com.xinyirun.scm.bean.system.vo.business.monitor;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorSumVo  extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = 4026804841141423474L;

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

    /**
     * 同步失败数量
     */
    private Integer sync_error_count;

    /**
     * 退货总数
     */
    private BigDecimal count_return_qty;
}
