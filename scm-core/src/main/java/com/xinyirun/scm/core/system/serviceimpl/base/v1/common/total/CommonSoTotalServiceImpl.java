package com.xinyirun.scm.core.system.serviceimpl.base.v1.common.total;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.ar.BArSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.business.so.ar.BArTotalEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractTotalEntity;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderTotalEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanTotalEntity;
import com.xinyirun.scm.bean.system.bo.fund.total.SoTotalDataRecalculateBo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveSourceVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.so.ar.BArTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arrefund.BArReFundMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arrefund.BArReFundTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveSourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.so.arreceive.BArReceiveSourceMapper;
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractMapper;
import com.xinyirun.scm.core.system.mapper.business.so.socontract.BSoContractTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderDetailTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderMapper;
import com.xinyirun.scm.core.system.mapper.business.so.soorder.BSoOrderTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.so.settlement.BSoSettlementTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanTotalMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.so.settlement.BSoSettlementMapper;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanTotalVo;
import com.xinyirun.scm.core.system.service.base.v1.common.total.ICommonSoTotalService;
import com.xinyirun.scm.core.system.service.business.so.ar.IBArTotalService;
import com.xinyirun.scm.core.system.service.business.so.arrefund.IBArReFundTotalService;
import com.xinyirun.scm.core.system.service.business.so.cargo_right_transfer.IBSoCargoRightTransferTotalService;
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferMapper;
import com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer.BSoCargoRightTransferTotalMapper;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferVo;
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
 * 销售合同表-财务数据汇总 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Service
public class CommonSoTotalServiceImpl extends ServiceImpl<BSoContractTotalMapper, BSoContractTotalEntity> implements ICommonSoTotalService {

    private static final Logger log = LoggerFactory.getLogger(CommonSoTotalServiceImpl.class);

    @Autowired
    private BSoContractMapper bSoContractMapper;

    @Autowired
    private BSoContractTotalMapper bSoContractTotalMapper;

    @Autowired
    private BSoOrderMapper bSoOrderMapper;

    @Autowired
    private BSoOrderTotalMapper bSoOrderTotalMapper;

    @Autowired
    private BArSourceAdvanceMapper bArSourceAdvanceMapper;

    @Autowired
    private BArReceiveDetailMapper bArReceiveDetailMapper;

    @Autowired
    private BArReceiveSourceMapper bArReceiveSourceMapper;

    @Autowired
    private BArReceiveMapper bArReceiveMapper;

    @Autowired
    private BArReceiveSourceAdvanceMapper bArReceiveSourceAdvanceMapper;

    @Autowired
    private BArTotalMapper bArTotalMapper;

    @Autowired
    private BArMapper bArMapper;

    @Autowired
    private IBArTotalService bArTotalService;

    @Autowired
    private BArReFundMapper bArReFundMapper;

    @Autowired
    private BArReFundTotalMapper bArReFundTotalMapper;

    @Autowired
    private IBArReFundTotalService bArReFundTotalService;

    @Autowired
    private BArDetailMapper bArDetailMapper;

    @Autowired
    private BOutPlanDetailMapper bOutPlanDetailMapper;

    @Autowired
    private BOutPlanTotalMapper bOutPlanTotalMapper;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private BSoOrderDetailTotalMapper bSoOrderDetailTotalMapper;

    @Autowired
    private BSoSettlementMapper bSoSettlementMapper;

    @Autowired
    private BSoSettlementTotalMapper bSoSettlementTotalMapper;

    @Autowired
    private BSoCargoRightTransferMapper bSoCargoRightTransferMapper;

    @Autowired
    private BSoCargoRightTransferTotalMapper bSoCargoRightTransferTotalMapper;

    @Autowired
    private IBSoCargoRightTransferTotalService bSoCargoRightTransferTotalService;

    /**
     * 重新计算所有的财务数据，最终得到合同ID集合 contractIdSet
     * @param bo 查询条件
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalData(SoTotalDataRecalculateBo bo) {
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
             * 1. 处理应收退款total数据（b_ar_refund_total） - 优先处理
             * 2. 处理销售结算汇总数据（b_so_settlement_total）
             *    - 通过合同ID查询b_so_settlement_detail_source_outbound表获取结算ID集合
             *    - 批量更新b_so_settlement_total表的汇总数据：processing_qty, processing_weight, processing_volume, unprocessed_qty, unprocessed_weight, unprocessed_volume, processed_qty, processed_weight, processed_volume, planned_qty, planned_weight, planned_volume, planned_amount, settled_qty, settled_weight, settled_volume, settled_amount
             * 3. 处理出库方面的total数据（b_out_plan_detail、b_out_plan_total）
             *    - b_out_plan_detail: 更新processing_qty, processing_weight, processing_volume, unprocessed_qty, unprocessed_weight, unprocessed_volume, processed_qty, processed_weight, processed_volume
             *    - b_out_plan_total: 汇总计划级别的processing_qty_total, processing_weight_total, processing_volume_total, unprocessed_qty_total, unprocessed_weight_total, unprocessed_volume_total, processed_qty_total, processed_weight_total, processed_volume_total
             * 4. 处理收款单total数据（b_ar_receive、b_ar_source_advance）
             *    - b_ar_receive: 更新收款单总金额字段
             *    - b_ar_source_advance: receivable_amount_total, received_amount_total, receiving_amount_total, unreceive_amount_total
             * 5. 处理应收账款total数据（b_ar_total、b_ar_detail、b_ar_source_advance）
             *    - b_ar_total: receivable_amount_total, received_amount_total, receiving_amount_total, stopreceive_amount_total, cancelreceive_amount_total, unreceive_amount_total
             *    - b_ar_detail: 调用updateTotalData更新总计字段
             *    - b_ar_source_advance: stopreceive_amount_total, cancelreceive_amount_total（中止和作废分配）
             * 6. 处理销售订单total数据（b_so_order_total）
             *    - updateSoOrderTotalData: 更新销售订单总计数据
             *    - updateAdvanceAmountTotalData: 更新预付款总计数据
             *    - updateReceivedTotalData: 更新已收款总金额数据
             * 7. 处理销售合同total数据（b_so_contract_total）
             *    - updateContractAdvanceTotalData: 汇总合同下所有订单的预付款数据
             */
            // ===================== 处理应收退款total数据（b_ar_refund_total） - 优先处理 =====================
            processArRefundTotalDataByContractId(contractId);
            // ===================== 处理货权转移total数据（b_so_cargo_right_transfer_total） =====================
            processCargoRightTransferTotalDataByContractId(contractId);
            // ===================== 处理销售结算汇总数据（b_so_settlement_total） =====================
            processSoSettlementTotalDataByContractId(contractId);
            // ===================== 处理出库方面的total数据（b_out_plan_detail、b_out_plan_total） =====================
            processOutPlanTotalDataByContractId(contractId);
            // ===================== 处理收款total数据（b_ar_receive、b_ar_source_advance） =====================
            processReceiveTotalDataByContractId(contractId);
            // ===================== 处理应收账款total数据（b_ar_total、b_ar_detail、b_ar_source_advance） =====================
            processArTotalDataByContractId(contractId);
            // ===================== 处理销售订单total数据（b_so_order_total） =====================
            processSoOrderTotalDataByContractId(contractId);
            // ===================== 处理销售合同total数据（b_so_contract_total） =====================
            processSoContractTotalDataByContractId(contractId);
            // ===================== END =====================
        }
        return true;
    }

    /**
     * 按销售合同编号重新生成Total数据
     * @param code 销售合同编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoContractCode(String code) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoContractCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按销售合同id重新生成Total数据
     * @param id 销售合同id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoContractId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoContractId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按销售订单编号重新生成Total数据
     * @param code 销售订单编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoOrderCode(String code) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoOrderCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按销售订单id重新生成Total数据
     * @param id 销售订单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoOrderId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoOrderId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按应收账款id重新生成Total数据
     * @param id 应收账款id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByArId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setArId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按应收账款编号重新生成Total数据
     * @param code 应收账款编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByArCode(String code) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setArCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按收款单id重新生成Total数据
     * @param id 收款单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByArReceiveId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setArReceiveId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按收款单编号重新生成Total数据
     * @param code 收款单编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByArReceiveCode(String code) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setArReceiveCode(code);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按出库计划id重新生成Total数据
     * @param id 出库计划id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByOutPlanId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setOutPlanId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按出库单id重新生成Total数据
     * @param id 出库单id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataByOutboundId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setOutboundId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按销售结算id重新生成Total数据
     * @param id 销售结算id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoSettlementId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoSettlementId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按退款id重新生成Total数据
     * @param id 退款id
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoRefundId(Integer id) {
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        bo.setSoRefundId(id);
        return reCalculateAllTotalData(bo);
    }

    /**
     * 按退款编号重新生成Total数据
     * @param code 退款编号
     * @return 是否操作成功
     */
    @Override
    public Boolean reCalculateAllTotalDataBySoRefundCode(String code) {
        if (code == null || code.isBlank()) {
            log.warn("退款编号不能为空");
            return false;
        }
        
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
        
        try {
            // 使用新添加的selectIdByCode方法查询退款ID
            Integer refundId = bArReFundMapper.selectIdByCode(code);
            if (refundId != null) {
                bo.setSoRefundId(refundId);
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
        SoTotalDataRecalculateBo bo = new SoTotalDataRecalculateBo();
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
        
        Integer cargoRightTransferId = bSoCargoRightTransferMapper.selectIdByCode(code);
        if (cargoRightTransferId == null) {
            throw new BusinessException("未找到货权转移记录，编号：" + code);
        }
        
        return reCalculateAllTotalDataByCargoRightTransferId(cargoRightTransferId);
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
     * 参数校验，校验bo至少有一个字段有值，否则抛异常
     */
    private void validateTotalDataRecalculateBo(SoTotalDataRecalculateBo bo) {
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
    private List<Integer> collectContractIds(SoTotalDataRecalculateBo bo) {
        if (bo.getSoContractId() != null || (bo.getSoContractIds() != null && !bo.getSoContractIds().isEmpty())
                || (bo.getSoContractCode() != null && !bo.getSoContractCode().isBlank())
                || (bo.getSoContractCodes() != null && !bo.getSoContractCodes().isEmpty())) {
            return getContractIdsFromSoContract(bo);
        } else if (bo.getSoOrderId() != null || (bo.getSoOrderIds() != null && !bo.getSoOrderIds().isEmpty())
                || (bo.getSoOrderCode() != null && !bo.getSoOrderCode().isBlank())
                || (bo.getSoOrderCodes() != null && !bo.getSoOrderCodes().isEmpty())) {
            return getContractIdsFromSoOrder(bo);
        } else if (bo.getArId() != null || (bo.getArIds() != null && !bo.getArIds().isEmpty())
                || (bo.getArCode() != null && !bo.getArCode().isBlank())
                || (bo.getArCodes() != null && !bo.getArCodes().isEmpty())) {
            return getContractIdsFromAr(bo);
        } else if (bo.getArReceiveId() != null || (bo.getArReceiveIds() != null && !bo.getArReceiveIds().isEmpty())
                || (bo.getArReceiveCode() != null && !bo.getArReceiveCode().isBlank())
                || (bo.getArReceiveCodes() != null && !bo.getArReceiveCodes().isEmpty())) {
            return getContractIdsFromArReceive(bo);
        } else if (bo.getOutPlanId() != null || (bo.getOutPlanIds() != null && !bo.getOutPlanIds().isEmpty())) {
            return getContractIdsFromOutPlan(bo);
        } else if (bo.getOutboundId() != null || (bo.getOutboundIds() != null && !bo.getOutboundIds().isEmpty())) {
            return getContractIdsFromOutbound(bo);
        } else if (bo.getSoSettlementId() != null || (bo.getSoSettlementIds() != null && !bo.getSoSettlementIds().isEmpty())) {
            return getContractIdsFromSoSettlement(bo);
        } else if (bo.getSoRefundId() != null || (bo.getSoRefundIds() != null && !bo.getSoRefundIds().isEmpty())) {
            return getContractIdsFromSoRefund(bo);
        } else if (bo.getCargoRightTransferId() != null || (bo.getCargoRightTransferIds() != null && !bo.getCargoRightTransferIds().isEmpty())) {
            return getContractIdsFromCargoRightTransfer(bo);
        }
        return new ArrayList<>();
    }

    /**
     * 销售合同分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromSoContract(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getSoContractId() != null) {
            contractIdSet.add(bo.getSoContractId());
        }
        if (bo.getSoContractCode() != null && !bo.getSoContractCode().isBlank()) {
            BSoContractVo vo = bSoContractMapper.selectByCode(bo.getSoContractCode());
            if (vo != null && vo.getId() != null) {
                contractIdSet.add(vo.getId());
            }
        }
        if (bo.getSoContractIds() != null && !bo.getSoContractIds().isEmpty()) {
            contractIdSet.addAll(bo.getSoContractIds());
        }
        if (bo.getSoContractCodes() != null && !bo.getSoContractCodes().isEmpty()) {
            for (String code : bo.getSoContractCodes()) {
                if (code != null && !code.isBlank()) {
                    BSoContractVo vo = bSoContractMapper.selectByCode(code);
                    if (vo != null && vo.getId() != null) {
                        contractIdSet.add(vo.getId());
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 销售订单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromSoOrder(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getSoOrderId() != null) {
            BSoOrderVo vo = bSoOrderMapper.selectId(bo.getSoOrderId());
            if (vo != null && vo.getSo_contract_id() != null) {
                contractIdSet.add(vo.getSo_contract_id());
            }
        }
        if (bo.getSoOrderCode() != null && !bo.getSoOrderCode().isBlank()) {
            BSoOrderVo vo = bSoOrderMapper.selectByCode(bo.getSoOrderCode());
            if (vo != null && vo.getSo_contract_id() != null) {
                contractIdSet.add(vo.getSo_contract_id());
            }
        }
        if (bo.getSoOrderIds() != null && !bo.getSoOrderIds().isEmpty()) {
            for (Integer id : bo.getSoOrderIds()) {
                BSoOrderVo vo = bSoOrderMapper.selectId(id);
                if (vo != null && vo.getSo_contract_id() != null) {
                    contractIdSet.add(vo.getSo_contract_id());
                }
            }
        }
        if (bo.getSoOrderCodes() != null && !bo.getSoOrderCodes().isEmpty()) {
            for (String code : bo.getSoOrderCodes()) {
                if (code != null && !code.isBlank()) {
                    BSoOrderVo vo = bSoOrderMapper.selectByCode(code);
                    if (vo != null && vo.getSo_contract_id() != null) {
                        contractIdSet.add(vo.getSo_contract_id());
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * AR分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromAr(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getArId() != null) {
            List<BArSourceAdvanceVo> list = bArSourceAdvanceMapper.selectByArId(bo.getArId());
            if (list != null) {
                for (BArSourceAdvanceVo vo : list) {
                    if (vo.getSo_contract_id() != null) {
                        contractIdSet.add(vo.getSo_contract_id());
                    }
                }
            }
        }
        if (bo.getArCode() != null && !bo.getArCode().isBlank()) {
            List<BArSourceAdvanceVo> list = bArSourceAdvanceMapper.selectByCode(bo.getArCode());
            if (list != null) {
                for (BArSourceAdvanceVo vo : list) {
                    if (vo.getSo_contract_id() != null) {
                        contractIdSet.add(vo.getSo_contract_id());
                    }
                }
            }
        }
        if (bo.getArIds() != null && !bo.getArIds().isEmpty()) {
            for (Integer id : bo.getArIds()) {
                List<BArSourceAdvanceVo> list = bArSourceAdvanceMapper.selectByArId(id);
                if (list != null) {
                    for (BArSourceAdvanceVo vo : list) {
                        if (vo.getSo_contract_id() != null) {
                            contractIdSet.add(vo.getSo_contract_id());
                        }
                    }
                }
            }
        }
        if (bo.getArCodes() != null && !bo.getArCodes().isEmpty()) {
            for (String code : bo.getArCodes()) {
                if (code != null && !code.isBlank()) {
                    List<BArSourceAdvanceVo> list = bArSourceAdvanceMapper.selectByCode(code);
                    if (list != null) {
                        for (BArSourceAdvanceVo vo : list) {
                            if (vo.getSo_contract_id() != null) {
                                contractIdSet.add(vo.getSo_contract_id());
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 收款单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromArReceive(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<String> contractCodeSet = new LinkedHashSet<>();
        if (bo.getArReceiveId() != null) {
            List<BArReceiveSourceVo> list = bArReceiveSourceMapper.selectByArReceiveId(bo.getArReceiveId());
            if (list != null) {
                for (BArReceiveSourceVo vo : list) {
                    if (vo.getSo_contract_code() != null && !vo.getSo_contract_code().isBlank()) {
                        contractCodeSet.add(vo.getSo_contract_code());
                    }
                }
            }
        }
        if (bo.getArReceiveCode() != null && !bo.getArReceiveCode().isBlank()) {
            List<BArReceiveSourceVo> list = bArReceiveSourceMapper.selectByArReceiveCode(bo.getArReceiveCode());
            if (list != null) {
                for (BArReceiveSourceVo vo : list) {
                    if (vo.getSo_contract_code() != null && !vo.getSo_contract_code().isBlank()) {
                        contractCodeSet.add(vo.getSo_contract_code());
                    }
                }
            }
        }
        if (bo.getArReceiveIds() != null && !bo.getArReceiveIds().isEmpty()) {
            for (Integer id : bo.getArReceiveIds()) {
                List<BArReceiveSourceVo> list = bArReceiveSourceMapper.selectByArReceiveId(id);
                if (list != null) {
                    for (BArReceiveSourceVo vo : list) {
                        if (vo.getSo_contract_code() != null && !vo.getSo_contract_code().isBlank()) {
                            contractCodeSet.add(vo.getSo_contract_code());
                        }
                    }
                }
            }
        }
        if (bo.getArReceiveCodes() != null && !bo.getArReceiveCodes().isEmpty()) {
            for (String code : bo.getArReceiveCodes()) {
                if (code != null && !code.isBlank()) {
                    List<BArReceiveSourceVo> list = bArReceiveSourceMapper.selectByArReceiveCode(code);
                    if (list != null) {
                        for (BArReceiveSourceVo vo : list) {
                            if (vo.getSo_contract_code() != null && !vo.getSo_contract_code().isBlank()) {
                                contractCodeSet.add(vo.getSo_contract_code());
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
                BSoContractVo vo = bSoContractMapper.selectByContractCode(code);
                if (vo != null && vo.getId() != null) {
                    contractIdSet.add(vo.getId());
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 出库计划分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromOutPlan(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getOutPlanId() != null) {
            List<Integer> contractIds = bOutPlanDetailMapper.selectContractIdsByOutPlanId(bo.getOutPlanId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getOutPlanIds() != null && !bo.getOutPlanIds().isEmpty()) {
            for (Integer outPlanId : bo.getOutPlanIds()) {
                List<Integer> contractIds = bOutPlanDetailMapper.selectContractIdsByOutPlanId(outPlanId);
                if (contractIds != null) {
                    contractIdSet.addAll(contractIds);
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 出库单分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromOutbound(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getOutboundId() != null) {
            List<Integer> contractIds = bOutMapper.selectContractIdsByOutboundId(bo.getOutboundId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getOutboundIds() != null && !bo.getOutboundIds().isEmpty()) {
            for (Integer outboundId : bo.getOutboundIds()) {
                List<Integer> contractIds = bOutMapper.selectContractIdsByOutboundId(outboundId);
                if (contractIds != null) {
                    contractIdSet.addAll(contractIds);
                }
            }
        }
        return new ArrayList<>(contractIdSet);
    }

    /**
     * 销售结算分支，获取合同ID集合
     */
    private List<Integer> getContractIdsFromSoSettlement(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        if (bo.getSoSettlementId() != null) {
            List<Integer> contractIds = bSoSettlementMapper.selectContractIdsBySettlementId(bo.getSoSettlementId());
            if (contractIds != null) {
                contractIdSet.addAll(contractIds);
            }
        }
        if (bo.getSoSettlementIds() != null && !bo.getSoSettlementIds().isEmpty()) {
            for (Integer settlementId : bo.getSoSettlementIds()) {
                List<Integer> contractIds = bSoSettlementMapper.selectContractIdsBySettlementId(settlementId);
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
    private List<Integer> getContractIdsFromSoRefund(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        LinkedHashSet<String> contractCodeSet = new LinkedHashSet<>();
        
        if (bo.getSoRefundId() != null) {
            try {
                // 使用现有的selectId方法查询退款信息，获取合同编号
                BArReFundVo refundVo = bArReFundMapper.selectId(bo.getSoRefundId());
                if (refundVo != null && refundVo.getSo_contract_code() != null && !refundVo.getSo_contract_code().isBlank()) {
                    contractCodeSet.add(refundVo.getSo_contract_code());
                }
                log.debug("处理退款ID: {} 的合同关联查询，找到合同编号: {}", bo.getSoRefundId(), refundVo != null ? refundVo.getSo_contract_code() : "null");
            } catch (Exception e) {
                log.warn("查询退款ID {} 的合同关联时出现异常: {}", bo.getSoRefundId(), e.getMessage());
            }
        }
        
        if (bo.getSoRefundIds() != null && !bo.getSoRefundIds().isEmpty()) {
            for (Integer refundId : bo.getSoRefundIds()) {
                try {
                    // 使用现有的selectId方法查询退款信息，获取合同编号
                    BArReFundVo refundVo = bArReFundMapper.selectId(refundId);
                    if (refundVo != null && refundVo.getSo_contract_code() != null && !refundVo.getSo_contract_code().isBlank()) {
                        contractCodeSet.add(refundVo.getSo_contract_code());
                    }
                    log.debug("处理退款ID: {} 的合同关联查询，找到合同编号: {}", refundId, refundVo != null ? refundVo.getSo_contract_code() : "null");
                } catch (Exception e) {
                    log.warn("查询退款ID {} 的合同关联时出现异常: {}", refundId, e.getMessage());
                }
            }
        }
        
        // 合同编号转合同ID
        for (String contractCode : contractCodeSet) {
            if (contractCode != null && !contractCode.isBlank()) {
                BSoContractVo contractVo = bSoContractMapper.selectByContractCode(contractCode);
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
    private List<Integer> getContractIdsFromCargoRightTransfer(SoTotalDataRecalculateBo bo) {
        LinkedHashSet<Integer> contractIdSet = new LinkedHashSet<>();
        
        if (bo.getCargoRightTransferId() != null) {
            try {
                // 通过货权转移ID查询货权转移信息，获取合同ID
                BSoCargoRightTransferVo cargoTransferVo = bSoCargoRightTransferMapper.selectId(bo.getCargoRightTransferId());
                if (cargoTransferVo != null && cargoTransferVo.getSo_contract_id() != null) {
                    contractIdSet.add(cargoTransferVo.getSo_contract_id());
                }
                log.debug("处理货权转移ID: {} 的合同关联查询，找到合同ID: {}", bo.getCargoRightTransferId(), 
                    cargoTransferVo != null ? cargoTransferVo.getSo_contract_id() : "null");
            } catch (Exception e) {
                log.warn("查询货权转移ID {} 的合同关联时出现异常: {}", bo.getCargoRightTransferId(), e.getMessage());
            }
        }
        
        if (bo.getCargoRightTransferIds() != null && !bo.getCargoRightTransferIds().isEmpty()) {
            for (Integer cargoTransferId : bo.getCargoRightTransferIds()) {
                try {
                    BSoCargoRightTransferVo cargoTransferVo = bSoCargoRightTransferMapper.selectId(cargoTransferId);
                    if (cargoTransferVo != null && cargoTransferVo.getSo_contract_id() != null) {
                        contractIdSet.add(cargoTransferVo.getSo_contract_id());
                    }
                    log.debug("处理货权转移ID: {} 的合同关联查询，找到合同ID: {}", cargoTransferId, 
                        cargoTransferVo != null ? cargoTransferVo.getSo_contract_id() : "null");
                } catch (Exception e) {
                    log.warn("查询货权转移ID {} 的合同关联时出现异常: {}", cargoTransferId, e.getMessage());
                }
            }
        }
        
        log.debug("getContractIdsFromCargoRightTransfer 最终合同ID集合: {}", contractIdSet);
        return new ArrayList<>(contractIdSet);
    }

    // ================================== 处理各个业务模块的Total数据方法 ==================================

    /**
     * 处理应收退款total数据（b_ar_refund_total）
     * @param contractId 合同ID
     */
    private void processArRefundTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取相关的退款ID集合
        LinkedHashSet<Integer> arRefundIdSet = new LinkedHashSet<>();
        List<Integer> refundIdList = bArReFundTotalMapper.selectRefundIdsByContractId(contractId);
        if (refundIdList != null && !refundIdList.isEmpty()) {
            arRefundIdSet.addAll(refundIdList);
        }

        // 2. 如果有退款ID，先插入缺失的Total记录，再进行批量更新
        if (!arRefundIdSet.isEmpty()) {
            try {
                // 2.1 插入缺失的Total记录（处理历史数据）
                int insertResult = bArReFundTotalMapper.insertMissingRefundTotal(arRefundIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的应收退款总计数据成功，插入行数: {}, 退款ID集合: {}", insertResult, arRefundIdSet);
                }
                
                // 2.2 批量更新退款总计数据，从退款源单表同步数据
                int updateResult = bArReFundTotalMapper.batchUpdateRefundTotalFromSource(arRefundIdSet);
                log.debug("批量更新应收退款总计数据成功，影响行数: {}, 退款ID集合: {}", updateResult, arRefundIdSet);
            } catch (Exception e) {
                log.error("处理应收退款总计数据失败，退款ID集合: {}, 错误信息: {}", arRefundIdSet, e.getMessage(), e);
                throw new BusinessException(e);
            }
        } else {
            log.debug("合同ID {} 下未找到应收退款数据", contractId);
        }
    }

    /**
     * 处理货权转移总计数据（b_so_cargo_right_transfer_total）
     * @param contractId 合同ID
     */
    private void processCargoRightTransferTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取相关的货权转移ID集合
        LinkedHashSet<Integer> cargoRightTransferIdSet = new LinkedHashSet<>();
        List<Integer> cargoTransferIdList = bSoCargoRightTransferTotalMapper.selectCargoRightTransferIdsByContractId(contractId);
        if (cargoTransferIdList != null && !cargoTransferIdList.isEmpty()) {
            cargoRightTransferIdSet.addAll(cargoTransferIdList);
        }

        // 2. 如果有货权转移ID，先插入缺失的Total记录，再进行批量更新
        if (!cargoRightTransferIdSet.isEmpty()) {
            try {
                // 2.1 插入缺失的Total记录（处理历史数据）
                Integer insertResult = bSoCargoRightTransferTotalMapper.insertMissingCargoRightTransferTotal(cargoRightTransferIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的货权转移总计数据成功，插入行数: {}, 货权转移ID集合: {}", insertResult, cargoRightTransferIdSet);
                }
                
                // 2.2 批量更新货权转移总计数据，从明细表同步数据
                int updateResult = bSoCargoRightTransferTotalMapper.batchUpdateCargoRightTransferTotalFromDetail(cargoRightTransferIdSet);
                log.debug("批量更新货权转移总计数据成功，影响行数: {}, 货权转移ID集合: {}", updateResult, cargoRightTransferIdSet);
            } catch (Exception e) {
                log.error("处理货权转移总计数据失败，货权转移ID集合: {}, 错误信息: {}", cargoRightTransferIdSet, e.getMessage(), e);
                throw new BusinessException("处理货权转移总计数据失败: " + e.getMessage());
            }
        } else {
            log.debug("合同ID {} 下未找到货权转移数据", contractId);
        }
    }

    /**
     * 处理销售结算汇总数据（b_so_settlement_total）
     * @param contractId 合同ID
     */
    private void processSoSettlementTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取到b_so_settlement_detail_source_outbound表的数据，生成对应的LinkedHashSet<Integer> soSettlementIdSet
        LinkedHashSet<Integer> soSettlementIdSet = new LinkedHashSet<>();
        List<Integer> settlementIdList = bSoSettlementTotalMapper.selectSettlementIdsByContractId(contractId);
        if (settlementIdList != null && !settlementIdList.isEmpty()) {
            soSettlementIdSet.addAll(settlementIdList);
        }

        // 2. 如果有结算ID，先插入缺失的数据，再进行批量更新
        if (!soSettlementIdSet.isEmpty()) {
            try {
                // 先插入缺失的结算汇总记录
                int insertResult = bSoSettlementTotalMapper.insertMissingSettlementTotal(soSettlementIdSet);
                if (insertResult > 0) {
                    log.debug("插入缺失的销售结算汇总数据成功，插入行数: {}, 结算ID集合: {}", insertResult, soSettlementIdSet);
                }
                
                // 然后批量更新汇总数据
                int updateResult = bSoSettlementTotalMapper.batchUpdateSettlementTotal(soSettlementIdSet);
                log.debug("批量更新销售结算汇总数据成功，影响行数: {}, 结算ID集合: {}", updateResult, soSettlementIdSet);
            } catch (Exception e) {
                log.error("处理销售结算汇总数据失败，结算ID集合: {}, 错误信息: {}", soSettlementIdSet, e.getMessage(), e);
                throw new BusinessException(e);
            }
        } else {
            log.debug("合同ID {} 下未找到销售结算数据", contractId);
        }
    }

    /**
     * 处理出库计划total数据（b_out_plan_detail、b_out_plan_total）
     * @param contractId 合同ID
     */
    private void processOutPlanTotalDataByContractId(Integer contractId) {
        // 1. 通过contractId获取到b_out_plan_detail表的数据，生成对应的LinkedHashSet<Integer> outPlanIdSet
        LinkedHashSet<Integer> outPlanIdSet = new LinkedHashSet<>();
        List<BOutPlanDetailEntity> outPlanDetailList = bOutPlanDetailMapper.selectByContractId(contractId);
        if (outPlanDetailList != null && !outPlanDetailList.isEmpty()) {
            for (BOutPlanDetailEntity detail : outPlanDetailList) {
                if (detail.getOut_plan_id() != null) {
                    outPlanIdSet.add(detail.getOut_plan_id());
                }
            }
        }

        // 2. 如果有出库计划ID，进行后续处理
        if (!outPlanIdSet.isEmpty()) {
            // 2.1 更新b_out_plan_detail：调用updateProcessingQtyByOutPlanId方法
            try {
                bOutPlanDetailMapper.updateProcessingQtyByOutPlanId(outPlanIdSet);
                log.debug("更新出库计划明细处理数量统计成功，出库计划ID集合: {}", outPlanIdSet);
            } catch (Exception e) {
                log.error("更新出库计划明细处理数量统计失败，出库计划ID集合: {}, 错误信息: {}", outPlanIdSet, e.getMessage(), e);
            }

            // 2.2 确保b_out_plan_total记录存在（先插入不存在的记录）
            for (Integer outPlanId : outPlanIdSet) {
                BOutPlanTotalVo existingRecord = bOutPlanTotalMapper.selectByOutPlanId(outPlanId);
                if (existingRecord == null) {
                    // 如果记录不存在，则新增一条记录，只设置out_plan_id
                    BOutPlanTotalEntity newEntity = new BOutPlanTotalEntity();
                    newEntity.setOut_plan_id(outPlanId);
                    bOutPlanTotalMapper.insert(newEntity);
                    log.debug("新增出库计划汇总数据记录，out_plan_id: {}", outPlanId);
                }
            }

            // 2.3 更新b_out_plan_total：调用updateOutPlanTotalData方法
            try {
                int updateResult = bOutPlanTotalMapper.updateOutPlanTotalData(outPlanIdSet);
                log.debug("更新出库计划汇总数据成功，影响行数: {}, 出库计划ID集合: {}", updateResult, outPlanIdSet);
            } catch (Exception e) {
                log.error("更新出库计划汇总数据失败，出库计划ID集合: {}, 错误信息: {}", outPlanIdSet, e.getMessage(), e);
            }
        } else {
            log.debug("合同ID {} 下未找到出库计划明细数据", contractId);
        }
    }

    /**
     * 处理收款（b_ar_receive、b_ar_source_advance）的total数据
     * @param contractId 合同ID
     */
    private void processReceiveTotalDataByContractId(Integer contractId) {
        // 0、实例化LinkedHashSet<Integer> ArReceive
        LinkedHashSet<Integer> arReceiveSet = new LinkedHashSet<>();
        // 1、调用bArReceiveSourceAdvanceMapper.selectByContractId
        List<BArReceiveSourceAdvanceVo> receiveSourceAdvanceList = bArReceiveSourceAdvanceMapper.selectByContractId(contractId);
        if (receiveSourceAdvanceList != null && !receiveSourceAdvanceList.isEmpty()) {
            for (BArReceiveSourceAdvanceVo vo : receiveSourceAdvanceList) {
                if (vo.getAr_id() != null) {
                    arReceiveSet.add(vo.getAr_id());
                }
            }
        }
        // 2、执行更新逻辑
        if (!arReceiveSet.isEmpty()) {
            int i = bArReceiveMapper.updateTotalData(arReceiveSet);
            log.debug("更新收款单总金额数据成功，影响行数: {}", i);
        }

        // ========== 新增：更新b_ar_source_advance申请收款总金额、已收款总金额、收款中总金额、未收款总金额 ========== 
        for (Integer arId : arReceiveSet) {
            // 1. 查询b_ar_source_advance
            List<BArSourceAdvanceVo> advanceVoList = bArSourceAdvanceMapper.selectByArId(arId);
            // 2. 查询b_ar_receive金额汇总
            BArReceiveVo receiveVo = bArReceiveMapper.getSumAmount(arId, null);
            BigDecimal remainReceived = receiveVo != null && receiveVo.getReceived_amount_total() != null ? receiveVo.getReceived_amount_total() : BigDecimal.ZERO;
            // 3. 先进先出分配received_amount_total（已收款金额）
            List<BArSourceAdvanceVo> fifoList = new ArrayList<>();
            for (BArSourceAdvanceVo advanceVo : advanceVoList) {
                BigDecimal receivable = advanceVo.getOrder_amount() == null ? BigDecimal.ZERO : advanceVo.getOrder_amount();
                advanceVo.setReceivable_amount_total(receivable);
                BigDecimal received = BigDecimal.ZERO;
                if (remainReceived.compareTo(BigDecimal.ZERO) > 0) {
                    if (remainReceived.compareTo(receivable) >= 0) {
                        received = receivable;
                        remainReceived = remainReceived.subtract(receivable);
                    } else {
                        received = remainReceived;
                        remainReceived = BigDecimal.ZERO;
                    }
                }
                advanceVo.setReceived_amount_total(received);
                fifoList.add(advanceVo);
            }
            // 4. 先进先出分配receiving_amount_total（收款中金额）
            BigDecimal remainReceiving = receiveVo != null && receiveVo.getReceiving_amount_total() != null ? receiveVo.getReceiving_amount_total() : BigDecimal.ZERO;
            for (BArSourceAdvanceVo advanceVo : fifoList) {
                BigDecimal maxReceiving = advanceVo.getReceivable_amount_total().subtract(advanceVo.getReceived_amount_total());
                BigDecimal receiving = BigDecimal.ZERO;
                if (remainReceiving.compareTo(BigDecimal.ZERO) > 0) {
                    if (remainReceiving.compareTo(maxReceiving) >= 0) {
                        receiving = maxReceiving;
                        remainReceiving = remainReceiving.subtract(maxReceiving);
                    } else {
                        receiving = remainReceiving;
                        remainReceiving = BigDecimal.ZERO;
                    }
                }
                advanceVo.setReceiving_amount_total(receiving);
            }

            // 5. 统一计算未收款总金额（unreceive_amount_total = receivable_amount_total - received_amount_total - receiving_amount_total）
            for (BArSourceAdvanceVo advanceVo : fifoList) {
                advanceVo.setUnreceive_amount_total(
                    advanceVo.getReceivable_amount_total()
                        .subtract(advanceVo.getReceived_amount_total())
                        .subtract(advanceVo.getReceiving_amount_total())
                );
            }

            // 分摊完毕开始更新
            for (BArSourceAdvanceVo advanceVo : advanceVoList) {
                BArSourceAdvanceEntity entity = new BArSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bArSourceAdvanceMapper.updateById(entity);
            }
        }
    }

    /**
     * 处理应收账款total数据（b_ar_total、b_ar_detail）- 预付款
     * @param contractId 合同ID
     */
    private void processArTotalDataByContractId(Integer contractId) {
        // 0、实例化LinkedHashSet<Integer> Ar
        LinkedHashSet<Integer> arSet = new LinkedHashSet<>();
        // 1、搜索select * from b_ar_source_advance where so_contract_id=contractId
        List<BArSourceAdvanceVo> arSourceAdvanceList = bArSourceAdvanceMapper.selectByContractId(contractId);
        if (arSourceAdvanceList != null && !arSourceAdvanceList.isEmpty()) {
            for (BArSourceAdvanceVo vo : arSourceAdvanceList) {
                if (vo.getAr_id() != null) {
                    arSet.add(vo.getAr_id());
                }
            }
        }
        // 2、循环变量Ar，更新应收账款total表(b_ar_total)
        for (Integer arId : arSet) {
            // 2.1、查询b_ar_total
            BArTotalVo arTotalVo = bArTotalMapper.selectByArId(arId);
            if (arTotalVo == null) {
                arTotalVo = new BArTotalVo();
                arTotalVo.setAr_id(arId);
            }
            // 2.2、获取申请收款总金额
            BigDecimal receivableAmount = bArSourceAdvanceMapper.getSumReceivableAmount(arId);
            arTotalVo.setReceivable_amount_total(receivableAmount);
            // 2.3、获取已收款总金额
            BArReceiveVo receivedVo = bArReceiveMapper.getSumAmount(arId, null);
            if (receivedVo != null) {
                arTotalVo.setReceived_amount_total(receivedVo.getReceived_amount_total());
            } else {
                arTotalVo.setReceived_amount_total(BigDecimal.ZERO);
            }
            // 2.4、获取收款中总金额
            BArReceiveVo receivingVo = bArReceiveMapper.getSumAmount(arId, DictConstant.DICT_B_AR_RECEIVE_STATUS_ZERO);
            if (receivingVo != null) {
                arTotalVo.setReceiving_amount_total(receivingVo.getReceivable_amount_total());
            } else {
                arTotalVo.setReceiving_amount_total(BigDecimal.ZERO);
            }

            // 获取已中止收款总金额和作废收款总金额
            // 2.4.1、判断应收账款的收款状态是否为已中止
            // 查询应收账款的receive_status和status
            BArVo arVo = bArMapper.selectId(arTotalVo.getAr_id());
            if (arVo != null && DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP.equals(arVo.getReceive_status())) {
                // 2.4.2、如果是已中止，则设置receiving_amount_total=0，stopreceive_amount_total=receivable_amount_total-received_amount_total
                arTotalVo.setReceiving_amount_total(BigDecimal.ZERO);
                BigDecimal receivableAmountTotal = handleNullBigDecimal(arTotalVo.getReceivable_amount_total());
                BigDecimal receivedAmountTotal = handleNullBigDecimal(arTotalVo.getReceived_amount_total());
                arTotalVo.setStopReceive_amount_total(receivableAmountTotal.subtract(receivedAmountTotal));
                arTotalVo.setCancelReceive_amount_total(BigDecimal.ZERO);
            } else if (arVo != null && DictConstant.DICT_B_AR_STATUS_FIVE.equals(arVo.getStatus())) {
                // 2.4.3、如果是作废状态，则设置receiving_amount_total=0, stopreceive_amount_total=0, cancelreceive_amount_total=receivable_amount_total-received_amount_total
                arTotalVo.setReceiving_amount_total(BigDecimal.ZERO);
                arTotalVo.setStopReceive_amount_total(BigDecimal.ZERO);
                BigDecimal receivableAmountTotal = handleNullBigDecimal(arTotalVo.getReceivable_amount_total());
                BigDecimal receivedAmountTotal = handleNullBigDecimal(arTotalVo.getReceived_amount_total());
                arTotalVo.setCancelReceive_amount_total(receivableAmountTotal.subtract(receivedAmountTotal));
            } else {
                // 2.4.4、如果不是已中止也不是作废，则保持原有的收款中总金额
                arTotalVo.setStopReceive_amount_total(BigDecimal.ZERO);
                arTotalVo.setCancelReceive_amount_total(BigDecimal.ZERO);
            }

            // 2.5、未收款总金额
            // 未收款总金额 = 申请收款总金额 - 已收款总金额 - 收款中总金额 - 中止收款总金额 - 作废收款总金额
            BigDecimal receivableAmountTotal = handleNullBigDecimal(arTotalVo.getReceivable_amount_total());
            BigDecimal receivedAmountTotal = handleNullBigDecimal(arTotalVo.getReceived_amount_total());
            BigDecimal receivingAmountTotal = handleNullBigDecimal(arTotalVo.getReceiving_amount_total());
            BigDecimal stopAmountTotal = handleNullBigDecimal(arTotalVo.getStopReceive_amount_total());
            BigDecimal cancelAmountTotal = handleNullBigDecimal(arTotalVo.getCancelReceive_amount_total());
            BigDecimal unreceiveAmountTotal = receivableAmountTotal.subtract(receivedAmountTotal).subtract(receivingAmountTotal).subtract(stopAmountTotal).subtract(cancelAmountTotal);
            arTotalVo.setUnreceive_amount_total(unreceiveAmountTotal);
            
            // 2.6、将VO转换为Entity并更新数据库
            BArTotalEntity arTotalEntity = new BArTotalEntity();
            BeanUtils.copyProperties(arTotalVo, arTotalEntity);

            /**
             * 保存或更新b_ar_total表
             */
            bArTotalService.saveOrUpdate(arTotalEntity);
        }
        // 处理应收账款明细的total（b_ar_detail）数据
        if (!arSet.isEmpty()) {
            bArDetailMapper.updateTotalData(arSet);
        }
        
        // 在b_ar_total更新完毕后，处理中止收款总金额分配
        if (!arSet.isEmpty()) {
            // ========== 新增：更新b_ar_source_advance中止收款总金额stopreceive_amount_total ==========
            processStopReceiveAmountDistribution(arSet);
            
            // ========== 新增：更新b_ar_source_advance作废收款总金额cancelreceive_amount_total ==========
            processCancelReceiveAmountDistribution(arSet);
        }
    }

    /**
     * 处理应收账款作废时的预付款作废金额分配
     * @param arIdSet 应收账款ID集合
     */
    private void processCancelReceiveAmountDistribution(LinkedHashSet<Integer> arIdSet) {
        if (arIdSet == null || arIdSet.isEmpty()) {
            return;
        }
        
        // 循环处理每个应收账款ID
        for (Integer arId : arIdSet) {
            // 检查该应收账款是否为作废状态
            BArVo arVo = bArMapper.selectId(arId);
            if (arVo == null || !DictConstant.DICT_B_AR_STATUS_FIVE.equals(arVo.getStatus())) {
                // 如果不是作废状态，跳过处理
                continue;
            }
            
            // 查询该应收账款下的所有预付款源单记录
            List<BArSourceAdvanceVo> sourceAdvanceList = bArSourceAdvanceMapper.selectByArId(arId);
            if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
                continue;
            }
            
            // 处理每条预付款源单记录
            for (BArSourceAdvanceVo advanceVo : sourceAdvanceList) {
                // 设置作废金额为申请金额
                BigDecimal orderAmount = handleNullBigDecimal(advanceVo.getOrder_amount());
                advanceVo.setCancelReceive_amount_total(orderAmount);
                
                // 同时清空其他相关金额字段（作废时这些字段应该为0）
                advanceVo.setReceiving_amount_total(BigDecimal.ZERO);
                advanceVo.setUnreceive_amount_total(BigDecimal.ZERO);
                advanceVo.setStopReceive_amount_total(BigDecimal.ZERO);
                
                // 更新数据库记录
                BArSourceAdvanceEntity entity = new BArSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bArSourceAdvanceMapper.updateById(entity);
                
                log.debug("更新应收账款源单记录作废金额，ar_id: {}, source_advance_id: {}, cancel_amount: {}", 
                    arId, advanceVo.getId(), advanceVo.getCancelReceive_amount_total());
            }
            
            log.info("完成应收账款作废处理，ar_id: {}, 共处理预付款源单记录: {} 条", 
                arId, sourceAdvanceList.size());
        }
    }

    /**
     * 按先进先出算法分配并更新b_ar_source_advance中止收款总金额
     * @param arIdSet 应收账款ID集合
     */
    private void processStopReceiveAmountDistribution(LinkedHashSet<Integer> arIdSet) {
        if (arIdSet == null || arIdSet.isEmpty()) {
            return;
        }
        
        // 1. 循环arIdSet，处理中止收款总金额
        for (Integer arId : arIdSet) {
            // 1.1 获取应收账款的中止金额
            BArTotalVo arTotalVo = bArTotalMapper.selectByArId(arId);
            if (arTotalVo == null) {
                continue;
            }
            
            // 检查该应收账款是否为中止状态
            BArVo arVo = bArMapper.selectId(arId);
            if (arVo == null || !DictConstant.DICT_B_AR_RECEIVE_STATUS_STOP.equals(arVo.getReceive_status())) {
                continue;
            }
            
            BigDecimal totalStopAmount = handleNullBigDecimal(arTotalVo.getStopReceive_amount_total());
            if (totalStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            
            // 1.2 查询该应收账款下的所有预付款源单记录
            List<BArSourceAdvanceVo> sourceAdvanceList = bArSourceAdvanceMapper.selectByArId(arId);
            if (sourceAdvanceList == null || sourceAdvanceList.isEmpty()) {
                continue;
            }
            
            // 1.3 按先进先出算法分配中止金额
            BigDecimal remainingStopAmount = totalStopAmount;
            
            for (BArSourceAdvanceVo advanceVo : sourceAdvanceList) {
                if (remainingStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }
                
                // 计算当前记录的可中止金额
                BigDecimal receivableAmount = handleNullBigDecimal(advanceVo.getReceivable_amount_total());
                BigDecimal receivedAmount = handleNullBigDecimal(advanceVo.getReceived_amount_total());
                BigDecimal availableStopAmount = receivableAmount.subtract(receivedAmount);
                
                // 检查是否满足条件：receivable_amount_total > received_amount_total
                if (availableStopAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    // 如果没有可中止的金额，设置为0并清空其他字段
                    advanceVo.setStopReceive_amount_total(BigDecimal.ZERO);
                    advanceVo.setReceiving_amount_total(BigDecimal.ZERO);
                    advanceVo.setUnreceive_amount_total(BigDecimal.ZERO);
                } else {
                    // 计算实际分配的中止金额（取可中止金额和剩余中止金额的较小值）
                    BigDecimal allocatedStopAmount = remainingStopAmount.compareTo(availableStopAmount) <= 0 
                        ? remainingStopAmount : availableStopAmount;
                    
                    // 更新字段值
                    advanceVo.setStopReceive_amount_total(allocatedStopAmount);
                    advanceVo.setReceiving_amount_total(BigDecimal.ZERO);
                    advanceVo.setUnreceive_amount_total(receivableAmount.subtract(receivedAmount).subtract(allocatedStopAmount));
                    
                    // 减少剩余中止金额
                    remainingStopAmount = remainingStopAmount.subtract(allocatedStopAmount);
                }
                
                // 1.4 更新数据库记录
                BArSourceAdvanceEntity entity = new BArSourceAdvanceEntity();
                BeanUtils.copyProperties(advanceVo, entity);
                bArSourceAdvanceMapper.updateById(entity);
                
                log.debug("更新应收账款源单记录中止金额，ar_id: {}, source_advance_id: {}, stopreceive_amount: {}", 
                    arId, advanceVo.getId(), advanceVo.getStopReceive_amount_total());
            }
        }
    }

    /**
     * 处理销售订单total数据（b_so_order_total）
     * @param contractId 合同ID
     */
    private void processSoOrderTotalDataByContractId(Integer contractId) {
        // 1. 根据合同ID查询销售订单，获取销售订单ID集合
        LinkedHashSet<Integer> soOrderIdSet = new LinkedHashSet<>();
        List<BSoOrderVo> soOrderList = bSoOrderMapper.selectBySoContractId(contractId);
        if (soOrderList != null && !soOrderList.isEmpty()) {
            for (BSoOrderVo vo : soOrderList) {
                if (vo.getId() != null) {
                    soOrderIdSet.add(vo.getId());
                }
            }
        }

        // 2. 如果有销售订单，先确保记录存在，然后调用updateTotalData更新数据
        if (!soOrderIdSet.isEmpty()) {
            // 2.1 检查并插入不存在的记录
            for (Integer soOrderId : soOrderIdSet) {
                BSoOrderTotalVo existingRecord = bSoOrderTotalMapper.selectBySoId(soOrderId);
                if (existingRecord == null) {
                    // 如果记录不存在，则新增一条记录，只设置so_order_id
                    BSoOrderTotalEntity newEntity = new BSoOrderTotalEntity();
                    newEntity.setSo_order_id(soOrderId);
                    bSoOrderTotalMapper.insert(newEntity);
                    log.debug("新增销售订单总计数据记录，so_order_id: {}", soOrderId);
                }
            }
            
            // 2.2 更新销售订单基础汇总数据 (b_so_order_total)
            log.debug("开始更新销售订单总计数据，订单数量: {}", soOrderIdSet.size());
            
            // 2.2.1 更新订单基础总计数据（金额、税额、数量）
            int orderTotalResult = bSoOrderTotalMapper.updateSoOrderTotalData(soOrderIdSet);
            log.debug("更新销售订单基础总计数据完成，影响行数: {}", orderTotalResult);
            
            // 2.2.2 更新预付款相关数据
            int advanceResult = bSoOrderTotalMapper.updateAdvanceAmountTotalData(soOrderIdSet);
            log.debug("更新预付款数据完成，影响行数: {}", advanceResult);
            
            // 2.2.3 更新已收款总金额数据（目前仅考虑预付款来源：b_ar_source_advance）
            int receivedResult = bSoOrderTotalMapper.updateReceivedTotalData(soOrderIdSet);
            log.debug("更新已收款总金额数据完成，影响行数: {}", receivedResult);
            
            // 2.2.4 更新退款数据（从b_ar_refund_total汇总到销售订单总计表）
            int refundResult = bSoOrderTotalMapper.updateRefundAmountTotalData(soOrderIdSet);
            log.debug("更新退款数据完成，影响行数: {}", refundResult);
            
            // 2.2.4.5 更新货权转移明细汇总数据（从b_so_cargo_right_transfer_detail汇总到销售订单明细汇总表）
            int cargoRightTransferDetailResult = bSoOrderDetailTotalMapper.updateCargoRightTransferTotalBySoOrderIds(soOrderIdSet);
            log.debug("更新货权转移明细汇总数据完成，影响行数: {}", cargoRightTransferDetailResult);
            
            // 2.2.5 更新货权转移数据（从b_so_cargo_right_transfer_total汇总到销售订单总计表）
            int cargoRightTransferResult = bSoOrderTotalMapper.updateCargoRightTransferTotalData(soOrderIdSet);
            log.debug("更新货权转移数据完成，影响行数: {}", cargoRightTransferResult);

            // 2.3 更新销售订单明细汇总数据 (b_so_order_detail_total)
            log.debug("开始更新销售订单明细汇总数据");
            
            // 2.3.0 确保明细汇总记录存在
            int insertResult = bSoOrderDetailTotalMapper.insertMissingRecords(soOrderIdSet);
            log.debug("插入缺失的明细汇总记录数: {}", insertResult);
            
            // 2.3.1 更新出库相关汇总数据（处理中、未处理、已处理、取消等数据）
            int detailOutboundResult = bSoOrderDetailTotalMapper.updateOutboundTotalBySoOrderIds(soOrderIdSet);
            log.debug("更新明细出库汇总数据完成，影响行数: {}", detailOutboundResult);
            
            // 2.3.2 更新明细级别待结算数量汇总
            int detailSettleResult = bSoOrderDetailTotalMapper.updateSettleCanQtyTotal(soOrderIdSet);
            log.debug("更新明细待结算数量汇总完成，影响行数: {}", detailSettleResult);

            // 2.4 回写订单级别汇总数据 (b_so_order_total)
            log.debug("开始回写订单级别汇总数据");
            
            // 2.4.1 从明细汇总表回写出库计划和货权转移相关数据到订单总计表
            int outPlanResult = bSoOrderTotalMapper.updateOutboundAndCargoRightTransferTotalData(soOrderIdSet);
            log.debug("回写出库计划和货权转移数据完成，影响行数: {}", outPlanResult);
            
            // 2.4.2 更新订单级别待结算数量汇总
            int orderSettleResult = bSoOrderTotalMapper.updateSettleCanQtyTotal(soOrderIdSet);
            log.debug("更新订单待结算数量汇总完成，影响行数: {}", orderSettleResult);
            
            log.debug("销售订单总计数据更新完成，订单ID集合: {}", soOrderIdSet);
        }
    }

    /**
     * 处理销售合同total数据（b_so_contract_total）
     * @param contractId 合同ID
     */
    private void processSoContractTotalDataByContractId(Integer contractId) {
        // 1. 检查并确保记录存在
        BSoContractTotalVo existingRecord = bSoContractTotalMapper.selectBySoContractId(contractId);
        if (existingRecord == null) {
            // 如果记录不存在，则新增一条记录，只设置so_contract_id
            BSoContractTotalEntity newEntity = new BSoContractTotalEntity();
            newEntity.setSo_contract_id(contractId);
            bSoContractTotalMapper.insert(newEntity);
            log.debug("新增销售合同总计数据记录，so_contract_id: {}", contractId);
        }
        
        // 2. 使用SQL批量汇总和更新合同级预付款数据
        int updateResult = bSoContractTotalMapper.updateContractAdvanceTotalData(contractId);
        if (updateResult > 0) {
            log.debug("SQL汇总更新销售合同总计数据成功，so_contract_id: {}, 更新记录数: {}", contractId, updateResult);
        } else {
            log.warn("SQL汇总更新销售合同总计数据失败，so_contract_id: {}", contractId);
        }

        // 3. 更新销售合同订单笔数
        int orderCountUpdateResult = bSoContractTotalMapper.updateContractOrderCount(contractId);
        if (orderCountUpdateResult > 0) {
            log.debug("更新销售合同订单笔数成功，so_contract_id: {}, 更新记录数: {}", contractId, orderCountUpdateResult);
        } else {
            log.warn("更新销售合同订单笔数失败，so_contract_id: {}", contractId);
        }
    }
}