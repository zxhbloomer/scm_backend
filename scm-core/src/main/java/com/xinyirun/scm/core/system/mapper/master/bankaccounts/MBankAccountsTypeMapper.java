package com.xinyirun.scm.core.system.mapper.master.bankaccounts;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsTypeEntity;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsTypeVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 款项类型 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
public interface MBankAccountsTypeMapper extends BaseMapper<MBankAccountsTypeEntity> {

    /**
     * 获取款项类型
     */
    @Select("select * from m_bank_accounts_type where bank_id = #{bank_id}")
    List<MBankAccountsTypeVo> getBankType(@Param("bank_id") Integer bank_id);
}
