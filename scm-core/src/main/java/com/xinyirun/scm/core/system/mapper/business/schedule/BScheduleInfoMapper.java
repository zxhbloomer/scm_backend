package com.xinyirun.scm.core.system.mapper.business.schedule;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleInfoEntity;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-04-11
 */
@Repository
public interface BScheduleInfoMapper extends BaseMapper<BScheduleInfoEntity> {

    /**
     * 出库监管id查询调度
     */
    @Select("                                                                                                       "
            + "     SELECT                                                                                          "
            + "            t1.*                                                                                     "
            + "     FROM                                                                                            "
            + "  	       b_schedule_info t1                                                                       "
            + "     where true                                                                                      "
            + "            and (t1.schedule_id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                   "
            + "     ")
    BScheduleInfoVo selectByScheduleId(@Param("p1") Integer schedule_id);

}
