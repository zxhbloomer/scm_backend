package com.xinyirun.scm.bean.system.bo.business.bpm;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BBpmNoticeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1875042684175892856L;
    /**
     * 是否启用发送, false不发送
     */
    private Boolean is_using;

    /**
     * 发送人员
     */
    private Integer staff_id;

    /**
     * 显示方式, 0消息通知, 1弹窗显示
     */
    private String notice_type;
}
