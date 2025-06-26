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
public class BpmMatterVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6684920289353088618L;
    /** 总数统计 - 待办流程数量 */
    private Integer pendingProcessQty;
    /** 今日总数统计 - 待办流程数量 */
    private Integer todayPendingProcessQty;
    /** 总数统计 - 已处理数量*/
    private Integer processedQty;
    /** 今日总数统计 - 已处理数量*/
    private Integer todayProcessedQty;
    /** 总数统计 - 已发起数量 */
    private Integer initiatedQty;
    /** 今日总数统计 - 已发起数量 */
    private Integer todayInitiatedQty;
    /** 今日总数统计 - 更新 */
    private Integer todayUpdateInitiatedQty;
    /** 总数统计 - 我收到的数量 */
    private Integer receivedQty;
    /**今日 总数统计 - 我收到的数量 */
    private Integer todayReceivedQty;

    /** 用户code */
    private String staffCode;
}
