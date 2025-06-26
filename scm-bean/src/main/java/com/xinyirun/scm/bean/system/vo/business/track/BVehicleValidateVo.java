package com.xinyirun.scm.bean.system.vo.business.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class BVehicleValidateVo implements Serializable {

    private static final long serialVersionUID = 2406836732830861550L;

    /**
     * 返回状态
     */
    private String success;

    private BVehicleValidateDataVo data;

    private String info;

    private String msg;

    /**
     * 验车日志
     */
    private String validate_log;
}
