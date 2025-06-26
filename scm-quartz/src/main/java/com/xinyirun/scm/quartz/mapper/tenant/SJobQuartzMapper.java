package com.xinyirun.scm.quartz.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.quartz.SJobEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-07-04
 */
@Repository
public interface SJobQuartzMapper extends BaseMapper<SJobEntity> {
    @Update("                                                                                           "
            + "    update s_job t                                                                     "
            + "       set t.run_times = #{p1.run_times,jdbcType=INTEGER},                             "
            + "           t.fire_time = #{p1.fire_time,jdbcType=DATE},                                "
            + "           t.scheduled_fire_time = #{p1.scheduled_fire_time,jdbcType=DATE},            "
            + "           t.prev_fire_time = #{p1.prev_fire_time,jdbcType=DATE},                      "
            + "           t.next_fire_time = #{p1.next_fire_time,jdbcType=DATE},                      "
            + "           t.u_time = now(3)                                                           "
            + "     where t.id = #{p1.id,jdbcType=BIGINT}                                             "
            + "                                                                          "
    )
    int updateJob(@Param("p1") SJobEntity job);
}
