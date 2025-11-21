package com.xinyirun.scm.ai.core.mapper.mcp;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 权限查询Mapper
 *
 * 提供用户页面权限和按钮权限的数据库查询
 *
 * RBAC权限模型:
 * staff_id → m_staff_org → m_role_position → s_role → m_permission_role → m_permission → m_permission_pages → m_permission_operation
 *
 * @author zzxxhh
 * @since 2025-11-21
 */
@Mapper
public interface PermissionAiMapper {

    /**
     * 根据页面名称模糊查询用户可访问的页面
     *
     * 通过RBAC权限链查询用户有权访问且名称匹配的页面
     *
     * @param staffId 员工ID
     * @param pageName 页面名称(支持模糊匹配)
     * @return 匹配的页面列表,包含page_code和page_name
     */
    @Select("""
        SELECT DISTINCT
            t6.`code` AS page_code,
            t6.`name` AS page_name,
            sp.meta_title AS page_title
        FROM m_staff_org t1
        INNER JOIN m_role_position t2 ON t1.serial_id = t2.position_id
        INNER JOIN s_role t3 ON t3.id = t2.role_id AND t3.is_del = 0
        INNER JOIN m_permission_role t4 ON t2.role_id = t4.role_id
        INNER JOIN m_permission t5 ON t5.id = t4.permission_id AND t5.is_del = 0
        INNER JOIN m_permission_pages t6 ON t6.permission_id = t5.id
        LEFT JOIN s_pages sp ON sp.code = t6.`code`
        WHERE t1.staff_id = #{staffId}
            AND t1.serial_type = 'm_position'
            AND (t6.`name` LIKE CONCAT('%', #{pageName}, '%')
                 OR sp.meta_title LIKE CONCAT('%', #{pageName}, '%'))
        ORDER BY t6.`code`
        """)
    List<Map<String, Object>> findPagesByName(@Param("staffId") Long staffId,
                                               @Param("pageName") String pageName);

    /**
     * 根据页面编码查询用户的按钮权限
     *
     * @param staffId 员工ID
     * @param pageCode 页面编码
     * @return 按钮权限列表,包含perms(权限标识)和descr(权限描述)
     */
    @Select("""
        SELECT DISTINCT
            t7.perms,
            t7.descr
        FROM m_staff_org t1
        INNER JOIN m_role_position t2 ON t1.serial_id = t2.position_id
        INNER JOIN s_role t3 ON t3.id = t2.role_id AND t3.is_del = 0
        INNER JOIN m_permission_role t4 ON t2.role_id = t4.role_id
        INNER JOIN m_permission t5 ON t5.id = t4.permission_id AND t5.is_del = 0
        INNER JOIN m_permission_pages t6 ON t6.permission_id = t5.id
        INNER JOIN m_permission_operation t7 ON t7.permission_page_id = t6.id
        WHERE t1.staff_id = #{staffId}
            AND t1.serial_type = 'm_position'
            AND t6.`code` = #{pageCode}
        ORDER BY t7.perms
        """)
    List<Map<String, Object>> findButtonPermissions(@Param("staffId") Long staffId,
                                                     @Param("pageCode") String pageCode);
}
