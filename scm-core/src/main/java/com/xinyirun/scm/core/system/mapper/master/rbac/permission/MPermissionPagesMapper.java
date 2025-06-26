package com.xinyirun.scm.core.system.mapper.master.rbac.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionPagesEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 权限页面表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Repository
public interface MPermissionPagesMapper extends BaseMapper<MPermissionPagesEntity> {

    /**
     * 表复制，s_pages->m_permission_pages
     * @param entity
     * @return
     */
    @Insert("                                                            "
            + "  INSERT INTO m_permission_pages (                            "
            + "   	         permission_id,                                  "
            + "   	         permission_menu_id,                                        "
            + "   	         page_id,                                        "
            + "   	         `code`,                                         "
            + "   	         `name`,                                         "
            + "   	         component,                                      "
            + "   	         perms,                                          "
            + "   	         meta_title,                                     "
            + "   	         meta_icon,                                      "
            + "   	         descr,                                          "
//        + "   	         tenant_id,                                      "
            + "   	         c_id,                                           "
            + "   	         c_time,                                         "
            + "   	         u_id,                                           "
            + "   	         u_time,                                         "
            + "   	         dbversion                                       "
            + "      )                                                       "
            + "       SELECT                                                 "
            + "              #{p1.permission_id,jdbcType=BIGINT},            "
            + "              t2.id,                                          "
            + "              t1.id,                                          "
            + "              t1.`code`,                                      "
            + "              t1.`name`,                                      "
            + "              t1.component,                                   "
            + "              t1.perms,                                       "
            + "              t1.meta_title,                                  "
            + "              t1.meta_icon,                                   "
            + "              t1.descr,                                       "
//        + "              #{p1.tenant_id,jdbcType=BIGINT},                "
            + "              #{p1.c_id,jdbcType=BIGINT},                     "
            + "              #{p1.c_time,jdbcType=TIMESTAMP},                "
            + "              #{p1.u_id,jdbcType=BIGINT},                     "
            + "              #{p1.u_time,jdbcType=TIMESTAMP},                "
            + "              #{p1.dbversion,jdbcType=INTEGER}                "
            + "         FROM s_pages AS t1                                   "
            + "         JOIN m_permission_menu t2 ON t1.id = t2.page_id       "
            + "        WHERE t2.root_id = #{p2}                              "
            + "          AND t2.page_id IS NOT NULL                          "
            + "              ")
    int copySPages2MPermissionPages(@Param("p1") MPermissionPagesEntity entity, @Param("p2") Long root_id);

    /**
     * 根据permission_id删除数据
     */
    @Delete("                                                                                                           "
            + "   delete                                                                                                "
            + "          FROM m_permission_pages t1                                                                     "
            + "   where                                                                                                 "
            + "    t1.permission_id = #{p1}                                                                             "
            + "                                                                          ")
    void deleteByPermissionId(@Param("p1") Long permission_id);
}
