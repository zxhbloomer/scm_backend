package com.xinyirun.scm.bean.system.vo.report.largescreen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: Wqf
 * @Description: 加工稻谷入库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsMonitorLargeScreenVo implements Serializable {

    private static final long serialVersionUID = 6407365645665690190L;

    private int no;

    /**
     * 任务号
     */
    private String code;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 状态
     */
    private String status_name;
}
