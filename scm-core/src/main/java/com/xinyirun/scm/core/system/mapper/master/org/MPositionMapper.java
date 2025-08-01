package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.user.MPositionInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 岗位主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MPositionMapper extends BaseMapper<MPositionEntity> {

    String COMMON_SELECT = "                                                                                            "
        + "                                                                                                             "
        + "        SELECT                                                                                               "
        + "               t1.*,                                                                                         "
        + "               c_staff.name as c_name,                                                                       "
        + "               u_staff.name as u_name,                                                                       "
        + "               t2.label as is_del_name,                                                                      "
        + "               t3.staff_count,                                                                               "
        + "               t4.role_count,                                                                                "
        + "               tt4.warehouse_count,                                                                          "
        + "               tt5.warehouse_count warehouse_count1,                                                         "
        + "               f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name,                            "
        + "               f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                         "
        + "               f_get_org_simple_name ( vor.CODE, 'm_dept' ) dept_full_simple_name                            "
        + "          FROM m_position t1                                                                                 "
        + "     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       "
        + "     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       "
         + "     LEFT JOIN v_dict_info AS t2 ON t2.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "'                    "
        + "    and t2.dict_value = CONCAT('', t1.is_del)                                                           "
        + "     left join (                                                                                             "
        + "                  select count(1) staff_count,                                                               "
        + "                         subt.serial_id,                                                                     "
        + "                         subt.serial_type                                                                    "
        + "                    from m_staff_org subt                                                                    "
        + "                group by subt.serial_id, subt.serial_type                                                    "
        + "                )  t3 on t3.serial_id = t1.id                                                                "
        + "           and t3.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "'            "
        + "     left join (                                                                                             "
        + "	            SELECT                                                                                          "
        + "	            	count( 1 ) role_count,                                                                      "
        + "	            	subt2.position_id                                                                           "
        + "	            FROM                                                                                            "
        + "	            	s_role subt1                                                                                "
        + "	            	INNER JOIN m_role_position subt2 ON subt1.id = subt2.role_id                                "
        + "	            WHERE                                                                                           "
        + "	            	subt1.is_del = '0'                                                                          "
        + "	            GROUP BY                                                                                        "
        + "	            	subt2.position_id                                                                           "
        + "                )  t4 on t4.position_id = t1.id                                                              "
        + "     left join (                                                                                             "
        + "                 select count(1) as warehouse_count,                                                         "
        + "                        ttab.serial_id                                                                       "
        + "                   from (                                                                                    "
        + "                             SELECT distinct com_t6.id,                                                      "
        + "                                    com_t1.serial_id                                                         "
        + "                               FROM b_warehouse_relation com_t1                                                       "
        + "                         inner JOIN m_position com_t2                                                        "
        + "                                 ON com_t1.serial_id = com_t2.id                                             "
        + "                                AND com_t1.serial_type = 'm_position'                                        "
        + "                         inner JOIN b_warehouse_relation com_t3                                              "
        + "                                 ON com_t3.serial_id = com_t2.id                                             "
        + "                                AND com_t3.serial_type = 'm_position'                                        "
        + "                         inner JOIN m_warehouse_relation com_t4                                              "
        + "                                 ON com_t3.warehouse_relation_code = com_t4.code                             "
        + "                                AND com_t4.serial_type = 'b_warehouse_group'                                 "
        + "                         inner JOIN b_warehouse_group_relation com_t5                                        "
        + "                                 ON com_t4.serial_id = com_t5.warehouse_group_id                             "
        + "                         inner join m_warehouse com_t6                                                       "
        + "                                 on com_t6.id = com_t5.warehouse_id                                          "
        + "                         inner join m_position com_t7 on com_t7.id = com_t1.serial_id                        "
        + "                          where com_t1.serial_type = 'm_position'                                        "
        + "                           ) ttab                                                                            "
        + "                          group by ttab.serial_id                                                            "
        + "                )  tt4 on tt4.serial_id = t1.id                                                              "
        + "     left join (                                                                                             "
        + "                 select count(1) as warehouse_count,                                                         "
        + "                        ttab.serial_id                                                                       "
        + "                   from (                                                                                    "
        + "                             SELECT distinct com_t1.serial_id,                                               "
        + "                                    com_t1.warehouse_id                                                      "
        + "                               FROM b_warehouse_position com_t1                                              "
        + "                           ) ttab                                                                            "
        + "                          group by ttab.serial_id                                                            "
        + "                )  tt5 on tt5.serial_id = t1.id                                                              "
        + "      LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id               "
        + "                                                                            ";


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where true                                                              "
        + "    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)  "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
        + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                              "
        + "    and (                                                                                                 "
        + "       case when #{p1.dataModel,jdbcType=VARCHAR} = '"+ DictConstant.DICT_ORG_USED_TYPE_SHOW_UNUSED +"' then   "
        + "           not exists(                                                                                    "
        + "                     select 1                                                                             "
        + "                       from m_org subt1                                                                   "
        + "                      where subt1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'    "
        + "                        and t1.id = subt1.serial_id                                                        "
        + "           )                                                                                              "
        + "       else true                                                                                          "
        + "       end                                                                                                "
        + "        )                                                                                                 "
        + "      ")
    IPage<MPositionVo> selectPage(Page page, @Param("p1") MPositionVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + COMMON_SELECT
            + "  where true                                                                                             "
            + "  and t1.id = #{p1.id,jdbcType=BIGINT}                                                                   "
            + "   ")
    MPositionVo getDetail(@Param("p1") MPositionVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "		SELECT                                                                                                                                                             "
            + "			@row_num := @row_num + 1 AS NO,                                                                                                                                "
            + "			t1.simple_name,                                                                                                                                                "
            + "         t1.name,                                                                                                                                                       "
            + "         t1.code,                                                                                                                                                       "
//            + "			c_staff.NAME AS c_name,                                                                                                                                        "
            + "			u_staff.NAME AS u_name,                                                                                                                                        "
            + "			t2.label AS delete_status,                                                                                                                                       "
            + "			tt6.role_name role_concat_name,                                                                                                                                "
            + "			t1.u_time,                                                                                                                                                     "
//            + "			t4.role_count,                                                                                                                                                 "
//            + "			tt5.warehouse_name,                                                                                                                                            "
//            + "			t3.staff_name,                                                                                                                                                 "
            + "			f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name,                                                                                             "
            + "			f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                                                                                          "
            + "			f_get_org_simple_name ( vor.CODE, 'm_dept' ) dept_full_simple_name                                                                                             "
            + "		FROM                                                                                                                                                               "
            + "			m_position t1                                                                                                                                                  "
//            + "			LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                                                                              "
            + "			LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                                                              "
            + "			LEFT JOIN v_dict_info AS t2 ON t2.CODE = 'sys_delete_type'                                                                                                     "
            + "			AND t2.dict_value = CONCAT('', t1.is_del)                                                                                                            "
//            + "			LEFT JOIN (                                                                                                                                                    "
//            + "			SELECT                                                                                                                                                         "
//            + "				subt1.serial_id,                                                                                                                                           "
//            + "				subt1.serial_type,                                                                                                                                         "
//            + "				GROUP_CONCAT( subt2.NAME SEPARATOR ',' ) staff_name                                                                                                        "
//            + "			FROM                                                                                                                                                           "
//            + "				m_staff_org subt1                                                                                                                                          "
//            + "				INNER JOIN m_staff subt2 ON subt1.staff_id = subt2.id                                                                                                      "
//            + "			GROUP BY                                                                                                                                                       "
//            + "				subt1.serial_id,                                                                                                                                           "
//            + "				subt1.serial_type                                                                                                                                          "
//            + "			) t3 ON t3.serial_id = t1.id                                                                                                                                   "
//            + "			AND t3.serial_type = 'm_position'                                                                                                                              "
//            + "			LEFT JOIN ( SELECT count( 1 ) role_count, subt.position_id FROM m_role_position subt GROUP BY subt.position_id ) t4 ON t4.position_id = t1.id                  "
//            + "			LEFT JOIN (                                                                                                                                                    "
//            + "			SELECT                                                                                                                                                         "
//            + "				ttab1.serial_id,                                                                                                                                           "
//            + "				GROUP_CONCAT( ttab2.short_name SEPARATOR ',' ) warehouse_name                                                                                              "
//            + "			FROM                                                                                                                                                           "
//            + "				b_warehouse_position ttab1                                                                                                                                 "
//            + "				INNER JOIN m_warehouse ttab2 ON ttab2.id = ttab1.warehouse_id                                                                                              "
//            + "			GROUP BY                                                                                                                                                       "
//            + "				ttab1.serial_id                                                                                                                                            "
//            + "			) tt5 ON tt5.serial_id = t1.id                                                                                                                                 "
            + "  LEFT JOIN (                                                                                                                                                               "
            + "    SELECT GROUP_CONCAT(sr.name) role_name, mrp.position_id                                                                                                             "
            + "    FROM m_role_position mrp                                                                                                                                            "
            + "    INNER JOIN s_role sr ON sr.id = mrp.role_id                                                                                                                         "
            + "    GROUP BY mrp.position_id"
            + "  ) tt6 ON tt6.position_id = t1.id                                                                                                                                      "
            + "			LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id,                                                                      "
            + "			( SELECT @row_num := 0 ) t6                                                                                                                                    "
            + "  where true "
            + "    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                              "
            + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                                                              "
            + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                                                                             "
            + "  ORDER BY t1.u_time DESC                                                                                                                                               "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)       "
            + "      ")
    List<MPositionExportVo> select(@Param("p1") MPositionVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>    "
            + "		SELECT                                                                                                                                                             "
            + "			@row_num := @row_num + 1 AS NO,                                                                                                                                "
//            + "         CASE WHEN t1.is_del = true THEN '已删除' ELSE '未删除' END delete_status,                                                                                        "
            + "			t1.simple_name,                                                                                                                                                "
            + "         t1.name,                                                                                                                                                       "
            + "         t1.code,                                                                                                                                                       "
//            + "			c_staff.NAME AS c_name,                                                                                                                                        "
            + "			u_staff.NAME AS u_name,                                                                                                                                        "
            + "			t2.label AS delete_status,                                                                                                                                     "
            + "			tt6.role_name role_concat_name,                                                                                                                                "
            + "			t1.u_time,                                                                                                                                                     "
//            + "			t4.role_count,                                                                                                                                                 "
//            + "			tt5.warehouse_name,                                                                                                                                            "
//            + "			t3.staff_name,                                                                                                                                                 "
            + "			f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name,                                                                                             "
            + "			f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                                                                                          "
            + "			f_get_org_simple_name ( vor.CODE, 'm_dept' ) dept_full_simple_name                                                                                             "
            + "		FROM                                                                                                                                                               "
            + "			m_position t1                                                                                                                                                  "
//            + "			LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                                                                              "
            + "			LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                                                              "
            + "			LEFT JOIN v_dict_info AS t2 ON t2.CODE = 'sys_delete_type'                                                                                                     "
            + "			AND t2.dict_value = CONCAT('', t1.is_del)                                                                                                            "
//            + "			LEFT JOIN (                                                                                                                                                    "
//            + "			SELECT                                                                                                                                                         "
//            + "				subt1.serial_id,                                                                                                                                           "
//            + "				subt1.serial_type,                                                                                                                                         "
//            + "				GROUP_CONCAT( subt2.NAME SEPARATOR ',' ) staff_name                                                                                                        "
//            + "			FROM                                                                                                                                                           "
//            + "				m_staff_org subt1                                                                                                                                          "
//            + "				INNER JOIN m_staff subt2 ON subt1.staff_id = subt2.id                                                                                                      "
//            + "			GROUP BY                                                                                                                                                       "
//            + "				subt1.serial_id,                                                                                                                                           "
//            + "				subt1.serial_type                                                                                                                                          "
//            + "			) t3 ON t3.serial_id = t1.id                                                                                                                                   "
//            + "			AND t3.serial_type = 'm_position'                                                                                                                              "
//            + "			LEFT JOIN ( SELECT count( 1 ) role_count, subt.position_id FROM m_role_position subt GROUP BY subt.position_id ) t4 ON t4.position_id = t1.id                  "
//            + "			LEFT JOIN (                                                                                                                                                    "
//            + "			SELECT                                                                                                                                                         "
//            + "				ttab1.serial_id,                                                                                                                                           "
//            + "				GROUP_CONCAT( ttab2.short_name SEPARATOR ',' ) warehouse_name                                                                                              "
//            + "			FROM                                                                                                                                                           "
//            + "				b_warehouse_position ttab1                                                                                                                                 "
//            + "				INNER JOIN m_warehouse ttab2 ON ttab2.id = ttab1.warehouse_id                                                                                              "
//            + "			GROUP BY                                                                                                                                                       "
//            + "				ttab1.serial_id                                                                                                                                            "
//            + "			) tt5 ON tt5.serial_id = t1.id                                                                                                                                 "
            + "  LEFT JOIN (                                                                                                                                                               "
            + "    SELECT GROUP_CONCAT(sr.name) role_name, mrp.position_id                                                                                                             "
            + "    FROM m_role_position mrp                                                                                                                                            "
            + "    INNER JOIN s_role sr ON sr.id = mrp.role_id                                                                                                                         "
            + "    GROUP BY mrp.position_id"
            + "  ) tt6 ON tt6.position_id = t1.id                                                                                                                                      "
            + "			LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id,                                                                      "
            + "			( SELECT @row_num := 0 ) t6                                                                                                                                    "
            + "  where true                                                                                                                                                            "
            + "    and t1.id in                                                                                                                                                        "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                                                                            "
            + "         #{item.id}                                                                                                                                                     "
            + "        </foreach>                                                                                                                                                      "
            + "  ORDER BY t1.u_time DESC                                                                                                                                               "
            + "</script>      ")
    List<MPositionExportVo> selectIdsInForExport(@Param("p1") List<MPositionVo> searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
        + " select t.* "
        + "   from m_position t "
        + "  where true "
//        + "    and (t.tenant_id = #{p2} or #{p2} is null  )                                               "
        + "    and t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>"
        + "         #{item.id}  "
        + "        </foreach>"
        + "  </script>")
    List<MPositionEntity> selectIdsIn(@Param("p1") List<MPositionVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from m_position t "
        + "  where true "
        + "    and t.code =  #{p1}   "
        + "    and (t.id  <>  #{p2} or #{p2} is null)   "
//        + "    and (t.tenant_id  = #{p4} or #{p4} is null)   "
        + "    and t.is_del =  0   "
        + "      ")
    List<MPositionEntity> selectByCode(@Param("p1") String code, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from m_position t "
        + "  where true "
        + "    and t.name =  #{p1}   "
        + "    and (t.id  <>  #{p2} or #{p2} is null )   "
//        + "    and (t.tenant_id  = #{p4} or #{p4} is null)   "
        + "    and t.is_del =  0   "
        + "      ")
    List<MPositionEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from m_position t "
        + "  where true "
        + "    and t.simple_name =  #{p1}   "
        + "    and (t.id  <>  #{p2} or #{p2} is null)   "
//        + "    and (t.tenant_id  = #{p4} or #{p4} is null)   "
        + "    and t.is_del =  0   "
        + "      ")
    List<MPositionEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 获取单条数据
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                       "
        + COMMON_SELECT
        + "  where true                                                                                             "
        + "    and (t1.id = #{p1.id,jdbcType=BIGINT})                                                               "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)      "
        + "                                                                          ")
    MPositionVo selectByid(@Param("p1") MPositionVo searchCondition);

    /**
     * 查询在组织架构中是否存在有被使用的数据
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                   "
        + " select count(1)                                                                                     "
        + "   from m_org t                                                                                      "
        + "  where true                                                                                         "
        + "    and t.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "'     "
        + "    and t.serial_id = #{p1.id,jdbcType=BIGINT}                                                       "
        + "                                                                                                     ")
    int isExistsInOrg(@Param("p1") MPositionEntity searchCondition);

    /**
     * 通过page_code获取岗位信息
     * @param page_code
     * @return
     */
    @Select("    "
            +"			SELECT                                                                                 "
            +"				t5.*                                                                               "
            +"			FROM                                                                                   "
            +"				m_permission_pages t1                                                              "
            +"				INNER JOIN m_permission_role t2 ON t1.permission_id = t2.permission_id             "
            +"				INNER JOIN m_role_position t3 ON t2.role_id = t3.role_id                           "
            +"				INNER JOIN s_role t4 ON t2.role_id = t4.id                                         "
            +"				INNER JOIN m_position t5 ON t3.position_id = t5.id                                 "
            +"			WHERE                                                                                  "
            +"			TRUE                                                                                   "
            +"				AND t4.is_del = 0                                                                  "
            +"				AND t4.is_enable = 1                                                               "
            +"				AND t5.is_del = 0                                                                  "
            +"				AND t1.CODE = #{p1,jdbcType=VARCHAR}                                               "
            + "      ")
    List<MPositionVo> selectPositionByPageCode(@Param("p1") String page_code);

    /**
     * 通过page_code获取岗位信息
     * @return
     */
    @Select("    "
            +"			SELECT                                                                                 "
            +"				t5.*                                                                               "
            +"			FROM                                                                                   "
            +"				m_permission_operation t1                                                          "
            +"				INNER JOIN m_permission_role t2 ON t1.permission_id = t2.permission_id             "
            +"				INNER JOIN m_role_position t3 ON t2.role_id = t3.role_id                           "
            +"				INNER JOIN s_role t4 ON t2.role_id = t4.id                                         "
            +"				INNER JOIN m_position t5 ON t3.position_id = t5.id                                 "
            +"			WHERE                                                                                  "
            +"			TRUE                                                                                   "
            +"				AND t1.is_enable = true                                                            "
            +"				AND t4.is_del = 0                                                                  "
            +"				AND t4.is_enable = 1                                                               "
            +"				AND t5.is_del = 0                                                                  "
            +"				AND t1.perms= #{p1,jdbcType=VARCHAR}                                               "
            + "      ")
    List<MPositionVo> selectPositionByPerms(@Param("p1") String perms);


    /**
     * 获取全部岗位
     * @param condition
     * @return
     */
    @Select("                                                                                                                                                           "
            + "	SELECT                                                                                                  "
            + "		subt2.id position_id,                                                                               "
            + "		subt2.NAME position_name,                                                                           "
            + "		subt2.simple_name position_simple_name,                                                             "
            + "		subt1.staff_id                                                                                      "
            + "	FROM                                                                                                    "
            + "		m_staff_org subt1                                                                                   "
            + "		INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position'                                     "
            + "		AND subt1.serial_id = subt2.id                                                                      "
            + "    and (subt1.staff_id =#{p1.staff_id,jdbcType=BIGINT} or #{p1.staff_id,jdbcType=BIGINT} is null)       "
            + " ")
    List<MPositionInfoVo> getAllPositionList(@Param("p1") MPositionInfoVo condition);

    /**
     * 查询岗位仓库组列表
     */
    @Select(" "
            + " SELECT                                                                                                  "
            + " 	t2.id serial_id,                                                                                    "
            + " 	'b_warehouse_group' serial_type,                                                                    "
            + " 	concat(t2.name,'(仓库组)') label,                                                                    "
            + " 	t2.CODE serial_code                                                                                 "
            + " FROM                                                                                                    "
            + " 	b_warehouse_relation t1                                                                             "
            + " 	INNER JOIN b_warehouse_group t2 ON t2.id = t1.serial_id                                             "
            + " 	AND t1.serial_type = 'b_warehouse_group'                                                            "
            + " WHERE                                                                                                   "
            + " TRUE                                                                                                    "
            + " 	AND t1.position_id = #{p1}                                                                          "
            + " ")
    List<TreeDataVo> selectWarehouseGroupList(@Param("p1") Long position_id);

    /**
     * 查询岗位仓库组仓库列表
     */
    @Select(" "
            + "	SELECT                                                                                                  "
            + "		t2.id serial_id,                                                                                    "
            + "		'm_warehouse' serial_type,                                                                          "
            + "		concat(t2.name,'(仓库)') label,                                                                      "
            + "		t2.CODE serial_code                                                                                 "
            + "	FROM                                                                                                    "
            + "		b_warehouse_group_relation t1                                                                       "
            + "		INNER JOIN m_warehouse t2 ON t2.id = t1.warehouse_id                                                "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t1.warehouse_group_id = #{p1}                                                                   "
            + " ")
    List<TreeDataVo> selectWarehouseListByGroupId(@Param("p1") Long warehouse_group_id);

    /**
     * 查询岗位仓库列表
     */
    @Select(" "
            + "	SELECT                                                                                                  "
            + "		t2.id serial_id,                                                                                    "
            + "		'm_warehouse' serial_type,                                                                          "
            + "		concat(t2.name,'(仓库)') label,                                                                      "
            + "		t2.CODE serial_code                                                                                 "
            + "	FROM                                                                                                    "
            + "		b_warehouse_relation t1                                                                             "
            + "		INNER JOIN m_warehouse t2 ON t2.id = t1.serial_id                                                   "
            + "		AND t1.serial_type = 'm_warehouse'                                                                  "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t1.position_id = #{p1}                                                                          "
            + " ")
    List<TreeDataVo> selectWarehouseListByPositionId(@Param("p1") Long position_id);

}
