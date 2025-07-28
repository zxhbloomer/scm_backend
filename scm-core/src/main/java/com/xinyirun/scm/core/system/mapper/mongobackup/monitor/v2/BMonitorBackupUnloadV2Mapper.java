package com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorUnloadEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
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
public interface BMonitorBackupUnloadV2Mapper extends BaseMapper<BMonitorUnloadEntity> {

    @Select("select 1 from b_monitor_unload where id = #{p1.monitor_unload_id}  for update")
    List<Integer> selectForUpdate(@Param("p1") BBkMonitorLogDetailVo vo);
}
