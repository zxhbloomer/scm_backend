package com.xinyirun.scm.core.system.mapper.business.so.soorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

/**
 * <p>
 * 销售订单汇总表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Repository
public interface BSoOrderTotalMapper extends BaseMapper<BSoOrderTotalEntity> {

    /**
     * 根据销售订单主表ID查询销售订单财务信息
     */
    @Select("select * from b_so_order_total where so_order_id = #{soId}")
    BSoOrderTotalVo selectBySoId(@Param("soId") Integer soId);

    /**
     * 根据应收账款ID查询对应的销售订单信息
     * @param arId 应收账款ID
     * @return BSoOrderVo
     */
    @Select("SELECT " +
            "  t3.* " +
            "FROM " +
            "  b_ar_total t1 " +
            "  LEFT JOIN b_ar_source_advance t2 ON t2.ar_id = t1.ar_id " +
            "  LEFT JOIN b_so_order t3 ON t3.code = t2.so_order_code " +
            "WHERE TRUE " +
            "  AND t1.ar_id = #{arId}")
    BSoOrderVo getSoOrderTotalByArId(@Param("arId") Integer arId);

    /**
     * 批量更新销售订单总计数据
     * 根据销售合同ID下的订单详情重新计算并更新订单总计表的金额、税额、数量
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total t3
                JOIN (
                    SELECT
                        t2.so_order_id,
                        SUM(t2.amount) AS total_amount,
                        SUM(t2.tax_amount) AS total_tax_amount,
                        SUM(t2.qty) AS total_qty
                    FROM b_so_order_detail t2
                    JOIN b_so_order t1 ON t1.id = t2.so_order_id
                    WHERE t1.id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY t2.so_order_id
                ) agg ON t3.so_order_id = agg.so_order_id
                SET
                    t3.amount_total = agg.total_amount,
                    t3.tax_amount_total = agg.total_tax_amount,
                    t3.qty_total = agg.total_qty
                WHERE EXISTS (
                    SELECT 1
                    FROM b_so_order t1
                    WHERE t1.id = t3.so_order_id
                    AND t1.id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                )
            </script>
            """)
    int updateSoOrderTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新预收款数据
     * 根据销售订单ID更新预收款相关的总计数据
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total t1
                JOIN b_so_order t2 ON t1.so_order_id = t2.id
                LEFT JOIN (
                    SELECT
                        so_order_id,
                        SUM(unreceive_amount_total) AS total_unreceive,
                        SUM(received_amount_total) AS total_received,
                        SUM(receiving_amount_total) AS total_receiving,
                        SUM(receivable_amount_total) AS total_receivable,
                        SUM(stopreceive_amount_total) AS stopreceive_amount,
                        SUM(cancelreceive_amount_total) AS cancel_amount
                    FROM b_ar_source_advance
                    WHERE so_order_id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY so_order_id
                ) tab ON t1.so_order_id = tab.so_order_id
                SET
                    t1.advance_unreceive_total = COALESCE(tab.total_unreceive, 0),
                    t1.advance_received_total = COALESCE(tab.total_received, 0),
                    t1.advance_receiving_total = COALESCE(tab.total_receiving, 0),
                    t1.advance_receive_total = COALESCE(tab.total_receivable, 0),
                    t1.advance_stopreceive_total = COALESCE(tab.stopreceive_amount, 0),
                    t1.advance_cancelreceive_total = COALESCE(tab.cancel_amount, 0)
                WHERE t1.so_order_id IN
                <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateAdvanceAmountTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新已收款总金额数据
     * 根据销售订单ID更新已收款总金额
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total t1
                INNER JOIN b_so_order t2 ON t1.so_order_id = t2.id
                INNER JOIN (
                    SELECT
                        so_order_id,
                        SUM(IFNULL(received_amount_total, 0)) as total_received_amount
                    FROM b_ar_source_advance t3
                    WHERE t3.so_order_id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY so_order_id
                ) t3 ON t1.so_order_id = t3.so_order_id
                SET t1.received_total = t3.total_received_amount
                WHERE t2.id IN
                <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateReceivedTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新出库计划和货权转移相关统计数据
     * 根据销售订单ID汇总明细总计表数据更新订单总计表
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
        <script>
        UPDATE b_so_order_total t1
        JOIN (
            SELECT 
                t2.so_order_id,
                SUM(IFNULL(t2.outbound_processing_qty_total, 0)) AS sum_outbound_processing_qty_total,
                SUM(IFNULL(t2.outbound_processing_weight_total, 0)) AS sum_outbound_processing_weight_total,
                SUM(IFNULL(t2.outbound_processing_volume_total, 0)) AS sum_outbound_processing_volume_total,
                SUM(IFNULL(t2.outbound_unprocessed_qty_total, 0)) AS sum_outbound_unprocessed_qty_total,
                SUM(IFNULL(t2.outbound_unprocessed_weight_total, 0)) AS sum_outbound_unprocessed_weight_total,
                SUM(IFNULL(t2.outbound_unprocessed_volume_total, 0)) AS sum_outbound_unprocessed_volume_total,
                SUM(IFNULL(t2.outbound_processed_qty_total, 0)) AS sum_outbound_processed_qty_total,
                SUM(IFNULL(t2.outbound_processed_weight_total, 0)) AS sum_outbound_processed_weight_total,
                SUM(IFNULL(t2.outbound_processed_volume_total, 0)) AS sum_outbound_processed_volume_total,
                SUM(IFNULL(t2.outbound_cancel_qty_total, 0)) AS sum_outbound_cancel_qty_total,
                SUM(IFNULL(t2.outbound_cancel_weight_total, 0)) AS sum_outbound_cancel_weight_total,
                SUM(IFNULL(t2.outbound_cancel_volume_total, 0)) AS sum_outbound_cancel_volume_total,
                SUM(IFNULL(t2.inventory_out_total, 0)) AS sum_inventory_out_total,
                SUM(IFNULL(t2.inventory_out_plan_total, 0)) AS sum_inventory_out_plan_total,
                SUM(IFNULL(t2.cargo_right_untransfer_qty_total, 0)) AS sum_cargo_right_untransfer_qty_total,
                SUM(IFNULL(t2.cargo_right_transfering_qty_total, 0)) AS sum_cargo_right_transfering_qty_total,
                SUM(IFNULL(t2.cargo_right_transferred_qty_total, 0)) AS sum_cargo_right_transferred_qty_total,
                SUM(IFNULL(t2.cargo_right_transfer_cancel_qty_total, 0)) AS sum_cargo_right_transfer_cancel_qty_total
            FROM b_so_order_detail_total t2
            WHERE t2.so_order_id IN
            <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            GROUP BY t2.so_order_id
        ) t3 ON t1.so_order_id = t3.so_order_id
        SET 
            t1.outbound_processing_qty_total = t3.sum_outbound_processing_qty_total,
            t1.outbound_processing_weight_total = t3.sum_outbound_processing_weight_total,
            t1.outbound_processing_volume_total = t3.sum_outbound_processing_volume_total,
            t1.outbound_unprocessed_qty_total = t3.sum_outbound_unprocessed_qty_total,
            t1.outbound_unprocessed_weight_total = t3.sum_outbound_unprocessed_weight_total,
            t1.outbound_unprocessed_volume_total = t3.sum_outbound_unprocessed_volume_total,
            t1.outbound_processed_qty_total = t3.sum_outbound_processed_qty_total,
            t1.outbound_processed_weight_total = t3.sum_outbound_processed_weight_total,
            t1.outbound_processed_volume_total = t3.sum_outbound_processed_volume_total,
            t1.outbound_cancel_qty_total = t3.sum_outbound_cancel_qty_total,
            t1.outbound_cancel_weight_total = t3.sum_outbound_cancel_weight_total,
            t1.outbound_cancel_volume_total = t3.sum_outbound_cancel_volume_total,
            t1.inventory_out_total = t3.sum_inventory_out_total,
            t1.inventory_out_plan_total = t3.sum_inventory_out_plan_total,
            t1.cargo_right_untransfer_qty_total = t3.sum_cargo_right_untransfer_qty_total,
            t1.cargo_right_transfering_qty_total = t3.sum_cargo_right_transfering_qty_total,
            t1.cargo_right_transferred_qty_total = t3.sum_cargo_right_transferred_qty_total,
            t1.cargo_right_transfer_cancel_qty_total = t3.sum_cargo_right_transfer_cancel_qty_total
        </script>
        """)
    int updateOutboundAndCargoRightTransferTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新可结算数量汇总
     * 计算公式：settle_can_qty_total = inventory_out_total - settle_planned_qty_total （实际出库汇总-应结算数量汇总）
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total
                SET settle_can_qty_total =  IFNULL(cargo_right_transferred_qty_total,0) + IFNULL(inventory_out_total, 0) - IFNULL(settle_planned_qty_total, 0)
                WHERE so_order_id IN
                <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateSettleCanQtyTotal(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新退款数据
     * 根据销售订单ID更新退款相关的总计数据
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total t1
                LEFT JOIN (
                    SELECT
                        so_order_id,
                        SUM(IFNULL(refundable_amount_total, 0)) AS total_refundable,
                        SUM(IFNULL(refunded_amount_total, 0)) AS total_refunded,
                        SUM(IFNULL(refunding_amount_total, 0)) AS total_refunding,
                        SUM(IFNULL(unrefund_amount_total, 0)) AS total_unrefund,
                        SUM(IFNULL(cancelrefund_amount_total, 0)) AS total_cancelrefund
                    FROM b_ar_refund_total
                    WHERE so_order_id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY so_order_id
                ) t2 ON t1.so_order_id = t2.so_order_id
                SET
                    t1.advance_refundable_total = COALESCE(t2.total_refundable, 0),
                    t1.advance_refunded_total = COALESCE(t2.total_refunded, 0),
                    t1.advance_refunding_total = COALESCE(t2.total_refunding, 0),
                    t1.advance_unrefund_total = COALESCE(t2.total_unrefund, 0),
                    t1.advance_cancelrefund_total = COALESCE(t2.total_cancelrefund, 0)
                WHERE t1.so_order_id IN
                <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateRefundAmountTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

    /**
     * 更新货权转移数据
     * 根据销售订单ID从明细总计表汇总货权转移相关的统计数据
     * @param so_order_id 销售订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_so_order_total t1
                LEFT JOIN (
                    SELECT
                        so_order_id,
                        SUM(IFNULL(cargo_right_untransfer_qty_total, 0)) AS total_untransfer_qty,
                        SUM(IFNULL(cargo_right_transfering_qty_total, 0)) AS total_transfering_qty,
                        SUM(IFNULL(cargo_right_transferred_qty_total, 0)) AS total_transferred_qty,
                        SUM(IFNULL(cargo_right_transfer_cancel_qty_total, 0)) AS total_transfer_cancel_qty
                    FROM b_so_order_detail_total
                    WHERE so_order_id IN
                    <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY so_order_id
                ) t2 ON t1.so_order_id = t2.so_order_id
                SET
                    t1.cargo_right_untransfer_qty_total = COALESCE(t2.total_untransfer_qty, 0),
                    t1.cargo_right_transfering_qty_total = COALESCE(t2.total_transfering_qty, 0),
                    t1.cargo_right_transferred_qty_total = COALESCE(t2.total_transferred_qty, 0),
                    t1.cargo_right_transfer_cancel_qty_total = COALESCE(t2.total_transfer_cancel_qty, 0)
                WHERE t1.so_order_id IN
                <foreach collection='so_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateCargoRightTransferTotalData(@Param("so_order_id") LinkedHashSet<Integer> so_order_id);

}