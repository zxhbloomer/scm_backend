package com.xinyirun.scm.core.system.serviceimpl.business.poorder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderAttachEntity;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderAttachVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
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
import com.xinyirun.scm.core.system.mapper.business.ap.BApMapper;
import com.xinyirun.scm.core.system.mapper.business.pocontract.BPoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.poorder.IBPoOrderTotalService;
import com.xinyirun.scm.core.system.service.business.poorder.IBPoOrderService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BPoOrderAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 采购订单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Slf4j
@Service
public class BPoOrderServiceImpl extends ServiceImpl<BPoOrderMapper, BPoOrderEntity> implements IBPoOrderService {

    @Autowired
    private BPoOrderMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BPoContractMapper bPoContractMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BPoOrderAttachMapper bPoOrderAttachMapper;

    @Autowired
    private BPoOrderDetailMapper bPoOrderDetailMapper;

    @Autowired
    private BPoOrderAutoCodeServiceImpl bPoOrderAutoCodeService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private BApMapper bApMapper;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private IBPoOrderTotalService iBPoOrderFinService;

    @Autowired
    private ICommonPoTotalService iCommonPoTotalService;

    /**
     * 获取采购订单信息
     * @param id
     */
    @Override
    public PoOrderVo selectById(Integer id) {
        PoOrderVo poOrderVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(poOrderVo.getDoc_att_file());
        poOrderVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_PO_CONTRACT_STATUS_FOUR.equals(poOrderVo.getStatus()) || Objects.equals(poOrderVo.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(poOrderVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_ORDER);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            poOrderVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                poOrderVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            poOrderVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            poOrderVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (poOrderVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(poOrderVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            poOrderVo.setProject(bProjectVo);
        }

        // 添加合同信息
        if (poOrderVo.getPo_contract_id() != null) {
            PoContractVo poContractVo = bPoContractMapper.selectId(poOrderVo.getPo_contract_id());
            List<SFileInfoVo> contract_doc_att_files = isFileService.selectFileInfo(poContractVo.getDoc_att_file());
            poContractVo.setDoc_att_files(contract_doc_att_files);
            poOrderVo.setPo_contract(poContractVo);
        }

        return poOrderVo;
    }

    /**
     * 采购订单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<PoOrderVo> startInsert(PoOrderVo searchCondition) {
        // 1. 校验业务规则
        checkInsertLogic(searchCondition);
        
        // 2.保存采购订单
        InsertResultAo<PoOrderVo> insertResultAo = insert(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER);
        return insertResultAo;
    }

    /**
     * 新增采购订单主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<PoOrderVo> insert(PoOrderVo poOrderVo) {
        // 1. 保存主表信息
        BPoOrderEntity bPoOrderEntity = saveMainEntity(poOrderVo);
        // 2. 保存明细信息
        saveDetailList(poOrderVo, bPoOrderEntity);
        // 3. 保存附件信息
        saveAttach(poOrderVo, bPoOrderEntity);
        // 4. 设置返回ID
        poOrderVo.setId(bPoOrderEntity.getId());
        // 5. 更新订单财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByPoOrderId(bPoOrderEntity.getId());
        return InsertResultUtil.OK(poOrderVo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(PoOrderVo poOrderVo) {
        CheckResultAo cr = checkLogic(poOrderVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BPoOrderEntity saveMainEntity(PoOrderVo poOrderVo) {
        BPoOrderEntity bPoOrderEntity = new BPoOrderEntity();
        BeanUtils.copyProperties(poOrderVo, bPoOrderEntity);
        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_ONE);
        bPoOrderEntity.setCode(bPoOrderAutoCodeService.autoCode().getCode());
        bPoOrderEntity.setIs_del(Boolean.FALSE);
        bPoOrderEntity.setBpm_process_name("新增采购订单审批");
//        calculatePoorderAmounts(poOrderVo.getDetailListData(), bPoOrderEntity);
        int bPurOrder = mapper.insert(bPoOrderEntity);
        if (bPurOrder == 0){
            throw new BusinessException("新增失败");
        }
        return bPoOrderEntity;
    }

    /**
     * 保存明细信息
     */
    private void saveDetailList(PoOrderVo poOrderVo, BPoOrderEntity bPoOrderEntity) {
        List<PoOrderDetailVo> detailListData = poOrderVo.getDetailListData();
        for (PoOrderDetailVo detailListDatum : detailListData) {
            BPoOrderDetailEntity bPoOrderDetailEntity = new BPoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bPoOrderDetailEntity);
            bPoOrderDetailEntity.setPo_order_id(bPoOrderEntity.getId());
            int bPurOrderDetail = bPoOrderDetailMapper.insert(bPoOrderDetailEntity);
            if (bPurOrderDetail == 0){
                throw new BusinessException("新增失败");
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(PoOrderVo poOrderVo, BPoOrderEntity bPoOrderEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bPoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_ORDER);
        BPoOrderAttachEntity bPoOrderAttachEntity = insertFile(fileEntity, poOrderVo, new BPoOrderAttachEntity());
        bPoOrderAttachEntity.setPo_order_id(bPoOrderEntity.getId());
        int insert = bPoOrderAttachMapper.insert(bPoOrderAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

//    /**
//     * 计算采购订单金额和数量
//     * @param detailListData 采购订单明细列表
//     * @param poOrderEntity 采购订单实体
//     */
//    private void calculatePoorderAmounts(List<PoOrderDetailVo> detailListData, BPoOrderEntity poOrderEntity) {
//        if (detailListData == null || detailListData.isEmpty()) {
//            // 如果明细为空，设置为0
//            poOrderEntity.setOrder_amount_sum(BigDecimal.ZERO);
//            poOrderEntity.setOrder_total(BigDecimal.ZERO);
//            poOrderEntity.setTax_amount_sum(BigDecimal.ZERO);
//            return;
//        }
//
//        BigDecimal orderAmountSum = BigDecimal.ZERO;    // 订单总金额
//        BigDecimal orderTotal = BigDecimal.ZERO;        // 总采购数量（吨）
//        BigDecimal taxAmountSum = BigDecimal.ZERO;      // 总税额
//
//        for (PoOrderDetailVo detail : detailListData) {
//            BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
//            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
//            BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
//
//            // 计算订单总金额：sum(明细.qty * 明细.price)
//            BigDecimal amount = qty.multiply(price);
//            orderAmountSum = orderAmountSum.add(amount);
//
//            // 计算总采购数量（吨）：sum(明细.qty)
//            orderTotal = orderTotal.add(qty);
//
//            // 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
//            BigDecimal taxAmount = amount.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//            taxAmountSum = taxAmountSum.add(taxAmount);
//        }
//
//        // 设置计算结果到订单实体
//        poOrderEntity.setOrder_amount_sum(orderAmountSum);
//        poOrderEntity.setOrder_total(orderTotal);
//        poOrderEntity.setTax_amount_sum(taxAmountSum);
//    }

    @Override
    public CheckResultAo checkLogic(PoOrderVo searchCondition, String checkType) {
        BPoOrderEntity bPoOrderEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                // 商品重复校验
                Map<String, Long> collect = searchCondition.getDetailListData()
                        .stream()
                        .map(PoOrderDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result);
                }

                // 标准合同下推校验 只能下推一个订单
                if (ObjectUtil.isNotEmpty(searchCondition.getPo_contract_id())) {
                    List<PoOrderVo> poOrderVos = mapper.validateDuplicateContractId(searchCondition);
                    if (CollectionUtil.isNotEmpty(poOrderVos)) {
                        return CheckResultUtil.NG("标准合同已存在下推订单", poOrderVos);
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bPoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_ZERO) && !Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，采购订单[%s]不是待审批,驳回状态,无法修改", bPoOrderEntity.getCode()));
                }

                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect2 = searchCondition.getDetailListData()
                        .stream()
                        .map(PoOrderDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result2 = new ArrayList<>();
                collect2.forEach((k,v)->{
                    if(v>1) result2.add(k);
                });

                if (result2!=null&&result2.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result2);
                }
                break;
            // 删除
            case CheckResultAo.DELETE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bPoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_ZERO) && !Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，采购订单[%s]不是待审批,驳回状态,无法删除",bPoOrderEntity.getCode()));
                }

                List<BApVo> delBApVos = bApMapper.selectByPoCode(searchCondition.getCode());
                if (CollectionUtil.isNotEmpty(delBApVos)){
                    return CheckResultUtil.NG("删除失败，存在关联付款管理");
                }
                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bPoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_FIVE) || Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_CONTRACT_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，采购订单[%s]无法重复作废",bPoOrderEntity.getCode()));
                }
                if (!Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，采购订单[%s]审核中，无法作废",bPoOrderEntity.getCode()));
                }

                List<BApVo> cancelOrderVos = bApMapper.selByPoCodeNotByStatus(searchCondition.getId(), DictConstant.DICT_B_AP_STATUS_FIVE);
                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
                    return CheckResultUtil.NG(String.format("作废失败，付款管理[%s]数据未作废，请先完成该付款管理的作废。",cancelOrderVos.stream().map(BApVo::getCode).collect(Collectors.toList())));
                }
                break;
            // todo 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bPoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bPoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bPoOrderEntity.getStatus(), DictConstant.DICT_B_PO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，采购合同[%s]未进入执行状态",bPoOrderEntity.getCode()));
                }

                break;

            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 列表查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<PoOrderVo> selectPage(PoOrderVo searchCondition) {
        // 分页条件
        Page<PoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 采购订单 统计
     *
     * @param searchCondition
     */
    @Override
    public PoOrderVo querySum(PoOrderVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 采购订单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(PoOrderVo searchCondition) {
        // 1. 校验业务规则
        checkUpdateLogic(searchCondition);
        
        // 2.保存采购订单
        UpdateResultAo<Integer> insertResultAo = update(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER);

        return insertResultAo;
    }

    /**
     * 审批流程回调
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(PoOrderVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        PoOrderVo poOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("合同金额:", poOrderVo.getOrder_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(poOrderVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 更新采购订单主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(PoOrderVo poOrderVo) {
        // 1. 更新主表信息
        BPoOrderEntity bPoOrderEntity = updateMainEntity(poOrderVo);
        // 2. 更新明细信息
        updateDetailList(poOrderVo, bPoOrderEntity);
        // 3. 更新附件信息
        updateAttach(poOrderVo, bPoOrderEntity);
        // 4. 更新订单财务数据
        iCommonPoTotalService.reCalculateAllTotalDataByPoOrderId(bPoOrderEntity.getId());
        return UpdateResultUtil.OK(1);
    }

    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(PoOrderVo poOrderVo) {
        CheckResultAo cr = checkLogic(poOrderVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新主表信息
     */
    private BPoOrderEntity updateMainEntity(PoOrderVo poOrderVo) {
        BPoOrderEntity bPoOrderEntity = (BPoOrderEntity) BeanUtilsSupport.copyProperties(poOrderVo, BPoOrderEntity.class);
        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_ONE);
        bPoOrderEntity.setBpm_process_name("更新采购订单审批");
//        calculatePoorderAmounts(poOrderVo.getDetailListData(), bPoOrderEntity);
        int updCount = mapper.updateById(bPoOrderEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bPoOrderEntity;
    }

    /**
     * 更新明细信息
     */
    private void updateDetailList(PoOrderVo poOrderVo, BPoOrderEntity bPoOrderEntity) {
        List<PoOrderDetailVo> detailListData = poOrderVo.getDetailListData();
        bPoOrderDetailMapper.delete(new LambdaQueryWrapper<BPoOrderDetailEntity>()
                .eq(BPoOrderDetailEntity :: getPo_order_id, bPoOrderEntity.getId()));
        for (PoOrderDetailVo detailListDatum : detailListData) {
            BPoOrderDetailEntity bPoOrderDetailEntity = new BPoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bPoOrderDetailEntity);
            bPoOrderDetailEntity.setPo_order_id(bPoOrderEntity.getId());
            int bPurOrderDetail = bPoOrderDetailMapper.insert(bPoOrderDetailEntity);
            if (bPurOrderDetail == 0){
                throw new BusinessException("新增采购订单明细表-商品失败");
            }
        }
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(PoOrderVo poOrderVo, BPoOrderEntity bPoOrderEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bPoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_ORDER);
        PoOrderAttachVo poOrderAttachVo = bPoOrderAttachMapper.selByPoOrderId(bPoOrderEntity.getId());
        if (poOrderAttachVo != null) {
            // 更新附件信息
            BPoOrderAttachEntity bPoOrderAttachEntity = (BPoOrderAttachEntity) BeanUtilsSupport.copyProperties(poOrderAttachVo, BPoOrderAttachEntity.class);
            insertFile(fileEntity, poOrderVo, bPoOrderAttachEntity);
            bPoOrderAttachEntity.setPo_order_id(bPoOrderEntity.getId());
            int update = bPoOrderAttachMapper.updateById(bPoOrderAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            BPoOrderAttachEntity bPoOrderAttachEntity = new BPoOrderAttachEntity();
            insertFile(fileEntity, poOrderVo, bPoOrderAttachEntity);
            bPoOrderAttachEntity.setPo_order_id(bPoOrderEntity.getId());
            int insert = bPoOrderAttachMapper.insert(bPoOrderAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 删除采购订单信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<PoOrderVo> searchCondition) {
        for (PoOrderVo poContractVo : searchCondition) {

            // 作废前check
            CheckResultAo cr = checkLogic(poContractVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BPoOrderEntity bPoContractEntity = mapper.selectById(poContractVo.getId());
            bPoContractEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bPoContractEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public PoOrderVo getPrintInfo(PoOrderVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_PO_ORDER);
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
     * 导出查询
     *
     * @param param
     */
    @Override
    public List<PoOrderVo> selectExportList(PoOrderVo param) {
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
     * 启动审批流
     */
    public void startFlowProcess(PoOrderVo bean,String type){
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
     * 附件逻辑 全删全增
     */
    public BPoOrderAttachEntity insertFile(SFileEntity fileEntity, PoOrderVo vo, BPoOrderAttachEntity extra) {
        //  其他附件附件全删
       /* if (vo.getDoc_att_file()!=null){
            deleteFile(vo.getDoc_att_file());
        }*/
        // 其他附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 其他附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setOne_file(null);
        }
        return extra;
    }

    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bPoOrderEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_TWO);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程拒绝 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_THREE);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购订单[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_ZERO);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购订单[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());
        bPoOrderEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bPoOrderEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bPoOrderEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bPoOrderEntity);

        log.debug("====》采购订单[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }


    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(PoOrderVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        PoOrderVo poOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", poOrderVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(poOrderVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bPoOrderEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_FIVE);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        log.debug("====》采购订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_TWO);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bPoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_ORDER);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购订单[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_TWO);
        bPoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bPoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bPoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_ORDER);
        mCancelService.delete(mCancelVo);

        log.debug("====》采购订单[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(PoOrderVo searchCondition) {
        log.debug("====》采购订单[{}]作废审批流程更新最新审批人，更新开始《====",searchCondition.getId());
        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        bPoOrderEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bPoOrderEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bPoOrderEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bPoOrderEntity);

        log.debug("====》采购订单[{}]作废审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(PoOrderVo searchCondition) {

        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BPoOrderEntity bPoOrderEntity = mapper.selectById(searchCondition.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bPoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PO_ORDER);
        fileEntity = insertCancelFile(fileEntity, searchCondition);

        bPoOrderEntity.setBpm_cancel_process_name("作废采购订单审批");
        bPoOrderEntity.setStatus(DictConstant.DICT_B_PO_ORDER_STATUS_FOUR);
        int insert = mapper.updateById(bPoOrderEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(searchCondition.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_PO_ORDER);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(searchCondition.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER_CANCEL);

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(PoOrderVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        return null;
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, PoOrderVo vo) {
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
}
