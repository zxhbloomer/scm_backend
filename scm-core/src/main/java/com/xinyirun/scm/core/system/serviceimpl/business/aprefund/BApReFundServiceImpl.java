package com.xinyirun.scm.core.system.serviceimpl.business.aprefund;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.*;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundAttachVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundDetailVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.*;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApRefundPayMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.aprefund.IBApReFundService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApReFundAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApReFundDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApReFundSourceAdvanceAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 应付退款管理表（Accounts Payable） 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Slf4j
@Service
public class BApReFundServiceImpl extends ServiceImpl<BApReFundMapper, BApReFundEntity> implements IBApReFundService {

    @Autowired
    private BApReFundMapper mapper;

    @Autowired
    private BApReFundDetailMapper bApReFundDetailMapper;

    @Autowired
    private BApReFundSourceMapper bApReFundSourceMapper;

    @Autowired
    private BApReFundSourceAdvanceMapper bApReFundSourceAdvanceMapper;

    @Autowired
    private BApReFundTotalMapper bApReFundTotalMapper;

    @Autowired
    private BApReFundAutoCodeServiceImpl bApReFundAutoCodeService;

    @Autowired
    private BApReFundSourceAdvanceAutoCodeServiceImpl bApReFundSourceAdvanceAutoCodeService;

    @Autowired
    private BApReFundDetailAutoCodeServiceImpl bApReFundDetailAutoCodeService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private BApRefundPayMapper bApRefundPayMapper;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private ICommonPoTotalService commonTotalService;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BApReFundAttachMapper bApReFundAttachMapper;

    /**
     * 计算业务单据信息、退款账户信息汇总数据
     * @param vo 退款信息
     */
    private void calculateSummaryData(BApReFundVo vo) {
        // 1、业务单据信息计算逻辑 - 使用 order_amount（本次申请退款金额）
        BigDecimal orderAmount = vo.getOrder_amount() != null ? vo.getOrder_amount() : BigDecimal.ZERO;
        
        // 设置退款总金额相关字段
        if (vo.getRefund_amount_total() == null) {
            vo.setRefund_amount_total(orderAmount);
        }
        
        // 已退款总金额初始化为0
        vo.setRefunded_amount_total(BigDecimal.ZERO);
        
        // 退款中总金额初始化为0
        vo.setRefunding_amount_total(BigDecimal.ZERO);
        
        // 未退款总金额 = 本次申请退款金额
        vo.setUnrefund_amount_total(orderAmount);
        
        // 可退款总金额，如果没有设置则使用本次申请金额
        if (vo.getRefundable_amount_total() == null) {
            vo.setRefundable_amount_total(orderAmount);
        }
        
        // 2、退款账户信息计算逻辑
        if (vo.getBankData() != null && vo.getBankData().getOrder_amount() != null) {
            vo.setDetail_refund_amount(vo.getBankData().getOrder_amount());
        } else {
            vo.setDetail_refund_amount(orderAmount);
        }
    }


    /**
     * 获取业务类型
     */
    @Override
    public List<BApReFundVo> getType() {
        return mapper.getType();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundVo> startInsert(BApReFundVo vo) {
        // 1. 校验业务规则
        checkInsertLogic(vo);
        
        // 2.保存退款管理
        InsertResultAo<BApReFundVo> insertResultAo = insert(vo);

        // 3.启动审批流程
        startFlowProcess(vo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND);

        return insertResultAo;
    }

    /**
     * 更新
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundVo> startUpdate(BApReFundVo vo) {
        // 1. 校验业务规则
        checkUpdateLogic(vo);
        
        // 2.保存退款管理
        UpdateResultAo<BApReFundVo> insertResultAo = update(vo);

        // 3.启动审批流程
        startFlowProcess(vo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND);

        return insertResultAo;
    }

    /**
     * 分页查询
     *
     * @param vo
     */
    @Override
    public IPage<BApReFundVo> selectPage(BApReFundVo vo) {
        // 分页条件
        Page<BApReFundVo> pageCondition = new Page(vo.getPageCondition().getCurrent(), vo.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, vo.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, vo);
    }

    /**
     * 根据id查询应付退款详细信息
     * 
     * 功能说明：
     * 1. 查询应付退款基础信息
     * 2. 查询退款附件信息
     * 3. 针对预付退款类型的特殊处理：
     *    - 查询源单信息判断业务类型
     *    - 获取关联单据信息（应付账款）和退款信息（银行账号）
     * 4. 查询作废记录相关信息
     *
     * @param id 应付退款ID
     * @return 完整的应付退款信息，包含关联单据和退款信息
     */
    @Override
    public BApReFundVo selectById(Integer id) {
        BApReFundVo bApReFundVo = mapper.selectId(id);

        // 增加退款账户方面的数据
        if (bApReFundVo != null) {
            BApReFundDetailVo bankData = new BApReFundDetailVo();
            
            // 从 mapper.selectId(id) 返回的对象中提取 b_ap_refund_detail tab3 相关的数据
            bankData.setAp_refund_id(bApReFundVo.getAp_refund_id());
            bankData.setAp_refund_code(bApReFundVo.getAp_refund_code());
            bankData.setBank_accounts_id(bApReFundVo.getBank_accounts_id());
            bankData.setBank_accounts_code(bApReFundVo.getBank_accounts_code());
            bankData.setRefundable_amount(bApReFundVo.getRefundable_amount());
            bankData.setRefunded_amount(bApReFundVo.getRefunded_amount());
            bankData.setRefunding_amount(bApReFundVo.getRefunding_amount());
            bankData.setUnrefund_amount(bApReFundVo.getUnrefund_amount());
            bankData.setOrder_amount(bApReFundVo.getDetail_order_amount());
            bankData.setPo_goods(bApReFundVo.getPo_goods());
            
            // 银行账户相关信息
            bankData.setName(bApReFundVo.getName());
            bankData.setBank_name(bApReFundVo.getBank_name());
            bankData.setAccount_number(bApReFundVo.getAccount_number());
            bankData.setAccounts_purpose_type_name(bApReFundVo.getAccounts_purpose_type_name());
            bankData.setBank_type_name(bApReFundVo.getBank_type_name());

            // 将 BApReFundDetailVo 对象设置到 BApReFundVo.bankData 中
            bApReFundVo.setBankData(bankData);
        }

        // 退款附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(bApReFundVo.getDoc_att_file());
        bApReFundVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_AP_REFUND_STATUS_FOUR.equals(bApReFundVo.getStatus()) || Objects.equals(bApReFundVo.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bApReFundVo.getId());
            serialIdAndType.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                bApReFundVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    bApReFundVo.setCancel_files(cancel_doc_att_files);
                }
                // 通过表m_staff获取作废提交人名称
                if (mCancelVo.getC_id() != null) {
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                    if (staffVo != null) {
                        bApReFundVo.setCancel_name(staffVo.getName());
                    }
                }
                // 作废时间
                bApReFundVo.setCancel_time(mCancelVo.getC_time());
            }
        }
        

        return bApReFundVo;
    }

    /**
     * 校验
     *
     * @param vo
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BApReFundVo vo, String checkType) {
        BApReFundEntity bApEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 校验本次申请退款金额>0
                if (vo.getOrder_amount() == null || vo.getOrder_amount().compareTo(BigDecimal.ZERO) <= 0) {
                    return CheckResultUtil.NG("本次申请退款金额必须大于0！");
                }

                // 校验退款账户信息
                if (vo.getBankData() != null) {
                    // 校验退款金额>0
                    if (vo.getBankData().getOrder_amount() == null || vo.getBankData().getOrder_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：退款账户表格中存在退款金额为0的数据，请检查！");
                    }
                    
                    // 校验退款总金额与申请退款总金额是否一致
                    if (vo.getBankData().getOrder_amount() != null && vo.getOrder_amount() != null) {
                        if (vo.getBankData().getOrder_amount().compareTo(vo.getOrder_amount()) != 0) {
                            return CheckResultUtil.NG(String.format("退款数据校验失败：请确保各账户退款金额之和与申请退款金额完全一致。申请退款总金额：%s，退款总金额：%s", 
                                    vo.getOrder_amount(), vo.getBankData().getOrder_amount()));
                        }
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (vo.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(vo.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，应付退款[%s]不是待审批,驳回状态,无法修改", bApEntity.getCode()));
                }

                // 校验本次申请退款金额>0
                if (vo.getOrder_amount() == null || vo.getOrder_amount().compareTo(BigDecimal.ZERO) <= 0) {
                    return CheckResultUtil.NG("本次申请退款金额必须大于0！");
                }

                // 校验退款账户信息
                if (vo.getBankData() != null) {
                    // 校验退款金额>0
                    if (vo.getBankData().getOrder_amount() == null || vo.getBankData().getOrder_amount().compareTo(BigDecimal.ZERO) <= 0) {
                        return CheckResultUtil.NG("校验出错：退款账户表格中存在退款金额为0的数据，请检查！");
                    }
                    
                    // 校验退款总金额与申请退款总金额是否一致
                    if (vo.getBankData().getOrder_amount() != null && vo.getOrder_amount() != null) {
                        if (vo.getBankData().getOrder_amount().compareTo(vo.getOrder_amount()) != 0) {
                            return CheckResultUtil.NG(String.format("退款数据校验失败：请确保各账户退款金额之和与申请退款金额完全一致。申请退款总金额：%s，退款总金额：%s", 
                                    vo.getOrder_amount(), vo.getBankData().getOrder_amount()));
                        }
                    }
                }

                break;
            // 删除
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (vo.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(vo.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，退款管理[%s]不是待审批,驳回状态,无法删除", bApEntity.getCode()));
                }

                List<BApReFundPayVo> delBApPayVo = bApRefundPayMapper.selectApPayByNotStatus(vo.getId(), DictConstant.DICT_B_AP_PAY_BILL_STATUS_TWO);
                if (CollectionUtil.isNotEmpty(delBApPayVo)) {
                    return CheckResultUtil.NG("删除失败，存在退款单。");
                }
                break;
            // 作废
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (vo.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(vo.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_FIVE) || Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，退款管理[%s]无法重复作废",bApEntity.getCode()));
                }
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，退款管理[%s]审核中，无法作废",bApEntity.getCode()));
                }

                List<BApReFundPayVo> cancelBApPayVo = bApRefundPayMapper.selectApPayByNotStatus(vo.getId(), DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_THREE);
                if (CollectionUtil.isNotEmpty(cancelBApPayVo)) {
                    return CheckResultUtil.NG(String.format("作废失败，该应付退款下退款单号%s数据尚未作废，请先完成该退款单的作废。",cancelBApPayVo.stream().map(BApReFundPayVo::getCode).collect(Collectors.toList())));
                }

                break;

            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 审批流程回调
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BApReFundVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BApReFundVo bApReFundVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("业务类型:", bApReFundVo.getType_name());
        jsonObject.put("申请退款总金额:", bApReFundVo.getRefund_amount_total());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bApReFundVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 新增应付退款主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundVo> insert(BApReFundVo vo) {
        // 1. 计算汇总数据
        calculateSummaryData(vo);
        // 2. 保存主表信息
        BApReFundEntity bApReFundEntity = saveMainEntity(vo);
        // 3. 保存源单信息
        saveSourceEntity(vo, bApReFundEntity);
        // 4. 保存源单-预收款信息
        saveSourceAdvanceEntity(vo, bApReFundEntity);
        // 5. 保存银行账户明细
        saveDetailList(vo, bApReFundEntity);
        // 6. 保存附件信息
        saveAttach(vo, bApReFundEntity);
        // 7. 设置返回ID
        vo.setId(bApReFundEntity.getId());
        // 7.5. 保存Total汇总信息
        saveTotalEntity(vo, bApReFundEntity);
        // 8. 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByPoRefundId(bApReFundEntity.getId());
        return InsertResultUtil.OK(vo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BApReFundVo vo) {
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BApReFundEntity saveMainEntity(BApReFundVo vo) {
        BApReFundEntity bApReFundEntity = new BApReFundEntity();
        BeanUtils.copyProperties(vo, bApReFundEntity);
        bApReFundEntity.setCode(bApReFundAutoCodeService.autoCode().getCode());
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ONE);
        bApReFundEntity.setRefund_status(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO);
        bApReFundEntity.setIs_del(Boolean.FALSE);
        bApReFundEntity.setBpm_process_name("新增应付退款管理审批");
        bApReFundEntity.setId(null);
        int bAp = mapper.insert(bApReFundEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("新增失败");
        }
        return bApReFundEntity;
    }

    /**
     * 保存源单信息
     */
    private void saveSourceEntity(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        BApRefundSourceEntity bApSourceEntity = new BApRefundSourceEntity();
        bApSourceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApSourceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApSourceEntity.setType(bApReFundEntity.getType());
        bApSourceEntity.setPo_order_code(vo.getPo_order_code());
        bApSourceEntity.setPo_order_id(vo.getPo_order_id());
        bApSourceEntity.setPo_contract_id(vo.getPo_contract_id());
        bApSourceEntity.setPo_contract_code(vo.getPo_contract_code());
        bApSourceEntity.setProject_code(vo.getProject_code());
        int bApSource = bApReFundSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 保存源单-预收款信息
     * 新增，所以未退款=本次退款金额
     */
    private void saveSourceAdvanceEntity(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = new BApReFundSourceAdvanceEntity();
        bApReFundSourceAdvanceEntity.setCode(bApReFundSourceAdvanceAutoCodeService.autoCode().getCode());
        bApReFundSourceAdvanceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApReFundSourceAdvanceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApReFundSourceAdvanceEntity.setType(bApReFundEntity.getType());
        
        // 从基础字段获取数据
        bApReFundSourceAdvanceEntity.setPo_contract_id(vo.getPo_contract_id());
        bApReFundSourceAdvanceEntity.setPo_contract_code(vo.getPo_contract_code());
        bApReFundSourceAdvanceEntity.setPo_order_id(vo.getPo_order_id());
        bApReFundSourceAdvanceEntity.setPo_order_code(vo.getPo_order_code());
        bApReFundSourceAdvanceEntity.setOrder_amount(vo.getOrder_amount());
        
        // 从 vo 中获取更多字段
        bApReFundSourceAdvanceEntity.setPo_goods(vo.getPo_goods());

        // 初始化退款相关金额字段
        bApReFundSourceAdvanceEntity.setRefundable_amount_total(vo.getRefundable_amount_total() != null ? vo.getRefundable_amount_total() : vo.getOrder_amount());
        bApReFundSourceAdvanceEntity.setRefunded_amount_total(BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setRefunding_amount_total(BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setUnrefund_amount_total(vo.getRefundable_amount_total() != null ? vo.getRefundable_amount_total() : vo.getOrder_amount());
        bApReFundSourceAdvanceEntity.setCancelrefund_amount_total(BigDecimal.ZERO);

        bApReFundSourceAdvanceEntity.setAdvance_paid_total(vo.getAdvance_paid_total() != null ? vo.getAdvance_paid_total() : BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setAdvance_refund_amount_total(vo.getAdvance_refund_amount_total() != null ? vo.getAdvance_refund_amount_total() : BigDecimal.ZERO);

        int bApSourceAdvance = bApReFundSourceAdvanceMapper.insert(bApReFundSourceAdvanceEntity);
        if (bApSourceAdvance <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 保存银行账户明细
     */
    private void saveDetailList(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        if (vo.getBankData() != null) {
            BApReFundDetailEntity bApReFundDetailEntity = new BApReFundDetailEntity();
            bApReFundDetailEntity.setCode(bApReFundDetailAutoCodeService.autoCode().getCode());
            bApReFundDetailEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundDetailEntity.setAp_refund_code(bApReFundEntity.getCode());
            
            // 从 bankData 中获取银行账户信息
            bApReFundDetailEntity.setBank_accounts_id(vo.getBankData().getBank_accounts_id());
            bApReFundDetailEntity.setBank_accounts_code(vo.getBankData().getBank_accounts_code());
            
            // 商品信息
            bApReFundDetailEntity.setPo_goods(vo.getPo_goods());
            
            // 退款金额信息
            bApReFundDetailEntity.setRefundable_amount(vo.getBankData().getRefundable_amount() != null ? vo.getBankData().getRefundable_amount() : vo.getOrder_amount());
            bApReFundDetailEntity.setRefunded_amount(BigDecimal.ZERO);
            bApReFundDetailEntity.setRefunding_amount(BigDecimal.ZERO);
            bApReFundDetailEntity.setUnrefund_amount(vo.getBankData().getRefundable_amount() != null ? vo.getBankData().getRefundable_amount() : vo.getOrder_amount());
            bApReFundDetailEntity.setOrder_amount(vo.getBankData().getOrder_amount() != null ? vo.getBankData().getOrder_amount() : vo.getOrder_amount());

            int bApDetail = bApReFundDetailMapper.insert(bApReFundDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
        BApReFundAttachEntity bApReFundAttachEntity = insertFile(fileEntity, vo, new BApReFundAttachEntity());
        bApReFundAttachEntity.setAp_refund_id(bApReFundEntity.getId());
        int attachInsert = bApReFundAttachMapper.insert(bApReFundAttachEntity);
        if (attachInsert <= 0) {
            throw new UpdateErrorException("新增附件信息失败");
        }
    }

    /**
     * 保存Total汇总信息
     */
    private void saveTotalEntity(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        BApReFundTotalEntity totalEntity = new BApReFundTotalEntity();
        totalEntity.setAp_refund_id(bApReFundEntity.getId());
        totalEntity.setPo_order_id(vo.getPo_order_id());
        totalEntity.setRefundable_amount_total(vo.getRefundable_amount_total() != null ? vo.getRefundable_amount_total() : vo.getOrder_amount());
        totalEntity.setRefunded_amount_total(BigDecimal.ZERO);
        totalEntity.setRefunding_amount_total(BigDecimal.ZERO);
        totalEntity.setUnrefund_amount_total(vo.getRefundable_amount_total() != null ? vo.getRefundable_amount_total() : vo.getOrder_amount());
        totalEntity.setCancelrefund_amount_total(BigDecimal.ZERO);
        
        int result = bApReFundTotalMapper.insert(totalEntity);
        if (result <= 0) {
            throw new UpdateErrorException("新增Total数据失败");
        }
    }

    /**
     * 更新应付退款主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundVo> update(BApReFundVo vo) {
        // 1. 计算汇总数据
        calculateSummaryData(vo);
        // 2. 更新主表信息
        BApReFundEntity bApReFundEntity = updateMainEntity(vo);
        // 3. 更新源单信息
        updateSourceEntity(vo, bApReFundEntity);
        // 4. 更新源单-预收款信息
        updateSourceAdvanceEntity(vo, bApReFundEntity);
        // 5. 更新银行账户明细
        updateDetailList(vo, bApReFundEntity);
        // 6. 更新附件信息
        updateAttach(vo, bApReFundEntity);
        // 7. 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByPoRefundId(bApReFundEntity.getId());
        return UpdateResultUtil.OK(vo);
    }

    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BApReFundVo vo) {
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新主表信息
     */
    private BApReFundEntity updateMainEntity(BApReFundVo vo) {
        BApReFundEntity bApReFundEntity = (BApReFundEntity) BeanUtilsSupport.copyProperties(vo, BApReFundEntity.class);
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ONE);
        bApReFundEntity.setIs_del(Boolean.FALSE);
        bApReFundEntity.setBpm_process_name("修改应付退款管理审批");
        int bAp = mapper.updateById(bApReFundEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("更新失败");
        }
        return bApReFundEntity;
    }

    /**
     * 更新源单信息
     */
    private void updateSourceEntity(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        bApReFundSourceMapper.delete(new LambdaQueryWrapper<BApRefundSourceEntity>()
                .eq(BApRefundSourceEntity :: getAp_refund_id, bApReFundEntity.getId()));
        BApRefundSourceEntity bApSourceEntity = new BApRefundSourceEntity();
        bApSourceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApSourceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApSourceEntity.setType(bApReFundEntity.getType());
        bApSourceEntity.setPo_order_id(vo.getPo_order_id());
        bApSourceEntity.setPo_order_code(vo.getPo_order_code());
        bApSourceEntity.setPo_contract_id(vo.getPo_contract_id());
        bApSourceEntity.setPo_contract_code(vo.getPo_contract_code());
        bApSourceEntity.setProject_code(vo.getProject_code());

        int bApSource = bApReFundSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("更新失败");
        }
    }

    /**
     * 更新源单-预收款信息
     */
    private void updateSourceAdvanceEntity(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        bApReFundSourceAdvanceMapper.delete(new LambdaQueryWrapper<BApReFundSourceAdvanceEntity>()
                .eq(BApReFundSourceAdvanceEntity :: getAp_refund_id, bApReFundEntity.getId()));
        
        // 创建单个源单预收款记录
        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = new BApReFundSourceAdvanceEntity();
        bApReFundSourceAdvanceEntity.setCode(bApReFundSourceAdvanceAutoCodeService.autoCode().getCode());
        bApReFundSourceAdvanceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApReFundSourceAdvanceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApReFundSourceAdvanceEntity.setType(bApReFundEntity.getType());
        
        // 从 vo 中获取字段
        bApReFundSourceAdvanceEntity.setPo_contract_id(vo.getPo_contract_id());
        bApReFundSourceAdvanceEntity.setPo_contract_code(vo.getPo_contract_code());
        bApReFundSourceAdvanceEntity.setPo_order_id(vo.getPo_order_id());
        bApReFundSourceAdvanceEntity.setPo_order_code(vo.getPo_order_code());
        bApReFundSourceAdvanceEntity.setOrder_amount(vo.getOrder_amount() != null ? vo.getOrder_amount() : BigDecimal.ZERO);
        
        // 其他字段
        bApReFundSourceAdvanceEntity.setPo_goods(vo.getPo_goods());

        // 退款相关金额字段
        bApReFundSourceAdvanceEntity.setRefundable_amount_total(vo.getRefundable_amount_total() != null ? vo.getRefundable_amount_total() : vo.getOrder_amount());
        bApReFundSourceAdvanceEntity.setRefunded_amount_total(vo.getRefunded_amount_total() != null ? vo.getRefunded_amount_total() : BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setRefunding_amount_total(vo.getRefunding_amount_total() != null ? vo.getRefunding_amount_total() : BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setUnrefund_amount_total(vo.getUnrefund_amount_total() != null ? vo.getUnrefund_amount_total() : vo.getOrder_amount());
        bApReFundSourceAdvanceEntity.setCancelrefund_amount_total(vo.getCancelrefund_amount_total() != null ? vo.getCancelrefund_amount_total() : BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setAdvance_paid_total(vo.getAdvance_paid_total() != null ? vo.getAdvance_paid_total() : BigDecimal.ZERO);
        bApReFundSourceAdvanceEntity.setAdvance_refund_amount_total(vo.getAdvance_refund_amount_total() != null ? vo.getAdvance_refund_amount_total() : BigDecimal.ZERO);

        int bApSourceAdvance = bApReFundSourceAdvanceMapper.insert(bApReFundSourceAdvanceEntity);
        if (bApSourceAdvance <= 0) {
            throw new UpdateErrorException("更新失败");
        }
    }

    /**
     * 更新银行账户明细
     */
    private void updateDetailList(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        bApReFundDetailMapper.delete(new LambdaQueryWrapper<BApReFundDetailEntity>()
                .eq(BApReFundDetailEntity :: getAp_refund_id, bApReFundEntity.getId()));
        
        if (vo.getBankData() != null) {
            // 创建单个银行账户明细记录
            BApReFundDetailEntity bApReFundDetailEntity = new BApReFundDetailEntity();
            bApReFundDetailEntity.setCode(bApReFundDetailAutoCodeService.autoCode().getCode());
            bApReFundDetailEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundDetailEntity.setAp_refund_code(bApReFundEntity.getCode());
            
            // 从 bankData 中获取银行账户信息
            bApReFundDetailEntity.setBank_accounts_id(vo.getBankData().getBank_accounts_id());
            bApReFundDetailEntity.setBank_accounts_code(vo.getBankData().getBank_accounts_code());
            
            // 商品信息
            bApReFundDetailEntity.setPo_goods(vo.getBankData().getPo_goods());
            
            // 退款金额信息
            bApReFundDetailEntity.setRefunded_amount(vo.getBankData().getRefunded_amount() != null ? vo.getBankData().getRefunded_amount() : BigDecimal.ZERO);
            bApReFundDetailEntity.setRefunding_amount(vo.getBankData().getRefunding_amount() != null ? vo.getBankData().getRefunding_amount() : BigDecimal.ZERO);
            bApReFundDetailEntity.setRefundable_amount(vo.getBankData().getRefundable_amount() != null ? vo.getBankData().getRefundable_amount() : vo.getOrder_amount());
            bApReFundDetailEntity.setUnrefund_amount(vo.getBankData().getUnrefund_amount() != null ? vo.getBankData().getUnrefund_amount() : vo.getOrder_amount());
            bApReFundDetailEntity.setOrder_amount(vo.getBankData().getOrder_amount() != null ? vo.getBankData().getOrder_amount() : vo.getOrder_amount());

            int bApDetail = bApReFundDetailMapper.insert(bApReFundDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("更新失败");
            }
        }
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BApReFundVo vo, BApReFundEntity bApReFundEntity) {
        BApReFundAttachVo bApReFundAttachVo = bApReFundAttachMapper.selectByApId(bApReFundEntity.getId());
        if (bApReFundAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApReFundEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
            BApReFundAttachEntity bApReFundAttachEntity = (BApReFundAttachEntity) BeanUtilsSupport.copyProperties(bApReFundAttachVo, BApReFundAttachEntity.class);
            insertFile(fileEntity, vo, bApReFundAttachEntity);
            bApReFundAttachEntity.setAp_refund_id(bApReFundEntity.getId());
            int attachUpdate = bApReFundAttachMapper.updateById(bApReFundAttachEntity);
            if (attachUpdate <= 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApReFundEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
            BApReFundAttachEntity bApReFundAttachEntity = new BApReFundAttachEntity();
            insertFile(fileEntity, vo, bApReFundAttachEntity);
            bApReFundAttachEntity.setAp_refund_id(bApReFundEntity.getId());
            int attachInsert = bApReFundAttachMapper.insert(bApReFundAttachEntity);
            if (attachInsert <= 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BApReFundVo vo) {
        log.debug("====》付款管理[{}]审批流程通过，更新开始《====",vo.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());

        bApReFundEntity.setBpm_instance_id(vo.getBpm_instance_id());
        bApReFundEntity.setBpm_instance_code(vo.getBpm_instance_code());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_TWO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPoRefundId(vo.getId());

        log.debug("====》付款管理[{}]审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BApReFundVo vo) {
        log.debug("====》付款管理[{}]审批流程拒绝，更新开始《====",vo.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_THREE);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程拒绝,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }


    /**
     * 审批流程通过 审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BApReFundVo vo) {
        log.debug("====》付款管理[{}]审批流程撤销，更新开始《====",vo.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ZERO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程撤销,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BApReFundVo vo) {
        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新开始《====",vo.getId());

        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());

        bApReFundEntity.setBpm_instance_id(vo.getBpm_instance_id());
        bApReFundEntity.setBpm_instance_code(vo.getBpm_instance_code());
        bApReFundEntity.setNext_approve_name(vo.getNext_approve_name());
        int i = mapper.updateById(bApReFundEntity);

        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BApReFundVo vo) {
        log.debug("====》付款管理[{}]审批流程通过，更新开始《====",vo.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());

        bApReFundEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bApReFundEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_FIVE);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 更新b_ap_refund_source_advance表的作废金额
        updateSourceAdvanceCancelAmount(vo.getId());

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByPoRefundId(vo.getId());

        log.debug("====》付款管理[{}]审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 更新退款源单的作废金额
     * 当应付退款作废审批通过时，将该应付退款下所有退款源单的作废金额设置为申请金额
     * 
     * @param apRefundId 应付退款ID
     */
    private void updateSourceAdvanceCancelAmount(Integer apRefundId) {
        log.debug("====》开始更新应付退款[{}]下退款源单的作废金额《====", apRefundId);
        
        // 查询该应付退款下的退款源单记录（简化为单条记录）
        BApReFundSourceAdvanceEntity sourceAdvanceEntity = bApReFundSourceAdvanceMapper.selectOne(
            new LambdaQueryWrapper<BApReFundSourceAdvanceEntity>()
                .eq(BApReFundSourceAdvanceEntity::getAp_refund_id, apRefundId)
        );
        
        if (sourceAdvanceEntity == null) {
            log.debug("应付退款[{}]下没有退款源单记录，跳过更新", apRefundId);
            return;
        }
        
        // 设置作废金额为退款金额
        sourceAdvanceEntity.setCancelrefund_amount_total(sourceAdvanceEntity.getOrder_amount());
        
        int updateResult = bApReFundSourceAdvanceMapper.updateById(sourceAdvanceEntity);
        if (updateResult <= 0) {
            log.error("更新退款源单[{}]作废金额失败", sourceAdvanceEntity.getId());
            throw new UpdateErrorException("更新退款源单作废金额失败");
        }
        
        log.debug("更新退款源单[{}]作废金额成功，金额: {}", 
            sourceAdvanceEntity.getId(), sourceAdvanceEntity.getOrder_amount());
        
        log.debug("====》完成更新应付退款[{}]下退款源单的作废金额《====", apRefundId);
    }

    /**
     * 作废审批流程拒绝 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_TWO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApReFundEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
        mCancelService.delete(mCancelVo);

        log.debug("====》付款管理[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_TWO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApReFundEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
        mCancelService.delete(mCancelVo);

        log.debug("====》付款管理[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bApReFundEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bApReFundEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bApReFundEntity);

        log.debug("====》付款管理[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 导出查询
     *
     * @param param
     */
    @Override
    public List<BApReFundVo> selectExportList(BApReFundVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = isConfigService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (Objects.isNull(param.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            Long count = mapper.selectExportCount(param);

            if (count != null && count > Long.parseLong(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.selectExportList(param);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     * @param vo
     */
    @Override
    public BApReFundVo getPrintInfo(BApReFundVo vo) {
        /**
         * 获取打印配置信息
         * 1、从s_config中获取到：print_system_config、
         */
        SConfigEntity _data = isConfigService.selectByKey(SystemConstants.PRINT_SYSTEM_CONFIG);
        String url = _data.getValue();
        String token = _data.getExtra1();

        /**
         * 获取打印配置信息
         * 2、从s_page中获取到print_code
         */
        SPagesVo param = new SPagesVo();
        param.setCode(PageCodeConstant.PAGE_B_AP_REFUND);
        SPagesVo pagesVo = isPagesService.get(param);

        /**
         * 获取打印配置信息
         * 3、从s_app_config中获取，报表系统的app_key，securit_key
         */
//        SAppConfigEntity key = isAppConfigService.getDataByAppCode(AppConfigConstant.PRINT_SYSTEM_CODE);

        String printUrl =  url + pagesVo.getPrint_code() + "?token=" + token + "&id=" + vo.getId();
//        printUrl = printUrl + "&app_key=" + key.getApp_key() + "&secret_key=" + key.getSecret_key();
        vo.setPrint_url(printUrl);
        vo.setQr_code(printUrl);
        log.debug("打印地址：" + printUrl);
        return vo;
    }

    /**
     * 删除
     *
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BApReFundVo> vo) {
        for (BApReFundVo bApReFundVo : vo) {

            // 删除前check
            CheckResultAo cr = checkLogic(bApReFundVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BApReFundEntity bApReFundEntity = mapper.selectById(bApReFundVo.getId());
            bApReFundEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bApReFundEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 作废
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundVo> cancel(BApReFundVo vo) {

        // 作废前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BApReFundEntity bApReFundEntity = mapper.selectById(vo.getId());
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_FOUR);
        bApReFundEntity.setBpm_cancel_process_name("作废应付退款管理审批");

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
        fileEntity = insertCancelFile(fileEntity, vo);

        int insert = mapper.updateById(bApReFundEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApReFundEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(vo.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(vo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND_CANCEL);

        return UpdateResultUtil.OK(vo);
    }



    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BApReFundVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BApReFundVo bApReFundVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", bApReFundVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bApReFundVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 附件处理逻辑
     */
    public BApReFundAttachEntity insertFile(SFileEntity fileEntity, BApReFundVo vo, BApReFundAttachEntity extra) {
        // 附件新增
        if (vo.getDoc_att_files() != null && !vo.getDoc_att_files().isEmpty()) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo doc_att_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                doc_att_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(doc_att_file, fileInfoEntity);
                fileInfoEntity.setFile_name(doc_att_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOne_file(null);
        }
        return extra;
    }

    /**
     * 取消文件处理逻辑
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BApReFundVo vo) {
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
     * 启动审批流
     */
    public void startFlowProcess(BApReFundVo bean,String type){
        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(bean.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            bBpmProcessVo.setCode(bpmProcessTemplatesService.getBpmFLowCodeByType(type));
            bBpmProcessVo.setSerial_type(type);
            bBpmProcessVo.setForm_data(bean.getForm_data());
            bBpmProcessVo.setForm_json(bean);
            bBpmProcessVo.setForm_class(bean.getClass().getName());
            bBpmProcessVo.setSerial_id(bean.getId());
            bBpmProcessVo.setInitial_process(bean.getInitial_process());
            bBpmProcessVo.setProcess_users(bean.getProcess_users());

            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动出库计划审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    /**
     * 获取下推预付退款款数据
     */
    @Override
    public BApReFundVo getApRefund(BApReFundVo searchCondition) {
        return mapper.getApRefund(searchCondition);
    }

    /**
     * 汇总查询
     */
    @Override
    public BApReFundVo querySum(BApReFundVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

}
