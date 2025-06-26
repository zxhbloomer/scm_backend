package com.xinyirun.scm.core.system.mapper.master.goods.unit;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitCalcEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitConvertVo;
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
 * @since 2022-01-16
 */
@Repository
public interface MGoodsUnitCalcMapper extends BaseMapper<MGoodsUnitCalcEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            concat(t.src_unit,':',t.tgt_unit,'[',t.calc,']') content,   "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_goods_unit_calc t                                         "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "  and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null) "
            + "  order by t.idx ")
    IPage<MGoodsUnitCalcVo> selectPage(Page page, @Param("p1") MGoodsUnitCalcVo searchCondition);


    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "  and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null) "
//            + "  and (t.src_unit_id = #{p1.src_unit_id,jdbcType=INTEGER} or #{p1.src_unit_id,jdbcType=INTEGER} is null) "  不建议使用这个条件，sku_id+src_unit_id应该只有一条
            + "  order by t.idx ")
    List<MGoodsUnitCalcVo> selectList(@Param("p1") MGoodsUnitCalcVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "     SELECT count(1)                                                                          "
            + "       FROM                                                                                   "
            + "  	       m_goods_unit_calc t                                                               "
            + "  where true                                                                                  "
            + "    and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null)   "
            + "  ")
    Integer getCount(@Param("p1") MGoodsUnitCalcVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "  and (t.sku_id = #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null) "
            + "  and (t.src_unit_id = #{p1.src_unit_id,jdbcType=INTEGER} or #{p1.src_unit_id,jdbcType=INTEGER} is null) "
            + "  order by t.idx ")
    MGoodsUnitCalcVo selectOne(@Param("p1") MGoodsUnitCalcVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "      	select t1.*                                                                          "
            + "      	  from m_unit t1	                                                                 "
            + "      	 where t1.enable = true                                                              "
            + "      	   and not exists (                                                                  "
            + "      	      	select 1                                                                     "
            + "      	   	      from m_goods_unit_calc t2                                                  "
            + "      	         where t2.sku_id = #{p1.sku_id,jdbcType=INTEGER}                             "
            + "      	           and t2.src_unit_id = t1.id                                                "
            + "      	     )                                                                               "
            + "        order by t1.name                                                                      "
            + "               ")
    List<MUnitVo> selectUnusedUnitsList(@Param("p1") MGoodsUnitCalcVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "      	select t1.*                                                                          "
            + "      	  from m_unit t1	                                                                 "
            + "      	 where t1.enable = true                                                              "
            + "        order by t1.name                                                                      "
            + "               ")
    List<MUnitVo> selectAllUnitsList(@Param("p1") MGoodsUnitCalcVo searchCondition);


    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MGoodsUnitCalcVo selectId(@Param("p1") int id);
}
