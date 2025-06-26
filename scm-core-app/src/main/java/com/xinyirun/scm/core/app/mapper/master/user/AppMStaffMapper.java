package com.xinyirun.scm.core.app.mapper.master.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionCountsVo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppMStaffMapper extends BaseMapper<MStaffEntity> {

    String common_select = "       "
            + "      SELECT                                                                                                         "
            + "            	t1.* ,                                                                                                  "
            + "            	t1.id staff_id ,                                                                                        "
            + "            	t1.name staff_name ,                                                                                    "
            + "            	t2.label as sex_text,                                                                                   "
            + "            	t3.label as service_text,                                                                               "
            + "            	t4.label as degree_text,                                                                                "
            + "            	t5.label as is_wed_text,                                                                                "
            + "            	t6.name as company_name,                                                                                "
            + "            	t6.simple_name as company_simple_name,                                                                  "
            + "            	t7.name as dept_name,                                                                                   "
            + "            	t7.simple_name as dept_simple_name,                                                                     "
            + "             c_staff.name as c_name,                                                                                 "
            + "             u_staff.name as u_name,                                                                                 "
            + "             t8.label as is_del_name,                                                                                "
            + "             t9.positions                                                                                            "
            + "        FROM                                                                                                         "
            + "            	m_staff t1                                                                                              "
            + "            	LEFT JOIN v_dict_info AS t2 ON t2.code = '" + DictConstant.DICT_SYS_SEX_TYPE + "' and t2.dict_value = t1.sex                      "
            + "            	LEFT JOIN v_dict_info AS t3 ON t3.code = '" + DictConstant.DICT_USR_SERVICE_TYPE + "' and t3.dict_value = t1.service              "
            + "            	LEFT JOIN v_dict_info AS t4 ON t4.code = '" + DictConstant.DICT_USR_DEGREE_TYPE + "' and t4.dict_value = t1.degree                "
            + "             LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_USR_WED_TYPE + "' and t5.dict_value = t1.is_wed                   "
            + "             LEFT JOIN m_company AS t6 ON t6.id = t1.company_id                                                      "
            + "             LEFT JOIN m_dept AS t7 ON t7.id = t1.dept_id                                                            "
            + "             LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       "
            + "             LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       "
            + "             LEFT JOIN v_dict_info AS t8 ON t8.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t8.dict_value = cast(t1.is_del as char(1)) "
            + "             LEFT JOIN (                                                                                             "
            + "                  select *                                                                                           "
            + "                    from (                                                                                           "
            + "                           SELECT                                                                                    "
            + "                                  subt1.staff_id,                                                                    "
            + "                                  count(*) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) count_order,     "
            + "                                  count(*) over ( PARTITION BY subt1.staff_id  ) count_position,                     "
            + "                                  JSON_ARRAYAGG( JSON_OBJECT(                                                                          "
            + "                                       'position_id', subt2.id, 'position_name', subt2.NAME, 'position_simple_name', subt2.simple_name                  "
            + "                                    ) ) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) AS positions                                           "
            + "                              FROM                                                                                   "
            + "                                   m_staff_org subt1                                                                 "
            + "                                   INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position' AND subt1.serial_id = subt2.id                       "
            + "                         ) tab"
            + "                   where tab.count_order = tab.count_position                                                        "
            + "                       ) t9 on t9.staff_id = t1.id                                                                   "
            + "       where true                                                                                                    "
            + "                    ";

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)           "
            + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                          "
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)      "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)      "
            + "      ")
    IPage<AppMStaffVo> selectPage(Page page, @Param("p1") AppMStaffVo searchCondition);

    /**
     * id查询当前用户数据
     */
    @Select("    "
            + "      SELECT                                                                                                         "
            + "            	t1.id as staff_id ,                                                                                     "
            + "            	t1.name as staff_name ,                                                                                 "
            + "            	t2.avatar  ,                                                                                            "
            + "            	t2.id as user_id ,                                                                                      "
            + "            	t2.login_name as user_login_name                                                                        "
            + "        FROM                                                                                                         "
            + "            	m_staff t1                                                                                              "
            + "            	LEFT JOIN m_user t2 on t1.user_id=t2.id                                                                 "
            + "       where true                                                                                                    "
            + "             and (t1.id = #{p1})                                                                                     "
            + "      ")
    AppMStaffVo getDetail(@Param("p1") Long staffId);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)           "
            + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                          "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)      "
            + "      ")
    List<AppMStaffVo> select(@Param("p1") AppMStaffVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
            + common_select
//        + "  and (t1.tenant_id  = #{p2} or #{p2} is null)                                                  "
            + "  and t1.id in                                                                                   "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id}                                                                              "
            + "        </foreach>                                                                               "
            + "  </script>                                                                                      ")
    List<AppMStaffVo> selectIdsIn(@Param("p1") List<AppMStaffVo> searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
            + common_select
//        + "  and (t1.tenant_id  = #{p2} or #{p2} is null)                                                  "
            + "  and t1.id in                                                                                   "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id}                                                                              "
            + "        </foreach>                                                                               "
            + "  </script>                                                                                      ")
    List<AppMStaffVo> exportSelectIdsIn(@Param("p1") List<AppMStaffVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + " select t.* "
            + "   from m_staff t "
            + "  where true "
            + "    and t.name =  #{p1}   "
            + "    and (t.id  =  #{p2} or #{p2} is null)   "
            + "      ")
    List<MStaffEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + " select t.* "
            + "   from m_staff t "
            + "  where true "
            + "    and t.name =  #{p1}   "
            + "    and (t.id  <>  #{p2} or #{p2} is null)   "
            + "      ")
    List<MStaffEntity> selectByNameNotEqualId(@Param("p1") String name, @Param("p2") Long not_equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + " select t.* "
            + "   from m_staff t "
            + "  where true "
            + "    and t.simple_name =  #{p1}   "
            + "    and (t.id  =  #{p2} or #{p2} is null)   "
            + "      ")
    List<MStaffEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + " select t.* "
            + "   from m_staff t "
            + "  where true "
            + "    and t.simple_name =  #{p1}   "
            + "    and (t.id  <>  #{p2} or #{p2} is null)   "
            + "      ")
    List<MStaffEntity> selectBySimpleNameNotEqualId(@Param("p1") String name, @Param("p2") Long not_equal_id);

    /**
     * 页面查询列表
     * @return
     */
    @Select("    "
            + common_select
            + "    and (t1.id =#{p1.staff_id,jdbcType=BIGINT} or #{p1.staff_id,jdbcType=BIGINT} is null)      "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)      "
            + "      ")
    AppMStaffVo selectId(@Param("p1") AppMStaffVo searchCondition);

    /**
     * 放到session中
     * @param p1
     * @return
     */
    @Select( "                                                  "
            + " select t.*                                          "
            + "   from m_staff t                                    "
            + "  where t.user_id = #{p1}                            "
//        + "    and (t1.tenant_id = #{p2} or #{p2} is null )    "
            + "                                                     ")
    MStaffEntity getDataByUser_id(@Param("p1") Long p1);

    /**
     * 查询在组织架构中是否存在有被使用的数据
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                   "
            + " select count(1)                                                                                     "
            + "   from m_org t                                                                                      "
            + "  where true                                                                                         "
            + "    and t.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_STAFF_SERIAL_TYPE + "'        "
            + "    and t.serial_id = #{p1.id,jdbcType=BIGINT}                                                       "
            + "                                                                                                     ")
    int isExistsInOrg(@Param("p1") MStaffEntity searchCondition);


    /**
     * 获取审批节点使用的数据
     * @param id
     * @return
     */
    @Select("                                                        "
            + "   SELECT                                             "
            + "       t1.id,                                         "
            + "       t1.code,                                       "
            + "       t1.`name`,                                     "
            + "       t2.avatar,                                     "
            + "       null as position,                              "
            + "       'user' as type                                 "
            + "   FROM                                               "
            + "       m_staff t1                                     "
            + "       LEFT JOIN m_user t2 on t2.id = t1.user_id      "
            + "   WHERE                                              "
            + "       t1.id =  #{p1}                                  "
            + "                                                      ")
    AppStaffUserBpmInfoVo getBpmDataByStaffid(@Param("p1") Long id);
}
