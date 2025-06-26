package com.xinyirun.scm.bean.system.bo.business.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author:      Wqf
 * Description: 预警是否发送信息
 * CreateTime : 2023/3/15 14:05
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BAlarmRulesBo implements Serializable {
    private static final long serialVersionUID = -5337461564367882209L;

    /**
     * 是否启用发送, false不发送
     */
    private Boolean is_using;

    /**
     * 发送人员
     */
    private Integer staff_id;

    /**
     * 显示方式, 0消息通知, 1弹窗显示，2用户密码过期
     */
    private String notice_type;
}
