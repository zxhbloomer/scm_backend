package com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v1;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务_入库
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_monitor_in")
public class BMonitorInRestoreEntity implements Serializable {

    private static final long serialVersionUID = 4163672438923231434L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 监管任务主表id
     */
    @TableField("monitor_id")
    private Integer monitor_id;

    /**
     * 入库单id
     */
    @TableField("in_id")
    private Integer in_id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    @TableField("status")
    private String status;

    /**
     * 是否集装箱
     */
    @TableField("is_container")
    private Boolean is_container;


    /**
     * 出库数量(吨)
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 皮重
     */
    @TableField("tare_weight")
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    @TableField("gross_weight")
    private BigDecimal gross_weight;

    /**
     * 净重
     */
    @TableField("net_weight")
    private BigDecimal net_weight;

    /**
     * 车头车尾带司机id
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 重车过磅附件id
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 卸货照片附件id
     */
    @TableField("three_file")
    private Integer three_file;

    /**
     * 卸货视频附件id
     */
    @TableField("four_file")
    private Integer four_file;

    /**
     * 车头车尾带司机id
     */
    @TableField("five_file")
    private Integer five_file;

    /**
     * 磅单(司机签字)附件id
     */
    @TableField("six_file")
    private Integer six_file;

    /**
     * 车头附件id
     */
    @TableField("seven_file")
    private Integer seven_file;


    /**
     * 车尾附件id
     */
    @TableField("eight_file")
    private Integer eight_file;


    /**
     * 磅单附件id
     */
    @TableField("nine_file")
    private Integer nine_file;

    /**
     * 行车轨迹附件id
     */
    @TableField("ten_file")
    private Integer ten_file;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 创建时间
     */
    @TableField(value="c_time")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id")
    private Long u_id;
}
