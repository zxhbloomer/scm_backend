package com.xinyirun.scm.core.system.mapper.sys.schedule.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SBMonitorAlarmV2Mapper extends BaseMapper<BMonitorEntity> {

    @Select("<script>"
            + " SELECT                                                                                                  "
            + "   t.id serial_id,                                                                                       "
            + "   t.status,                                                                                             "
            + "   t1.label status_name,                                                                                 "
            + "   t.code serial_code,                                                                                   "
            + "   t.out_empty_time,                                                                                     "
            + "   t.out_loading_time,                                                                                   "
            + "   t.out_heavy_time,                                                                                     "
            + "   t.in_heavy_time,                                                                                      "
            + "   t.in_unloading_time,                                                                                  "
            + "   t.in_empty_time                                                                                       "
            + " FROM b_monitor t                                                                                        "
            + " LEFT JOIN s_dict_data t1 ON t.status = t1.dict_value AND t1.code= '"+ DictConstant.DICT_B_MONITOR_STATUS+ "'"
            + " WHERE t.status IN                                                                                       "
            + " <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                    "
            + "    #{item}                                                                                              "
            + " </foreach>                                                                                              "
            + " AND t.id NOT IN (                                                                                       "
            + "   SELECT                                                                                                "
            + "     distinct serial_id                                                                                  "
            + "   FROM b_message                                                                                        "
            + "   WHERE serial_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "'                               "
            + " )                                                                                                       "
            + "</script>")
    List<BMessageBo> selectByStatusList(@Param("p1") List<String> statusList);

    @Select("<script>"
            + " SELECT                                                                                                  "
            + "   t.serial_id,                                                                                          "
            + "   t.serial_type,                                                                                        "
            + "   t.serial_code                                                                                         "
            + " FROM b_message t                                                                                        "
            + " INNER JOIN b_monitor t1 ON t.serial_id = t1.id AND t.serial_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "'"
            + " WHERE t.serial_status != t1.status                                                                      "
            + " UNION ALL                                                                                               "
            + " SELECT                                                                                                  "
            + "   t.serial_id,                                                                                          "
            + "   t.serial_type,                                                                                        "
            + "   t.serial_code                                                                                         "
            + " FROM b_message t                                                                                        "
            + " INNER JOIN b_monitor t1 ON t.serial_id = t1.id AND t.serial_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED + "'"
            + " WHERE t1.audit_status = '"+ DictConstant.DICT_B_MONITOR_AUDIT_STATUS_TWO +"'                            "
            + " UNION ALL                                                                                               "
            // wms 新增删除功能, 需要吧删除的也 从预警中删除掉
            + " SELECT                                                                                                  "
            + "   t.serial_id,                                                                                          "
            + "   t.serial_type,                                                                                        "
            + "   t.serial_code                                                                                         "
            + " FROM b_message t                                                                                        "
            + " LEFT JOIN b_monitor t1 ON t.serial_id = t1.id AND t.serial_type in ('" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "'"
            + " ,'" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED + "')                                         "
            + " WHERE t1.id is null and t.serial_type in ('" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "'           "
            + " ,'" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED + "')                                         "
            + "</script>")
    List<BMessageBo> selectIdAndStatus();

    /**
     * 根据 监管任务状态 和 审核状态查询
     * @param status
     * @param auditStatus
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                                 "
            + "  	t.id serial_id,                                                                                                     "
            + "  	t.STATUS,                                                                                                           "
            + "  	t.CODE serial_code,                                                                                                 "
            + "     t1.label status_name,                                                                                               "
            + "  	t.in_time                                                                                                           "
            + "  FROM                                                                                                                   "
            + "  	b_monitor t                                                                                                         "
            + " LEFT JOIN s_dict_data t1 ON t.status = t1.dict_value AND t1.code= '"+ DictConstant.DICT_B_MONITOR_STATUS+ "'            "
            + "  WHERE                                                                                                                  "
            + "  	t.`status` = #{p1}                                                                                                  "
            + "  	AND t.audit_status = #{p2}                                                                                          "
            + "     AND t.in_time <= DATE_SUB(NOW(), INTERVAL 72 HOUR)                                                                  "
            + "  	AND t.id NOT IN ( SELECT DISTINCT serial_id FROM b_message WHERE serial_type in (                                   "
            + " '" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "','" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR_UNAUDITED + "'       "
            + " ) )                                                                                                     "
    )
    List<BMessageBo> selectNotAuditMonitor(@Param("p1") String status, @Param("p2") String auditStatus);
}
