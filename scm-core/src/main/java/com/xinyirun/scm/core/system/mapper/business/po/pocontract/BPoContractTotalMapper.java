package com.xinyirun.scm.core.system.mapper.business.po.pocontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 采购合同表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Repository
public interface BPoContractTotalMapper extends BaseMapper<BPoContractTotalEntity> {

    /**
     * 根据采购合同主表ID查询采购合同财务信息
     */
    @Select("select * from b_po_contract_total where po_contract_id = #{poContractId}")
    BPoContractTotalVo selectByPoContractId(@Param("poContractId") Integer poContractId);

    /**
     * 根据应付账款ID查询对应的采购合同信息
     * @param apId 应付账款ID
     * @return PoContractVo
     */
    @Select("SELECT " +
            "  t3.* " +
            "FROM " +
            "  b_ap_total t1 " +
            "  LEFT JOIN b_ap_source_advance t2 ON t2.ap_id = t1.ap_id " +
            "  LEFT JOIN b_po_contract t3 ON t3.contract_code = t2.po_contract_code " +
            "WHERE TRUE " +
            "  AND t1.ap_id = #{apId}")
    BPoContractVo getPoContractTotalByApId(@Param("apId") Integer apId);    /**
     * 更新采购合同预付款汇总数据（优化版）
     * 根据合同ID汇总其下所有采购订单的预付款数据到合同级别
     * 使用一次JOIN子查询汇总所有字段，避免多次重复查询
     * @param contractId 合同ID
     * @return 更新记录数
     */
    @Update("<script>                                                                                                                                           " +
            "    UPDATE b_po_contract_total t1                                                                                                                   " +
            "    INNER JOIN (                                                                                                                                    " +
            "        SELECT                                                                                                                                      " +
            "            t3.po_contract_id,                                                                                                                      " +
            "            COALESCE(SUM(t2.advance_unpay_total), 0) AS sum_advance_unpay_total,                                                                   " +
            "            COALESCE(SUM(t2.advance_paid_total), 0) AS sum_advance_paid_total,                                                                     " +
            "            COALESCE(SUM(t2.advance_pay_total), 0) AS sum_advance_pay_total,                                                                       " +
            "            COALESCE(SUM(t2.advance_stoppay_total), 0) AS sum_advance_stoppay_total                                                                " +
            "        FROM b_po_order_total t2                                                                                                                    " +
            "        INNER JOIN b_po_order t3 ON t2.po_order_id = t3.id                                                                                         " +
            "        WHERE t3.po_contract_id = #{contractId}                                                                                                     " +
            "        GROUP BY t3.po_contract_id                                                                                                                  " +
            "    ) AS summary ON t1.po_contract_id = summary.po_contract_id                                                                                      " +
            "    SET                                                                                                                                             " +
            "        t1.advance_unpay_total = summary.sum_advance_unpay_total,                                                                                   " +
            "        t1.advance_paid_total = summary.sum_advance_paid_total,                                                                                     " +
            "        t1.advance_pay_total = summary.sum_advance_pay_total,                                                                                       " +
            "        t1.advance_stoppay_total = summary.sum_advance_stoppay_total                                                                                " +
            "    WHERE t1.po_contract_id = #{contractId}                                                                                                        " +
            "</script>                                                                                                                                           ")
    int updateContractAdvanceTotalData(@Param("contractId") Integer contractId);

}
