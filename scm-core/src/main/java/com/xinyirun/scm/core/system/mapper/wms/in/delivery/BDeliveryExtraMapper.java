package com.xinyirun.scm.core.system.mapper.wms.in.delivery;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.in.delivery.BDeliveryExtraEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 提货单副表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-06-26
 */
@Repository
public interface BDeliveryExtraMapper extends BaseMapper<BDeliveryExtraEntity> {


    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + "select * from b_delivery_extra t"
            + "  where t.delivery_id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    BDeliveryExtraEntity selectByInId(@Param("p1") Integer id);

    /**
     *  监管任务入库id查询提货单从表数据
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
            + "            t.nine_file pound_file,                                                                      "
            + "            t3.order_type,                                                                               "
            + "            t.six_file                                                                                   "
            + "       FROM                                                                                              "
            + "  	       b_monitor_in t                                                                               "
            + "     LEFT JOIN b_monitor  t1 ON t1.id = t.monitor_id                                                     "
            + "     LEFT JOIN b_schedule  t2 ON t2.id = t1.schedule_id                                                  "
            + "     LEFT JOIN b_in_plan_detail  t3 ON t3.id = t2.in_plan_detail_id                                      "
//            + "     LEFT JOIN b_in_order  t4 ON t4.id = t3.order_id                                                     "

            + "  LEFT JOIN (                                                                                            "
            + "		SELECT                                                                                              "
            + "			t1.*,'b_in_order' order_type,t1.supplier_id customer_id                                         "
            + "		FROM                                                                                                "
            + "			 b_in_order t1                                                                                  "
            + "			union all                                                                                       "
            + "		SELECT                                                                                              "
            + "			t2.*,'b_out_order' order_type,t2.client_id customer_id                                          "
            + "		FROM                                                                                                "
            + "			 b_out_order t2                                                                                 "
            + "     )t4 on t3.order_id = t4.id and  t3.order_type = t4.order_type                                       "

            + "     where true                                                                                          "
            + "         and t.id = #{p1,jdbcType=INTEGER}                                                               "
            + "  ")
    BDeliveryExtraEntity selectByMonitorInId(@Param("p1") Integer id);
}
