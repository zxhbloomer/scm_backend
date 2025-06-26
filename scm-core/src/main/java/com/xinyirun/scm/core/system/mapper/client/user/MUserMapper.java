package com.xinyirun.scm.core.system.mapper.client.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  用户表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-06-24
 */
@Repository
public interface MUserMapper extends BaseMapper<MUserEntity> {

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                      "
        + "   select t1.*,                                          "
        + "  			 t2.`code` as staff_code                    "
        + "     from m_user t1                                     "
        + "  	 inner join m_staff t2 on t1.staff_id = t2.id       "
        + "    where t1.login_name = #{p1}                         "
//        + "       and t.is_enable = true                       "
//        + "       and t.is_del = false                         "
        + "                                                    ")
    MUserVo getDataByName(@Param("p1") String p1);

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                             "
            + "      select t1.*,                                           "
            + "     			 t2.`code` as staff_code                     "
            + "        from m_user t1                                       "
            + "     	 inner join m_staff t2 on t1.staff_id = t2.id        "
            + "     where MD5(t.login_name) = #{p1}                          "
//        + "       and t.is_enable = true                       "
//        + "       and t.is_del = false                         "
            + "                                                    ")
    MUserVo getDataByMd5Name(@Param("p1") String p1);

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                 "
            + "    select t.*                                      "
            + "      from m_user t                                 "
            + "     where t.id = #{p1}                     "
//        + "       and t.is_enable = true                       "
//        + "       and t.is_del = false                         "
            + "                                                    ")
    MUserVo getDataById(@Param("p1") Long p1);

    /**
     *
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from m_user t                                 "
            + "     where t.staff_id = #{p1}                     "
//        + "       and t.is_enable = true                       "
//        + "       and t.is_del = false                         "
            + "                                                    ")
    MUserEntity getDataByStaffId(@Param("p1") Long staff_id);


    /**
     * 页面查询列表
     * @return
     */
    @Select("                                                                                                                    "
        + "      SELECT                                                                                                          "
        + "              t1.* ,                                                                                                  "
        + "            	 t2.label as type_text                                                                                   "
        + "        FROM                                                                                                          "
        + "              m_user t1                                                                                               "
        + "   left join  v_dict_info t2 on t2.code = 'usr_login_type' and t1.type = t2.dict_value                                "
        + "       where  true                                                                                                    "
        + "         and  (t1.id = #{p1})                                                                                         "
//        + "         and (t1.tenant_id  = #{p2} or #{p2} is null)                                                                "
        + "                                                                                                                      ")
    MUserVo selectUserById(@Param("p1") Long id );

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("                                                                                                    "
        + " select t.*                                                                                           "
        + "   from m_user t                                                                                      "
        + "  where true                                                                                          "
        + "    and t.login_name =  #{p1}                                                                         "
        + "    and (t.id  <>  #{p2} or #{p2} is null)                                                             "
        + "                                                                                                      ")
    List<MUserEntity> selectLoginName(@Param("p1") String login_name, @Param("p2") Long equal_id);

    /**
     * 查询员工岗位列表
     */
    @Select(" "
            + "	SELECT                                                                                                  "
            + "		t2.id serial_id,                                                                                    "
            + " 	'm_position' serial_type,                                                                           "
            + " 	t2.code serial_code,                                                                                "
            + "		concat(t2.name,'(岗位)') label                                                                       "
            + "	FROM                                                                                                    "
            + "		m_staff_org t1                                                                                      "
            + "		INNER JOIN m_position t2 ON t1.serial_id = t2.id                                                    "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'                             "
            + "		AND t1.staff_id = #{p1}                                                                             "
            + " ")
    List<TreeDataVo> selectStaffPositionList(@Param("p1") Long staff_id);

    /**
     * 查询员工岗位列表
     */
    @Select(" "
            + "	SELECT                                                                                                  "
            + "		t2.id serial_id,                                                                                    "
            + " 	's_role' serial_type,                                                                               "
            + " 	t2.code serial_code,                                                                                "
            + "		concat(t2.name,'(角色)') label                                                                       "
            + "	FROM                                                                                                    "
            + "		m_role_position t1                                                                                  "
            + "		INNER JOIN s_role t2 ON t1.role_id = t2.id                                                          "
            + "	WHERE                                                                                                   "
            + "	TRUE                                                                                                    "
            + "		AND t1.position_id = #{p1}                                                                          "
            + " ")
    List<TreeDataVo> selectPositionRoleList(@Param("p1") Long position_id);

    /**
     * 查询员工岗位列表
     */
    @Select(" "
           + " SELECT                                                                                                   "
           + " 	t2.id serial_id,                                                                                        "
           + " 	'm_permission' serial_type,                                                                             "
           + " 	'' serial_code,                                                                                         "
           + " 	concat(t2.name,'(权限)') label                                                                           "
           + " FROM                                                                                                     "
           + " 	m_permission_role t1                                                                                    "
           + " 	INNER JOIN m_permission t2 ON t1.permission_id = t2.id                                                  "
           + " WHERE                                                                                                    "
           + " TRUE                                                                                                     "
           + " 	AND t1.role_id = #{p1}                                                                                  "
            + " ")
    List<TreeDataVo> selectRolePermissionList(@Param("p1") Long role_id);

    /**
     * 查询员工仓库组列表
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
            + " 	AND t1.staff_id = #{p1}                                                                             "
            + " ")
    List<TreeDataVo> selectWarehouseGroupList(@Param("p1") Long staff_id);

    /**
     * 查询员工仓库组仓库列表
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
     * 查询员工仓库列表
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
            + "		AND t1.staff_id = #{p1}                                                                             "
            + " ")
    List<TreeDataVo> selectWarehouseListByStaffId(@Param("p1") Long staff_id);

    /**
     * 更新登录时间
     * @param id
     * @param now
     */
    @Update(""
            + "UPDATE m_user SET last_login_date = #{p2} WHERE id = #{p1}"
    )
    void updateLoginDate(@Param("p1") Long id, @Param("p2") LocalDateTime now);

    @Update(""
            + "UPDATE m_user SET last_logout_date = #{p2} WHERE id = #{p1}"
    )
    void updateLastLogoutDate(@Param("p1") Long id, @Param("p2") LocalDateTime now);

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from m_user t                                 "
            + "     where t.wx_unionid = #{p1}                     "
            + "       and t.is_enable = true                       "
            + "       and t.is_del = false                         "
            + "                                                    ")
    MUserEntity getDataByWxUnionid(@Param("p1") String p1);
}
