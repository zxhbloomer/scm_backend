package com.xinyirun.scm.core.system.serviceimpl.business.appay;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.entity.busniess.appay.*;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApTotalVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayAttachVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.config.event.servcie.FundService;
import com.xinyirun.scm.core.system.mapper.business.ap.BApDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.*;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonTotalService;
import com.xinyirun.scm.core.system.service.business.appay.IBApPayService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApPayAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApPayDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 付款单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BApPayServiceImpl extends ServiceImpl<BApPayMapper, BApPayEntity> implements IBApPayService {

    @Autowired
    private BApPayMapper mapper;

    @Autowired
    private BApPayDetailMapper bApPayDetailMapper;

    @Autowired
    private BApPayAttachMapper bApPayAttachMapper;    @Autowired
    private BApMapper bApMapper;

    @Autowired
    private BApTotalMapper bApTotalMapper;

    @Autowired
    private BApDetailMapper bApDetailMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BApPayAutoCodeServiceImpl bApPayAutoCodeService;

    @Autowired
    private BApPayDetailAutoCodeServiceImpl bApPayDetailAutoCodeService;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private BApSourceMapper bApSourceMapper;

    @Autowired
    private BApSourceAdvanceMapper bApSourceAdvanceMapper;

    @Autowired
    private BApPaySourceMapper bApPaySourceMapper;

    @Autowired
    private BApPaySourceAdvanceMapper bApPaySourceAdvanceMapper;

    @Autowired
    private ICommonTotalService commonTotalService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private FundService fundService;

    /**
     * 付款单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApPayVo> startInsert(BApPayVo searchCondition) {
        // 1、校验
        checkInsertLogic(searchCondition);
        
        // 2.保存付款单
        InsertResultAo<BApPayVo> insertResultAo = insert(searchCondition);

        // 3.total数据重算
        commonTotalService.reCalculateAllTotalDataByApPayId(searchCondition.getId());
        
        return insertResultAo;
    }

    /**
     * 下推付款单
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApPayVo> insert(BApPayVo searchCondition) {
        // 2、初始化数据
        initInsertApPayData(searchCondition);
        // 3、计算金额
        calcAmountInsertPay(searchCondition);
        // 4、保存主表
        BApPayEntity bApPayEntity = savePayMain(searchCondition);
        // 5、保存明细
        savePayDetailList(searchCondition, bApPayEntity);
        // 6、保存附件
        savePayAttach(searchCondition, bApPayEntity);
        // 7、保存源单
        savePaySource(searchCondition, bApPayEntity);
        // 8、保存源单-预收款
        savePaySourceAdvance(searchCondition, bApPayEntity);
        // 9、更新应付账款主表金额
//        updateApPayingAmount(searchCondition);
        // 10、资金流水监控（如需）
        // commonFundService.startApPayFund(bApPayEntity.getId());
        searchCondition.setId(bApPayEntity.getId());
        return InsertResultUtil.OK(searchCondition);
    }

    /** 校验逻辑 */
    private void checkInsertLogic(BApPayVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /** 保存主表 */
    private BApPayEntity savePayMain(BApPayVo searchCondition) {
        BApPayEntity bApPayEntity = (BApPayEntity) BeanUtilsSupport.copyProperties(searchCondition, BApPayEntity.class);
        bApPayEntity.setStatus(DictConstant.DICT_B_AP_PAY_BILL_STATUS_ZERO);
        int bApPay = mapper.insert(bApPayEntity);
        if (bApPay == 0){
            throw new BusinessException("新增付款单，新增失败");
        }
        return bApPayEntity;
    }

    /**
     * 新增操作
     * 保存明细
     *
     * */
    private void savePayDetailList(BApPayVo searchCondition, BApPayEntity bApPayEntity) {
        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData())) {
            for (BApPayDetailVo detailVo : searchCondition.getDetailListData()) {
                BApPayDetailEntity bApPayDetailEntity = (BApPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApPayDetailEntity.class);
                bApPayDetailEntity.setAp_pay_id(bApPayEntity.getId());
                bApPayDetailEntity.setAp_pay_code(bApPayEntity.getCode());
                /**
                 * 新增操作，所以状态必定是0-待付款
                 * paid_amount=0
                 * paying_amount=pay_amount
                 * unpay_amount=pay_amount
                 * cancel_amount=0
                 */
                bApPayDetailEntity.setPaid_amount(BigDecimal.ZERO);
                bApPayDetailEntity.setPaying_amount(bApPayDetailEntity.getPay_amount());
                bApPayDetailEntity.setUnpay_amount(bApPayDetailEntity.getPay_amount());
                bApPayDetailEntity.setCancel_amount(BigDecimal.ZERO);

                bApPayDetailEntity.setId(null);
                int bApPayDetail = bApPayDetailMapper.insert(bApPayDetailEntity);
                if (bApPayDetail == 0){
                    throw new BusinessException("新增付款单明细，新增失败");
                }
            }
        }
    }

    /** 保存附件 */
    private void savePayAttach(BApPayVo searchCondition, BApPayEntity bApPayEntity) {
        if (CollectionUtil.isNotEmpty(searchCondition.getDoc_att_files())) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApPayEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_PAY);
            fileMapper.insert(fileEntity);
            for (SFileInfoVo docAttFile : searchCondition.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                docAttFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(docAttFile, fileInfoEntity);
                fileInfoEntity.setFile_name(docAttFile.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            BApPayAttachEntity bApPayAttachEntity = new BApPayAttachEntity();
            bApPayAttachEntity.setAp_pay_id(bApPayEntity.getId());
            bApPayAttachEntity.setAp_pay_code(bApPayEntity.getCode());
            bApPayAttachEntity.setOne_file(fileEntity.getId());
            int bApPayAttach = bApPayAttachMapper.insert(bApPayAttachEntity);
            if (bApPayAttach == 0) {
                throw new BusinessException("新增付款单附件，新增失败");
            }
        }
    }

    /** 保存源单 */
    private void savePaySource(BApPayVo searchCondition, BApPayEntity bApPayEntity) {
        List<BApSourceVo> apSourceList = bApSourceMapper.selectByApId(searchCondition.getAp_id());
        if (CollectionUtil.isNotEmpty(apSourceList)) {
            for (BApSourceVo apSourceVo : apSourceList) {
                BApPaySourceEntity paySourceEntity = new BApPaySourceEntity();
                paySourceEntity.setAp_pay_id(bApPayEntity.getId());
                paySourceEntity.setAp_pay_code(bApPayEntity.getCode());
                paySourceEntity.setAp_id(apSourceVo.getAp_id());
                paySourceEntity.setAp_code(apSourceVo.getAp_code());
                paySourceEntity.setType(apSourceVo.getType());
                paySourceEntity.setProject_code(apSourceVo.getProject_code());
                paySourceEntity.setPo_contract_id(apSourceVo.getPo_contract_id());
                paySourceEntity.setPo_contract_code(apSourceVo.getPo_contract_code());
                paySourceEntity.setPo_order_code(apSourceVo.getPo_order_code());
                paySourceEntity.setPo_order_id(apSourceVo.getPo_order_id());
                bApPaySourceMapper.insert(paySourceEntity);
            }
        }
    }

    /** 保存源单-预收款 */
    private void savePaySourceAdvance(BApPayVo searchCondition, BApPayEntity bApPayEntity) {
        List<BApSourceAdvanceVo> apSourceAdvanceList = bApSourceAdvanceMapper.selectByApId(searchCondition.getAp_id());
        if (CollectionUtil.isNotEmpty(apSourceAdvanceList)) {
            for (BApSourceAdvanceVo apSourceAdvanceVo : apSourceAdvanceList) {
                BApPaySourceAdvanceEntity paySourceAdvanceEntity = new BApPaySourceAdvanceEntity();
                paySourceAdvanceEntity.setAp_pay_id(bApPayEntity.getId());
                paySourceAdvanceEntity.setAp_pay_code(bApPayEntity.getCode());
                paySourceAdvanceEntity.setAp_id(apSourceAdvanceVo.getAp_id());
                paySourceAdvanceEntity.setAp_code(apSourceAdvanceVo.getAp_code());
                paySourceAdvanceEntity.setType(apSourceAdvanceVo.getType());
                paySourceAdvanceEntity.setPo_contract_id(apSourceAdvanceVo.getPo_contract_id());
                paySourceAdvanceEntity.setPo_contract_code(apSourceAdvanceVo.getPo_contract_code());
                paySourceAdvanceEntity.setPo_order_code(apSourceAdvanceVo.getPo_order_code());
                paySourceAdvanceEntity.setPo_order_id(apSourceAdvanceVo.getPo_order_id());
                paySourceAdvanceEntity.setPo_goods(apSourceAdvanceVo.getPo_goods());
                paySourceAdvanceEntity.setQty_total(apSourceAdvanceVo.getQty_total());
                paySourceAdvanceEntity.setAmount_total(apSourceAdvanceVo.getAmount_total());
                paySourceAdvanceEntity.setPo_advance_payment_amount(apSourceAdvanceVo.getPo_advance_payment_amount());
                paySourceAdvanceEntity.setOrder_amount(apSourceAdvanceVo.getOrder_amount());
                paySourceAdvanceEntity.setRemark(apSourceAdvanceVo.getRemark());
                bApPaySourceAdvanceMapper.insert(paySourceAdvanceEntity);
            }
        }
    }

    /** 更新应付账款主表金额 */
    private void updateApPayingAmount(BApPayVo searchCondition) {
        BApEntity bApEntity = bApMapper.selectById(searchCondition.getAp_id());
        if (bApEntity != null) {
//            BigDecimal currentPayingAmount = bApEntity.getPaying_amount() != null ? bApEntity.getPaying_amount() : BigDecimal.ZERO;
//            BigDecimal newPayAmount = searchCondition.getPay_amount_total() != null ? searchCondition.getPay_amount_total() : BigDecimal.ZERO;
//            bApEntity.setPaying_amount(currentPayingAmount.add(newPayAmount));
            int bAp = bApMapper.updateById(bApEntity);
            if (bAp == 0){
                throw new BusinessException("更新应付账款主表失败");
            }
        }
    }

    /**
     * 列表查询
     * @param searchCondition
     */
    @Override
    public IPage<BApPayVo> selectPage(BApPayVo searchCondition) {
        // 分页条件
        Page<BApPayVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
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
    public BApPayVo selectById(Integer id) {
        BApPayVo bApPayVo = mapper.selById(id);
        if (bApPayVo == null) {
            return null;
        }
        // 1. 查询付款单明细
        bApPayVo.setDetailListData(mapper.getApPayDetail(id));

        // 2. 查询付款单附件
        BApPayAttachVo attachVo = bApPayAttachMapper.selectByBApId(id);
        if (attachVo != null) {
            // 2.1 付款单附件（one_file）
            if (attachVo.getOne_file() != null) {
                SFileEntity fileEntity = fileMapper.selectById(attachVo.getOne_file());
                if (fileEntity != null) {
                    List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", fileEntity.getId()));
                    List<SFileInfoVo> docAttFiles = new ArrayList<>();
                    for (SFileInfoEntity fileInfo : fileInfos) {
                        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                        fileInfoVo.setFileName(fileInfoVo.getFile_name());
                        docAttFiles.add(fileInfoVo);
                    }
                    bApPayVo.setDoc_att_files(docAttFiles);
                }
            }
            // 2.2 凭证附件（two_file）
            if (attachVo.getTwo_file() != null) {
                SFileEntity fileEntity = fileMapper.selectById(attachVo.getTwo_file());
                if (fileEntity != null) {
                    List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", fileEntity.getId()));
                    List<SFileInfoVo> voucherFiles = new ArrayList<>();
                    for (SFileInfoEntity fileInfo : fileInfos) {
                        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                        fileInfoVo.setFileName(fileInfoVo.getFile_name());
                        voucherFiles.add(fileInfoVo);
                    }
                    bApPayVo.setVoucher_files(voucherFiles);
                }
            }
        }

        // 4. 查询是否存在作废记录
        if (DictConstant.DICT_B_AP_PAY_BILL_STATUS_TWO.equals(bApPayVo.getStatus())) {
            // 构造作废查询条件
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bApPayVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_PAY);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);

            if (mCancelVo != null) {
                // 作废原因
                bApPayVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = fileInfoMapper.selectFIdList(mCancelVo.getFile_id());
                    bApPayVo.setCancel_doc_att_files(cancel_doc_att_files);
                }
                // 作废人
                if (mCancelVo.getC_id() != null) {
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    bApPayVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());
                }
                // 作废时间
                bApPayVo.setCancel_time(mCancelVo.getC_time());
            }
        }
        // 3、获取应付咱还数据b_ap_pay
        BApVo bApVo = bApMapper.selectId(bApPayVo.getAp_id());
        bApPayVo.setPay_status(bApVo.getPay_status());
        bApPayVo.setPay_status_name(bApVo.getPay_status_name());
        return bApPayVo;
    }

     /**
     * 付款复核
     *
     * @param searchCondition
     */    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApPayVo> payComplete(BApPayVo searchCondition) {
        // 1. 业务校验
        validatePayComplete(searchCondition);
        
        // 2. 更新付款单主表状态
        BApPayEntity bApPayEntity = updatePaymentStatus(searchCondition);
        
        // 3. 更新付款单明细表状态
        updatePaymentDetails(searchCondition.getId());
        
        // 4. 处理附件上传
        processVoucherAttachment(bApPayEntity, searchCondition);
        
        // 5. 重算总金额数据
        recalculateTotalData(searchCondition.getId());
        
        // 6. 更新应付账款主表付款状态
        updateApPaymentStatus(bApPayEntity.getAp_id());
        
        // 7. 更新资金流水表
        updateFundFlow(searchCondition.getId());
        
        searchCondition.setId(bApPayEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApPayVo> cancel(BApPayVo searchCondition) {

        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.作废付款单状态
        BApPayEntity bApPayEntity = mapper.selectById(searchCondition.getId());
        String orignalStatus = bApPayEntity.getStatus();

        bApPayEntity.setStatus(DictConstant.DICT_B_AP_PAY_BILL_STATUS_TWO);

        // 2.保存作废附件和作废原因到附件表
        BApPayAttachVo attachVo = bApPayAttachMapper.selectByBApId(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(searchCondition.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_PAY);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_PAY);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        int bApPay = mapper.updateById(bApPayEntity);
        if (bApPay == 0){
            throw new BusinessException("作废，更新失败");
        }
        searchCondition.setId(bApPayEntity.getId());

        // 更新付款单明细表状态
        List<BApPayDetailVo> apPayDetailVos = bApPayDetailMapper.selectById(searchCondition.getId());
        for (BApPayDetailVo detailVo : apPayDetailVos) {
            BApPayDetailEntity bApPayDetailEntity = (BApPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApPayDetailEntity.class);
                /**
                 * 作废操作，所以状态必定是2-作废
                 * paid_amount=0
                 * paying_amount=0
                 * unpay_amount=0
                 * cancel_amount=if bApPayEntity.status (源状态)= 1-已付款，则为pay_amount，否则为0
                 */
            if (orignalStatus.equals(DictConstant.DICT_B_AP_PAY_BILL_STATUS_ONE)) {
                bApPayDetailEntity.setCancel_amount(bApPayDetailEntity.getPay_amount());
            } else {
                bApPayDetailEntity.setCancel_amount(BigDecimal.ZERO);
            }
            bApPayDetailEntity.setPaid_amount(BigDecimal.ZERO);
            bApPayDetailEntity.setPaying_amount(BigDecimal.ZERO);
            bApPayDetailEntity.setUnpay_amount(BigDecimal.ZERO);

            int bApPayDetail = bApPayDetailMapper.updateById(bApPayDetailEntity);
            if (bApPayDetail == 0){
                throw new BusinessException("作废付款单明细，更新失败");
            }
        }

        // 3.total数据重算
        commonTotalService.reCalculateAllTotalDataByApPayId(searchCondition.getId());

        // 更新资金流水表
//        fundService.decreaseAdvanceAmount(searchCondition.getId());

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

    /**
     * 作废附件处理
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BApPayVo vo) {
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
     * 任务10：初始化新增付款单数据
     * 功能说明：通过ap_id，查找b_ap、b_ap_detail表数据，并填充BApPayVo
     * @param searchCondition 付款单Vo
     */
    private void initInsertApPayData(BApPayVo searchCondition) {
        // 1、查询b_ap表
        BApVo bApVo = bApMapper.selectId(searchCondition.getAp_id());
        if (bApVo != null) {
            // set数据
            searchCondition.setCode(bApPayAutoCodeService.autoCode().getCode());
            searchCondition.setAp_id(bApVo.getId());
            searchCondition.setAp_code(bApVo.getCode());
            searchCondition.setStatus(DictConstant.DICT_B_AP_PAY_BILL_STATUS_ZERO); // 1-待付款：使用常量
            searchCondition.setType(bApVo.getType());
            searchCondition.setSupplier_id(bApVo.getSupplier_id());
            searchCondition.setSupplier_code(bApVo.getSupplier_code());
            searchCondition.setSupplier_name(bApVo.getSupplier_name());
            searchCondition.setPurchaser_id(bApVo.getPurchaser_id());
            searchCondition.setPurchaser_code(bApVo.getPurchaser_code());
            searchCondition.setPurchaser_name(bApVo.getPurchaser_name());
//            searchCondition.setPayment_type(bApVo.getPayment_type());
            List<BApPayDetailVo>  apPayDetailVos = searchCondition.getDetailListData();
            BigDecimal payAmountTotal = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(apPayDetailVos)) {
                for (BApPayDetailVo detail : apPayDetailVos) {
                    if (detail.getPay_amount() != null) {
                        payAmountTotal = payAmountTotal.add(detail.getPay_amount());
                    }
                }
            }
            searchCondition.setPayable_amount_total(payAmountTotal);
            searchCondition.setPaid_amount_total(bApVo.getPaid_amount_total());
        }

        // 2、查询b_ap_detail表
        List<BApDetailVo> bApDetailVos = bApMapper.getApDetail(searchCondition.getAp_id());
        
        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData()) && CollectionUtil.isNotEmpty(bApDetailVos)) {
            // BApPayVo.detailListData进行循环
            for (BApPayDetailVo payDetailVo : searchCondition.getDetailListData()) {
                // 和返回List<BApDetailVo>比较（此处还有个循环）
                for (BApDetailVo apDetailVo : bApDetailVos) {
                    // 当BApDetailVo.code = BApPayVo.detailListData.ap_detail_code
                    if (apDetailVo.getCode().equals(payDetailVo.getCode())) {
                        // BApPayVo.detailListData数据进行set
                        payDetailVo.setCode(bApPayDetailAutoCodeService.autoCode().getCode());
                        payDetailVo.setAp_id(bApVo.getId());
                        payDetailVo.setAp_code(bApVo.getCode());
                        payDetailVo.setAp_detail_id(apDetailVo.getId());
                        payDetailVo.setAp_detail_code(apDetailVo.getCode());
                        payDetailVo.setBank_accounts_id(apDetailVo.getBank_accounts_id());
                        payDetailVo.setBank_accounts_code(apDetailVo.getBank_accounts_code());
                        payDetailVo.setBank_accounts_type_id(apDetailVo.getBank_accounts_type_id());
                        payDetailVo.setBank_accounts_type_code(apDetailVo.getBank_accounts_type_code());
                        payDetailVo.setPayable_amount(apDetailVo.getPayable_amount());
                        break; // 找到匹配项后跳出内层循环
                    }
                }
            }
        }
    }

    /**
     * 任务11：计算金额
     * 功能说明：计算金额
     * @param searchCondition 付款单Vo
     */
    private void calcAmountInsertPay(BApPayVo searchCondition) {
        BigDecimal payableAmountTotal = BigDecimal.ZERO;
        BigDecimal paidAmountTotal = BigDecimal.ZERO;
        BigDecimal payAmountTotal = BigDecimal.ZERO;

        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData())) {
            // BApPayVo.detailListData进行循环
            for (BApPayDetailVo detail : searchCondition.getDetailListData()) {
                // paid_amount已付款金额=0
                detail.setPaid_amount(BigDecimal.ZERO);
                
                // 累计计算各种金额
                if (detail.getPayable_amount() != null) {
                    payableAmountTotal = payableAmountTotal.add(detail.getPayable_amount());
                }
                if (detail.getPaid_amount() != null) {
                    paidAmountTotal = paidAmountTotal.add(detail.getPaid_amount());
                }
                if (detail.getPay_amount() != null) {
                    payAmountTotal = payAmountTotal.add(detail.getPay_amount());
                }
            }
        }

        // 设置汇总金额
        searchCondition.setPayable_amount_total(payableAmountTotal); // 付款单计划付款总金额
        searchCondition.setPaid_amount_total(paidAmountTotal); // 付款单已付款总金额
//        searchCondition.setPay_amount_total(payAmountTotal); // 付款单总金额
    }

    /**
     * 付款单校验逻辑
     * <p>
     * 用于校验付款单主表及明细的业务规则，包括：
     * 1. 付款日期不能为空
     * 2. 明细数据不能为空
     * 3. 明细付款金额必须大于0
     * 4. 明细付款金额不能大于未付款金额（pay_amount <= unpay_amount）
     *
     * @param searchCondition 付款单Vo
     * @param checkType 校验类型（如新增、更新、删除等）
     * @return 校验结果
     */
    @Override
    public CheckResultAo checkLogic(BApPayVo searchCondition, String checkType) {
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 校验付款日期是否为空
                if (searchCondition.getPay_date() == null) {
                    return CheckResultUtil.NG("校验出错:请输入付款日期。");
                }
                
                // 校验明细数据是否存在
                if (searchCondition.getDetailListData() == null || searchCondition.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个付款明细");
                }

                // 校验付款金额必须大于0，且不能大于未付款金额
                for (BApPayDetailVo detail : searchCondition.getDetailListData()) {
                    if (detail.getPay_amount() == null || detail.getPay_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：请输入付款指令金额大于0的数据。");
                    }
                    // pay_amount不能大于unpay_amount
                    if (detail.getUnpay_amount() != null && detail.getPay_amount() != null
                        && detail.getPay_amount().compareTo(detail.getUnpay_amount()) > 0) {
                        return CheckResultUtil.NG("输入错误：输入的付款指令金额需要小于等于未付款金额");
                    }
                }
                break;

            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }
                
                // 校验付款日期是否为空
                if (searchCondition.getPay_date() == null) {
                    return CheckResultUtil.NG("校验出错:请输入付款日期。");
                }
                
                // 校验明细数据是否存在
                if (searchCondition.getDetailListData() == null || searchCondition.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个付款明细");
                }

                // 校验付款金额必须大于0，且不能大于未付款金额
                for (BApPayDetailVo detail : searchCondition.getDetailListData()) {
                    if (detail.getPay_amount() == null || detail.getPay_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：请输入付款指令金额大于0的数据。");
                    }
                    // pay_amount不能大于unpay_amount
                    if (detail.getUnpay_amount() != null && detail.getPay_amount() != null
                        && detail.getPay_amount().compareTo(detail.getUnpay_amount()) > 0) {
                        return CheckResultUtil.NG("输入错误：输入的付款指令金额需要小于等于未付款金额");
                    }
                }
                break;

            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }
                break;
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (searchCondition.getVoucher_files() == null) {
                    return CheckResultUtil.NG("凭证附件为必须输入项目，请上传凭证附件。");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
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
    public BApPayVo querySum(BApPayVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Override
    public BApPayVo queryViewSum(BApPayVo searchCondition) {
        return mapper.queryViewSum(searchCondition);
    }

// ==================== 付款完成业务方法拆分 ====================

    /**
     * 验证付款完成业务逻辑
     * @param searchCondition 付款单信息
     */
    private void validatePayComplete(BApPayVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新付款单主表状态
     * @param searchCondition 付款单信息
     * @return 更新后的付款单实体
     */
    private BApPayEntity updatePaymentStatus(BApPayVo searchCondition) {
        BApPayEntity bApPayEntity = mapper.selectById(searchCondition.getId());
        bApPayEntity.setStatus(DictConstant.DICT_B_AP_PAY_BILL_STATUS_ONE);
        bApPayEntity.setPay_date(searchCondition.getPay_date());
        bApPayEntity.setVoucher_remark(searchCondition.getVoucher_remark());
        
        int updateResult = mapper.updateById(bApPayEntity);
        if (updateResult == 0) {
            throw new BusinessException("付款单状态更新失败");
        }
        
        return bApPayEntity;
    }

    /**
     * 更新付款单明细表状态（设置为已付款）
     * @param apPayId 付款单ID
     */
    private void updatePaymentDetails(Integer apPayId) {
        List<BApPayDetailVo> apPayDetailVos = bApPayDetailMapper.selectById(apPayId);
        for (BApPayDetailVo detailVo : apPayDetailVos) {
            BApPayDetailEntity bApPayDetailEntity = (BApPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApPayDetailEntity.class);
            
            // 已付款操作，设置相关金额字段
            bApPayDetailEntity.setPaid_amount(bApPayDetailEntity.getPay_amount());  // 已付金额 = 本次付款金额
            bApPayDetailEntity.setPaying_amount(BigDecimal.ZERO);                   // 付款中金额清零
            bApPayDetailEntity.setUnpay_amount(BigDecimal.ZERO);                    // 未付金额清零
            bApPayDetailEntity.setCancel_amount(BigDecimal.ZERO);                   // 作废金额清零
            
            int updateResult = bApPayDetailMapper.updateById(bApPayDetailEntity);
            if (updateResult == 0) {
                throw new BusinessException("付款单明细更新失败");
            }
        }
    }

    /**
     * 处理付款凭证附件
     * @param bApPayEntity 付款单实体
     * @param searchCondition 付款单信息（包含附件）
     */
    private void processVoucherAttachment(BApPayEntity bApPayEntity, BApPayVo searchCondition) {
        // 上传附件文件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApPayEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_PAY);
        SFileEntity sFileEntity = insertFile(fileEntity, searchCondition.getVoucher_files());

        // 更新或新增附件关联记录
        BApPayAttachVo bApPayAttachVo = bApPayAttachMapper.selectByBApId(bApPayEntity.getId());
        if (bApPayAttachVo == null) {
            // 新增附件记录
            insertPaymentAttachment(bApPayEntity, sFileEntity);
        } else {
            // 更新附件记录
            updatePaymentAttachment(bApPayAttachVo, sFileEntity);
        }
    }

    /**
     * 新增付款单附件记录
     * @param bApPayEntity 付款单实体
     * @param sFileEntity 文件实体
     */
    private void insertPaymentAttachment(BApPayEntity bApPayEntity, SFileEntity sFileEntity) {
        BApPayAttachEntity bApPayAttachEntity = new BApPayAttachEntity();
        bApPayAttachEntity.setTwo_file(sFileEntity.getId());
        bApPayAttachEntity.setAp_pay_code(bApPayEntity.getCode());
        bApPayAttachEntity.setAp_pay_id(bApPayEntity.getId());
        
        int insertResult = bApPayAttachMapper.insert(bApPayAttachEntity);
        if (insertResult == 0) {
            throw new BusinessException("付款单附件新增失败");
        }
    }

    /**
     * 更新付款单附件记录
     * @param bApPayAttachVo 原附件记录
     * @param sFileEntity 新文件实体
     */
    private void updatePaymentAttachment(BApPayAttachVo bApPayAttachVo, SFileEntity sFileEntity) {
        BApPayAttachEntity bApPayAttachEntity = (BApPayAttachEntity) BeanUtilsSupport.copyProperties(bApPayAttachVo, BApPayAttachEntity.class);
        bApPayAttachEntity.setTwo_file(sFileEntity.getId());
        
        int updateResult = bApPayAttachMapper.updateById(bApPayAttachEntity);
        if (updateResult == 0) {
            throw new BusinessException("付款单附件更新失败");
        }
        
        bApPayAttachVo.setTwo_file(sFileEntity.getId());
    }

    /**
     * 重新计算总金额数据
     * @param apPayId 付款单ID
     */
    private void recalculateTotalData(Integer apPayId) {
        commonTotalService.reCalculateAllTotalDataByApPayId(apPayId);
    }

    /**
     * 更新应付账款主表的付款状态
     * @param apId 应付账款主表ID
     */
    private void updateApPaymentStatus(Integer apId) {
        // 获取应付账款总表的应付金额
        BApTotalVo bApTotalVo = bApTotalMapper.selectByApId(apId);
        if (bApTotalVo == null) {
            return;
        }

        // 获取付款单的已付金额汇总（状态=1表示已付款）
        BApPayVo payAmountSummary = mapper.getSumAmount(apId, DictConstant.DICT_B_AP_PAY_BILL_STATUS_ONE);
        BigDecimal paidAmountTotal = payAmountSummary != null && payAmountSummary.getPaid_amount_total() != null 
            ? payAmountSummary.getPaid_amount_total() : BigDecimal.ZERO;

        // 比较金额并更新应付账款主表状态
        BApEntity bApEntity = bApMapper.selectById(apId);
        if (bApEntity == null) {
            return;
        }

        String newPayStatus = determinePaymentStatus(bApTotalVo.getPayable_amount_total(), paidAmountTotal);
        if (newPayStatus != null) {
            bApEntity.setPay_status(newPayStatus);
            int updateResult = bApMapper.updateById(bApEntity);
            if (updateResult == 0) {
                throw new BusinessException("应付账款主表状态更新失败");
            }
        }
    }

    /**
     * 根据应付金额和已付金额确定付款状态
     * @param payableAmount 应付总金额
     * @param paidAmount 已付总金额
     * @return 付款状态：1-部分付款，2-已付款
     */
    private String determinePaymentStatus(BigDecimal payableAmount, BigDecimal paidAmount) {
        if (payableAmount == null || paidAmount == null) {
            return null;
        }
        
        int comparison = payableAmount.compareTo(paidAmount);
        if (comparison == 0) {
            // 应付总金额 = 已付总金额，设置为已付款
            return "2";
        } else if (comparison > 0) {
            // 应付总金额 > 已付总金额，设置为部分付款
            return "1";
        }
        
        return null;
    }

    /**
     * 更新资金流水表
     * @param apPayId 付款单ID
     */
    private void updateFundFlow(Integer apPayId) {
        fundService.increaseAdvanceAmount(apPayId);
    }

}
