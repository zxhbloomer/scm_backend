package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 公司主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MCompanyMapper extends BaseMapper<MCompanyEntity> {
    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + "     SELECT                                                                                                                  "
        + "         t1.id,                                                                                                              "
        + "         t1.`code`,                                                                                                          "
        + "         t1.company_no,                                                                                                      "
        + "         t1.`name`,                                                                                                          "
        + "         t1.simple_name,                                                                                                     "
        + "         t1.address_id,                                                                                                      "
        + "         t1.juridical_name,                                                                                                  "
        + "         t1.register_capital,                                                                                                "
        + "         t1.type,                                                                                                            "
        + "         t1.setup_date,                                                                                                      "
        + "         t1.end_date,                                                                                                        "
        + "         t1.descr,                                                                                                           "
        + "         t1.is_del,                                                                                                          "
        + "         t1.c_id,                                                                                                            "
        + "         t1.c_time,                                                                                                          "
        + "         t1.u_id,                                                                                                            "
        + "         t1.u_time,                                                                                                          "
        + "         t1.dbversion,                                                                                                       "
        + "         t2.postal_code,                                                                                                     "
        + "         t2.province_code,                                                                                                   "
        + "         t2.city_code,                                                                                                       "
        + "         t2.area_code,                                                                                                       "
        + "         t2.detail_address,                                                                                                  "
        + "         c_staff.name as c_name,                                                                                             "
        + "         u_staff.name as u_name,                                                                                             "
        + "         t3.label as type_name,                                                                                              "
        + "         f_get_org_full_name(vor.code, 'm_group') group_full_name,                                                           "
        + "         f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name                                                   "
        + "     FROM                                                                                                                    "
        + "         m_company AS t1                                                                                                     "
        + "         LEFT JOIN m_address AS t2 ON t1.address_id = t2.id                                                                  "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                                   "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                   "
        + "         LEFT JOIN v_dict_info AS t3 ON t3.code = '" + DictConstant.DICT_SYS_COMPANY_TYPE + "' and t3.dict_value = t1.type   "
        + "         LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_company' and vor.serial_id = t1.id                             "
        + "  where true                                                                                                                 "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                   "
        + "    and (t1.company_no like CONCAT ('%',#{p1.company_no,jdbcType=VARCHAR},'%') or #{p1.company_no,jdbcType=VARCHAR} is null) "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                                  "
        + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                                                "
        + "    and (                                                                                                                    "
        + "       case when #{p1.dataModel,jdbcType=VARCHAR} = '"+ DictConstant.DICT_ORG_USED_TYPE_SHOW_UNUSED +"' then                 "
        + "           not exists(                                                                                                       "
        + "                     select 1                                                                                                "
        + "                       from m_org subt1                                                                                      "
        + "                      where subt1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_COMPANY +"'                            "
        + "                        and t1.id = subt1.serial_id                                                                          "
        + "           )                                                                                                                 "
        + "       else true                                                                                                             "
        + "       end                                                                                                                   "
        + "        )                                                                                                                    "
        + "      ")
    IPage<MCompanyVo> selectPage(Page page, @Param("p1") MCompanyVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + "     SELECT                                                                                                  "
        + "         t1.id,                                                                                              "
        + "         t1.`code`,                                                                                          "
        + "         t1.`name`,                                                                                          "
        + "         t1.simple_name,                                                                                     "
        + "         t1.address_id,                                                                                      "
        + "         t1.juridical_name,                                                                                  "
        + "         t1.register_capital,                                                                                "
        + "         t1.type,                                                                                            "
        + "         t1.setup_date,                                                                                      "
        + "         t1.end_date,                                                                                        "
        + "         t1.descr,                                                                                           "
        + "         t1.is_del,                                                                                          "
//        + "         t1.tenant_id,                                                                                       "
        + "         t1.c_id,                                                                                            "
        + "         t1.c_time,                                                                                          "
        + "         t1.u_id,                                                                                            "
        + "         t1.u_time,                                                                                          "
        + "         t1.dbversion,                                                                                       "
        + "         t2.postal_code,                                                                                     "
        + "         t2.province_code,                                                                                   "
        + "         t2.city_code,                                                                                       "
        + "         t2.area_code,                                                                                       "
        + "         t2.detail_address,                                                                                  "
        + "         c_staff.name as c_name,                                                                             "
        + "         u_staff.name as u_name,                                                                             "
        + "         t3.label as is_del_name,                                                                            "
        + "         t4.label as type_name                                                                               "
        + "     FROM                                                                                                    "
        + "         m_company AS t1                                                                                     "
        + "         LEFT JOIN m_address AS t2 ON t1.address_id = t2.id                                                  "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                   "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                   "
        + "         LEFT JOIN v_dict_info AS t3 ON t3.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t3.dict_value = cast(t1.is_del as char(1))      "
        + "         LEFT JOIN v_dict_info AS t4 ON t4.code = '" + DictConstant.DICT_SYS_COMPANY_TYPE + "' and t4.dict_value = t1.type    "
        + "  where true                                                                                                 "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)   "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                  "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)           "
        + "                                                                                                             ")
    List<MCompanyVo> select(@Param("p1") MCompanyVo searchCondition);

    /**
     * 没有分页，按id筛选条件,导出
     * @param searchCondition
     * @return
     */
    @Select("<script>                                                                                                   "
        + "     SELECT                                                                                                  "
        + "         t1.id,                                                                                              "
        + "         t1.`code`,                                                                                          "
        + "         t1.`name`,                                                                                          "
        + "         t1.simple_name,                                                                                     "
        + "         t1.address_id,                                                                                      "
        + "         t1.juridical_name,                                                                                  "
        + "         t1.register_capital,                                                                                "
        + "         t1.type,                                                                                            "
        + "         t1.setup_date,                                                                                      "
        + "         t1.end_date,                                                                                        "
        + "         t1.descr,                                                                                           "
        + "         t1.is_del,                                                                                          "
//        + "         t1.tenant_id,                                                                                       "
        + "         t1.c_id,                                                                                            "
        + "         t1.c_time,                                                                                          "
        + "         t1.u_id,                                                                                            "
        + "         t1.u_time,                                                                                          "
        + "         t1.dbversion,                                                                                       "
        + "         t2.postal_code,                                                                                     "
        + "         t2.province_code,                                                                                   "
        + "         t2.city_code,                                                                                       "
        + "         t2.area_code,                                                                                       "
        + "         t2.detail_address,                                                                                  "
        + "         c_staff.name as c_name,                                                                             "
        + "         u_staff.name as u_name,                                                                             "
        + "         t3.label as is_del_name                                                                             "
        + "     FROM                                                                                                    "
        + "         m_company AS t1                                                                                     "
        + "         LEFT JOIN m_address AS t2 ON t1.address_id = t2.id                                                  "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                   "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                   "
        + "         LEFT JOIN v_dict_info AS t3 ON t3.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t3.dict_value = cast(t1.is_del as char(1))      "
        + "  where true                                                                                                 "
//        + "    and (t1.tenant_id = #{p2} or #{p2} is null  )                                                            "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                 "
        + "         #{item.id}                                                                                          "
        + "        </foreach>                                                                                           "
        + "  </script>")
    List<MCompanyVo> selectIdsInForExport(@Param("p1") List<MCompanyVo> searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>                                                                                                   "
        + " select t.*                                                                                                 "
        + "   from m_company t                                                                                          "
        + "  where true                                                                                                 "
        + "  and t.id in                                                                                                "
//        + "    and (t.tenant_id = #{p2} or #{p2} is null  )                                                             "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                 "
        + "         #{item.id}                                                                                          "
        + "        </foreach>                                                                                           "
        + "  </script>")
    List<MCompanyEntity> selectIdsIn(@Param("p1") List<MCompanyVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("                                                                        "
        + " select t.*                                                               "
        + "   from m_company t                                                       "
        + "  where true                                                              "
        + "    and t.code =  #{p1}                                                   "
        + "    and (t.id  <>  #{p2} or #{p2} is null)                                 "
        + "                                                                          ")
    List<MCompanyEntity> selectByCode(@Param("p1") String code, @Param("p2") Long equal_id );

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("                                                                        "
        + " select t.*                                                               "
        + "   from m_company t                                                       "
        + "  where true                                                              "
        + "    and t.name =  #{p1}                                                   "
        + "    and (t.id  <>  #{p2} or #{p2} is null)                                 "
        + "                                                                          ")
    List<MCompanyEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("                                                                      "
        + " select t.*                                                             "
        + "   from m_company t                                                     "
        + "  where true                                                            "
        + "    and t.simple_name =  #{p1}                                          "
        + "    and (t.id  <>  #{p2} or #{p2} is null)                               "
        + "                                                                        ")
    List<MCompanyEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 查询在组织架构中是否存在有被使用的数据
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                   "
        + " select count(1)                                                                                          "
        + "   from m_org t                                                                                      "
        + "  where true                                                                                         "
        + "    and t.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE + "'      "
        + "    and t.serial_id = #{p1.id,jdbcType=BIGINT}                                                       "
        + "                                                                                                     ")
    int isExistsInOrg(@Param("p1") MCompanyEntity searchCondition);

    /**
     *
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    @Select("    "
        + "     SELECT                                                                                                "
        + "         t1.id,                                                                                            "
        + "         t1.`code`,                                                                                        "
        + "         t1.`name`,                                                                                        "
        + "         t1.company_no,                                                                                    "
        + "         t1.simple_name,                                                                                   "
        + "         t1.address_id,                                                                                    "
        + "         t1.juridical_name,                                                                                "
        + "         t1.register_capital,                                                                              "
        + "         t1.type,                                                                                          "
        + "         t1.setup_date,                                                                                    "
        + "         t1.end_date,                                                                                      "
        + "         t1.descr,                                                                                         "
        + "         t1.is_del,                                                                                        "
        + "         t1.c_id,                                                                                          "
        + "         t1.c_time,                                                                                        "
        + "         t1.u_id,                                                                                          "
        + "         t1.u_time,                                                                                        "
        + "         t1.dbversion,                                                                                     "
        + "         t2.postal_code,                                                                                   "
        + "         t2.province_code,                                                                                 "
        + "         t2.city_code,                                                                                     "
        + "         t2.area_code,                                                                                     "
        + "         t2.detail_address,                                                                                "
        + "         c_staff.name as c_name,                                                                           "
        + "         u_staff.name as u_name                                                                            "
        + "     FROM                                                                                                  "
        + "         m_company AS t1                                                                                   "
        + "         LEFT JOIN m_address AS t2 ON t1.address_id = t2.id                                                "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                 "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                 "
        + "  where true                                                                                               "
        + "    and t1.id =#{p1}                                                                                       "
        + "      ")
    MCompanyVo selectId(@Param("p1") Long id);
}
