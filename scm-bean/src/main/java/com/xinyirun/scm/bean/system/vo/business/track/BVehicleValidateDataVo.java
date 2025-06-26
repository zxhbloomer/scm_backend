package com.xinyirun.scm.bean.system.vo.business.track;

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
public class BVehicleValidateDataVo implements Serializable {

    private static final long serialVersionUID = -3326944694030313314L;

    /**
     * 是否入网
     */
    private Boolean flag;

    /**
     * 最后定位时间，flag为false时为空
     */
    private LocalDateTime gpsTime;

}
