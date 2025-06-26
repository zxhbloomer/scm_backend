package com.xinyirun.scm.core.system.mapper.business.alarm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmStaffEntity;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-16
 */
@Repository
public interface BAlarmStaffMapper extends BaseMapper<BAlarmStaffEntity> {

    /**
     * 根据 员工 查询
     * @param staff_id 员工 ID
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.staff_id,                                                                                        "
            +  "  	t.staff_name,                                                                                      "
            +  "  	t.login_name,                                                                                      "
            +  "  	t.id,                                                                                              "
            +  "  	t.`code`                                                                                           "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_staff t                                                                               "
            +  "  WHERE t.staff_id = #{p1}                                                                             "
    )
    List<BAlarmStaffVo> selectByStaff(@Param("p1") Integer staff_id);

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.staff_id,                                                                                         "
            +  "  	t.staff_name,                                                                                       "
            +  "  	t.login_name,                                                                                       "
            +  "  	t.id,                                                                                               "
            +  "  	t.`code`,                                                                                           "
            +  "  	t.c_time,                                                                                           "
            +  "  	t.u_time,                                                                                           "
            +  "  	t1.name c_name,                                                                                     "
            +  "  	t2.name u_name,                                                                                     "
            +  "  	JSON_ARRAYAGG(JSON_OBJECT('name', t4.name, 'id', t4.id)) as group_name_list                         "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_staff t                                                                                     "
            +  "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            +  "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            +  "  LEFT JOIN b_alarm_group_staff t3 ON t.id = t3.alarm_staff_id                                          "
            +  "  LEFT JOIN b_alarm_group t4 ON t3.alarm_group_id = t4.id                                               "
            +  "  WHERE t.id = #{p1}                                                                             "
    )
    @Results({
            @Result(property = "group_name_list", column = "group_name_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BAlarmStaffVo selectVoById(Integer id);

    /**
     * 分页查询
     * @param page
     * @param vo
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.staff_id,                                                                                         "
            +  "  	t.staff_name,                                                                                       "
            +  "  	t.login_name,                                                                                       "
            +  "  	t.id,                                                                                               "
            +  "  	t.`code`,                                                                                           "
            +  "  	t.c_time,                                                                                           "
            +  "  	t.u_time,                                                                                           "
            +  "  	t1.name c_name,                                                                                     "
            +  "  	t2.name u_name,                                                                                     "
            +  "  	JSON_ARRAYAGG(JSON_OBJECT('name', t4.name, 'id', t4.id)) as group_name_list                         "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_staff t                                                                                     "
            +  "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            +  "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            +  "  LEFT JOIN b_alarm_group_staff t3 ON t.id = t3.alarm_staff_id                                          "
            +  "  LEFT JOIN b_alarm_group t4 ON t3.alarm_group_id = t4.id                                               "
            +  "  WHERE TRUE                                                                                            "
            +  "  AND (CONCAT(ifnull(t.staff_name, ''), '_', ifnull(t.login_name, ''), '_', t.code) like concat('%', #{p1.staff_name}, '%') "
            +  "  or #{p1.staff_name} is null or #{p1.staff_name} = '')                                                 "
            + "   and EXISTS(                                                                                           "
            + "     SELECT 1 FROM b_alarm_group_staff t5                                                                "
            + "     LEFT JOIN b_alarm_group t6 ON t5.alarm_group_id = t6.id                                             "
            + "     where TRUE                                                                                          "
            + "     and (concat(ifnull(t6.name, ''), '_', ifnull(t6.short_name, ''),'_', ifnull(t6.code, '')) like concat('%', #{p1.group_name}, '%') or #{p1.group_name} is null or #{p1.group_name} = '')"
            + "     and (t5.alarm_staff_id = t.id or #{p1.group_name} is null or #{p1.group_name} = ''))                                                                              "
            +  "  group by t.id                                                                                         "
    )
    @Results({
            @Result(property = "group_name_list", column = "group_name_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BAlarmStaffVo> selectPageList(Page<BAlarmStaffVo> page,@Param("p1") BAlarmStaffVo vo);
}
