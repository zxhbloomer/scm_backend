package com.xinyirun.scm.core.whapp.mapper.master.contact_list;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorFileVo;
import com.xinyirun.scm.bean.whapp.vo.master.contact_list.WhAppContractListVo;
import com.xinyirun.scm.bean.whapp.vo.master.contact_list.WhAppMStaffPositionsVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.BMonitorFileVoTypeHandler;
import com.xinyirun.scm.core.whapp.config.mybatis.typehandlers.ContactListVoTypeHandler;
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
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface WhAppContactListMapper extends BaseMapper<MStaffEntity> {

    /**
     * 查询用户数据
     */
    @Select("    "
            + "       SELECT                                                                                             "
            + "       	t1.id as staff_id,                                                                              "
            + "       	t1.name as staff_name,                                                                          "
            + "       	t2.avatar,                                                                                      "
            + "       	t2.id as user_id,                                                                               "
            + "       	t1.mobile_phone ,                                                                               "
            + "       	t3.positions,                                                                                   "
            + "         t3.positions_name,                                                                             "
            + "         t4.name as company_name                                                                         "
            + "       FROM                                                                                               "
            + "       	m_staff t1                                                                                      "
            + "       	LEFT JOIN m_user t2 ON t1.user_id = t2.id                                                       "
            + "       	LEFT JOIN (                                                                                     "
            + "       		SELECT                                                                                       "
            + "       			subt1.staff_id,                                                                          "
            + "       			JSON_ARRAYAGG(JSON_OBJECT(                                                               "
            + "       					     		'position_id', subt2.id,                                              "
            + "       					     		'position_name', subt2.NAME,                                         "
            + "       					     		'position_simple_name', subt2.simple_name,                           "
            + "       					     		'staff_id', subt1.staff_id                                           "
            + "       					     		)) as positions,                                                    "
            + "       		     GROUP_CONCAT(subt2.NAME SEPARATOR '、')  as positions_name                              "
            + "       		FROM                                                                                        "
            + "       			m_staff_org subt1                                                                        "
            + "       			INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position'                          "
            + "       			AND subt1.serial_id = subt2.id                                                           "
            + "       		GROUP BY subt1.staff_id                                                                     "
            + "       	) t3 ON t3.staff_id = t1.id                                                                    "
            + "       LEFT JOIN m_company t4 on t4.id = t1.company_id                                                   "
            + "       WHERE                                                                                             "
            + "       	    t1.is_del = FALSE                                                                              "
            + "       	AND (CONCAT(IFNULL(t1.name, ''), IFNULL(t1.name_py, '')) like concat('%', #{p1.search_str}, '%')  or #{p1.search_str} is null)       "
            + "       ORDER BY staff_name                                                                               "
            + "      ")

    @Results({
            @Result(property = "positions", column = "positions", javaType =  List.class, typeHandler = ContactListVoTypeHandler.class),
    })
    List<WhAppContractListVo> list(@Param("p1") WhAppContractListVo searchCondition);

    /**
     * 查询用户数据
     */
    @Select("    "
            + "       SELECT                                                                                             "
            + "       	t1.id as staff_id,                                                                              "
            + "       	t1.name as staff_name,                                                                          "
            + "       	t2.avatar,                                                                                      "
            + "       	t2.id as user_id,                                                                               "
            + "       	t1.mobile_phone ,                                                                               "
            + "       	t3.positions,                                                                                   "
            + "         t3.positions_name,                                                                             "
            + "         t4.name as company_name                                                                         "
            + "       FROM                                                                                               "
            + "       	m_staff t1                                                                                      "
            + "       	LEFT JOIN m_user t2 ON t1.user_id = t2.id                                                       "
            + "       	LEFT JOIN (                                                                                     "
            + "       		SELECT                                                                                       "
            + "       			subt1.staff_id,                                                                          "
            + "       			JSON_ARRAYAGG(JSON_OBJECT(                                                               "
            + "       					     		'position_id', subt2.id,                                              "
            + "       					     		'position_name', subt2.NAME,                                         "
            + "       					     		'position_simple_name', subt2.simple_name,                           "
            + "       					     		'staff_id', subt1.staff_id                                           "
            + "       					     		)) as positions,                                                    "
            + "       		     GROUP_CONCAT(subt2.NAME SEPARATOR '、')  as positions_name                              "
            + "       		FROM                                                                                        "
            + "       			m_staff_org subt1                                                                        "
            + "       			INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position'                          "
            + "       			AND subt1.serial_id = subt2.id                                                           "
            + "       		GROUP BY subt1.staff_id                                                                     "
            + "       	) t3 ON t3.staff_id = t1.id                                                                    "
            + "       LEFT JOIN m_company t4 on t4.id = t1.company_id                                                   "
            + "       WHERE                                                                                             "
            + "       	t1.id = #{p1.staff_id,jdbcType=BIGINT}                                                                             "
            + "      ")

    @Results({
            @Result(property = "positions", column = "positions", javaType =  List.class, typeHandler = ContactListVoTypeHandler.class),
    })
    WhAppContractListVo get(@Param("p1") WhAppContractListVo searchCondition);
}
