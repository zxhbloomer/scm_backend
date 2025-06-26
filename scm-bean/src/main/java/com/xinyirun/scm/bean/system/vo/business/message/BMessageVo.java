package com.xinyirun.scm.bean.system.vo.business.message;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/3/22 14:35
 */

@Data
public class BMessageVo implements Serializable {

    private static final long serialVersionUID = 944153979263555281L;

    private int id;

    private String serial_code;

    private String serial_id;

    private String serial_type;

    private String msg;

    private String label;

    private LocalDateTime c_time;

    private Integer staff_id;

    private PageCondition pageCondition;

    private int inPlanCount;

    private int inCount;

    private int outPlanCount;

    private int outCount;

    private int monitorCount;

    private int count;

    private int monitorSyncErrorCount;

    /**
     * 监管任务 未审核预警
     */
    private int monitor_unaudited;

    /**
     * 中转港口预警
     */
    private int inventory_stagnation_warning;

    /**
     * 监管任务损耗预警
     */
    private int monitor_loss_warning;

    /**
     * 所有的预警数量
     */
    private int warning_count;
}
