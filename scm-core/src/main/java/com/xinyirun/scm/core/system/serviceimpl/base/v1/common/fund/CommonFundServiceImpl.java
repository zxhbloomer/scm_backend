package com.xinyirun.scm.core.system.serviceimpl.base.v1.common.fund;

import com.xinyirun.scm.bean.entity.busniess.fund.BFundMonitorEntity;
import com.xinyirun.scm.bean.entity.busniess.fund.BFundUsageEntity;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.bo.fund.monit.in.FundInBo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPaySourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPayVo;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundUsageVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.enums.fund.FundEventTypeEnum;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPayMapper;
import com.xinyirun.scm.core.system.mapper.business.appay.BApPaySourceAdvanceMapper;
import com.xinyirun.scm.core.system.mapper.business.fund.BFundMonitorMapper;
import com.xinyirun.scm.core.system.mapper.business.fund.BFundUsageMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.fund.ICommonFundService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BFundMonitorAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BFundUsageAutoCodeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 资金使用情况
 *
 * @author
 */
@Service
@Slf4j
public class CommonFundServiceImpl extends BaseServiceImpl<BFundUsageMapper, BFundUsageEntity> implements ICommonFundService {

    @Autowired
    private BApPayMapper bApPayMapper;

    @Autowired
    private BApPayDetailMapper bApPayDetailMapper;

    @Autowired
    private BApPaySourceAdvanceMapper bApPaySourceAdvanceMapper;

    @Autowired
    private BFundMonitorMapper bFundMonitorMapper;

    @Autowired
    private BFundMonitorAutoCodeServiceImpl fundMonitorAutoCodeService;
    
    @Autowired
    private BFundUsageAutoCodeServiceImpl fundUsageAutoCodeService;

    /**
     * 新增一条流水，处理预付款金额增加，操作：付款单操作付款凭证上传完成付款，预付款已付款金额增加
     * @param fundInBo 资金输入业务对象
     */
    @Override
    public void increaseAdvancePayment(FundInBo fundInBo) {
        log.info("开始处理预付款金额增加，付款单ID: {}", fundInBo.getTrade_id());

        // 1. 查询付款单主表数据：查询已付款
        BApPayVo bApPayVo = bApPayMapper.selById(fundInBo.getTrade_id());
        if (bApPayVo == null) {
            log.error("付款单不存在，ID: {}", fundInBo.getTrade_id());
            return;
        }

        // 检查付款单状态，必须是已付款状态
        if (!DictConstant.DICT_B_AP_PAY_STATUS_ONE.equals(bApPayVo.getStatus())) {
            log.error("付款单状态不正确，当前状态: {}，期望状态: {}（已付款），付款单ID: {}", 
                     bApPayVo.getStatus(), DictConstant.DICT_B_AP_PAY_STATUS_ONE, fundInBo.getTrade_id());
            return;
        }

        // 2. 查询付款单明细数据
        List<BApPayDetailVo> bApPayDetailList = bApPayDetailMapper.selectById(fundInBo.getTrade_id());
        if (bApPayDetailList == null || bApPayDetailList.isEmpty()) {
            log.error("付款单明细不存在，付款单ID: {}", fundInBo.getTrade_id());
            return;
        }

        // 3. 查询付款来源预付聚合数据
        BApPaySourceAdvanceVo sourceAdvanceVo = bApPaySourceAdvanceMapper.selectAggregatedByApPayId(fundInBo.getTrade_id());

        // 4. 创建资金监控记录列表
        List<BFundMonitorVo> fundMonitorVoList = new ArrayList<>();

        // 5. 循环付款单明细，为每条明细创建资金监控记录
        for (BApPayDetailVo detail : bApPayDetailList) {
            // 生成自动编码
            SCodeEntity codeEntity = fundMonitorAutoCodeService.autoCode();
            String code = codeEntity != null ? codeEntity.getCode() : "";

            BFundMonitorVo fundMonitorVo = new BFundMonitorVo();
            fundMonitorVo.setCode(code);
            fundMonitorVo.setType("1");
            fundMonitorVo.setBusiness_type(FundEventTypeEnum.YUFU_PAY_INC.getCode());
            fundMonitorVo.setBusiness_type_name(FundEventTypeEnum.YUFU_PAY_INC.getMsg());
            fundMonitorVo.setSerial_type("b_ap_pay");
            fundMonitorVo.setSerial_id(bApPayVo.getId());
            fundMonitorVo.setSerial_code(bApPayVo.getCode());
            fundMonitorVo.setEnterprise_id(bApPayVo.getPurchaser_id());
            fundMonitorVo.setEnterprise_code(bApPayVo.getPurchaser_code());
            fundMonitorVo.setBank_account_id(detail.getBank_accounts_id());
            fundMonitorVo.setBank_account_code(detail.getBank_accounts_code());
            fundMonitorVo.setBank_accounts_type_id(detail.getBank_accounts_type_id());
            fundMonitorVo.setBank_accounts_type_code(detail.getBank_accounts_type_code());
            fundMonitorVo.setFund_type("0");
            fundMonitorVo.setTrade_id(bApPayVo.getId());
            fundMonitorVo.setTrade_code(bApPayVo.getCode());
            fundMonitorVo.setTrade_type("b_ap_pay");

            // 设置聚合字段，如果查询到了聚合数据
            if (sourceAdvanceVo != null) {
                fundMonitorVo.setTrade_order_id(sourceAdvanceVo.getPo_order_id_gc());
                fundMonitorVo.setTrade_order_code(sourceAdvanceVo.getPo_order_code_gc());
                fundMonitorVo.setTrade_order_type("b_po_order");
                fundMonitorVo.setTrade_contract_id(sourceAdvanceVo.getPo_contract_id_gc());
                fundMonitorVo.setTrade_contract_code(sourceAdvanceVo.getPo_contract_code_gc());
                fundMonitorVo.setTrade_contract_type("b_po_contract");
            }

            fundMonitorVo.setAmount(bApPayVo.getPaid_amount_total());

            fundMonitorVoList.add(fundMonitorVo);
        }

        // 6. 根据填充的BFundMonitorVo列表，通过实体类插入数据到b_fund_monitor
        if (!fundMonitorVoList.isEmpty()) {
            for (int i = 0; i < fundMonitorVoList.size(); i++) {
                BFundMonitorVo vo = fundMonitorVoList.get(i);
                BApPayDetailVo detail = bApPayDetailList.get(i);
                
                // 将VO转换为Entity，使用BeanUtilsSupport进行属性拷贝
                BFundMonitorEntity entity = (BFundMonitorEntity) BeanUtilsSupport.copyProperties(vo, BFundMonitorEntity.class);

                // 插入数据库
                bFundMonitorMapper.insert(entity);
                
                // 7. 资金使用情况表-更新或新增
                updateOrInsertFundUsage(bApPayVo, detail, vo, fundInBo);
            }
            log.info("成功插入{}条资金监控记录，付款单ID: {}", fundMonitorVoList.size(), fundInBo.getTrade_id());
        }
    }

    /**
     * 新增一条流水，处理预付款金额减少，业务操作：已付款的付款单操作作废凭证，预付款作废收付金额增加
     * @param fundInBo 资金输入业务对象
     */
    @Override
    public void decreaseAdvancePayment(FundInBo fundInBo) {
        log.info("开始处理预付款金额减少，付款单ID: {}", fundInBo.getTrade_id());

        // 1. 查询付款单主表数据
        BApPayVo bApPayVo = bApPayMapper.selById(fundInBo.getTrade_id());
        if (bApPayVo == null) {
            log.error("付款单不存在，ID: {}", fundInBo.getTrade_id());
            return;
        }
        // 检查付款单状态，必须是作废状态
        if (!DictConstant.DICT_B_AP_PAY_STATUS_TWO.equals(bApPayVo.getStatus())) {
            log.error("付款单状态不正确，当前状态: {}，期望状态: {}（作废），付款单ID: {}",
                    bApPayVo.getStatus(), DictConstant.DICT_B_AP_PAY_STATUS_TWO, fundInBo.getTrade_id());
            return;
        }

        // 检查付款单作废时，是否已经上传凭证完成了付款？也就是作废金额>0
        if (bApPayVo.getCancel_amount_total().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("付款单已作废金额为0，无法进行作废数据更新，付款单ID: {}", fundInBo.getTrade_id());
            return;
        }

        // 2. 查询付款单明细数据
        List<BApPayDetailVo> bApPayDetailList = bApPayDetailMapper.selectById(fundInBo.getTrade_id());
        if (bApPayDetailList == null || bApPayDetailList.isEmpty()) {
            log.error("付款单明细不存在，付款单ID: {}", fundInBo.getTrade_id());
            return;
        }

        // 3. 查询付款来源预付聚合数据
        BApPaySourceAdvanceVo sourceAdvanceVo = bApPaySourceAdvanceMapper.selectAggregatedByApPayId(fundInBo.getTrade_id());

        // 4. 创建资金监控记录列表
        List<BFundMonitorVo> fundMonitorVoList = new ArrayList<>();

        // 5. 循环付款单明细，为每条明细创建资金监控记录
        for (BApPayDetailVo detail : bApPayDetailList) {
            // 生成自动编码
            SCodeEntity codeEntity = fundMonitorAutoCodeService.autoCode();
            String code = codeEntity != null ? codeEntity.getCode() : "";

            BFundMonitorVo fundMonitorVo = new BFundMonitorVo();
            fundMonitorVo.setCode(code);
            fundMonitorVo.setType("1");
            fundMonitorVo.setBusiness_type(FundEventTypeEnum.YUFU_PAY_DEC.getCode());
            fundMonitorVo.setBusiness_type_name(FundEventTypeEnum.YUFU_PAY_DEC.getMsg());
            fundMonitorVo.setSerial_type("b_ap_pay");
            fundMonitorVo.setSerial_id(bApPayVo.getId());
            fundMonitorVo.setSerial_code(bApPayVo.getCode());
            fundMonitorVo.setEnterprise_id(bApPayVo.getPurchaser_id());
            fundMonitorVo.setEnterprise_code(bApPayVo.getPurchaser_code());
            fundMonitorVo.setBank_account_id(detail.getBank_accounts_id());
            fundMonitorVo.setBank_account_code(detail.getBank_accounts_code());
            fundMonitorVo.setBank_accounts_type_id(detail.getBank_accounts_type_id());
            fundMonitorVo.setBank_accounts_type_code(detail.getBank_accounts_type_code());
            fundMonitorVo.setFund_type("0");
            fundMonitorVo.setTrade_id(bApPayVo.getId());
            fundMonitorVo.setTrade_code(bApPayVo.getCode());
            fundMonitorVo.setTrade_type("b_ap_pay");

            // 设置聚合字段，如果查询到了聚合数据
            if (sourceAdvanceVo != null) {
                fundMonitorVo.setTrade_order_id(sourceAdvanceVo.getPo_order_id_gc());
                fundMonitorVo.setTrade_order_code(sourceAdvanceVo.getPo_order_code_gc());
                fundMonitorVo.setTrade_order_type("b_po_order");
                fundMonitorVo.setTrade_contract_id(sourceAdvanceVo.getPo_contract_id_gc());
                fundMonitorVo.setTrade_contract_code(sourceAdvanceVo.getPo_contract_code_gc());
                fundMonitorVo.setTrade_contract_type("b_po_contract");
            }

            fundMonitorVo.setAmount(bApPayVo.getPaid_amount_total());

            fundMonitorVoList.add(fundMonitorVo);
        }

        // 6. 根据填充的BFundMonitorVo列表，通过实体类插入数据到b_fund_monitor
        if (!fundMonitorVoList.isEmpty()) {
            for (int i = 0; i < fundMonitorVoList.size(); i++) {
                BFundMonitorVo vo = fundMonitorVoList.get(i);
                BApPayDetailVo detail = bApPayDetailList.get(i);
                
                // 将VO转换为Entity，使用BeanUtilsSupport进行属性拷贝
                BFundMonitorEntity entity = (BFundMonitorEntity) BeanUtilsSupport.copyProperties(vo, BFundMonitorEntity.class);

                // 插入数据库
                bFundMonitorMapper.insert(entity);
                
                // 7. 资金使用情况表-更新或新增
                updateOrInsertFundUsage(bApPayVo, detail, vo, fundInBo);
            }
            log.info("成功插入{}条资金监控记录（减少），付款单ID: {}", fundMonitorVoList.size(), fundInBo.getTrade_id());
        }
    }

    /**
     * 更新或新增资金使用情况表
     * @param bApPayVo 付款单主表数据
     * @param detail 付款单明细数据
     * @param fundMonitorVo 资金监控VO
     * @param fundInBo 资金输入业务对象
     */
    private void updateOrInsertFundUsage(BApPayVo bApPayVo, BApPayDetailVo detail, BFundMonitorVo fundMonitorVo, FundInBo fundInBo) {
        log.info("开始处理资金使用情况表同步，企业ID: {}, 银行账户ID: {}", 
                 bApPayVo.getPurchaser_id(), detail.getBank_accounts_id());

        // 1. 查询现有的资金使用情况记录
        BFundUsageVo existingUsage = baseMapper.selectByCondition(
            bApPayVo.getPurchaser_id(),
            detail.getBank_accounts_id(),
            detail.getBank_accounts_type_id(),
            "0" // 资金类型：0-资金池
        );

        if (existingUsage == null) {
            // 2. 如果不存在，新增记录
            insertNewFundUsage(bApPayVo, detail, fundMonitorVo, fundInBo);
        } else {
            // 3. 如果存在，更新记录
            updateExistingFundUsage(existingUsage, bApPayVo, fundInBo);
        }
    }

    /**
     * 新增资金使用情况记录
     * @param bApPayVo 付款单主表数据
     * @param detail 付款单明细数据
     * @param fundMonitorVo 资金监控VO
     * @param fundInBo 资金输入业务对象
     */
    private void insertNewFundUsage(BApPayVo bApPayVo, BApPayDetailVo detail, BFundMonitorVo fundMonitorVo, FundInBo fundInBo) {
        log.info("新增资金使用情况记录，企业ID: {}, 银行账户ID: {}", 
                 bApPayVo.getPurchaser_id(), detail.getBank_accounts_id());

        // 生成自动编码
        SCodeEntity codeEntity = fundUsageAutoCodeService.autoCode();
        String code = codeEntity != null ? codeEntity.getCode() : "";

        // 创建新的资金使用情况记录
        BFundUsageVo newUsageVo = new BFundUsageVo();
        newUsageVo.setCode(code);
        newUsageVo.setEnterprise_id(bApPayVo.getPurchaser_id());
        newUsageVo.setEnterprise_code(bApPayVo.getPurchaser_code());
        newUsageVo.setBank_account_id(detail.getBank_accounts_id());
        newUsageVo.setBank_account_code(detail.getBank_accounts_code());
        newUsageVo.setBank_accounts_type_id(detail.getBank_accounts_type_id());
        newUsageVo.setBank_accounts_type_code(detail.getBank_accounts_type_code());
        newUsageVo.setType("0"); // 资金类型：0-资金池
        newUsageVo.setTrade_id(null); // 资金池，交易ID为null
        newUsageVo.setTrade_code(null); // 资金池，交易编号为null
        newUsageVo.setTrade_type(null); // 资金池，交易类型为null

        // 设置金额字段
        newUsageVo.setIncrease_amount_lock(BigDecimal.ZERO);
        newUsageVo.setDecrease_amount_lock(BigDecimal.ZERO);
        newUsageVo.setRefund_amount(BigDecimal.ZERO);
        newUsageVo.setCancel_refund_amount(BigDecimal.ZERO);
        newUsageVo.setSettlement_amount(BigDecimal.ZERO);

        // 根据资金事件类型设置不同的金额字段
        switch (fundInBo.getFund_event()) {
            case "YUFU_PAY_INC":
                log.info("处理预付款金额增加，设置收付金额: {}", bApPayVo.getPaid_amount_total());
                newUsageVo.setPr_amount(bApPayVo.getPaid_amount_total()); // 本次流水金额
                newUsageVo.setCancel_pr_amount(BigDecimal.ZERO);
                break;
            case "YUFU_PAY_DEC":
                log.info("处理预付款金额减少，设置作废收付金额: {}", bApPayVo.getPaid_amount_total());
                newUsageVo.setPr_amount(BigDecimal.ZERO);
                newUsageVo.setCancel_pr_amount(bApPayVo.getPaid_amount_total()); // 作废收付金额为本次流水金额
                break;
            default:
                log.warn("未知的资金事件类型: {}", fundInBo.getFund_event());
                break;
        }

        // 计算可用金额: pr_amount - cancel_pr_amount + refund_amount - cancel_refund_amount - settlement_amount
        BigDecimal amount = newUsageVo.getPr_amount()
                .subtract(newUsageVo.getCancel_pr_amount())
                .add(newUsageVo.getRefund_amount())
                .subtract(newUsageVo.getCancel_refund_amount())
                .subtract(newUsageVo.getSettlement_amount());
        newUsageVo.setAmount(amount);

        // 转换为Entity并插入数据库
        BFundUsageEntity entity = (BFundUsageEntity) BeanUtilsSupport.copyProperties(newUsageVo, BFundUsageEntity.class);
        baseMapper.insert(entity);

        log.info("成功新增资金使用情况记录，编号: {}, 金额: {}", code, amount);
    }

    /**
     * 更新现有资金使用情况记录
     * @param existingUsage 现有记录
     * @param bApPayVo 付款单主表数据
     * @param fundInBo 资金输入业务对象
     */
    private void updateExistingFundUsage(BFundUsageVo existingUsage, BApPayVo bApPayVo, FundInBo fundInBo) {
        log.info("更新现有资金使用情况记录，ID: {}, 资金事件: {}", 
                 existingUsage.getId(), fundInBo.getFund_event());

        // 根据资金事件类型更新不同的金额字段
        switch (fundInBo.getFund_event()) {
            case "YUFU_PAY_INC":
                log.info("处理预付款金额增加，累加收付金额: {}", bApPayVo.getPaid_amount_total());
                BigDecimal newPrAmount = existingUsage.getPr_amount().add(bApPayVo.getPaid_amount_total());
                existingUsage.setPr_amount(newPrAmount);
                break;
            case "YUFU_PAY_DEC":
                log.info("处理预付款金额减少，累加作废收付金额: {}", bApPayVo.getPaid_amount_total());
                BigDecimal currentCancelAmount = existingUsage.getCancel_pr_amount() != null ? 
                        existingUsage.getCancel_pr_amount() : BigDecimal.ZERO;
                BigDecimal newCancelAmount = currentCancelAmount.add(bApPayVo.getPaid_amount_total());
                existingUsage.setCancel_pr_amount(newCancelAmount);
                break;
            default:
                log.warn("未知的资金事件类型: {}", fundInBo.getFund_event());
                break;
        }

        // 重新计算可用金额
        BigDecimal amount = existingUsage.getPr_amount()
                .subtract(existingUsage.getCancel_pr_amount() != null ? existingUsage.getCancel_pr_amount() : BigDecimal.ZERO)
                .add(existingUsage.getRefund_amount() != null ? existingUsage.getRefund_amount() : BigDecimal.ZERO)
                .subtract(existingUsage.getCancel_refund_amount() != null ? existingUsage.getCancel_refund_amount() : BigDecimal.ZERO)
                .subtract(existingUsage.getSettlement_amount() != null ? existingUsage.getSettlement_amount() : BigDecimal.ZERO);
        existingUsage.setAmount(amount);

        // 转换为Entity并更新数据库
        BFundUsageEntity entity = (BFundUsageEntity) BeanUtilsSupport.copyProperties(existingUsage, BFundUsageEntity.class);
        baseMapper.updateById(entity);

        log.info("成功更新资金使用情况记录，ID: {}, 可用金额: {}", 
                 existingUsage.getId(), amount);
    }

}