package com.xinyirun.scm.core.system.mapper.business.po.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
@Repository
public interface BPoOrderTotalMapper extends BaseMapper<BPoOrderTotalEntity> {

    /**
     * 根据采购订单主表ID查询采购订单财务信息
     */
    @Select("select * from b_po_order_total where po_order_id = #{poId}")
    BPoOrderTotalVo selectByPoId(@Param("poId") Integer poId);

    /**
     * 根据应付账款ID查询对应的采购订单信息
     * @param apId 应付账款ID
     * @return PoOrderVo
     */
    @Select("SELECT " +
            "  t3.* " +
            "FROM " +
            "  b_ap_total t1 " +
            "  LEFT JOIN b_ap_source_advance t2 ON t2.ap_id = t1.ap_id " +
            "  LEFT JOIN b_po_order t3 ON t3.code = t2.po_order_code " +
            "WHERE TRUE " +
            "  AND t1.ap_id = #{apId}")
    PoOrderVo getPoOrderTotalByApId(@Param("apId") Integer apId);

    /**
     * 批量更新采购订单总计数据
     * 根据采购合同ID下的订单详情重新计算并更新订单总计表的金额、税额、数量
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_po_order_total t3
                JOIN (
                    SELECT
                        t2.po_order_id,
                        SUM(t2.amount) AS total_amount,
                        SUM(t2.tax_amount) AS total_tax_amount,
                        SUM(t2.qty) AS total_qty
                    FROM b_po_order_detail t2
                    JOIN b_po_order t1 ON t1.id = t2.po_order_id
                    WHERE t1.id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY t2.po_order_id
                ) agg ON t3.po_order_id = agg.po_order_id
                SET
                    t3.amount_total = agg.total_amount,
                    t3.tax_amount_total = agg.total_tax_amount,
                    t3.qty_total = agg.total_qty
                WHERE EXISTS (
                    SELECT 1
                    FROM b_po_order t1
                    WHERE t1.id = t3.po_order_id
                    AND t1.id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                )
            </script>
            """)
    int updatePoOrderTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新预付款数据
     * 根据采购订单ID更新预付款相关的总计数据
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_po_order_total t1
                JOIN b_po_order t2 ON t1.po_order_id = t2.id
                LEFT JOIN (
                    SELECT
                        po_order_id,
                        SUM(unpay_amount_total) AS total_unpay,
                        SUM(paid_amount_total) AS total_paid,
                        SUM(paying_amount_total) AS total_paying,
                        SUM(payable_amount_total) AS total_payable,
                        SUM(stoppay_amount_total) AS stoppay_amount,
                        SUM(cancelpay_amount_total) AS cancel_amount
                    FROM b_ap_source_advance
                    WHERE po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) tab ON t1.po_order_id = tab.po_order_id
                SET
                    t1.advance_unpay_total = COALESCE(tab.total_unpay, 0),
                    t1.advance_paid_total = COALESCE(tab.total_paid, 0),
                    t1.advance_paying_total = COALESCE(tab.total_paying, 0),
                    t1.advance_pay_total = COALESCE(tab.total_payable, 0),
                    t1.advance_stoppay_total = COALESCE(tab.stoppay_amount, 0),
                    t1.advance_cancelpay_total = COALESCE(tab.cancel_amount, 0)
                WHERE t1.po_order_id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateAdvanceAmountTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新已付款总金额数据
     * 根据采购订单ID更新已付款总金额
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_po_order_total t1
                INNER JOIN b_po_order t2 ON t1.po_order_id = t2.id
                INNER JOIN (
                    SELECT
                        po_order_id,
                        SUM(IFNULL(paid_amount_total, 0)) as total_paid_amount
                    FROM b_ap_source_advance t3
                    WHERE t3.po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) t3 ON t1.po_order_id = t3.po_order_id
                SET t1.paid_total = t3.total_paid_amount
                WHERE t2.id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updatePaidTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新入库计划相关统计数据
     * 根据采购订单ID汇总明细总计表数据更新订单总计表
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
        <script>
        UPDATE b_po_order_total t1
        JOIN (
            SELECT 
                t2.po_order_id,
                SUM(IFNULL(t2.inbound_processing_qty_total, 0)) AS sum_inbound_processing_qty_total,
                SUM(IFNULL(t2.inbound_processing_weight_total, 0)) AS sum_inbound_processing_weight_total,
                SUM(IFNULL(t2.inbound_processing_volume_total, 0)) AS sum_inbound_processing_volume_total,
                SUM(IFNULL(t2.inbound_unprocessed_qty_total, 0)) AS sum_inbound_unprocessed_qty_total,
                SUM(IFNULL(t2.inbound_unprocessed_weight_total, 0)) AS sum_inbound_unprocessed_weight_total,
                SUM(IFNULL(t2.inbound_unprocessed_volume_total, 0)) AS sum_inbound_unprocessed_volume_total,
                SUM(IFNULL(t2.inbound_processed_qty_total, 0)) AS sum_inbound_processed_qty_total,
                SUM(IFNULL(t2.inbound_processed_weight_total, 0)) AS sum_inbound_processed_weight_total,
                SUM(IFNULL(t2.inbound_processed_volume_total, 0)) AS sum_inbound_processed_volume_total,
                SUM(IFNULL(t2.inbound_cancel_qty_total, 0)) AS sum_inbound_cancel_qty_total,
                SUM(IFNULL(t2.inbound_cancel_weight_total, 0)) AS sum_inbound_cancel_weight_total,
                SUM(IFNULL(t2.inbound_cancel_volume_total, 0)) AS sum_inbound_cancel_volume_total,
                SUM(IFNULL(t2.inventory_in_total, 0)) AS sum_inventory_in_total,
                SUM(IFNULL(t2.inventory_in_plan_total, 0)) AS sum_inventory_in_plan_total
            FROM b_po_order_detail_total t2
            WHERE t2.po_order_id IN
            <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            GROUP BY t2.po_order_id
        ) t3 ON t1.po_order_id = t3.po_order_id
        SET 
            t1.inbound_processing_qty_total = t3.sum_inbound_processing_qty_total,
            t1.inbound_processing_weight_total = t3.sum_inbound_processing_weight_total,
            t1.inbound_processing_volume_total = t3.sum_inbound_processing_volume_total,
            t1.inbound_unprocessed_qty_total = t3.sum_inbound_unprocessed_qty_total,
            t1.inbound_unprocessed_weight_total = t3.sum_inbound_unprocessed_weight_total,
            t1.inbound_unprocessed_volume_total = t3.sum_inbound_unprocessed_volume_total,
            t1.inbound_processed_qty_total = t3.sum_inbound_processed_qty_total,
            t1.inbound_processed_weight_total = t3.sum_inbound_processed_weight_total,
            t1.inbound_processed_volume_total = t3.sum_inbound_processed_volume_total,
            t1.inbound_cancel_qty_total = t3.sum_inbound_cancel_qty_total,
            t1.inbound_cancel_weight_total = t3.sum_inbound_cancel_weight_total,
            t1.inbound_cancel_volume_total = t3.sum_inbound_cancel_volume_total,
            t1.inventory_in_total = t3.sum_inventory_in_total,
            t1.inventory_in_plan_total = t3.sum_inventory_in_plan_total
        </script>
        """)
    int updateInPlanTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新可结算数量汇总
     * 计算公式：settle_can_qty_total = inventory_in_total - settle_planned_qty_total （实际入库汇总-应结算数量汇总）
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_po_order_total
                SET settle_can_qty_total = IFNULL(inventory_in_total, 0) - IFNULL(settle_planned_qty_total, 0)
                WHERE po_order_id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateSettleCanQtyTotal(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新退款数据
     * 根据采购订单ID更新退款相关的总计数据
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                UPDATE b_po_order_total t1
                LEFT JOIN (
                    SELECT
                        po_order_id,
                        SUM(IFNULL(refundable_amount_total, 0)) AS total_refundable,
                        SUM(IFNULL(refunded_amount_total, 0)) AS total_refunded,
                        SUM(IFNULL(refunding_amount_total, 0)) AS total_refunding,
                        SUM(IFNULL(unrefund_amount_total, 0)) AS total_unrefund,
                        SUM(IFNULL(cancelrefund_amount_total, 0)) AS total_cancelrefund
                    FROM b_ap_refund_total
                    WHERE po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) t2 ON t1.po_order_id = t2.po_order_id
                SET
                    t1.advance_refundable_total = COALESCE(t2.total_refundable, 0),
                    t1.advance_refunded_total = COALESCE(t2.total_refunded, 0),
                    t1.advance_refunding_total = COALESCE(t2.total_refunding, 0),
                    t1.advance_unrefund_total = COALESCE(t2.total_unrefund, 0),
                    t1.advance_cancelrefund_total = COALESCE(t2.total_cancelrefund, 0)
                WHERE t1.po_order_id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateRefundAmountTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

}
