package com.xinyirun.scm.bean.system.vo.business.alarm;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: MStaffPositionTransferVo
 * @Description: 预警组岗位员工VO
 * @Author: zxh
 * @date: 2020/1/10
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BAlarmStaffGroupTransferVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 4545611121745266445L;

    /**
     * 穿梭框：全部员工
     */
    List<BAlarmStaffTransferVo> staff_all;

    /**
     * 穿梭框：该岗位下，全部员工
     */
    Long [] staff_alarm;

    /**
     * 该岗位下，员工数量
     */
    int staff_alarm_count;
}
