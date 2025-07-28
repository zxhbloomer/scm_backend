package com.xinyirun.scm.core.system.mapper.business.pp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.pp.BPpProductEntity;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpProductVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 生产计划_产成品、副产品 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
@Repository
public interface BPpProductMapper extends BaseMapper<BPpProductEntity> {


    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.id,                                                                                     "
            +  "    t.code,                                                                                     "
            +  "    t.type,                                                                                     "
            +  "    t.pp_id,                                                                                               "
            +  "    t2.id sku_id,                                                                                               "
            +  "    t2.code sku_code,                                                                                               "
            +  "  	t3.name goods_prop,                                                                                 "
            +  "  	t2.name goods_name,                                                                                 "
            +  "  	t2.pm,                                                                                              "
            +  "  	t2.spec,                                                                                            "
            +  "    t.pp_router,                                                                                               "
            +  "    t.qty,                                                                                               "
            +  "  	t.warehouse_id,                                                                                   "
            +  "  	t1.label type_name,                                                                                 "
            +  "  	t.warehouse_code,                                                                                   "
            +  "  	t.location_id,                                                                                    "
            +  "  	t.location_code,                                                                                    "
            +  "  	t.bin_id,                                                                                       "
            +  "  	t.bin_code,                                                                                       "
            +  "  	t.unit_id,                                                                                       "
            +  "  	t.unit_name,                                                                             "
            +  "  	t.c_id,                                                                                     "
            +  "  	t.u_id,                                                                                          "
            +  "  	t.c_time,                                                                                         "
            +  "  	t.u_time,                                                                                          "
            +  "    t2.goods_id,                                                                                        "
            +  "    t2.goods_code,                                                                                       "
            +  "  	t4.name warehouse_name                                                                             "
            +  "  FROM b_pp_product t                                                                                   "
            +  "  LEFT JOIN s_dict_data t1 ON t.type = t1.dict_value and t1.code = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE +"'"
            +  "  LEFT JOIN m_goods_spec t2 ON t2.id = t.sku_id                                                         "
            +  "  LEFT JOIN m_goods_spec_prop t3 ON t2.prop_id = t3.id                                                  "
            +  "  LEFT JOIN m_warehouse t4 ON t.warehouse_id = t4.id                                                    "
            +  "  WHERE t.pp_id = #{p1}                                                                                 "
    )
    List<BPpProductVo> selectByWoId(@Param("p1") Integer id);

    @Delete(" DELETE FROM b_pp_product WHERE pp_id = #{id,jdbcType=VARCHAR} ")
    void deleteByPpId(Integer id);
}
