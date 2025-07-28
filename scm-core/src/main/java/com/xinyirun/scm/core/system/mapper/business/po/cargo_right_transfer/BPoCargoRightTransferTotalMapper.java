package com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.cargo_right_transfer.BPoCargoRightTransferTotalEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 货权转移汇总表 Mapper 接口
 *
 * @author system
 * @since 2025-01-19
 */
@Repository
public interface BPoCargoRightTransferTotalMapper extends BaseMapper<BPoCargoRightTransferTotalEntity> {

    /**
     * 根据合同ID查询货权转移ID集合
     * @param contractId 合同ID
     * @return 货权转移ID集合
     */
    @Select("""
            SELECT DISTINCT t.id
            FROM b_po_cargo_right_transfer t
            WHERE t.po_contract_id = #{contractId}
              AND t.is_del = false
            """)
    List<Integer> selectCargoRightTransferIdsByContractId(@Param("contractId") Integer contractId);

    /**
     * 插入缺失的货权转移总计记录
     * @param cargoRightTransferIds 货权转移ID集合
     * @return 插入的记录数
     */
    @Insert("""
            <script>
            INSERT INTO b_po_cargo_right_transfer_total (cargo_right_transfer_id, po_order_id, cargo_right_untransfer_qty_total, cargo_right_transfering_qty_total, cargo_right_transferred_qty_total, cargo_right_transfer_cancel_qty_total)
            SELECT t.id, t.po_order_id, 0, 0, 0, 0
            FROM b_po_cargo_right_transfer t
            WHERE t.id IN
                <foreach collection="cargoRightTransferIds" item="id" open="(" separator="," close=")">
                    #{id}
                </foreach>
              AND NOT EXISTS (
                  SELECT 1 FROM b_po_cargo_right_transfer_total total
                  WHERE total.cargo_right_transfer_id = t.id
              )
            </script>
            """)
    Integer insertMissingCargoRightTransferTotal(@Param("cargoRightTransferIds") LinkedHashSet<Integer> cargoRightTransferIds);

    /**
     * 批量更新货权转移总计数据，从明细表汇总计算
     * 根据货权转移主表的状态(0-待审批,1-审批中,2-执行中,3-驳回,4-作废审批中,5-已作废,6-已完成)
     * 对明细表的转移数量进行分状态汇总统计
     * 
     * 汇总规则：
     * - 货权未转移数量汇总(cargo_right_untransfer_qty_total) = 状态为 0,3,4 时的转移数量汇总
     * - 货权转移中数量汇总(cargo_right_transfering_qty_total) = 状态为 1 时的转移数量汇总  
     * - 货权已转移数量汇总(cargo_right_transferred_qty_total) = 状态为 2,6 时的转移数量汇总
     * - 货权转移取消数量汇总(cargo_right_transfer_cancel_qty_total) = 状态为 5 时的转移数量汇总
     * 
     * @param cargoRightTransferIds 货权转移ID集合
     * @return 更新的记录数
     */
    @Update("""
            <script>
            UPDATE b_po_cargo_right_transfer_total total
            JOIN (
                SELECT 
                    main.id as cargo_right_transfer_id,
                    -- 货权未转移数量汇总：状态为 0-待审批,3-驳回,4-作废审批中 时的转移数量汇总
                    SUM(CASE WHEN main.status IN ('0', '3', '4') THEN IFNULL(detail.transfer_qty, 0) ELSE 0 END) AS untransfer_qty_total,
                    -- 货权转移中数量汇总：状态为 1-审批中 时的转移数量汇总
                    SUM(CASE WHEN main.status IN ('1') THEN IFNULL(detail.transfer_qty, 0) ELSE 0 END) AS transfering_qty_total,
                    -- 货权已转移数量汇总：状态为 2-执行中,6-已完成 时的转移数量汇总
                    SUM(CASE WHEN main.status IN ('2', '6') THEN IFNULL(detail.transfer_qty, 0) ELSE 0 END) AS transferred_qty_total,
                    -- 货权转移取消数量汇总：状态为 5-已作废 时的转移数量汇总
                    SUM(CASE WHEN main.status IN ('5') THEN IFNULL(detail.transfer_qty, 0) ELSE 0 END) AS transfer_cancel_qty_total
                FROM b_po_cargo_right_transfer main
                LEFT JOIN b_po_cargo_right_transfer_detail detail ON main.id = detail.cargo_right_transfer_id
                WHERE main.id IN
                <foreach collection="cargoRightTransferIds" item="id" open="(" separator="," close=")">
                    #{id}
                </foreach>
                GROUP BY main.id
            ) summary ON total.cargo_right_transfer_id = summary.cargo_right_transfer_id
            SET 
                total.cargo_right_untransfer_qty_total = summary.untransfer_qty_total,
                total.cargo_right_transfering_qty_total = summary.transfering_qty_total,
                total.cargo_right_transferred_qty_total = summary.transferred_qty_total,
                total.cargo_right_transfer_cancel_qty_total = summary.transfer_cancel_qty_total
            WHERE total.cargo_right_transfer_id IN
                <foreach collection="cargoRightTransferIds" item="id" open="(" separator="," close=")">
                    #{id}
                </foreach>
            </script>
            """)
    int batchUpdateCargoRightTransferTotalFromDetail(@Param("cargoRightTransferIds") LinkedHashSet<Integer> cargoRightTransferIds);

}