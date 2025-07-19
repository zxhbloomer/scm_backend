package com.xinyirun.scm.bean.system.vo.business.wms.inventory;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @date 2022/9/1 9:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BDirectlyWarehouseVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 7676586094783124733L;

    /**
     * 库点名称
     */
    private String warehouse_name;

    /**
     * 竞拍数
     */
    private BigDecimal contract_num;

    /**
     * 流水数
     */
    private BigDecimal in_num;

    /**
     * 当天车辆数
     */
    private Integer vehicle_count_today;

    /**
     * 累计车辆数
     */
    private Integer vehicle_count;

    /**
     * 当天出库数
     */
    private BigDecimal actual_count_today;

    /**
     * 累计出库数
     */
    private BigDecimal actual_count;

    /**
     * 待出库数量
     */
    private BigDecimal pending_count;

    /**
     * 截止日期与合同
     */
    private List<BDirectlyWarehouseFileJsonVo> file_json;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 流水数
     */
    private BigDecimal in_actual_count;
}
