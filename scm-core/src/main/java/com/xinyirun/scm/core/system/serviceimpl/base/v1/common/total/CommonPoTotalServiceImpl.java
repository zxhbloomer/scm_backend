package com.xinyirun.scm.core.system.serviceimpl.base.v1.common.total;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.busniess.po.ap.BApTotalEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractTotalEntity;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderTotalEntity;
import com.xinyirun.scm.bean.system.bo.fund.total.TotalDataRecalculateBo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPaySourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPaySourceVo;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.business.po.ap.BApDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.ap.BApMapper;
import com.xinyirun.scm.core.system.mapper.business.po.ap.BApSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.po.ap.BApTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundMapper;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPayMapper;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPaySourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.po.appay.BApPaySourceMapper;
import com.xinyirun.scm.core.system.mapper.business.po.pocontract.BPoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.po.pocontract.BPoContractTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderDetailTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.po.poorder.BPoOrderTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.po.settlement.BPoSettlementTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.business.po.settlement.BPoSettlementMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanTotalVo;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.po.ap.IBApTotalService;
import com.xinyirun.scm.core.system.service.business.po.aprefund.IBApReFundTotalService;
import com.xinyirun.scm.core.system.service.business.po.cargo_right_transfer.IBPoCargoRightTransferTotalService;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferMapper;
import com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer.BPoCargoRightTransferTotalMapper;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 采购合同表-财务数据汇总 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Service
public class CommonPoTotalServiceImpl extends ServiceImpl<BPoContractTotalMapper, BPoContractTotalEntity> implements ICommonPoTotalService {

    private static final Logger log = LoggerFactory.getLogger(CommonPoTotalServiceImpl.class);

    @Autowired
    private BPoContractMapper bPoContractMapper;

    @Autowired
    private BPoContractTotalMapper bPoContractTotalMapper;

    @Autowired
    private BPoOrderMapper bPoOrderMapper;

    @Autowired
    private BPoOrderTotalMapper bPoOrderTotalMapper;

    @Autowired
    private BApSourceAdvanceMapper bApSourceAdvanceMapper;

    @Autowired
    private BApPayDetailMapper bApPayDetailMapper;

    @Autowired
    private BApPaySourceMapper bApPaySourceMapper;

    @Autowired
    private BApPayMapper bApPayMapper;

    @Autowired
    private BApPaySourceAdvanceMapper bApPaySourceAdvanceMapper;

    @Autowired
    private BApTotalMapper bApTotalMapper;

    @Autowired
    private BApMapper bApMapper;

    @Autowired
    private IBApTotalService bApTotalService;

    @Autowired
    private BApReFundMapper bApReFundMapper;

    @Autowired
    private BApReFundTotalMapper bApReFundTotalMapper;

    @Autowired
    private IBApReFundTotalService bApReFundTotalService;

    @Autowired
    private BApDetailMapper bApDetailMapper;

    @Autowired
    private BInPlanDetailMapper bInPlanDetailMapper;

    @Autowired
    private BInPlanTotalMapper bInPlanTotalMapper;

    @Autowired
    private BInMapper bInMapper;

    @Autowired
    private BPoOrderDetailTotalMapper poOrderDetailTotalMapper;

    @Autowired
    private BPoSettlementMapper bPoSettlementMapper;

    @Autowired
    private BPoSettlementTotalMapper bPoSettlementTotalMapper;

    @Autowired
    private BPoCargoRightTransferMapper bPoCargoRightTransferMapper;

    @Autowired
    private BPoCargoRightTransferTotalMapper bPoCargoRightTransferTotalMapper;

    @Autowired
    private IBPoCargoRightTransferTotalService bPoCargoRightTransferTotalService;

    /**
     * 重新计算所有的财务数据，最终得到合同ID集合 contractIdSet
     * @param bo 查询条件
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalData(TotalDataRecalculateBo bo) {
        // 1. 参数校验
        validateTotalDataRecalculateBo(bo);

        // 2. 业务分支，收集合同ID，去重
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>(collectContractIds(bo));

        // 3. 判断合同ID集合是否为空
        if (contractIdSet.isEmpty()) {
            throw new BusinessException("未获取到任何合同ID");
        }
        log.info("最终合同ID集合 contractIdSet: {}", contractIdSet);
        // contractIdSet 可供后续业务使用

        // 循环contractIdSet，处理每个合同ID
        for (Integer contractId : contractIdSet) {
            /**
             * 处理每个合同ID的Total数据
             * 1. 处理应付退款total数据（b_ap_refund_total） - 优先处理
             * 2. 处理采购结算汇总数据（b_po_settlement_total）
             *    - 通过合同ID查询b_po_settlement_detail_source_inbound表获取结算ID集合
             *    - 批量更新b_po_settlement_total表的汇总数据：processing_qty, processing_weight, processing_volume, unprocessed_qty, unprocessed_weight, unprocessed_volume, processed_qty, processed_weight, processed_volume, planned_qty, planned_weight, planned_volume, planned_amount, settled_qty, settled_weight, settled_volume, settled_amount
             * 3. 处理入库方面的total数据（b_in_plan_detail、b_in_plan_total）
             *    - b_in_plan_detail: 更新processing_qty, processing_weight, processing_volume, unprocessed_qty, unprocessed_weight, unprocessed_volume, processed_qty, processed_weight, processed_volume
             *    - b_in_plan_total: 汇总计划级别的processing_qty_total, processing_weight_total, processing_volume_total, unprocessed_qty_total, unprocessed_weight_total, unprocessed_volume_total, processed_qty_total, processed_weight_total, processed_volume_total
             * 4. 处理付款单total数据（b_ap_pay、b_ap_source_advance）
             *    - b_ap_pay: 更新付款单总金额字段
             *    - b_ap_source_advance: payable_amount_total, paid_amount_total, paying_amount_total, unpay_amount_total
             * 5. 处理应付账款total数据（b_ap_total、b_ap_detail、b_ap_source_advance）
             *    - b_ap_total: payable_amount_total, paid_amount_total, paying_amount_total, stoppay_amount_total, cancelpay_amount_total, unpay_amount_total
             *    - b_ap_detail: 调用updateTotalData更新总计字段
             *    - b_ap_source_advance: stoppay_amount_total, cancelpay_amount_total（中止和作废分配）
             * 6. 处理采购订单total数据（b_po_order_total）
             *    - updatePoOrderTotalData: 更新采购订单总计数据
             *    - updateAdvanceAmountTotalData: 更新预付款总计数据
             *    - updatePaidTotalData: 更新已付款总金额数据
             * 7. 处理采购合同total数据（b_po_contract_total）
             *    - updateContractAdvanceTotalData: 汇总合同下所有订单的预付款数据
             */
            // ===================== 处理应付退款total数据（b_ap_refund_total） - 优先处理 =====================
            processApRefundTotalDataByContractId(contractId);
            // ===================== 处理货权转秛total数据（b_po_cargo_right_transfer_total） =====================
            processCargoRightTransferTotalDataByContractId(contractId);
            // ===================== 处理采购结算汇总数据（b_po_settlement_total） =====================
            processPoSettlementTotalDataByContractId(contractId);
            // ===================== 处理入库方面的total数据（b_in_plan_detail、b_in_plan_total） =====================
            processInPlanTotalDataByContractId(contractId);
            // ===================== 处理付款total数据（b_ap_pay、b_ap_source_advance） =====================
            processPayTotalDataByContractId(contractId);
            // ===================== 处理应付账款total数据（b_ap_total、b_ap_detail、b_ap_source_advance） =====================
            processApTotalDataByContractId(contractId);
            // ===================== 处理采购订单total数据（b_po_order_total） =====================
            processPoOrderTotalDataByContractId(contractId);
            // ===================== 处理采购合同total数据（b_po_contract_total） =====================
            processPoContractTotalDataByContractId(contractId);
            // ===================== END =====================
        }
        return true;
    }

    /**
     * 处理BigDecimal为null的情况，返回BigDecimal.ZERO
     * @param value 可能为null的BigDecimal值
     * @return 处理后的BigDecimal值，如果为null则返回BigDecimal.ZERO
     */
    private BigDecimal handleNullBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }


    /**
     * 处理预付款（b_ap_source_advance）的total数据
     * 步骤：
     * 1. 根据合同ID查询b_ap_pay_source_advance表，获取所有ap_id
     * 2. 将ap_id去重后放入LinkedHashSet
     * 3. 调用bApPayMapper.updateTotalData批量更新付款单的total数据
     *
     * @param contractId 合同ID
     */
    private void processPayTotalDataByContractId(Integer contractId) {
        // 0、实例化LinkedHashSet<Integer> ApPay
        LinkedHashSet<Integer> apPaySet = new LinkedHashSet<>();
        // 1、调用bApPaySourceAdvanceMapper.selectByContractId
        List<BApPaySourceAdvanceVo> paySourceAdvanceList = bApPaySourceAdvanceMapper.selectByContractId(contractId);
        if (paySourceAdvanceList != null && !paySourceAdvanceList.isEmpty()) {
            for (BApPaySourceAdvanceVo vo : paySourceAdvanceList) {
                if (vo.getAp_id() != null) {
                    apPaySet.add(vo.getAp_id());
                }
            }
        }
        // 2、执行更新逻辑
        if (!apPaySet.isEmpty()) {
//            int t = bApPayDetailMapper.updateTotalData(apPaySet);
//            log.debug("更新付款单明细表数据成功，影响行数: {}", t);
            int i = bApPayMapper.updateTotalData(apPaySet);
            log.debug("更新付款单总金额数据成功，影响行数: {}", i);
        }

        // ========== 新增：更新b_ap_source_advance申请付款总金额、已付款总金额、付款中总金额、未付款总金额 ========== 
        for (Integer apId : apPaySet) {
            // 1. 查询b_ap_source_advance
            List<BApSourceAdvanceVo> advanceVoList = bApSourceAdvanceMapper.selectByApId(apId);
            // 2. 查询b_ap_pay金额汇总
            BApPayVo payVo = bApPayMapper.getSumAmount(apId, null);
            BigDecimal remainPaid = payVo != null && payVo.getPaid_amount_total() != null ? payVo.getPaid_amount_total() : BigDecimal.ZERO;
            // 3. 先进先出分配paid_amount_total（已付款金额）
            List<BApSourceAdvanceVo> fifoList = new ArrayList<>();
            for (BApSourceAdvanceVo advanceVo : advanceVoList) {
                BigDecimal payable = advanceVo.getOrder_amount() == null ? BigDecimal.ZERO : advanceVo.getOrder_amount();
                advanceVo.setPayable_amount_total(payable);
                BigDecimal paid = BigDecimal.ZERO;
                if (remainPaid.compareTo(BigDecimal.ZERO) > 0) {
                    if (remainPaid.compareTo(payable) >= 0) {
                        paid = payable;
                        remainPaid = remainPaid.subtract(payable);
                    } else {
                        paid = remainPaid;
                        remainPaid = BigDecimal.ZERO;
                    }
                }
                advanceVo.setPaid_amount_total(paid);
                fifoList.add(advanceVo);
            }
            // 4. 先进先出分配paying_amount_total（付款中金额）
            BigDecimal remainPaying = payVo != null && payVo.getPaying_amount_total() != null ? payVo.getPaying_amount_total() : BigDecimal.ZERO;
            for (BApSourceAdvanceVo advanceVo : fifoList) {
                BigDecimal maxPaying = advanceVo.getPayable_amount_total().subtract(advanceVo.getPaid_amount_total());
                BigDecimal paying = BigDecimal.ZERO;
                if (remainPaying.compareTo(BigDecimal.ZERO) > 0) {
                    if (remainPaying.compareTo(maxPaying) >= 0) {
                        paying = maxPaying;
                        remainPaying = remainPaying.subtract(maxPaying);
                    } else {
                        paying = remainPaying;
                        remainPaying = BigDecimal.ZERO;
                    }
                }
                advanceVo.setPaying_amount_total(paying);
            }

            // 5. 统一计算未付款总金额（unpay_amount_total = payable_amount_total - paid_amount_total - paying_amount_total）
            for (BApSourceAdvanceVo advanceVo : fifoList) {
                advanceVo.setUnpay_amount_total(
                    advanceVo.getPayable_amount_total()
                        .subtract(advanceVo.getPaid_amount_total())
                        .subtract(advanceVo.getPaying_amount_total())
                );
            }

            // 分摊完毕开始更新
            for (BApSourceAdvanceVo advanceVo : advanceVoList) {
                BApSourceAdvanceEntity entity = new BApSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bApSourceAdvanceMapper.updateById(entity);
            }

        }

    }

    /**
     * 处理应付账款total数据（b_ap_total、b_ap_detail）- 预付款
     * 步骤：
     * 1. 根据合同ID查询b_ap_source_advance表，获取所有ap_id
     * 2. 将ap_id去重后放入LinkedHashSet
     * 3. 循环ap_id，分别查询和计算各类金额，并赋值到BApTotalVo
     * 4. TODO: 如有保存/更新b_ap_total表的逻辑，请在此补充
     *
     * @param contractId 合同ID
     */
    private void processApTotalDataByContractId(Integer contractId) {
        // 0、实例化LinkedHashSet<Integer> Ap
        LinkedHashSet<Integer> apSet = new LinkedHashSet<>();
        // 1、搜索select * from b_ap_source_advance where po_contract_id=contractId
        List<BApSourceAdvanceVo> apSourceAdvanceList = bApSourceAdvanceMapper.selectByContractId(contractId);
        if (apSourceAdvanceList != null && !apSourceAdvanceList.isEmpty()) {
            for (BApSourceAdvanceVo vo : apSourceAdvanceList) {
                if (vo.getAp_id() != null) {
                    apSet.add(vo.getAp_id());
                }
            }
        }
        // 2、循环变量Ap，更新应付账款total表(b_ap_total)
        for (Integer apId : apSet) {
            // 2.1、查询b_ap_total
            BApTotalVo apTotalVo = bApTotalMapper.selectByApId(apId);
            if (apTotalVo == null) {
                apTotalVo = new BApTotalVo();
                apTotalVo.setAp_id(apId);
            }
            // 2.2、获取申请付款总金额
            BigDecimal payableAmount = bApSourceAdvanceMapper.getSumPayableAmount(apId);
            apTotalVo.setPayable_amount_total(payableAmount);
            // 2.3、获取已付款总金额
            BApPayVo paidVo = bApPayMapper.getSumAmount(apId, null);
            if (paidVo != null) {
                apTotalVo.setPaid_amount_total(paidVo.getPaid_amount_total());
            } else {
                apTotalVo.setPaid_amount_total(BigDecimal.ZERO);
            }
            // 2.4、获取付款中总金额
            BApPayVo payingVo = bApPayMapper.getSumAmount(apId, DictConstant.DICT_B_AP_PAY_STATUS_ZERO);
            if (payingVo != null) {
                apTotalVo.setPaying_amount_total(payingVo.getPayable_amount_total());
            } else {
                apTotalVo.setPaying_amount_total(BigDecimal.ZERO);
            }

            // 获取已中止付款总金额和作废付款总金额
            // 2.4.1、判断应付账款的付款状态是否为已中止
            // 查询应付账款的pay_status和status
            BApVo apVo = bApMapper.selectId(apTotalVo.getAp_id());
            if (apVo != null && DictConstant.DICT_B_AP_PAY_STATUS_STOP.equals(apVo.getPay_status())) {
                // 2.4.2、如果是已中止，则设置paying_amount_total=0，stoppay_amount_total=payable_amount_total-paid_amount_total
                apTotalVo.setPaying_amount_total(BigDecimal.ZERO);
                BigDecimal payableAmountTotal = handleNullBigDecimal(apTotalVo.getPayable_amount_total());
                BigDecimal paidAmountTotal = handleNullBigDecimal(apTotalVo.getPaid_amount_total());
                apTotalVo.setStoppay_amount_total(payableAmountTotal.subtract(paidAmountTotal));
                apTotalVo.setCancelpay_amount_total(BigDecimal.ZERO);
            } else if (apVo != null && DictConstant.DICT_B_AP_STATUS_FIVE.equals(apVo.getStatus())) {
                // 2.4.3、如果是作废状态，则设置paying_amount_total=0, stoppay_amount_total=0, cancelpay_amount_total=payable_amount_total-paid_amount_total
                apTotalVo.setPaying_amount_total(BigDecimal.ZERO);
                apTotalVo.setStoppay_amount_total(BigDecimal.ZERO);
                BigDecimal payableAmountTotal = handleNullBigDecimal(apTotalVo.getPayable_amount_total());
                BigDecimal paidAmountTotal = handleNullBigDecimal(apTotalVo.getPaid_amount_total());
                apTotalVo.setCancelpay_amount_total(payableAmountTotal.subtract(paidAmountTotal));
            } else {
                // 2.4.4、如果不是已中止也不是作废，则保持原有的付款中总金额
                apTotalVo.setStoppay_amount_total(BigDecimal.ZERO);
                apTotalVo.setCancelpay_amount_total(BigDecimal.ZERO);
            }

            // 2.5、未付款总金额
            // 未付款总金额 = 申请付款总金额 - 已付款总金额 - 付款中总金额 - 中止付款总金额 - 作废付款总金额
            BigDecimal payableAmountTotal = handleNullBigDecimal(apTotalVo.getPayable_amount_total());
            BigDecimal paidAmountTotal = handleNullBigDecimal(apTotalVo.getPaid_amount_total());
            BigDecimal payingAmountTotal = handleNullBigDecimal(apTotalVo.getPaying_amount_total());
            BigDecimal stopAmountTotal = handleNullBigDecimal(apTotalVo.getStoppay_amount_total());
            BigDecimal cancelAmountTotal = handleNullBigDecimal(apTotalVo.getCancelpay_amount_total());
            BigDecimal unpayAmountTotal = payableAmountTotal.subtract(paidAmountTotal).subtract(payingAmountTotal).subtract(stopAmountTotal).subtract(cancelAmountTotal);
            apTotalVo.setUnpay_amount_total(unpayAmountTotal);
            
            // 2.6、将VO转换为Entity并更新数据库
            BApTotalEntity apTotalEntity = new BApTotalEntity();
            BeanUtils.copyProperties(apTotalVo, apTotalEntity);

            /**
             * 保存或更新b_ap_total表
             */
            bApTotalService.saveOrUpdate(apTotalEntity);
        }
        // 处理应付账款明细的total（b_ap_detail）数据
        if (!apSet.isEmpty()) {
            bApDetailMapper.updateTotalData(apSet);
        }
        
        // 在b_ap_total更新完毕后，处理中止付款总金额分配
        if (!apSet.isEmpty()) {
            // ========== 新增：更新b_ap_source_advance中止付款总金额stoppay_amount_total ==========
            processStopPaymentAmountDistribution(apSet);
            
            // ========== 新增：更新b_ap_source_advance作废付款总金额cancelpay_amount_total ==========
            processCancelPaymentAmountDistribution(apSet);
        }
    }

    /**
     * 处理应付退款total数据（b_ap_refund_total）
     * @param contractId 合同ID
     */
    private void processApRefundTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取相关的退款ID集合
        LinkedHashSet<Integer> apRefundIdSet = new LinkedHashSet<>();
        List<Integer> refundIdList = bApReFundTotalMapper.selectRefundIdsByContractId(contractId);
        if (refundIdList != null && !refundIdList.isEmpty()) {
            apRefundIdSet.addAll(refundIdList);
        }

        // 2. 如果有退款ID，先插入缺失的Total记录，再进行批量更新
        if (!apRefundIdSet.isEmpty()) {
            try {
                // 2.1 插入缺失的Total记录（处理历史数据）
                int insertResult = bApReFundTotalMapper.insertMissingRefundTotal(apRefundIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的应付退款总计数据成功，插入行数: {}, 退款ID集合: {}", insertResult, apRefundIdSet);
                }
                
                // 2.2 批量更新退款总计数据，从退款源单表同步数据
                int updateResult = bApReFundTotalMapper.batchUpdateRefundTotalFromSource(apRefundIdSet);
                log.debug("批量更新应付退款总计数据成功，影响行数: {}, 退款ID集合: {}", updateResult, apRefundIdSet);
            } catch (Exception e) {
                log.error("处理应付退款总计数据失败，退款ID集合: {}, 错误信息: {}", apRefundIdSet, e.getMessage(), e);
                throw new BusinessException(e);
            }
        } else {
            log.debug("合同ID {} 下未找到应付退款数据", contractId);
        }
    }

    /**
     * 处理应付账款作废时的预付款作废金额分配
     * 当应付账款状态为作废时，将该ap下所有预付款源单的cancelpay_amount_total设置为order_amount
     * 
     * @param apIdSet 应付账款ID集合
     */
    private void processCancelPaymentAmountDistribution(LinkedHashSet<Integer> apIdSet) {
        if (apIdSet == null || apIdSet.isEmpty()) {
            return;
        }
        
        // 循环处理每个应付账款ID
        for (Integer apId : apIdSet) {
            // 检查该应付账款是否为作废状态
            BApVo apVo = bApMapper.selectId(apId);
            if (apVo == null || !DictConstant.DICT_B_AP_STATUS_FIVE.equals(apVo.getStatus())) {
                // 如果不是作废状态，跳过处理
                continue;
            }
            
            // 查询该应付账款下的所有预付款源单记录
            List<BApSourceAdvanceVo> sourceAdvanceList = bApSourceAdvanceMapper.selectByApId(apId);
            if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
                continue;
            }
            
            // 处理每条预付款源单记录
            for (BApSourceAdvanceVo advanceVo : sourceAdvanceList) {
                // 设置作废金额为申请金额
                BigDecimal orderAmount = handleNullBigDecimal(advanceVo.getOrder_amount());
                advanceVo.setCancelpay_amount_total(orderAmount);
                
                // 同时清空其他相关金额字段（作废时这些字段应该为0）
                advanceVo.setPaying_amount_total(BigDecimal.ZERO);
                advanceVo.setUnpay_amount_total(BigDecimal.ZERO);
                advanceVo.setStoppay_amount_total(BigDecimal.ZERO);
                
                // 更新数据库记录
                BApSourceAdvanceEntity entity = new BApSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bApSourceAdvanceMapper.updateById(entity);
                
                log.debug("更新应付账款源单记录作废金额，ap_id: {}, source_advance_id: {}, cancel_amount: {}", 
                    apId, advanceVo.getId(), advanceVo.getCancelpay_amount_total());
            }
            
            log.info("完成应付账款作废处理，ap_id: {}, 共处理预付款源单记录: {} 条", 
                apId, sourceAdvanceList.size());
        }
    }

    /**
     * 按先进先出算法分配并更新b_ap_source_advance中止付款总金额
     * 
     * @param apIdSet 应付账款ID集合
     */
    private void processStopPaymentAmountDistribution(LinkedHashSet<Integer> apIdSet) {
        /**
         * 1、获取这个ap下的中止金额，
         * select
         *   stoppay_amount_total --中止金额
         *  from b_ap t1
         *  left join b_ap_total t2 on t1.id = t2.ap_id
         *  where t1.id = ap_id
         * 从b_ap 表中查询所有的id = ap_id and pay_status = '已中止' and is_deleted = false的数据
         *
         * 2、循环apPaySet，查询b_ap_source_advance得到 List<BApSourceAdvanceVo>
         * 3、继续循环 List<BApSourceAdvanceVo>，计算每个ap的stoppay_amount_total
         *    算法：先进先出
         *          BApSourceAdvanceVo.paying_amount_total = 0
         *          BApSourceAdvanceVo.unpay_amount_total = 0
         *          BApSourceAdvanceVo.stoppay_amount_total = BApSourceAdvanceVo.payable_amount_total - BApSourceAdvanceVo.paid_amount_total
         *          必须满足： BApSourceAdvanceVo.payable_amount_total > BApSourceAdvanceVo.paid_amount_total+stoppay_amount_total
         *          还需要满足：BApSourceAdvanceVo.stoppay_amount_total（累计）<=中止金额
         * 4、更新b_ap_source_advance表的stoppay_amount_total字段
         * 5、循环结束
         * 6、循环结束
         *
         */
        if (apIdSet == null || apIdSet.isEmpty()) {
            return;
        }
        
        // 1. 循环apIdSet，处理中止付款总金额
        for (Integer apId : apIdSet) {
            // 1.1 获取应付账款的中止金额
            BApTotalVo apTotalVo = bApTotalMapper.selectByApId(apId);
            if (apTotalVo == null) {
                continue;
            }
            
            // 检查该应付账款是否为中止状态
            BApVo apVo = bApMapper.selectId(apId);
            if (apVo == null || !DictConstant.DICT_B_AP_PAY_STATUS_STOP.equals(apVo.getPay_status())) {
                continue;
            }
            
            BigDecimal totalStopAmount = handleNullBigDecimal(apTotalVo.getStoppay_amount_total());
            if (totalStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            // 1.2 查询该应付账款下的所有预付款源单记录
            List<BApSourceAdvanceVo> sourceAdvanceList = bApSourceAdvanceMapper.selectByApId(apId);
            if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
                continue;
            }
            
            // 1.3 按先进先出算法分配中止金额
            BigDecimal remainingStopAmount = totalStopAmount;
            
            for (BApSourceAdvanceVo advanceVo : sourceAdvanceList) {
                if (remainingStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                
                // 计算当前记录的可中止金额
                BigDecimal payableAmount = handleNullBigDecimal(advanceVo.getPayable_amount_total());
                BigDecimal paidAmount = handleNullBigDecimal(advanceVo.getPaid_amount_total());
                BigDecimal availableStopAmount = payableAmount.subtract(paidAmount);
                
                // 检查是否满足条件：payable_amount_total > paid_amount_total
                if (availableStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    // 如果没有可中止的金额，设置为0并清空其他字段
                    advanceVo.setStoppay_amount_total(BigDecimal.ZERO);
                    advanceVo.setPaying_amount_total(BigDecimal.ZERO);
                    advanceVo.setUnpay_amount_total(BigDecimal.ZERO);
                } else {
                    // 计算实际分配的中止金额（取可中止金额和剩余中止金额的较小值）
                    BigDecimal allocatedStopAmount = remainingStopAmount.compareTo(availableStopAmount) <= 0 
                        ? remainingStopAmount : availableStopAmount;
                    
                    // 更新字段值
                    advanceVo.setStoppay_amount_total(allocatedStopAmount);
                    advanceVo.setPaying_amount_total(BigDecimal.ZERO);
                    advanceVo.setUnpay_amount_total(payableAmount.subtract(paidAmount).subtract(allocatedStopAmount));
                    
                    // 减少剩余中止金额
                    remainingStopAmount = remainingStopAmount.subtract(allocatedStopAmount);
                }
                
                // 1.4 更新数据库记录
                BApSourceAdvanceEntity entity = new BApSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bApSourceAdvanceMapper.updateById(entity);
                
                log.debug("更新应付账款源单记录中止金额，ap_id: {}, source_advance_id: {}, stoppay_amount: {}", 
                    apId, advanceVo.getId(), advanceVo.getStoppay_amount_total());
            }
        }
    }

    /**
     * 参数校验，校验bo至少有一个字段有值，否则抛异常
     */
    private void validateTotalDataRecalculateBo(TotalDataRecalculateBo bo) {
        if (bo == null) {
            throw new BusinessException("参数不能为空");
        }
        boolean hasValue = false;
        try {
            Field[] fields = bo.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(bo);
                if (value != null) {
                    if (value instanceof List) {
                        if (!((List<?>) value).isEmpty()) {
                            hasValue = true;
                            break;
                        }
                    } else if (value instanceof String) {
                        if (!((String) value).isBlank()) {
                            hasValue = true;
                            break;
                        }
                    } else {
                        hasValue = true;
                        break;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new BusinessException("参数校验异常");
        }
        if (!hasValue) {
            throw new BusinessException("请至少输入一个查询条件");
        }
    }

    /**
     * 业务分支选择，收集合同ID
     */
    private List<Integer> collectContractIds(TotalDataRecalculateBo bo) {
        if (bo.getPoContractId() != null || (bo.getPoContractIds() != null && !bo.getPoContractIds().isEmpty())
                || (bo.getPoContractCode() != null && !bo.getPoContractCode().isBlank())
                || (bo.getPoContractCodes() != null && !bo.getPoContractCodes().isEmpty())) {
            return getContractIdsFromPoContract(bo);
        } else if (bo.getPoOrderId() != null || (bo.getPoOrderIds() != null && !bo.getPoOrderIds().isEmpty())
                || (bo.getPoOrderCode() != null && !bo.getPoOrderCode().isBlank())
                || (bo.getPoOrderCodes() != null && !bo.getPoOrderCodes().isEmpty())) {
            return getContractIdsFromPoOrder(bo);
        } else if (bo.getApId() != null || (bo.getApIds() != null && !bo.getApIds().isEmpty())
                || (bo.getApCode() != null && !bo.getApCode().isBlank())
                || (bo.getApCodes() != null && !bo.getApCodes().isEmpty())) {
            return getContractIdsFromAp(bo);
        } else if (bo.getApPayId() != null || (bo.getApPayIds() != null && !bo.getApPayIds().isEmpty())
                || (bo.getApPayCode() != null && !bo.getApPayCode().isBlank())
                || (bo.getApPayCodes() != null && !bo.getApPayCodes().isEmpty())) {
            return getContractIdsFromApPay(bo);
        } else if (bo.getInPlanId() != null || (bo.getInPlanIds() != null && !bo.getInPlanIds().isEmpty())) {
            return getContractIdsFromInPlan(bo);
        } else if (bo.getInboundId() != null || (bo.getInboundIds() != null && !bo.getInboundIds().isEmpty())) {
            return getContractIdsFromInbound(bo);
        } else if (bo.getPoSettlementId() != null || (bo.getPoSettlementIds() != null && !bo.getPoSettlementIds().isEmpty())) {
            return getContractIdsFromPoSettlement(bo);
        } else if (bo.getPoRefundId() != null || (bo.getPoRefundIds() != null && !bo.getPoRefundIds().isEmpty())) {
            return getContractIdsFromPoRefund(bo);
        } else if (bo.getCargoRightTransferId() != null || (bo.getCargoRightTransferIds() != null && !bo.getCargoRightTransferIds().isEmpty())) {
            return getContractIdsFromCargoRightTransfer(bo);
        }
        return new ArrayList<>();
    }

    /**
     * 采购合同分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromPoContract(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getPoContractId() != null) {
            contractIdSet.add(bo.getPoContractId());
        }
        if (bo.getPoContractCode() != null && !bo.getPoContractCode().isBlank()) {
            PoContractVo vo = bPoContractMapper.selectByCode(bo.getPoContractCode());
            if (vo != null && vo.getId() != null) {
                contractIdSet.add(vo.getId());
            }
        }
        if (bo.getPoContractIds() != null && !bo.getPoContractIds().isEmpty()) {
            contractIdSet.addAll(bo.getPoContractIds());
        }
        if (bo.getPoContractCodes() != null && !bo.getPoContractCodes().isEmpty()) {
            for (String code : bo.getPoContractCodes()) {
                if (code != null && !code.isBlank()) {
                    PoContractVo vo = bPoContractMapper.selectByCode(code);
                    if (vo != null && vo.getId() != null) {
                        contractIdSet.add(vo.getId());
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 采购订单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromPoOrder(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getPoOrderId() != null) {
            PoOrderVo vo = bPoOrderMapper.selectId(bo.getPoOrderId());
            if (vo != null && vo.getPo_contract_id() != null) {
                contractIdSet.add(vo.getPo_contract_id());
            }
        }
        if (bo.getPoOrderCode() != null && !bo.getPoOrderCode().isBlank()) {
            PoOrderVo vo = bPoOrderMapper.selectByCode(bo.getPoOrderCode());
            if (vo != null && vo.getPo_contract_id() != null) {
                contractIdSet.add(vo.getPo_contract_id());
            }
        }
        if (bo.getPoOrderIds() != null && !bo.getPoOrderIds().isEmpty()) {
            for (Integer id : bo.getPoOrderIds()) {
                PoOrderVo vo = bPoOrderMapper.selectId(id);
                if (vo != null && vo.getPo_contract_id() != null) {
                    contractIdSet.add(vo.getPo_contract_id());
                }
            }
        }
        if (bo.getPoOrderCodes() != null && !bo.getPoOrderCodes().isEmpty()) {
            for (String code : bo.getPoOrderCodes()) {
                if (code != null && !code.isBlank()) {
                    PoOrderVo vo = bPoOrderMapper.selectByCode(code);
                    if (vo != null && vo.getPo_contract_id() != null) {
                        contractIdSet.add(vo.getPo_contract_id());
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * AP分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromAp(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getApId() != null) {
            List<BApSourceAdvanceVo> list = bApSourceAdvanceMapper.selectByApId(bo.getApId());
            if (list != null) {
                for (BApSourceAdvanceVo vo : list) {
                    if (vo.getPo_contract_id() != null) {
                        contractIdSet.add(vo.getPo_contract_id());
                    }
                }
            }
        }
        if (bo.getApCode() != null && !bo.getApCode().isBlank()) {
            List<BApSourceAdvanceVo> list = bApSourceAdvanceMapper.selectByCode(bo.getApCode());
            if (list != null) {
                for (BApSourceAdvanceVo vo : list) {
                    if (vo.getPo_contract_id() != null) {
                        contractIdSet.add(vo.getPo_contract_id());
                    }
                }
            }
        }
        if (bo.getApIds() != null && !bo.getApIds().isEmpty()) {
            for (Integer id : bo.getApIds()) {
                List<BApSourceAdvanceVo> list = bApSourceAdvanceMapper.selectByApId(id);
                if (list != null) {
                    for (BApSourceAdvanceVo vo : list) {
                        if (vo.getPo_contract_id() != null) {
                            contractIdSet.add(vo.getPo_contract_id());
                        }
                    }
                }
            }
        }
        if (bo.getApCodes() != null && !bo.getApCodes().isEmpty()) {
            for (String code : bo.getApCodes()) {
                if (code != null && !code.isBlank()) {
                    List<BApSourceAdvanceVo> list = bApSourceAdvanceMapper.selectByCode(code);
                    if (list != null) {
                        for (BApSourceAdvanceVo vo : list) {
                            if (vo.getPo_contract_id() != null) {
                                contractIdSet.add(vo.getPo_contract_id());
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 付款单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromApPay(TotalDataRecalculateBo bo) {
        LinkedHashSet<String> contractCodeSet = new LinkedHashSet<>();
        if (bo.getApPayId() != null) {
            List<BApPaySourceVo> list = bApPaySourceMapper.selectByApPayId(bo.getApPayId());
            if (list != null) {
                for (BApPaySourceVo vo : list) {
                    if (vo.getPo_contract_code() != null && !vo.getPo_contract_code().isBlank()) {
                        contractCodeSet.add(vo.getPo_contract_code());
                    }
                }
            }
        }
        if (bo.getApPayCode() != null && !bo.getApPayCode().isBlank()) {
            List<BApPaySourceVo> list = bApPaySourceMapper.selectByApPayCode(bo.getApPayCode());
            if (list != null) {
                for (BApPaySourceVo vo : list) {
                    if (vo.getPo_contract_code() != null && !vo.getPo_contract_code().isBlank()) {
                        contractCodeSet.add(vo.getPo_contract_code());
                    }
                }
            }
        }
        if (bo.getApPayIds() != null && !bo.getApPayIds().isEmpty()) {
            for (Integer id : bo.getApPayIds()) {
                List<BApPaySourceVo> list = bApPaySourceMapper.selectByApPayId(id);
                if (list != null) {
                    for (BApPaySourceVo vo : list) {
                        if (vo.getPo_contract_code() != null && !vo.getPo_contract_code().isBlank()) {
                            contractCodeSet.add(vo.getPo_contract_code());
                        }
                    }
                }
            }
        }
        if (bo.getApPayCodes() != null && !bo.getApPayCodes().isEmpty()) {
            for (String code : bo.getApPayCodes()) {
                if (code != null && !code.isBlank()) {
                    List<BApPaySourceVo> list = bApPaySourceMapper.selectByApPayCode(code);
                    if (list != null) {
                        for (BApPaySourceVo vo : list) {
                            if (vo.getPo_contract_code() != null && !vo.getPo_contract_code().isBlank()) {
                                contractCodeSet.add(vo.getPo_contract_code());
                            }
                        }
                    }
                }
            }
        }
        // 合同编号转合同ID
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        for (String code : contractCodeSet) {
            if (code != null && !code.isBlank()) {
                PoContractVo vo = bPoContractMapper.selectByContractCode(code);
                if (vo != null && vo.getId() != null) {
                    contractIdSet.add(vo.getId());
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 入库计划分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromInPlan(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getInPlanId() != null) {
            List<Integer> contractIds = bInPlanDetailMapper.selectContractIdsByInPlanId(bo.getInPlanId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getInPlanIds() != null && !bo.getInPlanIds().isEmpty()) {
            for (Integer inPlanId : bo.getInPlanIds()) {
                List<Integer> contractIds = bInPlanDetailMapper.selectContractIdsByInPlanId(inPlanId);
                if (contractIds != null) {
                    contractIdSet.addAll(contractIds);
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 入库单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromInbound(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getInboundId() != null) {
            List<Integer> contractIds = bInMapper.selectContractIdsByInboundId(bo.getInboundId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getInboundIds() != null && !bo.getInboundIds().isEmpty()) {
            for (Integer inboundId : bo.getInboundIds()) {
                List<Integer> contractIds = bInMapper.selectContractIdsByInboundId(inboundId);
                if (contractIds != null) {
                    contractIdSet.addAll(contractIds);
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 采购结算分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromPoSettlement(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getPoSettlementId() != null) {
            List<Integer> contractIds = bPoSettlementMapper.selectContractIdsBySettlementId(bo.getPoSettlementId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getPoSettlementIds() != null && !bo.getPoSettlementIds().isEmpty()) {
            for (Integer settlementId : bo.getPoSettlementIds()) {
                List<Integer> contractIds = bPoSettlementMapper.selectContractIdsBySettlementId(settlementId);
                if (contractIds != null) {
                    contractIdSet.addAll(contractIds);
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 退款分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromPoRefund(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        LinkedHashSet<String> contractCodeSet = new LinkedHashSet<>();
        
        if (bo.getPoRefundId() != null) {
            try {
                // 使用现有的selectId方法查询退款信息，获取合同编号
                BApReFundVo refundVo = bApReFundMapper.selectId(bo.getPoRefundId());
                if (refundVo != null && refundVo.getPo_contract_code() != null && !refundVo.getPo_contract_code().isBlank()) {
                    contractCodeSet.add(refundVo.getPo_contract_code());
                }
                log.debug("处理退款ID: {} 的合同关联查询，找到合同编号: {}", bo.getPoRefundId(), refundVo != null ? refundVo.getPo_contract_code() : "null");
            } catch (Exception e) {
                log.warn("查询退款ID {} 的合同关联时出现异常: {}", bo.getPoRefundId(), e.getMessage());
            }
        }
        
        if (bo.getPoRefundIds() != null && !bo.getPoRefundIds().isEmpty()) {
            for (Integer refundId : bo.getPoRefundIds()) {
                try {
                    // 使用现有的selectId方法查询退款信息，获取合同编号
                    BApReFundVo refundVo = bApReFundMapper.selectId(refundId);
                    if (refundVo != null && refundVo.getPo_contract_code() != null && !refundVo.getPo_contract_code().isBlank()) {
                        contractCodeSet.add(refundVo.getPo_contract_code());
                    }
                    log.debug("处理退款ID: {} 的合同关联查询，找到合同编号: {}", refundId, refundVo != null ? refundVo.getPo_contract_code() : "null");
                } catch (Exception e) {
                    log.warn("查询退款ID {} 的合同关联时出现异常: {}", refundId, e.getMessage());
                }
            }
        }
        
        // 合同编号转合同ID
        for (String contractCode : contractCodeSet) {
            if (contractCode != null && !contractCode.isBlank()) {
                PoContractVo contractVo = bPoContractMapper.selectByContractCode(contractCode);
                if (contractVo != null && contractVo.getId() != null) {
                    contractIdSet.add(contractVo.getId());
                }
            }
        }
        
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 货权转移分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromCargoRightTransfer(TotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        
        if (bo.getCargoRightTransferId() != null) {
            try {
                // 通过货权转移ID查询货权转移信息，获取合同ID
                BPoCargoRightTransferVo cargoTransferVo = bPoCargoRightTransferMapper.selectId(bo.getCargoRightTransferId());
                if (cargoTransferVo != null && cargoTransferVo.getPo_contract_id() != null) {
                    contractIdSet.add(cargoTransferVo.getPo_contract_id());
                }
                log.debug("处理货权转移ID: {} 的合同关联查询，找到合同ID: {}", bo.getCargoRightTransferId(), 
                    cargoTransferVo != null ? cargoTransferVo.getPo_contract_id() : "null");
            } catch (Exception e) {
                log.warn("查询货权转移ID {} 的合同关联时出现异常: {}", bo.getCargoRightTransferId(), e.getMessage());
            }
        }
        
        if (bo.getCargoRightTransferIds() != null && !bo.getCargoRightTransferIds().isEmpty()) {
            for (Integer cargoTransferId : bo.getCargoRightTransferIds()) {
                try {
                    BPoCargoRightTransferVo cargoTransferVo = bPoCargoRightTransferMapper.selectId(cargoTransferId);
                    if (cargoTransferVo != null && cargoTransferVo.getPo_contract_id() != null) {
                        contractIdSet.add(cargoTransferVo.getPo_contract_id());
                    }
                    log.debug("处理货权转移ID: {} 的合同关联查询，找到合同ID: {}", cargoTransferId, 
                        cargoTransferVo != null ? cargoTransferVo.getPo_contract_id() : "null");
                } catch (Exception e) {
                    log.warn("查询货权转移ID {} 的合同关联时出现异常: {}", cargoTransferId, e.getMessage());
                }
            }
        }
        
        log.debug("getContractIdsFromCargoRightTransfer 最终合同ID集合: {}", contractIdSet);
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 按采购合同编号重新生成Total数据
     * @param code 采购合同编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoContractCode(String code) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoContractCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按采购合同id重新生成Total数据
     * @param id 采购合同id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoContractId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoContractId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按采购订单编号重新生成Total数据
     * @param code 采购订单编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoOrderCode(String code) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoOrderCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按采购订单id重新生成Total数据
     * @param id 采购订单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoOrderId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoOrderId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按应付账款id重新生成Total数据
     * @param id 应付账款id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByApId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setApId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按应付账款编号重新生成Total数据
     * @param code 应付账款编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByApCode(String code) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setApCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按付款单id重新生成Total数据
     * @param id 付款单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByApPayId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setApPayId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按付款单编号重新生成Total数据
     * @param code 付款单编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByApPayCode(String code) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setApPayCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 处理采购订单total数据（b_po_order_total）
     * 根据合同ID获取关联的采购订单ID集合，然后调用BPoOrderTotalMapper.updateTotalData更新
     * 
     * @param contractId 合同ID
     */
    private void processPoOrderTotalDataByContractId(Integer contractId) {
        // 1. 根据合同ID查询采购订单，获取采购订单ID集合
        LinkedHashSet<Integer> poOrderIdSet = new LinkedHashSet<>();
        List<PoOrderVo> poOrderList = bPoOrderMapper.selectByPoContractId(contractId);
        if (poOrderList != null && !poOrderList.isEmpty()) {
            for (PoOrderVo vo : poOrderList) {
                if (vo.getId() != null) {
                    poOrderIdSet.add(vo.getId());
                }
            }
        }

        // 2. 如果有采购订单，先确保记录存在，然后调用updateTotalData更新数据
        if (!poOrderIdSet.isEmpty()) {
            // 2.1 检查并插入不存在的记录
            for (Integer poOrderId : poOrderIdSet) {
                BPoOrderTotalVo existingRecord = bPoOrderTotalMapper.selectByPoId(poOrderId);
                if (existingRecord == null) {
                    // 如果记录不存在，则新增一条记录，只设置po_order_id
                    BPoOrderTotalEntity newEntity = new BPoOrderTotalEntity();
                    newEntity.setPo_order_id(poOrderId);
                    bPoOrderTotalMapper.insert(newEntity);
                    log.debug("新增采购订单总计数据记录，po_order_id: {}", poOrderId);
                }
            }
            
            // 2.2 更新采购订单基础汇总数据 (b_po_order_total)
            log.debug("开始更新采购订单总计数据，订单数量: {}", poOrderIdSet.size());
            
            // 2.2.1 更新订单基础总计数据（金额、税额、数量）
            int orderTotalResult = bPoOrderTotalMapper.updatePoOrderTotalData(poOrderIdSet);
            log.debug("更新采购订单基础总计数据完成，影响行数: {}", orderTotalResult);
            
            // 2.2.2 更新预付款相关数据
            int advanceResult = bPoOrderTotalMapper.updateAdvanceAmountTotalData(poOrderIdSet);
            log.debug("更新预付款数据完成，影响行数: {}", advanceResult);
            
            // 2.2.3 更新已付款总金额数据（目前仅考虑预付款来源：b_ap_source_advance）
            int paidResult = bPoOrderTotalMapper.updatePaidTotalData(poOrderIdSet);
            log.debug("更新已付款总金额数据完成，影响行数: {}", paidResult);
            
            // 2.2.4 更新退款数据（从b_ap_refund_total汇总到采购订单总计表）
            int refundResult = bPoOrderTotalMapper.updateRefundAmountTotalData(poOrderIdSet);
            log.debug("更新退款数据完成，影响行数: {}", refundResult);
            
            // 2.2.4.5 更新货权转移明细汇总数据（从b_po_cargo_right_transfer_detail汇总到采购订单明细汇总表）
            int cargoRightTransferDetailResult = poOrderDetailTotalMapper.updateCargoRightTransferTotalByPoOrderIds(poOrderIdSet);
            log.debug("更新货权转移明细汇总数据完成，影响行数: {}", cargoRightTransferDetailResult);
            
            // 2.2.5 更新货权转移数据（从b_po_cargo_right_transfer_total汇总到采购订单总计表）
            int cargoRightTransferResult = bPoOrderTotalMapper.updateCargoRightTransferTotalData(poOrderIdSet);
            log.debug("更新货权转移数据完成，影响行数: {}", cargoRightTransferResult);

            // 2.3 更新采购订单明细汇总数据 (b_po_order_detail_total)
            log.debug("开始更新采购订单明细汇总数据");
            
            // 2.3.0 确保明细汇总记录存在
            int insertResult = poOrderDetailTotalMapper.insertMissingRecords(poOrderIdSet);
            log.debug("插入缺失的明细汇总记录数: {}", insertResult);
            
            // 2.3.1 更新入库相关汇总数据（处理中、未处理、已处理、取消等数据）
            int detailInboundResult = poOrderDetailTotalMapper.updateInboundTotalByPoOrderIds(poOrderIdSet);
            log.debug("更新明细入库汇总数据完成，影响行数: {}", detailInboundResult);
            
            // 2.3.2 更新明细级别待结算数量汇总
            int detailSettleResult = poOrderDetailTotalMapper.updateSettleCanQtyTotal(poOrderIdSet);
            log.debug("更新明细待结算数量汇总完成，影响行数: {}", detailSettleResult);

            // 2.4 回写订单级别汇总数据 (b_po_order_total)
            log.debug("开始回写订单级别汇总数据");
            
            // 2.4.1 从明细汇总表回写入库计划和货权转移相关数据到订单总计表
            int inPlanResult = bPoOrderTotalMapper.updateInboundAndCargoRightTransferTotalData(poOrderIdSet);
            log.debug("回写入库计划和货权转移数据完成，影响行数: {}", inPlanResult);
            
            // 2.4.2 更新订单级别待结算数量汇总
            int orderSettleResult = bPoOrderTotalMapper.updateSettleCanQtyTotal(poOrderIdSet);
            log.debug("更新订单待结算数量汇总完成，影响行数: {}", orderSettleResult);
            
            log.debug("采购订单总计数据更新完成，订单ID集合: {}", poOrderIdSet);
        }
    }

    /**
     * 处理采购合同total数据（b_po_contract_total）
     * 根据合同ID汇总其下所有采购订单的总计数据到合同级别
     * 步骤：
     * 1. 检查并确保b_po_contract_total记录存在
     * 2. 从b_po_order_total汇总advance_unpay_total, advance_paid_total, advance_pay_total, advance_stoppay_total到合同级别
     * 
     * @param contractId 合同ID
     */
    /**
     * 处理采购合同级预付款数据汇总（SQL实现版）
     * 根据合同ID汇总其下所有采购订单的预付款相关字段到b_po_contract_total表
     * 
     * 采用SQL实现的优势：
     * 1. 性能优势：直接在数据库层面进行聚合计算，避免大量数据传输
     * 2. 事务安全：减少并发更新时的数据不一致风险
     * 3. 代码简洁：避免复杂的Java循环和累加逻辑
     * 
     * 步骤：
     * 1. 检查并确保b_po_contract_total记录存在
     * 2. 使用SQL批量汇总和更新合同级预付款数据
     * 
     * @param contractId 合同ID
     */
    private void processPoContractTotalDataByContractId(Integer contractId) {
        // 1. 检查并确保记录存在
        BPoContractTotalVo existingRecord = bPoContractTotalMapper.selectByPoContractId(contractId);
        if (existingRecord == null) {
            // 如果记录不存在，则新增一条记录，只设置po_contract_id
            BPoContractTotalEntity newEntity = new BPoContractTotalEntity();
            newEntity.setPo_contract_id(contractId);
            bPoContractTotalMapper.insert(newEntity);
            log.debug("新增采购合同总计数据记录，po_contract_id: {}", contractId);
        }
        
        // 2. 使用SQL批量汇总和更新合同级预付款数据
        int updateResult = bPoContractTotalMapper.updateContractAdvanceTotalData(contractId);
        if (updateResult > 0) {
            log.debug("SQL汇总更新采购合同总计数据成功，po_contract_id: {}, 更新记录数: {}", contractId, updateResult);
        } else {
            log.warn("SQL汇总更新采购合同总计数据失败，po_contract_id: {}", contractId);
        }
    }

    /**
     * 处理采购结算汇总数据（b_po_settlement_total）
     * 步骤：
     * 1. 通过contractId从b_po_settlement_detail_source_inbound表获取结算ID集合
     * 2. 使用结算ID集合批量更新b_po_settlement_total表的汇总数据
     * 
     * @param contractId 合同ID
     */
    private void processPoSettlementTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取到b_po_settlement_detail_source_inbound表的数据，生成对应的LinkedHashSet<Integer> poSettlementIdSet
        LinkedHashSet<Integer> poSettlementIdSet = new LinkedHashSet<>();
        List<Integer> settlementIdList = bPoSettlementTotalMapper.selectSettlementIdsByContractId(contractId);
        if (settlementIdList != null && !settlementIdList.isEmpty()) {
            poSettlementIdSet.addAll(settlementIdList);
        }

        // 2. 如果有结算ID，先插入缺失的数据，再进行批量更新
        if (!poSettlementIdSet.isEmpty()) {
            try {
                // 先插入缺失的结算汇总记录
                int insertResult = bPoSettlementTotalMapper.insertMissingSettlementTotal(poSettlementIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的采购结算汇总数据成功，插入行数: {}, 结算ID集合: {}", insertResult, poSettlementIdSet);
                }
                
                // 然后批量更新汇总数据
                int updateResult = bPoSettlementTotalMapper.batchUpdateSettlementTotal(poSettlementIdSet);
                log.debug("批量更新采购结算汇总数据成功，影响行数: {}, 结算ID集合: {}", updateResult, poSettlementIdSet);
            } catch (Exception e) {
                log.error("处理采购结算汇总数据失败，结算ID集合: {}, 错误信息: {}", poSettlementIdSet, e.getMessage(), e);
                throw new BusinessException(e);
            }
        } else {
            log.debug("合同ID {} 下未找到采购结算数据", contractId);
        }
    }

    /**
     * 处理入库计划total数据（b_in_plan_detail、b_in_plan_total）
     * 步骤：
     * 1. 通过contractId获取到b_in_plan_detail表的数据，生成对应的LinkedHashSet<Integer> inPlanIdSet
     * 2. 更新b_in_plan_detail：调用b_in_plan_detail的mapper方法updateProcessingQtyByInPlanId，传入inPlanIdSet，完成更新
     * 3. 更新或者插入b_in_plan_total：调用b_in_plan_total的mapper方法updateInPlanTotalData，传入inPlanIdSet，完成更新或者插入
     * 
     * @param contractId 合同ID
     */
    private void processInPlanTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取到b_in_plan_detail表的数据，生成对应的LinkedHashSet<Integer> inPlanIdSet
        LinkedHashSet<Integer> inPlanIdSet = new LinkedHashSet<>();
        List<BInPlanDetailEntity> inPlanDetailList = bInPlanDetailMapper.selectByContractId(contractId);
        if (inPlanDetailList != null && !inPlanDetailList.isEmpty()) {
            for (BInPlanDetailEntity detail : inPlanDetailList) {
                if (detail.getIn_plan_id() != null) {
                    inPlanIdSet.add(detail.getIn_plan_id());
                }
            }
        }

        // 2. 如果有入库计划ID，进行后续处理
        if (!inPlanIdSet.isEmpty()) {
            // 2.1 更新b_in_plan_detail：调用updateProcessingQtyByInPlanId方法
            try {
                bInPlanDetailMapper.updateProcessingQtyByInPlanId(inPlanIdSet);
                log.debug("更新入库计划明细处理数量统计成功，入库计划ID集合: {}", inPlanIdSet);
            } catch (Exception e) {
                log.error("更新入库计划明细处理数量统计失败，入库计划ID集合: {}, 错误信息: {}", inPlanIdSet, e.getMessage(), e);
            }

            // 2.2 确保b_in_plan_total记录存在（先插入不存在的记录）
            for (Integer inPlanId : inPlanIdSet) {
                BInPlanTotalVo existingRecord = bInPlanTotalMapper.selectByInPlanId(inPlanId);
                if (existingRecord == null) {
                    // 如果记录不存在，则新增一条记录，只设置in_plan_id
                    BInPlanTotalEntity newEntity = new BInPlanTotalEntity();
                    newEntity.setIn_plan_id(inPlanId);
                    bInPlanTotalMapper.insert(newEntity);
                    log.debug("新增入库计划汇总数据记录，in_plan_id: {}", inPlanId);
                }
            }

            // 2.3 更新b_in_plan_total：调用updateInPlanTotalData方法
            try {
                int updateResult = bInPlanTotalMapper.updateInPlanTotalData(inPlanIdSet);
                log.debug("更新入库计划汇总数据成功，影响行数: {}, 入库计划ID集合: {}", updateResult, inPlanIdSet);
            } catch (Exception e) {
                log.error("更新入库计划汇总数据失败，入库计划ID集合: {}, 错误信息: {}", inPlanIdSet, e.getMessage(), e);
            }
        } else {
            log.debug("合同ID {} 下未找到入库计划明细数据", contractId);
        }
    }

    /**
     * 按入库计划id重新生成Total数据
     * @param id 入库计划id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPlanId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setInPlanId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按入库单id重新生成Total数据
     * @param id 入库单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByInboundId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setInboundId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按采购结算id重新生成Total数据
     * @param id 采购结算id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoSettlementId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoSettlementId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按退款id重新生成Total数据
     * @param id 退款id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoRefundId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setPoRefundId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按退款编号重新生成Total数据
     * @param code 退款编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByPoRefundCode(String code) {
        if (code == null || code.isBlank()) {
            log.warn("退款编号不能为空");
            return false;
        }
        
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        
        try {
            // 使用新添加的selectIdByCode方法查询退款ID
            Integer refundId = bApReFundMapper.selectIdByCode(code);
            if (refundId != null) {
                bo.setPoRefundId(refundId);
                log.info("根据退款编号 {} 查询到退款ID: {}", code, refundId);
                return reCalculateAllTotalData(bo);
            } else {
                log.warn("未找到退款编号为 {} 的退款记录", code);
                return false;
            }
            
        } catch (Exception e) {
            log.error("根据退款编号 {} 处理Total数据重计算时出现异常: {}", code, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 按货权转移id重新生成Total数据
     * @param id 货权转移id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByCargoRightTransferId(Integer id) {
        TotalDataRecalculateBo bo = new TotalDataRecalculateBo();
        bo.setCargoRightTransferId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按货权转移编号重新生成Total数据
     * @param code 货权转移编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByCargoRightTransferCode(String code) {
        if (code == null || code.isBlank()) {
            throw new BusinessException("货权转移编号不能为空");
        }
        
        Integer cargoRightTransferId = bPoCargoRightTransferMapper.selectIdByCode(code);
        if (cargoRightTransferId == null) {
            throw new BusinessException("未找到货权转移记录，编号：" + code);
        }
        
        return reCalculateAllTotalDataByCargoRightTransferId(cargoRightTransferId);
    }

    /**
     * 处理货权转移总计数据（b_po_cargo_right_transfer_total）
     * 步骤：
     * 1. 根据合同ID查询货权转移ID集合
     * 2. 插入缺失的Total记录（处理历史数据）
     * 3. 批量更新货权转移总计数据，从明细表同步数据
     *
     * @param contractId 合同ID
     */
    private void processCargoRightTransferTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取相关的货权转移ID集合
        LinkedHashSet<Integer> cargoRightTransferIdSet = new LinkedHashSet<>();
        List<Integer> cargoTransferIdList = bPoCargoRightTransferTotalMapper.selectCargoRightTransferIdsByContractId(contractId);
        if (cargoTransferIdList != null && !cargoTransferIdList.isEmpty()) {
            cargoRightTransferIdSet.addAll(cargoTransferIdList);
        }

        // 2. 如果有货权转移ID，先插入缺失的Total记录，再进行批量更新
        if (!cargoRightTransferIdSet.isEmpty()) {
            try {
                // 2.1 插入缺失的Total记录（处理历史数据）
                Integer insertResult = bPoCargoRightTransferTotalMapper.insertMissingCargoRightTransferTotal(cargoRightTransferIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的货权转移总计数据成功，插入行数: {}, 货权转移ID集合: {}", insertResult, cargoRightTransferIdSet);
                }
                
                // 2.2 批量更新货权转移总计数据，从明细表同步数据
                int updateResult = bPoCargoRightTransferTotalMapper.batchUpdateCargoRightTransferTotalFromDetail(cargoRightTransferIdSet);
                log.debug("批量更新货权转移总计数据成功，影响行数: {}, 货权转移ID集合: {}", updateResult, cargoRightTransferIdSet);
            } catch (Exception e) {
                log.error("处理货权转移总计数据失败，货权转移ID集合: {}, 错误信息: {}", cargoRightTransferIdSet, e.getMessage(), e);
                throw new BusinessException("处理货权转移总计数据失败: " + e.getMessage());
            }
        } else {
            log.debug("合同ID {} 下未找到货权转移数据", contractId);
        }
    }

}