package com.xinyirun.scm.core.system.mapper.sys.pages;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.pages.SPagesFunctionEntity;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionExportVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesFunctionVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 页面按钮表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-06-05
 */
@Repository
public interface SPagesFunctionMapper extends BaseMapper<SPagesFunctionEntity> {

    String common_select = ""
        + "    SELECT                                                                                                                "
        + "           t1.* ,                                                                                                         "
        + "           t2.code as page_code,                                                                                          "
        + "           t2.name as page_name,                                                                                          "
        + "           t2.perms as page_perms,                                                                                        "
        + "           t3.code as function_code,                                                                                      "
        + "           t3.name as function_name,                                                                                      "
        + "           c_staff.name as c_name,                                                                                        "
        + "           u_staff.name as u_name                                                                                         "
        + "      FROM                                                                                                                "
        + "           s_pages_function t1                                                                                            "
        + " LEFT JOIN s_pages t2 ON t1.page_id = t2.id                                                                               "
        + " LEFT JOIN s_function t3 ON t1.function_id = t3.id                                                                        "
        + " LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                                        "
        + " LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                        "
        + "     where true                                                                                                           "
        + "       and (t2.code like CONCAT ('%',#{p1.page_code,jdbcType=VARCHAR},'%') or #{p1.page_code,jdbcType=VARCHAR} is null)   "
        + "       and (t2.name like CONCAT ('%',#{p1.page_name,jdbcType=VARCHAR},'%') or #{p1.page_name,jdbcType=VARCHAR} is null)   "
        + "";

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + common_select
        + "   ")
    IPage<SPagesFunctionVo> selectPage(Page page, @Param("p1") SPagesFunctionVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + common_select
        + "      ")
    List<SPagesFunctionVo> select(@Param("p1") SPagesFunctionVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param condition
     * @return
     */
    @Select("    "
        + common_select
        + "  and t1.id =  #{p1.id, jdbcType=BIGINT}                                     "
        + "      ")
    SPagesFunctionVo selectId(@Param("p1") SPagesFunctionVo condition);

    @Select("    "
        + common_select
        + "  and (t1.id =  #{p1.id, jdbcType=BIGINT} or #{p1.id, jdbcType=BIGINT} is null)                             "
        + "  and (t1.id <> #{p1.ne_id, jdbcType=BIGINT} or #{p1.ne_id, jdbcType=BIGINT} is null)                       "
        + "  and (t1.page_id =  #{p1.page_id, jdbcType=BIGINT} or #{p1.page_id, jdbcType=BIGINT} is null)              "
        + "  and (t1.function_id =  #{p1.function_id, jdbcType=BIGINT} or #{p1.function_id, jdbcType=BIGINT} is null)  "
        + "      ")
    List<SPagesFunctionVo> selectByPageIdAndFunctionId(@Param("p1") SPagesFunctionVo condition);

    /**
     * 查询导出数量
     * @param searchConditionList 页面查询参数
     * @return int
     */
    @Select(""
            + "    SELECT                                                                                                                "
            + "        COUNT(1)                                                                                                          "
            + "      FROM                                                                                                                "
            + "           s_pages_function t1                                                                                            "
            + " LEFT JOIN s_pages t2 ON t1.page_id = t2.id                                                                               "
            + " LEFT JOIN s_function t3 ON t1.function_id = t3.id                                                                        "
            + "     where true                                                                                                           "
            + "       and (t2.code like CONCAT ('%',#{p1.page_code,jdbcType=VARCHAR},'%') or #{p1.page_code,jdbcType=VARCHAR} is null)   "
            + "       and (t2.name like CONCAT ('%',#{p1.page_name,jdbcType=VARCHAR},'%') or #{p1.page_name,jdbcType=VARCHAR} is null)   "
    )
    int selectExportNum(@Param("p1") SPagesFunctionVo searchConditionList);

    /**
     * 数据导出
     * @param searchConditionList
     * @return
     */
    @Select("<script>"
            + "    SELECT                                                                                                                "
            + "            @row_num:= @row_num+ 1 as no,                                                                                 "
            + "           t1.perms ,                                                                                                     "
            + "           t1.sort ,                                                                                                      "
            + "           t1.u_time ,                                                                                                    "
            + "           t2.code as page_code,                                                                                          "
            + "           t2.name as page_name,                                                                                          "
            + "           t2.perms as page_perms,                                                                                        "
            + "           t3.code as function_code,                                                                                      "
            + "           t3.name as function_name,                                                                                      "
            + "           u_staff.name as u_name                                                                                         "
            + "      FROM                                                                                                                "
            + "           s_pages_function t1                                                                                            "
            + " LEFT JOIN s_pages t2 ON t1.page_id = t2.id                                                                               "
            + " LEFT JOIN s_function t3 ON t1.function_id = t3.id                                                                        "
            + " LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                        "
            + " ,(select @row_num:=0) t4                                                                                                 "
            + "     where true                                                                                                           "
            + "       and (t2.code like CONCAT ('%',#{p1.page_code,jdbcType=VARCHAR},'%') or #{p1.page_code,jdbcType=VARCHAR} is null)   "
            + "       and (t2.name like CONCAT ('%',#{p1.page_name,jdbcType=VARCHAR},'%') or #{p1.page_name,jdbcType=VARCHAR} is null)   "
            + "  <if test='p1.ids != null and p1.ids.length != 0'>                                                      "
            + "  AND t1.id in                                                                                           "
            + "  <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>               "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "  </foreach>                                                                                             "
            + "  </if>                                                                                                  "
            + " order by t1.u_time DESC                                                                                 "
            + " </script>                                                                                                                "
    )
    List<SPagesFunctionExportVo> exportList(@Param("p1") SPagesFunctionVo searchConditionList);

}
