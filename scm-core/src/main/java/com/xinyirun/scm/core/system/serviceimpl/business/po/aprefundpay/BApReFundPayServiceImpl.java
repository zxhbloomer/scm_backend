package com.xinyirun.scm.core.system.serviceimpl.business.po.aprefundpay;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.po.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.*;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayAttachVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefundpay.*;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.po.aprefundpay.IBApReFundPayService;
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
        // 注意：这里允许查询退款管理表，因为退款单是基于退款管理创建的
        // 但是查询后只用于初始化退款单数据，不应该在其他地方混用表映射
        if (searchCondition.getAp_refund_id() != null) {
            // 1、从退款管理表查询基础数据用于初始化
            // 这是业务上的合理关联，退款单需要从退款管理获取初始数据
            Object bApReFundVo = bApReFundMapper.selectId(searchCondition.getAp_refund_id());
            if (bApReFundVo != null) {
                // 使用反射或Map方式处理数据，避免直接依赖退款管理域的VO
                // 这里简化处理，实际应该使用更安全的方式
                searchCondition.setCode(bApReFundPayAutoCodeService.autoCode().getCode());
                searchCondition.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO); // 0-待退款：使用常量
                // 其他字段应该从前端传入或者通过安全的方式获取
            }
        }

        // 2、处理退款单明细数据初始化
        if (searchCondition.getBankData() != null) {
            // 获取单个明细数据
            BApReFundPayDetailVo payDetailVo = searchCondition.getBankData();
            // 设置退款单明细基本信息
            payDetailVo.setAp_refund_id(searchCondition.getAp_refund_id());
            payDetailVo.setAp_refund_code(searchCondition.getAp_refund_code());
            // 明细数据从前端传入，不需要从退款管理表中查询
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

        if (searchCondition.getBankData() != null) {
            // 获取单个明细数据
            BApReFundPayDetailVo detail = searchCondition.getBankData();
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
     * @param vo 退款单Vo
     * @return 退款单Entity
     */
    private BApReFundPayEntity saveRefundPayMain(BApReFundPayVo vo) {
        BApReFundPayEntity bApReFundPayEntity = (BApReFundPayEntity) BeanUtilsSupport.copyProperties(vo, BApReFundPayEntity.class);
        bApReFundPayEntity.setId(null);
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO);
        int bApReFundPay = mapper.insert(bApReFundPayEntity);
        if (bApReFundPay == 0){
            throw new BusinessException("新增退款单，新增失败");
        }
        return bApReFundPayEntity;
    }

    /**
     * 保存退款单明细
     * @param vo 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPayDetailList(BApReFundPayVo vo, BApReFundPayEntity bApReFundPayEntity) {
        // 从前端传入的明细数据创建退款单明细
        BApReFundPayDetailEntity bApReFundPayDetailEntity = (BApReFundPayDetailEntity) BeanUtilsSupport.copyProperties(vo.getBankData(), BApReFundPayDetailEntity.class);
        bApReFundPayDetailEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
        bApReFundPayDetailEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
        bApReFundPayDetailEntity.setAp_refund_id(vo.getAp_refund_id());
        bApReFundPayDetailEntity.setAp_refund_code(vo.getAp_refund_code());

        // 设置vo.refund_order_amount给bApReFundPayDetailEntity.order_amount
        bApReFundPayDetailEntity.setOrder_amount(vo.getRefund_order_amount());

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

    /**
     * 保存退款单附件
     * @param vo 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPayAttach(BApReFundPayVo vo, BApReFundPayEntity bApReFundPayEntity) {
        if (CollectionUtil.isNotEmpty(vo.getPush_files())) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApReFundPayEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
            fileMapper.insert(fileEntity);
            for (SFileInfoVo docAttFile : vo.getPush_files()) {
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
     * @param vo 退款单Vo
     * @param bApReFundPayEntity 退款单Entity
     */
    private void saveRefundPaySource(BApReFundPayVo vo, BApReFundPayEntity bApReFundPayEntity) {
        // 注意：这里允许查询退款管理的源单表，因为退款单需要复制退款管理的源单数据
        // 但是查询后立即转换为退款单域的实体，不应该在其他地方混用表映射
        if (vo.getAp_refund_id() != null) {
            // 从退款管理源单表查询数据，然后转换为退款单源单数据
            List<BApReFundSourceVo> apReFundSourceList = bApReFundSourceMapper.selectByApRefundId(vo.getAp_refund_id());
            if (CollectionUtil.isNotEmpty(apReFundSourceList)) {
                for (BApReFundSourceVo apReFundSourceVo : apReFundSourceList) {
                    // 使用反射或Map方式处理数据，避免直接依赖退款管理域的VO
                    // 这里简化处理，实际应该使用更安全的方式
                    BApReFundPaySourceEntity paySourceEntity = new BApReFundPaySourceEntity();
                    paySourceEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
                    paySourceEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
                    paySourceEntity.setAp_refund_id(vo.getAp_refund_id());
                    paySourceEntity.setAp_refund_code(vo.getAp_refund_code());
                    // 其他字段应该通过安全的方式从源数据中获取
                    bApReFundPaySourceMapper.insert(paySourceEntity);
                }
            }
        }
    }

    /** 保存退款单关联源单-预收款 */
    private void saveRefundPaySourceAdvance(BApReFundPayVo vo, BApReFundPayEntity bApReFundPayEntity) {
        List<BApReFundSourceAdvanceVo> apReFundSourceAdvanceList = bApReFundSourceAdvanceMapper.selectByApRefundId(vo.getAp_refund_id());
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
                paySourceAdvanceEntity.setAdvance_paid_total(apReFundSourceAdvanceVo.getAdvance_paid_total());
                paySourceAdvanceEntity.setAdvance_refund_amount_total(apReFundSourceAdvanceVo.getAdvance_refund_amount_total());

                bApReFundPaySourceAdvanceMapper.insert(paySourceAdvanceEntity);
            }
        }
    }

    /**
     * 退款单新增
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundPayVo> startInsert(BApReFundPayVo vo) {
        // 1、校验
        checkInsertLogic(vo);
        
        // 2.保存退款单
        InsertResultAo<BApReFundPayVo> insertResultAo = insert(vo);

        // 3.total数据重算
        // commonTotalService.reCalculateAllTotalDataByApRefundPayId(vo.getId());
        
        return insertResultAo;
    }

    /**
     * 下推退款单
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundPayVo> insert(BApReFundPayVo vo) {
        // 2、初始化数据
        initInsertApRefundPayData(vo);
        // 3、计算金额
        calcAmountInsertRefundPay(vo);
        // 4、保存主表
        BApReFundPayEntity bApReFundPayEntity = saveRefundPayMain(vo);
        // 5、保存明细
        saveRefundPayDetailList(vo, bApReFundPayEntity);
        // 6、保存附件
        saveRefundPayAttach(vo, bApReFundPayEntity);
        // 7、保存源单
        saveRefundPaySource(vo, bApReFundPayEntity);
        // 8、保存源单-预收款
        saveRefundPaySourceAdvance(vo, bApReFundPayEntity);
        // 9、更新应付退款主表金额
        // updateApRefundingAmount(vo);
        // 10、资金流水监控（如需）
        // commonFundService.startApRefundPayFund(bApReFundPayEntity.getId());
        vo.setId(bApReFundPayEntity.getId());
        return InsertResultUtil.OK(vo);
    }

    /**
     * 列表查询
     * @param vo
     */
    @Override
    public IPage<BApReFundPayVo> selectPage(BApReFundPayVo vo) {
        // 分页条件
        Page<BApReFundPayVo> pageCondition = new Page(vo.getPageCondition().getCurrent(), vo.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, vo.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, vo);
    }

    /**
     * 获取单条数据
     *
     * @param id
     */
    @Override
    public BApReFundPayVo selectById(Integer id) {
        BApReFundPayVo bApReFundPayVo = mapper.selectId(id);
        if (bApReFundPayVo == null) {
            return null;
        }
        
        // 1. 查询退款单明细数据（1:1关系）
        List<BApReFundPayDetailVo> payDetailVos = bApReFundPayDetailMapper.selectById(id);
        if (CollectionUtil.isNotEmpty(payDetailVos)) {
            // 根据业务设计，每个退款单只有一个明细记录，获取第一条记录
            BApReFundPayDetailVo detailVo = payDetailVos.get(0);
            bApReFundPayVo.setBankData(detailVo);
        }

        // 2. 查询退款单附件
        BApReFundPayAttachVo attachVo = bApReFundPayAttachMapper.selectByBApId(id);
        if (attachVo != null) {
            // 2.1 退款单附件（one_file）
            if (attachVo.getOne_file() != null) {
                SFileEntity fileEntity = fileMapper.selectById(attachVo.getOne_file());
                if (fileEntity != null) {
                    List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", fileEntity.getId()));
                    List<SFileInfoVo> oneFiles = new ArrayList<>();
                    for (SFileInfoEntity fileInfo : fileInfos) {
                        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                        fileInfoVo.setFileName(fileInfoVo.getFile_name());
                        oneFiles.add(fileInfoVo);
                    }
                    attachVo.setOne_files(oneFiles);
                }
            }
            // 2.2 凭证附件（two_file）
            if (attachVo.getTwo_file() != null) {
                SFileEntity fileEntity = fileMapper.selectById(attachVo.getTwo_file());
                if (fileEntity != null) {
                    List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", fileEntity.getId()));
                    List<SFileInfoVo> twoFiles = new ArrayList<>();
                    for (SFileInfoEntity fileInfo : fileInfos) {
                        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                        fileInfoVo.setFileName(fileInfoVo.getFile_name());
                        twoFiles.add(fileInfoVo);
                    }
                    attachVo.setTwo_files(twoFiles);
                }
            }
        }

        // 3. 处理凭证附件（向下兼容原有逻辑）
        if (bApReFundPayVo.getVoucher_file() != null) {
            SFileEntity file = fileMapper.selectById(bApReFundPayVo.getVoucher_file());
            if (file != null) {
                List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
                List<SFileInfoVo> voucherFiles = new ArrayList<>();
                for (SFileInfoEntity fileInfo : fileInfos) {
                    SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                    fileInfoVo.setFileName(fileInfoVo.getFile_name());
                    voucherFiles.add(fileInfoVo);
                }
                bApReFundPayVo.setVoucher_files(voucherFiles);
            }
        }

        // 4. 查询是否存在作废记录
        if (DictConstant.DICT_B_AP_REFUND_PAY_STATUS_TWO.equals(bApReFundPayVo.getStatus())) {
            // 构造作废查询条件
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bApReFundPayVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                bApReFundPayVo.setCancel_reason(mCancelVo.getRemark());
                if (mCancelVo.getFile_id() != null) {
                    SFileEntity cancelFileEntity = fileMapper.selectById(mCancelVo.getFile_id());
                    if (cancelFileEntity != null) {
                        List<SFileInfoEntity> cancelFileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", cancelFileEntity.getId()));
                        List<SFileInfoVo> cancelFiles = new ArrayList<>();
                        for (SFileInfoEntity fileInfo : cancelFileInfos) {
                            SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                            fileInfoVo.setFileName(fileInfoVo.getFile_name());
                            cancelFiles.add(fileInfoVo);
                        }
                        bApReFundPayVo.setCancel_files(cancelFiles);
                    }
                }
            }
        }

        return bApReFundPayVo;
    }

    /**
     * 凭证上传、完成退款
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundPayVo> refundComplete(BApReFundPayVo vo) {
        // 1. 业务校验
        validateRefundComplete(vo);
        
        // 2. 更新退款单主表状态
        BApReFundPayEntity bApReFundPayEntity = updateRefundCompletionStatus(vo);
        
        // 3. 更新退款单明细表状态
        updateRefundCompletionDetails(vo.getId());
        
        // 4. 处理凭证附件上传
        processVoucherAttachment(bApReFundPayEntity, vo);
        
        // 5. 重算总金额数据
        recalculateTotalData(vo.getId());
        
        // 6. 更新应付退款主表状态
        updateApRefundPaymentStatus(bApReFundPayEntity.getAp_refund_id());
        
        // 7. 更新资金流水表
        updateFundFlow(vo.getId());
        
        vo.setId(bApReFundPayEntity.getId());
        return UpdateResultUtil.OK(vo);
    }

    /**
     * 根据应退金额和已退金额确定退款状态
     * @param refundAmount 应退总金额
     * @param refundedAmount 已退总金额
     * @return 退款状态：1-部分退款，2-已退款
     */
    private String determineRefundStatus(BigDecimal refundAmount, BigDecimal refundedAmount) {
        if (refundAmount == null || refundedAmount == null) {
            return null;
        }
        
        int comparison = refundAmount.compareTo(refundedAmount);
        if (comparison == 0) {
            // 应退总金额 = 已退总金额，设置为已退款
            return "2";
        } else if (comparison > 0) {
            // 应退总金额 > 已退总金额，设置为部分退款
            return "1";
        }
        
        return null;
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
        
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_TWO);

        // 2.保存作废附件和作废原因到附件表
        BApReFundPayAttachVo attachVo = bApReFundPayAttachMapper.selectByBApId(searchCondition.getId());

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
            if (originalStatus.equals(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ONE)) {
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
     * @param vo 退款单Vo
     */
    private void checkInsertLogic(BApReFundPayVo vo) {
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 通用校验逻辑
     * @param vo 退款单Vo
     * @param checkType 校验类型
     * @return 校验结果
     */
    @Override
    public CheckResultAo checkLogic(BApReFundPayVo vo, String checkType) {
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增校验逻辑
                // 1. 退款日期不能为空
                if (vo.getRefund_date() == null) {
                    return CheckResultUtil.NG("退款日期不能为空");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新校验逻辑
                // 1. ID不能为空
                if (vo.getId() == null) {
                    return CheckResultUtil.NG("退款单ID不能为空");
                }
                // 2. 退款日期不能为空
                if (vo.getRefund_date() == null) {
                    return CheckResultUtil.NG("退款日期不能为空");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 删除校验逻辑
                // 1. ID不能为空
                if (vo.getId() == null) {
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
     * @param vo 查询条件
     * @return 汇总结果
     */
    @Override
    public BApReFundPayVo querySum(BApReFundPayVo vo) {
        return mapper.querySum(vo);
    }

    /**
     * 单条汇总查询
     * @param vo 查询条件
     * @return 汇总结果
     */
    @Override
    public BApReFundPayVo queryViewSum(BApReFundPayVo vo) {
        return mapper.queryViewSum(vo);
    }

    /**
     * 退款完成业务校验
     * @param vo 退款单信息
     */
    private void validateRefundComplete(BApReFundPayVo vo) {
        CheckResultAo cr = checkLogic(vo, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新退款单主表状态
     * @param vo 退款单信息
     * @return 更新后的退款单实体
     */
    private BApReFundPayEntity updateRefundCompletionStatus(BApReFundPayVo vo) {
        BApReFundPayEntity bApReFundPayEntity = mapper.selectById(vo.getId());
        bApReFundPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_TWO);
        bApReFundPayEntity.setVoucher_remark(vo.getVoucher_remark());
        
        int updateResult = mapper.updateById(bApReFundPayEntity);
        if (updateResult == 0) {
            throw new BusinessException("退款单状态更新失败");
        }
        
        return bApReFundPayEntity;
    }

    /**
     * 更新退款单明细表状态（设置为已退款）
     * @param apRefundPayId 退款单ID
     */
    private void updateRefundCompletionDetails(Integer apRefundPayId) {
        List<BApReFundPayDetailVo> apRefundPayDetailVos = bApReFundPayDetailMapper.selectById(apRefundPayId);
        for (BApReFundPayDetailVo detailVo : apRefundPayDetailVos) {
            BApReFundPayDetailEntity bApReFundPayDetailEntity = (BApReFundPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApReFundPayDetailEntity.class);
            
            // 已退款操作，设置相关金额字段
            bApReFundPayDetailEntity.setRefunded_amount(bApReFundPayDetailEntity.getOrder_amount());  // 已退金额 = 本次退款金额
            bApReFundPayDetailEntity.setRefunding_amount(BigDecimal.ZERO);                             // 退款中金额清零
            bApReFundPayDetailEntity.setUnrefund_amount(BigDecimal.ZERO);                              // 未退金额清零
            bApReFundPayDetailEntity.setCancel_amount(BigDecimal.ZERO);                                // 作废金额清零
            
            int updateResult = bApReFundPayDetailMapper.updateById(bApReFundPayDetailEntity);
            if (updateResult == 0) {
                throw new BusinessException("退款单明细更新失败");
            }
        }
    }

    /**
     * 处理凭证附件上传
     * @param bApReFundPayEntity 退款单实体
     * @param vo 退款单VO
     */
    private void processVoucherAttachment(BApReFundPayEntity bApReFundPayEntity, BApReFundPayVo vo) {
        // 上传附件文件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundPayEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND_PAY);
        SFileEntity sFileEntity = insertFile(fileEntity, vo.getVoucher_files());

        // 更新或新增附件关联记录
        BApReFundPayAttachVo bApReFundPayAttachVo = bApReFundPayAttachMapper.selectByBApId(bApReFundPayEntity.getId());
        if (bApReFundPayAttachVo == null) {
            // 新增附件记录
            insertRefundPaymentAttachment(bApReFundPayEntity, sFileEntity);
        } else {
            // 更新附件记录
            updateRefundPaymentAttachment(bApReFundPayAttachVo, sFileEntity);
        }
    }

    /**
     * 重算总金额数据
     * @param apRefundPayId 退款单ID
     */
    private void recalculateTotalData(Integer apRefundPayId) {
//        commonFundService.reCalculateAllTotalDataByApRefundPayId(apRefundPayId);
    }

    /**
     * 更新应付退款主表的退款状态
     * @param apRefundId 应付退款主表ID
     */
    private void updateApRefundPaymentStatus(Integer apRefundId) {
        // 获取应付退款总表的应退金额
        BApReFundTotalVo bApReFundTotalVo = bApReFundTotalMapper.selectByApRefundId(apRefundId);
        if (bApReFundTotalVo == null) {
            return;
        }

        // 获取退款单的已退金额汇总（状态=1表示已退款）
        BApReFundPayVo refundAmountSummary = mapper.getSumAmount(apRefundId, DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ONE);
        BigDecimal refundedAmountTotal = refundAmountSummary != null && refundAmountSummary.getRefunded_amount_total() != null 
            ? refundAmountSummary.getRefunded_amount_total() : BigDecimal.ZERO;

        // 比较金额并更新应付退款主表状态
        BApReFundEntity bApReFundEntity = bApReFundMapper.selectById(apRefundId);
        if (bApReFundEntity == null) {
            return;
        }

        // 根据已退金额判断退款状态
        BigDecimal totalRefundableAmount = bApReFundTotalVo.getRefundable_amount_total() != null 
            ? bApReFundTotalVo.getRefundable_amount_total() : BigDecimal.ZERO;
        
        if (refundedAmountTotal.compareTo(totalRefundableAmount) >= 0) {
            // 已退金额 >= 应退金额，设置为已退款
            bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_TWO);
        } else if (refundedAmountTotal.compareTo(BigDecimal.ZERO) > 0) {
            // 已退金额 > 0，设置为部分退款
            bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ONE);
        }
        
        bApReFundMapper.updateById(bApReFundEntity);
    }

    /**
     * 更新资金流水表
     * @param apRefundPayId 退款单ID
     */
    private void updateFundFlow(Integer apRefundPayId) {
//        commonFundService.increaseRefundAmount(apRefundPayId);
    }

    /**
     * 新增退款单附件记录
     * @param bApReFundPayEntity 退款单实体
     * @param sFileEntity 文件实体
     */
    private void insertRefundPaymentAttachment(BApReFundPayEntity bApReFundPayEntity, SFileEntity sFileEntity) {
        BApReFundPayAttachEntity bApReFundPayAttachEntity = new BApReFundPayAttachEntity();
        bApReFundPayAttachEntity.setTwo_file(sFileEntity.getId());
        bApReFundPayAttachEntity.setAp_refund_pay_code(bApReFundPayEntity.getCode());
        bApReFundPayAttachEntity.setAp_refund_pay_id(bApReFundPayEntity.getId());
        
        int insertResult = bApReFundPayAttachMapper.insert(bApReFundPayAttachEntity);
        if (insertResult == 0) {
            throw new BusinessException("退款单附件新增失败");
        }
    }

    /**
     * 更新退款单附件记录
     * @param bApReFundPayAttachVo 退款单附件VO
     * @param sFileEntity 文件实体
     */
    private void updateRefundPaymentAttachment(BApReFundPayAttachVo bApReFundPayAttachVo, SFileEntity sFileEntity) {
        BApReFundPayAttachEntity bApReFundPayAttachEntity = (BApReFundPayAttachEntity) BeanUtilsSupport.copyProperties(bApReFundPayAttachVo, BApReFundPayAttachEntity.class);
        bApReFundPayAttachEntity.setTwo_file(sFileEntity.getId());
        
        int updateResult = bApReFundPayAttachMapper.updateById(bApReFundPayAttachEntity);
        if (updateResult == 0) {
            throw new BusinessException("退款单附件更新失败");
        }
        
        bApReFundPayAttachVo.setTwo_file(sFileEntity.getId());
    }
}
