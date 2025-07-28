package com.xinyirun.scm.core.system.mapper.business.bkmonitor.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.bkmonitor.BBkMonitorSyncLogEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * monitor 备份 同步信息表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-04-06
 */
@Repository
public interface BBkMonitorSyncLogV2Mapper extends BaseMapper<BBkMonitorSyncLogEntity> {

    @Select(""
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.monitor_id,                                                                                         "
            + "   t.monitor_code,                                                                                       "
            + "   t.exception,                                                                                          "
            + "   t.flag,                                                                                               "
            + "   t.type,                                                                                               "
            + "   t3.name last_backup_name,                                                                             "
            + "   t4.name last_restore_name,                                                                            "
            + "   (case t.type when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1 +"' then '备份' when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2 +"' then '恢复' else '' end) type_name, "
            + "   (case t.type when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1 +"' then t1.label when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2 +"' then t2.label else '' end) status_name, "
            + "   t.last_restore_time,                                                                                  "
            + "   t.u_time,                                                                                  "
            + "   t.last_backup_time                                                                                    "
            + "  FROM                                                                                                   "
            + "    b_bk_monitor_sync_log t                                                                              "
            + "  LEFT JOIN s_dict_data t1 ON t1.`code` = '" + DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS +"' AND t1.dict_value = t.status"
            + "  LEFT JOIN s_dict_data t2 ON t2.`code` = '" + DictConstant.DICT_B_MONITOR_RESTORE_DETAIL_STATUS +"' AND t2.dict_value = t.status"
            + "  LEFT JOIN m_staff t3 ON t.last_backup_id = t3.id                                                       "
            + "  LEFT JOIN m_staff t4 ON t.last_restore_id = t4.id                                                      "
            + " WHERE (t.`status` = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                          "
            + " and (t.`flag` = #{p1.flag} or #{p1.flag} is null or #{p1.flag} = '')                                    "
            + " and (t.`type` = #{p1.type} or #{p1.type} is null or #{p1.type} = '')                                    "
            + " and (t.`version` = #{p1.version})                                                                       "
            + " and (t.`monitor_code` like concat('%', #{p1.monitor_code}, '%') or #{p1.monitor_code} is null or #{p1.monitor_code} = '')"
    )
    IPage<BBkMonitorLogDetailVo> selectPageList(@Param("p1") BBkMonitorLogDetailVo param, Page<BBkMonitorLogDetailVo> page);
}
