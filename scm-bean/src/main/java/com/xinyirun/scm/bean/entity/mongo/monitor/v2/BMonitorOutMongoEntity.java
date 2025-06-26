package com.xinyirun.scm.bean.entity.mongo.monitor.v2;

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
 * 监管任务_出库
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Document("b_monitor_out_v2")
public class BMonitorOutMongoEntity implements Serializable {

    private static final long serialVersionUID = -5328121974439392219L;

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
    private Integer out_id;

    /**
     * 类型
     */
    private String type;

    /**
     * 是否集装箱
     */
    private Boolean is_container;

    /**
     * 单据状态:0空车过磅，1正在装货，2重车出库，3装货完成
     */
    private String status;

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
     * 车头照片附件id
     */
    private Integer one_file;

    /**
     * 车尾照片附件id
     */
    private Integer two_file;

    /**
     * 司机承诺书附件id
     */
    private Integer three_file;

    /**
     * 司机身份证id
     */
    private Integer four_file;

    /**
     * 车头照片附件id
     */
    private Integer five_file;

    /**
     * 车尾照片附件id
     */
    private Integer six_file;

    /**
     * 车侧身附件id
     */
    private Integer seven_file;

    /**
     * 装货视频附件id
     */
    private Integer eight_file;

    /**
     * 车头照片附件id
     */
    private Integer nine_file;

    /**
     * 车尾照片附件id
     */
    private Integer ten_file;

    /**
     * 磅单附件id
     */
    private Integer eleven_file;

    /**
     * 司机驾驶证附件id
     */
    private Integer twelve_file;

    /**
     * 车辆行驶证附件id
     */
    private Integer thirteen_file;

    /**
     * 车厢情况照片id
     */
    private Integer fourteen_file;

    /**
     * 商品近照附件id
     */
    private Integer fifteen_file;

    /**
     * 数据版本，乐观锁使用
     */
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
