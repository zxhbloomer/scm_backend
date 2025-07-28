package com.xinyirun.scm.core.system.mapper.sys.schedule.v5;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 监管任务损耗预警
 */
@Repository
public interface SBMonitorLossWarningBatchV5Mapper extends BaseMapper<BMonitorEntity> {

    String common_select = "  "
            + "  SELECT                                                                           "
            + " 	    t.id serial_id,                                                          "
            + " 	    t.code serial_code,                                                      "
            + " 		ifnull(t1.qty,t2.qty) as in_qty,                                          "
            + "         ifnull(t3.qty,t4.qty) as out_qty,                                         "
            + "         ifnull(ifnull(t3.qty,t4.qty) - ifnull(t1.qty,t2.qty) - ifnull(t7.qty,0), 0)  as qty_loss     "
            + "      FROM                                                                         "
            + " 	        b_monitor t                                                           "
            + " LEFT JOIN b_monitor_in t1 ON t1.monitor_id = t.id                                 "
            + " LEFT JOIN b_monitor_unload t2 ON t2.monitor_id = t.id                             "
            + " LEFT JOIN b_monitor_out t3 ON t3.monitor_id = t.id                                "
            + " LEFT JOIN b_monitor_delivery t4 ON t4.monitor_id = t.id                           "
            +"  LEFT JOIN b_return_relation t7 ON t7.serial_id = t.id AND t7.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_MONITOR+"'                     "
            +"  AND  t7.STATUS = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS_TG +"'                                                                        "
            + " where TRUE                                                                        "
            ;



    @Select("                                                                                                                               "
            +        common_select
            + " AND t.status = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                                             "
            + " AND ifnull(t1.type,t2.type)!=  '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"'                                             "
            + " AND	ifnull(ifnull(t3.qty,t4.qty) - ifnull(t1.qty,t2.qty) - ifnull(t7.qty,0) , 0) > 0                                                            "
            + " AND ifnull(ifnull(t3.qty,t4.qty) - ifnull(t1.qty,t2.qty) - ifnull(t7.qty,0), 0)/ifnull(t3.qty,t4.qty) * 100  >= #{p1,jdbcType=INTEGER}         "
            + " AND DATE_FORMAT(t.c_time, '%Y-%m-%d' )>=#{p2}                                                                                                  "
            +" 	AND not exists (  SELECT  1 FROM b_message tt1 where                                                                        "
            +"       tt1.serial_type = '"+DictConstant.DICT_SYS_CODE_TYPE_M_MONITOR_LOSS+"'                                                 "
            +"       and tt1.type = '"+DictConstant.DICT_B_MESSAGE_TYPE_1+"'                                                                "
            +"       and t.id = tt1.serial_id and t.code = tt1.serial_code)                                                                 "
            + "     ")
    List<BMessageBo> selectByLossWarning(@Param("p1") Double value,@Param("p2")LocalDate dateTime);

    @Select("  SELECT                                                                                                                    "
            +"    t1.serial_id,                                                                                                          "
            +"    t1.serial_type,                                                                                                        "
            +"    t1.serial_code                                                                                                         "
            +"   FROM b_message t1                                                                                                       "
            +"  LEFT JOIN b_monitor t2 on t2.id = t1.serial_id                                                                           "
            +"    AND t1.serial_type = '"+DictConstant.DICT_SYS_CODE_TYPE_M_MONITOR_LOSS+"'	                                             "
            +"    AND t1.type= '"+DictConstant.DICT_B_MESSAGE_TYPE_1+"'                                                                  "
            +"  LEFT JOIN b_monitor_in t3 ON t3.monitor_id = t2.id                                                                       "
            +"  LEFT JOIN b_monitor_unload t4 ON t4.monitor_id = t2.id                                                                   "
            +"  LEFT JOIN b_monitor_out t5 ON t5.monitor_id = t2.id                                                                      "
            +"  LEFT JOIN b_monitor_delivery t6 ON t6.monitor_id = t2.id                                                                 "
            +"  LEFT JOIN b_return_relation t7 ON t7.serial_id = t2.id AND t7.serial_type = '"+ SystemConstants.SERIAL_TYPE.B_MONITOR+"'                     "
            +"  AND  t7.STATUS = '"+ DictConstant.DICT_B_RETURN_RELATION_STATUS_TG +"'                                                                        "
            +"  where TRUE                                                                                                               "
            +"  AND t2.status = 7                                                                                                        "
            +"  AND ifnull(t5.type,t6.type)!=  '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"'                                          "
            +"  AND	ifnull(ifnull(t5.qty,t6.qty) - ifnull(t3.qty,t4.qty) - ifnull(t7.qty,0), 0) >= 0                                                         "
            +"  AND ifnull(ifnull(t5.qty,t6.qty) - ifnull(t3.qty,t4.qty) - ifnull(t7.qty,0), 0)/ifnull(t5.qty,t6.qty) * 100  < #{p1,jdbcType=INTEGER}       "
//            +"  AND DATE_FORMAT(t2.c_time, '%Y-%m-%d' )>=#{p2}                                                                           "
            +" ")
    List<BMessageBo> selectByLossWarningIsDel(@Param("p1")Double value);
}
