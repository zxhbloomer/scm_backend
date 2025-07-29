package com.xinyirun.scm.core.system.serviceimpl.master.bankaccounts;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsEntity;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsPurposeEntity;
import com.xinyirun.scm.bean.entity.master.bank.MBankAccountsTypeEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsExportVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsTypeVo;
import com.xinyirun.scm.bean.system.vo.master.bankaccounts.MBankAccountsVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.bankaccounts.MBankAccountsMapper;
import com.xinyirun.scm.core.system.mapper.master.bankaccounts.MBankAccountsTypeMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.service.master.bankaccounts.IMBankAccountsService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 企业银行账户表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-24
 */
@Service
public class MBankAccountsServiceImpl extends BaseServiceImpl<MBankAccountsMapper, MBankAccountsEntity> implements IMBankAccountsService {

    @Autowired
    private MBankAccountsMapper mapper;

    @Autowired
    private SDictDataMapper sDictDataMapper;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private MBankAccountsTypeMapper mBankAccountsTypeMapper;

    /**
     * 分页查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<MBankAccountsVo> selectPage(MBankAccountsVo searchCondition) {
        // 分页条件
        Page<MBankAccountsVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * id查询
     * @param id
     */
    @Override
    public MBankAccountsVo selectById(Integer id) {
        MBankAccountsVo vo = mapper.selById(id);
        // 查询账户类型
        List<MBankAccountsTypeVo> bankTypes = mBankAccountsTypeMapper.getBankType(id);
        if (CollectionUtil.isNotEmpty(bankTypes)) {
            String[] bankTypeCodes = bankTypes.stream().map(MBankAccountsTypeVo::getCode).toArray(String[]::new);
            vo.setBank_type(bankTypeCodes);
        }
        return vo;
    }

    /**
     * 企业银行账户，校验数据
     * @param vo
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(MBankAccountsVo vo, String checkType) {
        List<MBankAccountsVo> mBankAccountsVos = mapper.validateDuplicateCode(vo);

        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (CollectionUtil.isNotEmpty(mBankAccountsVos)) {
                    return CheckResultUtil.NG("校验出错：您输入的编码[" + vo.getCode() + "]已存在，请重新输入。", mBankAccountsVos);
                }
                
                // 校验bank_type是否为空
                if (ObjectUtil.isEmpty(vo.getBank_type()) || vo.getBank_type().length == 0) {
                    return CheckResultUtil.NG("请至少选择一个账户类型");
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (CollectionUtil.isNotEmpty(mBankAccountsVos)) {
                    return CheckResultUtil.NG("校验出错：您输入的编码[" + vo.getCode() + "]已存在，请重新输入。", mBankAccountsVos);
                }
                
                // 校验bank_type是否为空
                if (ObjectUtil.isEmpty(vo.getBank_type()) || vo.getBank_type().length == 0) {
                    return CheckResultUtil.NG("请至少选择一个账户类型");
                }
                
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:

                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 企业银行账户，新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<MBankAccountsVo> insert(MBankAccountsVo searchCondition) {
        // 插入前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.插入企业账户
        MBankAccountsEntity mBankAccountsEntity = new MBankAccountsEntity();
        BeanUtils.copyProperties(searchCondition,mBankAccountsEntity);

        // 2.设置主体企业默认账户,其他的账户修改为非默认
        if (ObjectUtil.isNotEmpty(searchCondition.getIs_default()) && searchCondition.getIs_default() == 1) {
            MBankAccountsVo mBankAccountsVo = mapper.selByEnterpriseIdAndStatus(searchCondition.getEnterprise_id());
            if (ObjectUtil.isNotEmpty(mBankAccountsVo)) {
                MBankAccountsEntity mBankAccounts = new MBankAccountsEntity();
                BeanUtils.copyProperties(mBankAccountsVo,mBankAccounts);
                mBankAccounts.setIs_default(0);
                mapper.updateById(mBankAccounts);
            }
        }

        int insert =  mapper.insert(mBankAccountsEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 3.增加账户类型数据到m_bank_accounts_type表
        if (ObjectUtil.isNotEmpty(searchCondition.getBank_type())) {
            for (String bankType : searchCondition.getBank_type()) {
                // 根据bank_type查询字典数据获取name
                SDictDataVo dictData = sDictDataMapper.getDetailByCodeAndDictValue(DictConstant.DICT_M_BANK_TYPE, bankType);
                String typeName = ObjectUtil.isNotEmpty(dictData) ? dictData.getLabel() : bankType;
                
                // 插入m_bank_accounts_type记录
                MBankAccountsTypeEntity typeEntity = new MBankAccountsTypeEntity();
                typeEntity.setBank_id(mBankAccountsEntity.getId());
                typeEntity.setCode(bankType);
                typeEntity.setName(typeName);
                typeEntity.setStatus("1");
                
                mBankAccountsTypeMapper.insert(typeEntity);
            }
        }

        searchCondition.setId(mBankAccountsEntity.getId());
        return InsertResultUtil.OK(searchCondition);
    }


    /**
     * 企业银行账户，修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<MBankAccountsVo> update(MBankAccountsVo searchCondition) {
        // 插入前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.修改
        MBankAccountsEntity mBankAccountsEntity = new MBankAccountsEntity();
        BeanUtils.copyProperties(searchCondition,mBankAccountsEntity);

        // 2.设置主体企业默认账户,其他的账户修改为非默认
        if (ObjectUtil.isNotEmpty(searchCondition.getIs_default()) && searchCondition.getIs_default() == 1) {
            MBankAccountsVo mBankAccountsVo = mapper.selByEnterpriseIdAndStatus(searchCondition.getEnterprise_id());
            if (ObjectUtil.isNotEmpty(mBankAccountsVo)) {
                MBankAccountsEntity mBankAccounts = new MBankAccountsEntity();
                BeanUtils.copyProperties(mBankAccountsVo,mBankAccounts);
                mBankAccounts.setIs_default(0);
                mapper.updateById(mBankAccounts);
            }
        }

        int insert =  mapper.updateById(mBankAccountsEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 3.更新账户类型数据到m_bank_accounts_type表
        if (ObjectUtil.isNotEmpty(searchCondition.getBank_type())) {
            // 先删除已有的账户类型记录
            mBankAccountsTypeMapper.delete(new QueryWrapper<MBankAccountsTypeEntity>()
                    .eq("bank_id", mBankAccountsEntity.getId()));
            
            // 重新插入账户类型记录
            for (String bankType : searchCondition.getBank_type()) {
                // 根据bank_type查询字典数据获取name
                SDictDataVo dictData = sDictDataMapper.getDetailByCodeAndDictValue(DictConstant.DICT_M_BANK_TYPE, bankType);
                String typeName = ObjectUtil.isNotEmpty(dictData) ? dictData.getLabel() : bankType;
                
                // 插入m_bank_accounts_type记录
                MBankAccountsTypeEntity typeEntity = new MBankAccountsTypeEntity();
                typeEntity.setBank_id(mBankAccountsEntity.getId());
                typeEntity.setCode(bankType);
                typeEntity.setName(typeName);
                typeEntity.setStatus("1");
                
                mBankAccountsTypeMapper.insert(typeEntity);
            }
        }

        searchCondition.setId(mBankAccountsEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 企业银行账户 删除
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<MBankAccountsVo> searchCondition) {
        for (MBankAccountsVo mBankAccountsVo : searchCondition) {
            // 逻辑删除
            MBankAccountsEntity bPoContractEntity = mapper.selectById(mBankAccountsVo.getId());
            bPoContractEntity.setStatus(DictConstant.DICT_M_BANK_STATUS_DEL);

            int delCount = mapper.updateById(bPoContractEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 启用银行账户 状态修改
     * @param searchCondition
     */
    @Override
    public UpdateResultAo<Integer> updateStatus(List<MBankAccountsVo> searchCondition) {
        for (MBankAccountsVo mBankAccountsVo : searchCondition) {
            // 状态修改
            MBankAccountsEntity bPoContractEntity = mapper.selectById(mBankAccountsVo.getId());
            bPoContractEntity.setStatus(bPoContractEntity.getStatus().equals(DictConstant.DICT_M_BANK_STATUS_ZERO) ? DictConstant.DICT_M_BANK_STATUS_ONE : DictConstant.DICT_M_BANK_STATUS_ZERO);

            int delCount = mapper.updateById(bPoContractEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return UpdateResultUtil.OK(1);
    }

    /**
     * 企业银行账户 导出
     *
     * @param searchCondition
     */
    @Override
    public List<MBankAccountsExportVo> selectExportList(MBankAccountsVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(searchCondition.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectExportCount(searchCondition);

            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportList(searchCondition);
    }

    /**
     * 获取企业银行默认账户
     * @param searchCondition
     */
    @Override
    public MBankAccountsVo getPurchaser(MBankAccountsVo searchCondition) {
        MBankAccountsVo purchaser = mapper.getPurchaser(searchCondition);
//        List<MBankAccountsTypeEntity> mBankAccountsTypeEntities = mBankAccountsTypeMapper.selectList(null);

        return purchaser;
    }

    /**
     * 获取销售方企业的默认银行账户
     * @param searchCondition
     */
    @Override
    public MBankAccountsVo getSeller(MBankAccountsVo searchCondition) {
        MBankAccountsVo seller = mapper.getSeller(searchCondition);
//        List<MBankAccountsTypeEntity> mBankAccountsTypeEntities = mBankAccountsTypeMapper.selectList(null);

        return seller;
    }

    @Override
    public IPage<MBankAccountsVo> dialogpageList(MBankAccountsVo searchCondition) {
        // 分页条件
        Page<MBankAccountsVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.dialogpageList(pageCondition, searchCondition);
    }

    /**
     * 获取款项类型
     *
     * @param searchCondition
     */
    @Override
    public List<MBankAccountsTypeVo> getBankType(MBankAccountsTypeVo searchCondition) {
        return mBankAccountsTypeMapper.getBankType(searchCondition.getBank_id());
    }

    /**
     * 获取银行收款账户下拉
     * @param searchCondition
     */
    @Override
    public List<MBankAccountsVo> getBankCollection(MBankAccountsVo searchCondition) {
        return mapper.getBankCollection(searchCondition);
    }


}
