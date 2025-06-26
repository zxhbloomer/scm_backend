package com.xinyirun.scm.core.system.mapper.master.rbac.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionOperationEntity;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.MPermissionOperationVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 权限页面操作表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-08-07
 */
@Repository
public interface MPermissionOperationMapper extends BaseMapper<MPermissionOperationEntity> {

    /**
     * 表复制，s_pages_function->m_permission_operation
     * @param entity
     * @return
     */
    @Insert("                                                            "
            + "  INSERT INTO m_permission_operation (                        "
            + "   	  	     permission_id,                                  "
            + "   	  	     operation_id,                                   "
            + "   	  	     page_id,                                        "
            + "   	  	     permission_page_id,                                        "
            + "   	  	     is_enable,                                      "
            + "   	  	     type,                                           "
            + "   	  	     function_id,                                    "
            + "   	  	     sort,                                           "
            + "   	  	     perms,                                          "
            + "   	  	     descr,                                          "
//        + "   	  	     tenant_id,                                      "
            + "   	  	     c_id,                                           "
            + "   	  	     c_time,                                         "
            + "   	  	     u_id,                                           "
            + "   	  	     u_time,                                         "
            + "   	  	     dbversion                                       "
            + "      )                                                       "
            + "       SELECT                                                 "
            + "              #{p1.permission_id,jdbcType=BIGINT},            "
            + "              t1.id,                                          "
            + "              t1.page_id,                                     "
            + "              t3.id,                                          "
            + "              #{p1.is_enable,jdbcType=BOOLEAN},               "
            + "              t1.type,                                        "
            + "              t1.function_id,                                 "
            + "              t1.sort,                                        "
            + "              t1.perms,                                       "
            + "              t1.descr,                                       "
//        + "              #{p1.tenant_id,jdbcType=BIGINT},                "
            + "              #{p1.c_id,jdbcType=BIGINT},                     "
            + "              #{p1.c_time,jdbcType=TIMESTAMP},                "
            + "              #{p1.u_id,jdbcType=BIGINT},                     "
            + "              #{p1.u_time,jdbcType=TIMESTAMP},                "
            + "              #{p1.dbversion,jdbcType=INTEGER}                "
            + "         FROM s_pages_function AS t1                          "
            + "         JOIN m_permission_menu t2 ON t1.page_id = t2.page_id            "
            + "         join m_permission_pages t3 on t2.id = t3.permission_menu_id            "
            + "        WHERE t2.root_id = #{p2}                              "
            + "          AND t2.page_id IS NOT NULL                          "
            + "              ")
    int copyMPermissionOperation2MPermissionOperation(@Param("p1") MPermissionOperationEntity entity, @Param("p2") Long root_id);

    /**
     * 根据permission_id删除数据
     */
    @Delete("                                                                                                           "
            + "   delete                                                                                                "
            + "          FROM m_permission_operation t1                                                                 "
            + "   where                                                                                                 "
            + "    t1.permission_id = #{p1}                                                                             "
            + "                                                                          ")
    void deleteByPermissionId(@Param("p1") Long permission_id);

    /**
     * 根据permission_id查询数据
     */
    @Select("                                                                                                           "
            + "   select *                                                                                                "
            + "          FROM m_permission_operation t1                                                                 "
            + "   where                                                                                                 "
            + "   t1.is_enable = true                                                                                   "
            + "    and t1.permission_id = #{p1}                                                                         "
            + "                                                                          ")
    List<MPermissionOperationVo> selectByPermissionId(@Param("p1") Long permission_id);


    @Update("   <script>   "
            + " update                                                                                                  "
            + "   m_permission_operation t                                                                              "
            + "  set t.is_enable = true                                                                                             "
            + "  where true                                                                                             "
            + "    and t.perms in                                                                                       "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item.perms}                                                                           "
            + "        </foreach>                                                                                       "
            + "        and t.permission_id = #{p2,jdbcType=BIGINT}                                                      "
            + "  </script>    ")
    void updatePermissionOperation(@Param("p1") List<MPermissionOperationVo> searchCondition, @Param("p2") Long permission_id);
}
