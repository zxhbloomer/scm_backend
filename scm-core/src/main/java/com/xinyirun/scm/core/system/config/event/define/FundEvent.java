package com.xinyirun.scm.core.system.config.event.define;

import com.xinyirun.scm.bean.system.bo.fund.monit.in.FundInBo;
import com.xinyirun.scm.common.enums.fund.FundEventTypeEnum;
import org.springframework.context.ApplicationEvent;

/**
 * 资金事件
 * @author system
 * @date 2025/06/17
 */
public class FundEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 8929893689628625795L;

    /** 资金输入业务对象 */
    private FundInBo fundInBo;
    
    /** 事件类型 */
    private FundEventTypeEnum eventType;    /**

     * 构造函数
     * @param source 事件源
     * @param bo 资金输入业务对象
     * @param eventType 事件类型
     */
    public FundEvent(Object source, FundInBo bo, FundEventTypeEnum eventType) {
        super(source);
        this.fundInBo = bo;
        this.eventType = eventType;
    }

    public FundInBo getFundInBo() {
        return fundInBo;
    }

    public FundEventTypeEnum getEventType() {
        return eventType;
    }
}
