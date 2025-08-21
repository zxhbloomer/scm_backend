package com.xinyirun.scm.core.system.mapper.sys.rbac.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRolePositionOperationVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRoleTransferVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PermissionItemListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-11
 */
@Repository
public interface SRoleMapper extends BaseMapper<SRoleEntity> {

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("   "
        + " select t1.*, t2.permission_count, t2.permissionList "
        + "   from s_role t1 "
        + "     left join (                                                                                             "
        + "                  select count(1) permission_count,                                                               "
        + "                         subt.role_id,                                                                     "
        + "                         JSON_ARRAYAGG(JSON_OBJECT('id', subt1.id, 'key', subt1.name, 'label', subt1.name)) permissionList                                                  "
        + "                    from m_permission_role subt                                                                    "
        + "                    INNER JOIN m_permission subt1 ON subt.permission_id = subt1.id                               "
        + "                group by subt.role_id                                                  "
        + "                )  t2 on t2.role_id = t1.id                                                                "
        + "  where (t1.name        like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "    and (t1.code        like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "    and (t1.simple_name like CONCAT ('%',#{p1.simple_name,jdbcType=VARCHAR},'%') or #{p1.simple_name,jdbcType=VARCHAR} is null) "
        + "    and (t1.is_del = #{p1.is_del} or  #{p1.is_del} is null)                                                  "
    )
    @Results({
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    IPage<SRoleVo> selectPage(Page page, @Param("p1") SRoleVo searchCondition );

    /**
     * 按条件获取所有数据，导出
     * @param searchCondition
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "     @row_num:= @row_num+ 1 as no,                                                                       "
            + "     t.code,                                                                                             "
            + "     t.type,                                                                                             "
            + "     t.name,                                                                                             "
            + "     t.descr,                                                                                            "
            + "     IF(is_del, '已删除', '未删除') is_delete,                                                             "
            + "     t2.permissionList,                                                                                  "
            + "     t.u_time                                                                                            "
            + "   FROM s_role t                                                                                         "
            + "   LEFT JOIN (                                                                                           "
            + "       SELECT GROUP_CONCAT(subt1.name) permissionList,                                                   "
            + "                         subt.role_id                                                                    "
            + "       FROM m_permission_role subt                                                                       "
            + "       INNER JOIN m_permission subt1 ON subt.permission_id = subt1.id                                    "
            + "                group by subt.role_id                                                                    "
            + "                )  t2 on t2.role_id = t.id                                                               "
            + "  ,(select @row_num:=0) t3                                                                               "
            + "  where (t.name        like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code        like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.simple_name like CONCAT ('%',#{p1.simple_name,jdbcType=VARCHAR},'%') or #{p1.simple_name,jdbcType=VARCHAR} is null) "
            + "    and (t.is_del = #{p1.is_del} or #{p1.is_del} is null)                                                "
            + "  ORDER BY t.u_time DESC                                                                                 "
            + "  ")
    List<SRoleExportVo> selectExportAll(@Param("p1") SRoleVo searchCondition );

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
        + " select t.* "
        + "   from s_role t "
        + "  where t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>"
        + "         #{item.id}  "
        + "        </foreach>"
        + "  </script>")
    List<SRoleEntity> selectIdsIn(@Param("p1") List<SRoleVo> searchCondition );

    /**
     * 获取全部权限
     * @param condition
     * @return
     */
    @Select("                                                                        "
            + "     SELECT                                                               "
            + "             t1.id AS `key`,                                              "
            + "             t1.NAME AS label                                            "
            + "       FROM  s_role t1                                                   "
            + "      WHERE                                                               "
            + "             t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+"   "
            + "   order by  t1.name                                                      "
            + "                                                                          ")
    List<MRoleTransferVo> getAllRoleTransferList(@Param("p1") MRoleTransferVo condition);

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
            + "       FROM  s_role t1                                                   "
            + "       inner join  m_role_position t2 on t1.id = t2.role_id                                                 "
            + "      WHERE                                                               "
            + "             t1.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"   "
            + "    and (t2.position_id =#{p1.position_id,jdbcType=BIGINT} or #{p1.position_id,jdbcType=BIGINT} is null)                               "
            + "   order by  t1.name                                                      "
            + "                                                                          ")
    List<MRoleTransferVo> getAllRoleList(@Param("p1") MRoleTransferVo condition);

    /**
     * 获取该岗位下，全部角色
     * @param condition
     * @return
     */
    @Select("                                                                                                                  "
            + "     SELECT                                                                                                     "
            + "             t1.role_id AS `key`                                                                                "
            + "       FROM  m_role_position t1                                                                                 "
            + "  LEFT JOIN  s_role t2 ON t1.role_id = t2.id                                                                    "
            + "      where  t1.position_id = #{p1.position_id,jdbcType=BIGINT}                                                 "
            + "   order by  t2.`name`                                                                                          "
            + "                                                                                                                ")
    List<Integer> getUsedRoleTransferList(@Param("p1") MRoleTransferVo condition);

    /**
     * 获取要删除，角色岗位数据
     * @param bean
     * @return
     */
    @Select("  <script>        "
            + "       select t1.id ,                                                                                           "
            + "              t2.name as role_name ,                                                                            "
            + "              t3.name as position_name ,                                                                        "
            + "              t1.c_id,                                                                                          "
            + "              t1.c_time,                                                                                        "
            + "              t1.u_id,                                                                                          "
            + "              t1.u_time                                                                                         "
            + "         from                                                                                                   "
            + "               m_role_position t1                                                                               "
            + "    left join  s_role t2 on t1.role_id = t2.id                                                                  "
            + "    left join  m_position t3 on t3.id = t1.position_id                                                          "
            + "        where                                                                                                   "
            + "               t1.position_id =  #{p1.position_id,jdbcType=BIGINT}                                              "
            + "   <if test='p1.position_roles != null and p1.position_roles.length!=0' >                                       "
            + "         and t1.role_id not in                                                                                  "
            + "        <foreach collection='p1.position_roles' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item}                                                                                                "
            + "        </foreach>                                                                                              "
            + "   </if>                                                                                                        "
            + "   </script>                                                                                                    ")
    List<MRolePositionOperationVo> selectDeleteMember(@Param("p1") MRoleTransferVo bean);

    /**
     * 获取要新增的岗位权限数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                                        "
            + "       select  t1.id                                                                                            "
            + "         from  s_role t1                                                                                        "
            + "        where  not exists (                                                                                     "
            + "                 select true                                                                                    "
            + "                   from m_role_position t2                                                                      "
            + "                  where t2.position_id = #{p1.position_id,jdbcType=BIGINT}                                      "
            + "                    and t1.id = t2.role_id                                                                      "
            + "              )                                                                                                 "
            + "     <choose>                                                                                                   "
            + "       <when test='p1.position_roles != null and p1.position_roles.length!=0'>                                  "
            + "           and t1.id in                                                                                         "
            + "          <foreach collection='p1.position_roles' item='item' index='index' open='(' separator=',' close=')'>   "
            + "           #{item}                                                                                              "
            + "          </foreach>                                                                                            "
            + "       </when>                                                                                                  "
            + "       <otherwise>                                                                                              "
            + "           and false                                                                                            "
            + "       </otherwise>                                                                                             "
            + "     </choose>                                                                                                  "
            + "   </script>                                                                                                    ")
    List<MRolePositionOperationVo> selectInsertMember(@Param("p1") MRoleTransferVo bean);

    /**
     * 查询员工岗位数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                                        "
            + "       select t1.id ,                                                                                           "
            + "              t2.name as staff_name ,                                                                           "
            + "              t3.name as position_name,                                                                         "
            + "              t1.c_id,                                                                                          "
            + "              t1.c_time,                                                                                        "
            + "              t1.u_id,                                                                                          "
            + "              t1.u_time                                                                                         "
            + "         from                                                                                                   "
            + "               m_role_position t1                                                                               "
            + "    left join  s_role t2 on t1.role_id = t2.id                                                                  "
            + "    left join  m_position t3 on t3.id = t1.position_id                                                          "
            + "        where                                                                                                   "
            + "               t1.position_id =  #{p1.position_id,jdbcType=INTEGER}                                             "
            + "   <if test='p1.position_roles != null and p1.position_roles.length!=0' >                                       "
            + "         and t1.role_id in                                                                                      "
            + "        <foreach collection='p1.position_roles' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item}                                                                                                "
            + "        </foreach>                                                                                              "
            + "   </if>                                                                                                        "
            + "   </script>                                                                                                    ")
    List<MRolePositionOperationVo> selectMember(@Param("p1") MRoleTransferVo bean);

    /**
     * 部分数据导出
     * @param searchConditionList
     * @return
     */
    @Select("<script>"
            + " SELECT                                                                                                  "
            + "     @row_num:= @row_num+ 1 as no,                                                                       "
            + "     t.code,                                                                                             "
            + "     t.type,                                                                                             "
            + "     t.name,                                                                                             "
            + "     t.descr,                                                                                            "
            + "     IF(is_del, '已删除', '未删除') is_delete,                                                             "
            + "     t2.permissionList,                                                                                  "
            + "     t.u_time                                                                                            "
            + "   FROM s_role t                                                                                         "
            + "   LEFT JOIN (                                                                                           "
            + "       SELECT GROUP_CONCAT(subt1.name) permissionList,                                                   "
            + "                         subt.role_id                                                                    "
            + "       FROM m_permission_role subt                                                                       "
            + "       INNER JOIN m_permission subt1 ON subt.permission_id = subt1.id                                    "
            + "                group by subt.role_id                                                                    "
            + "                )  t2 on t2.role_id = t.id                                                               "
            + "  ,(select @row_num:=0) t3                                                                               "
            + "  where t.id in                                                                                          "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.id}                                                                                      "
            + "        </foreach>                                                                                       "
            + "  ORDER BY t.u_time DESC                                                                                 "
            + "  </script>")
    List<SRoleExportVo> selectExportList(@Param("p1") List<SRoleVo> searchConditionList);

    /**
     * 查询数量
     * @param searchCondition
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "     COUNT(1)                                                                       "
            + "   FROM s_role t                                                                                         "
            + "  where (t.name        like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code        like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.simple_name like CONCAT ('%',#{p1.simple_name,jdbcType=VARCHAR},'%') or #{p1.simple_name,jdbcType=VARCHAR} is null) "
            + "    and (t.is_del = #{p1.is_del} or #{p1.is_del} is null)                                                "
            + "  ")
    int selectExportNum(@Param("p1") SRoleVo searchCondition);
}
