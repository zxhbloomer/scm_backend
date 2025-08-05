package com.xinyirun.scm.core.system.mapper.business.po.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 采购订单明细表-商品 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Repository
public interface BPoOrderDetailMapper extends BaseMapper<BPoOrderDetailEntity> {

    /**
     * 获取订单总金额、订单总税额、订单总采购数量
     * @param po_order_id 采购订单ID
     * @return 订单明细合计VO
     */
    @Select("""
            -- 计算采购订单明细汇总数据
            SELECT 
            -- amount: 总额
            SUM(amount) AS amount, 
            -- tax_amount: 税额
            SUM(tax_amount) AS tax_amount, 
            -- qty: 数量
            SUM(qty) AS qty 
            FROM b_po_order_detail 
            -- po_order_id: 采购订单主表ID参数
            WHERE po_order_id = #{po_order_id}
            """)
    BPoOrderDetailVo getSumAmount(@Param("po_order_id") Integer po_order_id);

}
