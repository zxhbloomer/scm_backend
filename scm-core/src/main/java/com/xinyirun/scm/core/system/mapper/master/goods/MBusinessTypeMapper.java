package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MBusinessTypeExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MBusinessTypeVo;
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
 * @since 2021-09-27
 */
@Repository
public interface MBusinessTypeMapper extends BaseMapper<MBusinessTypeEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_business_type t                                                  "
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
    @Select("    "
            + common_select
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MBusinessTypeVo> selectPage(Page page, @Param("p1") MBusinessTypeVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    List<MBusinessTypeVo> selectList(@Param("p1") MBusinessTypeVo searchCondition);


    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "    and t.name =  #{p1}"
            + "      ")
    List<MBusinessTypeEntity> selectByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("    "
            + common_select
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<MBusinessTypeEntity> selectByCode(@Param("p1") String code);

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
    List<MBusinessTypeEntity> selectIdsIn(@Param("p1") List<MBusinessTypeVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  and t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MBusinessTypeVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     * @return
     */
    @Select("    "
            + common_select
            + "  and t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    MBusinessTypeVo selectBusinessByCode(@Param("p1") String code);

    /**
     * 商品板块
     *
     * @param searchCondition 入参
     * @return List<MBusinessTypeExportVo>
     */
    @Select({" <script>                                                                        "
                    + "     SELECT                                                             "
                    + "            t.name,                                                     "
                    + "            @row_num:= @row_num+ 1 as no,                               "
                    + "            t.code,                                                     "
                    + "            if(t.enable, '是', '否') enable,                             "
                    + "            t.c_time,                                                   "
                    + "            t.u_time,                                                   "
                    + "            t1.name as c_name,                                          "
                    + "            t2.name as u_name                                           "
                    + "       FROM                                                             "
                    + "  	       m_business_type t                                           "
                    + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                "
                    + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                "
                    + " ,(select @row_num:=0) t5                                               "
                    + "  where true                                                            "
                    + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '') "
                    + "   <if test='p1.ids != null and p1.ids.length != 0' >                                           "
                    + "    and t.id in                                                                                 "
                    + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>"
                    + "         #{item}                                                                                "
                    + "        </foreach>                                                                              "
                    + "   </if>                                                                                        "
                    + " </script>                                                                                      "
    })
    List<MBusinessTypeExportVo> exportList(@Param("p1") MBusinessTypeVo searchCondition);
}
