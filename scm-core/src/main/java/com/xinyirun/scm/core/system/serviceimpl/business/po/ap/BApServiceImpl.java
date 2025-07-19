package com.xinyirun.scm.core.system.serviceimpl.business.po.ap;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.po.ap.*;
import com.xinyirun.scm.bean.entity.busniess.po.appay.BApPayDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.po.appay.BApPayEntity;
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
import com.xinyirun.scm.bean.system.vo.business.po.ap.*;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayVo;
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
import com.xinyirun.scm.core.system.mapper.business.po.ap.*;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPayMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.po.ap.IBApService;
import com.xinyirun.scm.core.system.service.business.po.ap.IBApTotalService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BApSourceAdvanceAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 应付账款管理表（Accounts Payable） 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Slf4j
@Service
public class BApServiceImpl extends ServiceImpl<BApMapper, BApEntity> implements IBApService {

    @Autowired
    private BApMapper mapper;

    @Autowired
    private BApDetailMapper bApDetailMapper;

    @Autowired
    private BApSourceMapper bApSourceMapper;

    @Autowired
    private BApSourceAdvanceMapper bApSourceAdvanceMapper;

    @Autowired
    private BApAutoCodeServiceImpl bApAutoCodeService;

    @Autowired
    private BApSourceAdvanceAutoCodeServiceImpl bApSourceAdvanceAutoCodeService;

    @Autowired
    private BApDetailAutoCodeServiceImpl bApDetailAutoCodeService;

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
    private BApPayMapper bApPayMapper;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private BApAttachMapper bApAttachMapper;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private IBApTotalService bApTotalService;

    @Autowired
    private ICommonPoTotalService commonTotalService;

    @Autowired
    private BApPayDetailMapper bApPayDetailMapper;

    /**
     * 计算业务单据信息、付款账户信息汇总数据
     * @param searchCondition 搜索条件
     */
    private void calculateSummaryData(BApVo searchCondition) {
        // 1、业务单据信息计算逻辑
        if (searchCondition.getPoOrderListData() != null && !searchCondition.getPoOrderListData().isEmpty()) {
            // 计算申请付款总金额：sum(order_amount)
            BigDecimal payableAmountTotal = searchCondition.getPoOrderListData().stream()
                    .filter(item -> item.getOrder_amount() != null)
                    .map(BApSourceAdvanceVo::getOrder_amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            searchCondition.setPayable_amount_total(payableAmountTotal);
            
            // 已付款总金额初始化为0
            searchCondition.setPaid_amount_total(BigDecimal.ZERO);
            
            // 付款中总金额初始化为0
            searchCondition.setPaying_amount_total(BigDecimal.ZERO);
            
            // 未付款总金额 = 申请付款总金额
            searchCondition.setUnpay_amount_total(payableAmountTotal);
        }
        
        // 2、付款账户信息计算逻辑
        if (searchCondition.getBankListData() != null && !searchCondition.getBankListData().isEmpty()) {
            // 计算付款总金额：付款账户信息的付款金额汇总值 = sum(payable_amount)
            BigDecimal detailPayableAmount = searchCondition.getBankListData().stream()
                    .filter(item -> item.getPayable_amount() != null)
                    .map(BApDetailVo::getPayable_amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            searchCondition.setDetail_payable_amount(detailPayableAmount);
        }
    }

    /**
     * 获取业务类型
     */
    @Override
    public List<BApVo> getType() {
        return mapper.getType();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApVo> startInsert(BApVo searchCondition) {
        // 1. 校验业务规则
        checkInsertLogic(searchCondition);
        
        // 2.保存采购合同
        InsertResultAo<BApVo> insertResultAo = insert(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP);

        return insertResultAo;
    }

    /**
     * 更新
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApVo> startUpdate(BApVo searchCondition) {
        // 1. 校验业务规则
        checkUpdateLogic(searchCondition);
        
        // 2.保存采购合同
        UpdateResultAo<BApVo> insertResultAo = update(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP);

        return insertResultAo;
    }

    /**
     * 分页查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<BApVo> selectPage(BApVo searchCondition) {
        // 分页条件
        Page<BApVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }


    /**
     * 根据id查询应付账款详细信息
     * 
     * 功能说明：
     * 1. 查询应付账款基础信息
     * 2. 查询付款附件信息
     * 3. 针对预付款类型（type=2）的特殊处理：
     *    - 查询源单信息判断业务类型
     *    - 如果是预付款类型，则获取关联单据信息（订单）和付款信息（银行账号）
     * 4. 查询作废记录相关信息
     * 
     * 注意：本方法包含了对预付款业务的特殊逻辑处理，确保预付款单据能够正确显示
     * 关联的订单信息和银行账户信息，提升用户体验和业务完整性。
     *
     * @param id 应付账款ID
     * @return 完整的应付账款信息，包含关联单据和付款信息
     */
    @Override
    public BApVo selectById(Integer id) {
        BApVo baPVo = mapper.selectId(id);

        // 付款附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(baPVo.getDoc_att_file());
        baPVo.setDoc_att_files(doc_att_files);

        /**
         * 业务单据信息数据：
         * 数据获取防范：
         * 先找源单：应付账款关联单据表-源单，type：1-应付、2-预付（b_ap_source_advance）、3-其他支出
         * 2-预付场合（b_ap_source_advance）：这里找数据
         */

        // 根据应付账款ID查询源单信息，判断type是否为预付款类型
        BApSourceVo bApSourceVo = mapper.getApSource(id);
        if (bApSourceVo != null && DictConstant.DICT_B_AP_TYPE_TWO.equals(bApSourceVo.getType())) {
            // type = 2（预付），执行后续逻辑
            
            // 1. 获取关联单据信息（订单）
            List<BApSourceAdvanceVo> poOrderList = mapper.getApSourceAdvancePayOnlyUsedInUpdateType(id);
            baPVo.setPoOrderListData(poOrderList);
            
            // 2. 获取付款信息（银行账号）
            List<BApDetailVo> bankList = mapper.getApDetail(id);
            baPVo.setBankListData(bankList);
        }

        // 付款单明细表：b_ap_pay_detail

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_AP_STATUS_FOUR.equals(baPVo.getStatus()) || Objects.equals(baPVo.getStatus(), DictConstant.DICT_B_AP_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(baPVo.getId());
            serialIdAndType.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                baPVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    baPVo.setCancel_files(cancel_doc_att_files);
                }
                // 通过表m_staff获取作废提交人名称
                if (mCancelVo.getC_id() != null) {
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                    if (staffVo != null) {
                        baPVo.setCancel_name(staffVo.getName());
                    }
                }
                // 作废时间
                baPVo.setCancel_time(mCancelVo.getC_time());
            }
        }
        // 查询中止相关信息
        if (DictConstant.DICT_B_AP_PAY_STATUS_STOP.equals(baPVo.getPay_status())) {
            // 中止附件信息
            if (baPVo.getStoppay_file() != null) {
                List<SFileInfoVo> stop_doc_att_files = isFileService.selectFileInfo(baPVo.getStoppay_file());
                baPVo.setStoppay_files(stop_doc_att_files);
            }
            // 通过表m_staff获取中止提交人名称
            if (baPVo.getStoppay_u_id() != null) {
                MStaffVo searchCondition = new MStaffVo();
                searchCondition.setId(baPVo.getStoppay_u_id().longValue());
                MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                if (staffVo != null) {
                    baPVo.setStop_name(staffVo.getName());
                }
            }
        }


        return baPVo;
    }

    /**
     * 校验
     *
     * @param searchCondition
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BApVo searchCondition, String checkType) {
        BApEntity bApEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getPoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个采购订单");
                }

                Map<String, Long> collect = searchCondition.getPoOrderListData()
                        .stream()
                        .map(BApSourceAdvanceVo::getPo_order_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("采购订单添加重复",result);
                }

                if (searchCondition.getPoOrderListData().size()>1){
//                    return CheckResultUtil.NG("采购订单只能添加一个");
                }

                for (BApSourceAdvanceVo poOrderListDatum : searchCondition.getPoOrderListData()) {
                    if (poOrderListDatum.getOrder_amount()==null||poOrderListDatum.getOrder_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：业务单据信息表格中存在本次申请金额为0的数据，请检查！");
                    }
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                for (BApDetailVo bApDetailVo : searchCondition.getBankListData()) {
                    if (bApDetailVo.getPayable_amount() == null||bApDetailVo.getPayable_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：付款账户表格中存在付款金额为0的数据，请检查！");
                    }
                }

                // 校验付款总金额与申请付款总金额是否一致
                if (searchCondition.getDetail_payable_amount() != null && searchCondition.getPayable_amount_total() != null) {
                    if (searchCondition.getDetail_payable_amount().compareTo(searchCondition.getPayable_amount_total()) != 0) {
                        return CheckResultUtil.NG(String.format("付款数据校验失败：请确保各账户付款金额之和与申请付款金额完全一致。申请付款总金额：%s，付款总金额：%s", 
                                searchCondition.getPayable_amount_total(), searchCondition.getDetail_payable_amount()));
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(searchCondition.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，采购订单[%s]不是待审批,驳回状态,无法修改", bApEntity.getCode()));
                }

                if (searchCondition.getPoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个采购订单");
                }

                Map<String, Long> collect1 = searchCondition.getPoOrderListData()
                        .stream()
                        .map(BApSourceAdvanceVo::getPo_order_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result1 = new ArrayList<>();
                collect1.forEach((k,v)->{
                    if(v>1) result1.add(k);
                });

                if (result1!=null&&result1.size()>0){
                    return CheckResultUtil.NG("采购订单添加重复",result1);
                }

                if (searchCondition.getPoOrderListData().size()>1){
//                    return CheckResultUtil.NG("采购订单只能添加一个");
                }

                for (BApSourceAdvanceVo poOrderListDatum : searchCondition.getPoOrderListData()) {
                    if (poOrderListDatum.getOrder_amount()==null||poOrderListDatum.getOrder_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("请填写需要付款的金额，当前提交的付款金额不能为0！");
                    }
                }

                if (searchCondition.getBankListData().size()>1){
                    return CheckResultUtil.NG("银行账户信息添加一个");
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                for (BApDetailVo bApDetailVo : searchCondition.getBankListData()) {
                    if (bApDetailVo.getPayable_amount() == null||bApDetailVo.getPayable_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：付款账户表格中存在付款金额为0的数据，请检查！");
                    }
                }

                // 校验付款总金额与申请付款总金额是否一致
                if (searchCondition.getDetail_payable_amount() != null && searchCondition.getPayable_amount_total() != null) {
                    if (searchCondition.getDetail_payable_amount().compareTo(searchCondition.getPayable_amount_total()) != 0) {
                        return CheckResultUtil.NG(String.format("付款数据校验失败：请确保各账户付款金额之和与申请付款金额完全一致。申请付款总金额：%s，付款总金额：%s", 
                                searchCondition.getPayable_amount_total(), searchCondition.getDetail_payable_amount()));
                    }
                }

                break;
            // 删除
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(searchCondition.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，付款管理[%s]不是待审批,驳回状态,无法删除", bApEntity.getCode()));
                }

                List<BApPayVo> delBApPayVo = bApPayMapper.selectApPayByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AP_PAY_STATUS_TWO);
                if (CollectionUtil.isNotEmpty(delBApPayVo)) {
                    return CheckResultUtil.NG("删除失败，存在付款单。");
                }
                break;
            // 作废
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(searchCondition.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_FIVE) || Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，付款管理[%s]无法重复作废",bApEntity.getCode()));
                }
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，付款管理[%s]审核中，无法作废",bApEntity.getCode()));
                }

                List<BApPayVo> cancelBApPayVo = bApPayMapper.selectApPayByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AP_PAY_STATUS_TWO);
                if (CollectionUtil.isNotEmpty(cancelBApPayVo)) {
                    return CheckResultUtil.NG(String.format("作废失败，该应付账款下付款单号%s数据尚未作废，请先完成该付款单的作废。",cancelBApPayVo.stream().map(BApPayVo::getCode).collect(Collectors.toList())));
                }

                break;
            // 中止付款
            case CheckResultAo.STOP_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(searchCondition.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("中止失败，付款管理[%s]未审批通过",bApEntity.getCode()));
                }

//                // 是否已付款
//                if (!Objects.equals(bApEntity.getPay_status(), DictConstant.DICT_B_AP_PAY_STATUS_TWO)) {
//                    return CheckResultUtil.NG(String.format("中止失败，付款管理[%s]已付款",bApEntity.getCode()));
//                }

                // 是否已中止
                if (Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_PAY_STATUS_STOP)) {
                    return CheckResultUtil.NG(String.format("中止失败，付款管理[%s]无法重复中止付款",bApEntity.getCode()));
                }

                // 查询是否存在付款单，且状态是待付款
                List<BApPayVo> stopBApPayVo = bApPayMapper.selectApPayByStatus(searchCondition.getId(), DictConstant.DICT_B_AP_PAY_STATUS_ZERO);
                if (CollectionUtil.isNotEmpty(stopBApPayVo)) {
                    return CheckResultUtil.NG("中止失败，该应付账款下，付款单“"+stopBApPayVo.stream().map(BApPayVo::getCode).collect(Collectors.toList())+"”状态为待付款，请先完成该付款单的处理。");
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
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BApVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BApVo bApVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("业务类型:", bApVo.getType_name());
        jsonObject.put("申请付款总金额:", bApVo.getPayable_amount());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bApVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 新增应付账款主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApVo> insert(BApVo vo) {
        // 1. 计算汇总数据
        calculateSummaryData(vo);
        // 3. 保存主表信息
        BApEntity bApEntity = saveMainEntity(vo);
        // 4. 保存源单信息
        saveSourceEntity(vo, bApEntity);
        // 5. 保存源单-预收款信息
        saveSourceAdvanceEntity(vo, bApEntity);
        // 6. 保存银行账户明细
        saveDetailList(vo, bApEntity);
        // 7. 保存附件信息
        saveAttach(vo, bApEntity);
        // 8. 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByApId(bApEntity.getId());
//        bApTotalService.calcNewApAmount(bApEntity.getId());
        // 9. 设置返回ID
        vo.setId(bApEntity.getId());
        return InsertResultUtil.OK(vo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BApVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BApEntity saveMainEntity(BApVo searchCondition) {
        BApEntity bApEntity = new BApEntity();
        BeanUtils.copyProperties(searchCondition, bApEntity);
        bApEntity.setCode(bApAutoCodeService.autoCode().getCode());
        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_ONE);
        bApEntity.setPay_status(DictConstant.DICT_B_AP_PAY_STATUS_ZERO);
        bApEntity.setIs_del(Boolean.FALSE);
        bApEntity.setBpm_process_name("新增付款管理审批");
        bApEntity.setId(null);
        int bAp = mapper.insert(bApEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("新增失败");
        }
        return bApEntity;
    }

    /**
     * 保存源单信息
     */
    private void saveSourceEntity(BApVo searchCondition, BApEntity bApEntity) {
        BApSourceEntity bApSourceEntity = new BApSourceEntity();
        bApSourceEntity.setAp_id(bApEntity.getId());
        bApSourceEntity.setAp_code(bApEntity.getCode());
        bApSourceEntity.setType(bApEntity.getType());
        bApSourceEntity.setPo_order_code(bApEntity.getPo_order_code());
        bApSourceEntity.setPo_order_id(bApEntity.getPo_order_id());
        bApSourceEntity.setPo_contract_id(bApEntity.getPo_contract_id());
        bApSourceEntity.setPo_contract_code(bApEntity.getPo_contract_code());
        bApSourceEntity.setProject_code(bApEntity.getProject_code());
        int bApSource = bApSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 保存源单-预收款信息
     * 新增，所以未付款=本次付款金额
     */
    private void saveSourceAdvanceEntity(BApVo searchCondition, BApEntity bApEntity) {
        for (BApSourceAdvanceVo bApSourceAdvanceVo : searchCondition.getPoOrderListData()) {
            BApSourceAdvanceEntity bApSourceAdvanceEntity = new BApSourceAdvanceEntity();
            BeanUtils.copyProperties(bApSourceAdvanceVo, bApSourceAdvanceEntity);
            bApSourceAdvanceEntity.setCode(bApSourceAdvanceAutoCodeService.autoCode().getCode());
            bApSourceAdvanceEntity.setAp_id(bApEntity.getId());
            bApSourceAdvanceEntity.setAp_code(bApEntity.getCode());
            bApSourceAdvanceEntity.setType(bApEntity.getType());
            bApSourceAdvanceEntity.setId(null);

            // 初始化付款相关金额字段
            bApSourceAdvanceEntity.setPayable_amount_total(bApSourceAdvanceVo.getOrder_amount());
            bApSourceAdvanceEntity.setPaid_amount_total(BigDecimal.ZERO);
            bApSourceAdvanceEntity.setPaying_amount_total(BigDecimal.ZERO);
            bApSourceAdvanceEntity.setUnpay_amount_total(bApSourceAdvanceEntity.getOrder_amount());

            int bApSourceAdvance = bApSourceAdvanceMapper.insert(bApSourceAdvanceEntity);
            if (bApSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 保存银行账户明细
     */
    private void saveDetailList(BApVo searchCondition, BApEntity bApEntity) {
        for (BApDetailVo bApDetailVo : searchCondition.getBankListData()) {
            BApDetailEntity bApDetailEntity = new BApDetailEntity();
            BeanUtils.copyProperties(bApDetailVo, bApDetailEntity);
            bApDetailEntity.setCode(bApDetailAutoCodeService.autoCode().getCode());
            bApDetailEntity.setAp_id(bApEntity.getId());
            bApDetailEntity.setAp_code(bApEntity.getCode());
            bApDetailEntity.setPaid_amount(BigDecimal.ZERO);
            int bApDetail = bApDetailMapper.insert(bApDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BApVo searchCondition, BApEntity bApEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP);
        BApAttachEntity bApAttachEntity = insertFile(fileEntity, searchCondition, new BApAttachEntity());
        bApAttachEntity.setAp_id(bApEntity.getId());
        int attachInsert = bApAttachMapper.insert(bApAttachEntity);
        if (attachInsert <= 0) {
            throw new UpdateErrorException("新增附件信息失败");
        }
    }

    /**
     * 更新应付账款主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApVo> update(BApVo searchCondition) {
        // 1. 计算汇总数据
        calculateSummaryData(searchCondition);
        // 3. 更新主表信息
        BApEntity bApEntity = updateMainEntity(searchCondition);
        // 4. 更新源单信息
        updateSourceEntity(searchCondition, bApEntity);
        // 5. 更新源单-预收款信息
        updateSourceAdvanceEntity(searchCondition, bApEntity);
        // 6. 更新银行账户明细
        updateDetailList(searchCondition, bApEntity);
        // 7. 更新附件信息
        updateAttach(searchCondition, bApEntity);
        // 8. 先删除再汇总财务数据
//        bApTotalService.calcDeleteApAmount(bApEntity.getId());
//        bApTotalService.calcNewApAmount(bApEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BApVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新主表信息
     */
    private BApEntity updateMainEntity(BApVo searchCondition) {
        BApEntity bApEntity = (BApEntity) BeanUtilsSupport.copyProperties(searchCondition, BApEntity.class);
        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_ONE);
        bApEntity.setIs_del(Boolean.FALSE);
        bApEntity.setBpm_process_name("修改付款管理审批");
        int bAp = mapper.updateById(bApEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("新增失败");
        }
        return bApEntity;
    }

    /**
     * 更新源单信息
     */
    private void updateSourceEntity(BApVo searchCondition, BApEntity bApEntity) {
        bApSourceMapper.deleteByApId(bApEntity.getId());
        BApSourceEntity bApSourceEntity = new BApSourceEntity();
        bApSourceEntity.setAp_id(bApEntity.getId());
        bApSourceEntity.setAp_code(bApEntity.getCode());
        bApSourceEntity.setType(bApEntity.getType());
        bApSourceEntity.setPo_order_id(bApEntity.getPo_order_id());
        bApSourceEntity.setPo_order_code(bApEntity.getPo_order_code());
        bApSourceEntity.setPo_contract_code(bApEntity.getPo_contract_code());
        bApSourceEntity.setProject_code(bApEntity.getProject_code());
        int bApSource = bApSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 更新源单-预收款信息
     */
    private void updateSourceAdvanceEntity(BApVo searchCondition, BApEntity bApEntity) {
        bApSourceAdvanceMapper.deleteByApId(bApEntity.getId());
        for (BApSourceAdvanceVo bApSourceAdvanceVo : searchCondition.getPoOrderListData()) {
            BApSourceAdvanceEntity bApSourceAdvanceEntity = new BApSourceAdvanceEntity();
            BeanUtils.copyProperties(bApSourceAdvanceVo, bApSourceAdvanceEntity);
            bApSourceAdvanceEntity.setCode(bApSourceAdvanceAutoCodeService.autoCode().getCode());
            bApSourceAdvanceEntity.setAp_id(bApEntity.getId());
            bApSourceAdvanceEntity.setAp_code(bApEntity.getCode());
            bApSourceAdvanceEntity.setType(bApEntity.getType());
            int bApSourceAdvance = bApSourceAdvanceMapper.insert(bApSourceAdvanceEntity);
            if (bApSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 更新银行账户明细
     */
    private void updateDetailList(BApVo searchCondition, BApEntity bApEntity) {
        bApDetailMapper.deleteByApId(bApEntity.getId());
        for (BApDetailVo bApDetailVo : searchCondition.getBankListData()) {
            BApDetailEntity bApDetailEntity = new BApDetailEntity();
            BeanUtils.copyProperties(bApDetailVo, bApDetailEntity);
            bApDetailEntity.setCode(bApDetailAutoCodeService.autoCode().getCode());
            bApDetailEntity.setAp_id(bApEntity.getId());
            bApDetailEntity.setAp_code(bApEntity.getCode());
            bApDetailEntity.setPaid_amount(BigDecimal.ZERO);
            int bApDetail = bApDetailMapper.insert(bApDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BApVo searchCondition, BApEntity bApEntity) {
        BApAttachVo bApAttachVo = bApAttachMapper.selectByApId(bApEntity.getId());
        if (bApAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP);
            BApAttachEntity bApAttachEntity = (BApAttachEntity) BeanUtilsSupport.copyProperties(bApAttachVo, BApAttachEntity.class);
            insertFile(fileEntity, searchCondition, bApAttachEntity);
            bApAttachEntity.setAp_id(bApEntity.getId());
            int attachUpdate = bApAttachMapper.updateById(bApAttachEntity);
            if (attachUpdate <= 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bApEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP);
            BApAttachEntity bApAttachEntity = new BApAttachEntity();
            insertFile(fileEntity, searchCondition, bApAttachEntity);
            bApAttachEntity.setAp_id(bApEntity.getId());
            int attachInsert = bApAttachMapper.insert(bApAttachEntity);
            if (attachInsert <= 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 审批流程 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BApVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bApEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_TWO);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BApVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_THREE);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程 审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BApVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_ZERO);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  审批流程 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BApVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bApEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bApEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bApEntity);

        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程 审批流程通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BApVo vo) {
        log.debug("====》付款管理[{}]作废审批流程通过，更新开始《====",vo.getId());
        BApEntity bApEntity = mapper.selectById(vo.getId());

        bApEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bApEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_FIVE);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 更新b_ap_source_advance表的作废金额
        updateSourceAdvanceCancelAmount(vo.getId());

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByApId(bApEntity.getId());

        log.debug("====》付款管理[{}]作废审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 更新预付款源单的作废金额
     * 当应付账款作废审批通过时，将该应付账款下所有预付款源单的作废金额设置为申请金额
     * 
     * @param apId 应付账款ID
     */
    private void updateSourceAdvanceCancelAmount(Integer apId) {
        log.debug("====》开始更新应付账款[{}]下预付款源单的作废金额《====", apId);
        
        // 查询该应付账款下的所有预付款源单记录
        List<BApSourceAdvanceVo> sourceAdvanceList = bApSourceAdvanceMapper.selectByApId(apId);
        if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
            log.debug("应付账款[{}]下没有预付款源单记录，跳过更新", apId);
            return;
        }
        
        // 更新每条预付款源单记录的作废金额
        for (BApSourceAdvanceVo sourceAdvanceVo : sourceAdvanceList) {
            BApSourceAdvanceEntity entity = new BApSourceAdvanceEntity();
            // 复制VO到Entity的所有字段值
            BeanUtils.copyProperties(sourceAdvanceVo, entity);
            // 设置作废金额为申请金额
            entity.setCancelpay_amount_total(sourceAdvanceVo.getOrder_amount());
            
            int updateResult = bApSourceAdvanceMapper.updateById(entity);
            if (updateResult <= 0) {
                log.error("更新预付款源单[{}]作废金额失败", sourceAdvanceVo.getId());
                throw new UpdateErrorException("更新预付款源单作废金额失败");
            }
            
            log.debug("更新预付款源单[{}]作废金额成功，金额: {}", 
                sourceAdvanceVo.getId(), sourceAdvanceVo.getOrder_amount());
        }
        
        log.debug("====》完成更新应付账款[{}]下预付款源单的作废金额，共处理{}条记录《====", 
            apId, sourceAdvanceList.size());
    }

    /**
     * 作废审批流程 作废审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BApVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_TWO);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP);
        mCancelService.delete(mCancelVo);

        log.debug("====》付款管理[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 作废审批流程 作废审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BApVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_TWO);
        bApEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP);
        mCancelService.delete(mCancelVo);

        log.debug("====》付款管理[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BApVo searchCondition) {
        log.debug("====》付款管理[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BApEntity bApEntity = mapper.selectById(searchCondition.getId());

        bApEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bApEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bApEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bApEntity);

        log.debug("====》付款管理[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 汇总查询
     * @param searchCondition
     */
    @Override
    public BApVo querySum(BApVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 导出查询
     *
     * @param param
     */
    @Override
    public List<BApVo> selectExportList(BApVo param) {
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
     * @param searchCondition
     */
    @Override
    public BApVo getPrintInfo(BApVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_B_AP);
        SPagesVo pagesVo = isPagesService.get(param);

        /**
         * 获取打印配置信息
         * 3、从s_app_config中获取，报表系统的app_key，securit_key
         */
//        SAppConfigEntity key = isAppConfigService.getDataByAppCode(AppConfigConstant.PRINT_SYSTEM_CODE);

        String printUrl =  url + pagesVo.getPrint_code() + "?token=" + token + "&id=" + searchCondition.getId();
//        printUrl = printUrl + "&app_key=" + key.getApp_key() + "&secret_key=" + key.getSecret_key();
        searchCondition.setPrint_url(printUrl);
        searchCondition.setQr_code(printUrl);
        log.debug("打印地址：" + printUrl);
        return searchCondition;
    }

    /**
     * 删除
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BApVo> searchCondition) {
        for (BApVo BApVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(BApVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BApEntity bApEntity = mapper.selectById(BApVo.getId());
            bApEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bApEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            } else {
                // 计算应付账款管理表-财务数据汇总
//                bApTotalService.calcDeleteApAmount(bApEntity.getId());
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApVo> cancel(BApVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BApEntity bApEntity = mapper.selectById(searchCondition.getId());
        bApEntity.setStatus(DictConstant.DICT_B_AP_STATUS_FOUR);
        bApEntity.setBpm_cancel_process_name("作废付款管理审批");

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        int insert = mapper.updateById(bApEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByApId(bApEntity.getId());
        
        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_CANCEL);

        return UpdateResultUtil.OK(searchCondition);
    }


    /**
     * 中止付款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> suspendPayment(BApVo searchCondition) {
        // 1. 业务校验
        validateSuspendPayment(searchCondition);
        
        // 2. 更新应付账款主表状态
        BApEntity bApEntity = updateApMainStatus(searchCondition);
        
        // 3. 处理中止付款附件
        processSuspendAttachment(bApEntity, searchCondition);
        
        // 4. 中止相关付款单（因为校验付款单状态！=作废、完成不能中止，所以下面的这个方法永远不会被执行）
//        suspendRelatedPayments(bApEntity.getId());
        
        // 5. 重新计算汇总数据
        recalculateApTotalData(bApEntity.getId());
        
        return UpdateResultUtil.OK(1);
    }

    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BApVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BApVo bApVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", bApVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bApVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 附件处理逻辑
     */
    public BApAttachEntity insertFile(SFileEntity fileEntity, BApVo vo, BApAttachEntity extra) {
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
            extra.setOneFile(fileEntity.getId());
            fileEntity.setId(null);
        } else {
            extra.setOneFile(null);
        }
        return extra;
    }

    /**
     * 取消文件处理逻辑
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BApVo vo) {
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
     * 停止文件处理逻辑
     */
    public SFileEntity insertStopFile(SFileEntity fileEntity, BApVo vo) {
        // 停止附件新增
        if (vo.getStoppay_files() != null && vo.getStoppay_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo stop_file : vo.getStoppay_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                stop_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(stop_file, fileInfoEntity);
                fileInfoEntity.setFile_name(stop_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }
        return fileEntity;
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(BApVo bean,String type){
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

// ==================== 中止付款业务方法拆分 ====================

    /**
     * 验证中止付款业务逻辑
     * @param searchCondition 应付账款信息
     */
    private void validateSuspendPayment(BApVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.STOP_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新应付账款主表状态为中止付款
     * @param searchCondition 应付账款信息
     * @return 更新后的应付账款实体
     */
    private BApEntity updateApMainStatus(BApVo searchCondition) {
        BApEntity bApEntity = mapper.selectById(searchCondition.getId());
        bApEntity.setPay_status(DictConstant.DICT_B_AP_PAY_STATUS_STOP);
        bApEntity.setStoppay_reason(searchCondition.getStoppay_reason());
        bApEntity.setStoppay_u_id(SecurityUtil.getStaff_id().intValue());
        bApEntity.setStoppay_u_time(LocalDateTime.now());
        int updateResult = mapper.updateById(bApEntity);
        if (updateResult == 0) {
            throw new UpdateErrorException("应付账款主表状态更新失败");
        }
        
        return bApEntity;
    }

    /**
     * 处理中止付款附件
     * @param bApEntity 应付账款实体
     * @param searchCondition 应付账款信息（包含附件）
     */
    private void processSuspendAttachment(BApEntity bApEntity, BApVo searchCondition) {
        // 保存中止付款附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP);
        fileEntity = insertStopFile(fileEntity, searchCondition);

        // 更新应付账款实体的附件ID
        bApEntity.setStoppay_file(fileEntity.getId());
        int updateResult = mapper.updateById(bApEntity);
        if (updateResult == 0) {
            throw new UpdateErrorException("应付账款附件信息更新失败");
        }
    }

    /**
     * 中止相关付款单及其明细
     * @param apId 应付账款主表ID
     */
    private void suspendRelatedPayments(Integer apId) {
        // 查询待付款状态的付款单
        List<BApPayVo> bApPayVos = bApPayMapper.selectApPayByStatus(apId, DictConstant.DICT_B_AP_PAY_STATUS_ZERO);
        
        if (CollectionUtil.isNotEmpty(bApPayVos)) {
            for (BApPayVo paymentVo : bApPayVos) {
                // 更新付款单状态为中止
                updatePaymentStatusToSuspend(paymentVo);
                
                // 更新付款单明细状态
                updatePaymentDetailsToSuspend(paymentVo.getId());
            }
        }
    }

    /**
     * 更新付款单状态为中止
     * @param paymentVo 付款单信息
     */
    private void updatePaymentStatusToSuspend(BApPayVo paymentVo) {
        paymentVo.setStatus(DictConstant.DICT_B_AP_PAY_STATUS_STOP);
        BApPayEntity bApPayEntity = (BApPayEntity) BeanUtilsSupport.copyProperties(paymentVo, BApPayEntity.class);
        
        int updateResult = bApPayMapper.updateById(bApPayEntity);
        if (updateResult <= 0) {
            throw new UpdateErrorException("付款单状态更新失败");
        }
    }

    /**
     * 更新付款单明细状态为中止
     * @param paymentId 付款单ID
     */
    private void updatePaymentDetailsToSuspend(Integer paymentId) {
        List<BApPayDetailVo> bApPayDetailVos = bApPayDetailMapper.selectById(paymentId);
        
        if (CollectionUtil.isNotEmpty(bApPayDetailVos)) {
            for (BApPayDetailVo detailVo : bApPayDetailVos) {
                // 设置中止付款相关金额字段
                detailVo.setPaying_amount(BigDecimal.ZERO);                   // 付款中金额清零
                detailVo.setUnpay_amount(BigDecimal.ZERO);                    // 未付金额清零
                detailVo.setCancel_amount(BigDecimal.ZERO);                   // 作废金额清零
                
                BApPayDetailEntity bApPayDetailEntity = (BApPayDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BApPayDetailEntity.class);
                int updateResult = bApPayDetailMapper.updateById(bApPayDetailEntity);
                if (updateResult <= 0) {
                    throw new UpdateErrorException("付款单明细更新失败");
                }
            }
        }
    }

    /**
     * 重新计算应付账款汇总数据
     * @param apId 应付账款主表ID
     */
    private void recalculateApTotalData(Integer apId) {
        // 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByApId(apId);
    }

}
