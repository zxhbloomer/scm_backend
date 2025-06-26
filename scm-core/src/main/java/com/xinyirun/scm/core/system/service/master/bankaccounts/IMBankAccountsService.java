package com.xinyirun.scm.core.system.service.master.bankaccounts;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsExportVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsTypeVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsVo;

import java.util.List;

/**
 * <p>
 * 企业银行账户表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
public interface IMBankAccountsService extends IService<MBankAccountsEntity> {

    /**
     * 分页查询
     */
    IPage<MBankAccountsVo> selectPage(MBankAccountsVo searchCondition);

    /**
     * id查询
     */
    MBankAccountsVo selectById(Integer id);

    /**
     * 企业银行账户，校验数据
     */
    CheckResultAo checkLogic(MBankAccountsVo searchCondition, String checkType);

    /**
     * 企业银行账户，新增
     */
    InsertResultAo<MBankAccountsVo> insert(MBankAccountsVo searchCondition);

    /**
     * 企业银行账户 更新
     */
    UpdateResultAo<MBankAccountsVo> update(MBankAccountsVo searchCondition);

    /**
     * 企业银行账户 删除
     */
    DeleteResultAo<Integer> delete(List<MBankAccountsVo> searchCondition);

    /**
     * 启用银行账户 状态修改
     */
    UpdateResultAo<Integer> updateStatus(List<MBankAccountsVo> searchCondition);

    /**
     * 企业银行账户 导出
     */
    List<MBankAccountsExportVo> selectExportList(MBankAccountsVo searchCondition);

    /**
     * 获取企业银行默认账户
     */
    MBankAccountsVo getPurchaser(MBankAccountsVo searchCondition);

    /**
     * 企业银行账户，弹窗获取分页列表
     */
    IPage<MBankAccountsVo> dialogpageList(MBankAccountsVo searchCondition);

    /**
     * 获取款项类型
     */
    List<MBankAccountsTypeVo> getBankType(MBankAccountsTypeVo searchCondition);

    /**
     * 获取银行收款账户下拉
     */
    List<MBankAccountsVo> getBankCollection(MBankAccountsVo searchCondition);
}
