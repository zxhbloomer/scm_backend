package com.xinyirun.scm.core.system.serviceimpl.business.aprefund;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApRefundSourceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayEntity;
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
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundPayVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
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
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefund.BApReFundSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.aprefundpay.BApRefundPayMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.aprefund.IBApReFundService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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

    /**
     * 获取业务类型
     */
    @Override
    public List<BApReFundVo> getType() {
        return mapper.getType();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundVo> startInsert(BApReFundVo searchCondition) {
        // 1.保存
        InsertResultAo<BApReFundVo> insertResultAo = insert(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND);

        return insertResultAo;
    }

    /**
     * 更新
     *
     * @param searchCondition
     */
    @Override
    public UpdateResultAo<BApReFundVo> startUpdate(BApReFundVo searchCondition) {
        // 1.保存采购合同
        UpdateResultAo<BApReFundVo> insertResultAo = update(searchCondition);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND);

        return insertResultAo;
    }

    /**
     * 分页查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<BApReFundVo> selectPage(BApReFundVo searchCondition) {
        // 分页条件
        Page<BApReFundVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 根据id查询
     *
     * @param id
     */
    @Override
    public BApReFundVo selectById(Integer id) {
        return mapper.selectId(id);
    }

    /**
     * 校验
     *
     * @param searchCondition
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BApReFundVo searchCondition, String checkType) {
        BApReFundEntity bApEntity = null;

        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getPoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个采购订单");
                }

                Map<String, Long> collect = searchCondition.getPoOrderListData()
                        .stream()
                        .map(BApReFundSourceAdvanceVo::getPo_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("采购订单添加重复",result);
                }

                if (searchCondition.getPoOrderListData().size()>1){
                    return CheckResultUtil.NG("采购订单只能添加一个");
                }

                for (BApReFundSourceAdvanceVo poOrderListDatum : searchCondition.getPoOrderListData()) {
                    if (poOrderListDatum.getRefund_amount()==null||poOrderListDatum.getRefund_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("采购订单计划金额不能为空");
                    }
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                if (searchCondition.getBankListData().size()>1){
                    return CheckResultUtil.NG("银行账户信息添加一个");
                }

                for (BApReFundDetailVo bApDetailVo : searchCondition.getBankListData()) {
                    if (bApDetailVo.getRefunded_amount() == null||bApDetailVo.getRefunded_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("银行账户申请金额不能为空");
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
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，采购订单[%s]不是待审批,驳回状态,无法修改", bApEntity.getCode()));
                }

                if (searchCondition.getPoOrderListData()==null){
                    return CheckResultUtil.NG("至少添加一个采购订单");
                }

                Map<String, Long> collect1 = searchCondition.getPoOrderListData()
                        .stream()
                        .map(BApReFundSourceAdvanceVo::getPo_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result1 = new ArrayList<>();
                collect1.forEach((k,v)->{
                    if(v>1) result1.add(k);
                });

                if (result1!=null&&result1.size()>0){
                    return CheckResultUtil.NG("采购订单添加重复",result1);
                }

                if (searchCondition.getPoOrderListData().size()>1){
                    return CheckResultUtil.NG("采购订单只能添加一个");
                }

                for (BApReFundSourceAdvanceVo poOrderListDatum : searchCondition.getPoOrderListData()) {
                    if (poOrderListDatum.getRefund_amount()==null||poOrderListDatum.getRefund_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("采购订单计划金额不能为空");
                    }
                }

                if (searchCondition.getBankListData().size()>1){
                    return CheckResultUtil.NG("银行账户信息添加一个");
                }

                if (searchCondition.getBankListData()==null){
                    return CheckResultUtil.NG("请添加银行账户信息");
                }

                for (BApReFundDetailVo bApDetailVo : searchCondition.getBankListData()) {
                    if (bApDetailVo.getRefunded_amount() == null||bApDetailVo.getRefunded_amount().compareTo(BigDecimal.ZERO)<=0){
                        return CheckResultUtil.NG("银行账户申请金额不能为空");
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
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_ZERO) && !Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，退款管理[%s]不是待审批,驳回状态,无法删除", bApEntity.getCode()));
                }

                List<BApReFundPayVo> delBApPayVo = bApRefundPayMapper.selectApPayByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AP_PAY_BILL_STATUS_TWO);
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
                if (Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_FIVE) || Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，退款管理[%s]无法重复作废",bApEntity.getCode()));
                }
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，退款管理[%s]审核中，无法作废",bApEntity.getCode()));
                }

                List<BApReFundPayVo> cancelBApPayVo = bApRefundPayMapper.selectApPayByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_THREE);
                if (CollectionUtil.isNotEmpty(cancelBApPayVo)) {
                    return CheckResultUtil.NG(String.format("作废失败，退款管理%s数据未作废，请先完成该付款单的作废。",cancelBApPayVo.stream().map(BApReFundPayVo::getCode).collect(Collectors.toList())));
                }
                break;
            // 中止退款
            case CheckResultAo.STOP_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bApEntity = mapper.selectById(searchCondition.getId());
                if (bApEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (!Objects.equals(bApEntity.getStatus(), DictConstant.DICT_B_AP_REFUND_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("中止失败，退款管理[%s]未审批通过",bApEntity.getCode()));
                }

                // 是否已付款
                if (!Objects.equals(bApEntity.getPay_status(), DictConstant.DICT_B_AP_REFUND_PAY_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("中止失败，退款管理[%s]已付款",bApEntity.getCode()));
                }

                // 是否已中止
                if (!Objects.equals(bApEntity.getPay_status(), DictConstant.DICT_B_AP_REFUND_PAY_STATUS_STOP)) {
                    return CheckResultUtil.NG(String.format("中止失败，退款管理[%s]无法重复中止付款",bApEntity.getCode()));
                }
                /*List<BApReFundPayVo> stopBApPayVo = bApRefundPayMapper.selectApPayByNotStatus(searchCondition.getId(), DictConstant.DICT_B_AP_PAY_BILL_STATUS_ONE);
                if (CollectionUtil.isNotEmpty(stopBApPayVo)) {
                    return CheckResultUtil.NG("中止失败，付款单号"+stopBApPayVo.stream().map(BApReFundPayVo::getCode).collect(Collectors.toList())+"数据付款中，请先完成该退款单的付款或作废。");
                }*/
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
        jsonObject.put("申请退款总金额:", bApReFundVo.getRefund_amount());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bApReFundVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 退款管理  新增
     * @param searchCondition
     */
    public InsertResultAo<BApReFundVo> insert(BApReFundVo searchCondition) {

        // 插入前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.保存基础信息
        BApReFundEntity bApReFundEntity = new BApReFundEntity();
        BeanUtils.copyProperties(searchCondition, bApReFundEntity);
        bApReFundEntity.setCode(bApReFundAutoCodeService.autoCode().getCode());
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ONE);
        bApReFundEntity.setPay_status(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_ZERO);
        bApReFundEntity.setRefunding_amount(BigDecimal.ZERO);
        bApReFundEntity.setRefunded_amount(BigDecimal.ZERO);

        /** 未删除 */
        bApReFundEntity.setIs_del(Boolean.FALSE);

        /** 审批流程名称 */
        bApReFundEntity.setBpm_process_name("新增应付退款管理审批");


        int bAp = mapper.insert(bApReFundEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.保存预付退款业务表数据
        BApRefundSourceEntity bApSourceEntity = new BApRefundSourceEntity();
        bApSourceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApSourceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApSourceEntity.setType(bApReFundEntity.getType());
        bApSourceEntity.setPo_code(bApReFundEntity.getPo_code());
        bApSourceEntity.setPo_contract_code(bApReFundEntity.getPo_contract_code());
        bApSourceEntity.setProject_code(bApReFundEntity.getProject_code());

        int bApSource = bApReFundSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 保存商品数据
        for (BApReFundSourceAdvanceVo bApSourceAdvanceVo : searchCondition.getPoOrderListData()) {
            BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = new BApReFundSourceAdvanceEntity();
            BeanUtils.copyProperties(bApSourceAdvanceVo, bApReFundSourceAdvanceEntity);
            bApReFundSourceAdvanceEntity.setCode(bApReFundSourceAdvanceAutoCodeService.autoCode().getCode());
            bApReFundSourceAdvanceEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundSourceAdvanceEntity.setAp_refund_code(bApReFundEntity.getCode());
            bApReFundSourceAdvanceEntity.setType(bApReFundEntity.getType());

            int bApSourceAdvance = bApReFundSourceAdvanceMapper.insert(bApReFundSourceAdvanceEntity);
            if (bApSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }

        // 3.保存银行账户信息
        for (BApReFundDetailVo bApDetailVo : searchCondition.getBankListData()) {
            BApReFundDetailEntity bApReFundDetailEntity = new BApReFundDetailEntity();
            BeanUtils.copyProperties(bApDetailVo, bApReFundDetailEntity);
            bApReFundDetailEntity.setCode(bApReFundDetailAutoCodeService.autoCode().getCode());
            bApReFundDetailEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundDetailEntity.setAp_refund_code(bApReFundEntity.getCode());
            bApReFundDetailEntity.setRefunded_amount(BigDecimal.ZERO);

            int bApDetail = bApReFundDetailMapper.insert(bApReFundDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }

        searchCondition.setId(bApReFundEntity.getId());
        return InsertResultUtil.OK(searchCondition);
    }

    /**
     * 修改
     * @param searchCondition
     */
    public UpdateResultAo<BApReFundVo> update(BApReFundVo searchCondition) {

        // 插入前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.保存基础信息
        BApReFundEntity bApReFundEntity = (BApReFundEntity) BeanUtilsSupport.copyProperties(searchCondition, BApReFundEntity.class);
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ONE);

        /** 未删除 */
        bApReFundEntity.setIs_del(Boolean.FALSE);

        /** 审批流程名称 */
        bApReFundEntity.setBpm_process_name("修改应付退款管理审批");

        int bAp = mapper.updateById(bApReFundEntity);
        if (bAp <= 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.保存预付款业务表数据
        bApReFundSourceMapper.delete(new LambdaQueryWrapper<BApRefundSourceEntity>()
                .eq(BApRefundSourceEntity :: getAp_refund_id, bApReFundEntity.getId()));
        BApRefundSourceEntity bApSourceEntity = new BApRefundSourceEntity();
        bApSourceEntity.setAp_refund_id(bApReFundEntity.getId());
        bApSourceEntity.setAp_refund_code(bApReFundEntity.getCode());
        bApSourceEntity.setType(bApReFundEntity.getType());
        bApSourceEntity.setPo_code(bApReFundEntity.getPo_code());
        bApSourceEntity.setPo_contract_code(bApReFundEntity.getPo_contract_code());
        bApSourceEntity.setProject_code(bApReFundEntity.getProject_code());

        int bApSource = bApReFundSourceMapper.insert(bApSourceEntity);
        if (bApSource <= 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 保存商品数据
        bApReFundSourceAdvanceMapper.delete(new LambdaQueryWrapper<BApReFundSourceAdvanceEntity>()
                .eq(BApReFundSourceAdvanceEntity :: getAp_refund_id, bApReFundEntity.getId()));
        for (BApReFundSourceAdvanceVo bApSourceAdvanceVo : searchCondition.getPoOrderListData()) {
            BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = new BApReFundSourceAdvanceEntity();
            BeanUtils.copyProperties(bApSourceAdvanceVo, bApReFundSourceAdvanceEntity);
            bApReFundSourceAdvanceEntity.setCode(bApReFundSourceAdvanceAutoCodeService.autoCode().getCode());
            bApReFundSourceAdvanceEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundSourceAdvanceEntity.setAp_refund_code(bApReFundEntity.getCode());
            bApReFundSourceAdvanceEntity.setType(bApReFundEntity.getType());

            int bApSourceAdvance = bApReFundSourceAdvanceMapper.insert(bApReFundSourceAdvanceEntity);
            if (bApSourceAdvance <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }

        // 3.保存银行账户信息
        bApReFundDetailMapper.delete(new LambdaQueryWrapper<BApReFundDetailEntity>()
                .eq(BApReFundDetailEntity :: getAp_refund_id, bApReFundEntity.getId()));
        for (BApReFundDetailVo bApDetailVo : searchCondition.getBankListData()) {
            BApReFundDetailEntity bApReFundDetailEntity = new BApReFundDetailEntity();
            BeanUtils.copyProperties(bApDetailVo, bApReFundDetailEntity);
            bApReFundDetailEntity.setCode(bApReFundDetailAutoCodeService.autoCode().getCode());
            bApReFundDetailEntity.setAp_refund_id(bApReFundEntity.getId());
            bApReFundDetailEntity.setAp_refund_code(bApReFundEntity.getCode());
            bApReFundDetailEntity.setRefunded_amount(BigDecimal.ZERO);

            int bApDetail = bApReFundDetailMapper.insert(bApReFundDetailEntity);
            if (bApDetail <= 0) {
                throw new UpdateErrorException("新增失败");
            }
        }

        searchCondition.setId(bApReFundEntity.getId());
        return UpdateResultUtil.OK(searchCondition);
    }

    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_TWO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程通过 审批流程拒绝
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_THREE);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }


    /**
     * 审批流程通过 审批流程撤销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_ZERO);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bApReFundEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bApReFundEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bApReFundEntity);

        log.debug("====》付款管理[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BApReFundVo searchCondition) {
        log.debug("====》付款管理[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());

        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_FIVE);
        bApReFundEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bApReFundEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》付款管理[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
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
     * @param searchCondition
     */
    @Override
    public BApReFundVo getPrintInfo(BApReFundVo searchCondition) {
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
    public DeleteResultAo<Integer> delete(List<BApReFundVo> searchCondition) {
        for (BApReFundVo bApReFundVo : searchCondition) {

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
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundVo> cancel(BApReFundVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());
        bApReFundEntity.setStatus(DictConstant.DICT_B_AP_REFUND_STATUS_FOUR);
        bApReFundEntity.setBpm_cancel_process_name("作废应付退款管理审批");

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
        fileEntity = insertFile(fileEntity, searchCondition);

        int insert = mapper.updateById(bApReFundEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bApReFundEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_AP_REFUND);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_AP_REFUND_CANCEL);

        return UpdateResultUtil.OK(searchCondition);
    }


    /**
     * 中止付款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> suspendPayment(BApReFundVo searchCondition) {

        // 插入前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.STOP_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.更新付款状态 （更新计划总金额 = 实付总金额）
        BApReFundEntity bApReFundEntity = mapper.selectById(searchCondition.getId());
        bApReFundEntity.setPay_status(DictConstant.DICT_B_AP_REFUND_PAY_STATUS_STOP);
        bApReFundEntity.setRefund_amount(bApReFundEntity.getRefunded_amount());
        bApReFundEntity.setRefunding_amount(BigDecimal.ZERO);

        // 保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bApReFundEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_AP_REFUND);
        fileEntity = insertFile(fileEntity, searchCondition);

        bApReFundEntity.setStop_file(fileEntity.getId());
        bApReFundEntity.setStop_reason(searchCondition.getStop_reason());
        int insert = mapper.updateById(bApReFundEntity);
        if (insert == 0) {
            throw new UpdateErrorException("修改失败");
        }

        // 2.应付退款关联单据表-源单-预收款
        BApReFundSourceAdvanceEntity bApReFundSourceAdvanceEntity = bApReFundSourceAdvanceMapper.selectByApRefundId(bApReFundEntity.getId());
        bApReFundSourceAdvanceEntity.setRefunding_amount(BigDecimal.ZERO);

        // 3.查询待付款 中止付款单（退款中止 不更新资金使用情况表）
        List<BApReFundPayEntity> bApPayVos = bApRefundPayMapper.selectApPayByStatus(bApReFundEntity.getId(), DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_ONE);
        if (CollectionUtil.isNotEmpty(bApPayVos)) {
            for (BApReFundPayEntity bApPayEntity : bApPayVos) {
                bApPayEntity.setStatus(DictConstant.DICT_B_AP_REFUND_PAY_ONE_STATUS_STOP);
                int bApPay = bApRefundPayMapper.updateById(bApPayEntity);
                if (bApPay <= 0) {
                    throw new UpdateErrorException("修改付款单失败");
                }
            }
        }

        return UpdateResultUtil.OK(1);
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
     * 附件逻辑 全删全增
     */
    public SFileEntity insertFile(SFileEntity fileEntity, BApReFundVo vo) {
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
}
