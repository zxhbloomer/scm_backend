package com.xinyirun.scm.core.system.mapper.business.so.socontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 销售合同表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Repository
public interface BSoContractTotalMapper extends BaseMapper<BSoContractTotalEntity> {

    /**
     * 根据销售合同主表ID查询销售合同财务信息
     */
    @Select(""" 
            -- 根据销售合同主表ID查询财务汇总数据
            select * from b_so_contract_total 
            -- soContractId: 销售合同主表ID
            where so_contract_id = #{soContractId}
            """)
    BSoContractTotalVo selectBySoContractId(@Param("soContractId") Integer soContractId);

    /**
     * 根据应收账款ID查询对应的销售合同信息
     * @param arId 应收账款ID
     * @return BSoContractVo
     */
    @Select(""" 
            -- 根据应收账款ID查询对应的销售合同信息，通过应收预收来源关联
            SELECT 
              t3.* 
            FROM 
              b_ar_total t1 
              -- 关联应收预收来源表
              LEFT JOIN b_ar_source_advance t2 ON t2.ar_id = t1.ar_id 
              -- 通过合同编号关联销售合同，contract_code: 合同编号，为用户手写编号，如果页面未输入，等于code自动编号
              LEFT JOIN b_so_contract t3 ON t3.contract_code = t2.so_contract_code 
            WHERE TRUE 
              -- arId: 应收账款ID
              AND t1.ar_id = #{arId}
            """)
    BSoContractVo getSoContractTotalByArId(@Param("arId") Integer arId);

    /**
     * 更新销售合同预收款汇总数据（优化版）
     * 根据合同ID汇总其下所有销售订单的预收款数据到合同级别
     * 使用一次JOIN子查询汇总所有字段，避免多次重复查询
     * @param contractId 合同ID
     * @return 更新记录数
     */
    @Update(""" 
            <script>
            -- 更新销售合同预收款汇总数据，根据下属所有销售订单汇总预收款信息
            UPDATE b_so_contract_total t1
            INNER JOIN (
                -- 子查询：汇总指定合同下所有销售订单的预收款数据
                SELECT
                    t3.so_contract_id,
                    -- advance_unreceive_total: 预收款未收总金额
                    COALESCE(SUM(t2.advance_unreceive_total), 0) AS sum_advance_unreceive_total,
                    -- advance_received_total: 预收款已收款总金额
                    COALESCE(SUM(t2.advance_received_total), 0) AS sum_advance_received_total,
                    -- advance_receive_total: 预收款计划收款金额
                    COALESCE(SUM(t2.advance_receive_total), 0) AS sum_advance_receive_total,
                    -- advance_stopreceive_total: 预收款已中止总金额
                    COALESCE(SUM(t2.advance_stopreceive_total), 0) AS sum_advance_stopreceive_total
                FROM b_so_order_total t2
                INNER JOIN b_so_order t3 ON t2.so_order_id = t3.id
                -- contractId: 合同ID
                WHERE t3.so_contract_id = #{contractId}
                GROUP BY t3.so_contract_id
            ) AS summary ON t1.so_contract_id = summary.so_contract_id
            SET
                t1.advance_unreceive_total = summary.sum_advance_unreceive_total,
                t1.advance_received_total = summary.sum_advance_received_total,
                t1.advance_receive_total = summary.sum_advance_receive_total,
                t1.advance_stopreceive_total = summary.sum_advance_stopreceive_total
            -- contractId: 合同ID
            WHERE t1.so_contract_id = #{contractId}
            </script>
            """)
    int updateContractAdvanceTotalData(@Param("contractId") Integer contractId);

    /**
     * 根据合同状态查询销售合同财务信息示例
     * 演示如何正确处理status常量条件
     * @param contractStatus 合同状态
     * @return 合同财务信息列表
     */
    @Select(""" 
            -- 根据合同状态查询销售合同财务信息
            -- 状态说明：0-进行中，1-作废，2-已完成，3-中止（对应SystemConstants.API_STATUS_*常量）
            SELECT 
                t1.*,
                t2.contract_code,
                t2.contract_name,
                t2.status as contract_status
            FROM b_so_contract_total t1
            INNER JOIN b_so_contract t2 ON t1.so_contract_id = t2.id
            WHERE TRUE
              -- contractStatus: 合同状态，0-进行中,1-作废,2-已完成,3-中止
              AND t2.status = #{contractStatus}
            ORDER BY t2.c_time DESC
            """)
    List<BSoContractTotalVo> selectByContractStatus(@Param("contractStatus") String contractStatus);

    /**
     * 查询进行中的销售合同财务信息
     * 直接使用常量值"0"，不使用变量
     */
    @Select(""" 
            -- 查询进行中的销售合同财务信息
            -- status = "0": 进行中状态（对应SystemConstants.API_STATUS_PROGRESS = "0"）
            SELECT 
                t1.*,
                t2.contract_code,
                t2.contract_name
            FROM b_so_contract_total t1
            INNER JOIN b_so_contract t2 ON t1.so_contract_id = t2.id
            WHERE TRUE
              -- 直接使用常量值"0"，对应SystemConstants.API_STATUS_PROGRESS
              AND t2.status = "0"
            ORDER BY t2.c_time DESC
            """)
    List<BSoContractTotalVo> selectProgressContractTotals();

}