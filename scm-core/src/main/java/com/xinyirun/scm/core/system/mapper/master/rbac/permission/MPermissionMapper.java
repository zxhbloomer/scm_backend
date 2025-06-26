package com.xinyirun.scm.core.system.mapper.master.rbac.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionRoleOperationVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPermissionTransferVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MMenuRootNodeListVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MMenuRootNodeVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionMenuVo;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
@Repository
public interface MPermissionMapper extends BaseMapper<MPermissionEntity> {

    String COMMON_SELECT = "                                                              "
        + "                                                                               "
        + "      SELECT                                                                   "
        + "             t1.*,                                                             "
        + "             c_staff.name as c_name,                                           "
        + "             u_staff.name as u_name                                            "
        + "        FROM                                                                   "
        + "             m_permission t1                                                   "
        + "   LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                           "
        + "   LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                           "
        + "                                                                               ";


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where true                                                                                                "
        + "    and t1.type = '"+ DictConstant.DICT_MSTR_PERMISSION_TYPE_DEPT +"'                                "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
        + "    and t1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_DEPT +"'                              "
        + "    and t1.serial_id = #{p1.serial_id,jdbcType=BIGINT}                                                      "
        + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
        + "      ")
    IPage<MPermissionVo> selectPage(Page page, @Param("p1") MPermissionVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + COMMON_SELECT
            + "  where true                                                                                                "
            + "    and t1.type = '"+ DictConstant.DICT_MSTR_PERMISSION_TYPE_ROLE +"'                                "
            + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
            + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
            + "      ")
    IPage<MPermissionVo> selectRolePermissionPage(Page page, @Param("p1") MPermissionVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + COMMON_SELECT
            + "  where true                                                                                                "
            + "    and t1.type = '"+ DictConstant.DICT_MSTR_PERMISSION_TYPE_ROLE +"'                                "
            + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
            + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
            + "    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                               "
            + "      ")
    List<MPermissionVo> selectCascaderList(@Param("p1") MPermissionVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            +"		SELECT                                                                                    "
            +"			count(1)                                                                              "
            +"		FROM                                                                                      "
            +"			m_staff_org t1                                                                        "
            +"			INNER JOIN m_role_position t2 ON t2.position_id = t1.serial_id                        "
            +"			AND t1.serial_type = 'm_position'                                                     "
            +"			INNER JOIN m_permission_role t3 ON t3.role_id = t2.role_id                            "
            +"			INNER JOIN m_permission t4 ON t4.id = t3.permission_id                                "
            +"		WHERE                                                                                     "
            +"			t1.staff_id = #{p1,jdbcType=BIGINT}                                                   "
            +"			and t4.is_admin = true                                                                "
//            +"			GROUP BY t4.id                                                                        "
            + "      ")
    Integer selectPermissionsByStaffId(@Param("p1") Long staff_id);

    /**
     * 获取全部权限
     * @param condition
     * @return
     */
    @Select("                                                                        "
            + "     SELECT                                                               "
            + "             t1.id AS `key`,                                              "
            + "             t1.NAME AS label                                            "
//        + "             t1.tenant_id                                                 "
            + "       FROM  m_permission t1                                                   "
            + "      WHERE                                                               "
            + "             t1.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"   "
            + "          AND   t1.serial_type is null  "
            + "   order by  t1.name                                                      "
            + "                                                                          ")
    List<MPermissionTransferVo> getAllPermissionTransferList(@Param("p1") MPermissionTransferVo condition);

    /**
     * 获取全部权限
     * @param condition
     * @return
     */
    @Select("                                                                        "
            + "     SELECT                                                               "
            + "             t1.id AS `key`,                                              "
            + "             t1.NAME AS label                                            "
//        + "             t1.tenant_id                                                 "
            + "       FROM  m_permission t1                                                   "
            + "       inner join  m_permission_role t2 on t1.id = t2.permission_id                                                 "
            + "      WHERE                                                               "
            + "             t1.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"   "
            + "    and (t2.role_id =#{p1.role_id,jdbcType=BIGINT} or #{p1.role_id,jdbcType=BIGINT} is null)                               "
            + "          AND   t1.serial_type is null  "
            + "   order by  t1.name                                                      "
            + "                                                                          ")
    List<MPermissionTransferVo> getAllPermissionList(@Param("p1") MPermissionTransferVo condition);

    /**
     * 获取该角色下，全部权限
     * @param condition
     * @return
     */
    @Select("                                                                                                         "
            + "     SELECT                                                                                                "
            + "             t1.permission_id AS `key`                                                                          "
            + "       FROM  m_permission_role t1                                                                                "
            + "  LEFT JOIN  m_permission t2 ON t1.permission_id = t2.id                                                             "
            + "      where  t1.role_id = #{p1.role_id,jdbcType=BIGINT}                                              "
            + "   order by  t2.`name`                                                                                     "
            + "                                                                                                           ")
    List<Integer> getUsedPermissionTransferList(@Param("p1") MPermissionTransferVo condition);

    /**
     * 获取要删除，权限角色数据
     * @param bean
     * @return
     */
    @Select("  <script>        "
            + "       select t1.id ,                                                                                     "
            + "              t2.name as permission_name ,                                                                     "
            + "              t3.name as role_name ,                                                                  "
            + "              t1.c_id,                                                                                    "
            + "              t1.c_time,                                                                                  "
            + "              t1.u_id,                                                                                    "
            + "              t1.u_time                                                                                   "
            + "         from                                                                                             "
            + "               m_permission_role t1                                                                             "
            + "    left join  m_permission t2 on t1.permission_id = t2.id                                                          "
            + "    left join  s_role t3 on t3.id = t1.role_id                                                      "
            + "        where                                                                                             "
            + "               t1.role_id =  #{p1.role_id,jdbcType=BIGINT}                                          "
            + "   <if test='p1.role_permissions != null and p1.role_permissions.length!=0' >                               "
            + "         and t1.permission_id not in                                                                           "
            + "        <foreach collection='p1.role_permissions' item='item' index='index' open='(' separator=',' close=')'>  "
            + "         #{item}                                                                                          "
            + "        </foreach>                                                                                        "
            + "   </if>                                                                                                  "
            + "   </script>                                                                                              ")
    List<MPermissionRoleOperationVo> selectDeleteMember(@Param("p1") MPermissionTransferVo bean);

    /**
     * 获取要新增的权限角色数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                              "
            + "       select  t1.id                                                                                      "
            + "         from  m_permission t1                                                                                 "
            + "        where  not exists (                                                                               "
            + "                 select true                                                                              "
            + "                   from m_permission_role t2                                                                    "
            + "                  where t2.role_id = #{p1.role_id,jdbcType=BIGINT}                                  "
            + "                    and t1.id = t2.permission_id                                                               "
            + "              )                                                      "
            + "     <choose>                                                                                             "
            + "       <when test='p1.role_permissions != null and p1.role_permissions.length!=0'>                          "
            + "           and t1.id in                                                                             "
            + "          <foreach collection='p1.role_permissions' item='item' index='index' open='(' separator=',' close=')'>  "
            + "           #{item}                                                                                        "
            + "          </foreach>                                                                                      "
            + "       </when>                                                                                            "
            + "       <otherwise>                                                                                        "
            + "           and false                                                                                      "
            + "       </otherwise>                                                                                       "
            + "     </choose>                                                                                            "
            + "   </script>                                                                                              ")
    List<MPermissionRoleOperationVo> selectInsertMember(@Param("p1") MPermissionTransferVo bean);

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
            + "               m_permission_role t1                                                                             "
            + "    left join  m_permission t2 on t1.permission_id = t2.id                                                                            "
            + "    left join  s_role t3 on t3.id = t1.role_id                                                                            "
            + "        where                                                                                             "
            + "               t1.role_id =  #{p1.role_id,jdbcType=INTEGER}                                          "
            + "   <if test='p1.role_permissions != null and p1.role_permissions.length!=0' >                               "
            + "         and t1.permission_id in                                                                           "
            + "        <foreach collection='p1.role_permissions' item='item' index='index' open='(' separator=',' close=')'>  "
            + "         #{item}                                                                                          "
            + "        </foreach>                                                                                        "
            + "   </if>                                                                                                  "
            + "   </script>                                                                                              ")
    List<MPermissionRoleOperationVo> selectMember(@Param("p1") MPermissionTransferVo bean);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where true                                                                                                "
        + "    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)  "
        + "    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  "
        + "    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 "
//        + "    and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "      ")
    List<MPermissionVo> select(@Param("p1") MPermissionVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
        + COMMON_SELECT
        + "  where true                                                                                                "
        + "    and t1.id in                                                                                            "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                "
        + "         #{item.id}                                                                                         "
        + "        </foreach>                                                                                          "
        + "  </script>")
    List<MPermissionVo> selectIdsIn(@Param("p1") List<MPermissionVo> searchCondition);

    /**
     * 获取单条数据
     * @param id
     * @return
     */
    @Select("                                                                        "
        + COMMON_SELECT
        + "  where true                                                              "
        + "    and (t1.id = #{p1})                                                   "
        + "                                                                          ")
    MPermissionVo selectByid(@Param("p1") Long id);

    /**
     * 部门权限表数据获取系统菜单根节点
     * @param vo
     * @return
     */
    @Select("                                                                                                           "
        + "        select t1.id ,                                                                                       "
        + "               t1.id AS  VALUE,                                                                              "
        + "               t1.NAME AS label                                                                              "
        + "          FROM m_menu t1                                                                                     "
        + "   where true                                                                                                "
        + "     and t1.type = '" + DictConstant.DICT_SYS_MENU_TYPE_ROOT + "'                                     "
//        + "     and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "                                                                          ")
    List<MMenuRootNodeVo> getSystemMenuRootList(@Param("p1") MMenuRootNodeListVo vo);


    /**
     * 删除权限菜单
     * @return
     */
    @Delete("                                                                                                           "
            + "   delete                                                                                                "
            + "          FROM m_permission_menu t1                                                                      "
            + "   where                                                                                                 "
            + "    t1.permission_id = #{p1}                                                                             "
            + "                                                                          ")
    void deletePermissionMenu(@Param("p1") Long permission_id);

    /**
     * 查询权限菜单
     * @return
     */
    @Select("                                                                                                           "
            + "   select *                                                                                              "
            + "          FROM m_permission_menu t1                                                                      "
            + "   where                                                                                                 "
            + "   t1.is_enable = true                                                                                   "
            + "   and t1.permission_id = #{p1}                                                                          "
            + "                                                                          ")
    List<MPermissionMenuVo> selectPermissionMenu(@Param("p1") Long permission_id);

    /**
     * 查询权限下所有菜单
     * @return
     */
    @Select("                                                                                                           "
            + "   select *                                                                                              "
            + "          FROM m_permission_menu t1                                                                      "
            + "   where                                                                                                 "
            + "   t1.permission_id = #{p1}                                                                          "
            + "                                                                          ")
    List<MPermissionMenuVo> selectAllPermissionMenu(@Param("p1") Long permission_id);

}
