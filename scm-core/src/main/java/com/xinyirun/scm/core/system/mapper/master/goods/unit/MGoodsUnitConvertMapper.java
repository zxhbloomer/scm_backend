//package com.xinyirun.scm.core.system.mapper.master.goods.unit;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitConvertEntity;
//import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * <p>
// *  Mapper 接口
// * </p>
// *
// * @author htt
// * @since 2021-09-23
// */
//@Repository
//public interface MGoodsUnitConvertMapper extends BaseMapper<MGoodsUnitConvertEntity> {
//
//    String common_select = "  "
//            + "     SELECT                                                             "
//            + "            t.*,                                                        "
//            + "            concat(t.jl_unit,'>',t.hs_unit,'【',t.hs_gx,'】') des,                                                            "
//            + "            t1.name as c_name,                                          "
//            + "            t2.name as u_name                                           "
//            + "       FROM                                                             "
//            + "  	       m_goods_unit_convert t                                                  "
//            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
//            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
//            + "                                                                        "
//            ;
//
//    /**
//     * 页面查询列表
//     * @param page
//     * @param searchCondition
//     * @return
//     */
//    @Select("    "
//            + common_select
//            + "  where true "
//            + "  and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null) "
//            + "  order by t.idx ")
//    IPage<MGoodsUnitConvertVo> selectPage(Page page, @Param("p1") MGoodsUnitConvertVo searchCondition);
//
//    /**
//     * 页面查询列表
//     * @param searchCondition
//     * @return
//     */
//    @Select("    "
//            + common_select
//            + "  where true "
//            + "  and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null) "
//            + "  and (t.jl_unit_id = #{p1.jl_unit_id,jdbcType=INTEGER} or #{p1.jl_unit_id,jdbcType=INTEGER} is null) "
//            + "  order by t.idx ")
//    List<MGoodsUnitConvertVo> selectList(@Param("p1") MGoodsUnitConvertVo searchCondition);
//
//    /**
//     * 按条件获取所有数据，没有分页
//     * @param name
//     * @return
//     */
//    @Select("    "
//            + common_select
//            + "  where true "
//            + "    and t.name =  #{p1}"
//            + "      ")
//    List<MGoodsUnitConvertEntity> selectByName(@Param("p1") String name);
//
//    /**
//     * 按条件获取所有数据，没有分页
//     * @param businessTypeId
//     * @return
//     */
//    @Select("    "
//            + common_select
//            + "  where true "
//            + "    and t3.id =  #{p1,jdbcType=INTEGER}"
//            + "      ")
//    List<MGoodsUnitConvertEntity> selectByBusiness(@Param("p1") int buisnessTypeId);
//
//    /**
//     * 没有分页，按id筛选条件
//     * @param searchCondition
//     * @return
//     */
//    @Select("   <script>   "
//            + common_select
//            + "  where t.id in "
//            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
//            + "         #{item.id,jdbcType=INTEGER}  "
//            + "        </foreach>    "
//            + "  </script>    ")
//    List<MGoodsUnitConvertEntity> selectIdsIn(@Param("p1") List<MGoodsUnitConvertVo> searchCondition);
//
//    /**
//     * 按条件获取所有数据，没有分页
//     * @param id
//     * @return
//     */
//    @Select("    "
//            + common_select
//            + "  where t.id =  #{p1,jdbcType=INTEGER}"
//            + "      ")
//    MGoodsUnitConvertVo selectId(@Param("p1") int id);
//
//    /**
//     * 删除状态为制单和驳回的明细数据
//     */
//    @Select("    "
//            + "  delete from m_goods_unit_convert                                                                               "
//            + "  where sku_id = #{p1,jdbcType=INTEGER}                                                                     "
//            + "   ")
//    void deleteBySkuId(@Param("p1") int sku_id);
//}
