package com.xinyirun.scm.core.system.config.event.servcie;

import com.xinyirun.scm.bean.system.bo.fund.monit.in.FundInBo;
import com.xinyirun.scm.common.enums.fund.FundEventTypeEnum;
import com.xinyirun.scm.core.system.config.event.define.FundEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * 资金服务
 * @author system
 * @date 2025/06/17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FundService {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 预付款金额增加
     * 预付款类型，付款单操作付款凭证上传完成付款，预付款已付款金额增加
     * @param id ap_pay_id；付款单id
     */
    public void increaseAdvanceAmount(Integer id) {
        log.info("预付款金额增加事件开始处理，付款单ID: {}", id);
        
        FundInBo fundInBo = FundInBo.builder()
                .trade_id(id)
                .trade_type("b_ap_pay")
                .fund_event(FundEventTypeEnum.YUFU_PAY_INC.getCode())
                .build();
        
        eventPublisher.publishEvent(new FundEvent(this, fundInBo, FundEventTypeEnum.YUFU_PAY_INC));
        
        log.info("预付款金额增加事件发布成功，付款单ID: {}, 事件类型: {}", id, FundEventTypeEnum.YUFU_PAY_INC.getCode());
    }    /**
     * 预付款金额减少
     * 预付款类型，付款单操作（已付款）付款单作废，预付款已付款金额减少
     * @param id ap_pay_id；付款单id
     */
    public void decreaseAdvanceAmount(Integer id) {
        log.info("预付款金额减少事件开始处理，付款单ID: {}", id);
        
        FundInBo fundInBo = FundInBo.builder()
                .trade_id(id)
                .trade_type("b_ap_pay")
                .fund_event(FundEventTypeEnum.YUFU_PAY_DEC.getCode())
                .build();
        
        eventPublisher.publishEvent(new FundEvent(this, fundInBo, FundEventTypeEnum.YUFU_PAY_DEC));
        
        log.info("预付款金额减少事件发布成功，付款单ID: {}, 事件类型: {}", id, FundEventTypeEnum.YUFU_PAY_DEC.getCode());
    }
}
