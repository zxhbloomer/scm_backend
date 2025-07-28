package com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 销售货权转移明细表 Mapper 接口
 *
 * @author system
 * @since 2025-07-27
 */
@Repository
public interface BSoCargoRightTransferDetailMapper extends BaseMapper<BSoCargoRightTransferDetailEntity> {

    /**
     * 根据货权转移主表ID查询明细列表
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 明细列表
     */
    @Select("""
        SELECT 
            tab1.*,
            tab2.name as goods_name,
            tab3.name as unit_name,
            tab4.label as quality_status_name,
            CONCAT(tab1.goods_code, '-', tab1.sku_code, '-', tab1.sku_name) as virtual_sku_code_name
        FROM b_so_cargo_right_transfer_detail tab1
        LEFT JOIN m_goods tab2 ON tab2.id = tab1.goods_id
        LEFT JOIN m_unit tab3 ON tab3.id = tab1.unit_id
        LEFT JOIN sys_dict_data tab4 ON tab4.dict_type = 'goods_quality_status' 
            AND tab4.dict_value = tab1.quality_status
        WHERE tab1.cargo_right_transfer_id = #{cargoRightTransferId}
        ORDER BY tab1.id ASC
        """)
    List<BSoCargoRightTransferDetailVo> selectByCargoRightTransferId(@Param("cargoRightTransferId") Integer cargoRightTransferId);

    /**
     * 根据SKU ID统计转移数量
     *
     * @param skuId SKU ID
     * @return 转移数量
     */
    @Select("""
        SELECT COALESCE(SUM(transfer_qty), 0) 
        FROM b_so_cargo_right_transfer_detail tab1
        INNER JOIN b_so_cargo_right_transfer tab2 ON tab2.id = tab1.cargo_right_transfer_id
        WHERE tab1.sku_id = #{skuId} 
        AND tab2.status IN ('2', '6')
        """)
    BigDecimal sumTransferredQtyBySkuId(@Param("skuId") Integer skuId);

    /**
     * 根据货权转移主表ID删除明细数据
     *
     * @param cargoRightTransferId 货权转移主表ID
     * @return 删除行数
     */
    @Select("""
        DELETE FROM b_so_cargo_right_transfer_detail 
        WHERE cargo_right_transfer_id = #{cargoRightTransferId}
        """)
    Integer deleteByCargoRightTransferId(@Param("cargoRightTransferId") Integer cargoRightTransferId);

    /**
     * 根据商品ID查询货权转移明细
     *
     * @param goodsId 商品ID
     * @return 明细列表
     */
    @Select("""
        SELECT 
            tab1.*,
            tab2.code as cargo_right_transfer_code,
            tab2.status as cargo_right_transfer_status
        FROM b_so_cargo_right_transfer_detail tab1
        INNER JOIN b_so_cargo_right_transfer tab2 ON tab2.id = tab1.cargo_right_transfer_id
        WHERE tab1.goods_id = #{goodsId} 
        AND tab2.is_del = false
        ORDER BY tab1.id DESC
        """)
    List<BSoCargoRightTransferDetailVo> selectByGoodsId(@Param("goodsId") Integer goodsId);

    /**
     * 批量插入明细数据
     *
     * @param detailList 明细列表
     * @return 插入行数
     */
    @Select("""
        <script>
        INSERT INTO b_so_cargo_right_transfer_detail (
            cargo_right_transfer_id, so_order_detail_id, so_order_id, so_order_code,
            goods_id, goods_code, goods_name, sku_id, sku_code, sku_name,
            unit_id, origin, order_qty, order_price, order_amount,
            transfer_qty, transfer_price, transfer_amount,
            quality_status, batch_no, production_date, expiry_date, remark,
            c_time, u_time, c_id, u_id, dbversion
        ) VALUES
        <foreach collection="detailList" item="item" separator=",">
        (
            #{item.cargoRightTransferId}, #{item.soOrderDetailId}, #{item.soOrderId}, #{item.soOrderCode},
            #{item.goodsId}, #{item.goodsCode}, #{item.goodsName}, #{item.skuId}, #{item.skuCode}, #{item.skuName},
            #{item.unitId}, #{item.origin}, #{item.orderQty}, #{item.orderPrice}, #{item.orderAmount},
            #{item.transferQty}, #{item.transferPrice}, #{item.transferAmount},
            #{item.qualityStatus}, #{item.batchNo}, #{item.productionDate}, #{item.expiryDate}, #{item.remark},
            #{item.cTime}, #{item.uTime}, #{item.cId}, #{item.uId}, #{item.dbversion}
        )
        </foreach>
        </script>
        """)
    Integer batchInsert(@Param("detailList") List<BSoCargoRightTransferDetailEntity> detailList);
}