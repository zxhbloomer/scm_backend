package com.xinyirun.scm.bean.entity.mongo.monitor.v1;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("b_monitor_in")
public class BMonitorInMongoEntity implements Serializable {

    private static final long serialVersionUID = -6502651062589229621L;

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 数据库id
     */
    private Integer mysql_id;

    /**
     * 监管任务主表id
     */
    private Integer monitor_id;

    /**
     * 入库单id
     */
    private Integer in_id;

    /**
     * 类型
     */
    private String type;

    /**
     * 单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 是否集装箱
     */
    private Boolean is_container;


    /**
     * 出库数量(吨)
     */
    private BigDecimal qty;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 净重
     */
    private BigDecimal net_weight;

    /**
     * 车头车尾带司机id
     */
    private Integer one_file;

    /**
     * 重车过磅附件id
     */
    private Integer two_file;

    /**
     * 卸货照片附件id
     */
    private Integer three_file;

    /**
     * 卸货视频附件id
     */
    private Integer four_file;

    /**
     * 车头车尾带司机id
     */
    private Integer five_file;

    /**
     * 磅单(司机签字)附件id
     */
    private Integer six_file;

    /**
     * 车头附件id
     */
    private Integer seven_file;


    /**
     * 车尾附件id
     */
    private Integer eight_file;


    /**
     * 磅单附件id
     */
    private Integer nine_file;

    /**
     * 行车轨迹附件id
     */
    private Integer ten_file;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    private Integer dbversion;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;
}
