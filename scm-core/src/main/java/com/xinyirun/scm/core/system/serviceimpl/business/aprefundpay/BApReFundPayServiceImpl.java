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
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPaySourceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPaySourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundDetailVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundSourceVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayAttachVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.aprefund.*;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApRefundPayMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPaySourceMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApReFundPaySourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.aprefundpay.IBApReFundPayService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApReFundPayAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Autowired
    private BApReFundSourceMapper bApReFundSourceMapper;

    @Autowired
    private BApReFundTotalMapper bApReFundTotalMapper;

    @Autowired
    private BApReFundPaySourceMapper bApReFundPaySourceMapper;

    @Autowired
    private BApReFundPaySourceAdvanceMapper bApReFundPaySourceAdvanceMapper;

    @Autowired
    private MCancelService mCancelService;

    /**
     * 初始化退款单数据
     * @param searchCondition 退款单Vo
     */
    private void initInsertApRefundPayData(BApReFundPayVo searchCondition) {
        // 1、查询b_ap_refund表
        BApReFundVo bApReFundVo = bApReFundMapper.selectId(searchCondition.getAp_refund_id());
        if (bApReFundVo != null) {
            // set数据
            searchCondition.setCode(bApReFundPayAutoCodeService.autoCode().getCode());
            searchCondition.setAp_refund_id(bApReFundVo.getId());
            searchCondition.setAp_refund_code(bApReFundVo.getCode());
            searchCondition.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO); // 0-待退款：使用常量
            searchCondition.setType(bApReFundVo.getType());
            searchCondition.setSupplier_id(bApReFundVo.getSupplier_id());
            searchCondition.setSupplier_code(bApReFundVo.getSupplier_code());
            searchCondition.setSupplier_name(bApReFundVo.getSupplier_name());
            searchCondition.setPurchaser_id(bApReFundVo.getPurchaser_id());
            searchCondition.setPurchaser_code(bApReFundVo.getPurchaser_code());
            searchCondition.setPurchaser_name(bApReFundVo.getPurchaser_name());
            
            BApReFundPayDetailVo apReFundPayDetailVo = searchCondition.getDetailData();
            BigDecimal refundAmountTotal = BigDecimal.ZERO;
            if (apReFundPayDetailVo != null && apReFundPayDetailVo.getOrder_amount() != null) {
                refundAmountTotal = apReFundPayDetailVo.getOrder_amount();
            }
            searchCondition.setRefundable_amount_total(refundAmountTotal);
            searchCondition.setRefunded_amount_total(bApReFundVo.getRefunded_amount_total());
        }

        // 2、查询b_ap_refund_detail表
        List<BApReFundDetailVo> bApReFundDetailVos = bApReFundMapper.getApRefundDetail(searchCondition.getAp_refund_id());
        
        if (searchCondition.getDetailData() != null && CollectionUtil.isNotEmpty(bApReFundDetailVos)) {
            // 获取单个明细数据
            BApReFundPayDetailVo payDetailVo = searchCondition.getDetailData();
            // 和返回List<BApReFundDetailVo>比较
            for (BApReFundDetailVo apReFundDetailVo : bApReFundDetailVos) {
                // 当BApReFundDetailVo.code = BApReFundPayVo.detailData.ap_refund_code
                if (apReFundDetailVo.getId().equals(payDetailVo.getId())) {
                    payDetailVo.setAp_refund_id(apReFundDetailVo.getId());
                    payDetailVo.setAp_refund_code(apReFundDetailVo.getCode());
                    payDetailVo.setBank_accounts_id(apReFundDetailVo.getBank_accounts_id());
                    payDetailVo.setBank_accounts_code(apReFundDetailVo.getBank_accounts_code());
                    payDetailVo.setRefundable_amount(apReFundDetailVo.getRefundable_amount());
                    break; // 找到匹配项后跳出循环
                }
            }
        }
    }

    /**
     * 计算退款金额
     * 功能说明：计算退款金额汇总
     * @param searchCondition 退款单Vo
     */
    private void calcAmountInsertRefundPay(BApReFundPayVo searchCondition) {
        BigDecimal refundableAmountTotal = BigDecimal.ZERO;
        BigDecimal refundedAmountTotal = BigDecimal.ZERO;
        BigDecimal refundAmountTotal = BigDecimal.ZERO;

        if (searchCondition.getDetailData() != null) {
            // 获取单个明细数据
            BApReFundPayDetailVo detail = searchCondition.getDetailData();
            // refunded_amount已退款金额=0
            detail.setRefunded_amount(BigDecimal.ZERO);
            
            // 计算各种金额
            if (detail.getRefundable_amount() != null) {
                refundableAmountTotal = detail.getRefundable_amount();
            }
            if (detail.getRefunded_amount() != null) {
                refundedAmountTotal = detail.getRefunded_amount();
            }
            if (detail.getOrder_amount() != null) {
                refundAmountTotal = detail.getOrder_amount();
            }
        }

        // 设置汇总金额
        searchCondition.setRefundable_amount_total(refundableAmountTotal); // 退款单计划退款总金额
        searchCondition.setRefunded_amount_total(refundedAmountTotal); // 退款单已退款总金额
        searchCondition.setRefunding_amount_total(refundAmountTotal); // 退款单本次退款总金额
    }

    /**
     * 保存退款单主表
     * @param searchCondition 退款单Vo
     * @return 退款单Entity
     */
    private BApReFundPayEntity saveRefundPayMain(BApReFundPayVo searchCondition) {
        BApReFundPayEntity bApReFundPayEntity = (BApReFundPayEntity) BeanUtilsSupport.copyProperties(searchCondition, BApReFundPayEntity.class);
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO);
        int bApReFundPay = mapper.insert(bApReFundPayEntity);
        if (bApReFundPay == 0){
            throw new BusinessException("新增退款单，新增失败");
        }
        return bApReFundPayEntity;
    }

    /**
     * 保存退款单明细
     * @param searchCondition 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPayDetailList(BApReFundPayVo searchCondition, BApReFundPayEntity bApReFundPayEntity) {
        if (searchCondition.getDetailData() != null) {
            BApReFundPayDetailVo detailVo = searchCondition.getDetailData();
            BApReFundPayDetailEntity bApReFundPayDetailEntity = (BApReFundPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApReFundPayDetailEntity.class);
            bApReFundPayDetailEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
            bApReFundPayDetailEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
            /**
             * 新增操作，所以状态必定是0-待退款
             * refunded_amount=0
             * refunding_amount=order_amount
             * unrefund_amount=order_amount
             * cancel_amount=0
             */
            bApReFundPayDetailEntity.setRefunded_amount(BigDecimal.ZERO);
            bApReFundPayDetailEntity.setRefunding_amount(bApReFundPayDetailEntity.getOrder_amount());
            bApReFundPayDetailEntity.setUnrefund_amount(bApReFundPayDetailEntity.getOrder_amount());
            bApReFundPayDetailEntity.setCancel_amount(BigDecimal.ZERO);

            bApReFundPayDetailEntity.setId(null);
            int bApReFundPayDetail = bApReFundPayDetailMapper.insert(bApReFundPayDetailEntity);
            if (bApReFundPayDetail == 0){
                throw new BusinessException("新增退款单明细，新增失败");
            }
        }
    }

    /**
     * 保存退款单附件
     * @param searchCondition 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPayAttach(BApReFundPayVo searchCondition, BApReFundPayEntity bApReFundPayEntity) {
        if (CollectionUtil.isNotEmpty(searchCondition.getPush_files())) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApReFundPayEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
            fileMapper.insert(fileEntity);
            for (SFileInfoVo docAttFile : searchCondition.getPush_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                docAttFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(docAttFile, fileInfoEntity);
                fileInfoEntity.setFile_name(docAttFile.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            BApReFundPayAttachEntity bApReFundPayAttachEntity = new BApReFundPayAttachEntity();
            bApReFundPayAttachEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
            bApReFundPayAttachEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
            bApReFundPayAttachEntity.setOne_file(fileEntity.getId());
            int bApReFundPayAttach = bApReFundPayAttachMapper.insert(bApReFundPayAttachEntity);
            if (bApReFundPayAttach == 0) {
                throw new BusinessException("新增退款单附件，新增失败");
            }
        }
    }

    /**
     * 保存退款单源单
     * @param searchCondition 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPaySource(BApReFundPayVo searchCondition, BApReFundPayEntity bApReFundPayEntity) {
        List<BApReFundSourceVo> apReFundSourceList = bApReFundSourceMapper.selectByApRefundId(searchCondition.getAp_refund_id());
        if (CollectionUtil.isNotEmpty(apReFundSourceList)) {
            for (BApReFundSourceVo apReFundSourceVo : apReFundSourceList) {
                BApReFundPaySourceEntity paySourceEntity = new BApReFundPaySourceEntity();
                paySourceEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
                paySourceEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
                paySourceEntity.setAp_refund_id(apReFundSourceVo.getAp_refund_id());
                paySourceEntity.setAp_refund_code(apReFundSourceVo.getAp_refund_code());
                paySourceEntity.setType(apReFundSourceVo.getType());
                paySourceEntity.setProject_code(apReFundSourceVo.getProject_code());
                paySourceEntity.setPo_contract_id(apReFundSourceVo.getPo_contract_id());
                paySourceEntity.setPo_contract_code(apReFundSourceVo.getPo_contract_code());
                paySourceEntity.setPo_order_code(apReFundSourceVo.getPo_order_code());
                paySourceEntity.setPo_order_id(apReFundSourceVo.getPo_order_id());
                bApReFundPaySourceMapper.insert(paySourceEntity);
            }
        }
    }

    /** 保存退款单关联源单-预收款 */
    private void saveRefundPaySourceAdvance(BApReFundPayVo searchCondition, BApReFundPayEntity bApReFundPayEntity) {
        List<BApReFundSourceAdvanceVo> apReFundSourceAdvanceList = bApReFundSourceAdvanceMapper.selectByApRefundId(searchCondition.getAp_refund_id());
        if (CollectionUtil.isNotEmpty(apReFundSourceAdvanceList)) {
            for (BApReFundSourceAdvanceVo apReFundSourceAdvanceVo : apReFundSourceAdvanceList) {
                BApReFundPaySourceAdvanceEntity paySourceAdvanceEntity = new BApReFundPaySourceAdvanceEntity();
                paySourceAdvanceEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
                paySourceAdvanceEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
                paySourceAdvanceEntity.setAp_refund_id(apReFundSourceAdvanceVo.getAp_refund_id());
                paySourceAdvanceEntity.setAp_refund_code(apReFundSourceAdvanceVo.getAp_refund_code());
                paySourceAdvanceEntity.setType(apReFundSourceAdvanceVo.getType());
                paySourceAdvanceEntity.setPo_contract_id(apReFundSourceAdvanceVo.getPo_contract_id());
                paySourceAdvanceEntity.setPo_contract_code(apReFundSourceAdvanceVo.getPo_contract_code());
                paySourceAdvanceEntity.setPo_order_code(apReFundSourceAdvanceVo.getPo_order_code());
                paySourceAdvanceEntity.setPo_order_id(apReFundSourceAdvanceVo.getPo_order_id());
                paySourceAdvanceEntity.setPo_goods(apReFundSourceAdvanceVo.getPo_goods());
                paySourceAdvanceEntity.setOrder_amount(apReFundSourceAdvanceVo.getOrder_amount());
                bApReFundPaySourceAdvanceMapper.insert(paySourceAdvanceEntity);
            }
        }
    }

    /**
     * 退款单新增
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundPayVo> startInsert(BApReFundPayVo searchCondition) {
        // 1、校验
        checkInsertLogic(searchCondition);
        
        // 2.保存退款单
        InsertResultAo<BApReFundPayVo> insertResultAo = insert(searchCondition);

        // 3.total数据重算
        // commonTotalService.reCalculateAllTotalDataByApRefundPayId(searchCondition.getId());
        
        return insertResultAo;
    }

    /**
     * 下推退款单
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundPayVo> insert(BApReFundPayVo searchCondition) {
        // 2、初始化数据
        initInsertApRefundPayData(searchCondition);
        // 3、计算金额
        calcAmountInsertRefundPay(searchCondition);
        // 4、保存主表
        BApReFundPayEntity bApReFundPayEntity = saveRefundPayMain(searchCondition);
        // 5、保存明细
        saveRefundPayDetailList(searchCondition, bApReFundPayEntity);
        // 6、保存附件
        saveRefundPayAttach(searchCondition, bApReFundPayEntity);
        // 7、保存源单
        saveRefundPaySource(searchCondition, bApReFundPayEntity);
        // 8、保存源单-预收款
        saveRefundPaySourceAdvance(searchCondition, bApReFundPayEntity);
        // 9、更新应付退款主表金额
        // updateApRefundingAmount(searchCondition);
        // 10、资金流水监控（如需）
        // commonFundService.startApRefundPayFund(bApReFundPayEntity.getId());
        searchCondition.setId(bApReFundPayEntity.getId());
        return InsertResultUtil.OK(searchCondition);
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
        // 1. 业务校验
        validatePaymentReview(searchCondition);
        
        // 2. 更新退款单主表状态
        BApReFundPayEntity bApReFundPayEntity = updateRefundPaymentStatus(searchCondition);
        
        // 3. 更新退款单明细表状态
        updateRefundPaymentDetails(searchCondition.getId());
        
        // 4. 处理附件上传
        processVoucherAttachment(bApReFundPayEntity, searchCondition);
        
        // 5. 重算总金额数据
        recalculateTotalData(searchCondition.getId());
        
        // 6. 更新应付退款主表状态
        updateApRefundPaymentStatus(bApReFundPayEntity.getAp_refund_id());
        
        // 7. 更新资金流水表
        updateFundFlow(searchCondition.getId());
        
        searchCondition.setId(bApReFundPayEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 付款复核业务校验
     * @param searchCondition 退款单信息
     */
    private void validatePaymentReview(BApReFundPayVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新退款单主表状态
     * @param searchCondition 退款单信息
     * @return 更新后的退款单实体
     */
    private BApReFundPayEntity updateRefundPaymentStatus(BApReFundPayVo searchCondition) {
        BApReFundPayEntity bApReFundPayEntity = mapper.selectById(searchCondition.getId());
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_TWO);
        bApReFundPayEntity.setRefund_date(searchCondition.getRefund_date());
        bApReFundPayEntity.setVoucher_remark(searchCondition.getVoucher_remark());
        bApReFundPayEntity.setRefund_method(searchCondition.getRefund_method());

        int updateResult = mapper.updateById(bApReFundPayEntity);
        if (updateResult == 0) {
            throw new BusinessException("退款单状态更新失败");
        }
        
        return bApReFundPayEntity;
    }

    /**
     * 更新退款单明细表状态
     * @param apRefundPayId 退款单ID
     */
    private void updateRefundPaymentDetails(Integer apRefundPayId) {
        List<BApReFundPayDetailVo> apReFundPayDetailVos = bApReFundPayDetailMapper.selectById(apRefundPayId);
        if (CollectionUtil.isNotEmpty(apReFundPayDetailVos)) {
            // 根据新设计，每个退款单只有一个明细记录，获取第一条记录
            BApReFundPayDetailVo detailVo = apReFundPayDetailVos.get(0);
            BApReFundPayDetailEntity bApReFundPayDetailEntity = (BApReFundPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApReFundPayDetailEntity.class);
            
            // 已退款操作，设置相关金额字段
            bApReFundPayDetailEntity.setRefunded_amount(bApReFundPayDetailEntity.getOrder_amount()); // 已退金额 = 本次退款金额
            bApReFundPayDetailEntity.setRefunding_amount(BigDecimal.ZERO);                           // 退款中金额清零
            bApReFundPayDetailEntity.setUnrefund_amount(BigDecimal.ZERO);                            // 未退金额清零
            bApReFundPayDetailEntity.setCancel_amount(BigDecimal.ZERO);                              // 作废金额清零
            
            int updateResult = bApReFundPayDetailMapper.updateById(bApReFundPayDetailEntity);
            if (updateResult == 0) {
                throw new BusinessException("退款单明细状态更新失败");
            }
        }
    }

    /**
     * 处理凭证附件
     * @param bApReFundPayEntity 退款单实体
     * @param searchCondition 退款单信息
     */
    private void processVoucherAttachment(BApReFundPayEntity bApReFundPayEntity, BApReFundPayVo searchCondition) {
        // 上传附件文件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundPayEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
        SFileEntity sFileEntity = insertFile(fileEntity, searchCondition.getVoucher_files());

        // 更新或新增附件关联记录
        BApReFundPayAttachVo bApReFundPayAttachVo = bApReFundPayAttachMapper.selectByBapId(bApReFundPayEntity.getId());
        if (bApReFundPayAttachVo == null) {
            // 新增附件记录
            BApReFundPayAttachEntity bApReFundPayAttachEntity = new BApReFundPayAttachEntity();
            bApReFundPayAttachEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
            bApReFundPayAttachEntity.setTwo_file(sFileEntity.getId());
            int insertResult = bApReFundPayAttachMapper.insert(bApReFundPayAttachEntity);
            if (insertResult == 0) {
                throw new BusinessException("附件关联记录新增失败");
            }
        } else {
            // 更新附件记录
            BApReFundPayAttachEntity bApReFundPayAttachEntity = (BApReFundPayAttachEntity) BeanUtilsSupport.copyProperties(bApReFundPayAttachVo, BApReFundPayAttachEntity.class);
            bApReFundPayAttachEntity.setTwo_file(sFileEntity.getId());
            int updateResult = bApReFundPayAttachMapper.updateById(bApReFundPayAttachEntity);
            if (updateResult == 0) {
                throw new BusinessException("附件关联记录更新失败");
            }
        }
    }

    /**
     * 重算总金额数据
     * @param apRefundPayId 退款单ID
     */
    private void recalculateTotalData(Integer apRefundPayId) {
        // 重算退款单相关的总金额数据
        // 这里需要根据具体业务逻辑实现
        // commonTotalService.reCalculateAllTotalDataByApRefundPayId(apRefundPayId);
    }

    /**
     * 更新应付退款主表的退款状态
     * @param apRefundId 应付退款主表ID
     */
    private void updateApRefundPaymentStatus(Integer apRefundId) {
        // 获取应付退款主表
        BApReFundVo bApReFundVo = bApReFundMapper.selectId(apRefundId);
        if (bApReFundVo == null) {
            return;
        }

        // 获取退款单的已退金额汇总（状态=2表示已退款）
        // 这里需要在mapper中实现相应的查询方法
        // 暂时先实现基本逻辑
        BApReFundEntity bApReFundEntity = new BApReFundEntity();
        bApReFundEntity.setId(apRefundId);
        // 更新退款状态逻辑
        // bApReFundEntity.setRefund_status(计算后的状态);
        
        // int updateResult = bApReFundMapper.updateById(bApReFundEntity);
        // if (updateResult == 0) {
        //     throw new BusinessException("应付退款主表状态更新失败");
        // }
    }

    /**
     * 更新资金流水表
     * @param apRefundPayId 退款单ID
     */
    private void updateFundFlow(Integer apRefundPayId) {
        // 更新资金流水表
        // commonFundService.increaseRefundAmount(apRefundPayId);
    }

    /**
     * 作废 （只能作废已付款账单：实际交易金额 = 累计发生金额 -累计退款金额 -累计作废金额 -累计核销金额 +累计退款作废金额）
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundPayVo> cancel(BApReFundPayVo searchCondition) {
        
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.作废退款单状态
        BApReFundPayEntity bApReFundPayEntity = mapper.selectById(searchCondition.getId());
        String originalStatus = bApReFundPayEntity.getStatus();
        
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_THREE);

        // 2.保存作废附件和作废原因到附件表
        BApReFundPayAttachVo attachVo = bApReFundPayAttachMapper.selectByBapId(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(searchCondition.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        int bApReFundPay = mapper.updateById(bApReFundPayEntity);
        if (bApReFundPay == 0){
            throw new BusinessException("作废，更新失败");
        }
        searchCondition.setId(bApReFundPayEntity.getId());

        // 更新退款单明细表状态
        List<BApReFundPayDetailVo> apReFundPayDetailVos = bApReFundPayDetailMapper.selectById(searchCondition.getId());
        if (CollectionUtil.isNotEmpty(apReFundPayDetailVos)) {
            // 根据新设计，每个退款单只有一个明细记录，获取第一条记录
            BApReFundPayDetailVo detailVo = apReFundPayDetailVos.get(0);
            BApReFundPayDetailEntity bApReFundPayDetailEntity = (BApReFundPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApReFundPayDetailEntity.class);
            /**
             * 作废操作，所以状态必定是2-作废
             * refunded_amount=0
             * refunding_amount=0
             * unrefund_amount=0
             * cancel_amount=if bApReFundPayEntity.status (源状态)= 1-已退款，则为refund_amount，否则为0
             */
            if (originalStatus.equals(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_TWO)) {
                bApReFundPayDetailEntity.setCancel_amount(bApReFundPayDetailEntity.getOrder_amount());
            } else {
                bApReFundPayDetailEntity.setCancel_amount(BigDecimal.ZERO);
            }
            bApReFundPayDetailEntity.setRefunded_amount(BigDecimal.ZERO);
            bApReFundPayDetailEntity.setRefunding_amount(BigDecimal.ZERO);
            bApReFundPayDetailEntity.setUnrefund_amount(BigDecimal.ZERO);

            int bApReFundPayDetail = bApReFundPayDetailMapper.updateById(bApReFundPayDetailEntity);
            if (bApReFundPayDetail == 0){
                throw new BusinessException("作废退款单明细，更新失败");
            }
        }

        // 3.total数据重算
        // commonTotalService.reCalculateAllTotalDataByApRefundPayId(searchCondition.getId());

        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 作废附件处理
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BApReFundPayVo vo) {
        // 其他附件新增
        if (vo.getCancel_files() != null && vo.getCancel_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getCancel_files()) {
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

    /**
     * 新增校验
     * @param searchCondition 退款单Vo
     */
    private void checkInsertLogic(BApReFundPayVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 通用校验逻辑
     * @param searchCondition 退款单Vo
     * @param checkType 校验类型
     * @return 校验结果
     */
    @Override
    public CheckResultAo checkLogic(BApReFundPayVo searchCondition, String checkType) {
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增校验逻辑
                // 1. 退款日期不能为空
                if (searchCondition.getRefund_date() == null) {
                    return CheckResultUtil.NG("退款日期不能为空");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新校验逻辑
                // 1. ID不能为空
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("退款单ID不能为空");
                }
                // 2. 退款日期不能为空
                if (searchCondition.getRefund_date() == null) {
                    return CheckResultUtil.NG("退款日期不能为空");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 删除校验逻辑
                // 1. ID不能为空
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("退款单ID不能为空");
                }
                break;
            case CheckResultAo.FINISH_CHECK_TYPE:
                // 完成校验逻辑
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 作废校验逻辑
                // 无特殊校验
                break;
            default:
                break;
        }
        return CheckResultUtil.OK();
    }

    /**
     * 汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Override
    public BApReFundPayVo querySum(BApReFundPayVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Override
    public BApReFundPayVo queryViewSum(BApReFundPayVo searchCondition) {
        return mapper.queryViewSum(searchCondition);
    }
}
