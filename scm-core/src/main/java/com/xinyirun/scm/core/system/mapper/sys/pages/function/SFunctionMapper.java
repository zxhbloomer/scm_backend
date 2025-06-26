package com.xinyirun.scm.core.system.mapper.sys.pages.function;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.pages.function.SFunctionEntity;
import com.xinyirun.scm.bean.system.vo.sys.pages.function.SFunctionVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 按钮表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-06-16
 */
@Repository
public interface SFunctionMapper extends BaseMapper<SFunctionEntity> {

    /**
     * 页面查询
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                             "
        + "     SELECT                                                                                                    "
        + "            t1.*,                                                                                              "
        + "            c_staff.name as c_name,                                                                            "
        + "            u_staff.name as u_name,                                                                            "
        + "            t2.max_sort,                                                                                       "
        + "            t2.min_sort                                                                                        "
        + "       FROM                                                                                                    "
        + "  	       s_function t1                                                                                      "
        + "  LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                            "
        + "  LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                            "
        + "  INNER JOIN (                                                                                                 "
        + "  		SELECT                                                                                                "
        + "  			   count(1) - 1 AS max_sort,                                                                      "
        + "  			   0 AS min_sort                                                                                  "
        + "  		  FROM                                                                                                "
        + "  			   s_function                                                                                     "
        + "  	) t2 on true                                                                                              "
        + "      where true                                                                                               "
        + "        and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "        and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "   order by t1.sort                                                                                            "
        )
    List<SFunctionVo> selectPage(@Param("p1") SFunctionVo searchCondition);


    /**
     * 页面查询
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                             "
        + "     SELECT                                                                                                    "
        + "            t1.*,                                                                                              "
        + "            c_staff.name as c_name,                                                                            "
        + "            u_staff.name as u_name,                                                                            "
        + "            t2.max_sort,                                                                                       "
        + "            t2.min_sort                                                                                        "
        + "       FROM                                                                                                    "
        + "  	       s_function t1                                                                                      "
        + "  LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                            "
        + "  LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                            "
        + "  INNER JOIN (                                                                                                 "
        + "  		SELECT                                                                                                "
        + "  			   count(1) - 1 AS max_sort,                                                                      "
        + "  			   0 AS min_sort                                                                                  "
        + "  		  FROM                                                                                                "
        + "  			   s_function                                                                                     "
        + "  	) t2 on true                                                                                              "
        + "      where true                                                                                               "
        + "        and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "        and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "   order by t1.sort                                                                                            "
        + "                                                                                                              ")
    List<SFunctionVo> select(@Param("p1") SFunctionVo searchCondition);

    /**
     * 页面查询
     * @param id
     * @return
     */
    @Select("                                      "
        + "     SELECT                                                     "
        + "            t1.*,                                               "
        + "            c_staff.name as c_name,                             "
        + "            u_staff.name as u_name,                             "
        + "            t2.max_sort,                                        "
        + "            t2.min_sort                                         "
        + "       FROM                                                     "
        + "  	       s_function t1                                       "
        + "  LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id             "
        + "  LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id             "
        + "  INNER JOIN (                                                  "
        + "  		SELECT                                                 "
        + "  			   count(1) - 1 AS max_sort,                       "
        + "  			   0 AS min_sort                                   "
        + "  		  FROM                                                 "
        + "  			   s_function                                      "
        + "  	) t2 on true                                               "
        + "      where true                                                "
        + "    and t1.id =  #{p1}                                          "
        )
    SFunctionVo selectId(@Param("p1") Long id);

    /**
     * 获取排序最大序号
     */
    @Select("    "
        + "   SELECT                                                         "
        + "          (MAX(IFNULL(t.sort, 0))) AS sort                       "
        + "     FROM s_function t                                            "
        + "      ")
    SFunctionVo getSortNum();
}
