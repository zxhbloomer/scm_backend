package com.xinyirun.scm.core.system.mapper.business.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderDetailVo;
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
    @Select("SELECT SUM(amount) AS amount, SUM(tax_amount) AS tax_amount, SUM(qty) AS qty FROM b_po_order_detail WHERE po_order_id = #{po_order_id}")
    PoOrderDetailVo getSumAmount(@Param("po_order_id") Integer po_order_id);

}
