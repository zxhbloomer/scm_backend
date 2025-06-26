package com.xinyirun.scm.bean.system.vo.workbench;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 事项
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpmRemindVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -5955215758869154746L;

    /** 待办流程数量 */
    private Integer pendingQty;
    /** 超1天 */
    private Integer overOneDay;
    private Integer overOneDaypercentage;
    /** 超2天 */
    private Integer overTwoDay;
    private Integer overTwoDaypercentage;
    /** 超3天 */
    private Integer overThreeDay;
    private Integer overThreeDaypercentage;
    /** 超3天 */
    private Integer overOneWeek;
    private Integer overOneWeekpercentage;

    /** 用户code */
    private String staffCode;
}
