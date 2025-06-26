package com.xinyirun.scm.bean.api.vo.business.largescreen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 按仓库类型， 库存量统计
 *
 * @Author: wangqianfeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiWarehouseInventoryStatisticsVo implements Serializable {

    private static final long serialVersionUID = 5629240402195766667L;

    /**
     * 库存量统计
     */
    private List<ApiWarehouseStatisticsVo> inventory_statistics;

    /**
     * 不同类型的仓库下 库点统计
     */
    private List<ApiWarehouseStatisticsVo> warehouse_statistics;

    /**
     * 批次时间
     */
    private String batch_date;
}
