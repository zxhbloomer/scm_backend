package com.xinyirun.scm.core.system.config.event.listener;

import com.xinyirun.scm.bean.system.bo.fund.monit.in.FundInBo;
import com.xinyirun.scm.common.enums.fund.FundEventTypeEnum;
import com.xinyirun.scm.core.system.config.event.define.FundEvent;
import com.xinyirun.scm.core.system.service.base.v1.common.fund.ICommonFundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 资金事件监听器
 * @author system
 * @date 2025/06/17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FundEventListener {

    @Autowired
    private final ICommonFundService commonFundService;

    /**
     * 监听资金事件
     * @param event 资金事件
     */
    @EventListener
    public void onFundEvent(FundEvent event) {
        FundInBo fundInBo = event.getFundInBo();
        FundEventTypeEnum eventType = event.getEventType();
        
        log.info("接收到资金事件，事件类型: {}, 交易ID: {}, 交易类型: {}", 
                eventType.getCode(), fundInBo.getTrade_id(), fundInBo.getTrade_type());
        
        // 根据事件类型处理不同的业务逻辑
        switch (eventType) {
            case YUFU_PAY_INC:
                handleAdvancePaymentIncrease(fundInBo);
                break;
            case YUFU_PAY_DEC:
                handleAdvancePaymentDecrease(fundInBo);
                break;
            case YUFU_RTN_INC:
                handleAdvanceRefundIncrease(fundInBo);
                break;
            case YUFU_RTN_DEC:
                handleAdvanceRefundDecrease(fundInBo);
                break;
            default:
                log.warn("未知的资金事件类型: {}", eventType.getCode());
                break;
        }
        
        log.info("资金事件处理完成，事件类型: {}, 交易ID: {}", 
                eventType.getCode(), fundInBo.getTrade_id());
    }

    /**
     * 处理预付款金额增加
     * @param fundInBo 资金输入业务对象
     */
    private void handleAdvancePaymentIncrease(FundInBo fundInBo) {
        log.info("处理预付款金额增加，交易ID: {}", fundInBo.getTrade_id());
        commonFundService.increaseAdvancePayment(fundInBo);
    }

    /**
     * 处理预付款金额减少
     * @param fundInBo 资金输入业务对象
     */
    private void handleAdvancePaymentDecrease(FundInBo fundInBo) {
        log.info("处理预付款金额减少，交易ID: {}", fundInBo.getTrade_id());
        commonFundService.decreaseAdvancePayment(fundInBo);
    }

    /**
     * 处理预付款退回金额增加
     * @param fundInBo 资金输入业务对象
     */
    private void handleAdvanceRefundIncrease(FundInBo fundInBo) {
        log.info("处理预付款退回金额增加，交易ID: {}", fundInBo.getTrade_id());
        // TODO: 实现预付款退回金额增加的具体业务逻辑
    }

    /**
     * 处理预付款退回金额减少
     * @param fundInBo 资金输入业务对象
     */
    private void handleAdvanceRefundDecrease(FundInBo fundInBo) {
        log.info("处理预付款退回金额减少，交易ID: {}", fundInBo.getTrade_id());
        // TODO: 实现预付款退回金额减少的具体业务逻辑
    }
}
