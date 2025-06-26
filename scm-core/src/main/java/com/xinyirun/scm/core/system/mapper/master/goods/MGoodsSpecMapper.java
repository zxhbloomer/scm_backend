package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecLeftVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MGoodsSpecMapper extends BaseMapper<MGoodsSpecEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t7.name as prop_name,                                       "
            + "            t7.code as prop_code,                                       "
            + "            t.code as sku_code,                                         "
            + "            t6.id as goods_id,                                          "
            + "            t6.code as goods_code,                                      "
            + "            t6.name as goods_name,                                      "
            + "            t3.id as business_id,                                       "
            + "            t4.id as industry_id,                                       "
            + "            t5.id as category_id,                                       "
            + "            t3.name as business_name,                                   "
            + "            t4.name as industry_name,                                   "
            + "            t5.name as category_name,                                   "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_goods_spec t                                              "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                "
            + "  LEFT JOIN m_goods t6 ON t.goods_id = t6.id                            "
            + "  LEFT JOIN m_category t5 ON t6.category_id = t5.id                     "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                     "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                "
            + "  LEFT JOIN m_goods_spec_prop t7 ON t.prop_id = t7.id                   "
            + "                                                                        "
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
            + "    and (concat(ifnull(t.spec,''),                                                                       "
            + "     ifnull(t3.name,''),                                                                                 "
            + "     ifnull(t.name,''),                                                                                  "
            + "     ifnull(t4.name,''),                                                                                 "
            + "     ifnull(t6.name,''),                                                                                 "
            + "     ifnull(t5.name,''),                                                                                 "
            + "     ifnull(t7.name,''),                                                                                 "
            + "     ifnull(t.code,''))                                                                                  "
            + "like CONCAT ('%',#{p1.keyword,jdbcType=VARCHAR},'%') or #{p1.keyword,jdbcType=VARCHAR} is null)          "
            + "    and (t6.id = #{p1.goods_id} or #{p1.goods_id} is null) "
            + "      ")
    IPage<MGoodsSpecVo> selectPage(Page page, @Param("p1") MGoodsSpecVo searchCondition);

    /**
     * 查询树状列表
     * @param searchCondition
     * @return
     */
    @Select(" select    "
            + "  t.*     "
            + "  t3.name as business_name , t3.id asbusiness_id,   "
            + "  t4.name as industry_name , t4.id as industry_id,     "
            + "  t5.name as category_name , t5.id as category_id,     "
            + "  t6.name , t6.id as goods_id                            "
            + "  from    "
            + "  	       m_goods_spec t                                                  "
            + "  LEFT JOIN m_category t5 ON t.category_id = t5.id                "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                                 "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                "
            + "  LEFT JOIN m_goods t6 ON t6.goods_id = t5.id                "
            )
    List<MGoodsSpecLeftVo> selectLeft(MGoodsSpecLeftVo searchCondition);

    /**
     * 按条件获取数据
     * @param spec
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.enable =  true "
            + "    and t.spec =  #{p1} "
            + "      ")
    List<MGoodsSpecEntity> selectByName(@Param("p1") String spec);

    /**
     * 按条件获取数据
     * @param spec
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.enable =  true "
            + "    and t6.NAME =  #{p1} "
            + "    and t.spec =  #{p2} "
            + "      ")
    List<MGoodsSpecEntity> selectByName(@Param("p1") String goodsName, @Param("p2") String spec);

    /**
     * 按条件获取数据
     * @param code
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.goods_code =  #{p1}"
            + "      ")
    List<MGoodsSpecEntity> selectByGoodsCode(@Param("p1") String code);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<MGoodsSpecEntity> selectIdsIn(@Param("p1") List<MGoodsSpecVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MGoodsSpecVo selectId(@Param("p1") int id);

    /**
     * 按规格编码和来源获取数据
     * @param vo
     * @return
     */
    @Select("    "
            + common_select
            + "  where  t.code =  #{p1.code,jdbcType=VARCHAR}    "
            + "      ")
    MGoodsSpecEntity selectByCodeAppCode(@Param("p1") ApiGoodsSpecVo vo);

    /**
     * 按规格编码和来源获取数据
     * @param code
     * @return
     */
    @Select("    "
            + common_select
            + "  where  t.code =  #{p1,jdbcType=VARCHAR}    "
            + "      ")
    MGoodsSpecVo selectByCode(@Param("p1") String code);

    @Select(" <script>                                                                                                 "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.name,                                                                                     "
            + "            t.code,                                                                                     "
            + "            if(t.enable, '是', '否') enable,                                                             "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t.spec,                                                                                     "
            + "            t.code as sku_code,                                                                         "
            + "            t3.name as business_name,                                                                   "
            + "            t4.name as industry_name,                                                                   "
            + "            t5.name as category_name,                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t7.name as prop_name,                                                                       "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_goods_spec t                                                                              "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + "  LEFT JOIN m_goods t6 ON t.goods_id = t6.id                                                            "
            + "  LEFT JOIN m_category t5 ON t6.category_id = t5.id                                                     "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                                                     "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                                                "
            + "  LEFT JOIN m_goods_spec_prop t7 ON t.prop_id = t7.id                   "
            + " ,(select @row_num:=0) t15                                                                              "
            + "  where true                                                                                            "
            + "    and (concat(ifnull(t.spec,''),                                                                      "
            + "     ifnull(t3.name,''),                                                                                "
            + "     ifnull(t.name,''),                                                                                 "
            + "     ifnull(t4.name,''),                                                                                "
            + "     ifnull(t6.name,''),                                                                                "
            + "     ifnull(t5.name,''),                                                                                "
            + "     ifnull(t7.name,''),                                                                                "
            + "     ifnull(t.code,''))                                                                                 "
            + " like CONCAT ('%',#{p1.keyword,jdbcType=VARCHAR},'%') or #{p1.keyword,jdbcType=VARCHAR} is null)        "
            + "    and (t6.id = #{p1.goods_id} or #{p1.goods_id} is null)                                              "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                   "
            + "    and t.id in                                                                                         "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>        "
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + " </script>                                                                                              "
    )
    List<MGoodsSpecExportVo> exportList(@Param("p1") MGoodsSpecVo searchConditionList);

    /**
     * 查询物料转换商品, 转换前的, 过滤转换前的和转换后的
     * @param pageCondition
     * @param searchCondition
     * @return
     */
    @Select("<script>    "
            + common_select
            + "  where true "
            + "    and t.id not in (SELECT t1.source_sku_id FROM b_material_convert_detail t1 left join b_material_convert t2 "
            + "    ON t1.material_convert_id = t2.id where t1.source_sku_id is not null and t2.is_latested = true       "
            + "    AND (t2.owner_id = #{p1.owner_id} or #{p1.owner_id} is null or #{p1.owner_id} = '')                  "
            + "  <if test='p1.covert_type == 1'>                                                                        "
            + "    union all SELECT t1.target_sku_id FROM b_material_convert_detail t1 left join  b_material_convert t2 "
            + "    ON t1.material_convert_id = t2.id where t1.target_sku_id is not null and t2.is_latested = true       "
            + "    AND (t2.owner_id = #{p1.owner_id} or #{p1.owner_id} is null or #{p1.owner_id} = '')                  "
            + "  </if>)"
            + "    and (concat(ifnull(t.spec,''),                                                                       "
            + "     ifnull(t3.name,''),                                                                                 "
            + "     ifnull(t.name,''),                                                                                  "
            + "     ifnull(t4.name,''),                                                                                 "
            + "     ifnull(t6.name,''),                                                                                 "
            + "     ifnull(t5.name,''),                                                                                 "
            + "     ifnull(t7.name,''),                                                                                 "
            + "     ifnull(t.code,''))                                                                                  "
            + "like CONCAT ('%',#{p1.keyword,jdbcType=VARCHAR},'%') or #{p1.keyword,jdbcType=VARCHAR} is null)          "
            + "    and (t6.id = #{p1.goods_id} or #{p1.goods_id} is null)                                               "
            + " </script>     ")
    IPage<MGoodsSpecVo> getConvertGoodsList(Page<MBusinessTypeEntity> pageCondition,@Param("p1") MGoodsSpecVo searchCondition);

    @Select("    "
            + common_select
            + "  where true "
            + "    and (t6.id = #{p1.goods_id} or #{p1.goods_id} is null) "
            + "      ")
    List<MGoodsSpecVo> selectListByGoodsId(@Param("p1") MGoodsSpecVo searchCondition);

}
