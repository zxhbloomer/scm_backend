package com.xinyirun.scm.core.system.mapper.business.bkmonitor.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.bkmonitor.BBkMonitorLogDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * monitor 备份状态表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-29
 */
@Repository
public interface BBkMonitorLogDetailV2Mapper extends BaseMapper<BBkMonitorLogDetailEntity> {

    /**
     * 分页新增
     *
     * @param curSize  当前条数
     * @param pageSize 分页大小
     * @param param    查询参数
     * @param logId    日志id
     * @return 新增总条数
     */
    @Insert(""
            +  "  INSERT INTO b_bk_monitor_log_detail (                                                                 "
            +  "    monitor_id,                                                                                         "
            +  "    monitor_delivery_id,                                                                                "
            +  "    monitor_in_id,                                                                                      "
            +  "    monitor_out_id,                                                                                     "
            +  "    monitor_unload_id,                                                                                  "
            +  "    log_id,                                                                                             "
            +  "    `status`,                                                                                           "
            +  "    c_time )                                                                                            "
            +  "  SELECT                                                                                                "
            +  "    t5.monitor_id,                                                                                      "
            +  "    t5.monitor_delivery_id,                                                                             "
            +  "    t5.monitor_in_id,                                                                                   "
            +  "    t5.monitor_out_id,                                                                                  "
            +  "    t5.monitor_unload_id,                                                                               "
            +  "    #{p4} AS log_id,                                                                                    "
            +  "    '1' AS `status`,                                                                                    "
            +  "    NOW() AS c_time                                                                                     "
            +  "  FROM                                                                                                  "
            +  "    (                                                                                                   "
            +  "  	SELECT                                                                                              "
            +  "  	  t.id AS monitor_id,                                                                               "
            +  "  	  t4.id AS monitor_delivery_id,                                                                     "
            +  "  	  t1.id AS monitor_in_id,                                                                           "
            +  "  	  t3.id AS monitor_out_id,                                                                          "
            +  "  	  t2.id AS monitor_unload_id                                                                        "
            +  "  	FROM                                                                                                "
            +  "  	  b_monitor t                                                                                       "
            +  "  	  LEFT JOIN b_monitor_in t1 ON t1.monitor_id = t.id                                                 "
            +  "  	  LEFT JOIN b_monitor_unload t2 ON t2.monitor_id = t.id                                             "
            +  "  	  LEFT JOIN b_monitor_out t3 ON t3.monitor_id = t.id                                                "
            +  "  	  LEFT JOIN b_monitor_delivery t4 ON t4.monitor_id = t.id                                           "
            +  "  	LIMIT #{p1}, #{p2}                                                                                  "
            +  "    ) t5                                                                                                "
    )
    int insertBatch(@Param("p1") int curSize,@Param("p2") int pageSize,@Param("p3") BBkMonitorVo param,@Param("p4") Integer logId);

    @Select(""
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.monitor_id,                                                                                         "
            + "   t.monitor_delivery_id,                                                                                "
            + "   t.monitor_in_id,                                                                                      "
            + "   t.monitor_out_id,                                                                                     "
            + "   t.monitor_unload_id,                                                                                  "
            + "   t.log_id                                                                                              "
            + "  FROM                                                                                                   "
            + "    b_bk_monitor_log_detail t                                                                            "
            + " WHERE t.`status` = #{p3.status}                                                                         "
            + " ORDER BY t.id                                                                                           "
            + " LIMIT #{p1}, #{p2}                                                                                      "

    )
    List<BBkMonitorLogDetailEntity> selectListByStatus(@Param("p1") int curSize,@Param("p2") int pageSize,@Param("p3")  BBkMonitorLogDetailEntity param);

    /**
     * 分页查询
     * @param param
     * @param page
     * @return
     */
    @Select(""
            + " SELECT                                                                                                  "
            + "   t.id,                                                                                                 "
            + "   t.monitor_id,                                                                                         "
            + "   t.monitor_code,                                                                                       "
            + "   t.exception,                                                                                          "
            + "   t.flag,                                                                                               "
            + "   t1.label status_name,                                                                                 "
            + "   t3.name c_name,                                                                                       "
            + "   (case t2.type when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_1 +"' then '备份' when '"+ DictConstant.DICT_B_MONITOR_BACKUP_TYPE_2 +"' then '恢复' else '' end) type_name,                                                                                       "
            + "   t.c_time                                                                                              "
            + "  FROM                                                                                                   "
            + "    b_bk_monitor_log_detail t                                                                            "
            + "  LEFT JOIN s_dict_data t1 ON t1.`code` = '" + DictConstant.DICT_B_MONITOR_BACKUP_DETAIL_STATUS +"' AND t1.dict_value = t.status"
            + "  LEFT JOIN b_bk_monitor_log t2 ON t.log_id = t2.id                                                      "
            + "  LEFT JOIN m_staff t3 ON t2.c_id = t3.id                                                                "
            + " WHERE (t.`status` = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                          "
            + " and (t.`flag` = #{p1.flag} or #{p1.flag} is null or #{p1.flag} = '')                                    "
            + " and (t2.`type` = #{p1.type} or #{p1.type} is null or #{p1.type} = '')                                    "
            + " and (t.`monitor_code` like concat('%', #{p1.monitor_code}, '%') or #{p1.monitor_code} is null or #{p1.monitor_code} = '')"
    )
    IPage<BBkMonitorLogDetailVo> selectPageList(@Param("p1") BBkMonitorLogDetailVo param, Page<BBkMonitorLogDetailEntity> page);
}
