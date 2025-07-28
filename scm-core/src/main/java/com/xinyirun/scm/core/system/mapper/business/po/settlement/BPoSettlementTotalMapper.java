package com.xinyirun.scm.core.system.mapper.business.po.settlement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.settlement.BPoSettlementTotalEntity;
import org.apache.ibatis.annotations.*;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 采购结算明细-数据汇总 Mapper 接口
 */
@Mapper
public interface BPoSettlementTotalMapper extends BaseMapper<BPoSettlementTotalEntity> {

    /**
     * 根据合同ID查询采购结算ID列表
     * @param contractId 合同ID
     * @return 采购结算ID列表
     */
    @Select("""
            SELECT po_settlement_id 
            FROM b_po_settlement_detail_source_inbound 
            WHERE po_contract_id = #{contractId}
            """)
    List<Integer> selectSettlementIdsByContractId(@Param("contractId") Integer contractId);

    /**
     * 批量更新采购结算汇总数据
     * @param poSettlementIds 采购结算ID集合
     * @return 更新行数
     */
    @Update("""
            <script>
            UPDATE b_po_settlement_total t2
            JOIN (
                SELECT 
                    po_settlement_id,
                    COALESCE(SUM(processing_qty), 0) as sum_processing_qty,
                    COALESCE(SUM(processing_weight), 0) as sum_processing_weight,
                    COALESCE(SUM(processing_volume), 0) as sum_processing_volume,
                    COALESCE(SUM(unprocessed_qty), 0) as sum_unprocessed_qty,
                    COALESCE(SUM(unprocessed_weight), 0) as sum_unprocessed_weight,
                    COALESCE(SUM(unprocessed_volume), 0) as sum_unprocessed_volume,
                    COALESCE(SUM(processed_qty), 0) as sum_processed_qty,
                    COALESCE(SUM(processed_weight), 0) as sum_processed_weight,
                    COALESCE(SUM(processed_volume), 0) as sum_processed_volume,
                    COALESCE(SUM(planned_qty), 0) as sum_planned_qty,
                    COALESCE(SUM(planned_weight), 0) as sum_planned_weight,
                    COALESCE(SUM(planned_volume), 0) as sum_planned_volume,
                    COALESCE(SUM(planned_amount), 0) as sum_planned_amount,
                    COALESCE(SUM(settled_qty), 0) as sum_settled_qty,
                    COALESCE(SUM(settled_weight), 0) as sum_settled_weight,
                    COALESCE(SUM(settled_volume), 0) as sum_settled_volume,
                    COALESCE(SUM(settled_amount), 0) as sum_settled_amount
                FROM b_po_settlement_detail_source_inbound
                WHERE po_settlement_id IN 
                <foreach collection="poSettlementIds" item="item" open="(" close=")" separator=",">
                    #{item}
                </foreach>
                GROUP BY po_settlement_id
            ) t1 ON t2.po_settlement_id = t1.po_settlement_id
            SET 
                t2.processing_qty = t1.sum_processing_qty,
                t2.processing_weight = t1.sum_processing_weight,
                t2.processing_volume = t1.sum_processing_volume,
                t2.unprocessed_qty = t1.sum_unprocessed_qty,
                t2.unprocessed_weight = t1.sum_unprocessed_weight,
                t2.unprocessed_volume = t1.sum_unprocessed_volume,
                t2.processed_qty = t1.sum_processed_qty,
                t2.processed_weight = t1.sum_processed_weight,
                t2.processed_volume = t1.sum_processed_volume,
                t2.planned_qty = t1.sum_planned_qty,
                t2.planned_weight = t1.sum_planned_weight,
                t2.planned_volume = t1.sum_planned_volume,
                t2.planned_amount = t1.sum_planned_amount,
                t2.settled_qty = t1.sum_settled_qty,
                t2.settled_weight = t1.sum_settled_weight,
                t2.settled_volume = t1.sum_settled_volume,
                t2.settled_amount = t1.sum_settled_amount
            WHERE t2.po_settlement_id IN 
            <foreach collection="poSettlementIds" item="item" open="(" close=")" separator=",">
                #{item}
            </foreach>
            </script>
            """)
    int batchUpdateSettlementTotal(@Param("poSettlementIds") LinkedHashSet<Integer> poSettlementIds);

    /**
     * 插入缺失的结算汇总记录（使用INSERT SELECT NOT EXISTS）
     * @param poSettlementIds 采购结算ID集合
     * @return 插入行数
     */
    @Insert("""
            <script>
            INSERT INTO b_po_settlement_total (
                po_settlement_id
            )
            SELECT  
                po_settlement_id
            FROM b_po_settlement_detail_source_inbound t1
            WHERE t1.po_settlement_id IN (
                <foreach collection="poSettlementIds" item="item" separator=",">
                    #{item}
                </foreach>
            )
            AND NOT EXISTS (
                SELECT 1 FROM b_po_settlement_total t2 
                WHERE t2.po_settlement_id = t1.po_settlement_id
            )
            </script>
            """)
    int insertMissingSettlementTotal(@Param("poSettlementIds") LinkedHashSet<Integer> poSettlementIds);

} 