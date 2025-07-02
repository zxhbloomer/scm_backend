package com.xinyirun.scm.core.system.serviceimpl.base.v1.common.total;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.busniess.ap.BApTotalEntity;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.pocontract.BPoContractTotalEntity;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderTotalEntity;
import com.xinyirun.scm.bean.system.bo.fund.total.TotalDataRecalculateBo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApTotalVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPaySourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPaySourceVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayVo;
import com.xinyirun.scm.bean.system.vo.business.pocontract.BPoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.BPoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.business.ap.BApDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.ap.BApTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPaySourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPaySourceMapper;
import com.xinyirun.scm.core.system.mapper.business.pocontract.BPoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.pocontract.BPoContractTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.poorder.BPoOrderTotalMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.wms.inplan.BInPlanTotalMapper;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanTotalEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanTotalVo;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonPoTotalService;
import com.xinyirun.scm.core.system.service.business.ap.IBApTotalService;
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
    private BApDetailMapper bApDetailMapper;

    @Autowired
    private BInPlanDetailMapper bInPlanDetailMapper;

    @Autowired
    private BInPlanTotalMapper bInPlanTotalMapper;

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
             * 1. 处理入库方面的total数据（b_in_plan_detail、b_in_plan_total）
             *    - b_in_plan_detail: 更新processing_qty, processing_weight, processing_volume, unprocessed_qty, unprocessed_weight, unprocessed_volume, processed_qty, processed_weight, processed_volume
             *    - b_in_plan_total: 汇总计划级别的processing_qty_total, processing_weight_total, processing_volume_total, unprocessed_qty_total, unprocessed_weight_total, unprocessed_volume_total, processed_qty_total, processed_weight_total, processed_volume_total
             * 2. 处理付款单total数据（b_ap_pay、b_ap_source_advance）
             *    - b_ap_pay: 更新付款单总金额字段
             *    - b_ap_source_advance: payable_amount_total, paid_amount_total, paying_amount_total, unpay_amount_total
             * 3. 处理应付账款total数据（b_ap_total、b_ap_detail、b_ap_source_advance）
             *    - b_ap_total: payable_amount_total, paid_amount_total, paying_amount_total, stoppay_amount_total, cancelpay_amount_total, unpay_amount_total
             *    - b_ap_detail: 调用updateTotalData更新总计字段
             *    - b_ap_source_advance: stoppay_amount_total, cancelpay_amount_total（中止和作废分配）
             * 4. 处理采购订单total数据（b_po_order_total）
             *    - updatePoOrderTotalData: 更新采购订单总计数据
             *    - updateAdvanceAmountTotalData: 更新预付款总计数据
             *    - updatePaidTotalData: 更新已付款总金额数据
             * 5. 处理采购合同total数据（b_po_contract_total）
             *    - updateContractAdvanceTotalData: 汇总合同下所有订单的预付款数据
             */
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
            BApPayVo payingVo = bApPayMapper.getSumAmount(apId, DictConstant.DICT_B_AP_PAY_BILL_STATUS_ZERO);
            if (payingVo != null) {
                apTotalVo.setPaying_amount_total(payingVo.getPayable_amount_total());
            } else {
                apTotalVo.setPaying_amount_total(BigDecimal.ZERO);
            }

            // 获取已中止付款总金额和作废付款总金额
            // 2.4.1、判断应付账款的付款状态是否为已中止
            // 查询应付账款的pay_status和status
            BApVo apVo = bApMapper.selectId(apTotalVo.getAp_id());
            if (apVo != null && DictConstant.DICT_B_AP_PAY_BILL_STATUS_STOP.equals(apVo.getPay_status())) {
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
            
            // 2.2 更新总计数据
            int updResult1 = bPoOrderTotalMapper.updatePoOrderTotalData(poOrderIdSet);
            log.debug("更新采购订单总计数据成功，影响行数: {}", updResult1);
            // 更新预付款数据
            int updResult2 = bPoOrderTotalMapper.updateAdvanceAmountTotalData(poOrderIdSet);
            log.debug("更新预付款数据成功，影响行数: {}", updResult2);
            // 2.3 更新累计付款
            // 目前：只有预付款，所以源单只考虑b_ap_source_advance
            int updResult3 = bPoOrderTotalMapper.updatePaidTotalData(poOrderIdSet);
            log.debug("更新已付款总金额数据成功，影响行数: {}", updResult3);

            // 2.4 更新入库计划数据
            int updResult4 =bPoOrderTotalMapper.updateInPlanTotalData(poOrderIdSet);
            log.debug("更新入库计划数据成功，影响行数: {}", updResult4);
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

}