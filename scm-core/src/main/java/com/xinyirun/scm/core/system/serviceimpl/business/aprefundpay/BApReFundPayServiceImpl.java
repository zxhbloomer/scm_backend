package com.xinyirun.scm.core.system.serviceimpl.business.aprefundpay;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.aprefund.*;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.aprefund.*;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApRefundPayMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPayService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApReFundPayAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 退款单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApReFundPayServiceImpl extends ServiceImpl<BApRefundPayMapper, BApReFundPayEntity> implements IBApReFundPayService {

    @Autowired
    private BApRefundPayMapper mapper;

    @Autowired
    private BApReFundPayDetailMapper bApReFundPayDetailMapper;

    @Autowired
    private BApReFundPayAttachMapper bApReFundPayAttachMapper;

    @Autowired
    private BApReFundMapper bApReFundMapper;

    @Autowired
    private BApReFundDetailMapper bApReFundDetailMapper;

    @Autowired
    private BApReFundSourceAdvanceMapper bApReFundSourceAdvanceMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BApReFundPayAutoCodeServiceImpl bApReFundPayAutoCodeService;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    /**
     * 下推付款单
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundPayVo> insert(BApReFundPayVo vo) {

        // 1.新增付款单
        BApReFundPayEntity bApReFundPayEntity = (BApReFundPayEntity) BeanUtilsSupport.copyProperties(vo, BApReFundPayEntity.class);
        bApReFundPayEntity.setCode(bApReFundPayAutoCodeService.autoCode().getCode());
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_ONE);
        int bApPay = mapper.insert(bApReFundPayEntity);
        if (bApPay == 0){
            throw new BusinessException("新增付款单，新增失败");
        }

        // 2.新增付款单明细
        BApReFundPayDetailEntity bApReFundPayDetailEntity = new BApReFundPayDetailEntity();
        bApReFundPayDetailEntity.setAp_refund_id(bApReFundPayEntity.getId());
        bApReFundPayDetailEntity.setAp_refund_code(bApReFundPayEntity.getCode());
        bApReFundPayDetailEntity.setAp_id(bApReFundPayEntity.getId());
        bApReFundPayDetailEntity.setAp_code(bApReFundPayEntity.getCode());
        bApReFundPayDetailEntity.setPo_contract_code(vo.getPo_contract_code());
        int bApPayDetail = bApReFundPayDetailMapper.insert(bApReFundPayDetailEntity);
        if (bApPayDetail == 0){
            throw new BusinessException("新增付款单明细，新增失败");
        }

        // 3.新增付款单附件
        BApReFundPayAttachEntity bApReFundPayAttachEntity = new BApReFundPayAttachEntity();
        bApReFundPayAttachEntity.setAp_refund_id(bApReFundPayEntity.getId());
        bApReFundPayAttachEntity.setAp_refund_code(bApReFundPayEntity.getCode());

        // 附件增加
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundPayEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
        SFileEntity sFileEntity = insertFile(fileEntity, vo.getPush_files());
        bApReFundPayAttachEntity.setFiles(sFileEntity.getId());
        int bApPayAttach = bApReFundPayAttachMapper.insert(bApReFundPayAttachEntity);
        if (bApPayAttach == 0){
            throw new BusinessException("新增付款单明细，新增失败");
        }

        // 4.更新应付账款主表金额字段
        BApReFundEntity bApReFundEntity = bApReFundMapper.selectById(vo.getAp_refund_id());
//        bApReFundEntity.setRefunding_amount(bApReFundEntity.getRefunding_amount().add(bApReFundPayEntity.getRefund_amount()));
        int bAp = bApReFundMapper.updateById(bApReFundEntity);
        if (bAp == 0){
            throw new BusinessException("新增付款单明细，新增失败");
        }

        // 5.更新应付退款关联单据表-源单-预收款
//        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = bApReFundSourceAdvanceMapper.selectByApRefundId(bApReFundPayEntity.getAp_refund_id());
//        bApReFundSourceAdvanceEntity.setRefunding_amount(bApReFundSourceAdvanceEntity.getRefunding_amount().add(bApReFundPayEntity.getRefund_amount()));
//        int bApReSoAd = bApReFundSourceAdvanceMapper.updateById(bApReFundSourceAdvanceEntity);
//        if (bApReSoAd == 0){
//            throw new BusinessException("新增付款单明细，新增失败");
//        }

        vo.setId(bApReFundPayEntity.getId());
        return InsertResultUtil.OK(vo);
    }

    /**
     * 列表查询
     * @param searchCondition
     */
    @Override
    public IPage<BApReFundPayVo> selectPage(BApReFundPayVo searchCondition) {
        // 分页条件
        Page<BApReFundPayVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取单条数据
     *
     * @param id
     */
    @Override
    public BApReFundPayVo selectById(Integer id) {
        BApReFundPayVo bApReFundPayVo = mapper.selById(id);
        if (bApReFundPayVo != null && bApReFundPayVo.getVoucher_file() != null) {
            SFileEntity file = fileMapper.selectById(bApReFundPayVo.getVoucher_file());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            bApReFundPayVo.setVoucher_files(new ArrayList<>());
            for (SFileInfoEntity fileInfo : fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                bApReFundPayVo.getVoucher_files().add(fileInfoVo);
            }
        }
        return bApReFundPayVo;
    }

    /**
     * 付款复核
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundPayVo> paymentReview(BApReFundPayVo searchCondition) {
//
//        // 1.更新付款单状态
//        BApReFundPayEntity bApReFundPayEntity = mapper.selectById(searchCondition.getId());
//        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_TWO);
//        bApReFundPayEntity.setSupplier_enterprise_bank_name(searchCondition.getSupplier_enterprise_bank_name());
//        bApReFundPayEntity.setBuyer_enterprise_bank_name(searchCondition.getBuyer_enterprise_bank_name());
//        bApReFundPayEntity.setRefund_date(searchCondition.getRefund_date());
//        bApReFundPayEntity.setRefund_method(searchCondition.getRefund_method());
//        bApReFundPayEntity.setVoucher_remark(searchCondition.getVoucher_remark());
//
//        int bApReFundPay = mapper.updateById(bApReFundPayEntity);
//        if (bApReFundPay == 0){
//            throw new BusinessException("付款复核，更新失败");
//        }
//
//        // 附件增加
//        SFileEntity fileEntity = new SFileEntity();
//        fileEntity.setSerial_id(bApReFundPayEntity.getId());
//        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
//        SFileEntity sFileEntity = insertFile(fileEntity, searchCondition.getVoucher_files());
//
//        BApReFundPayAttachVo bApReFundPayAttachVo = bApReFundPayAttachMapper.selectByBapId(searchCondition.getId());
//        BApReFundPayAttachEntity bApReFundPayAttachEntity = (BApReFundPayAttachEntity) BeanUtilsSupport.copyProperties(bApReFundPayAttachVo, BApReFundPayAttachEntity.class);
//        bApReFundPayAttachEntity.setVoucher_files(sFileEntity.getId());
//
//        int bApPayAttach = bApReFundPayAttachMapper.updateById(bApReFundPayAttachEntity);
//        if (bApPayAttach == 0){
//            throw new BusinessException("付款复核，更新失败");
//        }
//
//        // 4.更新应付账款主表金额字段
//        BApReFundEntity bApReFundEntity = bApReFundMapper.selectById(searchCondition.getAp_refund_id());
//        bApReFundEntity.setRefunding_amount(bApReFundEntity.getRefunding_amount().subtract(bApReFundPayEntity.getRefund_amount()));
//        bApReFundEntity.setRefunded_amount(bApReFundEntity.getRefunded_amount().add(bApReFundPayEntity.getRefund_amount()));
//
//        if (bApReFundEntity.getRefund_amount().compareTo(bApReFundEntity.getRefunded_amount()) == 0){
//            bApReFundEntity.setPay_status(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_TWO); // 注意：字段已更改为refund_status
//        } else if (bApReFundEntity.getRefund_amount().compareTo(bApReFundEntity.getRefunded_amount()) > 0){
//            bApReFundEntity.setPay_status(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ONE); // 注意：字段已更改为refund_status
//        } else if (bApReFundEntity.getRefund_amount().compareTo(bApReFundEntity.getRefunded_amount()) < 0){
//            throw new BusinessException("付款复核，更新失败");
//        }
//
//        int bApReFund = bApReFundMapper.updateById(bApReFundEntity);
//        if (bApReFund == 0){
//            throw new BusinessException("付款复核，更新失败");
//        }
//
//        // 5.更新银行账户付款信息
//        BApReFundDetailEntity bApDetailEntity = bApReFundDetailMapper.selectByApRefundId(searchCondition.getAp_refund_id());
//        bApDetailEntity.setRefunded_amount(bApDetailEntity.getRefunded_amount().add(bApReFundPayEntity.getRefund_amount()));
//        int bApDetail = bApReFundDetailMapper.updateById(bApDetailEntity);
//        if (bApDetail == 0){
//            throw new BusinessException("银行账户付款信息，新增失败");
//        }
//
//        // 6.更新应付退款关联单据表-源单-预收款
//        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = bApReFundSourceAdvanceMapper.selectByApRefundId(bApReFundPayEntity.getAp_refund_id());
//        bApReFundSourceAdvanceEntity.setRefunding_amount(bApReFundSourceAdvanceEntity.getRefunding_amount().subtract(bApReFundPayEntity.getRefund_amount()));
//        bApReFundSourceAdvanceEntity.setRefunded_amount(bApReFundSourceAdvanceEntity.getRefunded_amount().add(bApReFundPayEntity.getRefund_amount()));
//
//        int bApReSoAd = bApReFundSourceAdvanceMapper.updateById(bApReFundSourceAdvanceEntity);
//        if (bApReSoAd == 0){
//            throw new BusinessException("新增付款单明细，新增失败");
//        }
//
//        searchCondition.setId(bApReFundPayEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 作废 （只能作废已付款账单：实际交易金额 = 累计发生金额 -累计退款金额 -累计作废金额 -累计核销金额 +累计退款作废金额）
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundPayVo> cancel(BApReFundPayVo searchCondition) {
//        // 1.作废付款单状态
//        BApReFundPayEntity bApReFundPayEntity = mapper.selectById(searchCondition.getId());
//
//        // 更新作废付款单
//        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_THREE);
//        int bApPay = mapper.updateById(bApReFundPayEntity);
//        if (bApPay == 0){
//            throw new BusinessException("作废，更新失败");
//        }
//
//        // 2.更新应付账款主表金额字段（已付款总金额-作废单金额 ）
//        BApReFundEntity bApReFundEntity = bApReFundMapper.selectById(bApReFundPayEntity.getAp_refund_id());
//        bApReFundEntity.setRefunded_amount(bApReFundEntity.getRefunded_amount().subtract(bApReFundPayEntity.getRefund_amount()));
//
//        // 更新应付账款状态
//        List<BApReFundPayEntity> bApReFundPayVos = mapper.selectApPayByStatus(bApReFundPayEntity.getAp_refund_id(), DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_TWO);
//        bApReFundEntity.setPay_status(CollectionUtil.isEmpty(bApReFundPayVos)?DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO:DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ONE); // 注意：字段已更改为refund_status
//
//        // 更新应付账款主表
//        int bAp = bApReFundMapper.updateById(bApReFundEntity);
//        if (bAp == 0){
//            throw new BusinessException("作废，更新失败");
//        }
//
//        // 3.更新银行账户付款信息
//        BApReFundDetailEntity bApReFundDetailEntity = bApReFundDetailMapper.selectByApRefundId(bApReFundPayEntity.getAp_refund_id());
//        bApReFundDetailEntity.setRefunded_amount(bApReFundDetailEntity.getRefunded_amount().subtract(bApReFundPayEntity.getRefund_amount()));
//        int bApDetail = bApReFundDetailMapper.updateById(bApReFundDetailEntity);
//        if (bApDetail == 0){
//            throw new BusinessException("银行账户付款信息，新增失败");
//        }
//
//        // 4.更新应付退款关联单据表-源单-预收款
//        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = bApReFundSourceAdvanceMapper.selectByApRefundId(bApReFundPayEntity.getAp_refund_id());
//        bApReFundSourceAdvanceEntity.setRefunded_amount(bApReFundSourceAdvanceEntity.getRefunded_amount().subtract(bApReFundSourceAdvanceEntity.getRefund_amount()));
//
//        int bApReSoAd = bApReFundSourceAdvanceMapper.updateById(bApReFundSourceAdvanceEntity);
//        if (bApReSoAd == 0){
//            throw new BusinessException("新增付款单明细，新增失败");
//        }
//        searchCondition.setId(bApReFundPayEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 附件逻辑 全删全增
     */
    public SFileEntity insertFile(SFileEntity fileEntity, List<SFileInfoVo> sFileInfoVos) {
        if (CollectionUtil.isNotEmpty(sFileInfoVos)){
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : sFileInfoVos) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }
        return fileEntity;
    }
}
