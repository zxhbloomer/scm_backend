package com.xinyirun.scm.core.system.serviceimpl.business.fund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.fund.BFundUsageEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundUsageVo;
import com.xinyirun.scm.core.system.mapper.business.fund.BFundUsageMapper;
import com.xinyirun.scm.core.system.service.business.fund.IBFundUsageService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资金使用情况表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Service
public class BFundUsageServiceImpl extends BaseServiceImpl<BFundUsageMapper, BFundUsageEntity> implements IBFundUsageService {

    @Autowired
    private BFundUsageMapper mapper;

    /**
     * 查询资金使用情况（设置悲观锁）
     * @param enterpriseId-企业id
     * @param bankAccountId-银行账户id
     * @param bankAccountsTypeId-账款id
     * @param tradeNo-交易号
     */
    @Override
    public BFundUsageEntity selectUsageForUpdate(Integer enterpriseId, Integer bankAccountId, Integer bankAccountsTypeId, String tradeNo) {
//        return mapper.selectUsageForUpdate(enterpriseId, bankAccountId, bankAccountsTypeId, tradeNo);
        return null;
    }

    /**
     * 列表查询
     * @param searchCondition
     */
    @Override
    public IPage<BFundUsageVo> selectPage(BFundUsageVo searchCondition) {
        // 分页条件
        Page<BFundUsageVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return null;
    }
}
