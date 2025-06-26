package com.xinyirun.scm.core.system.mapper.master.rbac.permission.dept;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation.OperationMenuDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.OperationFunctionInfoVoTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
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
public interface MPermissionDeptOperationMapper extends BaseMapper<MPermissionEntity> {

    String commonTreeGrid = "    "
        + "      select                                                                                            "
        + "	            t2.id,                                                                                     "
        + "	            t2.is_enable,                                                                              "
        + "	            t2.is_default,                                                                             "
        + "	            t1.menu_id,                                                                                "
        + "			    t1.menu_id as `value`,                                                                     "
        + "			    t1.name,                                                                                   "
        + "			    t1.name as label,                                                                          "
        + "             t1.parent_id,                                                                              "
        + "             t2.root_id,                                                                                "
        + "             t1.level,                                                                                  "
        + "             t1.depth_name,                                                                             "
        + "             t1.depth_id,                                                                               "
        + "             t4.depth_id as parent_depth_id,                                                            "
        + "             t2.code,                                                                                   "
        + "             t2.is_default,                                                                             "
        + "             t2.type,                                                                                   "
        + "             t3.label as type_name,                                                                     "
        + "             t2.visible,                                                                                "
        + "             t2.perms,                                                                                  "
        + "             t2.page_id,                                                                                "
        + "             t2.page_code,                                                                              "
        + "             t2.path,                                                                                   "
        + "             t2.route_name,                                                                             "
        + "             t2.meta_title,                                                                             "
        + "             t2.meta_icon,                                                                              "
        + "             t2.component,                                                                              "
        + "             t2.affix,                                                                                  "
        + "             t2.descr,                                                                                  "
//        + "             t2.tenant_id,                                                                              "
        + "             t2.c_id,                                                                                   "
        + "             t2.c_time,                                                                                 "
        + "             t2.u_id,                                                                                   "
        + "             t2.u_time,                                                                                 "
        + "             t2.dbversion,                                                                              "
        + "             t5.function_info,                                                                          "
        + "       (case when t5.function_count = t5.function_enable_count then true else false end) as check_all,  "
        + "             (CASE WHEN t5.function_count = t5.function_enable_count THEN false                         "
        + "                   WHEN (t5.function_count <> t5.function_enable_count)                                 "
        + "                        and (t5.function_enable_count > 0) THEN true                                    "
        + "                   ELSE FALSE END ) AS indeterminate,                                                   "
        + "             c_staff.name as c_name,                                                                    "
        + "             u_staff.name as u_name                                                                     "
        + "         from v_permission_tree t1                                                                      "
        + "   inner join m_permission_menu t2                                                                      "
        + "		      on t1.menu_id = t2.menu_id                                                                   "
//        + "          and t1.tenant_id = t2.tenant_id                                                               "
        + "          and t1.permission_id = t2.permission_id                                                       "
        + "    left join v_dict_info t3                                                                            "
        + "		      on t3.code = '" + DictConstant.DICT_SYS_MENU_TYPE + "' and t3.dict_value = t2.type    "
        + "	   LEFT join v_permission_tree t4 on t4.menu_id = t1.parent_id                                         "
//        + "          and t4.tenant_id = t2.tenant_id                                                               "
        + "          and t4.permission_id = t2.permission_id                                                       "
        + "    LEFT JOIN v_permission_operation_info t5 on t5.permission_menu_id = t2.id                                 "
//        + "          and t5.tenant_id = t2.tenant_id                                                               "
        + "          and t5.permission_id = t2.permission_id                                                       "
        + "    LEFT JOIN m_staff c_staff                                                                           "
        + "           ON t2.c_id = c_staff.id                                                                      "
        + "    LEFT JOIN m_staff u_staff                                                                           "
        + "           ON t2.u_id = u_staff.id                                                                      "
        + "                                                                                            ";

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + commonTreeGrid
        + "  where true "
//        + "    AND (t2.tenant_id = #{p1.tenant_id,jdbcType=BIGINT})                                                   "
        + "    and (t2.visible =#{p1.visible,jdbcType=VARCHAR} or #{p1.visible,jdbcType=VARCHAR} is null)             "
        + "    and (t2.permission_id =#{p1.permission_id,jdbcType=BIGINT})                                            "
        + "  order by t2.code                                                                                         "
        + "      ")
    @Results({
        @Result(property = "function_info", column = "function_info", typeHandler = OperationFunctionInfoVoTypeHandler.class),
    })
    List<OperationMenuDataVo> select(@Param("p1") OperationMenuDataVo searchCondition);

}
