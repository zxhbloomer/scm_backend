package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.org.MStaffOrgEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffOrgVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户组织机构关系表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MStaffOrgMapper extends BaseMapper<MStaffOrgEntity> {

    /**
     * 获取员工组织关系详细信息
     * @param staffId 员工ID
     * @return 员工组织关系列表
     */
    @Select("""
        SELECT                                                                                               
               t5.id,                              
               t5.staff_id,                        
               t6.code as staff_code,              
               t6.name as staff_name,              
               t1.id as position_id,               
               t1.code as position_code,           
               t1.name as position_name,           
               t5.serial_id,                       
               t5.serial_type,                     
               f_get_org_simple_name(t8.code, 'm_group') as group_name,
               f_get_org_simple_name(t8.code, 'm_company') as company_name,
               f_get_org_simple_name(t8.code, 'm_dept') as dept_name,
               t1.c_id,
               t1.c_time,
               t1.u_id,
               t1.u_time,
               t1.dbversion                                                                
          FROM m_position t1                                                                                 
        INNER JOIN m_staff_org t5 ON t5.serial_id = t1.id 
           AND t5.serial_type = 'm_position'
        LEFT JOIN m_staff t6 ON t5.staff_id = t6.id
        LEFT JOIN v_org_relation t8 ON t8.serial_type = 'm_position' and t8.serial_id = t1.id
        WHERE t5.staff_id = #{staffId}
          AND (t6.is_del = false OR t6.is_del IS NULL)
        ORDER BY t1.id
    """)
    List<MStaffOrgVo> getStaffOrgRelation(@Param("staffId") Long staffId);

    /**
     * 删除员工指定类型的组织关系
     * @param staffId 员工ID
     * @param serialType 关系类型
     * @return 删除记录数
     */
    @Delete("DELETE FROM m_staff_org WHERE staff_id = #{staffId} AND serial_type = #{serialType}")
    int deleteByStaffIdAndSerialType(@Param("staffId") Long staffId, @Param("serialType") String serialType);

    /**
     * 删除员工与指定组织的关系
     * @param staffId 员工ID
     * @param serialId 组织ID
     * @param serialType 关系类型
     * @return 删除记录数
     */
    @Delete("DELETE FROM m_staff_org WHERE staff_id = #{staffId} AND serial_id = #{serialId} AND serial_type = #{serialType}")
    int deleteByStaffIdSerialIdAndType(@Param("staffId") Long staffId, @Param("serialId") Long serialId, @Param("serialType") String serialType);

    /**
     * 删除员工的所有组织关系
     * @param staffId 员工ID
     * @return 删除记录数
     */
    @Delete("DELETE FROM m_staff_org WHERE staff_id = #{staffId}")
    int deleteAllByStaffId(@Param("staffId") Long staffId);
}
