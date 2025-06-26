package com.xinyirun.scm.bean.system.vo.business.order;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: xtj
 * @Description:
 * @CreateTime : 2024/8/12 14:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOrderInvertedVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5044316500896406984L;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 日期
     */
    private String[] badgeDate;


    /**
     * 开库日期
     */
    private String[] auctionDateList;

    private LocalDateTime date;

    /**
     * 竞拍日期
     */
    private String auction_date;

    /**
     * 开库日期
     */
    private LocalDateTime opening_date;

    /**
     * 采购合同号
     */
    private String contract_no;

    /**
     * 出库到期日
     */
    private LocalDateTime delivery_due_date;

    /**
     * 实际储存库点
     */
    private String warehouse_name;

    /**
     * 合同量
     */
    private BigDecimal contract_quantity;

    /**
     * 实际应出库数量（扣除升贴水数量）
     */
    private BigDecimal actual_quantity;

    /**
     * 剩余数量
     */
    private BigDecimal remaining_quantity;

    /**
     * 实际日出库量
     */
    private BigDecimal actual_daily_quantity;

    /**
     * 累计出库量
     */
    private BigDecimal accumulated_out_quantity;

    /**
     * 计划出库天数
     */
    private String plan_out_days;

    /**
     * 导出code
     */
    private String export_code;

    private String[] export_codes;

    /**
     * 实际出库耗用天数
     */
    private String actual_plan_out_days;

    /**
     * 日出库计划
     */
    private BigDecimal plan_out_day;

    /**
     * 出库进度
     */
    private BigDecimal plan_out_speed;

    /**
     * 备份日期（记录前一天时间）
     */
    private LocalDateTime backups_date;

    private Integer warehouse_id;

    /**
     * 开库开始日期
     */
    private LocalDateTime opening_date_start;

    /**
     * 开库结束日期
     */
    private LocalDateTime opening_date_end;


}
