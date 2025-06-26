package com.xinyirun.scm.core.system.service.business.fund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.fund.BFundUsageEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundUsageVo;

/**
 * <p>
 * 资金使用情况表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
public interface IBFundUsageService extends IService<BFundUsageEntity> {

    /**
     * 查询资金使用情况（设置悲观锁）
     * @param enterpriseId-企业id
     * @param bankAccountId-银行账户id
     * @param bankAccountsTypeId-账款id
     * @param tradeNo-交易号
     */
    BFundUsageEntity selectUsageForUpdate(Integer enterpriseId, Integer bankAccountId, Integer bankAccountsTypeId, String tradeNo);

    /**
     * 查询资金使用情况
     */
    IPage<BFundUsageVo> selectPage(BFundUsageVo searchCondition);
}
