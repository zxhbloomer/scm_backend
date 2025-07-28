package com.xinyirun.scm.core.system.serviceimpl.business.so.ar;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.so.ar.*;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveEntity;
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
import com.xinyirun.scm.bean.system.vo.business.so.ar.*;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;
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
import com.xinyirun.scm.core.system.mapper.business.so.ar.*;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.so.ar.IBArService;
import com.xinyirun.scm.core.system.service.business.so.ar.IBArTotalService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund.CommonFundServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BArAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BArDetailAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BArSourceAdvanceAutoCodeServiceImpl;
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
 * 应收账款管理表（Accounts Receivable） 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Slf4j
@Service
public class BArServiceImpl extends ServiceImpl<BArMapper, BArEntity> implements IBArService {

    @Autowired
    private BArMapper mapper;

    @Autowired
    private BArDetailMapper bArDetailMapper;

    @Autowired
    private BArSourceMapper bArSourceMapper;

    @Autowired
    private BArSourceAdvanceMapper bArSourceAdvanceMapper;

    @Autowired
    private BArAutoCodeServiceImpl bArAutoCodeService;

    @Autowired
    private BArSourceAdvanceAutoCodeServiceImpl bArSourceAdvanceAutoCodeService;

    @Autowired
    private BArDetailAutoCodeServiceImpl bArDetailAutoCodeService;

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
    private BArReceiveMapper bArReceiveMapper;

    @Autowired
    private CommonFundServiceImpl commonFundService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private BArAttachMapper bArAttachMapper;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private IBArTotalService bArTotalService;

    @Autowired
    private ICommonSoTotalService commonTotalService;

    @Autowired
    private BArReceiveDetailMapper bArReceiveDetailMapper;

    /**
     * 计算业务单据信息、收款账户信息汇总数据
     * @param searchCondition 搜索条件
     */
    private void calculateSummaryData(BArVo searchCondition) {
        // 1、业务单据信息计算逻辑
        if (searchCondition.getSoOrderListData() != null && !searchCondition.getSoOrderListData().isEmpty()) {
            // 计算申请收款总金额：sum(order_amount)
            BigDecimal receivableAmountTotal = searchCondition.getSoOrderListData().stream()
                    .filter(item -> item.getOrder_amount() != null)
                    .map(BArSourceAdvanceVo::getOrder_amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            searchCondition.setReceivable_amount_total(receivableAmountTotal);
            
            // 已收款总金额初始化为0
            searchCondition.setReceived_amount_total(BigDecimal.ZERO);
            
            // 收款中总金额初始化为0
            searchCondition.setReceiving_amount_total(BigDecimal.ZERO);
            
            // 未收款总金额 = 申请收款总金额
            searchCondition.setUnreceive_amount_total(receivableAmountTotal);
        }
        
        // 2、收款账户信息计算逻辑
        if (searchCondition.getBankListData() != null && !searchCondition.getBankListData().isEmpty()) {
            // 计算收款总金额：收款账户信息的收款金额汇总值 = sum(receivable_amount)
            BigDecimal detailReceivableAmount = searchCondition.getBankListData().stream()
                    .filter(item -> item.getReceivable_amount() != null)
                    .map(BArDetailVo::getReceivable_amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            searchCondition.setDetail_receivable_amount(detailReceivableAmount);
        }
    }

    /**
     * 获取业务类型
     */
    @Override
    public List<BArVo> getType() {
        return mapper.getType();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BArVo> startInsert(BArVo searchCondition) {
        // 1. 校验业务规则
        checkInsertLogic(searchCondition);
        
        // 2.保存销售合同
        InsertResultAo<BArVo> insertResultAo = insert(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AR);

        return insertResultAo;
    }

    /**
     * 更新
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BArVo> startUpdate(BArVo searchCondition) {
        // 1. 校验业务规则
        checkUpdateLogic(searchCondition);
        
        // 2.保存销售合同
        UpdateResultAo<BArVo> insertResultAo = update(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AR);

        return insertResultAo;
    }

    /**
     * 分页查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<BArVo> selectPage(BArVo searchCondition) {
        // 分页条件
        Page<BArVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }


    /**
     * 根据id查询应收账款详细信息
     * 
     * 功能说明：
     * 1. 查询应收账款基础信息
     * 2. 查询收款附件信息
     * 3. 针对预收款类型（type=2）的特殊处理：
     *    - 查询源单信息判断业务类型
     *    - 如果是预收款类型，则获取关联单据信息（订单）和收款信息（银行账号）
     * 4. 查询作废记录相关信息
     * 
     * 注意：本方法包含了对预收款业务的特殊逻辑处理，确保预收款单据能够正确显示
     * 关联的订单信息和银行账户信息，提升用户体验和业务完整性。
     *
     * @param id 应收账款ID
     * @return 完整的应收账款信息，包含关联单据和收款信息
     */
    @Override
    public BArVo selectById(Integer id) {
        BArVo arVo = mapper.selectId(id);

        // 收款附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(arVo.getDoc_att_file());
        arVo.setDoc_att_files(doc_att_files);

        /**
         * 业务单据信息数据：
         * 数据获取防范：
         * 先找源单：应收账款关联单据表-源单，type：1-应收、2-预收（b_ar_source_advance）、3-其他收入
         * 2-预收场合（b_ar_source_advance）：这里找数据
         */

        // 根据应收账款ID查询源单信息，判断type是否为预收款类型
        BArSourceVo bArSourceVo = mapper.getArSource(id);
        if (bArSourceVo != null && DictConstant.DICT_B_AR_TYPE_TWO.equals(bArSourceVo.getType())) {
            // type = 2（预收），执行后续逻辑
            
            // 1. 获取关联单据信息（订单）
            List<BArSourceAdvanceVo> soOrderList = mapper.getArSourceAdvanceReceiveOnlyUsedInUpdateType(id);
            arVo.setSoOrderListData(soOrderList);
            
            // 2. 获取收款信息（银行账号）
            List<BArDetailVo> bankList = mapper.getArDetail(id);
            arVo.setBankListData(bankList);
        }

        // 收款单明细表：b_ar_receive_detail

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_AR_STATUS_FOUR.equals(arVo.getStatus()) || Objects.equals(arVo.getStatus(), DictConstant.DICT_B_AR_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(arVo.getId());
            serialIdAndType.setSerial_type(SystemConstants.SERIAL_TYPE.B_AR);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            if (mCancelVo != null) {
                // 作废理由
                arVo.setCancel_reason(mCancelVo.getRemark());
                // 作废附件信息
                if (mCancelVo.getFile_id() != null) {
                    List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                    arVo.setCancel_files(cancel_doc_att_files);
                }
                // 通过表m_staff获取作废提交人名称
                if (mCancelVo.getC_id() != null) {
                    MStaffVo searchCondition = new MStaffVo();
                    searchCondition.setId(mCancelVo.getC_id());
                    MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                    if (staffVo != null) {
                        arVo.setCancel_name(staffVo.getName());
                    }
                }
                // 作废时间
                arVo.setCancel_time(mCancelVo.getC_time());
            }
        }
        // 查询中止相关信息
        if (DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP.equals(arVo.getReceive_status())) {
            // 中止附件信息
            if (arVo.getStopreceive_file() != null) {
                List<SFileInfoVo> stop_doc_att_files = isFileService.selectFileInfo(arVo.getStopreceive_file());
                arVo.setStopreceive_files(stop_doc_att_files);
            }
            // 通过表m_staff获取中止提交人名称
            if (arVo.getStopReceive_u_id() != null) {
                MStaffVo searchCondition = new MStaffVo();
                searchCondition.setId(arVo.getStopReceive_u_id().longValue());
                MStaffVo staffVo = mStaffMapper.selectByid(searchCondition);
                if (staffVo != null) {
                    arVo.setStop_name(staffVo.getName());
                }
            }
        }

        return arVo;
    }

    @Override
    public CheckResultAo checkLogic(BArVo searchCondition, String checkType) {
        BArEntity bArEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getSoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个销售订单");
                }

                Map<String, Long> collect = searchCondition.getSoOrderListData()
                        .stream()
                        .map(BArSourceAdvanceVo::getSo_order_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("销售订单添加重复",result);
                }

                if (searchCondition.getSoOrderListData().size()>1){
//                    return CheckResultUtil.NG("销售订单只能添加一个");
                }

                for (BArSourceAdvanceVo soOrderListDatum : searchCondition.getSoOrderListData()) {
                    if (soOrderListDatum.getOrder_amount()==null||soOrderListDatum.getOrder_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：业务单据信息表格中存在本次申请金额为0的数据，请检查！");
                    }
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                for (BArDetailVo bArDetailVo : searchCondition.getBankListData()) {
                    if (bArDetailVo.getReceivable_amount() == null||bArDetailVo.getReceivable_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：收款账户表格中存在收款金额为0的数据，请检查！");
                    }
                }

                // 校验收款总金额与申请收款总金额是否一致
                if (searchCondition.getDetail_receivable_amount() != null && searchCondition.getReceivable_amount_total() != null) {
                    if (searchCondition.getDetail_receivable_amount().compareTo(searchCondition.getReceivable_amount_total()) != 0) {
                        return CheckResultUtil.NG(String.format("收款数据校验失败：请确保各账户收款金额之和与申请收款金额完全一致。申请收款总金额：%s，收款总金额：%s", 
                                searchCondition.getReceivable_amount_total(), searchCondition.getDetail_receivable_amount()));
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bArEntity = mapper.selectById(searchCondition.getId());
                if (bArEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_ZERO) && !Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，销售订单[%s]不是待审批,驳回状态,无法修改", bArEntity.getCode()));
                }

                if (searchCondition.getSoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个销售订单");
                }

                Map<String, Long> collect1 = searchCondition.getSoOrderListData()
                        .stream()
                        .map(BArSourceAdvanceVo::getSo_order_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result1 = new ArrayList<>();
                collect1.forEach((k,v)->{
                    if(v>1) result1.add(k);
                });

                if (result1!=null&&result1.size()>0){
                    return CheckResultUtil.NG("销售订单添加重复",result1);
                }

                if (searchCondition.getSoOrderListData().size()>1){
//                    return CheckResultUtil.NG("销售订单只能添加一个");
                }

                for (BArSourceAdvanceVo soOrderListDatum : searchCondition.getSoOrderListData()) {
                    if (soOrderListDatum.getOrder_amount()==null||soOrderListDatum.getOrder_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("请填写需要收款的金额，当前提交的收款金额不能为0！");
                    }
                }

                if (searchCondition.getBankListData().size()>1){
                    return CheckResultUtil.NG("银行账户信息添加一个");
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                for (BArDetailVo bArDetailVo : searchCondition.getBankListData()) {
                    if (bArDetailVo.getReceivable_amount() == null||bArDetailVo.getReceivable_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("校验出错：收款账户表格中存在收款金额为0的数据，请检查！");
                    }
                }

                // 校验收款总金额与申请收款总金额是否一致
                if (searchCondition.getDetail_receivable_amount() != null && searchCondition.getReceivable_amount_total() != null) {
                    if (searchCondition.getDetail_receivable_amount().compareTo(searchCondition.getReceivable_amount_total()) != 0) {
                        return CheckResultUtil.NG(String.format("收款数据校验失败：请确保各账户收款金额之和与申请收款金额完全一致。申请收款总金额：%s，收款总金额：%s", 
                                searchCondition.getReceivable_amount_total(), searchCondition.getDetail_receivable_amount()));
                    }
                }

                break;
            // 删除
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bArEntity = mapper.selectById(searchCondition.getId());
                if (bArEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_ZERO) && !Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，收款管理[%s]不是待审批,驳回状态,无法删除", bArEntity.getCode()));
                }

                List<BArReceiveVo> delBArReceiveVo = bArReceiveMapper.selectArReceiveByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AR_RECEIVE_STATUS_TWO);
                if (CollectionUtil.isNotEmpty(delBArReceiveVo)) {
                    return CheckResultUtil.NG("删除失败，存在收款单。");
                }
                break;
            // 作废
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bArEntity = mapper.selectById(searchCondition.getId());
                if (bArEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_FIVE) || Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，收款管理[%s]无法重复作废",bArEntity.getCode()));
                }
                if (!Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，收款管理[%s]审核中，无法作废",bArEntity.getCode()));
                }

                List<BArReceiveVo> cancelBArReceiveVo = bArReceiveMapper.selectArReceiveByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AR_RECEIVE_STATUS_TWO);
                if (CollectionUtil.isNotEmpty(cancelBArReceiveVo)) {
                    return CheckResultUtil.NG(String.format("作废失败，该应收账款下收款单号%s数据尚未作废，请先完成该收款单的作废。",cancelBArReceiveVo.stream().map(BArReceiveVo::getCode).collect(Collectors.toList())));
                }

                break;
            // 中止收款
            case CheckResultAo.STOP_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bArEntity = mapper.selectById(searchCondition.getId());
                if (bArEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (!Objects.equals(bArEntity.getStatus(), DictConstant.DICT_B_AR_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("中止失败，收款管理[%s]未审批通过",bArEntity.getCode()));
                }

                // 是否已中止
                if (Objects.equals(bArEntity.getReceive_status(), DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP)) {
                    return CheckResultUtil.NG(String.format("中止失败，收款管理[%s]无法重复中止收款",bArEntity.getCode()));
                }

                // 查询是否存在收款单，且状态是待收款
                List<BArReceiveVo> stopBArReceiveVo = bArReceiveMapper.selectArReceiveByStatus(searchCondition.getId(), DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO);
                if (CollectionUtil.isNotEmpty(stopBArReceiveVo)) {
                    return CheckResultUtil.NG("中止失败，该应收账款下，收款单“"+stopBArReceiveVo.stream().map(BArReceiveVo::getCode).collect(Collectors.toList())+"“状态为待收款，请先完成该收款单的处理。");
                }
                break;

            default:
        }
        return CheckResultUtil.OK();
    }

    @Override
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BArVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BArVo bArVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("业务类型:", bArVo.getType_name());
        jsonObject.put("申请收款总金额:", bArVo.getReceivable_amount());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bArVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 新增应收账款主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BArVo> insert(BArVo vo) {
        // 1. 计算汇总数据
        calculateSummaryData(vo);
        // 3. 保存主表信息
        BArEntity bArEntity = saveMainEntity(vo);
        // 4. 保存源单信息
        saveSourceEntity(vo, bArEntity);
        // 5. 保存源单-预收款信息
        saveSourceAdvanceEntity(vo, bArEntity);
        // 6. 保存银行账户明细
        saveDetailList(vo, bArEntity);
        // 7. 保存附件信息
        saveAttach(vo, bArEntity);
        // 8. 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByArId(bArEntity.getId());
        // 9. 设置返回ID
        vo.setId(bArEntity.getId());
        return InsertResultUtil.OK(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BArVo> searchCondition) {
        for (BArVo bArVo : searchCondition) {

            // 删除前check
            CheckResultAo cr = checkLogic(bArVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BArEntity bArEntity = mapper.selectById(bArVo.getId());
            bArEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bArEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            } else {
                // 计算应收账款管理表-财务数据汇总
//                bArTotalService.calcDeleteArAmount(bArEntity.getId());
            }
        }
        return DeleteResultUtil.OK(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BArVo> cancel(BArVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BArEntity bArEntity = mapper.selectById(searchCondition.getId());
        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_FOUR);
        bArEntity.setBpm_cancel_process_name("作废收款管理审批");

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bArEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        int insert = mapper.updateById(bArEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bArEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AR);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 汇总合计数据生成
        commonTotalService.reCalculateAllTotalDataByArId(bArEntity.getId());
        
        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AR_CANCEL);

        return UpdateResultUtil.OK(searchCondition);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> suspendReceive(BArVo searchCondition) {
        // 1. 业务校验
        validateSuspendReceive(searchCondition);
        
        // 2. 更新应收账款主表状态
        BArEntity bArEntity = updateArMainStatus(searchCondition);
        
        // 3. 处理中止收款附件
        processSuspendAttachment(bArEntity, searchCondition);
        
        // 4. 中止相关收款单（因为校验收款单状态！=作废、完成不能中止，所以下面的这个方法永远不会被执行）
//        suspendRelatedReceives(bArEntity.getId());
        
        // 5. 重新计算汇总数据
        recalculateArTotalData(bArEntity.getId());
        
        return UpdateResultUtil.OK(1);
    }

    @Override
    public BArVo querySum(BArVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    @Override
    public List<BArVo> selectExportList(BArVo param) {
        return mapper.selectExportList(param);
    }

    @Override
    public BArVo getPrintInfo(BArVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_B_AR);
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

    // 私有辅助方法
    private void checkInsertLogic(BArVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    private void checkUpdateLogic(BArVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    public UpdateResultAo<BArVo> update(BArVo searchCondition) {
        // 1. 计算汇总数据
        calculateSummaryData(searchCondition);
        // 3. 更新主表信息
        BArEntity bArEntity = updateMainEntity(searchCondition);
        // 4. 更新源单信息
        updateSourceEntity(searchCondition, bArEntity);
        // 5. 更新源单-预收款信息
        updateSourceAdvanceEntity(searchCondition, bArEntity);
        // 6. 更新银行账户明细
        updateDetailList(searchCondition, bArEntity);
        // 7. 更新附件信息
        updateAttach(searchCondition, bArEntity);
        // 8. 先删除再汇总财务数据
//        bArTotalService.calcDeleteArAmount(bArEntity.getId());
//        bArTotalService.calcNewArAmount(bArEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    private BArEntity saveMainEntity(BArVo searchCondition) {
        BArEntity bArEntity = new BArEntity();
        BeanUtils.copyProperties(searchCondition, bArEntity);
        bArEntity.setCode(bArAutoCodeService.autoCode().getCode());
        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_ONE);
        bArEntity.setReceive_status(DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO);
        bArEntity.setIs_del(Boolean.FALSE);
        bArEntity.setBpm_process_name("新增收款管理审批");
        bArEntity.setId(null);
        int bAr = mapper.insert(bArEntity);
        if (bAr <= 0) {
            throw new UpdateErrorException("新增失败");
        }
        return bArEntity;
    }

    private void saveSourceEntity(BArVo searchCondition, BArEntity bArEntity) {
        BArSourceEntity bArSourceEntity = new BArSourceEntity();
        bArSourceEntity.setAr_id(bArEntity.getId());
        bArSourceEntity.setAr_code(bArEntity.getCode());
        bArSourceEntity.setType(bArEntity.getType());
        bArSourceEntity.setSo_order_code(bArEntity.getSo_order_code());
        bArSourceEntity.setSo_order_id(bArEntity.getSo_order_id());
        bArSourceEntity.setSo_contract_id(bArEntity.getSo_contract_id());
        bArSourceEntity.setSo_contract_code(bArEntity.getSo_contract_code());
        bArSourceEntity.setProject_code(bArEntity.getProject_code());
        int bArSource = bArSourceMapper.insert(bArSourceEntity);
        if (bArSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    private void saveSourceAdvanceEntity(BArVo searchCondition, BArEntity bArEntity) {
        for (BArSourceAdvanceVo bArSourceAdvanceVo : searchCondition.getSoOrderListData()) {
            BArSourceAdvanceEntity bArSourceAdvanceEntity = new BArSourceAdvanceEntity();
            BeanUtils.copyProperties(bArSourceAdvanceVo, bArSourceAdvanceEntity);
            bArSourceAdvanceEntity.setCode(bArSourceAdvanceAutoCodeService.autoCode().getCode());
            bArSourceAdvanceEntity.setAr_id(bArEntity.getId());
            bArSourceAdvanceEntity.setAr_code(bArEntity.getCode());
            bArSourceAdvanceEntity.setType(bArEntity.getType());
            bArSourceAdvanceEntity.setId(null);

            // 初始化收款相关金额字段
            bArSourceAdvanceEntity.setReceivable_amount_total(bArSourceAdvanceVo.getOrder_amount());
            bArSourceAdvanceEntity.setReceived_amount_total(BigDecimal.ZERO);
            bArSourceAdvanceEntity.setReceiving_amount_total(BigDecimal.ZERO);
            bArSourceAdvanceEntity.setUnreceive_amount_total(bArSourceAdvanceEntity.getOrder_amount());

            int bArSourceAdvance = bArSourceAdvanceMapper.insert(bArSourceAdvanceEntity);
            if (bArSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    private void saveDetailList(BArVo searchCondition, BArEntity bArEntity) {
        for (BArDetailVo bArDetailVo : searchCondition.getBankListData()) {
            BArDetailEntity bArDetailEntity = new BArDetailEntity();
            BeanUtils.copyProperties(bArDetailVo, bArDetailEntity);
            bArDetailEntity.setCode(bArDetailAutoCodeService.autoCode().getCode());
            bArDetailEntity.setAr_id(bArEntity.getId());
            bArDetailEntity.setAr_code(bArEntity.getCode());
            bArDetailEntity.setReceived_amount(BigDecimal.ZERO);
            int bArDetail = bArDetailMapper.insert(bArDetailEntity);
            if (bArDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    private void saveAttach(BArVo searchCondition, BArEntity bArEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bArEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR);
        BArAttachEntity bArAttachEntity = insertFile(fileEntity, searchCondition, new BArAttachEntity());
        bArAttachEntity.setAr_id(bArEntity.getId());
        int attachInsert = bArAttachMapper.insert(bArAttachEntity);
        if (attachInsert <= 0) {
            throw new UpdateErrorException("新增附件信息失败");
        }
    }

    /**
     * 更新主表信息
     */
    private BArEntity updateMainEntity(BArVo searchCondition) {
        BArEntity bArEntity = (BArEntity) BeanUtilsSupport.copyProperties(searchCondition, BArEntity.class);
        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_ONE);
        bArEntity.setIs_del(Boolean.FALSE);
        bArEntity.setBpm_process_name("修改收款管理审批");
        int bAr = mapper.updateById(bArEntity);
        if (bAr <= 0) {
            throw new UpdateErrorException("新增失败");
        }
        return bArEntity;
    }

    /**
     * 更新源单信息
     */
    private void updateSourceEntity(BArVo searchCondition, BArEntity bArEntity) {
        bArSourceMapper.deleteByArId(bArEntity.getId());
        BArSourceEntity bArSourceEntity = new BArSourceEntity();
        bArSourceEntity.setAr_id(bArEntity.getId());
        bArSourceEntity.setAr_code(bArEntity.getCode());
        bArSourceEntity.setType(bArEntity.getType());
        bArSourceEntity.setSo_order_id(bArEntity.getSo_order_id());
        bArSourceEntity.setSo_order_code(bArEntity.getSo_order_code());
        bArSourceEntity.setSo_contract_code(bArEntity.getSo_contract_code());
        bArSourceEntity.setProject_code(bArEntity.getProject_code());
        int bArSource = bArSourceMapper.insert(bArSourceEntity);
        if (bArSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

    /**
     * 更新源单-预收款信息
     */
    private void updateSourceAdvanceEntity(BArVo searchCondition, BArEntity bArEntity) {
        bArSourceAdvanceMapper.deleteByArId(bArEntity.getId());
        for (BArSourceAdvanceVo bArSourceAdvanceVo : searchCondition.getSoOrderListData()) {
            BArSourceAdvanceEntity bArSourceAdvanceEntity = new BArSourceAdvanceEntity();
            BeanUtils.copyProperties(bArSourceAdvanceVo, bArSourceAdvanceEntity);
            bArSourceAdvanceEntity.setCode(bArSourceAdvanceAutoCodeService.autoCode().getCode());
            bArSourceAdvanceEntity.setAr_id(bArEntity.getId());
            bArSourceAdvanceEntity.setAr_code(bArEntity.getCode());
            bArSourceAdvanceEntity.setType(bArEntity.getType());
            int bArSourceAdvance = bArSourceAdvanceMapper.insert(bArSourceAdvanceEntity);
            if (bArSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 更新银行账户明细
     */
    private void updateDetailList(BArVo searchCondition, BArEntity bArEntity) {
        bArDetailMapper.deleteByArId(bArEntity.getId());
        for (BArDetailVo bArDetailVo : searchCondition.getBankListData()) {
            BArDetailEntity bArDetailEntity = new BArDetailEntity();
            BeanUtils.copyProperties(bArDetailVo, bArDetailEntity);
            bArDetailEntity.setCode(bArDetailAutoCodeService.autoCode().getCode());
            bArDetailEntity.setAr_id(bArEntity.getId());
            bArDetailEntity.setAr_code(bArEntity.getCode());
            bArDetailEntity.setReceived_amount(BigDecimal.ZERO);
            int bArDetail = bArDetailMapper.insert(bArDetailEntity);
            if (bArDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BArVo searchCondition, BArEntity bArEntity) {
        BArAttachVo bArAttachVo = bArAttachMapper.selectByArId(bArEntity.getId());
        if (bArAttachVo != null) {
            // 更新附件信息
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bArEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR);
            BArAttachEntity bArAttachEntity = (BArAttachEntity) BeanUtilsSupport.copyProperties(bArAttachVo, BArAttachEntity.class);
            insertFile(fileEntity, searchCondition, bArAttachEntity);
            bArAttachEntity.setAr_id(bArEntity.getId());
            int attachUpdate = bArAttachMapper.updateById(bArAttachEntity);
            if (attachUpdate <= 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            saveAttach(searchCondition, bArEntity);
        }
    }

    /**
     * 审批流程 审批流程通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BArVo searchCondition) {
        log.debug("====》收款管理[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bArEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_TWO);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》收款管理[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BArVo searchCondition) {
        log.debug("====》收款管理[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_THREE);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》收款管理[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程 审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BArVo searchCondition) {
        log.debug("====》收款管理[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_ZERO);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》收款管理[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     *  审批流程 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BArVo searchCondition) {
        log.debug("====》收款管理[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bArEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bArEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bArEntity);

        log.debug("====》收款管理[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程 审批流程通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BArVo vo) {
        log.debug("====》收款管理[{}]作废审批流程通过，更新开始《====",vo.getId());
        BArEntity bArEntity = mapper.selectById(vo.getId());

        bArEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bArEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_FIVE);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 更新b_ar_source_advance表的作废金额
        updateSourceAdvanceCancelAmount(vo.getId());

        // 调用共通，更新total
        commonTotalService.reCalculateAllTotalDataByArId(bArEntity.getId());

        log.debug("====》收款管理[{}]作废审批流程通过,更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 更新预收款源单的作废金额
     * 当应收账款作废审批通过时，将该应收账款下所有预收款源单的作废金额设置为申请金额
     * 
     * @param arId 应收账款ID
     */
    private void updateSourceAdvanceCancelAmount(Integer arId) {
        log.debug("====》开始更新应收账款[{}]下预收款源单的作废金额《====", arId);
        
        // 查询该应收账款下的所有预收款源单记录
        List<BArSourceAdvanceVo> sourceAdvanceList = bArSourceAdvanceMapper.selectByArId(arId);
        if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
            log.debug("应收账款[{}]下没有预收款源单记录，跳过更新", arId);
            return;
        }
        
        // 更新每条预收款源单记录的作废金额
        for (BArSourceAdvanceVo sourceAdvanceVo : sourceAdvanceList) {
            BArSourceAdvanceEntity entity = new BArSourceAdvanceEntity();
            // 复制VO到Entity的所有字段值
            BeanUtils.copyProperties(sourceAdvanceVo, entity);
            // 设置作废金额为申请金额
            entity.setCancelreceive_amount_total(sourceAdvanceVo.getOrder_amount());
            
            int updateResult = bArSourceAdvanceMapper.updateById(entity);
            if (updateResult <= 0) {
                log.error("更新预收款源单[{}]作废金额失败", sourceAdvanceVo.getId());
                throw new UpdateErrorException("更新预收款源单作废金额失败");
            }
            
            log.debug("更新预收款源单[{}]作废金额成功，金额: {}", 
                sourceAdvanceVo.getId(), sourceAdvanceVo.getOrder_amount());
        }
        
        log.debug("====》完成更新应收账款[{}]下预收款源单的作废金额，共处理{}条记录《====", 
            arId, sourceAdvanceList.size());
    }

    /**
     * 作废审批流程 作废审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BArVo searchCondition) {
        log.debug("====》收款管理[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_TWO);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AR);
        mCancelService.delete(mCancelVo);

        log.debug("====》收款管理[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程 作废审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BArVo searchCondition) {
        log.debug("====》收款管理[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setStatus(DictConstant.DICT_B_AR_STATUS_TWO);
        bArEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bArEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AR);
        mCancelService.delete(mCancelVo);

        log.debug("====》收款管理[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BArVo searchCondition) {
        log.debug("====》收款管理[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BArEntity bArEntity = mapper.selectById(searchCondition.getId());

        bArEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bArEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bArEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bArEntity);

        log.debug("====》收款管理[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程创建成功后的回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BArVo searchCondition){
        log.debug("====》收款管理[{}]作废审批流程创建成功，更新开始《====",searchCondition.getId());
        BArVo bArVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("业务类型:", bArVo.getType_name());
        jsonObject.put("申请收款总金额:", bArVo.getReceivable_amount());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bArVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        log.debug("====》收款管理[{}]作废审批流程创建成功，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }

    /**
     * 文件处理逻辑
     */
    public BArAttachEntity insertFile(SFileEntity fileEntity, BArVo vo, BArAttachEntity extra) {
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
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BArVo vo) {
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
    public SFileEntity insertStopFile(SFileEntity fileEntity, BArVo vo) {
        // 停止附件新增
        if (vo.getStopreceive_files() != null && vo.getStopreceive_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo stop_file : vo.getStopreceive_files()) {
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
    public void startFlowProcess(BArVo bean,String type){
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

// ==================== 中止收款业务方法拆分 ====================

    /**
     * 验证中止收款业务逻辑
     * @param searchCondition 应收账款信息
     */
    private void validateSuspendReceive(BArVo searchCondition) {
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.STOP_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }


    /**
     * 处理中止收款附件信息
     * @param bArEntity 应收账款实体
     * @param searchCondition 应收账款信息
     */
    private void processSuspendAttachment(BArEntity bArEntity, BArVo searchCondition) {
        if (searchCondition.getStopreceive_files() != null && !searchCondition.getStopreceive_files().isEmpty()) {
            // 保存中止收款附件
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(bArEntity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AR);
            insertStopFile(fileEntity, searchCondition);

            // 更新应收账款表的附件ID字段
            bArEntity.setStopReceive_file(fileEntity.getId());
            int updateResult = mapper.updateById(bArEntity);
            if (updateResult <= 0) {
                throw new UpdateErrorException("更新中止收款附件ID失败");
            }

            log.debug("保存应收账款[{}]中止收款附件成功，附件ID: {}", bArEntity.getId(), fileEntity.getId());
        }
    }

    /**
     * 中止相关收款单
     * @param arId 应收账款ID
     */
    private void suspendRelatedReceives(Integer arId) {
        // 查询待收款状态的收款单
        List<BArReceiveVo> pendingReceives = bArReceiveMapper.selectArReceiveByStatus(arId, DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO);
        
        if (pendingReceives != null && !pendingReceives.isEmpty()) {
            for (BArReceiveVo receiveVo : pendingReceives) {
                // 更新收款单状态为中止
                updateReceiveStatusToSuspend(receiveVo);
                // 更新收款单明细状态为中止
                updateReceiveDetailsToSuspend(receiveVo.getId());
            }
            log.debug("中止应收账款[{}]下的{}个收款单", arId, pendingReceives.size());
        }
    }

    /**
     * 更新收款单状态为中止
     * @param receiveVo 收款单信息
     */
    private void updateReceiveStatusToSuspend(BArReceiveVo receiveVo) {
        receiveVo.setReceive_status(DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP);
        BArReceiveEntity bArReceiveEntity = (BArReceiveEntity) BeanUtilsSupport.copyProperties(receiveVo, BArReceiveEntity.class);
        
        int updateResult = bArReceiveMapper.updateById(bArReceiveEntity);
        if (updateResult <= 0) {
            throw new UpdateErrorException("收款单状态更新失败");
        }
    }

    /**
     * 更新收款单明细状态为中止
     * @param receiveId 收款单ID
     */
    private void updateReceiveDetailsToSuspend(Integer receiveId) {
        List<BArReceiveDetailVo> bArReceiveDetailVos = bArReceiveDetailMapper.selectById(receiveId);
        
        if (CollectionUtil.isNotEmpty(bArReceiveDetailVos)) {
            for (BArReceiveDetailVo detailVo : bArReceiveDetailVos) {
                // 设置中止收款相关金额字段
                detailVo.setReceiving_amount(BigDecimal.ZERO);                // 收款中金额清零
                detailVo.setUnreceive_amount(BigDecimal.ZERO);                // 未收款金额清零
                detailVo.setCancel_amount(BigDecimal.ZERO);                   // 作废金额清零
                
                BArReceiveDetailEntity bArReceiveDetailEntity = (BArReceiveDetailEntity) BeanUtilsSupport.copyProperties(detailVo, BArReceiveDetailEntity.class);
                int updateResult = bArReceiveDetailMapper.updateById(bArReceiveDetailEntity);
                if (updateResult <= 0) {
                    throw new UpdateErrorException("收款单明细更新失败");
                }
            }
        }
    }

    /**
     * 重新计算应收账款汇总数据
     * @param arId 应收账款ID
     */
    private void recalculateArTotalData(Integer arId) {
        try {
            commonTotalService.reCalculateAllTotalDataByArId(arId);
            log.debug("重新计算应收账款[{}]汇总数据成功", arId);
        } catch (Exception e) {
            log.error("重新计算应收账款[{}]汇总数据失败", arId, e);
            throw new UpdateErrorException("重新计算汇总数据失败");
        }
    }


    /**
     * 更新应收账款主表状态为中止收款
     * @param vo 应收账款信息
     * @return 更新后的应收账款实体
     */
    private BArEntity updateArMainStatus(BArVo vo) {
        BArEntity bArEntity = mapper.selectById(vo.getId());
        bArEntity.setReceive_status(DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP);
        bArEntity.setStopReceive_reason(vo.getStopReceive_reason());
        bArEntity.setStopReceive_u_id(SecurityUtil.getStaff_id().intValue());
        bArEntity.setStopReceive_u_time(LocalDateTime.now());
        int updateResult = mapper.updateById(bArEntity);
        if (updateResult == 0) {
            throw new UpdateErrorException("应收账款主表状态更新失败");
        }
        
        return bArEntity;
    }
}