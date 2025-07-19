package com.xinyirun.scm.core.system.mapper.business.wms.in.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.in.order.BInOrderGoodsEntity;
import com.xinyirun.scm.bean.system.vo.wms.in.order.BInOrderGoodsVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Repository
public interface BInOrderGoodsMapper extends BaseMapper<BInOrderGoodsEntity> {

    /**
     * 查询列表
     */
    @Select("    "
            + "    select                                                                                                                   "
            + "    (t1.rate * 100) rate,                                                                                                     "
            + "    t1.*,                                                                                                                    "
            + "    t2.name goods_name,                                                                                                      "
            + "    t2.pm,                                                                                                                   "
            + "    t2.spec,                                                                                                                 "
            + "    t5.in_actual_count,                                                                                                      "
            + "    t3.label delivery_type_name                                                                                              "
            + "    from b_in_order_goods t1                                                                                                 "
            + "  left join m_goods_spec t2 on t1.sku_id = t2.id                                                                             "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                 "
            + "       where tab2.code = '" + DictConstant.DICT_B_ORDER_DELIVERY_TYPE + "')t3 on t3.dict_value = t1.delivery_type            "
            + "  left join (                                                                                                                "
            + "    select                                                                                                                   "
            + "      sum(t3.actual_count) in_actual_count,                                                                                  "
            + "      t3.sku_id,                                                                                                             "
            + "      ifnull(tt1.order_id, t4.order_id) order_id                                                                             "
            + "    from b_in t3                                                                                                             "
            + "    left join b_in_extra t4 ON t3.id = t4.in_id and t4.order_type = 'b_in_order'                                             "
            + "   LEFT JOIN b_in_plan_detail tt1 ON t3.plan_detail_id= tt1.id AND tt1.order_type = 'b_in_order'                             "
            + "    where t3.status = '" + DictConstant.DICT_B_IN_STATUS_TWO + "'                                                         "
            + "        AND t3.type = '"+ DictConstant.DICT_B_IN_TYPE_CG +"'                                                                 "
            + "    group by t3.sku_id, ifnull(tt1.order_id, t4.order_id)                                                                    "
            + "     ) t5 ON t5.sku_id = t1.sku_id and t1.order_id = t5.order_id                                                             "
            + "         where true                                                                                                          "
            + "         and t1.order_id =  #{p1.order_id,jdbcType=VARCHAR}                                                                  "
            + "      ")
    List<BInOrderGoodsVo> selectList(@Param("p1") BInOrderGoodsVo vo);

    /**
     * 查询列表
     */
    @Select("    "
            + "  delete from b_in_order_goods t1                                               "
            + "         where true                                                             "
            + "         and t1.order_id =  #{p1,jdbcType=INTEGER}                              "
            + "      ")
    void deleteByOrderId(@Param("p1") Integer order_id);

}
