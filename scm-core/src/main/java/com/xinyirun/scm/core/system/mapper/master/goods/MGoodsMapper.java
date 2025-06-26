package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;
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
public interface MGoodsMapper extends BaseMapper<MGoodsEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.name as business_name,                                          "
            + "            t3.id as business_id,                                          "
            + "            t4.name as industry_name,                                          "
            + "            t4.id as industry_id,                                          "
            + "            t5.name as category_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_goods t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_category t5 ON t.category_id = t5.id                "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                                 "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                "
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
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t3.name like CONCAT ('%',#{p1.business_name,jdbcType=VARCHAR},'%') or #{p1.business_name,jdbcType=VARCHAR} is null) "
            + "    and (t4.name like CONCAT ('%',#{p1.industry_name,jdbcType=VARCHAR},'%') or #{p1.industry_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.name like CONCAT ('%',#{p1.category_name,jdbcType=VARCHAR},'%') or #{p1.category_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.id = #{p1.category_id} or #{p1.category_id} is null) "
            + "      ")
    IPage<MGoodsVo> selectPage(Page page, @Param("p1") MGoodsVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.enable =  true "
            + "    and t.name =  #{p1} "
            + "      ")
    List<MGoodsEntity> selectByName(@Param("p1") String name);


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
    List<MGoodsEntity> selectIdsIn(@Param("p1") List<MGoodsVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MGoodsVo selectId(@Param("p1") int id);

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MGoodsExportVo>
     */
    @Select({"<script>                                                                                                 "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.name,                                                                                     "
            + "            t.code,                                                                                     "
            + "            if(t.enable, '是', '否') enable,                                                             "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t3.name as business_name,                                                                   "
            + "            t4.name as industry_name,                                                                   "
            + "            t5.name as category_name,                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_goods t                                                                                   "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + "  LEFT JOIN m_category t5 ON t.category_id = t5.id                                                      "
            + "  LEFT JOIN m_industry t4 ON t4.id = t5.industry_id                                                     "
            + "  LEFT JOIN m_business_type t3 ON t4.business_id = t3.id                                                "
            + " ,(select @row_num:=0) t6                                                                               "
            + "  where true                                                                                            "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t3.name like CONCAT ('%',#{p1.business_name,jdbcType=VARCHAR},'%') or #{p1.business_name,jdbcType=VARCHAR} is null) "
            + "    and (t4.name like CONCAT ('%',#{p1.industry_name,jdbcType=VARCHAR},'%') or #{p1.industry_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.name like CONCAT ('%',#{p1.category_name,jdbcType=VARCHAR},'%') or #{p1.category_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.id = #{p1.category_id} or #{p1.category_id} is null) "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                   "
            + "    and t.id in                                                                                         "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>        "
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + " </script>                                                                                              "

    })
    List<MGoodsExportVo> exportList(@Param("p1") MGoodsVo searchConditionList);

}
