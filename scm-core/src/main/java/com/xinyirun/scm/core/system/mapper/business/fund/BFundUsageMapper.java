package com.xinyirun.scm.core.system.mapper.business.fund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.fund.BFundUsageEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundUsageVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 资金使用情况表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
public interface BFundUsageMapper extends BaseMapper<BFundUsageEntity> {

    /**
     * 根据条件查询资金使用情况
     * @param enterpriseId 企业ID
     * @param bankAccountId 银行账户ID
     * @param bankAccountsTypeId 银行账户类型ID
     * @param type 类型
     * @return 资金使用情况VO
     */
    @Select("SELECT * FROM b_fund_usage " +
            "WHERE enterprise_id = #{enterpriseId} " +
            "AND bank_account_id = #{bankAccountId} " +
            "AND type = #{type}")
    BFundUsageVo selectByCondition(@Param("enterpriseId") Integer enterpriseId,
                                   @Param("bankAccountId") Integer bankAccountId,
                                   @Param("type") String type);

}
