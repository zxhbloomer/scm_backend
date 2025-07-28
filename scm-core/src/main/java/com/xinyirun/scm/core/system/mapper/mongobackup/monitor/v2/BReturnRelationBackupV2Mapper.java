package com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.returnrelation.BReturnRelationEntity;
import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.BReturnRelationRestoreV2Entity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BReturnRelationBackupV2Mapper extends BaseMapper<BReturnRelationEntity> {

    @Select("select 1 from b_return_relation where serial_id = #{p1.monitor_id} and serial_type = 'b_monitor' for update")
    List<Integer> selectForUpdate(@Param("p1") BBkMonitorLogDetailVo vo);


    @Select("select * from b_return_relation where serial_id = #{monitorId} and serial_type = 'b_monitor'")
    BReturnRelationRestoreV2Entity selectByMonitorId(@Param("monitorId") Integer monitorId);

    @Delete("delete from b_return_relation where serial_id = #{monitorId} and serial_type = 'b_monitor'")
    void deleteByMonitorId(@Param("monitorId") Integer monitorId);
}
