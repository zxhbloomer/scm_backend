package com.xinyirun.scm.core.system.service.base.v1.common.fund;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.fund.BFundUsageEntity;
import com.xinyirun.scm.bean.system.bo.fund.monit.in.FundInBo;

/**
 * 资金使用情况
 *
 * @author
 */
public interface ICommonFundService extends IService<BFundUsageEntity> {

    /**
     * 新增一条流水，处理预付款金额增加，操作：付款单操作付款凭证上传完成付款，预付款已付款金额增加
     * @param fundInBo 资金输入业务对象
     */
    void increaseAdvancePayment(FundInBo fundInBo);

    /**
     * 新增一条流水，处理预付款金额减少，业务操作：已付款的付款单操作作废凭证，预付款作废收付金额增加
     * @param fundInBo 资金输入业务对象
     */
    void decreaseAdvancePayment(FundInBo fundInBo);

}