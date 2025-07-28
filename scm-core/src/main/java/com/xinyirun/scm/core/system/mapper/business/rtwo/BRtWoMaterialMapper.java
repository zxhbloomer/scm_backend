package com.xinyirun.scm.core.system.mapper.business.rtwo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.rtwo.BRtWoMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterMaterialVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  生产管理_原材料Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Repository
public interface BRtWoMaterialMapper extends BaseMapper<BRtWoMaterialEntity> {

    /**
     * 生产配方分页查询, 不考虑合并单元格
     */
    @Select(""
            + "	SELECT                                                                                                  "
            + "		t2.pm,                                                                                              "
            + "		t2.spec,                                                                                            "
            + "		t1.*,                                                                                               "
            + "		t3.name goods_name,                                                                                 "
            + "		t4.name goods_prop,                                                                                 "
            + "		t5.name c_name,                                                                                     "
            + "		t5.name u_name,                                                                                     "
            + "		t1.c_time,                                                                                          "
            + "		t1.u_time                                                                                           "
            + "	FROM                                                                                                    "
            + "		b_rt_wo_router_material t1                                                                             "
            + "		LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                                                      "
            + "		LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                         "
            + "		LEFT JOIN m_goods_spec_prop t4 ON t4.id = t2.prop_id                                                "
            + "		LEFT JOIN m_staff t5 ON t5.id = t1.c_id                                                             "
            + "		LEFT JOIN m_staff t6 ON t6.id = t1.u_id                                                             "
            + "     WHERE TRUE                                                                                          "
            + "     AND t1.router_id =  #{p1.router_id,jdbcType=INTEGER}                                                "
    )
    List<BRtWoRouterMaterialVo> selectList(@Param("p1") BRtWoRouterMaterialVo param);

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.warehouse_id,                                                                                     "
            +  "    t.id,                                                                                               "
            +  "  	t.warehouse_code,                                                                                   "
            +  "  	t.location_id,                                                                                      "
            +  "  	t.location_code,                                                                                    "
            +  "  	t.bin_id,                                                                                           "
            +  "  	t.bin_code,                                                                                         "
            +  "  	t3.name goods_prop,                                                                                 "
            +  "  	t2.code sku_code,                                                                                   "
            +  "  	t2.name goods_name,                                                                                 "
            +  "  	t2.pm,                                                                                              "
            +  "  	t2.spec,                                                                                            "
            +  "  	t.wo_router,                                                                                        "
            +  "  	t.wo_qty,                                                                                           "
            +  "  	t.unit_name,                                                                                        "
            +  "  	t4.name warehouse_name,                                                                              "
            +  "  	t2.id sku_id,                                                                                       "
            +  "  	t.unit_id,                                                                                          "
            +  "  	t.b_out_plan_id                                                                                     "
            +  "  FROM b_rt_wo_material t                                                                                  "
            +  "  LEFT JOIN m_goods_spec t2 ON t2.id = t.sku_id                                                         "
            +  "  LEFT JOIN m_goods_spec_prop t3 ON t2.prop_id = t3.id                                                  "
            +  "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                    "
            +  "  WHERE t.wo_id = #{p1}"
    )
    List<BRtWoMaterialVo> selectByWoId(@Param("p1") Integer wo_id);

}
