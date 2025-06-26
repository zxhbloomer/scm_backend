package com.xinyirun.scm.bean.entity.busniess.monitor;

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
 * 监管任务_卸货
 *
 * @author wwl
 * @since 2022-02-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_monitor_unload")
@DataChangeEntityAnnotation(value="监管任务表_子表_unload", type = "com.xinyirun.scm.core.system.serviceimpl.log.datachange.monitor.DataChangeStrategyBMonitorUnloadEntityServiceImpl")
public class BMonitorUnloadEntity implements Serializable {

    private static final long serialVersionUID = -1549528323282406862L;

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
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    @TableField("status")
    @DataChangeLabelAnnotation("单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成")
    private String status;

    /**
     * 是否集装箱
     */
    @TableField("is_container")
    private Boolean is_container;


    /**
     * 出库数量(吨)
     */
    @DataChangeLabelAnnotation("出库数量(吨)")
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
     * 车头车尾带司机id
     */
    @DataChangeLabelAnnotation(value="司机车头照片附件", extension = "getAttachmentUrlExtension")
    @TableField("one_file")
    private Integer one_file;

    /**
     * 重车过磅附件id
     */
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    @TableField("two_file")
    private Integer two_file;

    /**
     * 卸货照片附件id
     */
    @DataChangeLabelAnnotation(value="司机承诺书附件", extension = "getAttachmentUrlExtension")
    @TableField("three_file")
    private Integer three_file;

    /**
     * 卸货视频附件id
     */
    @DataChangeLabelAnnotation(value="司机身份证附件", extension = "getAttachmentUrlExtension")
    @TableField("four_file")
    private Integer four_file;

    /**
     * 车头车尾带司机id
     */
    @DataChangeLabelAnnotation(value="车头照片附件", extension = "getAttachmentUrlExtension")
    @TableField("five_file")
    private Integer five_file;

    /**
     * 磅单(司机签字)附件id
     */
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    @TableField("six_file")
    private Integer six_file;

    /**
     * 车头附件id
     */
    @DataChangeLabelAnnotation(value="车侧身附件", extension = "getAttachmentUrlExtension")
    @TableField("seven_file")
    private Integer seven_file;


    /**
     * 车尾附件id
     */
    @DataChangeLabelAnnotation(value="装货视频附件", extension = "getAttachmentUrlExtension")
    @TableField("eight_file")
    private Integer eight_file;


    /**
     * 磅单附件id
     */
    @DataChangeLabelAnnotation(value="车头照片附件", extension = "getAttachmentUrlExtension")
    @TableField("nine_file")
    private Integer nine_file;

    /**
     * 行车轨迹附件id
     */
    @DataChangeLabelAnnotation(value="车尾照片附件", extension = "getAttachmentUrlExtension")
    @TableField("ten_file")
    private Integer ten_file;

    /**
     * 司机驾驶证ID
     */
    @TableField("eleven_file")
    @DataChangeLabelAnnotation(value="司机驾驶证附件", extension = "getAttachmentUrlExtension")
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
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 表明此属性不是数据库表的字段
     */
    @TableField(exist = false)
    private Boolean skipAutoFill = false;
}
