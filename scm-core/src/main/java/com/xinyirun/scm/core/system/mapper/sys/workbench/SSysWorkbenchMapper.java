package com.xinyirun.scm.core.system.mapper.sys.workbench;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.workbench.SSysWorkbenchEntity;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.notice.BNoticeVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmMatterVo;
import com.xinyirun.scm.bean.system.vo.workbench.BpmRemindVo;
import com.xinyirun.scm.bean.system.vo.workbench.SSysWorkbenchVo;
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
 * @since 2025-02-17
 */
@Repository
public interface SSysWorkbenchMapper extends BaseMapper<SSysWorkbenchEntity> {

    /**
     * 锁定临时表
     */
    @Select("    "
            + "       select *                                                             "
            + "         from s_sys_workbench t1                                            "
            + "        where true                                                          "
            + "          and t1.`code` = #{p1.code}                                        "
            + "          and t1.staff_id = #{p1.staff_id}                                     "
    )
    SSysWorkbenchVo getDataByCode(@Param("p1") SSysWorkbenchVo searchCondition);


    /**
     * 获取事项数据
     * @return
     */
    @Select("                                                                                                                                                          "
            +"  select                                                                                                                                                 "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0') as pendingProcessQty,                                           "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0' and date(t1.c_time) = curdate()) as todayPendingProcessQty,     "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '1') as processedQty,                                                "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '1' and date(t1.c_time) = curdate()) as todayProcessedQty,          "
            +"  (select count(1) from bpm_instance t1 where t1.owner_code = #{p1.staffCode} ) as initiatedQty,                                                                 "
            +"  (select count(1) from bpm_instance t1 where t1.owner_code = #{p1.staffCode} and date(t1.c_time) = curdate()) as todayInitiatedQty,                             "
            +"  (select count(1) from bpm_instance t1 where t1.owner_code = #{p1.staffCode} and date(t1.u_time) = curdate()) as todayUpdateInitiatedQty,                       "
            +"  (select count(1) from bpm_cc t1 where t1.user_code = #{p1.staffCode}) receivedQty,                                                                             "
            +"  (select count(1) from bpm_cc t1 where t1.user_code = #{p1.staffCode} and date(t1.c_time) = curdate()) todayReceivedQty                                         "
            +"                                                  "
    )
    BpmMatterVo getMatterData(@Param("p1") BpmMatterVo param);

    /**
     * 获取事项数据
     * @return
     */
    @Select("                                                                                                                                                                                                                             "
            +"  select                                                                                                                                                                                                                    "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0') as pendingQty,                                                                                                            "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0' AND DATEDIFF(CURDATE(),DATE( t1.c_time )) > 1 AND DATEDIFF(CURDATE(),DATE( t1.c_time )) <= 2)  as overOneDay,                   "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0' AND DATEDIFF(CURDATE(),DATE( t1.c_time )) > 2 AND DATEDIFF(CURDATE(),DATE( t1.c_time )) <= 3)  as overTwoDay,                    "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0' AND DATEDIFF(CURDATE(),DATE( t1.c_time )) > 3 AND DATEDIFF(CURDATE(),DATE( t1.c_time )) <= 4)  as overThreeDay,                   "
            +"  (SELECT count(1) FROM bpm_todo t1 WHERE t1.assignee_code = #{p1.staffCode} AND t1.STATUS = '0' AND DATEDIFF(CURDATE(),DATE( t1.c_time )) > 7 ) as overOneWeek                                                        "
            +"                                                  "
    )
    BpmRemindVo getRemindData(@Param("p1") BpmRemindVo param);


    /**
     * 获取通知数据
     * @return
     */
    @Select(
            "                                                                  "
            +"   SELECT                                                                                                                                                    "
            +"     t.id,                                                                                                                                                   "
            +"     t.msg,                                                                                                                                                  "
            +"     t.c_time,                                                                                                                                               "
            +"     t2.label as type_name,                                                                                                                                  "
            +"     t3.label as status_name,                                                                                                                                "
            +"     t4.name as c_name,                                                                                                                                      "
            +"     t1.is_read,                                                                                                                                             "
            +"     t.title                                                                                                                                                 "
            +"   FROM b_notice t                                                                                                                                           "
            +"   LEFT JOIN b_notice_staff t1 ON t.id = t1.notice_id                                                                                                        "
            +"   LEFT JOIN v_dict_info t2 ON t2.dict_value = t.type AND t2.code = 'b_notice_type'                                                                          "
            +"   LEFT JOIN v_dict_info t3 ON t3.dict_value = t.status AND t3.code = 'b_notice_status'                                                                      "
            +"   LEFT JOIN m_staff t4 ON t4.id = t.c_id                                                                                                                    "
            +"   WHERE (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                                              "
            +"   AND (t1.staff_id = #{p1.staff_id} or #{p1.staff_id} is null or #{p1.staff_id} = '')                                                                       "
            +"   AND (t.type = #{p1.type} or  #{p1.type} is null or #{p1.type} = ''  )                                                                                    "
            +"   AND t1.is_read is null                                                                                                                                    "
            +"   order by t.c_time desc                                                                                                                                    "
    )
    List<BNoticeVo> getNoticeListAll(@Param("p1") BNoticeVo param);
}
