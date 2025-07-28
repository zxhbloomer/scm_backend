package com.xinyirun.scm.core.system.mapper.business.message;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.message.BMessageEntity;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.vo.business.message.BMessageVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * websocket 消息通知表 Mapper 接口
 *
 * @author xinyirun
 * @since 2023-03-22
 */
@Repository
public interface BMessageMapper extends BaseMapper<BMessageEntity> {

    @Select(""
//            + " SELECT                                                                                                  "
//            + "    t.id,                                                                                                "
//            + "    t.c_time,                                                                                            "
//            + "    t.serial_code,                                                                                       "
//            + "    t.serial_id,                                                                                         "
//            + "    t.serial_type,                                                                                       "
//            + "    t.msg,                                                                                               "
//            + "    t.label,                                                                                             "
//            + "    t.staff_id                                                                                           "
//            + " FROM b_message t                                                                                        "
//            + " LEFT JOIN b_alarm_rules t1 ON t1.id = t.alarm_rules_id                                                  "
//            + " LEFT JOIN (                                                                                             "
//            + "   SELECT                                                                                                "
//            + "   FROM b_alarm_rules_group                                                                              "
//            + " LEFT JOIN b_alarm_rules_group t2 ON t2.type = '" + DictConstant.DICT_B_ALARM_RULES_STAFF_TYPE_1 + "' and t2.alarm_id = t1.id "
//            + " LEFT JOIN b_alarm_rules_group_staff t3 ON t2.id = t3.alarm_group_id                                     "
//            + " group by "
//
//            + " WHERE t.staff_id = #{p1.staff_id}                                                                       "
            +  "  SELECT                                                                                                "
            +  "  	t.id,                                                                                               "
            +  "  	t.c_time,                                                                                           "
            +  "  	t.serial_code,                                                                                      "
            +  "  	t.serial_id,                                                                                        "
            +  "  	t.serial_type,                                                                                      "
            +  "  	t.msg,                                                                                              "
            +  "  	t.label,                                                                                            "
            +  "  	t2.staff_id                                                                                         "
            +  "  FROM                                                                                                  "
            +  "  	b_message t                                                                                         "
            +  "  	LEFT JOIN b_alarm_rules t1 ON t1.type = t.alarm_rules_type                                          "
            +  "  	LEFT JOIN (                                                                                         "
            +  "  	SELECT                                                                                              "
            +  "  		tt2.staff_id,                                                                                   "
            +  "  		tt1.id                                                                                          "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules tt1                                                                               "
            +  "  		LEFT JOIN b_alarm_rules_group tt2 ON tt2.type = '1'                                             "
            +  "  		AND tt1.id = tt2.alarm_id                                                                       "
            +  "  	WHERE                                                                                               "
            +  "  		tt1.type = 0                                                                                    "
            +  "  		UNION                                                                                           "
            +  "  	SELECT                                                                                              "
            +  "  		tt5.staff_id ,                                                                                  "
            +  "  		tt3.id                                                                                          "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules tt3                                                                               "
            +  "  		LEFT JOIN b_alarm_rules_group tt4 ON tt3.id = tt4.alarm_id                                      "
            +  "  		AND tt4.type = '2'                                                                              "
            +  "  		LEFT JOIN b_alarm_group_staff tt5 ON tt4.alarm_group_id = tt5.alarm_group_id                    "
            +  "  	WHERE                                                                                               "
            +  "  		tt3.type = 0                                                                                    "
            +  "  	) t2 ON t2.id = t1.id                                                                               "
            +  "  	WHERE t2.staff_id = #{p1.staff_id}                                                                  "
            +  "    AND t.serial_type != '"+ DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED +"'                    "
            +  "  	group by t.id                                                                                       "
    )
    IPage<BMessageVo> selectPageList(Page<BMonitorEntity> pageCondition,@Param("p1") BMessageVo param);

    @Select(""
            +  " SELECT                                                                                                 "
            +  "    count(t.id) count,                                                                                  "
            +  "    t.serial_type                                                                                       "
            +  " FROM b_message t                                                                                       "
            +  "  	LEFT JOIN b_alarm_rules t1 ON t1.type = t.alarm_rules_type                                          "
            +  "  	LEFT JOIN (                                                                                         "
            +  "  	SELECT                                                                                              "
            +  "  		tt2.staff_id,                                                                                   "
            +  "  		tt1.id                                                                                          "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules tt1                                                                               "
            +  "  		LEFT JOIN b_alarm_rules_group tt2 ON tt2.type = '1'                                             "
            +  "  		AND tt1.id = tt2.alarm_id                                                                       "
            +  "  	WHERE                                                                                               "
            +  "  		tt1.type = 0                                                                                    "
            +  "  		UNION                                                                                           "
            +  "  	SELECT                                                                                              "
            +  "  		tt5.staff_id ,                                                                                  "
            +  "  		tt3.id                                                                                          "
            +  "  	FROM                                                                                                "
            +  "  		b_alarm_rules tt3                                                                               "
            +  "  		LEFT JOIN b_alarm_rules_group tt4 ON tt3.id = tt4.alarm_id                                      "
            +  "  		AND tt4.type = '2'                                                                              "
            +  "  		LEFT JOIN b_alarm_group_staff tt5 ON tt4.alarm_group_id = tt5.alarm_group_id                    "
            +  "  	WHERE                                                                                               "
            +  "  		tt3.type = 0                                                                                    "
            +  "  	) t2 ON t2.id = t1.id                                                                               "
            +  "  	WHERE t2.staff_id = #{p1.staff_id}                                                                  "
            +  " AND t.type = '" + DictConstant.DICT_B_MESSAGE_TYPE_1 + "'                                              "
            +  " GROUP BY t.serial_type                                                                                 "
    )
    List<BMessageVo> selectCountBySerialType(@Param("p1") BMessageVo param);
}
