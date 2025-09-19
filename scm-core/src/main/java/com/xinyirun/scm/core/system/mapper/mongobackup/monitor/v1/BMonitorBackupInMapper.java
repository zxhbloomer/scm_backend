package com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorInEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v1.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v1.BMonitorInUnloadDataMongoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BMonitorBackupInMapper extends BaseMapper<BMonitorInEntity> {


    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t14.id as monitor_out_id,                                                                    "
            + "            t3.monitor_time,                                                                             "
            + "            t3.waybill_code,                                                                             "
            + "            t3.code,                                                                                     "
            + "            t4.code as schedule_code,                                                                    "
            + "            t12.code as in_plan_code,                                                                    "
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
            + "  	       (select * from b_monitor_in tab1 union all select * from b_monitor_unload tab2) t            "
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
            + "  LEFT JOIN b_in_plan_detail t13 ON t4.in_plan_detail_id = t13.id                                        "
            + "  LEFT JOIN b_in_plan t12 ON t13.plan_id = t12.id                                                        "
            + "  LEFT JOIN b_monitor_out t14 ON t14.monitor_id = t3.id                                                  "
            + "                                                                                                         "
            ;

    /**
     * id查询
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                                   "
            + "    and (t.monitor_id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                       "
            + "      ")
    BMonitorInUnloadDataMongoVo selectMonitorInUnloadByMonitorId(@Param("p1") Integer id);


    @Select("select 1 from b_monitor_in where id = #{p1.monitor_in_id,jdbcType=INTEGER}  for update")
    List<Integer> selectForUpdate(@Param("p1") BBkMonitorLogDetailVo vo);
}
