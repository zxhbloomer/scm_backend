package com.xinyirun.scm.core.system.mapper.log.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import com.xinyirun.scm.bean.entity.log.sys.SLogSysEntity;
import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author Wang Qianfeng
 * @date 2022/10/21 11:07
 */
@Repository
public interface SLogSyncStatusMapper extends BaseMapper<BSyncStatusEntity> {

    @Select("    SELECT                                                                                                "
            +  "      t.serial_code,                                                                                   "
            +  "      t.id,                                                                                            "
            +  "      t1.label serial_type_name,                                                                       "
            +  "      t.serial_type serial_type,                                                                       "
            +  "      t.serial_id serial_id,                                                                           "
            +  "      t.serial_detail_id serial_detail_id,                                                                           "
            +  "      t.serial_detail_code serial_detail_code,                                                                           "
            +  "      t.c_time,                                                                                        "
            +  "      IF(t.`status`,'成功','失败') status_name,                                                         "
            +  "      t.msg                                                                                            "
            +  "    FROM b_sync_status_error t                                                                         "
            +  "    LEFT JOIN s_dict_data t1 ON t1.code = 'sys_code_type' AND t.serial_type = t1.dict_value            "
            +  "    where (t1.label like concat('%', #{p1.serial_type}, '%') or #{p1.serial_type} is null or #{p1.serial_type} = '')"
            +  "    and t.`status` = '" + DictConstant.DICT_LOG_SYNC_STATUS_E +                                      "'"
            +  "    and t.`sync_status` = 'OK'                                                                         "
            +  "    and (t.serial_code like concat('%', #{p1.serial_code}, '%') or #{p1.serial_code} is null or #{p1.serial_code} = '')"
            +  "    and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                         "
            +  "    and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) >= #{p1.c_time_start} or #{p1.c_time_start} is null or #{p1.c_time_start} = '')"
            +  "    and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) <= #{p1.c_time_end} or #{p1.c_time_end} is null or #{p1.c_time_end} = '')"
    )
    IPage<BSyncStatusVo> selectPage(@Param("p1") BSyncStatusVo searchCondition, Page<SLogSysEntity> pageCondition);

    @Select("select count(*) from ("
            + "    SELECT                                                                                              "
            +  "      t.id                                                                                             "
            +  "    FROM b_sync_status_error t                                                                         "
            +  "    LEFT JOIN s_dict_data t1 ON t1.code = 'sys_code_type' AND t.serial_type = t1.dict_value            "
            +  "    where (t1.label like concat('%', #{p1.serial_type}, '%') or #{p1.serial_type} is null or #{p1.serial_type} = '')"
            +  "    and t.`status` = '" + DictConstant.DICT_LOG_SYNC_STATUS_E +                                      "'"
            +  "    and (t.serial_code like concat('%', #{p1.serial_code}, '%') or #{p1.serial_code} is null or #{p1.serial_code} = '')"
            +  "    and (t.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                         "
            +  "    and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) >= #{p1.c_time_start} or #{p1.c_time_start} is null or #{p1.c_time_start} = '')"
            +  "    and (DATE_FORMAT(t.c_time, '%Y-%m-%d' ) <= #{p1.c_time_end} or #{p1.c_time_end} is null or #{p1.c_time_end} = '')"
            + "     limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  ${(p1.pageCondition.limit_count)}    "
            +  "   ) sub                                                                                               "
    )
    Integer getLimitCount(@Param("p1") BSyncStatusVo searchCondition);
}
