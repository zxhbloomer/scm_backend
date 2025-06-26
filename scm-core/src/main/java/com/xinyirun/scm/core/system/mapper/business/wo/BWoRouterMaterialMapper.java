package com.xinyirun.scm.core.system.mapper.business.wo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterMaterialEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterMaterialVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  生产配方_原材料 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Repository
public interface BWoRouterMaterialMapper extends BaseMapper<BWoRouterMaterialEntity> {

    /**
     * 根据 router_id 查询
     *
     * @param id router_id
     * @return List<BWoRouterProductVo>
     */
    @Select(""
            + "SELECT                                                                                                  "
            + "   t.id,                                                                                                "
            + "   t.`code`,                                                                                            "
            + "   t.router_id,                                                                                         "
            + "   t.sku_id,                                                                                            "
            + "   t.router,                                                                                            "
            + "   t2.`name` goods_name,                                                                                "
            + "   t1.`code` sku_code,                                                                                  "
            + "   t1.pm,                                                                                               "
            + "   t4.name goods_prop,                                                                                  "
            + "   t2.id goods_id,                                                                                      "
            + "   t2.code goods_code,                                                                                  "
            + "   t1.spec                                                                                              "
            + " FROM b_wo_router_material t                                                                            "
            + " LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + " LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + " LEFT JOIN m_goods_spec_prop t4 ON t1.prop_id = t4.id                                                   "
            + " WHERE t.router_id = #{p1}                                                                              "
    )
    List<BWoRouterMaterialVo> selectByRouterId(Integer id);
}
