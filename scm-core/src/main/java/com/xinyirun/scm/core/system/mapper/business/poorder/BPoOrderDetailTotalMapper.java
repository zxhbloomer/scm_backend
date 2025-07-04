package com.xinyirun.scm.core.system.mapper.business.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderDetailTotalEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.LinkedHashSet;

/**
 * 采购结算明细-数据汇总 Mapper 接口
 */
@Mapper
public interface BPoOrderDetailTotalMapper extends BaseMapper<BPoOrderDetailTotalEntity> {

    /**
     * 更新入库汇总数据
     * 根据入库计划明细数据更新采购订单明细汇总表的入库相关字段
     * 
     * @param poOrderId 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
        <script>
        UPDATE b_po_order_detail_total t1
        JOIN (
            SELECT
                t2.order_detail_id,
                SUM(IFNULL(t2.processing_qty, 0)) AS sum_processing_qty,
                SUM(IFNULL(t2.processing_weight, 0)) AS sum_processing_weight,
                SUM(IFNULL(t2.processing_volume, 0)) AS sum_processing_volume,
                SUM(IFNULL(t2.unprocessed_qty, 0)) AS sum_unprocessed_qty,
                SUM(IFNULL(t2.unprocessed_weight, 0)) AS sum_unprocessed_weight,
                SUM(IFNULL(t2.unprocessed_volume, 0)) AS sum_unprocessed_volume,
                SUM(IFNULL(t2.processed_qty, 0)) AS sum_processed_qty,
                SUM(IFNULL(t2.processed_weight, 0)) AS sum_processed_weight,
                SUM(IFNULL(t2.processed_volume, 0)) AS sum_processed_volume,
                SUM(IFNULL(t2.cancel_qty, 0)) AS sum_cancel_qty,
                SUM(IFNULL(t2.cancel_weight, 0)) AS sum_cancel_weight,
                SUM(IFNULL(t2.cancel_volume, 0)) AS sum_cancel_volume,
                SUM(IFNULL(t2.qty, 0)) AS sum_plan_qty
            FROM b_in_plan_detail t2
            WHERE t2.order_detail_id IN (
                SELECT pod.id 
                FROM b_po_order_detail pod 
                WHERE pod.po_order_id IN 
                <foreach collection="po_order_id" item="id" open="(" close=")" separator=",">
                    #{id}
                </foreach>
            )
            GROUP BY t2.order_detail_id
        ) t3 ON t1.id = t3.order_detail_id
        SET
            t1.inbound_processing_qty_total = t3.sum_processing_qty,
            t1.inbound_processing_weight_total = t3.sum_processing_weight,
            t1.inbound_processing_volume_total = t3.sum_processing_volume,
            t1.inbound_unprocessed_qty_total = t3.sum_unprocessed_qty,
            t1.inbound_unprocessed_weight_total = t3.sum_unprocessed_weight,
            t1.inbound_unprocessed_volume_total = t3.sum_unprocessed_volume,
            t1.inbound_processed_qty_total = t3.sum_processed_qty,
            t1.inbound_processed_weight_total = t3.sum_processed_weight,
            t1.inbound_processed_volume_total = t3.sum_processed_volume,
            t1.inbound_cancel_qty_total = t3.sum_cancel_qty,
            t1.inbound_cancel_weight_total = t3.sum_cancel_weight,
            t1.inbound_cancel_volume_total = t3.sum_cancel_volume,
            t1.inventory_in_total = (t3.sum_processed_qty - t3.sum_cancel_qty),
            t1.inventory_in_plan_total = t3.sum_plan_qty
        </script>
        """)
    int updateInboundTotalByPoOrderIds(@Param("po_order_id") LinkedHashSet<Integer> poOrderId);


    /**
     * 更新待结算数量汇总
     * 计算公式：待结算数量 = 实际入库汇总 - 应结算数量汇总
     * 
     * @param po_order_id 采购订单ID集合
     * @return 更新记录数
     */
    @Update("""
        <script>
        UPDATE b_po_order_detail_total
        SET settle_can_qty_total = IFNULL(inventory_in_total, 0) - IFNULL(settle_planned_qty_total, 0)
        WHERE po_order_id IN
        <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
        """)
    int updateSettleCanQtyTotal(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);

    /**
     * 确保明细汇总记录存在
     * 如果不存在则插入，避免更新时找不到记录
     * 
     * @param po_order_id 采购订单ID集合
     * @return 插入记录数
     */
    @Insert("""
        <script>
        INSERT IGNORE INTO b_po_order_detail_total (id, po_order_id)
        SELECT pod.id, pod.po_order_id
        FROM b_po_order_detail pod
        WHERE pod.po_order_id IN
        <foreach collection='po_order_id' item='id' open='(' separator=',' close=')'>
            #{id}
        </foreach>
        </script>
        """)
    int insertMissingRecords(@Param("po_order_id") LinkedHashSet<Integer> po_order_id);
} 