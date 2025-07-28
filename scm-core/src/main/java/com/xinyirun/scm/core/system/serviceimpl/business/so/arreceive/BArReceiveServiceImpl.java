package com.xinyirun.scm.core.system.serviceimpl.business.so.arreceive;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.ar.BArEntity;
import com.xinyirun.scm.bean.entity.business.so.arreceive.*;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveAttachVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.config.event.servcie.FundService;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.*;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.so.arreceive.IBArReceiveService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BArReceiveAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BArReceiveDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 收款单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Service
public class BArReceiveServiceImpl extends ServiceImpl<BArReceiveMapper, BArReceiveEntity> implements IBArReceiveService {

    @Autowired
    private BArReceiveMapper mapper;

    @Autowired
    private BArReceiveDetailMapper bArReceiveDetailMapper;

    @Autowired
    private BArReceiveAttachMapper bArReceiveAttachMapper;    @Autowired
    private BArMapper bArMapper;

    @Autowired
    private BArTotalMapper bArTotalMapper;

    @Autowired
    private BArDetailMapper bArDetailMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private BArReceiveAutoCodeServiceImpl bArReceiveAutoCodeService;

    @Autowired
    private BArReceiveDetailAutoCodeServiceImpl bArReceiveDetailAutoCodeService;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private BArSourceMapper bArSourceMapper;

    @Autowired
    private BArSourceAdvanceMapper bArSourceAdvanceMapper;

    @Autowired
    private BArReceiveSourceMapper bArReceiveSourceMapper;

    @Autowired
    private BArReceiveSourceAdvanceMapper bArReceiveSourceAdvanceMapper;

    @Autowired
    private ICommonSoTotalService commonTotalService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private FundService fundService;

    /**
     * 收款单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BArReceiveVo> startInsert(BArReceiveVo searchCondition) {
        // 1、校验
        checkInsertLogic(searchCondition);
        
        // 2.保存收款单
        InsertResultAo<BArReceiveVo> insertResultAo = insert(searchCondition);

        // 3.total数据重算
        commonTotalService.reCalculateAllTotalDataByArReceiveId(searchCondition.getId());
        
        return insertResultAo;
    }

    /**
     * 下推收款单
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BArReceiveVo> insert(BArReceiveVo searchCondition) {
        // 2、初始化数据
        initInsertArReceiveData(searchCondition);
        // 3、计算金额
        calcAmountInsertReceive(searchCondition);
        // 4、保存主表
        BArReceiveEntity bArReceiveEntity = saveReceiveMain(searchCondition);
        // 5、保存明细
        saveReceiveDetailList(searchCondition, bArReceiveEntity);
        // 6、保存附件
        saveReceiveAttach(searchCondition, bArReceiveEntity);
        // 7、保存源单
        saveReceiveSource(searchCondition, bArReceiveEntity);
        // 8、保存源单-预收款
        saveReceiveSourceAdvance(searchCondition, bArReceiveEntity);
        // 9、更新应收账款主表金额
//        updateArReceivingAmount(searchCondition);
        // 10、资金流水监控（如需）
        // commonFundService.startArReceiveFund(bArReceiveEntity.getId());
        searchCondition.setId(bArReceiveEntity.getId());
        return InsertResultUtil.OK(searchCondition);
    }

    /** 校验逻辑 */
    private void checkInsertLogic(BArReceiveVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /** 保存主表 */
    private BArReceiveEntity saveReceiveMain(BArReceiveVo searchCondition) {
        BArReceiveEntity bArReceiveEntity = (BArReceiveEntity) BeanUtilsSupport.copyProperties(searchCondition, BArReceiveEntity.class);
        bArReceiveEntity.setStatus(DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO);
        int bArReceive = mapper.insert(bArReceiveEntity);
        if (bArReceive == 0){
            throw new BusinessException("新增收款单，新增失败");
        }
        return bArReceiveEntity;
    }

    /**
     * 新增操作
     * 保存明细
     *
     * */
    private void saveReceiveDetailList(BArReceiveVo searchCondition, BArReceiveEntity bArReceiveEntity) {
        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData())) {
            for (BArReceiveDetailVo detailVo : searchCondition.getDetailListData()) {
                BArReceiveDetailEntity bArReceiveDetailEntity = (BArReceiveDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BArReceiveDetailEntity.class);
                bArReceiveDetailEntity.setAr_receive_id(bArReceiveEntity.getId());
                bArReceiveDetailEntity.setAr_receive_code(bArReceiveEntity.getCode());
                /**
                 * 新增操作，所以状态必定是0-待收款
                 * received_amount=0
                 * receiving_amount=receive_amount
                 * unreceive_amount=receive_amount
                 * cancel_amount=0
                 */
                bArReceiveDetailEntity.setReceived_amount(BigDecimal.ZERO);
                bArReceiveDetailEntity.setReceiving_amount(bArReceiveDetailEntity.getReceive_amount());
                bArReceiveDetailEntity.setUnreceive_amount(bArReceiveDetailEntity.getReceive_amount());
                bArReceiveDetailEntity.setCancel_amount(BigDecimal.ZERO);

                bArReceiveDetailEntity.setId(null);
                int bArReceiveDetail = bArReceiveDetailMapper.insert(bArReceiveDetailEntity);
                if (bArReceiveDetail == 0){
                    throw new BusinessException("新增收款单明细，新增失败");
                }
            }
        }
    }

    /** 保存附件 */
    private void saveReceiveAttach(BArReceiveVo searchCondition, BArReceiveEntity bArReceiveEntity) {
        if (CollectionUtil.isNotEmpty(searchCondition.getDoc_att_files())) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bArReceiveEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR_RECEIVE);
            fileMapper.insert(fileEntity);
            for (SFileInfoVo docAttFile : searchCondition.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                docAttFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(docAttFile, fileInfoEntity);
                fileInfoEntity.setFile_name(docAttFile.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            BArReceiveAttachEntity bArReceiveAttachEntity = new BArReceiveAttachEntity();
            bArReceiveAttachEntity.setAr_receive_id(bArReceiveEntity.getId());
            bArReceiveAttachEntity.setAr_receive_code(bArReceiveEntity.getCode());
            bArReceiveAttachEntity.setOne_file(fileEntity.getId());
            int bArReceiveAttach = bArReceiveAttachMapper.insert(bArReceiveAttachEntity);
            if (bArReceiveAttach == 0) {
                throw new BusinessException("新增收款单附件，新增失败");
            }
        }
    }

    /** 保存源单 */
    private void saveReceiveSource(BArReceiveVo searchCondition, BArReceiveEntity bArReceiveEntity) {
        List<BArSourceVo> arSourceList = bArSourceMapper.selectByArId(searchCondition.getAr_id());
        if (CollectionUtil.isNotEmpty(arSourceList)) {
            for (BArSourceVo arSourceVo : arSourceList) {
                BArReceiveSourceEntity receiveSourceEntity = new BArReceiveSourceEntity();
                receiveSourceEntity.setAr_receive_id(bArReceiveEntity.getId());
                receiveSourceEntity.setAr_receive_code(bArReceiveEntity.getCode());
                receiveSourceEntity.setAr_id(arSourceVo.getAr_id());
                receiveSourceEntity.setAr_code(arSourceVo.getAr_code());
                receiveSourceEntity.setType(arSourceVo.getType());
                receiveSourceEntity.setProject_code(arSourceVo.getProject_code());
                receiveSourceEntity.setSo_contract_id(arSourceVo.getSo_contract_id());
                receiveSourceEntity.setSo_contract_code(arSourceVo.getSo_contract_code());
                receiveSourceEntity.setSo_order_code(arSourceVo.getSo_order_code());
                receiveSourceEntity.setSo_order_id(arSourceVo.getSo_order_id());
                bArReceiveSourceMapper.insert(receiveSourceEntity);
            }
        }
    }

    /** 保存源单-预收款 */
    private void saveReceiveSourceAdvance(BArReceiveVo searchCondition, BArReceiveEntity bArReceiveEntity) {
        List<BArSourceAdvanceVo> arSourceAdvanceList = bArSourceAdvanceMapper.selectByArId(searchCondition.getAr_id());
        if (CollectionUtil.isNotEmpty(arSourceAdvanceList)) {
            for (BArSourceAdvanceVo arSourceAdvanceVo : arSourceAdvanceList) {
                BArReceiveSourceAdvanceEntity receiveSourceAdvanceEntity = new BArReceiveSourceAdvanceEntity();
                receiveSourceAdvanceEntity.setAr_receive_id(bArReceiveEntity.getId());
                receiveSourceAdvanceEntity.setAr_receive_code(bArReceiveEntity.getCode());
                receiveSourceAdvanceEntity.setAr_id(arSourceAdvanceVo.getAr_id());
                receiveSourceAdvanceEntity.setAr_code(arSourceAdvanceVo.getAr_code());
                receiveSourceAdvanceEntity.setType(arSourceAdvanceVo.getType());
                receiveSourceAdvanceEntity.setSo_contract_id(arSourceAdvanceVo.getSo_contract_id());
                receiveSourceAdvanceEntity.setSo_contract_code(arSourceAdvanceVo.getSo_contract_code());
                receiveSourceAdvanceEntity.setSo_order_code(arSourceAdvanceVo.getSo_order_code());
                receiveSourceAdvanceEntity.setSo_order_id(arSourceAdvanceVo.getSo_order_id());
                receiveSourceAdvanceEntity.setSo_goods(arSourceAdvanceVo.getSo_goods());
                receiveSourceAdvanceEntity.setSo_qty(arSourceAdvanceVo.getSo_qty());
                receiveSourceAdvanceEntity.setSo_amount(arSourceAdvanceVo.getSo_amount());
                receiveSourceAdvanceEntity.setSo_advance_receive_amount(arSourceAdvanceVo.getSo_advance_receive_amount());
                receiveSourceAdvanceEntity.setOrder_amount(arSourceAdvanceVo.getOrder_amount());
                receiveSourceAdvanceEntity.setRemark(arSourceAdvanceVo.getRemark());
                bArReceiveSourceAdvanceMapper.insert(receiveSourceAdvanceEntity);
            }
        }
    }

    private void updateArReceivingAmount(BArReceiveVo searchCondition) {
        BArEntity bArEntity = bArMapper.selectById(searchCondition.getAr_id());
        if (bArEntity != null) {
//            BigDecimal currentReceivingAmount = bArEntity.getReceiving_amount() != null ? bArEntity.getReceiving_amount() : BigDecimal.ZERO;
//            BigDecimal newReceiveAmount = searchCondition.getReceive_amount_total() != null ? searchCondition.getReceive_amount_total() : BigDecimal.ZERO;
//            bArEntity.setReceiving_amount(currentReceivingAmount.add(newReceiveAmount));
            int bAr = bArMapper.updateById(bArEntity);
            if (bAr == 0){
                throw new BusinessException("更新应收账款主表失败");
            }
        }
    }

    /**
     * 任务11：计算金额
     * 功能说明：计算金额
     * @param searchCondition 收款单Vo
     */
    private void calcAmountInsertReceive(BArReceiveVo searchCondition) {
        BigDecimal receivableAmountTotal = BigDecimal.ZERO;
        BigDecimal receivedAmountTotal = BigDecimal.ZERO;
        BigDecimal receiveAmountTotal = BigDecimal.ZERO;

        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData())) {
            // BArReceiveVo.detailListData进行循环
            for (BArReceiveDetailVo detail : searchCondition.getDetailListData()) {
                // received_amount已收款金额=0
                detail.setReceived_amount(BigDecimal.ZERO);
                
                // 累计计算各种金额
                if (detail.getReceivable_amount() != null) {
                    receivableAmountTotal = receivableAmountTotal.add(detail.getReceivable_amount());
                }
                if (detail.getReceived_amount() != null) {
                    receivedAmountTotal = receivedAmountTotal.add(detail.getReceived_amount());
                }
                if (detail.getReceive_amount() != null) {
                    receiveAmountTotal = receiveAmountTotal.add(detail.getReceive_amount());
                }
            }
        }

        // 设置汇总金额
        searchCondition.setReceivable_amount_total(receivableAmountTotal); // 收款单计划收款总金额
        searchCondition.setReceived_amount_total(receivedAmountTotal); // 收款单已收款总金额
//        searchCondition.setReceive_amount_total(receiveAmountTotal); // 收款单总金额
    }

    /**
     * 任务10：初始化新增收款单数据
     * 功能说明：通过ar_id，查找b_ar、b_ar_detail表数据，并填充BArReceiveVo
     * @param searchCondition 收款单Vo
     */
    private void initInsertArReceiveData(BArReceiveVo searchCondition) {
        // 1、查询b_ar表
        BArVo bArVo = bArMapper.selectId(searchCondition.getAr_id());
        if (bArVo != null) {
            // set数据
            searchCondition.setCode(bArReceiveAutoCodeService.autoCode().getCode());
            searchCondition.setAr_id(bArVo.getId());
            searchCondition.setAr_code(bArVo.getCode());
            searchCondition.setStatus(DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO); // 1-待收款：使用常量
            searchCondition.setType(bArVo.getType());
            searchCondition.setCustomer_id(bArVo.getCustomer_id());
            searchCondition.setCustomer_code(bArVo.getCustomer_code());
            searchCondition.setCustomer_name(bArVo.getCustomer_name());
            searchCondition.setSeller_id(bArVo.getSeller_id());
            searchCondition.setSeller_code(bArVo.getSeller_code());
            searchCondition.setSeller_name(bArVo.getSeller_name());
//            searchCondition.setReceive_type(bArVo.getReceive_type());
            List<BArReceiveDetailVo>  arReceiveDetailVos = searchCondition.getDetailListData();
            BigDecimal receiveAmountTotal = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(arReceiveDetailVos)) {
                for (BArReceiveDetailVo detail : arReceiveDetailVos) {
                    if (detail.getReceive_amount() != null) {
                        receiveAmountTotal = receiveAmountTotal.add(detail.getReceive_amount());
                    }
                }
            }
            searchCondition.setReceivable_amount_total(receiveAmountTotal);
            searchCondition.setReceived_amount_total(bArVo.getReceived_amount_total());
        }

        // 2、查询b_ar_detail表
        List<BArDetailVo> bArDetailVos = bArMapper.getArDetail(searchCondition.getAr_id());
        
        if (CollectionUtil.isNotEmpty(searchCondition.getDetailListData()) && CollectionUtil.isNotEmpty(bArDetailVos)) {
            // BArReceiveVo.detailListData进行循环
            for (BArReceiveDetailVo receiveDetailVo : searchCondition.getDetailListData()) {
                // 和返回List<BArDetailVo>比较（此处还有个循环）
                for (BArDetailVo arDetailVo : bArDetailVos) {
                    // 当BArDetailVo.code = BArReceiveVo.detailListData.ar_detail_code
                    if (arDetailVo.getCode().equals(receiveDetailVo.getCode())) {
                        // BArReceiveVo.detailListData数据进行set
                        receiveDetailVo.setCode(bArReceiveDetailAutoCodeService.autoCode().getCode());
                        receiveDetailVo.setAr_id(bArVo.getId());
                        receiveDetailVo.setAr_code(bArVo.getCode());
                        receiveDetailVo.setAr_detail_id(arDetailVo.getId());
                        receiveDetailVo.setAr_detail_code(arDetailVo.getCode());
                        receiveDetailVo.setBank_accounts_id(arDetailVo.getBank_accounts_id());
                        receiveDetailVo.setBank_accounts_code(arDetailVo.getBank_accounts_code());
                        receiveDetailVo.setReceivable_amount(arDetailVo.getReceivable_amount());
                        break; // 找到匹配项后跳出内层循环
                    }
                }
            }
        }
    }

    /**
     * 列表查询
     * @param searchCondition
     */
    @Override
    public IPage<BArReceiveVo> selectPage(BArReceiveVo searchCondition) {
        // 分页条件
        Page<BArReceiveVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
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
    public BArReceiveVo selectById(Integer id) {
        BArReceiveVo bArReceiveVo = mapper.selById(id);
        if (bArReceiveVo == null) {
            return null;
        }
        // 1. 查询收款单明细
        bArReceiveVo.setDetailListData(mapper.getArReceiveDetail(id));

        // 2. 查询收款单附件
        BArReceiveAttachVo attachVo = bArReceiveAttachMapper.selectByBArReceiveId(id);
        if (attachVo != null) {
            // 2.1 收款单附件（other_doc_att_files_id）
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
                    bArReceiveVo.setDoc_att_files(docAttFiles);
                }
            }
            // 2.2 凭证附件（voucher_files_id）
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
                    bArReceiveVo.setVoucher_files(voucherFiles);
                }
            }
        }

        // 4. 查询是否存在作废记录
        if (DictConstant.DICT_B_AR_RECEIVE_STATUS_TWO.equals(bArReceiveVo.getStatus())) {
            // 构造作废查询条件
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bArReceiveVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR_RECEIVE);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);

            if (mCancelVo != null) {
                // 作废原因
                bArReceiveVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = fileInfoMapper.selectFIdList(mCancelVo.getFile_id());
                    bArReceiveVo.setCancel_doc_att_files(cancel_doc_att_files);
                }
                // 作废人
                if (mCancelVo.getC_id() != null) {
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    bArReceiveVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());
                }
                // 作废时间
                bArReceiveVo.setCancel_time(mCancelVo.getC_time());
            }
        }
        // 3、获取应收账款数据b_ar
        BArVo bArVo = bArMapper.selectId(bArReceiveVo.getAr_id());
        bArReceiveVo.setReceive_status(bArVo.getReceive_status());
        bArReceiveVo.setReceive_status_name(bArVo.getReceive_status_name());
        return bArReceiveVo;
    }

     /**
     * 收款复核
     *
     * @param searchCondition
     */    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BArReceiveVo> receiveComplete(BArReceiveVo searchCondition) {
        // 1. 业务校验
        validateReceiveComplete(searchCondition);
        
        // 2. 更新收款单主表状态
        BArReceiveEntity bArReceiveEntity = updateReceiveStatus(searchCondition);
        
        // 3. 更新收款单明细表状态
        updateReceiveDetails(searchCondition.getId());
        
        // 4. 处理附件上传
        processVoucherAttachment(bArReceiveEntity, searchCondition);
        
        // 5. 重算总金额数据
        recalculateTotalData(searchCondition.getId());
        
        // 6. 更新应收账款主表收款状态
        updateArReceiveStatus(bArReceiveEntity.getAr_id());
        
        // 7. 更新资金流水表
        updateFundFlow(searchCondition.getId());
        
        searchCondition.setId(bArReceiveEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BArReceiveVo> cancel(BArReceiveVo searchCondition) {

        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.作废收款单状态
        BArReceiveEntity bArReceiveEntity = mapper.selectById(searchCondition.getId());
        String orignalStatus = bArReceiveEntity.getStatus();

        bArReceiveEntity.setStatus(DictConstant.DICT_B_AR_RECEIVE_STATUS_TWO);

        // 2.保存作废附件和作废原因到附件表
        BArReceiveAttachVo attachVo = bArReceiveAttachMapper.selectByBArReceiveId(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(searchCondition.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR_RECEIVE);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AR_RECEIVE);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        int bArReceive = mapper.updateById(bArReceiveEntity);
        if (bArReceive == 0){
            throw new BusinessException("作废，更新失败");
        }
        searchCondition.setId(bArReceiveEntity.getId());

        // 更新收款单明细表状态
        List<BArReceiveDetailVo> arReceiveDetailVos = bArReceiveDetailMapper.selectById(searchCondition.getId());
        for (BArReceiveDetailVo detailVo : arReceiveDetailVos) {
            BArReceiveDetailEntity bArReceiveDetailEntity = (BArReceiveDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BArReceiveDetailEntity.class);
                /**
                 * 作废操作，所以状态必定是2-作废
                 * received_amount=0
                 * receiving_amount=0
                 * unreceive_amount=0
                 * cancel_amount=if bArReceiveEntity.status (源状态)= 1-已收款，则为receive_amount，否则为0
                 */
            if (orignalStatus.equals(DictConstant.DICT_B_AR_RECEIVE_STATUS_ONE)) {
                bArReceiveDetailEntity.setCancel_amount(bArReceiveDetailEntity.getReceive_amount());
            } else {
                bArReceiveDetailEntity.setCancel_amount(BigDecimal.ZERO);
            }
            bArReceiveDetailEntity.setReceived_amount(BigDecimal.ZERO);
            bArReceiveDetailEntity.setReceiving_amount(BigDecimal.ZERO);
            bArReceiveDetailEntity.setUnreceive_amount(BigDecimal.ZERO);

            int bArReceiveDetail = bArReceiveDetailMapper.updateById(bArReceiveDetailEntity);
            if (bArReceiveDetail == 0){
                throw new BusinessException("作废收款单明细，更新失败");
            }
        }

        // 3.total数据重算
        commonTotalService.reCalculateAllTotalDataByArReceiveId(searchCondition.getId());

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
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BArReceiveVo vo) {
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
     * 根据业务需求检验数据，必须传入主要表的数据对象（或其他对象）
     *
     * @param searchCondition 主要检验的数据对象
     * @param checkType 校验类型（如新增、更新、删除等）
     * @return 校验结果
     */
    @Override
    public CheckResultAo checkLogic(BArReceiveVo searchCondition, String checkType) {
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 校验收款日期是否为空
                if (searchCondition.getReceive_date() == null) {
                    return CheckResultUtil.NG("校验出错:请输入收款日期。");
                }
                
                // 校验明细数据是否存在
                if (searchCondition.getDetailListData() == null || searchCondition.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个收款明细");
                }

                // 校验收款金额必须大于0，且不能大于未收款金额
                for (BArReceiveDetailVo detail : searchCondition.getDetailListData()) {
                    if (detail.getReceive_amount() == null || detail.getReceive_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：请输入收款指令金额大于0的数据。");
                    }
                    // receive_amount不能大于unreceive_amount
                    if (detail.getUnreceive_amount() != null && detail.getReceive_amount() != null
                        && detail.getReceive_amount().compareTo(detail.getUnreceive_amount()) > 0) {
                        return CheckResultUtil.NG("输入错误：输入的收款指令金额需要小于等于未收款金额");
                    }
                }
                break;

            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }
                
                // 校验收款日期是否为空
                if (searchCondition.getReceive_date() == null) {
                    return CheckResultUtil.NG("校验出错:请输入收款日期。");
                }
                
                // 校验明细数据是否存在
                if (searchCondition.getDetailListData() == null || searchCondition.getDetailListData().isEmpty()) {
                    return CheckResultUtil.NG("至少添加一个收款明细");
                }

                // 校验收款金额必须大于0，且不能大于未收款金额
                for (BArReceiveDetailVo detail : searchCondition.getDetailListData()) {
                    if (detail.getReceive_amount() == null || detail.getReceive_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：请输入收款指令金额大于0的数据。");
                    }
                    // receive_amount不能大于unreceive_amount
                    if (detail.getUnreceive_amount() != null && detail.getReceive_amount() != null
                        && detail.getReceive_amount().compareTo(detail.getUnreceive_amount()) > 0) {
                        return CheckResultUtil.NG("输入错误：输入的收款指令金额需要小于等于未收款金额");
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
    public BArReceiveVo querySum(BArReceiveVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Override
    public BArReceiveVo queryViewSum(BArReceiveVo searchCondition) {
        return mapper.queryViewSum(searchCondition);
    }

// ==================== 收款完成业务方法拆分 ====================

    /**
     * 验证收款完成业务逻辑
     * @param searchCondition 收款单信息
     */
    private void validateReceiveComplete(BArReceiveVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新收款单主表状态
     * @param searchCondition 收款单信息
     * @return 更新后的收款单实体
     */
    private BArReceiveEntity updateReceiveStatus(BArReceiveVo searchCondition) {
        BArReceiveEntity bArReceiveEntity = mapper.selectById(searchCondition.getId());
        bArReceiveEntity.setStatus(DictConstant.DICT_B_AR_RECEIVE_STATUS_TWO);
        bArReceiveEntity.setReceive_date(searchCondition.getReceive_date());
        bArReceiveEntity.setVoucher_remark(searchCondition.getVoucher_remark());
        
        int updateResult = mapper.updateById(bArReceiveEntity);
        if (updateResult == 0) {
            throw new BusinessException("收款单状态更新失败");
        }
        
        return bArReceiveEntity;
    }

    /**
     * 更新收款单明细表状态（设置为已收款）
     * @param arReceiveId 收款单ID
     */
    private void updateReceiveDetails(Integer arReceiveId) {
        List<BArReceiveDetailVo> arReceiveDetailVos = bArReceiveDetailMapper.selectById(arReceiveId);
        for (BArReceiveDetailVo detailVo : arReceiveDetailVos) {
            BArReceiveDetailEntity bArReceiveDetailEntity = (BArReceiveDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BArReceiveDetailEntity.class);
            
            // 已收款操作，设置相关金额字段
            bArReceiveDetailEntity.setReceived_amount(bArReceiveDetailEntity.getReceive_amount());  // 已收金额 = 本次收款金额
            bArReceiveDetailEntity.setReceiving_amount(BigDecimal.ZERO);                   // 收款中金额清零
            bArReceiveDetailEntity.setUnreceive_amount(BigDecimal.ZERO);                    // 未收金额清零
            bArReceiveDetailEntity.setCancel_amount(BigDecimal.ZERO);                   // 作废金额清零
            
            int updateResult = bArReceiveDetailMapper.updateById(bArReceiveDetailEntity);
            if (updateResult == 0) {
                throw new BusinessException("收款单明细更新失败");
            }
        }
    }

    /**
     * 处理收款凭证附件
     * @param bArReceiveEntity 收款单实体
     * @param searchCondition 收款单信息（包含附件）
     */
    private void processVoucherAttachment(BArReceiveEntity bArReceiveEntity, BArReceiveVo searchCondition) {
        // 上传附件文件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bArReceiveEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR_RECEIVE);
        SFileEntity sFileEntity = insertFile(fileEntity, searchCondition.getVoucher_files());

        // 更新或新增附件关联记录
        BArReceiveAttachVo bArReceiveAttachVo = bArReceiveAttachMapper.selectByBArReceiveId(bArReceiveEntity.getId());
        if (bArReceiveAttachVo == null) {
            // 新增附件记录
            insertReceiveAttachment(bArReceiveEntity, sFileEntity);
        } else {
            // 更新附件记录
            updateReceiveAttachment(bArReceiveAttachVo, sFileEntity);
        }
    }

    /**
     * 新增收款单附件记录
     * @param bArReceiveEntity 收款单实体
     * @param sFileEntity 文件实体
     */
    private void insertReceiveAttachment(BArReceiveEntity bArReceiveEntity, SFileEntity sFileEntity) {
        BArReceiveAttachEntity bArReceiveAttachEntity = new BArReceiveAttachEntity();
        bArReceiveAttachEntity.setTwo_file(sFileEntity.getId());
        bArReceiveAttachEntity.setAr_receive_code(bArReceiveEntity.getCode());
        bArReceiveAttachEntity.setAr_receive_id(bArReceiveEntity.getId());
        
        int insertResult = bArReceiveAttachMapper.insert(bArReceiveAttachEntity);
        if (insertResult == 0) {
            throw new BusinessException("收款单附件新增失败");
        }
    }

    /**
     * 更新收款单附件记录
     * @param bArReceiveAttachVo 原附件记录
     * @param sFileEntity 新文件实体
     */
    private void updateReceiveAttachment(BArReceiveAttachVo bArReceiveAttachVo, SFileEntity sFileEntity) {
        BArReceiveAttachEntity bArReceiveAttachEntity = (BArReceiveAttachEntity) BeanUtilsSupport.copyProperties(bArReceiveAttachVo, BArReceiveAttachEntity.class);
        bArReceiveAttachEntity.setTwo_file(sFileEntity.getId());
        
        int updateResult = bArReceiveAttachMapper.updateById(bArReceiveAttachEntity);
        if (updateResult == 0) {
            throw new BusinessException("收款单附件更新失败");
        }
        
        bArReceiveAttachVo.setTwo_file(sFileEntity.getId());
    }

    /**
     * 重新计算总金额数据
     * @param arReceiveId 收款单ID
     */
    private void recalculateTotalData(Integer arReceiveId) {
        commonTotalService.reCalculateAllTotalDataByArReceiveId(arReceiveId);
    }

    /**
     * 更新应收账款主表的收款状态
     * @param arId 应收账款主表ID
     */
    private void updateArReceiveStatus(Integer arId) {
        // 获取应收账款总表的应收金额
        BArTotalVo bArTotalVo = bArTotalMapper.selectByArId(arId);
        if (bArTotalVo == null) {
            return;
        }

        // 获取收款单的已收金额汇总（状态=1表示已收款）
        BArReceiveVo receiveAmountSummary = mapper.getSumAmount(arId, DictConstant.DICT_B_AR_RECEIVE_STATUS_ONE);
        BigDecimal receivedAmountTotal = receiveAmountSummary != null && receiveAmountSummary.getReceived_amount_total() != null 
            ? receiveAmountSummary.getReceived_amount_total() : BigDecimal.ZERO;

        // 比较金额并更新应收账款主表状态
        BArEntity bArEntity = bArMapper.selectById(arId);
        if (bArEntity == null) {
            return;
        }

        String newReceiveStatus = determineReceiveStatus(bArTotalVo.getReceivable_amount_total(), receivedAmountTotal);
        if (newReceiveStatus != null) {
            bArEntity.setReceive_status(newReceiveStatus);
            int updateResult = bArMapper.updateById(bArEntity);
            if (updateResult == 0) {
                throw new BusinessException("应收账款主表状态更新失败");
            }
        }
    }

    /**
     * 根据应收金额和已收金额确定收款状态
     * @param receivableAmount 应收总金额
     * @param receivedAmount 已收总金额
     * @return 收款状态：1-部分收款，2-已收款
     */
    private String determineReceiveStatus(BigDecimal receivableAmount, BigDecimal receivedAmount) {
        if (receivableAmount == null || receivedAmount == null) {
            return null;
        }
        
        int comparison = receivableAmount.compareTo(receivedAmount);
        if (comparison == 0) {
            // 应收总金额 = 已收总金额，设置为已收款
            return "2";
        } else if (comparison > 0) {
            // 应收总金额 > 已收总金额，设置为部分收款
            return "1";
        }
        
        return null;
    }

    /**
     * 更新资金流水表
     * @param arReceiveId 收款单ID
     */
    private void updateFundFlow(Integer arReceiveId) {
        fundService.increaseAdvanceAmount(arReceiveId);
    }

}  
































