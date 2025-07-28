package com.xinyirun.scm.bean.entity.business.monitor;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeEntityAnnotation;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("b_monitor_out")
@DataChangeEntityAnnotation(value="监管任务表_子表_out", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.monitor.DataChangeStrategyBMonitorOutEntityServiceImpl")
public class BMonitorOutEntity implements Serializable {

    private static final long serialVersionUID = -3871156734128275774L;
    /**
     * 主键id
     */
    @DataChangeLabelAnnotation("表id")
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
    @TableField("out_id")
    private Integer out_id;

    /**
     * 收货单id
     */
    @TableField("receive_id")
    private Integer receive_id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 是否集装箱
     */
    @TableField("is_container")
    private Boolean is_container;

    /**
     * 单据状态:0空车过磅，1正在装货，2重车出库，3装货完成
     */
    @DataChangeLabelAnnotation("单据状态:0空车过磅，1正在装货，2重车出库，3装货完成")
    @TableField("status")
    private String status;

    /**
     * 出库数量(吨)
     */
    @DataChangeLabelAnnotation("出库数量(吨)")
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 皮重
     */
    @TableField("tare_weight")
    @DataChangeLabelAnnotation("皮重")
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    @TableField("gross_weight")
    @DataChangeLabelAnnotation("毛重")
    private BigDecimal gross_weight;

    /**
     * 净重
     */
    @TableField("net_weight")
    @DataChangeLabelAnnotation("净重")
    private BigDecimal net_weight;

    /**
     * 车头照片附件id
     */
    @TableField("one_file")
    @DataChangeLabelAnnotation(value="司机车头照片附件", extension = "getAttachmentUrlExtension")
    private Integer one_file;

    /**
     * 车尾照片附件id
     */
    @TableField("two_file")
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    private Integer two_file;

    /**
     * 司机承诺书附件id
     */
    @TableField("three_file")
    @DataChangeLabelAnnotation(value="司机承诺书附件", extension = "getAttachmentUrlExtension")
    private Integer three_file;

    /**
     * 司机身份证id
     */
    @TableField("four_file")
    @DataChangeLabelAnnotation(value="司机身份证附件", extension = "getAttachmentUrlExtension")
    private Integer four_file;

    /**
     * 车头照片附件id
     */
    @TableField("five_file")
    @DataChangeLabelAnnotation(value="车头照片附件", extension = "getAttachmentUrlExtension")
    private Integer five_file;

    /**
     * 车尾照片附件id
     */
    @TableField("six_file")
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    private Integer six_file;

    /**
     * 车侧身附件id
     */
    @TableField("seven_file")
    @DataChangeLabelAnnotation(value="车侧身附件", extension = "getAttachmentUrlExtension")
    private Integer seven_file;

    /**
     * 装货视频附件id
     */
    @TableField("eight_file")
    @DataChangeLabelAnnotation(value="装货视频附件", extension = "getAttachmentUrlExtension")
    private Integer eight_file;

    /**
     * 车头照片附件id
     */
    @TableField("nine_file")
    @DataChangeLabelAnnotation(value="车头照片附件", extension = "getAttachmentUrlExtension")
    private Integer nine_file;

    /**
     * 车尾照片附件id
     */
    @TableField("ten_file")
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    private Integer ten_file;

    /**
     * 磅单附件id
     */
    @TableField("eleven_file")
    @DataChangeLabelAnnotation(value="磅单附件", extension = "getAttachmentUrlExtension")
    private Integer eleven_file;

    /**
     * 司机驾驶证附件id
     */
    @TableField("twelve_file")
    @DataChangeLabelAnnotation(value="司机驾驶证附件", extension = "getAttachmentUrlExtension")
    private Integer twelve_file;

    /**
     * 车辆行驶证附件id
     */
    @DataChangeLabelAnnotation(value="车辆行驶证附件", extension = "getAttachmentUrlExtension")
    @TableField("thirteen_file")
    private Integer thirteen_file;

    /**
     * 车厢情况照片id
     */
    @DataChangeLabelAnnotation(value="车厢情况照片", extension = "getAttachmentUrlExtension")
    @TableField("fourteen_file")
    private Integer fourteen_file;

    /**
     * 商品近照附件id
     */
    @DataChangeLabelAnnotation(value="商品近照附件", extension = "getAttachmentUrlExtension")
    @TableField("fifteen_file")
    private Integer fifteen_file;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 表明此属性不是数据库表的字段
     */
    @TableField(exist = false)
    private Boolean skipAutoFill = false;
}
