package com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2;

import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.BPreviewBackupDataV2Vo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 待备份数据, 查询参数
 * @CreateTime : 2023/3/29 11:20
 */

@Data
public class BBkMonitorVo implements Serializable {

    private static final long serialVersionUID = -8097861190804973271L;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 监管任务id
     */
    private Integer id;


    /**
     * 任务单号
     */
    private String code;


    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 总条数
     */
    private Long count;

    private Integer log_id;

    /**
     * 备份类型, 1备份, 2恢复
     */
    private String type;

    private String flag;

    private String exception;

    private List<String> ids;

    private Long staff_id;

    /**
     * 结算状态
     */
    private String settlement_status;

    /**
     * 审核状态
     */
    private String audit_status;

    /**
     * 预览数据
     */
    private List<BPreviewBackupDataV2Vo> preview_data;

}
