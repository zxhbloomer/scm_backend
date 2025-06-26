package com.xinyirun.scm.bean.entity.master.vehicle;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 车辆管理
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_vehicle")
public class MVehicleEntity implements Serializable {

    private static final long serialVersionUID = -920269843222574090L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 车牌号
     */
    @TableField("no")
    private String no;

    /**
     * 车牌颜色
     */
    @TableField("no_color")
    private String no_color;

    /**
     * 验车日志
     */
    @TableField("validate_log")
    private String validate_log;

    /**
     * 1-验车成功 2-验车失败
     */
    @TableField("validate_status")
    private String validate_status;

    /**
     * 最后一次定位时间
     */
    @TableField("gps_time")
    private LocalDateTime gps_time;

    /**
     * 车长车型
     */
    @TableField("spec")
    private String spec;

    /**
     * 载重(吨)
     */
    @TableField("loading")
    private BigDecimal loading;

    /**
     * 驾驶证正面附件
     */
    @TableField("license_front")
    private Integer license_front;

    /**
     * 驾驶证反面附件
     */
    @TableField("license_back")
    private Integer license_back;

    /**
     * 是否删除
     */
    @TableField("is_del")
    private Boolean is_del;

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
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

}
