package com.xinyirun.scm.core.system.mapper.business.wms.out.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutOrderGoodsEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutOrderGoodsVo;
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
public interface BOutOrderGoodsMapper extends BaseMapper<BOutOrderGoodsEntity> {

    /**
     * 查询列表
     */
    @Select("    "
            + "  select                                                                                                                     "
            + "         (t1.rate * 100) rate,                                                                                               "
            + "         t1.*,                                                                                                               "
            + "         t2.name sku_name,                                                                                                   "
            + "         t2.pm,                                                                                                              "
            + "         t2.spec,                                                                                                            "
            + "         t5.out_actual_count,                                                                                                "
            + "    t3.label delivery_type_name                                                                                              "
            + "     from b_out_order_goods t1                                                                                               "
            + "  left join m_goods_spec t2 on t1.sku_id = t2.id                                                                             "
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                 "
            + "       where tab2.code = '" + DictConstant.DICT_B_ORDER_DELIVERY_TYPE + "')t3 on t3.dict_value = t1.delivery_type            "
            + "  left join (                                                                                                                "
            + "    select                                                                                                                   "
            + "      sum(t3.actual_count) out_actual_count,                                                                                 "
            + "      t3.sku_id,                                                                                                             "
            + "      t4.order_id                                                                                                            "
            + "    from b_out t3                                                                                                            "
            + "    left join b_out_extra t4 ON t3.id = t4.out_id and t4.order_type = 'b_out_order'                                           "
            + "    where t3.status = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                                                        "
            + "      AND t3.type = '" + DictConstant.DICT_B_OUT_TYPE_XS + "'                                                                                "
            + "    group by t3.sku_id, t4.order_id                                                                                          "
            + "     ) t5 ON t5.sku_id = t1.sku_id and t1.order_id = t5.order_id                                                             "
            + "         where true                                                                                                          "
            + "         and t1.order_id =  #{p1.order_id,jdbcType=INTEGER}                                                                  "
            + "      ")
    List<BOutOrderGoodsVo> selectList(@Param("p1") BOutOrderGoodsVo vo);

    /**
     * 查询列表
     */
    @Select("    "
            + "  delete from b_out_order_goods t1                                              "
            + "         where true                                                             "
            + "         and t1.order_id =  #{p1,jdbcType=INTEGER}                              "
            + "      ")
    void deleteByOrderId(@Param("p1") Integer order_id);


}
