package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.system.vo.master.category.MCategoryExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MCategoryVo;
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
public interface MCategoryMapper extends BaseMapper<MCategoryEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_category t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  where true                                                                      "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select(common_select
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MCategoryVo> selectPage(Page page, @Param("p1") MCategoryVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select(common_select
            + "    and t.name =  #{p1}"
            + "      ")
    List<MCategoryEntity> selectByName(@Param("p1") String name);

    /**
     * 按名称查询（支持ID排除，用于重复性校验）
     * @param name 类别名称
     * @param excludeId 排除的ID（可为null）
     * @return 匹配的类别列表
     */
    @Select(common_select
            + "    and t.name = #{p1}"
            + "    and (#{p2} IS NULL OR t.id != #{p2})"
            + "      ")
    List<MCategoryEntity> selectByName(@Param("p1") String name, @Param("p2") Integer excludeId);

    /**
     * 按编码查询（支持ID排除，用于重复性校验）
     * @param code 类别编码
     * @param excludeId 排除的ID（可为null）
     * @return 匹配的类别列表
     */
    @Select(common_select
            + "    and t.code = #{p1}"
            + "    and (#{p2} IS NULL OR t.id != #{p2})"
            + "      ")
    List<MCategoryEntity> selectByCode(@Param("p1") String code, @Param("p2") Integer excludeId);


    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select
            + "  and t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<MCategoryEntity> selectIdsIn(@Param("p1") List<MCategoryVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  and t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MCategoryVo selectId(@Param("p1") int id);

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MCategoryExportVo>
     */
    @Select({" <script>                                                                                                "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.name,                                                                                     "
            + "            t.code,                                                                                     "
            + "            if(t.enable, '是', '否') enable,                                                            "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_category t                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + " ,(select @row_num:=0) t5                                                                               "
            + "  where true                                                                                            "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)"
            + "  <if test='p1.ids != null and p1.ids.length > 0'>                                                       "
            + "    and t.id in                                                                                         "
            + "      <foreach collection ='p1.ids' item='item' index='index' open='(' close=')' separator=','>         "
            + "          #{item}                                                                                       "
            + "       </foreach>                                                                                       "
            + "   </if>                                                                                                "
            + "  </script>                                                                                             "

    })
    List<MCategoryExportVo> exportList(@Param("p1") MCategoryVo searchConditionList);

    // ========== 删除校验查询方法（完全参考仓库管理） ==========

    /**
     * 检查类别下是否有启用状态的商品
     * @param categoryId 类别ID
     * @return 启用状态的商品记录数量
     */
    @Select("SELECT COUNT(1) FROM m_goods WHERE category_id = #{categoryId} AND enable = 1")
    Integer checkGoodsExists(@Param("categoryId") Integer categoryId);
}
