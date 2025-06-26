package com.xinyirun.scm.core.whapp.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.app.vo.business.monitor.AppBMonitorInVo;
import com.xinyirun.scm.bean.app.vo.business.monitor.AppBMonitorListSizeVo;
import com.xinyirun.scm.bean.app.vo.business.monitor.AppBMonitorOutVo;
import com.xinyirun.scm.bean.app.vo.business.monitor.AppBMonitorVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
public interface WhAppBMonitorMapper extends BaseMapper<BMonitorEntity> {

    String common_select = "  "
            + "     SELECT                                                                                                                                                         "
            + "            t13.code as container_code,                                                                                                                             "
            + "            t.*,                                                                                                                                                    "
            + "            ifnull(ifnull(t20.status,t21.status),ifnull(t22.status,t23.status)) as status,                                                                          "
            + "            t4.code as schedule_code,                                                                                                                               "
            + "            ifnull(t20.type,t21.type) as in_type,                                                                                                                   "
            + "            ifnull(t22.type,t23.type) as out_type,                                                                                                                  "
            + "            case when  ifnull(t20.type,t21.type) = '"+ SystemConstants.MONITOR.B_MONITOR_IN +"' then '入' else '卸' end as in_type_name,                             "
            + "            case when  ifnull(t22.type,t23.type) = '"+ SystemConstants.MONITOR.B_MONITOR_OUT +"' then '出' else '提' end as out_type_name,                           "
            + "            ifnull(t20.id,t21.id) as monitor_in_id,                                                                                                                 "
            + "            t20.in_id as in_id,                                                                                                                 "
            + "            t.in_time as monitor_finish_time,                                                                                                                       "
            + "            ifnull(t22.id,t23.id) as monitor_out_id,                                                                                                                "
            + "            t4.out_plan_code,                                                                                                                                       "
            + "            t4.in_plan_code,                                                                                                                                        "
            + "            t4.is_consumer,                                                                                                                                         "
            + "            t6.short_name out_warehouse_name,                                                                                                                       "
            + "            t4.out_warehouse_address,                                                                                                                               "
            + "            t5.short_name in_warehouse_name,                                                                                                                        "
            + "            t4.in_warehouse_address,                                                                                                                                "
            + "            t10.contract_no,                                                                                                                                        "
            + "            t11.name as goods_name,                                                                                                                                 "
            + "            t7.name as customer_name,                                                                                                                               "
            + "            t8.no as vehicle_no,                                                                                                                                    "
            + "            t9.name as driver_name,                                                                                                                                 "
            + "            t9.mobile_phone as driver_mobile_phone,                                                                                                                 "
            + "            t1.name as c_name,                                                                                                                                      "
            + "            t2.name as u_name,                                                                                                                                      "
            + "            t13.id as container_id,                                                                                                                                 "
            + "            t12.waybill_contract_no,                                                                                                                                "
            + "            t3.extra_code in_extra_code,                                                                                                                            "
            + "            t4.in_rule                                                                                                                                              "
            + "       FROM                                                                                                                                                         "
            + "  	        b_monitor t                                                                                                                                            "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                                            "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                                            "
            + "  LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                                                                                               "
            + "  LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                                                                                           "
            + "  LEFT JOIN b_monitor_out t22 ON t22.monitor_id = t.id                                                                                                              "
            + "  LEFT JOIN b_monitor_delivery t23 ON t23.monitor_id = t.id                                                                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                                                  "
            + "  LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                                                                            "
            + "  LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                                                                           "
            + "  LEFT JOIN m_customer t7 ON t.customer_id = t7.id                                                                                                                  "
            + "  LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                                                                                    "
            + "  LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                                                                                      "
            + "  LEFT JOIN b_order t10 on t4.order_id = t10.id                                                                                                                     "
            + "  LEFT JOIN m_goods_spec t11 ON t4.sku_id = t11.id                                                                                                                  "
            + "  LEFT JOIN b_schedule_info t12 on t12.schedule_id = t4.id                                                                                                          "
            + "  LEFT JOIN m_container t13 on t13.id = t.container_id                                                                                                              "
            + "  LEFT JOIN b_in_plan_detail t3 ON t4.in_plan_detail_id = t3.id                                                                                                     "
            ;

//    /**
//     * 页面查询列表
//     */
//    @Select("    "
//            + common_select
//            + "  where true                                                                                                                                        "
//            + "    and (t20.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  OR t21.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  )   "
//            + "    and t.status <> '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT+"'                                                                                 "
//            + "    and (CONCAT(ifnull(t7.code,''),ifnull(t7.short_name,''),ifnull(t7.name,''),ifnull(t4.out_plan_code,''),                                         "
//            + "     ifnull(t4.in_plan_code,''),ifnull(t10.contract_no,''),                                                                                         "
//            + "     ifnull(t.waybill_code,''),ifnull(t.code,''),ifnull(t4.code,''),ifnull(t5.name,''),ifnull(t6.name,''),                                          "
//            + "     ifnull(t8.no,''),ifnull(t9.name,''),ifnull(t11.pm,''),ifnull(t11.name,''),ifnull(t11.code,''),                                                 "
//            + "     ifnull(t11.spec,''),ifnull(t12.waybill_contract_no,''))                                                                                        "
//            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)        "
//            + "     AND (DATE_SUB(CURDATE(), INTERVAL #{p1.days,jdbcType=INTEGER} DAY) <= t.u_time or #{p1.days,jdbcType=INTEGER} is NULL)                         "
//            + "     ${p1.params.dataScopeAnnotation}                                                                                                               "
//            + "      ")
//    IPage<AppBMonitorVo> selectPage(Page page, @Param("p1") AppBMonitorVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "  where true                                                                                                                                        "
            + "    and (t20.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  OR t21.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  )   "
            + "    and t.status <> '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT+"'                                                                                 "
            + "    and (CONCAT(ifnull(t13.code,''),ifnull(t7.code,''),ifnull(t7.short_name,''),ifnull(t7.name,''),ifnull(t4.out_plan_code,''),                     "
            + "     ifnull(t4.in_plan_code,''),ifnull(t10.contract_no,''),                                                                                         "
            + "     ifnull(t.waybill_code,''),ifnull(t.code,''),ifnull(t4.code,''),ifnull(t5.name,''),ifnull(t6.name,''),                                          "
            + "     ifnull(t8.no,''),ifnull(t9.name,''),ifnull(t11.pm,''),ifnull(t11.name,''),ifnull(t11.code,''),                                                 "
            + "     ifnull(t11.spec,''),ifnull(t12.waybill_contract_no,''))                                                                                        "
            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)        "
            + "     AND (DATE_SUB(CURDATE(), INTERVAL #{p1.days,jdbcType=INTEGER} DAY) <= t.u_time or #{p1.days,jdbcType=INTEGER} is NULL)                         "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                               "
            // 排序固定u_time
            + "     order by t.u_time desc                                                                                                                         "
            // 此处应该用${} 否则会直接输出字符串
            + "     limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  #{p1.pageCondition.size}                                                         "
            + "      ")
    List<AppBMonitorVo> selectPage(@Param("p1") AppBMonitorVo searchCondition);


    /**
     * 页面查询列表
     */
    @Select("    "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "  SELECT count(*) c                                                                                                                                 "
            + "       FROM                                                                                                                                         "
            + "  	        b_monitor t                                                                                                                            "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                            "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                            "
            + "  LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                                                                               "
            + "  LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                                                                           "
            + "  LEFT JOIN b_monitor_out t22 ON t22.monitor_id = t.id                                                                                              "
            + "  LEFT JOIN b_monitor_delivery t23 ON t23.monitor_id = t.id                                                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                                  "
            + "  LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                                                            "
            + "  LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                                                           "
            + "  LEFT JOIN m_customer t7 ON t.customer_id = t7.id                                                                                                  "
            + "  LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                                                                    "
            + "  LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                                                                      "
            + "  LEFT JOIN b_order t10 on t10.serial_type in ('b_in_order','b_out_order') and t4.order_id = t10.id                                                 "
            + "  LEFT JOIN m_goods_spec t11 ON t4.sku_id = t11.id                                                                                                  "
            + "  LEFT JOIN b_schedule_info t12 on t12.schedule_id = t4.id                                                                                          "
            + "  where true                                                                                                                                        "
            + "    and (t20.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  OR t21.status = '"+ DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'  )   "
            + "    and t.status <> '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT+"'                                                                                 "
            + "    and (CONCAT(ifnull(t7.code,''),ifnull(t7.short_name,''),ifnull(t7.name,''),ifnull(t4.out_plan_code,''),                                         "
            + "     ifnull(t4.in_plan_code,''),ifnull(t10.contract_no,''),                                                                                         "
            + "     ifnull(t.waybill_code,''),ifnull(t.code,''),ifnull(t4.code,''),ifnull(t5.name,''),ifnull(t6.name,''),                                          "
            + "     ifnull(t8.no,''),ifnull(t9.name,''),ifnull(t11.pm,''),ifnull(t11.name,''),ifnull(t11.code,''),                                                 "
            + "     ifnull(t11.spec,''),ifnull(t12.waybill_contract_no,''))                                                                                        "
            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)        "
            + "     AND (DATE_SUB(CURDATE(), INTERVAL #{p1.days,jdbcType=INTEGER} DAY) <= t.u_time or #{p1.days,jdbcType=INTEGER} is NULL)                         "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                               "
            + "      ")
    Long selectFinishPageCount(@Param("p1") AppBMonitorVo searchCondition);

    /**
     * 调度单id查询监管任务
     */
    @Select("    "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t3.id as monitor_in_id,                                                                      "
            + "            t3.u_time as monitor_finish_time,                                                            "
            + "            t11.id as monitor_out_id,                                                                    "
            + "            t4.code as schedule_code,                                                                    "
            + "            t4.out_plan_code,                                                                            "
            + "            t6.name out_warehouse_name,                                                                  "
            + "            t4.out_warehouse_address,                                                                    "
            + "            t5.name in_warehouse_name,                                                                   "
            + "            t4.in_warehouse_address,                                                                     "
            + "            t10.contract_no,                                                                             "
            + "            t12.name as goods_name,                                                                      "
            + "            t7.name as customer_name,                                                                    "
            + "            t8.no as vehicle_no,                                                                         "
            + "            t9.name as driver_name,                                                                      "
            + "            t9.mobile_phone as driver_mobile_phone,                                                      "
            + "            ifnull(t3.status,t11.status) as status,                                                      "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       b_monitor t                                                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN b_monitor_in t3 ON t.id = t3.monitor_id                                                      "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                       "
            + "  LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                 "
            + "  LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                "
            + "  LEFT JOIN m_customer t7 ON t.customer_id = t7.id                                                       "
            + "  LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                         "
            + "  LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                           "

            + "  LEFT JOIN (                                                                                            "
            + "		SELECT                                                                                              "
            + "			t1.id,t2.contract_no                                                                            "
            + "		FROM                                                                                                "
            + "			b_order t1                                                                                      "
            + "			JOIN b_in_order t2 ON t1.serial_id = t2.id                                                      "
            + "			AND t1.serial_type = 'b_in_order'                                                               "
            + "			union all                                                                                       "
            + "		SELECT                                                                                              "
            + "			t1.id,t2.contract_no                                                                            "
            + "		FROM                                                                                                "
            + "			b_order t1                                                                                      "
            + "			JOIN b_out_order t2 ON t1.serial_id = t2.id                                                     "
            + "			AND t1.serial_type = 'b_out_order'                                                              "
            + "     )t10 on t4.order_id = t10.id                                                                        "

            + "  LEFT JOIN b_monitor_out t11 ON t.id = t11.monitor_id                                                   "
            + "  LEFT JOIN m_goods_spec t12 ON t4.sku_id = t12.id                                                       "
            + "  where true                                                                                             "
            + "         and (t4.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                         "
            + "      ")
    List<AppBMonitorVo> selectByScheduleId( @Param("p1") Integer id);


    /**
     * 调度单id查询待完成入库监管任务
     */
    @Select("    "
            + "     SELECT                                                                                              "
            + "            t.*                                                                                          "
            + "       FROM                                                                                              "
            + "  	       (select * from b_monitor_in tab1 union all select * from b_monitor_unload tab2) t            "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN b_monitor t3 ON t3.id = t.monitor_id                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t3.schedule_id                                                      "
            + "  where true                                                                                             "
            + "         and t.status != '"+DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'                              "
            + "         and (t4.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                         "
            + "      ")
    List<AppBMonitorInVo> selectBalanceInCount(@Param("p1") Integer id);


    /**
     * 调度单id查询待完成出库监管任务
     */
    @Select("    "
            + "     SELECT                                                                                              "
            + "            t.*                                                                                          "
            + "       FROM                                                                                              "
            + "  	       (select * from b_monitor_out tab1 union all select * from b_monitor_delivery tab2) t         "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN b_monitor t3 ON t3.id = t.monitor_id                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t3.schedule_id                                                      "
            + "  where true                                                                                             "
            + "         and t.status != '"+DictConstant.DICT_B_MONITOR_OUT_STATUS_FINISH+"'                             "
            + "         and (t4.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                         "
            + "      ")
    List<AppBMonitorOutVo> selectBalanceOutCount(@Param("p1") Integer id);


    /**
     * 调度单id查询已完成监管任务
     */
    @Select("    "
            + "     SELECT                                                                                                                            "
            + "            t.*                                                                                                                        "
            + "       FROM                                                                                                                            "
            + "  	       b_monitor t                                                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                               "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                               "
            + "  LEFT JOIN (select * from b_monitor_in tab1 union all select * from b_monitor_unload tab2) t3 ON t.id = t3.monitor_id                 "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                     "
            + "  where true                                                                                                                           "
            + "         and t3.status = '"+DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'                                                            "
            + "         and (t4.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                                                       "
            + "      ")
    List<AppBMonitorVo> selectFinishCount(@Param("p1") Integer id);

    /**
     * 查询入库数量
     */
    @Select("                                                                                                                                                  "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "     SELECT                                                                                                                                     "
            + "            count(t1.id) as in_size                                                                                                             "
            + "       FROM                                                                                                                                     "
            + "  	       b_monitor t                                                                                                                         "
            + "  	       left join (select * from b_monitor_in tab1 union all select * from b_monitor_unload tab2) t1 on t.id=t1.monitor_id                  "
            + "  	       left join (select * from b_monitor_out tab1 union all select * from b_monitor_delivery tab2) t2 on t.id=t2.monitor_id               "
            + "            LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                    "
            + "            LEFT JOIN m_warehouse t6 ON t4.in_warehouse_id = t6.id                                                                              "
            + "            LEFT JOIN m_warehouse t5 ON t4.out_warehouse_id = t5.id                                                                             "
            + "            LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                                                      "
            + "            LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                                                        "
            + "     where  true                                                                                                                                "
            + "            and t.status != '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT+"'                                                                     "
            + "            and t1.status != '"+DictConstant.DICT_B_MONITOR_IN_STATUS_FINISH+"'                                                                 "
            + "            and (CONCAT(t4.code,t5.name,t6.name,t8.no,t9.name)                                                                                  "
            + "            like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)   "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                        "
            + "     ")
    Integer selectInListSize(@Param("p1") AppBMonitorListSizeVo searchCondition);


    /**
     * 查询出库数量
     */
    @Select("                                                                                                                                                "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + "     SELECT                                                                                                                                   "
            + "            count(t2.id) as out_size                                                                                                          "
            + "       FROM                                                                                                                                   "
            + "  	       b_monitor t                                                                                                                       "
            + "  	       LEFT JOIN (select * from b_monitor_in tab1 union all select * from b_monitor_unload tab2) t1 on t.id=t1.monitor_id                "
            + "  	       LEFT JOIN (select * from b_monitor_out tab1 union all select * from b_monitor_delivery tab2) t2 on t.id=t2.monitor_id             "
            + "            LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                  "
            + "            LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                                            "
            + "            LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                                           "
            + "            LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                                                    "
            + "            LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                                                      "
            + "     where  true                                                                                                                              "
            + "            and t.status != '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT+"'                                                                   "
            + "            and t2.status != '"+DictConstant.DICT_B_MONITOR_OUT_STATUS_FINISH+"'                                                              "
            + "            and (CONCAT(t4.code,t5.name,t6.name,t8.no,t9.name)                                                                                "
            + "            like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null) "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                                        "
            + "     ")
    Integer selectOutListSize(@Param("p1") AppBMonitorListSizeVo searchCondition);

    /**
     * 车辆id查询监管任务
     */
    @Select("                                                                                                       "
            + "     SELECT                                                             "
            + "            count(t.id)                                                 "
            + "       FROM                                                             "
            + "  	       b_monitor t                                                  "
            + "     where true                                                                                         "
            + "         and t.status != '" + DictConstant.DICT_B_MONITOR_STATUS_EIGHT + "'                              "
            + "         and (t.vehicle_id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)          "
            + "     ")
    Integer selectByVehicleId(@Param("p1") Integer id);

    /**
     * 司机id查询监管任务
     */
    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "            count(1)                                                                                          "
            + "       FROM                                                                                              "
            + "  	       b_monitor t                                                                                  "
            + "     where true                                                                                          "
            + "         and t.status != '" + DictConstant.DICT_B_MONITOR_STATUS_EIGHT + "'                              "
            + "         and (t.driver_id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                   "
            + "     ")
    Integer selectByDriverId(@Param("p1") Integer id);

    /**
     * 承运商id查询监管任务
     */
    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "            t.*                                                                                          "
            + "       FROM                                                                                              "
            + "  	       b_monitor t                                                                                  "
            + "     where true                                                                                          "
            + "         and t.status != '" + DictConstant.DICT_B_MONITOR_STATUS_EIGHT + "'                              "
            + "         and (t.customer_id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                 "
            + "     ")
    List<BMonitorEntity> selectByCustomerId(@Param("p1") Integer id);

    /**
     * id查询
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                               "
            + "      ")
    AppBMonitorVo selectId(@Param("p1") Integer id);

    @Select("SELECT id from b_monitor where container_id = #{p1} and status != '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT +"'")
    List<Integer> selectActiveMonitorByContainerId(@Param("p1") Integer id);

    /**
     * 根据 物流订单ID 查询非作废的监管任务
     * @param scheduleId
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    id                                                                                                   "
            + "  FROM b_monitor                                                                                         "
            + "  WHERE schedule_id = #{p1}                                                                              "
            + "  AND status != '"+ DictConstant.DICT_B_MONITOR_STATUS_EIGHT +"'                                         "
            + "  union all                                                                                              "
            + "  SELECT                                                                                                 "
            + "    id                                                                                                   "
            + "  FROM b_monitor_backup                                                                                  "
            + "  WHERE schedule_id = #{p1}                                                                              "
    )
    List<Integer> selectNotCancelMonitorByScheduleId(@Param("p1") Integer scheduleId);

    /**
     * update轨迹查询时间范围
     * @param scheduleId
     * @return
     */
//    @Update(""
//            + "  UPDATE                                                                                                 "
//            + "   b_monitor                                                                                             "
//            + "  SET track_start_time = #{p1.track_start_time},                                                         "
//            + "   track_end_time = #{p1.track_end_time}                                                                 "
//            + "  WHERE id = #{p1.id}                                                                                    "
//    )
//    void updateTrackTime(@Param("p1") AppBMonitorVo vo);

    /**
     * update轨迹查询时间范围
     * @param scheduleId
     * @return
     */
    @Update(""
            + "  UPDATE                                                                                                 "
            + "   b_monitor                                                                                             "
            + "  SET track_log = #{p1.track_log},                                                                       "
            + "  track_c_time = now()                                                                                   "
            + "  WHERE id = #{p1.id}                                                                                    "
    )
    void updateTrackLog(@Param("p1") AppBMonitorVo vo);

    /**
     * 查询可以同步的监管任务
     * @param list
     * @return
     */
    @Select("<script>"
            + " SELECT t.* from b_monitor t                                                                             "
            + " LEFT JOIN b_schedule t1 ON t.schedule_id = t1.id                                                        "
            + " WHERE t.is_sync != '"+ DictConstant.DICT_B_MONITOR_IS_SYNC_N +"'                                        "
            + " <if test='p1 != null and p1.size != 0'>                                                                 "
            + "   AND t.id in                                                                                           "
            + "   <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                  "
            + "     #{item.id}                                                                                          "
            + "   </foreach>                                                                                            "
            + "  </if>                                                                                                  "
            + "</script>"
    )
    List<AppBMonitorVo> selectSyncData(@Param("p1") List<AppBMonitorVo> list);

    @Select("    "
            + " SELECT t.dbversion                                                                                      "
            + " FROM                                                                                                    "
            + "  b_monitor t                                                                                            "
            + " WHERE t.id =  #{p1,jdbcType=INTEGER}                                                                          "
            + "      ")
    Integer selectLatestDbVersion(@Param("p1") Integer id);
}
