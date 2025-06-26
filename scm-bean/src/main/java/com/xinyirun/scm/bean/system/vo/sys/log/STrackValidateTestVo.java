package com.xinyirun.scm.bean.system.vo.sys.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验车 和 轨迹
 *
 * @author zxh
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class STrackValidateTestVo implements Serializable {

    private static final long serialVersionUID = -3041300599432483529L;


    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 车辆颜色
     */
    private String vehicle_color;

    /**
     * 服务商 1-腾颢 2-好伙伴
     */
    private String service_type;

    /**
     * 开始时间
     */
    private LocalDateTime start_date;

    /**
     * 结束时间
     */
    private LocalDateTime end_date;

    /**
     * 结果
     */
    private String result;


}
