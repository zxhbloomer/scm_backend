package com.xinyirun.scm.core.system.mapper.sys.rbac.role;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
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
        + "    and t1.is_del = false                                                                                        "
    )
    @Results({
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    IPage<SRoleVo> selectPage(Page page, @Param("p1") SRoleVo searchCondition );


    /**
     * 导出专用查询方法，支持动态排序（统一处理全部导出和选中导出）
     * @param searchCondition 查询条件（当ids不为空时进行选中导出，为空时进行全部导出）
     * @param orderByClause 排序子句
     * @return
     */
    @Select("""
        <script>
        SELECT (@row_num:= @row_num + 1) as no,
               t.code,
               t.type,
               t.name,
               t.descr,
               IF(t.is_del, '已删除', '未删除') as is_delete,
               t2.permissionList,
               t.u_time
          FROM s_role t
         LEFT JOIN (
                   SELECT GROUP_CONCAT(subt1.name) permissionList,
                          subt.role_id
                     FROM m_permission_role subt
                    INNER JOIN m_permission subt1 ON subt.permission_id = subt1.id
                    GROUP BY subt.role_id
                   ) t2 ON t2.role_id = t.id
               ,(SELECT @row_num := 0) r
         WHERE true
           AND (t.name LIKE CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') OR #{p1.name,jdbcType=VARCHAR} IS NULL)
           AND (t.code LIKE CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') OR #{p1.code,jdbcType=VARCHAR} IS NULL)
           AND (t.simple_name LIKE CONCAT ('%',#{p1.simple_name,jdbcType=VARCHAR},'%') OR #{p1.simple_name,jdbcType=VARCHAR} IS NULL)
           AND t.is_del = false
           <if test='p1.ids != null and p1.ids.length > 0'>
           AND t.id in
           <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                #{item}
           </foreach>
           </if>
        ${orderByClause}
        </script>
        """)
    List<SRoleExportVo> selectExportList(@Param("p1") SRoleVo searchCondition, @Param("orderByClause") String orderByClause);

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
            + "    and t.is_del = false                                                                                      "
            + "  ")
    int selectExportNum(@Param("p1") SRoleVo searchCondition);

    /**
     * 角色选择弹窗查询（无分页，不过滤删除状态）
     * @param searchCondition
     * @return
     */
    @Select(""
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
            + "    and (t1.is_del = #{p1.is_del} or #{p1.is_del} is null)                                                "
            + "  ORDER BY t1.u_time DESC"
    )
    @Results({
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    List<SRoleVo> selectListForDialog(@Param("p1") SRoleVo searchCondition);
}
