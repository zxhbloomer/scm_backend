package com.xinyirun.scm.core.system.mapper.business.alarm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.alarm.BAlarmRulesGroupEntity;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmStaffTransferVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Repository
public interface BAlarmRulesGroupMapper extends BaseMapper<BAlarmRulesGroupEntity> {

    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "             t1.id AS `key`,                                                                             "
            + "             t1.NAME AS label                                                                            "
            + "       FROM  m_staff t1                                                                                  "
            + "      WHERE                                                                                              "
            + "             t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+"                                        "
            + "   order by  t1.id                                                                                       "
    )
    List<BAlarmStaffTransferVo> getAllStaffTransferList();


    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.staff_id                                                                                          "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_rules_group t                                                                               "
            +  "  WHERE t.alarm_id = #{p1}                                                                              "
            +  "  AND t.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1+"'                                                                              "
            + "   order by t.staff_id                                                                                   "
    )
    List<Long> getUsedStaffTransferList(@Param("p1") Integer alarmId);


    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "             t1.id AS `key`,                                                                             "
            + "             t1.name AS label                                                                            "
            + "       FROM  b_alarm_group t1                                                                            "
//            + "      WHERE                                                                                              "
//            + "             t1.is_del = "+ DictConstant.DICT_SYS_DELETE_MAP_NO+"                                        "
            + "   order by  t1.id                                                                                       "
    )
    List<BAlarmStaffTransferVo> getAllGroupTransferList();

    @Select(""
            +  "  SELECT                                                                                                "
            +  "    t.alarm_group_id                                                                                    "
            +  "  FROM                                                                                                  "
            +  "    b_alarm_rules_group t                                                                               "
            +  "  WHERE t.alarm_id = #{p1}                                                                              "
            +  "  AND t.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2+"'                                                                              "
            + "   order by t.staff_id                                                                                   "
    )
    List<Long> getUsedGroupTransferList(@Param("p1") Integer alarmId);
}
