package com.xinyirun.scm.core.system.mapper.business.po.pocontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    @Select(""" 
            -- 根据采购合同主表ID查询财务汇总数据
            select * from b_po_contract_total 
            -- poContractId: 采购合同主表ID
            where po_contract_id = #{poContractId}
            """)
    BPoContractTotalVo selectByPoContractId(@Param("poContractId") Integer poContractId);

    /**
     * 根据应付账款ID查询对应的采购合同信息
     * @param apId 应付账款ID
     * @return PoContractVo
     */
    @Select(""" 
            -- 根据应付账款ID查询对应的采购合同信息，通过应付预付来源关联
            SELECT 
              t3.* 
            FROM 
              b_ap_total t1 
              -- 关联应付预付来源表
              LEFT JOIN b_ap_source_advance t2 ON t2.ap_id = t1.ap_id 
              -- 通过合同编号关联采购合同，contract_code: 合同编号，为用户手写编号，如果页面未输入，等于code自动编号
              LEFT JOIN b_po_contract t3 ON t3.contract_code = t2.po_contract_code 
            WHERE TRUE 
              -- apId: 应付账款ID参数
              AND t1.ap_id = #{apId}
            """)
    BPoContractVo getPoContractTotalByApId(@Param("apId") Integer apId);    /**
     * 更新采购合同预付款汇总数据（优化版）
     * 根据合同ID汇总其下所有采购订单的预付款数据到合同级别
     * 使用一次JOIN子查询汇总所有字段，避免多次重复查询
     * @param contractId 合同ID
     * @return 更新记录数
     */
    @Update(""" 
            <script>
            -- 更新采购合同预付款汇总数据，根据下属所有采购订单汇总预付款信息
            UPDATE b_po_contract_total t1
            INNER JOIN (
                -- 子查询：汇总指定合同下所有采购订单的预付款数据
                SELECT
                    t3.po_contract_id,
                    -- advance_unpay_total: 预付款未付总金额
                    COALESCE(SUM(t2.advance_unpay_total), 0) AS sum_advance_unpay_total,
                    -- advance_paid_total: 预付款已付款总金额
                    COALESCE(SUM(t2.advance_paid_total), 0) AS sum_advance_paid_total,
                    -- advance_pay_total: 预付款计划付款金额
                    COALESCE(SUM(t2.advance_pay_total), 0) AS sum_advance_pay_total,
                    -- advance_stoppay_total: 预付款已中止总金额
                    COALESCE(SUM(t2.advance_stoppay_total), 0) AS sum_advance_stoppay_total
                FROM b_po_order_total t2
                INNER JOIN b_po_order t3 ON t2.po_order_id = t3.id
                -- contractId: 合同ID参数
                WHERE t3.po_contract_id = #{contractId}
                GROUP BY t3.po_contract_id
            ) AS summary ON t1.po_contract_id = summary.po_contract_id
            SET
                t1.advance_unpay_total = summary.sum_advance_unpay_total,
                t1.advance_paid_total = summary.sum_advance_paid_total,
                t1.advance_pay_total = summary.sum_advance_pay_total,
                t1.advance_stoppay_total = summary.sum_advance_stoppay_total
            -- contractId: 合同ID参数
            WHERE t1.po_contract_id = #{contractId}
            </script>
            """)
    int updateContractAdvanceTotalData(@Param("contractId") Integer contractId);

    /**
     * 更新采购合同订单笔数
     * 根据合同ID统计其下所有采购订单的数量并更新到合同汇总表
     * @param contractId 合同ID
     * @return 更新记录数
     */
    @Update(""" 
            -- 更新采购合同订单笔数，统计合同下所有采购订单数量
            UPDATE b_po_contract_total t1
            SET t1.order_count = (
                -- 子查询：统计指定合同下的采购订单数量
                SELECT COUNT(1)
                FROM b_po_order t2
                -- contractId: 合同ID参数
                WHERE t2.po_contract_id = #{contractId}
                AND t2.is_deleted = 0
            )
            WHERE t1.po_contract_id = #{contractId}
            """)
    int updateContractOrderCount(@Param("contractId") Integer contractId);

    /**
     * 根据合同状态查询采购合同财务信息示例
     * 演示如何正确处理status常量条件
     * @param contractStatus 合同状态
     * @return 合同财务信息列表
     */
    @Select(""" 
            -- 根据合同状态查询采购合同财务信息
            -- 状态说明：0-进行中，1-作废，2-已完成，3-中止（对应SystemConstants.API_STATUS_*常量）
            SELECT 
                t1.*,
                t2.contract_code,
                t2.contract_name,
                t2.status as contract_status
            FROM b_po_contract_total t1
            INNER JOIN b_po_contract t2 ON t1.po_contract_id = t2.id
            WHERE TRUE
              -- contractStatus: 合同状态参数，0-进行中,1-作废,2-已完成,3-中止
              AND t2.status = #{contractStatus}
            ORDER BY t2.c_time DESC
            """)
    List<BPoContractTotalVo> selectByContractStatus(@Param("contractStatus") String contractStatus);

    /**
     * 查询进行中的采购合同财务信息
     * 直接使用常量值"0"，不使用变量
     */
    @Select(""" 
            -- 查询进行中的采购合同财务信息
            -- status = "0": 进行中状态（对应SystemConstants.API_STATUS_PROGRESS = "0"）
            SELECT 
                t1.*,
                t2.contract_code,
                t2.contract_name
            FROM b_po_contract_total t1
            INNER JOIN b_po_contract t2 ON t1.po_contract_id = t2.id
            WHERE TRUE
              -- 直接使用常量值"0"，对应SystemConstants.API_STATUS_PROGRESS
              AND t2.status = "0"
            ORDER BY t2.c_time DESC
            """)
    List<BPoContractTotalVo> selectProgressContractTotals();

}
