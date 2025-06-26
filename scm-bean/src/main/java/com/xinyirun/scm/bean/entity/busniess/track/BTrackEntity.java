package com.xinyirun.scm.bean.entity.busniess.track;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_track")
public class BTrackEntity implements Serializable {

    private static final long serialVersionUID = -2922076148001736956L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 车牌号
     */
    @TableField("vehicle_no")
    private String vehicle_no;

    /**
     * 物流单号
     */
    @TableField("waybill_no")
    private String waybill_no;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime end_time;

    /**
     * 车牌颜色(1 蓝色、2 黄色、3 黄绿色)
     */
    @TableField("color")
    private String color;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 轨迹内容
     */
    @TableField("content")
    private String content;


}
