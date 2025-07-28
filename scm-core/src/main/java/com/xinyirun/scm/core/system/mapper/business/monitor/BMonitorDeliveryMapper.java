package com.xinyirun.scm.core.system.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.xinyirun.scm.bean.app.vo.business.monitor.AppBMonitorPickupVo;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorDeliveryEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-02-12
 */
@Repository
public interface BMonitorDeliveryMapper extends BaseMapper<BMonitorDeliveryEntity> {

//    @Select("                                                                                                       "
//            + "     SELECT                                                             "
//            + "            t1.owner_id,                                                       "
//            + "            t1.owner_code            "
//            + "       FROM                                                             "
//            + "  	       b_schedule t                                                  "
//            + "  LEFT JOIN b_out_plan t1 ON t.out_plan_code = t1.code                "
//            + "     where true                                                                                         "
//            + "     and (t.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)          "
//            + "     ")
//    AppBMonitorPickupVo selectByMonitorPickup(@Param("p1") Integer id);

    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t3.monitor_time,                                                                             "
            + "            t3.waybill_code,                                                                             "
            + "            t3.code,                                                                                     "
            + "            t4.code as schedule_code,                                                                    "
            + "            t4.out_plan_code,                                                                            "
            + "            t6.name out_warehouse_name,                                                                  "
            + "            t4.out_warehouse_address,                                                                    "
            + "            t5.name in_warehouse_name,                                                                   "
            + "            t4.in_warehouse_address,                                                                     "
            + "            t10.contract_no,                                                                             "
            + "            t11.name as goods_name,                                                                      "
            + "            t7.name as customer_name,                                                                    "
            + "            t8.no as vehicle_no,                                                                         "
            + "            t9.name as driver_name,                                                                      "
            + "            t9.mobile_phone as driver_mobile_phone,                                                      "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       b_monitor_out t                                                                              "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN b_monitor t3 ON t.monitor_id = t3.id                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t3.schedule_id                                                      "
            + "  LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                 "
            + "  LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                "
            + "  LEFT JOIN m_goods_spec t11 ON t4.sku_id = t11.id                                                       "
            + "  LEFT JOIN m_customer t7 ON t3.customer_id = t7.id                                                      "
            + "  LEFT JOIN m_vehicle t8 ON t3.vehicle_id = t8.id                                                        "
            + "  LEFT JOIN m_driver t9 ON t3.driver_id = t9.id                                                          "

            + "  LEFT JOIN (                                                                                                             "
            + "		SELECT                                                                                                               "
            + "			t1.id,t2.contract_no                                                                                             "
            + "		FROM                                                                                                                 "
            + "			b_order t1                                                                                                       "
            + "			JOIN b_in_order t2 ON t1.serial_id = t2.id                                                                       "
            + "			AND t1.serial_type = 'b_in_order'                                                                                "
            + "			union all                                                                                                        "
            + "		SELECT                                                                                                               "
            + "			t1.id,t2.contract_no                                                                                             "
            + "		FROM                                                                                                                 "
            + "			b_order t1                                                                                                       "
            + "			JOIN b_out_order t2 ON t1.serial_id = t2.id                                                                      "
            + "			AND t1.serial_type = 'b_out_order'                                                                               "
            + "     )t10 on t4.order_id = t10.id                                                                                         "


            + "                                                                                                         "
            ;

//    /**
//     * 页面查询列表
//     */
//    @Select("    "
//            + common_select
//            + "  where true                                                                                                                   "
//            + "    and t.status != '"+ DictConstant.DICT_B_MONITOR_OUT_STATUS_FINISH+"'                "
//            + "    and (CONCAT(t4.code,t5.name,t6.name,t8.no,t9.name)                     "
//            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null) "
//            + "      ")
//    IPage<AppBMonitorPickupVo> selectPage(Page page, @Param("p1") AppBMonitorPickupVo searchCondition);

//    /**
//     * id查询
//     */
//    @Select("    "
//            + common_select
//            + "  where true                                                                                                                   "
//            + "    and (t.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                "
//            + "      ")
//    AppBMonitorPickupVo selectId(@Param("p1") Integer id);


    /**
     * 调度单id查询
     */
    @Select("    "
            + "    SELECT                                                             "
            + "            t.*                                                       "
            + "       FROM                                                             "
            + "  	       b_monitor_out t                                                  "
            + "       LEFT JOIN b_monitor t1 ON t.monitor_id = t1.id                                 "
            + "       LEFT JOIN b_schedule t2 ON t1.schedule_id = t2.id                                 "
            + "    where true                                                                                         "
            + "       and t2.id =  #{p1,jdbcType=INTEGER}          "
            + "     ")
    BMonitorDeliveryEntity selectByScheduleId(@Param("p1") Integer id);
}
