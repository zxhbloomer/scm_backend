package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MOrgEntity;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.bean.system.vo.master.org.*;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
public interface MOrgMapper extends BaseMapper<MOrgEntity> {

    String COMMON_TREE_SELECT = "                                                                                           "
            + "                  select t1.* ,                                                                              "
            + "                         t2.code,                                                                            "
            + "                         IFNULL(t3.name,'') as label,                                                        "
            + "                         t3.name,                                                                            "
            + "                         t3.simple_name,                                                                     "
            + "                         t2.type,                                                                            "
            + "                         t4.label as type_text,                                                              "
            + "                         t2.son_count,                                                                       "
            + "                         t2.u_time,                                                                          "
            + "                         t2.dbversion ,                                                                      "
            + "                         t2.serial_id ,                                                                      "
            + "                         t2.serial_type                                                                      "
            + "                    from v_org_tree t1                                                                       "
            + "          inner join m_org t2 on t1.id = t2.id                                                               "
            + "           left join v_org_name t3 on t3.serial_type = t2.serial_type and t3.serial_id = t2.serial_id        "
            + "           left join v_dict_info t4 on t4.dict_value = t2.type and t4.code = 'org_setting_type'                                          "
            + "                                                                          ";

    /**
     * 左侧树查询
     */
    @Select(" <script>    "
        + COMMON_TREE_SELECT
        + "  where true                                                                                              "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)       "
        + "    and (t2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)    "
        + "   <if test='p1.codes != null and p1.codes.length!=0' >                                                   "
        + "    and t3.serial_type in                                                                                 "
        + "        <foreach collection='p1.codes' item='item' index='index' open='(' separator=',' close=')'>        "
        + "         #{item}                                                                                          "
        + "        </foreach>                                                                                        "
        + "   </if>                                                                                                  "
        + "   <if test='p1.current_code != null ' >                                                                  "
        + "    and (                                                                                                 "
        + "          case when length(t2.code) >= length(#{p1.current_code,jdbcType=VARCHAR}) then                   "
        + "                              t2.code like CONCAT (#{p1.current_code,jdbcType=VARCHAR},'%')               "
        + "          else true                                                                                       "
        + "           end                                                                                            "
        + "         )                                                                                                "
        + "   </if>                                                                                                  "
        + "  order by t2.code                                                                                        "
        + " </script>     ")
    List<MOrgTreeVo> getTreeList(@Param("p1") MOrgTreeVo searchCondition);

    /**
     * 左侧树查询
     */
    @Select("    "
            + COMMON_TREE_SELECT
            + "  where true                                                                                         "
//            + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)  "
            + "      ")
    List<MOrgTreeVo> getList(@Param("p1") MOrgTreeVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_TREE_SELECT
        + "  where true                                                                                                "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)         "
        + "    and (t1.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)      "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
        + "      ")
    IPage<MOrgVo> selectPage(Page page, @Param("p1") MOrgVo searchCondition);



    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_TREE_SELECT
        + "  where true                                                                                              "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)    "
        + "    and (t2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)    "
        + "      ")
    List<MOrgTreeVo> select(@Param("p1") MOrgVo searchCondition);

    /**
     * 查询添加的子结点是否合法
     * @return
     */
    @Select("                                                                                                        "
        + "           SELECT                                                                                         "
        + "           	count(1)                                                                                     "
        + "           FROM                                                                                           "
        + "           	m_org t1                                                                                     "
        + "           WHERE true                                                                                     "
        + "           	and t1.CODE LIKE CONCAT (#{p1},'%')                                                          "
        + "           	and t1.type > #{p2}                                                                          "
        + "                                                                                                          ")
    Integer selectNodeInsertStatus(@Param("p1")String code, @Param("p2")String type);

    /**
     * 获取单条数据
     * @param id
     * @return
     */
    @Select("                                                                        "
        + COMMON_TREE_SELECT
        + "  where true                                                              "
        + "    and (t1.id = #{p1})                                                   "
//        + "    and (t1.tenant_id = #{p2} or #{p2} is null)                          "
        + "                                                                          ")
    MOrgVo selectByid(@Param("p1") Long id);

    /**
     * 新增模式下，可新增子结点得类型
     * @return
     */
    @Select("  <script>                                                                                              "
        + "           SELECT                                                                                         "
        + "               t2.label as `name`,                                                                        "
        + "               t2.dict_value as `value`,                                                                  "
        + "               t1.`name` as dict_type_code,                                                               "
        + "               t2.id as dict_data_id                                                                      "
        + "            FROM                                                                                          "
        + "               s_dict_type t1                                                                             "
        + "               INNER JOIN s_dict_data t2 ON t1.id = t2.dict_type_id                                       "
        + "               AND t1.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"                             "
        + "               AND t2.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"                             "
        + "           where                                                                                          "
        + "               t1.code =  '" + DictConstant.DICT_ORG_SETTING_TYPE + "'                             "
//        + "           and t2.dict_value >=  #{p1.type,jdbcType=VARCHAR}                                              "
        + "		      and exists (                                                                                   "
        + "		   					select t.type                                                                    "
        + "		   					  from m_org t                                                                   "
        + "		   					 where true                                                                      "
//        + "		   					   and (t.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)   "
        + "		   					   and t.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%')                      "
//        + "		   					   and t2.dict_value >= t.type                                                   "
        + "		   	            )                                                                                    "
        + "   <if test='p1.filter_para != null and p1.filter_para.length!=0' >                                       "
        + "         and t2.dict_value not in                                                                         "
        + "        <foreach collection='p1.filter_para' item='item' index='index' open='(' separator=',' close=')'>  "
        + "         #{item}                                                                                          "
        + "        </foreach>                                                                                        "
        + "   </if>                                                                                                  "
        + "   </script>                                                                                              ")
    List<NameAndValueVo> getCorrectTypeByInsertStatus(@Param("p1") MOrgVo vo);

    /**
     * check逻辑，查看是否存在重复的子组织
     * @param vo
     * @return
     */
    @Select("                                                                                                        "
        + "           SELECT                                                                                         "
        + "           	count(1)                                                                                     "
        + "           FROM                                                                                           "
        + "           	m_org t1                                                                                     "
        + "           WHERE true                                                                                     "
//        + "           	AND (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)    "
        + "           	AND t1.serial_type = #{p1.serial_type,jdbcType=VARCHAR}                                      "
        + "           	AND t1.serial_id = #{p1.serial_id,jdbcType=BIGINT}                                           "
        + "           	AND (t1.id  =  #{p2} or #{p2} is null)                                                       "
        + "                                                                                                          ")
    Integer getCountBySerial(@Param("p1") MOrgEntity vo, @Param("p2") Long equal_id);

    /**
     * 根据code，进行 like 'code%'，匹配当前结点以及子结点
     * @param vo
     * @return
     */
    @Select("                                                                                                        "
        + "        select *                                                                                          "
        + "          from m_org t1                                                                                   "
        + "         where true                                                                                       "
        + "           and t1.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%')                                      "
//        + "           and t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT}                                             "
        + "                                                                                                          ")
    List<MOrgEntity> getDataByCode(@Param("p1") MOrgEntity vo);

    String groupListSql = "                                                                                              "
        + "         select                                                                                               "
        + "                t1.*,                                                                                         "
        + "                t2.parent_serial_id,                                                                          "
        + "                t2.parent_serial_type,                                                                        "
        + "                t2.parent_type_text,                                                                          "
        + "                c_staff.name as c_name,                                                                       "
        + "                u_staff.name as u_name,                                                                       "
        + "                vor.parent_name parent_group_name,                                                            "
        + "                vor.parent_simple_name parent_group_simple_name                                               "
        + "          FROM                                                                                                "
        + "               m_group t1                                                                                     "
        + "    inner JOIN v_org_relation t2 ON t2.type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_GROUP +"'                "
        + "           and (t2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "           and t1.id = t2.serial_id                                                                           "
        + "     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                        "
        + "     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                        "
        + "     LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_group' and vor.serial_id = t1.id  and vor.parent_serial_type = 'm_group'      "
        + "         where true                                                                                           "
        +"                                                                                                               ";
    /**
     * 集团查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
        + groupListSql
        + "                                                                                                             ")
    IPage<MGroupVo> getGroupList(Page page, @Param("p1") MOrgTreeVo searchCondition);

    String companyListSql = "                                                                                           "
        + "        select                                                                                               "
        + "               t1.*,                                                                                         "
        + "               t2.parent_serial_id,                                                                          "
        + "               t2.parent_serial_type,                                                                        "
        + "               t2.parent_type_text,                                                                          "
        + "               c_staff.name as c_name,                                                                       "
        + "               u_staff.name as u_name,                                                                       "
        + "               f_get_org_full_name(vor.code, 'm_group') group_full_name,                                     "
        + "               f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name                             "
        + "          FROM m_company t1                                                                                  "
        + "    inner JOIN v_org_relation t2 ON t2.type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_COMPANY +"'             "
        + "           and (t2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "           and t1.id = t2.serial_id                                                                           "
        + "     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                        "
        + "     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                        "
        + "     LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_company' and vor.serial_id = t1.id                  "
        + "  where true                                                                                                 "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "                                                                                                             ";
    /**
     * 企业查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
        + companyListSql
        + "                                                                                                             ")
    IPage<MCompanyVo> getCompanyList(Page page, @Param("p1") MOrgTreeVo searchCondition);

    String deptListSql = "                                                                                              "
        + "           SELECT                                                                                            "
        + "           	t1.* ,                                                                                          "
        + "           	t2.`name` as handler_id_name,                                                                   "
        + "           	t3.`name` as sub_handler_id_name,                                                               "
        + "           	t4.`name` as leader_id_name,                                                                    "
        + "           	t5.`name` as response_leader_id_name,                                                           "
        + "             t6.parent_serial_id,                                                                            "
        + "             t6.parent_serial_type,                                                                          "
        + "             t6.parent_name,                                                                                 "
        + "             t6.parent_simple_name,                                                                          "
        + "             t6.parent_type_text,                                                                            "
        + "      	    f_get_org_full_name(vor.code, 'm_group') group_full_name,                                       "
        + "      	    f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name,                              "
        + "             f_get_org_full_name ( vor.CODE, 'm_company' )  company_name,                                    "
        + "             f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                           "
        + "             f_get_org_full_name ( vor.CODE, 'm_dept' ) parent_dept_name,                                    "
        + "             f_get_org_simple_name ( vor.CODE, 'm_dept' ) parent_dept_simple_name,                           "
        + "             c_staff.name as c_name,                                                                         "
        + "             u_staff.name as u_name                                                                          "
        + "           FROM                                                                                              "
        + "           	m_dept t1                                                                                       "
        + "      LEFT JOIN m_staff t2 on t1.handler_id = t2.id                                                          "
        + "      LEFT JOIN m_staff t3 on t1.sub_handler_id = t3.id                                                      "
        + "      LEFT JOIN m_staff t4 on t1.leader_id = t4.id                                                           "
        + "      LEFT JOIN m_staff t5 on t1.response_leader_id = t5.id                                                   "
        + "      LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       "
        + "      LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       "
        + "      inner JOIN v_org_relation t6 ON t6.type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_DEPT +"'        "
        + "           and (t6.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "           and t1.id = t6.serial_id                                                                           "
        + "      LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_dept' and vor.serial_id = t1.id                    "
        + "  where true                                                                                                 "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "                                                                                                             ";
    /**
     * 部门查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
        + deptListSql
        + "                                                                                                             ")
    IPage<MDeptVo> getDeptList(Page page, @Param("p1") MOrgTreeVo searchCondition);

    String positionListSql = "                                                                                          "
        + "           SELECT                                                                                            "
        + "           	  t1.*,                                                                                         "
        + "               t7.warehouse_count,                                                                           "
        + "               t3.staff_count,                                                                               "
        + "               f_get_org_full_name(vor.code, 'm_group') group_full_name,                                     "
        + "               f_get_org_simple_name(vor.code, 'm_group') group_full_simple_name,                            "
        + "               f_get_org_full_name ( vor.CODE, 'm_company' )  company_name,                                  "
        + "               f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                         "
        + "               f_get_org_full_name ( vor.CODE, 'm_dept' ) dept_full_name,                                    "
        + "               f_get_org_simple_name ( vor.CODE, 'm_dept' ) dept_full_simple_name,                           "
        + "               c_staff.name as c_name,                                                                       "
        + "               u_staff.name as u_name                                                                        "
        + "           FROM                                                                                              "
        + "           	m_position t1                                                                                   "
        + "     left join (                                                                                             "
        + "                  select count(1) staff_count,                                                               "
        + "                         subt.serial_id,                                                                     "
        + "                         subt.serial_type                                                                    "
        + "                    from m_staff_org subt                                                                    "
        + "                group by subt.serial_id, subt.serial_type                                                    "
        + "                )  t3 on t3.serial_id = t1.id                                                                "
        + "           and t3.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "'            "
        + "   LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                         "
        + "   LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                         "
        + "  inner JOIN v_org_relation t6 ON t6.type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_POSITION +"'              "
        + "         and (t6.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)  "
        + "         and t1.id = t6.serial_id                                                                            "
        + "   LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id                 "
        + "   left join (                                                                                             "
        + "                 select count(1) as warehouse_count,                                                         "
        + "                        ttab.serial_id                                                                       "
        + "                   from (                                                                                    "
        + "                             SELECT distinct com_t1.serial_id,                                               "
        + "                                    com_t1.warehouse_id                                                      "
        + "                               FROM b_warehouse_position com_t1                                              "
        + "                           ) ttab                                                                            "
        + "                          group by ttab.serial_id                                                            "
        + "                )  t7 on t7.serial_id = t1.id                                                                "
        + "  where true                                                                                                 "
//        + "    and (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)        "
        + "                                                                                                             ";


    /**
     * 岗位查询列表
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
        + positionListSql
        + "                                                                                                             ")
    IPage<MPositionVo> getPositionList(Page page, @Param("p1") MOrgTreeVo searchCondition);


    /**
     * 获取所有的组织以及子组织数量，仅仅是数量
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
        + "   select                                                                                                    "
        + "           IFNULL((                                                                                          "
        + "               null                                                                                          " //todo：待定
        + "            ),0) as orgs_count ,                                                                             "
        + "           IFNULL((                                                                                          "
        + "            select count(1) from ( " + groupListSql +")     tab2                                             "
        + "            ),0) as group_count ,                                                                            "
        + "           IFNULL((                                                                                          "
        + "            select count(1) from ( " + companyListSql +")   tab3                                             "
        + "           ),0)  as company_count,                                                                           "
        + "           IFNULL((                                                                                          "
        + "            select count(1) from ( " + deptListSql +")      tab4                                             "
        + "           ),0)  as dept_count,                                                                              "
        + "           IFNULL((                                                                                          "
        + "            select count(1) from ( " + positionListSql +")  tab5                                             "
        + "           ),0)  as position_count,                                                                          "
        + "           IFNULL((                                                                                          "
        + "            select count(1) from ( " + staffListSql + ")  tab6                                               "
        + "           ),0)  as staff_count                                                                              "
        + "                                                                                                             ")
    MOrgCountsVo getAllOrgDataCount(@Param("p1") MOrgVo searchCondition);

    /**
     * 拖拽的保存
     * @param entity
     * @return
     */
    @Update("                                                                        "
        + "    update m_org t                                                        "
        + "       set t.parent_id = #{p1.parent_id,jdbcType=BIGINT} ,                "
        + "           t.code = #{p1.code,jdbcType=VARCHAR} ,                         "
        + "           t.son_count = #{p1.son_count,jdbcType=INTEGER},                "
        + "           t.u_id = #{p1.u_id,jdbcType=BIGINT},                           "
        + "           t.u_time = #{p1.u_time,jdbcType=TIMESTAMP}                     "
        + "     where t.id = #{p1.id,jdbcType=BIGINT}                                "
        + "                                                                          "
    )
    int updateDragSave(@Param("p1")MOrgEntity entity);

    /**
     * 获取全部员工
     * @param condition
     * @return
     */
    @Select("                                                                        "
        + "     SELECT                                                               "
        + "             t1.id AS `key`,                                              "
        + "             t1.NAME AS label                                            "
        + "       FROM  m_staff t1                                                   "
        + "      WHERE                                                               "
        + "             t1.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"   "
        + "   order by  t1.name                                                      "
        + "                                                                          ")
    List<MStaffTransferVo> getAllStaffTransferList(@Param("p1")MStaffTransferVo condition);

    /**
     * 获取该岗位下，全部员工
     * @param condition
     * @return
     */
    @Select("                                                                                                         "
        + "     SELECT                                                                                                "
        + "             t1.staff_id AS `key`                                                                          "
        + "       FROM  m_staff_org t1                                                                                "
        + "  LEFT JOIN  m_staff t2 ON t1.staff_id = t2.id                                                             "
        + "      where  t1.serial_id = #{p1.position_id,jdbcType=BIGINT}                                              "
        + "        AND  t1.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "'     "
//        + "        AND  t1.tenant_id = (t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null) "
        + "   order by  t2.`name`                                                                                     "
        + "                                                                                                           ")
    List<Long> getUsedStaffTransferList(@Param("p1")MStaffTransferVo condition);

    /**
     * 获取要删除，员工岗位数据
     * @param bean
     * @return
     */
    @Select("  <script>        "
        + "       select t1.id ,                                                                                     "
        + "              t2.name as staff_name ,                                                                     "
        + "              t3.name as position_name ,                                                                  "
        + "              t1.c_id,                                                                                    "
        + "              t1.c_time,                                                                                  "
        + "              t1.u_id,                                                                                    "
        + "              t1.u_time                                                                                   "
        + "         from                                                                                             "
        + "               m_staff_org t1                                                                             "
        + "    left join  m_staff t2 on t1.staff_id = t2.id                                                          "
        + "    left join  m_position t3 on t3.id = t1.serial_id                                                      "
        + "        where                                                                                             "
        + "               t1.serial_id =  #{p1.position_id,jdbcType=BIGINT}                                          "
        + "          and  t1.serial_type =  '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "' "
//        + "          and  t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT}                                             "
        + "   <if test='p1.staff_positions != null and p1.staff_positions.length!=0' >                               "
        + "         and t1.staff_id not in                                                                           "
        + "        <foreach collection='p1.staff_positions' item='item' index='index' open='(' separator=',' close=')'>  "
        + "         #{item}                                                                                          "
        + "        </foreach>                                                                                        "
        + "   </if>                                                                                                  "
        + "   </script>                                                                                              ")
    List<MStaffPositionOperationVo> selete_delete_member(@Param("p1") MStaffTransferVo bean);

    /**
     * 获取要新增的员工岗位数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                              "
        + "       select  t1.id                                                                                      "
        + "         from  m_staff t1                                                                                 "
        + "        where  not exists (                                                                               "
        + "                 select true                                                                              "
        + "                   from m_staff_org t2                                                                    "
        + "                  where t2.serial_id = #{p1.position_id,jdbcType=BIGINT}                                  "
        + "                    and t2.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "' "
        + "                    and t1.id = t2.staff_id                                                               "
//        + "                    and t1.tenant_id = t2.tenant_id                                                       "
        + "              )                                                      "
//        + "           and  t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT}                                             "
        + "     <choose>                                                                                             "
        + "       <when test='p1.staff_positions != null and p1.staff_positions.length!=0'>                          "
        + "           and t1.id in                                                                             "
        + "          <foreach collection='p1.staff_positions' item='item' index='index' open='(' separator=',' close=')'>  "
        + "           #{item}                                                                                        "
        + "          </foreach>                                                                                      "
        + "       </when>                                                                                            "
        + "       <otherwise>                                                                                        "
        + "           and false                                                                                      "
        + "       </otherwise>                                                                                       "
        + "     </choose>                                                                                            "
        + "   </script>                                                                                              ")
    List<MStaffPositionOperationVo> selete_insert_member(@Param("p1") MStaffTransferVo bean);

    /**
     * 查询员工岗位数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                              "
        + "       select t1.id ,                                                                                     "
        + "              t2.name as staff_name ,                                                                     "
        + "              t3.name as position_name,                                                                   "
        + "              t1.c_id,                                                                                    "
        + "              t1.c_time,                                                                                  "
        + "              t1.u_id,                                                                                    "
        + "              t1.u_time                                                                                   "
        + "         from                                                                                             "
        + "               m_staff_org t1                                                                             "
        + "    left join  m_staff t2 on t1.staff_id = t2.id                                                                            "
        + "    left join  m_position t3 on t3.id = t1.serial_id                                                                            "
        + "        where                                                                                             "
        + "               t1.serial_id =  #{p1.position_id,jdbcType=BIGINT}                                          "
        + "          and  t1.serial_type =  '" + DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE + "' "
//        + "          and  t1.tenant_id = #{p1.tenant_id,jdbcType=BIGINT}                                             "
        + "   <if test='p1.staff_positions != null and p1.staff_positions.length!=0' >                               "
        + "         and t1.staff_id in                                                                           "
        + "        <foreach collection='p1.staff_positions' item='item' index='index' open='(' separator=',' close=')'>  "
        + "         #{item}                                                                                          "
        + "        </foreach>                                                                                        "
        + "   </if>                                                                                                  "
        + "   </script>                                                                                              ")
    List<MStaffPositionOperationVo> selete_member(@Param("p1") MStaffTransferVo bean);

    /**
     * 和 MOrgMapper.java 的COMMON_TREE_SELECT相同
     */
    String MORGMAPPER_CLASS_COMMON_TREE_SELECT = "                                                                      "
        + "              select t1.id ,                                                                                 "
        + "                     t2.code ,                                                                               "
        + "                     t2.type ,                                                                               "
//        + "                     t1.tenant_id ,                                                                          "
        + "                     t2.serial_id                                                                            "
        + "               from v_org_tree t1                                                                            "
        + "          inner join m_org t2 on t1.id = t2.id                                                               "
        + "                                                                                                             ";

    /**
     * 1:根据code的定义规则，0001xxxx|xxxx|，每4位为一个层，
     * 2：根据code中的定义找到相应的组织下所有的用户
     */
    String staffListSql = "                                                                      "
        + "      SELECT                                                                                                         "
        + "            	t1.* ,                                                                                                  "
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
        + "             t9.positions,                                                                                           "
        + "             t10.avatar as avatar,                                                                                   "
        + "             t10.login_name                                                                                          "
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
        + "             LEFT JOIN v_dict_info AS t8 ON t8.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t8.dict_value = CONCAT('', t1.is_del)      "
        + "             LEFT JOIN (                                                                                             "
        + "                  select *                                                                                    "
        + "                    from (                                                                                   "
        + "                           SELECT                                                                                   "
        + "                                  subt1.staff_id,                                                                                   "
        + "                                  count(*) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) count_order,                                                             "
        + "                                  count(*) over ( PARTITION BY subt1.staff_id  ) count_position,                                 "
        + "                                  JSON_ARRAYAGG( JSON_OBJECT(                       "
        + "                                       'position_id', subt2.id, 'position_name', subt2.NAME, 'position_simple_name', subt2.simple_name               "
        + "                                    ) ) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) AS positions                                                        "
        + "                              FROM                                                                                      "
        + "                                   m_staff_org subt1                                                                "
        + "                                   INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position' AND subt1.serial_id = subt2.id                                                                 "
        + "                         ) tab"
        + "                   where tab.count_order = tab.count_position                                                                 "
        + "                       ) t9 on t9.staff_id = t1.id                                                                   "
        + "             LEFT JOIN m_user t10 ON t10.staff_id = t1.id                                                            "
        + "       where true                                                                                                    "
        + "         and exists (                                                                                                "
        + "                select true                                                                                          "
        + "                  from m_staff_org subtab1                                                                           "
        + "                 where subtab1.staff_id = t1.id                                                                      "
        + "                   and subtab1.serial_type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE +"'  "
//        + "                   and subtab1.tenant_id = t1.tenant_id                                                              "
        + "                   and subtab1.serial_id in (                                                                        "
        + "                                              select subtab2.serial_id                                               "
        + "                                                from (" + MORGMAPPER_CLASS_COMMON_TREE_SELECT + ") subtab2           "
        + "                                               where subtab2.type = '"+ DictConstant.DICT_ORG_SETTING_TYPE_POSITION +"'  "
//        + "                                                 and subtab2.tenant_id = t1.tenant_id                                "
        + "                                                 and (subtab2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%')) "
        + "                                            )                                                                        "
        + "                    )                                                                                                "
//        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                          "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)                   "
        + "                                                                                                                     ";

    /**
     * 1:判断租户编号就行了
     */
    String allStaffListSql = "                                                                      "
        + "      SELECT                                                                                                         "
        + "            	t1.* ,                                                                                                  "
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
        + "             t9.positions,                                                                                           "
        + "             t10.avatar as avatar,                                                                                   "
        + "             t10.login_name                                                                                          "
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
        + "             LEFT JOIN v_dict_info AS t8 ON t8.code = '" + DictConstant.DICT_SYS_DELETE_MAP + "' and t8.dict_value = CONCAT('', t1.is_del) "
        + "             LEFT JOIN (                                                                                             "
        + "                  select *                                                                                           "
        + "                    from (                                                                                           "
        + "                           SELECT                                                                                    "
        + "                                  subt1.staff_id,                                                                    "
        + "                                  count(*) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) count_order,     "
        + "                                  count(*) over ( PARTITION BY subt1.staff_id  ) count_position,                     "
        + "                                  JSON_ARRAYAGG( JSON_OBJECT(                                                        "
        + "                                       'position_id', subt2.id, 'position_name', subt2.NAME, 'position_simple_name', subt2.simple_name                  "
        + "                                    ) ) over ( PARTITION BY subt1.staff_id ORDER BY subt2.id ) AS positions                                           "
        + "                              FROM                                                                                   "
        + "                                   m_staff_org subt1                                                                 "
        + "                                   INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position' AND subt1.serial_id = subt2.id                       "
        + "                         ) tab"
        + "                   where tab.count_order = tab.count_position                                                        "
        + "                       ) t9 on t9.staff_id = t1.id                                                                   "
        + "             LEFT JOIN m_user t10 ON t10.staff_id = t1.id                                                            "
        + "       where true                                                                                                    "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)                   "
        + "                                                                                                                     ";


    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
        + staffListSql
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                             "
        + "    and (t10.login_name like CONCAT ('%',#{p1.login_name,jdbcType=VARCHAR},'%') or #{p1.login_name,jdbcType=VARCHAR} is null)            "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                                            "
        + "      ")
    IPage<MStaffTabDataVo> selectStaff(@Param("p2") Page<MStaffTabDataVo> pageCondition,@Param("p1") MStaffTabDataVo searchCondition);


    /**
     * 获取员工count
     * @param searchCondition
     * @return
     */
    @Select("    "
        + " select count(1) "
        + "   from (                                                                                                            "
        +            staffListSql
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)           "
        + "    and (t10.login_name like CONCAT ('%',#{p1.login_name,jdbcType=VARCHAR},'%') or #{p1.login_name,jdbcType=VARCHAR} is null)          "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                          "
        + "        ) count_tab                                                                                                  "
        + "                                                                                                                     ")
    Integer getCurrentOrgStaffCount(@Param("p1") MStaffTabDataVo searchCondition);


    /**
     * 获取所有员工count
     * @param searchCondition
     * @return
     */
    @Select("    "
        + " select count(1) "
        + "   from (                                                                                                            "
        +            allStaffListSql
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)           "
        + "    and (t10.login_name like CONCAT ('%',#{p1.login_name,jdbcType=VARCHAR},'%') or #{p1.login_name,jdbcType=VARCHAR} is null)          "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                          "
        + "        )  count_tab                                                                                                 "
        + "                                                                                                                     ")
    Integer getAllOrgStaffCount(@Param("p1") MStaffTabDataVo searchCondition);

    /**
     * 获取所有员工count
     * @param searchCondition
     * @return
     */
    @Select("    "
        +    allStaffListSql
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                             "
        + "    and (t10.login_name like CONCAT ('%',#{p1.login_name,jdbcType=VARCHAR},'%') or #{p1.login_name,jdbcType=VARCHAR} is null)          "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                                            "
        + "                                                                                                                                       ")
    IPage<MStaffTabDataVo> getAllOrgStaff(@Param("p2") Page<MStaffTabDataVo> pageCondition, @Param("p1") MStaffTabDataVo searchCondition);

    /**
     * 获取指定组织的子节点数量（原方法保留兼容性）
     * @param orgId 组织ID
     * @return 子节点数量
     */
    @Select("SELECT COUNT(*) FROM m_org WHERE parent_id = #{orgId}")
    Integer getSubCount(@Param("orgId") Long orgId);

    /**
     * 获取集团的分类子节点详细统计
     * @param orgId 集团组织ID
     * @return 分类统计详情（子集团数量、企业数量）
     */
    @Select("SELECT " +
            "SUM(CASE WHEN type = '20' THEN 1 ELSE 0 END) as sub_group_count, " +
            "SUM(CASE WHEN type = '30' THEN 1 ELSE 0 END) as company_count " +
            "FROM m_org WHERE parent_id = #{orgId}")
    MOrgSubCountDetailVo getGroupSubCountDetail(@Param("orgId") Long orgId);


}
