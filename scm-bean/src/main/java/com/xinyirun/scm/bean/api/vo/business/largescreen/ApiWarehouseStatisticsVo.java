package com.xinyirun.scm.bean.api.vo.business.largescreen;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/7/26 10:05
 */


@Data
public class ApiWarehouseStatisticsVo implements Serializable {

    private static final long serialVersionUID = -796758671385124332L;

    /**
     * 值
     */
    private BigDecimal value;

    private String name;

    private String warehouse_type;
}
