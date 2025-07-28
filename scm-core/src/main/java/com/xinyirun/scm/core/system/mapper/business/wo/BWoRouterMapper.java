package com.xinyirun.scm.core.system.mapper.business.wo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wo.BWoRouterEntity;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 生产配方服务类 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Repository
public interface BWoRouterMapper extends BaseMapper<BWoRouterEntity> {

    String comm_select = ""
            +  "  SELECT                                                                                                "
            +  "    t1.*,                                                                                               "
            +  "  	t2.`name` c_name,                                                                                   "
            +  "  	t3.`name` u_name                                                                                    "
            +  "  FROM                                                                                                  "
            +  "    b_wo_router t1                                                                                      "
            +  "  LEFT JOIN m_staff t2 ON t2.id = t1.c_id                                                               "
            +  "  LEFT JOIN m_staff t3 ON t3.id = t1.u_id                                                               "
            +  "  WHERE TRUE                                                                                            "
            ;
    /**
     * 根据Id查询详情
     *
     * @param id 主键id
     * @return
     */
    @Select(""
            + "   SELECT                                                                                                "
            + "     t.id,                                                                                               "
            + "     t.`code`,                                                                                           "
            + "     t.is_enable,                                                                                           "
            + "     t.dbversion,                                                                                           "
//            + "     t.json_material_list,                                                                                           "
//            + "     t.json_product_list,                                                                                           "
            + "     t.`name`                                                                                           "
            + "   FROM b_wo_router t                                                                                    "
            + "   WHERE t.id = #{p1}                                                                                    "
    )
//    @Results({
//            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
//            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
//    })
    BWoRouterVo getById(@Param("p1") Integer id);

    /**
     * 生产配方分页查询, 不考虑合并单元格
     * @param param 入参
     * @return IPage<BWoRouterVo>
     */
    @Select(""
            +  comm_select
            +  "  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')              "
            +  "  and (t1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')              "
            +  "  and (t1.is_enable = #{p1.is_enable} or #{p1.is_enable} is null)                                       "
            + "   and (date_format(t1.c_time, '%Y-%m-%d') >= date_format(#{p1.start_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.start_time,jdbcType=DATE} is null)"
            + "   and (date_format(t1.c_time, '%Y-%m-%d') <= date_format(#{p1.over_time,jdbcType=DATE}, '%Y-%m-%d') or #{p1.over_time,jdbcType=DATE} is null)  "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_router_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id         "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.product_goods_name}, '%') or #{p1.product_goods_name} is null or #{p1.product_goods_name} = '')"
            + "     and sub2.router_id = t1.id)                                                                         "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_router_material sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id         "
            + "     where true                                                                                          "
            + "     and (concat(ifnull(sub1.name, ''), '_', ifnull(sub1.code, '')) like concat('%', #{p1.material_goods_name}, '%') or #{p1.material_goods_name} is null or #{p1.material_goods_name} = '')"
            + "     and sub2.router_id = t1.id)                                                                         "
            + "   and EXISTS(                                                                                           "
            + "     select 1 from b_wo_router_product sub2 left join m_goods_spec sub1 on sub2.sku_id = sub1.id         "
            + "     where true                                                                                          "
            + "     and (sub1.code = #{p1.product_sku_code} or #{p1.product_sku_code} is null or #{p1.product_sku_code} = '')"
            + "     and sub2.router_id = t1.id)                                                                         "

    )
    @Results({
            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BWoRouterVo> selectPageList(@Param("p1") BWoRouterVo param, Page<BWoRouterVo> pageCondition);

    /**
     * 生产配方, 根据id查询 列表内容
     * @param id 入参
     * @return BWoRouterVo
     */
    @Select(""
            +  comm_select
            +  " and t1.id = #{id}                                                                                      "
    )
    @Results({
            @Result(property = "material_list", column = "json_material_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "product_list", column = "json_product_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BWoRouterVo selectById(Integer id);
}
