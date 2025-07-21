package com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferTotalVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 货权转移汇总表 Mapper 接口
 *
 * @author system
 * @since 2025-01-19
 */
@Repository
public interface BCargoRightTransferTotalMapper extends BaseMapper<BCargoRightTransferTotalEntity> {

    /**
     * 根据货权转移主表ID查询汇总数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 汇总数据
     */
    @Select("""
        SELECT 
            tab1.*,
            tab2.goods_type_count,
            tab2.sku_type_count,
            CASE 
                WHEN tab1.qty_total > 0 THEN tab1.amount_total / tab1.qty_total 
                ELSE 0 
            END as avg_price
        FROM b_cargo_right_transfer_total tab1
        LEFT JOIN (
            SELECT 
                cargo_right_transfer_id,
                COUNT(DISTINCT goods_id) as goods_type_count,
                COUNT(DISTINCT sku_id) as sku_type_count
            FROM b_cargo_right_transfer_detail 
            WHERE cargo_right_transfer_id = #{cargoRightTransferId}
        ) tab2 ON tab2.cargo_right_transfer_id = tab1.cargo_right_transfer_id
        WHERE tab1.cargo_right_transfer_id = #{cargoRightTransferId}
        """)
    BCargoRightTransferTotalVo selectByCargoRightTransferId(@Param("cargoRightTransferId") Integer cargoRightTransferId);

    /**
     * 根据明细数据重新计算汇总信息
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 更新行数
     */
    @Select("""
        INSERT INTO b_cargo_right_transfer_total (
            cargo_right_transfer_id, qty_total, weight_total, volume_total, amount_total
        ) 
        SELECT 
            #{cargoRightTransferId},
            COALESCE(SUM(transfer_qty), 0) as qty_total,
            0 as weight_total,
            0 as volume_total,
            COALESCE(SUM(transfer_amount), 0) as amount_total
        FROM b_cargo_right_transfer_detail 
        WHERE cargo_right_transfer_id = #{cargoRightTransferId}
        ON DUPLICATE KEY UPDATE
            qty_total = VALUES(qty_total),
            amount_total = VALUES(amount_total)
        """)
    Integer refreshTotal(@Param("cargoRightTransferId") Integer cargoRightTransferId);

    /**
     * 根据货权转移主表ID删除汇总数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 删除行数
     */
    @Select("""
        DELETE FROM b_cargo_right_transfer_total 
        WHERE cargo_right_transfer_id = #{cargoRightTransferId}
        """)
    Integer deleteByCargoRightTransferId(@Param("cargoRightTransferId") Integer cargoRightTransferId);
}