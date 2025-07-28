package com.xinyirun.scm.core.system.mapper.business.track;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.xinyirun.scm.bean.app.vo.business.track.AppBTrackVo;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
public interface BTrackMapper extends BaseMapper<BTrackEntity> {

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
    BTrackVo selectOne(@Param("p1") BTrackVo vo);

    /**
     * 按条件获取删除
     */
    @Update("    "
            + "  delete from b_track t1                                                                                 "
            + "    where t1.vehicle_no =  #{p1.vehicle_no,jdbcType=VARCHAR}                                             "
            + "    and t1.waybill_no =  #{p1.waybill_no,jdbcType=VARCHAR}                                               "
            + "    order by t1.c_time                                                                                   "
            + "    limit 1                                                                                              "
            + "      ")
    void deleteTrack(@Param("p1") BTrackVo vo);

    /**
     * 按条件获取数据
     */
//    @Select("    "
//            + "  select t1.*, t1.start_time track_start_time, t1.end_time track_end_time from b_track t1                "
//            + "  where true                                                                                             "
//            + "    and t1.vehicle_no =  #{p1.vehicle_no,jdbcType=VARCHAR}                                               "
//            + "    and t1.waybill_no =  #{p1.waybill_no,jdbcType=VARCHAR}                                               "
//            + "    order by t1.c_time desc                                                                              "
//            + "    limit 1                                                                                              "
//            + "      ")
//    AppBTrackVo selectOneData(@Param("p1") AppBTrackVo vo);

}
