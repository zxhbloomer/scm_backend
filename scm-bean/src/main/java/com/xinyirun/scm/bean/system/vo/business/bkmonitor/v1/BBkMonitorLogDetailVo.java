package com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 待备份数据, 发送mq参数
 * @CreateTime : 2023/3/29 11:20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BBkMonitorLogDetailVo implements Serializable {


    private static final long serialVersionUID = 5613264922274328849L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * b_monitor表id
     */
    private Integer monitor_id;

    private String monitor_code;

    /**
     * b_monitor_delivery表id
     */
    private Integer monitor_delivery_id;

    /**
     * b_monitor_in表id
     */
    private Integer monitor_in_id;

    /**
     * b_monitor_out表id
     */
    private Integer monitor_out_id;

    /**
     * b_monitor_unload表id
     */
    private Integer monitor_unload_id;

    private Integer log_id;

    private Integer log_detail_id;

    private PageCondition pageCondition;



    private String flag;

    private String status;

    private String status_name;

    /**
     * 1备份, 2恢复
     */
    private String type_name;

    private String type;

    /**
     * 版本
     */
    private String version;

    /**
     * 最后一次备份人, 恢复人
     */
    private String last_backup_name
            , last_restore_name;

    /**
     * 最后一次恢复人, 恢复时间
     */
    private LocalDateTime last_backup_time
            , last_restore_time;

    public BBkMonitorLogDetailVo(Integer monitor_id, Integer monitor_delivery_id, Integer monitor_in_id,
                                 Integer monitor_out_id, Integer monitor_unload_id, String monitor_code) {
        this.monitor_id = monitor_id;
        this.monitor_delivery_id = monitor_delivery_id;
        this.monitor_in_id = monitor_in_id;
        this.monitor_unload_id = monitor_unload_id;
        this.monitor_out_id = monitor_out_id;
        this.monitor_code = monitor_code;
    }
}
