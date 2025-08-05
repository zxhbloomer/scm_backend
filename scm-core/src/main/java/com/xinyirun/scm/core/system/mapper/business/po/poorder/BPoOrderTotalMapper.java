package com.xinyirun.scm.core.system.mapper.business.po.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderTotalVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderVo;
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
    @Select("""
            -- 根据采购订单主表ID查询采购订单财务汇总信息
            select * from b_po_order_total 
            -- poId: 采购订单主表ID参数
            where po_order_id = #{poId}
            """)
    BPoOrderTotalVo selectByPoId(@Param("poId") Integer poId);

    /**
     * 根据应付账款ID查询对应的采购订单信息
     * @param apId 应付账款ID
     * @return PoOrderVo
     */
    @Select("""
            -- 根据应付账款ID查询对应的采购订单信息，通过应付预付来源关联
            SELECT 
              t3.* 
            FROM 
              b_ap_total t1 
              -- 关联应付预付来源表
              LEFT JOIN b_ap_source_advance t2 ON t2.ap_id = t1.ap_id 
              -- 通过订单编号关联采购订单，code: 编号自动生成编号，po_order_code: 采购订单编号
              LEFT JOIN b_po_order t3 ON t3.code = t2.po_order_code 
            WHERE TRUE 
              -- apId: 应付账款ID参数
              AND t1.ap_id = #{apId}
            """)
    BPoOrderVo getPoOrderTotalByApId(@Param("apId") Integer apId);

    /**
     * 批量更新采购订单总计数据
     * 根据采购合同ID下的订单详情重新计算并更新订单总计表的金额、税额、数量
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                -- 批量更新采购订单总计数据，根据订单明细重新计算并更新总计表的金额、税额、数量
                UPDATE b_po_order_total t3
                JOIN (
                    -- 子查询：汇总指定采购订单的明细数据
                    SELECT
                        t2.po_order_id,
                        -- amount: 总金额
                        SUM(t2.amount) AS total_amount,
                        -- tax_amount: 税额
                        SUM(t2.tax_amount) AS total_tax_amount,
                        -- qty: 数量
                        SUM(t2.qty) AS total_qty
                    FROM b_po_order_detail t2
                    JOIN b_po_order t1 ON t1.id = t2.po_order_id
                    -- id: 采购订单主表ID参数
                    WHERE t1.id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY t2.po_order_id
                ) agg ON t3.po_order_id = agg.po_order_id
                SET
                    -- 更新总金额、税额、数量字段
                    t3.amount_total = agg.total_amount,
                    t3.tax_amount_total = agg.total_tax_amount,
                    t3.qty_total = agg.total_qty
                WHERE EXISTS (
                    -- 确保只更新指定的采购订单记录
                    SELECT 1
                    FROM b_po_order t1
                    WHERE t1.id = t3.po_order_id
                    -- id: 采购订单主表ID参数
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
                -- 更新预付款数据，根据采购订单ID更新预付款相关的总计数据
                UPDATE b_po_order_total t1
                JOIN b_po_order t2 ON t1.po_order_id = t2.id
                LEFT JOIN (
                    -- 子查询：从应付预付来源表汇总预付款相关数据
                    SELECT
                        po_order_id,
                        -- unpay_amount_total: 未付款金额总计
                        SUM(unpay_amount_total) AS total_unpay,
                        -- paid_amount_total: 已付款金额总计
                        SUM(paid_amount_total) AS total_paid,
                        -- paying_amount_total: 付款中金额总计
                        SUM(paying_amount_total) AS total_paying,
                        -- payable_amount_total: 应付款金额总计
                        SUM(payable_amount_total) AS total_payable,
                        -- stoppay_amount_total: 中止付款金额总计
                        SUM(stoppay_amount_total) AS stoppay_amount,
                        -- cancelpay_amount_total: 取消付款金额总计
                        SUM(cancelpay_amount_total) AS cancel_amount
                    FROM b_ap_source_advance
                    -- id: 采购订单主表ID参数
                    WHERE po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) tab ON t1.po_order_id = tab.po_order_id
                SET
                    -- 更新预付款相关的各个总计字段
                    t1.advance_unpay_total = COALESCE(tab.total_unpay, 0),
                    t1.advance_paid_total = COALESCE(tab.total_paid, 0),
                    t1.advance_paying_total = COALESCE(tab.total_paying, 0),
                    t1.advance_pay_total = COALESCE(tab.total_payable, 0),
                    t1.advance_stoppay_total = COALESCE(tab.stoppay_amount, 0),
                    t1.advance_cancelpay_total = COALESCE(tab.cancel_amount, 0)
                -- id: 采购订单主表ID参数
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
                -- 更新已付款总金额数据，根据采购订单ID更新已付款总金额
                UPDATE b_po_order_total t1
                INNER JOIN b_po_order t2 ON t1.po_order_id = t2.id
                INNER JOIN (
                    -- 子查询：汇总指定采购订单的已付款金额
                    SELECT
                        po_order_id,
                        -- paid_amount_total: 已付款金额总计
                        SUM(IFNULL(paid_amount_total, 0)) as total_paid_amount
                    FROM b_ap_source_advance t3
                    -- id: 采购订单主表ID参数
                    WHERE t3.po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) t3 ON t1.po_order_id = t3.po_order_id
                -- 更新已付款总金额字段
                SET t1.paid_total = t3.total_paid_amount
                -- id: 采购订单主表ID参数
                WHERE t2.id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updatePaidTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新入库计划和货权转移相关统计数据
     * 根据采购订单ID汇总明细总计表数据更新订单总计表
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
        <script>
        -- 更新入库计划和货权转移相关统计数据，根据采购订单ID汇总明细总计表数据更新订单总计表
        UPDATE b_po_order_total t1
        JOIN (
            -- 子查询：从采购订单明细总计表汇总入库和货权转移相关数据
            SELECT 
                t2.po_order_id,
                -- inbound_processing_qty_total: 入库处理中数量总计
                SUM(IFNULL(t2.inbound_processing_qty_total, 0)) AS sum_inbound_processing_qty_total,
                -- inbound_processing_weight_total: 入库处理中重量总计
                SUM(IFNULL(t2.inbound_processing_weight_total, 0)) AS sum_inbound_processing_weight_total,
                -- inbound_processing_volume_total: 入库处理中体积总计
                SUM(IFNULL(t2.inbound_processing_volume_total, 0)) AS sum_inbound_processing_volume_total,
                -- inbound_unprocessed_qty_total: 入库未处理数量总计
                SUM(IFNULL(t2.inbound_unprocessed_qty_total, 0)) AS sum_inbound_unprocessed_qty_total,
                -- inbound_unprocessed_weight_total: 入库未处理重量总计
                SUM(IFNULL(t2.inbound_unprocessed_weight_total, 0)) AS sum_inbound_unprocessed_weight_total,
                -- inbound_unprocessed_volume_total: 入库未处理体积总计
                SUM(IFNULL(t2.inbound_unprocessed_volume_total, 0)) AS sum_inbound_unprocessed_volume_total,
                -- inbound_processed_qty_total: 入库已处理数量总计
                SUM(IFNULL(t2.inbound_processed_qty_total, 0)) AS sum_inbound_processed_qty_total,
                -- inbound_processed_weight_total: 入库已处理重量总计
                SUM(IFNULL(t2.inbound_processed_weight_total, 0)) AS sum_inbound_processed_weight_total,
                -- inbound_processed_volume_total: 入库已处理体积总计
                SUM(IFNULL(t2.inbound_processed_volume_total, 0)) AS sum_inbound_processed_volume_total,
                -- inbound_cancel_qty_total: 入库取消数量总计
                SUM(IFNULL(t2.inbound_cancel_qty_total, 0)) AS sum_inbound_cancel_qty_total,
                -- inbound_cancel_weight_total: 入库取消重量总计
                SUM(IFNULL(t2.inbound_cancel_weight_total, 0)) AS sum_inbound_cancel_weight_total,
                -- inbound_cancel_volume_total: 入库取消体积总计
                SUM(IFNULL(t2.inbound_cancel_volume_total, 0)) AS sum_inbound_cancel_volume_total,
                -- inventory_in_total: 实际入库汇总
                SUM(IFNULL(t2.inventory_in_total, 0)) AS sum_inventory_in_total,
                -- inventory_in_plan_total: 计划入库汇总
                SUM(IFNULL(t2.inventory_in_plan_total, 0)) AS sum_inventory_in_plan_total,
                -- cargo_right_untransfer_qty_total: 货权未转移数量总计
                SUM(IFNULL(t2.cargo_right_untransfer_qty_total, 0)) AS sum_cargo_right_untransfer_qty_total,
                -- cargo_right_transfering_qty_total: 货权转移中数量总计
                SUM(IFNULL(t2.cargo_right_transfering_qty_total, 0)) AS sum_cargo_right_transfering_qty_total,
                -- cargo_right_transferred_qty_total: 货权已转移数量总计
                SUM(IFNULL(t2.cargo_right_transferred_qty_total, 0)) AS sum_cargo_right_transferred_qty_total,
                -- cargo_right_transfer_cancel_qty_total: 货权转移取消数量总计
                SUM(IFNULL(t2.cargo_right_transfer_cancel_qty_total, 0)) AS sum_cargo_right_transfer_cancel_qty_total
            FROM b_po_order_detail_total t2
            -- id: 采购订单主表ID参数
            WHERE t2.po_order_id IN
            <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            GROUP BY t2.po_order_id
        ) t3 ON t1.po_order_id = t3.po_order_id
        SET 
            -- 更新入库相关的所有统计字段
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
            t1.inventory_in_plan_total = t3.sum_inventory_in_plan_total,
            -- 更新货权转移相关的所有统计字段
            t1.cargo_right_untransfer_qty_total = t3.sum_cargo_right_untransfer_qty_total,
            t1.cargo_right_transfering_qty_total = t3.sum_cargo_right_transfering_qty_total,
            t1.cargo_right_transferred_qty_total = t3.sum_cargo_right_transferred_qty_total,
            t1.cargo_right_transfer_cancel_qty_total = t3.sum_cargo_right_transfer_cancel_qty_total
        </script>
        """)
    int updateInboundAndCargoRightTransferTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新可结算数量汇总
     * 计算公式：settle_can_qty_total = inventory_in_total - settle_planned_qty_total （实际入库汇总-应结算数量汇总）
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                -- 更新可结算数量汇总，计算公式：可结算数量 = 货权已转移数量 + 实际入库汇总 - 应结算数量汇总
                UPDATE b_po_order_total
                -- settle_can_qty_total: 可结算数量汇总
                -- cargo_right_transferred_qty_total: 货权已转移数量总计
                -- inventory_in_total: 实际入库汇总
                -- settle_planned_qty_total: 应结算数量汇总
                SET settle_can_qty_total =  IFNULL(cargo_right_transferred_qty_total,0) + IFNULL(inventory_in_total, 0) - IFNULL(settle_planned_qty_total, 0)
                -- id: 采购订单主表ID参数
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
                -- 更新退款数据，根据采购订单ID更新退款相关的总计数据
                UPDATE b_po_order_total t1
                LEFT JOIN (
                    -- 子查询：从应付退款总计表汇总退款相关数据
                    SELECT
                        po_order_id,
                        -- refundable_amount_total: 可退款金额总计
                        SUM(IFNULL(refundable_amount_total, 0)) AS total_refundable,
                        -- refunded_amount_total: 已退款金额总计
                        SUM(IFNULL(refunded_amount_total, 0)) AS total_refunded,
                        -- refunding_amount_total: 退款中金额总计
                        SUM(IFNULL(refunding_amount_total, 0)) AS total_refunding,
                        -- unrefund_amount_total: 未退款金额总计
                        SUM(IFNULL(unrefund_amount_total, 0)) AS total_unrefund,
                        -- cancelrefund_amount_total: 取消退款金额总计
                        SUM(IFNULL(cancelrefund_amount_total, 0)) AS total_cancelrefund
                    FROM b_ap_refund_total
                    -- id: 采购订单主表ID参数
                    WHERE po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) t2 ON t1.po_order_id = t2.po_order_id
                SET
                    -- 更新预付款退款相关的所有总计字段
                    t1.advance_refundable_total = COALESCE(t2.total_refundable, 0),
                    t1.advance_refunded_total = COALESCE(t2.total_refunded, 0),
                    t1.advance_refunding_total = COALESCE(t2.total_refunding, 0),
                    t1.advance_unrefund_total = COALESCE(t2.total_unrefund, 0),
                    t1.advance_cancelrefund_total = COALESCE(t2.total_cancelrefund, 0)
                -- id: 采购订单主表ID参数
                WHERE t1.po_order_id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateRefundAmountTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 更新货权转移数据
     * 根据采购订单ID从明细总计表汇总货权转移相关的统计数据
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
            <script>
                -- 更新货权转移数据，根据采购订单ID从明细总计表汇总货权转移相关的统计数据
                UPDATE b_po_order_total t1
                LEFT JOIN (
                    -- 子查询：从采购订单明细总计表汇总货权转移相关数据
                    SELECT
                        po_order_id,
                        -- cargo_right_untransfer_qty_total: 货权未转移数量总计
                        SUM(IFNULL(cargo_right_untransfer_qty_total, 0)) AS total_untransfer_qty,
                        -- cargo_right_transfering_qty_total: 货权转移中数量总计
                        SUM(IFNULL(cargo_right_transfering_qty_total, 0)) AS total_transfering_qty,
                        -- cargo_right_transferred_qty_total: 货权已转移数量总计
                        SUM(IFNULL(cargo_right_transferred_qty_total, 0)) AS total_transferred_qty,
                        -- cargo_right_transfer_cancel_qty_total: 货权转移取消数量总计
                        SUM(IFNULL(cargo_right_transfer_cancel_qty_total, 0)) AS total_transfer_cancel_qty
                    FROM b_po_order_detail_total
                    -- id: 采购订单主表ID参数
                    WHERE po_order_id IN
                    <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                        #{id}
                    </foreach>
                    GROUP BY po_order_id
                ) t2 ON t1.po_order_id = t2.po_order_id
                SET
                    -- 更新货权转移相关的所有总计字段
                    t1.cargo_right_untransfer_qty_total = COALESCE(t2.total_untransfer_qty, 0),
                    t1.cargo_right_transfering_qty_total = COALESCE(t2.total_transfering_qty, 0),
                    t1.cargo_right_transferred_qty_total = COALESCE(t2.total_transferred_qty, 0),
                    t1.cargo_right_transfer_cancel_qty_total = COALESCE(t2.total_transfer_cancel_qty, 0)
                -- id: 采购订单主表ID参数
                WHERE t1.po_order_id IN
                <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
                    #{id}
                </foreach>
            </script>
            """)
    int updateCargoRightTransferTotalData(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

}
