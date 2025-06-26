package com.xinyirun.scm.bean.system.vo.mongo.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class BMonitorTrackMongoDataVo implements Serializable {

    private static final long serialVersionUID = 4168685743845105941L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 物流单号
     */
    private String waybill_no;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime end_time;

    /**
     * 车牌颜色(1 蓝色、2 黄色、3 黄绿色)
     */
    private String color;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 轨迹内容
     */
    private String content;

}
