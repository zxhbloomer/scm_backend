package com.xinyirun.scm.core.system.mapper.mongobackup.track;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import com.xinyirun.scm.bean.system.vo.mongo.track.BMonitorTrackMongoDataVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Repository
public interface BTrackBackupMapper extends BaseMapper<BTrackEntity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from b_track t1                                                                               "
            + "  where true                                                                                             "
            + "    and t1.vehicle_no =  #{p1.vehicle_no,jdbcType=VARCHAR}                                               "
            + "    and t1.waybill_no =  #{p1.waybill_no,jdbcType=VARCHAR}                                               "
            + "    and t1.start_time <= #{p1.start_time,jdbcType=DATE}                                                "
            + "    and t1.end_time >= #{p1.end_time,jdbcType=DATE}                                                    "
            + "    order by t1.c_time                                                                                   "
            + "    limit 1                                                                                              "
            + "      ")
    BMonitorTrackMongoDataVo selectOne(@Param("p1") BTrackVo vo);

}
