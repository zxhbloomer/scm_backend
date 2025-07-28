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
@DataChangeEntityAnnotation(value="监管任务表_子表_in", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.monitor.DataChangeStrategyBMonitorInEntityServiceImpl")
public class BMonitorInEntity implements Serializable {

    private static final long serialVersionUID = 7711777676543125044L;
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
    @TableField("in_id")
    private Integer in_id;


    /**
     * 监管任务主表id
     */
    @TableField("delivery_id")
    private Integer delivery_id;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    @DataChangeLabelAnnotation("单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成")
    @TableField("status")
    private String status;

    /**
     * 是否集装箱
     */
    @TableField("is_container")
    private Boolean is_container;


    /**
     * 出库数量(吨)，datachange不需要显示
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 皮重
     */
    @DataChangeLabelAnnotation("皮重")
    @TableField("tare_weight")
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    @DataChangeLabelAnnotation("毛重")
    @TableField("gross_weight")
    private BigDecimal gross_weight;

    /**
     * 净重
     */
    @DataChangeLabelAnnotation("净重")
    @TableField("net_weight")
    private BigDecimal net_weight;

    /**
     * 司机车头照片附件
     */
    @TableField("one_file")
    @DataChangeLabelAnnotation(value="司机车头照片附件", extension = "getAttachmentUrlExtension")
    private Integer one_file;

    /**
     * 司机车尾照片附件id
     */
    @TableField("two_file")
    @DataChangeLabelAnnotation(value="司机车尾照片附件", extension = "getAttachmentUrlExtension")
    private Integer two_file;

    /**
     * 车头照片附件id
     */
    @TableField("three_file")
    @DataChangeLabelAnnotation(value="车头照片附件", extension = "getAttachmentUrlExtension")
    private Integer three_file;

    /**
     * 车尾照片附件id
     */
    @TableField("four_file")
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    private Integer four_file;

    /**
     * 车侧身照片附件id
     */
    @TableField("five_file")
    @DataChangeLabelAnnotation(value="车侧身照片附件", extension = "getAttachmentUrlExtension")
    private Integer five_file;

    /**
     * 卸货视频附件id
     */
    @TableField("six_file")
    @DataChangeLabelAnnotation(value="卸货视频附件", extension = "getAttachmentUrlExtension")
    private Integer six_file;

    /**
     * 司机车头附件id
     */
    @TableField("seven_file")
    @DataChangeLabelAnnotation(value="司机车头附件", extension = "getAttachmentUrlExtension")
    private Integer seven_file;


    /**
     * 司机车尾附件id
     */
    @TableField("eight_file")
    @DataChangeLabelAnnotation(value="司机车尾附件", extension = "getAttachmentUrlExtension")
    private Integer eight_file;


    /**
     * 磅单附件id
     */
    @TableField("nine_file")
    @DataChangeLabelAnnotation(value="磅单附件", extension = "getAttachmentUrlExtension")
    private Integer nine_file;

    /**
     * 行车轨迹附件id
     */
    @TableField("ten_file")
    @DataChangeLabelAnnotation(value="行车轨迹附件", extension = "getAttachmentUrlExtension")
    private Integer ten_file;

    /**
     * 司机行驶证ID
     */
    @TableField("eleven_file")
    @DataChangeLabelAnnotation(value="司机行驶证附件", extension = "getAttachmentUrlExtension")
    private Integer eleven_file;

    /**
     * 商品近照附件id
     */
    @DataChangeLabelAnnotation(value="商品近照附件", extension = "getAttachmentUrlExtension")
    @TableField("twelve_file")
    private Integer twelve_file;

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
