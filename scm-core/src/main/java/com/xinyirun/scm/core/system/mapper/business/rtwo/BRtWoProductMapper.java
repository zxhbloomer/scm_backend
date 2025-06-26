package com.xinyirun.scm.core.system.mapper.business.rtwo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoProductEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoProductVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterProductVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  生产管理_产成品、副产品 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Repository
public interface BRtWoProductMapper extends BaseMapper<BRtWoProductEntity> {

    /**
     * 生产配方分页查询, 不考虑合并单元格
     */
    @Select(""
            + "	SELECT                                                                                                  "
            + "		t2.pm,                                                                                              "
            + "		t2.spec,                                                                                            "
            + "		t7.label type_name,                                                                                 "
            + "		t1.*,                                                                                               "
            + "		t3.name goods_name,                                                                                 "
            + "		t4.name goods_prop,                                                                                 "
            + "		t5.name c_name,                                                                                     "
            + "		t5.name u_name,                                                                                     "
            + "		t1.c_time,                                                                                          "
            + "		t1.u_time                                                                                           "
            + "	FROM                                                                                                    "
            + "		b_rt_wo_router_product t1                                                                              "
            + "		LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                                                      "
            + "		LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                         "
            + "		LEFT JOIN m_goods_spec_prop t4 ON t4.id = t2.prop_id                                                "
            + "		LEFT JOIN m_staff t5 ON t5.id = t1.c_id                                                             "
            + "		LEFT JOIN m_staff t6 ON t6.id = t1.u_id                                                             "
            + "     LEFT JOIN v_dict_info t7 ON t7.CODE = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE +"'               "
            + "         AND t1.type = t7.dict_value                                                                     "
            + "     WHERE TRUE                                                                                          "
            + "     AND t1.router_id =  #{p1.router_id,jdbcType=INTEGER}                                                "
    )
    List<BRtWoRouterProductVo> selectList(@Param("p1") BRtWoRouterProductVo param);

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.warehouse_id,                                                                                     "
            +  "    t.id,                                                                                               "
            +  "  	t.warehouse_code,                                                                                   "
            +  "  	t.location_code,                                                                                    "
            +  "  	t.location_id,                                                                                      "
            +  "  	t.bin_id,                                                                                           "
            +  "  	t.bin_code,                                                                                         "
            +  "  	t1.label type_name,                                                                                 "
            +  "  	t.type,                                                                                             "
            +  "  	t3.name goods_prop,                                                                                 "
            +  "  	t2.code sku_code,                                                                                   "
            +  "  	t2.name goods_name,                                                                                 "
            +  "  	t2.pm,                                                                                              "
            +  "  	t2.spec,                                                                                            "
            +  "  	t.wo_router,                                                                                        "
            +  "  	t.wo_qty,                                                                                           "
            +  "  	t.unit_name,                                                                                        "
            +  "  	t2.id sku_id,                                                                                       "
            +  "  	t4.name warehouse_name,                                                                              "
            +  "  	t.b_in_plan_id,                                                                                     "
            +  "  	t.unit_id                                                                                           "
            +  "  FROM b_rt_wo_product t                                                                                   "
            +  "  LEFT JOIN s_dict_data t1 ON t.type = t1.dict_value and t1.code = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE +"'                  "
            +  "  LEFT JOIN m_goods_spec t2 ON t2.id = t.sku_id                                                         "
            +  "  LEFT JOIN m_goods_spec_prop t3 ON t2.prop_id = t3.id                                                  "
            +  "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                    "
            +  "  WHERE t.wo_id = #{p1}                                                                                 "
    )
    List<BRtWoProductVo> selectByWoId(@Param("p1") Integer wo_id);

    @Select(""
            + " SELECT sum(tab.wo_qty) from ("
            + " SELECT                                                                                                  "
            + "   ifnull(sum(t.wo_qty), 0) wo_qty                                                                       "
            + " FROM b_wo_product t                                                                                     "
            + " LEFT JOIN b_wo t1 ON t.wo_id = t1.id                                                                    "
            + " WHERE t.type = '" + DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'                                       "
            + " and t1.status between '" + DictConstant.DICT_B_WO_STATUS_2 + "' AND '" + DictConstant.DICT_B_WO_STATUS_3 + "'                                               "
//            + " and t1.status = '" + DictConstant.DICT_B_WO_STATUS_3 + "'                                               "
            + " and t1.delivery_order_detail_id = #{p2}                                                                 "
            + " and (t.wo_id != #{p1} or #{p1} is null)                                                                 "
            + " union all                                                                                               "
            + " SELECT                                                                                                  "
            + "   ifnull(sum(t.wo_qty), 0) wo_qty                                                                       "
            + " FROM b_rt_wo_product t                                                                                  "
            + " LEFT JOIN b_rt_wo t1 ON t.wo_id = t1.id                                                                 "
            + " WHERE t.type = '" + DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'                                       "
            + " and t1.status between '" + DictConstant.DICT_B_WO_STATUS_2 + "' AND '" + DictConstant.DICT_B_WO_STATUS_3 + "'"
//            + " and t1.status = '" + DictConstant.DICT_B_WO_STATUS_3 + "'                                               "
            + " and t1.delivery_order_detail_id = #{p2}                                                                 "
            + " and (t.wo_id != #{p1} or #{p1} is null)                                                                 "
            + " ) tab"
    )
    BigDecimal selectHasProductNum(@Param("p1") Integer wo_id,@Param("p2") Integer delivery_order_detail_id);
}
