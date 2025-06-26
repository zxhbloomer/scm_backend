package com.xinyirun.scm.core.system.mapper.business.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.out.BOutExtraEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 出库单其他信息 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-18
 */
@Repository
public interface BOutExtraMapper extends BaseMapper<BOutExtraEntity> {

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + "select * from b_out_extra t"
            + "  where t.out_id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BOutExtraEntity selectByInId(@Param("p1") int p1);

    /**
     *  监管任务出库id查询出库单从表数据
     */
    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "            t3.order_id,                                                                                 "
            + "            t4.price,                                                                                    "
            + "            t.one_file,                                                                                  "
            + "            t.two_file,                                                                                  "
            + "            t.three_file,                                                                                "
            + "            t.four_file,                                                                                 "
            + "            t.five_file,                                                                                 "
            + "            t.six_file,                                                                                  "
            + "            t.seven_file,                                                                                "
            + "            t.eight_file,                                                                                "
            + "            t.eleven_file pound_file,                                                                    "
            + "            t.nine_file                                                                                  "
            + "       FROM                                                                                              "
            + "  	       b_monitor_out t                                                                              "
            + "     LEFT JOIN b_monitor  t1 ON t1.id = t.monitor_id                                                     "
            + "     LEFT JOIN b_schedule  t2 ON t2.id = t1.schedule_id                                                  "
            + "     LEFT JOIN b_out_plan_detail  t3 ON t3.id = t2.out_plan_detail_id                                    "
            + "     LEFT JOIN b_out_order  t4 ON t4.id = t3.order_id                                                    "
            + "     where true                                                                                          "
            + "         and t.id = #{p1,jdbcType=INTEGER}                                                               "
            + "  ")
    BOutExtraEntity selectByMonitorOutId(@Param("p1") Integer id);

}
