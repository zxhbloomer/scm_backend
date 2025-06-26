package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MIndustryVo;
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
public interface MIndustryMapper extends BaseMapper<MIndustryEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.name as business_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_industry t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_business_type t3 ON t3.id = t.business_id                                 "
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
            + "    and (t3.id = #{p1.business_id,jdbcType=INTEGER} or #{p1.business_id,jdbcType=INTEGER} is null) "
            + "      ")
    IPage<MIndustryVo> selectPage(Page page, @Param("p1") MIndustryVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.name =  #{p1}"
            + "      ")
    List<MIndustryEntity> selectByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     * @param businessTypeId
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t3.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    List<MIndustryEntity> selectByBusiness(@Param("p1") int businessTypeId);

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
    List<MIndustryEntity> selectIdsIn(@Param("p1") List<MIndustryVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MIndustryVo selectId(@Param("p1") int id);

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MIndustryExportVo>
     */
    @Select({ "<script>                                                                                                "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.name,                                                                                     "
            + "            t.code,                                                                                     "
            + "            if(t.enable, '是', '否') enable,                                                                      "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t3.name as business_name,                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_industry t                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + "  LEFT JOIN m_business_type t3 ON t3.id = t.business_id                                                 "
            + " ,(select @row_num:=0) t23                                                                              "
            + "  where true "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '') "
            + "    and (t3.name like CONCAT ('%',#{p1.business_name,jdbcType=VARCHAR},'%') or #{p1.business_name,jdbcType=VARCHAR} is null or #{p1.business_name,jdbcType=VARCHAR} = '') "
            + "    and (t3.id = #{p1.business_id,jdbcType=INTEGER} or #{p1.business_id,jdbcType=INTEGER} is null)      "
            + "  <if test='p1.ids != null and p1.ids.size != 0'>                                                       "
            + "    and t.id in                                                                                         "
            + "      <foreach collection ='p1.ids' item='item' index='index' open='(' close=')' separator=','>         "
            + "          #{item}                                                                                       "
            + "       </foreach>                                                                                       "
            + "   </if>                                                                                                "
            + "  </script>                                                                                             "
    })
    List<MIndustryExportVo> exportList(@Param("p1") MIndustryVo searchConditionList);
}
