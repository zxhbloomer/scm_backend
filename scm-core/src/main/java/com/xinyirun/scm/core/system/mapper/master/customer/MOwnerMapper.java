package com.xinyirun.scm.core.system.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerExportVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
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
 * @since 2021-10-27
 */
@Repository
public interface MOwnerMapper extends BaseMapper<MOwnerEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_owner t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
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
            + "    and (CONCAT(t.name,t.short_name,t.name_pinyin,t.short_name_pinyin) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') "
            + "           or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MOwnerVo> selectPage(Page page, @Param("p1") MOwnerVo searchCondition);


    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    List<MOwnerVo> selectList(@Param("p1") MOwnerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<MOwnerEntity> selectByName(@Param("p1") String code);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<MOwnerEntity> selectByCode(@Param("p1") String code);

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
    List<MOwnerEntity> selectIdsIn(@Param("p1") List<MOwnerVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MOwnerVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MOwnerEntity selectByOwnerCode(@Param("p1") ApiCustomerVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}"
            + "      ")
    MOwnerEntity selectByOwnerCreditNo(@Param("p1") ApiCustomerVo vo);

    @Select({"<script>                                                                                                 "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.code,                                                                                     "
            + "            t.name,                                                                                     "
            + "            t.short_name,                                                                               "
            + "            if(t.enable, '启用', '禁用') enable,                                                          "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_owner t                                                                                   "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + " ,(select @row_num:=0) t23                                                                              "
            + "  where true                                                                                            "
            + "    and (CONCAT(t.name,t.short_name,t.name_pinyin,t.short_name_pinyin) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') "
            + "           or #{p1.name,jdbcType=VARCHAR} is null)                                                      "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "  <if test='p1.ids != null and p1.ids.size != 0'>                                                       "
            + "    and t.id in                                                                                         "
            + "      <foreach collection ='p1.ids' item='item' index='index' open='(' close=')' separator=','>         "
            + "          #{item}                                                                                       "
            + "       </foreach>                                                                                       "
            + "   </if>                                                                                                "
            + "  </script>                                                                                             "
    })
    List<MOwnerExportVo> exportList(@Param("p1") MOwnerVo searchCondition);
}
