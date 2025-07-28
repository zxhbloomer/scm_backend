package com.xinyirun.scm.core.system.serviceimpl.business.so.soorder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderAttachEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderEntity;
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
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderAttachVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;
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
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArMapper;
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderAttachMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.so.soorder.IBSoOrderTotalService;
import com.xinyirun.scm.core.system.service.business.so.soorder.IBSoOrderService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BSoOrderAutoCodeServiceImpl;
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
 * 销售订单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Slf4j
@Service
public class BSoOrderServiceImpl extends ServiceImpl<BSoOrderMapper, BSoOrderEntity> implements IBSoOrderService {

    @Autowired
    private BSoOrderMapper mapper;

    @Autowired
    private BProjectMapper bProjectMapper;

    @Autowired
    private BSoContractMapper bSoContractMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private BSoOrderAttachMapper bSoOrderAttachMapper;

    @Autowired
    private BSoOrderDetailMapper bSoOrderDetailMapper;

    @Autowired
    private BSoOrderAutoCodeServiceImpl bSoOrderAutoCodeService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private ISPagesService isPagesService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private BArMapper bArMapper;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private MStaffMapper mStaffMapper;

    @Autowired
    private IBSoOrderTotalService iBSoOrderFinService;

    // 注意：ICommonSoTotalService暂时跳过，按照要求不处理
    // @Autowired
    // private ICommonSoTotalService iCommonSoTotalService;

    /**
     * 获取销售订单信息
     * @param id
     */
    @Override
    public BSoOrderVo selectById(Integer id) {
        BSoOrderVo BSoOrderVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(BSoOrderVo.getDoc_att_file());
        BSoOrderVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_SO_CONTRACT_STATUS_FOUR.equals(BSoOrderVo.getStatus()) || Objects.equals(BSoOrderVo.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(BSoOrderVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            BSoOrderVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                BSoOrderVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            BSoOrderVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());

            // 作废时间
            BSoOrderVo.setCancel_time(mCancelVo.getC_time());
        }

        // 查询是否存在项目信息
        if (BSoOrderVo.getProject_code() != null) {
            BProjectVo bProjectVo = bProjectMapper.selectCode(BSoOrderVo.getProject_code());
            List<SFileInfoVo> project_doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
            bProjectVo.setDoc_att_files(project_doc_att_files);
            BSoOrderVo.setProject(bProjectVo);
        }

        // 添加合同信息
        if (BSoOrderVo.getSo_contract_id() != null) {
            BSoContractVo soContractVo = bSoContractMapper.selectId(BSoOrderVo.getSo_contract_id());
            List<SFileInfoVo> contract_doc_att_files = isFileService.selectFileInfo(soContractVo.getDoc_att_file());
            soContractVo.setDoc_att_files(contract_doc_att_files);
            BSoOrderVo.setSo_contract(soContractVo);
        }

        return BSoOrderVo;
    }

    /**
     * 销售订单  新增
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoOrderVo> startInsert(BSoOrderVo searchCondition) {
        // 1. 校验业务规则
        checkInsertLogic(searchCondition);
        
        // 2.保存销售订单
        InsertResultAo<BSoOrderVo> insertResultAo = insert(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition, SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER);
        return insertResultAo;
    }

    /**
     * 新增销售订单主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BSoOrderVo> insert(BSoOrderVo BSoOrderVo) {
        // 1. 保存主表信息
        BSoOrderEntity bSoOrderEntity = saveMainEntity(BSoOrderVo);
        // 2. 保存明细信息
        saveDetailList(BSoOrderVo, bSoOrderEntity);
        // 3. 保存附件信息
        saveAttach(BSoOrderVo, bSoOrderEntity);
        // 4. 设置返回ID
        BSoOrderVo.setId(bSoOrderEntity.getId());
        // 5. 更新订单财务数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(bSoOrderEntity.getId());
        return InsertResultUtil.OK(BSoOrderVo);
    }

    /**
     * 校验新增业务规则
     */
    private void checkInsertLogic(BSoOrderVo BSoOrderVo) {
        CheckResultAo cr = checkLogic(BSoOrderVo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 保存主表信息
     */
    private BSoOrderEntity saveMainEntity(BSoOrderVo BSoOrderVo) {
        BSoOrderEntity bSoOrderEntity = new BSoOrderEntity();
        BeanUtils.copyProperties(BSoOrderVo, bSoOrderEntity);
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ONE);
        bSoOrderEntity.setCode(bSoOrderAutoCodeService.autoCode().getCode());
        bSoOrderEntity.setIs_del(Boolean.FALSE);
        bSoOrderEntity.setBpm_process_name("新增销售订单审批");
//        calculateSoorderAmounts(soOrderVo.getDetailListData(), bSoOrderEntity);
        int bSalOrder = mapper.insert(bSoOrderEntity);
        if (bSalOrder == 0){
            throw new BusinessException("新增失败");
        }
        return bSoOrderEntity;
    }

    /**
     * 保存明细信息
     */
    private void saveDetailList(BSoOrderVo BSoOrderVo, BSoOrderEntity bSoOrderEntity) {
        List<BSoOrderDetailVo> detailListData = BSoOrderVo.getDetailListData();
        for (BSoOrderDetailVo detailListDatum : detailListData) {
            BSoOrderDetailEntity bSoOrderDetailEntity = new BSoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoOrderDetailEntity);
            bSoOrderDetailEntity.setSo_order_id(bSoOrderEntity.getId());
            int bSalOrderDetail = bSoOrderDetailMapper.insert(bSoOrderDetailEntity);
            if (bSalOrderDetail == 0){
                throw new BusinessException("新增失败");
            }
        }
    }

    /**
     * 保存附件信息
     */
    private void saveAttach(BSoOrderVo BSoOrderVo, BSoOrderEntity bSoOrderEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);
        BSoOrderAttachEntity bSoOrderAttachEntity = insertFile(fileEntity, BSoOrderVo, new BSoOrderAttachEntity());
        bSoOrderAttachEntity.setSo_order_id(bSoOrderEntity.getId());
        int insert = bSoOrderAttachMapper.insert(bSoOrderAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }
    }

//    /**
//     * 计算销售订单金额和数量
//     * @param detailListData 销售订单明细列表
//     * @param soOrderEntity 销售订单实体
//     */
//    private void calculateSoorderAmounts(List<SoOrderDetailVo> detailListData, BSoOrderEntity soOrderEntity) {
//        if (detailListData == null || detailListData.isEmpty()) {
//            // 如果明细为空，设置为0
//            soOrderEntity.setOrder_amount_sum(BigDecimal.ZERO);
//            soOrderEntity.setOrder_total(BigDecimal.ZERO);
//            soOrderEntity.setTax_amount_sum(BigDecimal.ZERO);
//            return;
//        }
//
//        BigDecimal orderAmountSum = BigDecimal.ZERO;    // 订单总金额
//        BigDecimal orderTotal = BigDecimal.ZERO;        // 总销售数量（吨）
//        BigDecimal taxAmountSum = BigDecimal.ZERO;      // 总税额
//
//        for (SoOrderDetailVo detail : detailListData) {
//            BigDecimal qty = detail.getQty() != null ? detail.getQty() : BigDecimal.ZERO;
//            BigDecimal price = detail.getPrice() != null ? detail.getPrice() : BigDecimal.ZERO;
//            BigDecimal taxRate = detail.getTax_rate() != null ? detail.getTax_rate() : BigDecimal.ZERO;
//
//            // 计算订单总金额：sum(明细.qty * 明细.price)
//            BigDecimal amount = qty.multiply(price);
//            orderAmountSum = orderAmountSum.add(amount);
//
//            // 计算总销售数量（吨）：sum(明细.qty)
//            orderTotal = orderTotal.add(qty);
//
//            // 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
//            BigDecimal taxAmount = amount.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//            taxAmountSum = taxAmountSum.add(taxAmount);
//        }
//
//        // 设置计算结果到订单实体
//        soOrderEntity.setOrder_amount_sum(orderAmountSum);
//        soOrderEntity.setOrder_total(orderTotal);
//        soOrderEntity.setTax_amount_sum(taxAmountSum);
//    }

    @Override
    public CheckResultAo checkLogic(BSoOrderVo searchCondition, String checkType) {
        BSoOrderEntity bSoOrderEntity = null;
        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                // 商品重复校验
                Map<String, Long> collect = searchCondition.getDetailListData()
                        .stream()
                        .map(BSoOrderDetailVo::getSku_code)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                List<String> result = new ArrayList<>();
                collect.forEach((k,v)->{
                    if(v>1) result.add(k);
                });

                if (result!=null&&result.size()>0){
                    return CheckResultUtil.NG("商品添加重复",result);
                }

                // 标准合同下推校验 只能下推一个订单
                if (ObjectUtil.isNotEmpty(searchCondition.getSo_contract_id())) {
                    List<BSoOrderVo> BSoOrderVos = mapper.validateDuplicateContractId(searchCondition);
                    if (CollectionUtil.isNotEmpty(BSoOrderVos)) {
                        return CheckResultUtil.NG("标准合同已存在下推订单", BSoOrderVos);
                    }
                }

                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_ZERO) && !Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("修改失败，销售订单[%s]不是待审批,驳回状态,无法修改", bSoOrderEntity.getCode()));
                }

                if (searchCondition.getDetailListData()==null){
                    return CheckResultUtil.NG("至少添加一个商品");
                }

                Map<String, Long> collect2 = searchCondition.getDetailListData()
                        .stream()
                        .map(BSoOrderDetailVo::getSku_code)
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

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否待审批或者驳回状态
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_ZERO) && !Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_THREE)) {
                    return CheckResultUtil.NG(String.format("删除失败，销售订单[%s]不是待审批,驳回状态,无法删除",bSoOrderEntity.getCode()));
                }

                List<BArVo> delBArVos = bArMapper.selectBySoCode(searchCondition.getCode());
                if (CollectionUtil.isNotEmpty(delBArVos)){
                    return CheckResultUtil.NG("删除失败，存在关联收款管理");
                }
                break;
            // 作废校验
            case CheckResultAo.CANCEL_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_FIVE) || Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_CONTRACT_STATUS_FOUR)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售订单[%s]无法重复作废",bSoOrderEntity.getCode()));
                }
                if (!Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("作废失败，销售订单[%s]审核中，无法作废",bSoOrderEntity.getCode()));
                }

                List<BArVo> cancelOrderVos = bArMapper.selBySoCodeNotByStatus(searchCondition.getId(), DictConstant.DICT_B_AR_STATUS_FIVE);
                if (CollectionUtil.isNotEmpty(cancelOrderVos)){
                    return CheckResultUtil.NG(String.format("作废失败，收款管理[%s]数据未作废，请先完成该收款管理的作废。",cancelOrderVos.stream().map(BArVo::getCode).collect(Collectors.toList())));
                }
                break;
            // todo 完成校验
            case CheckResultAo.FINISH_CHECK_TYPE:
                if (searchCondition.getId() == null) {
                    return CheckResultUtil.NG("id不能为空");
                }

                bSoOrderEntity = mapper.selectById(searchCondition.getId());
                if (bSoOrderEntity == null) {
                    return CheckResultUtil.NG("单据不存在");
                }

                // 是否已经作废
                if (Objects.equals(bSoOrderEntity.getStatus(), DictConstant.DICT_B_SO_ORDER_STATUS_TWO)) {
                    return CheckResultUtil.NG(String.format("完成失败，销售合同[%s]未进入执行状态",bSoOrderEntity.getCode()));
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
    public IPage<BSoOrderVo> selectPage(BSoOrderVo searchCondition) {
        // 分页条件
        Page<BSoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 按应收退款条件分页查询
     *
     * @param searchCondition
     */
    @Override
    public IPage<BSoOrderVo> selectPageByArrefund(BSoOrderVo searchCondition) {
        // 分页条件
        Page<BSoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPageByArrefund(pageCondition, searchCondition);
    }

    /**
     * 销售订单 统计
     *
     * @param searchCondition
     */
    @Override
    public BSoOrderVo querySum(BSoOrderVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    @Override
    public BSoOrderVo querySumByArrefund(BSoOrderVo searchCondition) {
        return mapper.querySumByArrefund(searchCondition);
    }

    /**
     * 销售订单  更新
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BSoOrderVo searchCondition) {
        // 1. 校验业务规则
        checkUpdateLogic(searchCondition);
        
        // 2.保存销售订单
        UpdateResultAo<Integer> insertResultAo = update(searchCondition);

        // 3.启动审批流程
        startFlowProcess(searchCondition,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER);

        return insertResultAo;
    }

    /**
     * 审批流程回调
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BSoOrderVo searchCondition) {
        log.debug("====》审批流程创建成功，更新开始《====");
        BSoOrderVo BSoOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 合同金额:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("合同金额:", BSoOrderVo.getOrder_amount_sum());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BSoOrderVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 更新销售订单主流程，分步骤调用各业务方法，便于维护和扩展
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BSoOrderVo BSoOrderVo) {
        // 1. 更新主表信息
        BSoOrderEntity bSoOrderEntity = updateMainEntity(BSoOrderVo);
        // 2. 更新明细信息
        updateDetailList(BSoOrderVo, bSoOrderEntity);
        // 3. 更新附件信息
        updateAttach(BSoOrderVo, bSoOrderEntity);
        // 4. 更新订单财务数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(bSoOrderEntity.getId());
        return UpdateResultUtil.OK(1);
    }

    /**
     * 校验更新业务规则
     */
    private void checkUpdateLogic(BSoOrderVo BSoOrderVo) {
        CheckResultAo cr = checkLogic(BSoOrderVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
    }

    /**
     * 更新主表信息
     */
    private BSoOrderEntity updateMainEntity(BSoOrderVo BSoOrderVo) {
        BSoOrderEntity bSoOrderEntity = (BSoOrderEntity) BeanUtilsSupport.copyProperties(BSoOrderVo, BSoOrderEntity.class);
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ONE);
        bSoOrderEntity.setBpm_process_name("更新销售订单审批");
//        calculateSoorderAmounts(soOrderVo.getDetailListData(), bSoOrderEntity);
        int updCount = mapper.updateById(bSoOrderEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return bSoOrderEntity;
    }

    /**
     * 更新明细信息
     */
    private void updateDetailList(BSoOrderVo BSoOrderVo, BSoOrderEntity bSoOrderEntity) {
        List<BSoOrderDetailVo> detailListData = BSoOrderVo.getDetailListData();
        bSoOrderDetailMapper.delete(new LambdaQueryWrapper<BSoOrderDetailEntity>()
                .eq(BSoOrderDetailEntity :: getSo_order_id, bSoOrderEntity.getId()));
        for (BSoOrderDetailVo detailListDatum : detailListData) {
            BSoOrderDetailEntity bSoOrderDetailEntity = new BSoOrderDetailEntity();
            BeanUtils.copyProperties(detailListDatum, bSoOrderDetailEntity);
            bSoOrderDetailEntity.setSo_order_id(bSoOrderEntity.getId());
            int bSalOrderDetail = bSoOrderDetailMapper.insert(bSoOrderDetailEntity);
            if (bSalOrderDetail == 0){
                throw new BusinessException("新增销售订单明细表-商品失败");
            }
        }
    }

    /**
     * 更新附件信息
     */
    private void updateAttach(BSoOrderVo BSoOrderVo, BSoOrderEntity bSoOrderEntity) {
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);
        BSoOrderAttachVo BSoOrderAttachVo = bSoOrderAttachMapper.selBySoOrderId(bSoOrderEntity.getId());
        if (BSoOrderAttachVo != null) {
            // 更新附件信息
            BSoOrderAttachEntity bSoOrderAttachEntity = (BSoOrderAttachEntity) BeanUtilsSupport.copyProperties(BSoOrderAttachVo, BSoOrderAttachEntity.class);
            insertFile(fileEntity, BSoOrderVo, bSoOrderAttachEntity);
            bSoOrderAttachEntity.setSo_order_id(bSoOrderEntity.getId());
            int update = bSoOrderAttachMapper.updateById(bSoOrderAttachEntity);
            if (update == 0) {
                throw new UpdateErrorException("更新附件信息失败");
            }
        } else {
            // 新增附件信息
            BSoOrderAttachEntity bSoOrderAttachEntity = new BSoOrderAttachEntity();
            insertFile(fileEntity, BSoOrderVo, bSoOrderAttachEntity);
            bSoOrderAttachEntity.setSo_order_id(bSoOrderEntity.getId());
            int insert = bSoOrderAttachMapper.insert(bSoOrderAttachEntity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }
    }

    /**
     * 删除销售订单信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BSoOrderVo> searchCondition) {
        for (BSoOrderVo soContractVo : searchCondition) {

            // 作废前check
            CheckResultAo cr = checkLogic(soContractVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BSoOrderEntity bSoContractEntity = mapper.selectById(soContractVo.getId());
            bSoContractEntity.setIs_del(Boolean.TRUE);

            int delCount = mapper.updateById(bSoContractEntity);
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
    public BSoOrderVo getPrintInfo(BSoOrderVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_SO_ORDER);
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
    public List<BSoOrderVo> selectExportList(BSoOrderVo param) {
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
    public void startFlowProcess(BSoOrderVo bean, String type){
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
    public BSoOrderAttachEntity insertFile(SFileEntity fileEntity, BSoOrderVo vo, BSoOrderAttachEntity extra) {
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
    public UpdateResultAo<Integer> bpmCallBackApprove(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bSoOrderEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());

        log.debug("====》销售订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程拒绝 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程拒绝，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_THREE);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());

        log.debug("====》销售订单[{}]审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }


    /**
     * 审批流程撤销 更新审核状态待审批
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程撤销，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_ZERO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());
        
        log.debug("====》销售订单[{}]审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程更新最新审批人，更新开始《====",searchCondition.getId());

        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());
        bSoOrderEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bSoOrderEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bSoOrderEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(bSoOrderEntity);

        log.debug("====》销售订单[{}]审批流程更新最新审批人，更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }


    /**
     *  作废审批流程回调
     *  作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BSoOrderVo searchCondition){
        log.debug("====》作废审批流程创建成功，更新开始《====");
        BSoOrderVo BSoOrderVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由:1000
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("作废理由:", BSoOrderVo.getCancel_reason());

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(BSoOrderVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]审批流程通过，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bSoOrderEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_FIVE);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());

        log.debug("====》销售订单[{}]审批流程通过,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]作废审批流程拒绝，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelService.delete(mCancelVo);

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());

        log.debug("====》销售订单[{}]作废审批流程拒绝,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BSoOrderVo searchCondition) {
        log.debug("====》销售订单[{}]作废审批流程撤销，更新开始《====",searchCondition.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(searchCondition.getId());

        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_TWO);
        bSoOrderEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(bSoOrderEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bSoOrderEntity.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelService.delete(mCancelVo);

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());
        
        log.debug("====》销售订单[{}]作废审批流程撤销,更新结束《====",searchCondition.getId());
        return UpdateResultUtil.OK(i);

    }

    /**
     *  作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BSoOrderVo vo) {
        log.debug("====》销售订单[{}]作废审批流程更新最新审批人，更新开始《====",vo.getId());
        BSoOrderEntity bSoOrderEntity = mapper.selectById(vo.getId());

        bSoOrderEntity.setBpm_cancel_instance_id(vo.getBpm_instance_id());
        bSoOrderEntity.setBpm_cancel_instance_code(vo.getBpm_instance_code());
        bSoOrderEntity.setNext_approve_name(vo.getNext_approve_name());
        int i = mapper.updateById(bSoOrderEntity);

        log.debug("====》销售订单[{}]作废审批流程更新最新审批人，更新结束《====",vo.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BSoOrderVo vo) {

        // 作废前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BSoOrderEntity bSoOrderEntity = mapper.selectById(vo.getId());

        // 1.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bSoOrderEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_SO_ORDER);
        fileEntity = insertCancelFile(fileEntity, vo);

        bSoOrderEntity.setBpm_cancel_process_name("作废销售订单审批");
        bSoOrderEntity.setStatus(DictConstant.DICT_B_SO_ORDER_STATUS_FOUR);
        int insert = mapper.updateById(bSoOrderEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 2.增加作废记录
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(vo.getId());
        mCancelVo.setSerial_type(SystemConstants.SERIAL_TYPE.B_SO_ORDER);
        mCancelVo.setFile_id(fileEntity.getId());
        mCancelVo.setRemark(vo.getCancel_reason());
        mCancelService.insert(mCancelVo);

        // 2.启动审批流程
        startFlowProcess(vo,SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER_CANCEL);

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(vo.getId());

        return UpdateResultUtil.OK(insert);
    }

    /**
     * 完成
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> finish(BSoOrderVo searchCondition) {
        // 作废前check
        CheckResultAo cr = checkLogic(searchCondition, CheckResultAo.FINISH_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 重新计算销售订单财务汇总数据 (暂时跳过ICommonSoTotalService)
        // iCommonSoTotalService.reCalculateAllTotalDataBySoOrderId(searchCondition.getId());

        return null;
    }

    /**
     * 附件
     */
    public SFileEntity insertCancelFile(SFileEntity fileEntity, BSoOrderVo vo) {
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
     * 分页查询包含结算信息
     *
     * @param searchCondition
     */
    @Override
    public IPage<BSoOrderVo> selectOrderListWithSettlePage(BSoOrderVo searchCondition) {
        // 分页条件
        Page<BSoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectOrderListWithSettlePage(pageCondition, searchCondition);
    }

    /**
     * 销售订单结算信息统计
     *
     * @param searchCondition
     */
    @Override
    public BSoOrderVo queryOrderListWithSettlePageSum(BSoOrderVo searchCondition) {
        return mapper.queryOrderListWithSettlePageSum(searchCondition);
    }

    /**
     * 货权转移专用-分页查询销售订单信息
     *
     * @param searchCondition
     */
    @Override
    public IPage<BSoOrderVo> selectOrderListForCargoRightTransferPage(BSoOrderVo searchCondition) {
        // 分页条件
        Page<BSoOrderVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询货权转移专用销售订单page
        return mapper.selectOrderListForCargoRightTransferPage(pageCondition, searchCondition);
    }

    /**
     * 货权转移专用-销售订单统计
     *
     * @param searchCondition
     */
    @Override
    public BSoOrderVo queryOrderListForCargoRightTransferPageSum(BSoOrderVo searchCondition) {
        return mapper.queryOrderListForCargoRightTransferPageSum(searchCondition);
    }

    /**
     * 货权转移专用-获取销售订单明细数据
     *
     * @param searchCondition
     */
    @Override
    public List<BSoOrderDetailVo> selectDetailData(BSoOrderVo searchCondition) {
        return mapper.selectDetailData(searchCondition);
    }
}