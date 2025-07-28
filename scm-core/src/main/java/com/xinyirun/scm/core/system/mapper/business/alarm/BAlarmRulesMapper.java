package com.xinyirun.scm.core.system.mapper.business.alarm;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.alarm.BAlarmRulesEntity;
import com.xinyirun.scm.bean.system.bo.business.alarm.BAlarmRulesBo;
import com.xinyirun.scm.bean.system.vo.business.alarm.BAlarmRulesVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 预警规则清单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-15
 */
@Repository
public interface BAlarmRulesMapper extends BaseMapper<BAlarmRulesEntity> {

    String comm_select = ""
            + " SELECT                                                                                                  "
            + "   t.name,                                                                                               "
            + "   t.id,                                                                                                 "
            + "   t.type,                                                                                               "
            + "   t1.label type_name,                                                                                   "
            + "   t.rule_type,                                                                                          "
            + "   t2.label rule_type_name,                                                                              "
            + "   t3.job_name,                                                                                          "
            + "   t.notice_time,                                                                                        "
            + "   t.notice_type,                                                                                        "
            + "   t4.label notice_type_name,                                                                            "
            + "   t.is_using,                                                                                           "
            + "   t.notice_plan,                                                                                        "
            + "   t.c_time,                                                                                             "
            + "   t.u_time,                                                                                             "
            + "   t5.name c_name,                                                                                       "
            + "   t6.name u_name,                                                                                       "
            + "   t11.group_list,                                                                                       "
            + "   t12.staff_list                                                                                        "
            + " FROM b_alarm_rules t                                                                                    "
            + " LEFT JOIN s_dict_data t1 ON t1.code = '"+ DictConstant.DICT_B_ALARM_RULES_TYPE +"' AND t.type = t1.dict_value"
            + " LEFT JOIN s_dict_data t2 ON t2.code = '"+ DictConstant.DICT_B_ALARM_SETTING_RULES +"' AND t.rule_type = t2.dict_value"
            + " LEFT JOIN s_job t3 ON t3.id = t.job_id                                                                  "
            + " LEFT JOIN s_dict_data t4 ON t4.code = '"+ DictConstant.DICT_B_ALARM_RULES_NOTICE_TYPE +"' AND t.notice_type = t4.dict_value"
            + " LEFT JOIN m_staff t5 ON t.c_id = t5.id                                                                  "
            + " LEFT JOIN m_staff t6 ON t.u_id = t6.id                                                                  "
            + " LEFT JOIN (                                                                                             "
            + "   SELECT                                                                                                "
            + "     t7.alarm_id,                                                                                        "
            + "     JSON_ARRAYAGG(JSON_OBJECT('name', t8.name, 'id', t8.id)) as group_list                              "
            + "   FROM                                                                                                  "
            + "     b_alarm_rules_group t7                                                                              "
            + " LEFT JOIN b_alarm_group t8 ON t7.alarm_group_id = t8.id                                                 "
            + " WHERE t7.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2+"'                                      "
            + " GROUP BY t7.alarm_id) t11 ON t11.alarm_id = t.id                                                        "
            + " LEFT JOIN (                                                                                             "
            + "   SELECT                                                                                                "
            + "     t9.alarm_id,                                                                                        "
            + "     JSON_ARRAYAGG(JSON_OBJECT('name', t10.name, 'id', t10.id)) as staff_list                            "
            + "   FROM                                                                                                  "
            + "     b_alarm_rules_group t9                                                                              "
            + " LEFT JOIN m_staff t10 ON t10.id = t9.staff_id                                                           "
            + " WHERE t9.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1+"'                                      "
            + " GROUP BY t9.alarm_id) t12 ON t12.alarm_id = t.id                                                        "
            + " WHERE TRUE                                                                                              "
            ;

    /**
     * 查询预警规则和员工
     * @param type
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "  	t3.notice_type,                                                                                     "
            +  "  	t3.is_using   ,                                                                                     "
            +  "  	t3.staff_id                                                                                         "
            +  "  FROM                                                                                                  "
            +  "  	(                                                                                                   "
            +  "  	SELECT                                                                                              "
            +  "  		t.notice_type,                                                                                  "
            +  "  		t.is_using,                                                                                    "
            +  "  		t1.staff_id                                                                                     "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules t                                                                                 "
            +  "  		LEFT JOIN b_alarm_rules_group t1 ON t.id = t1.alarm_id                                          "
            +  "  		WHERE t1.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1+"'                              "
            +  "        AND t.type = '" + DictConstant.DICT_B_ALARM_RULES_TYPE_0 + "'                                   "
            +  "    UNION                                                                                               "
            +  "  	SELECT                                                                                              "
            +  "  		t.notice_type,                                                                                  "
            +  "  		t.is_using,                                                                                     "
            +  "  		t2.staff_id                                                                                     "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules t                                                                                 "
            +  "  		LEFT JOIN b_alarm_rules_group t1 ON t.id = t1.alarm_id                                          "
            +  "  		LEFT JOIN b_alarm_group_staff t2 ON t1.alarm_group_id = t2.alarm_group_id                       "
            +  "  		WHERE t1.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2+"'                              "
            +  "        AND t.type = '" + DictConstant.DICT_B_ALARM_RULES_TYPE_0 + "'                                   "
            +  "  	) t3                                                                                                "
            +  "  GROUP BY                                                                                              "
            +  "  	t3.staff_id                                                                                         "
    )
    List<BAlarmRulesBo> selectAlarmRulesByType(String type);

    @Select(""
            + comm_select
            + " AND (t.name LIKE CONCAT('%', #{p1.name}, '%') OR #{p1.name} IS NULL OR #{p1.name} = '')                 "
            + " AND (t.rule_type = #{p1.rule_type} OR #{p1.rule_type} IS NULL OR #{p1.rule_type} = '')                  "
            + " AND (t.notice_type = #{p1.notice_type} OR #{p1.notice_type} IS NULL OR #{p1.notice_type} = '')          "
            + " AND (t.type = #{p1.type} OR #{p1.type} IS NULL OR #{p1.type} = '')                                      "
            + " AND EXISTS (                                                                                            "
            + "     SELECT 1 FROM b_alarm_rules_group tt1                                                               "
            + "     LEFT JOIN b_alarm_group tt2 ON tt1.alarm_group_id = tt2.id                                          "
            + "     WHERE tt1.type = '"+DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_2+"'                                 "
            + "     AND (tt2.name LIKE CONCAT('%', #{p1.group_name}, '%') OR #{p1.group_name} IS NULL OR #{p1.group_name} = '')"
            + "     AND (tt1.alarm_id = t.id OR #{p1.group_name} IS NULL OR #{p1.group_name} = '')                      "
            + " )"
            + " GROUP BY t.id                                                                                           "
    )
    @Results({
            @Result(property = "group_list", column = "group_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "staff_list", column = "staff_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BAlarmRulesVo> selectPageList(Page<BAlarmRulesVo> page,@Param("p1") BAlarmRulesVo param);


    @Select(""
            + comm_select
            + " AND (t.id = #{p1} OR #{p1} IS NULL)                                                                     "
            + " GROUP BY t.id                                                                                           "
    )
    @Results({
            @Result(property = "group_list", column = "group_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "staff_list", column = "staff_list", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BAlarmRulesVo selectVoById(@Param("p1") Integer id);

}
