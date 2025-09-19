package com.xinyirun.scm.core.system.mapper.mongobackup.monitor.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.mongo.monitor.v2.BMonitorDataMongoEntity;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorLogDetailVo;
import com.xinyirun.scm.bean.system.vo.business.bkmonitor.v2.BBkMonitorVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorPreviewFileVo;
import com.xinyirun.scm.bean.system.vo.business.monitor.BMonitorVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.BMonitorDataDetailMongoV2Vo;
import com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v2.BMonitorFilePreviewBackupDataV2Vo;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.BMonitorFileVoTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.BMonitorPreviewFileVoTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
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
public interface BMonitorBackupV2Mapper extends BaseMapper<BMonitorEntity> {

    String common_select = "  "
            + "     SELECT                                                                                                                                      "
            + "            t8.code as vehicle_code,                                                                                                             "
            + "            t9.code as driver_code,                                                                                                              "
            + "            t.id monitor_id,                                                                                                                     "
            + "            t.schedule_id,                                                                                                                       "
            + "            t.code,                                                                                                                              "
            + "            t.status,                                                                                                                            "
            + "            t.driver_id,                                                                                                                         "
            + "            t.vehicle_id,                                                                                                                        "
            + "            t.customer_code,                                                                                                                     "
            + "            t.customer_id,                                                                                                                       "
            + "            t.waybill_code,                                                                                                                      "
            + "            t.schedule_time,                                                                                                                     "
            + "            t.monitor_time,                                                                                                                      "
            + "            t.in_time,                                                                                                                           "
            + "            t.out_time,                                                                                                                          "
            + "            t.c_time,                                                                                                                            "
            + "            t.u_time,                                                                                                                            "
            + "            t.c_id,                                                                                                                              "
            + "            t.u_id,                                                                                                                              "
            + "            t.dbversion,                                                                                                                         "
            + "            t.track_start_time,                                                                                                                  "
            + "            t.track_end_time,                                                                                                                    "
            + "            t.validate_vehicle,                                                                                                                  "
            + "            t.settlement_status,                                                                                                                 "
            + "            t.audit_status,                                                                                                                      "
            + "            t.in_audit_id,                                                                                                                       "
            + "            t.in_audit_time,                                                                                                                     "
            + "            t.out_audit_id,                                                                                                                      "
            + "            t.out_audit_time,                                                                                                                    "
            + "            t.validate_time,                                                                                                                     "
            + "            t.gps_time,                                                                                                                          "
            + "            t.validate_vehicle_type,                                                                                                             "
            + "            t.out_empty_time,                                                                                                                    "
            + "            t.out_loading_time,                                                                                                                  "
            + "            t.out_heavy_time,                                                                                                                    "
            + "            t.in_heavy_time,                                                                                                                     "
            + "            t.in_unloading_time,                                                                                                                 "
            + "            t.is_sync,                                                                                                                           "
            + "            t19.label is_sync_name,                                                                                                              "
            + "            t.in_empty_time,                                                                                                                     "
            + "            t13.label status_name,                                                                                                               "
            + "            t24.label audit_status_name,                                                                                                         "
            + "            t25.label settlement_status_name,                                                                                                    "
            + "            t4.code as schedule_code,                                                                                                            "
            + "            t4.type as schedule_type,                                                                                                            "
            + "            t4.in_type as in_type,                                                                                                "
            + "            t4.out_type as out_type,                                                                                               "
            + "            ifnull(t20.qty,t21.qty) as in_qty,                                                                                                   "
            + "            ifnull(t22.qty,t23.qty) as out_qty,                                                                                                  "
            + "            case when ifnull(t20.type,t21.type) = '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"' then 0 "
            + "            else ifnull(ifnull(t22.qty,t23.qty) - ifnull(t20.qty,t21.qty), 0) end as qty_loss,                                                   "
            + "            case when ifnull(t20.type,t21.type) = '"+ SystemConstants.MONITOR.B_MONITOR_IN +"' then '入库' when                                   "
            + "             ifnull(t20.type,t21.type) = '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"' then '卸货' else '' end as in_type_name,                "
            + "            case when ifnull(t22.type,t23.type) = '"+ SystemConstants.MONITOR.B_MONITOR_OUT +"' then '出库' when                                  "
            + "            ifnull(t22.type,t23.type) = '"+ SystemConstants.MONITOR.B_MONITOR_DELIVERY +"' then '提货' else '' end as out_type_name,              "
            + "            ifnull(t20.id,t21.id) as monitor_in_id,                                                                                              "
            + "            ifnull(t22.id,t23.id) as monitor_out_id,                                                                                             "
            + "            t20.id as monitor_in_id_bk,                                                                                                          "
            + "            t21.id as monitor_unload_id_bk,                                                                                                      "
            + "            t22.id as monitor_out_id_bk,                                                                                                         "
            + "            t23.id as monitor_delivery_id_bk,                                                                                                    "
            + "            t4.out_plan_code,                                                                                                                    "
            + "            t4.in_plan_code,                                                                                                                     "
            + "            t6.name out_warehouse_name,                                                                                                          "
            + "            t6.short_name out_warehouse_short_name,                                                                                              "
            + "            t6.id out_warehouse_id,                                                                                                              "
            + "            t6.warehouse_type out_warehouse_type,                                                                                                "
            + "            t4.out_warehouse_address,                                                                                                            "
            + "            t5.name in_warehouse_name,                                                                                                           "
            + "            t5.short_name in_warehouse_short_name,                                                                                               "
            + "            t5.id in_warehouse_id,                                                                                                               "
            + "            t5.warehouse_type in_warehouse_type,                                                                                                 "
            + "            t4.in_warehouse_address,                                                                                                             "
            + "            ifnull(t10.contract_no,t4.contract_no) contract_no,                                                                                  "
            + "            t11.name as goods_name,                                                                                                              "
            + "            t11.spec as sku_name,                                                                                                                "
            + "            t11.pm as pm,                                                                                                                        "
            + "            t11.code as sku_code,                                                                                                                "
            + "            t7.name as customer_name,                                                                                                            "
            + "            t8.no as vehicle_no,                                                                                                                 "
            + "            t9.name as driver_name,                                                                                                              "
            + "            t9.mobile_phone as driver_mobile_phone,                                                                                              "
            + "            t1.name as c_name,                                                                                                                   "
            + "            t14.code as out_code,                                                                                                                "
            + "            t15.code as in_code,                                                                                                                 "
            + "            t16.label as out_status_name,                                                                                                        "
            + "            t17.label as in_status_name,                                                                                                         "
            + "            t14.status as out_status,                                                                                                            "
            + "            t15.status as in_status,                                                                                                             "
            + "            t2.name as u_name,                                                                                                                   "
            + "            t26.name as in_audit_name,                                                                                                           "
            + "            t27.name as out_audit_name,                                                                                                          "
            + "            t28.waybill_contract_no,                                                                                                             "
            + "            t3.label in_warehouse_type_name,                                                                                                     "
            + "            t12.label out_warehouse_type_name,                                                                                                   "
            + "            year(t.c_time) audit_year,                                                                                                           "
            + "            date_format(t.c_time, '%Y%m') audit_month,                                                                                           "
            + "            t18.remark as cancel_remark                                                                                                          "
            + "       FROM                                                                                                                                      "
            + "  	        b_monitor t                                                                                                                         "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                                         "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                                         "
            + "  LEFT JOIN b_schedule t4 ON t4.id = t.schedule_id                                                                                               "
            + "  LEFT JOIN m_warehouse t5 ON t4.in_warehouse_id = t5.id                                                                                         "
            + "  LEFT JOIN m_warehouse t6 ON t4.out_warehouse_id = t6.id                                                                                        "
            + "  LEFT JOIN m_customer t7 ON t.customer_id = t7.id                                                                                               "
            + "  LEFT JOIN m_vehicle t8 ON t.vehicle_id = t8.id                                                                                                 "
            + "  LEFT JOIN m_driver t9 ON t.driver_id = t9.id                                                                                                   "
            + "  LEFT JOIN b_order t10 on t10.serial_type in ('b_in_order','b_out_order') and t4.order_id = t10.id                                              "
            + "  LEFT JOIN m_goods_spec t11 ON t4.sku_id = t11.id                                                                                               "
            + "  LEFT JOIN v_dict_info t12 ON t12.code = '"+DictConstant.DICT_M_WAREHOUSE_TYPE+"' and t6.warehouse_type = t12.dict_value                        "
            + "  LEFT JOIN v_dict_info t13 ON t13.code = 'b_monitor_status' and t.status = t13.dict_value                                                       "
            + "  LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                                                                            "
            + "  LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                                                                        "
            + "  LEFT JOIN b_monitor_out t22 ON t22.monitor_id = t.id                                                                                           "
            + "  LEFT JOIN b_monitor_delivery t23 ON t23.monitor_id = t.id                                                                                      "
            + "  LEFT JOIN b_out t14 on t22.out_id = t14.id                                                                                                     "
            + "  LEFT JOIN b_in t15 on t20.in_id = t15.id                                                                                                       "
            + "  LEFT JOIN v_dict_info t16 ON t16.code = '"+DictConstant.DICT_B_OUT_STATUS+"' and t14.status = t16.dict_value                                   "
            + "  LEFT JOIN v_dict_info t17 ON t17.code = '"+DictConstant.DICT_B_IN_STATUS+"' and t15.status = t17.dict_value                                    "
            + "  LEFT JOIN m_cancel t18 ON t18.serial_id = t.id and t18.serial_type = '"+SystemConstants.SERIAL_TYPE.B_MONITOR+"'                               "
            + "  LEFT JOIN v_dict_info t24 ON t24.code = '"+DictConstant.DICT_B_MONITOR_AUDIT_STATUS+"' and t.audit_status = t24.dict_value                     "
            + "  LEFT JOIN v_dict_info t25 ON t25.code = '"+DictConstant.DICT_B_MONITOR_SETTLEMENT_STATUS+"' and t.settlement_status = t25.dict_value           "
            + "  LEFT JOIN m_staff t26 ON t.in_audit_id = t26.id                                                                                                "
            + "  LEFT JOIN m_staff t27 ON t.out_audit_id = t27.id                                                                                               "
            + "  LEFT JOIN b_schedule_info t28 ON t4.id = t28.schedule_id                                                                                       "
            + "  LEFT JOIN v_dict_info t3 ON t3.code = '"+DictConstant.DICT_M_WAREHOUSE_TYPE+"' and t5.warehouse_type = t3.dict_value                           "
            + "  LEFT JOIN v_dict_info t19 ON t19.code = '"+DictConstant.DICT_B_MONITOR_IS_SYNC+"' and t.is_sync = t19.dict_value                               "
            ;
    /**
     * 页面查询列表
     */
    @Select(" <script>   "
            + common_select
            + "  where true                                                                                             "
//            + "  and t.code = 'MON202301130005' " // 审核完成的
            + "  and t.id = #{p1}                 " // 审核完成的
            + "   </script>   ")
    BMonitorDataMongoEntity selectPageById(@Param("p1") Integer id);

    /**
     * 页面查询列表
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            +" select * from (select @row_num := @row_num + 1 AS no,tab1.*,tab2.id m_id from(                                                                                                                    "
            + common_select
            + "  where true                                                                                                                                                     "
            + "    and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} ='')                                 "
            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and t.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "
            + "   <if test='p1.audit_status_list != null and p1.audit_status_list.length!=0' >                                                                                  "
            + "    and t.audit_status in                                                                                                                                        "
            + "        <foreach collection='p1.audit_status_list' item='item' index='index' open='(' separator=',' close=')'>                                                   "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "
            + "    and (t.code like concat('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                         "
            + "    and (t4.code like concat('%',#{p1.schedule_code,jdbcType=VARCHAR},'%') or #{p1.schedule_code,jdbcType=VARCHAR} is null)                                      "
            + "    and (t.waybill_code like concat('%',#{p1.waybill_code,jdbcType=VARCHAR},'%') or #{p1.waybill_code,jdbcType=VARCHAR} is null)                                 "
            + "    and (concat(t7.name,t7.short_name,t7.code) like concat('%',#{p1.customer_name,jdbcType=VARCHAR},'%') or #{p1.customer_name,jdbcType=VARCHAR} is null)        "
            + "    and (concat(t8.code,t8.no) like concat('%',#{p1.vehicle_no,jdbcType=VARCHAR},'%') or #{p1.vehicle_no,jdbcType=VARCHAR} is null)                              "
            + "    and (concat(t9.name,t9.mobile_phone,t9.code) like concat('%',#{p1.driver_name,jdbcType=VARCHAR},'%') or #{p1.driver_name,jdbcType=VARCHAR} is null)          "
            + "    and (concat(ifnull(t10.contract_no,''),ifnull(t4.contract_no,''))  like concat('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)                                  "
            + "    and (concat(t11.name,t11.pm,t11.code,t11.spec) like concat('%',#{p1.goods_name,jdbcType=VARCHAR},'%') or #{p1.goods_name,jdbcType=VARCHAR} is null)          "
            + "    and (t4.out_type = #{p1.out_type,jdbcType=VARCHAR} or #{p1.out_type,jdbcType=VARCHAR} is null  or #{p1.out_type,jdbcType=VARCHAR} = '')                      "
            + "    and (t4.in_type = #{p1.in_type,jdbcType=VARCHAR} or #{p1.in_type,jdbcType=VARCHAR} is null or #{p1.in_type,jdbcType=VARCHAR} = '')                           "
            + "    and (concat(t6.name,t6.short_name) like concat('%',#{p1.out_warehouse_name,jdbcType=VARCHAR},'%') or #{p1.out_warehouse_name,jdbcType=VARCHAR} is null)      "
            + "    and (concat(t5.name,t5.short_name) like concat('%',#{p1.in_warehouse_name,jdbcType=VARCHAR},'%') or #{p1.in_warehouse_name,jdbcType=VARCHAR} is null)        "
            + "    and (t4.out_plan_code like concat('%',#{p1.out_plan_code,jdbcType=VARCHAR},'%') or #{p1.out_plan_code,jdbcType=VARCHAR} is null)                             "
            + "    and (t4.in_plan_code like concat('%',#{p1.in_plan_code,jdbcType=VARCHAR},'%') or #{p1.in_plan_code,jdbcType=VARCHAR} is null)                                "
            + "    and (DATE_FORMAT(t.out_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p1.start_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.start_time,jdbcType=DATE} is null)                "
            + "    and (DATE_FORMAT(t.out_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p1.over_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.over_time,jdbcType=DATE} is null)                  "
            + "    and (t.settlement_status = #{p1.settlement_status,jdbcType=VARCHAR} or #{p1.settlement_status,jdbcType=VARCHAR} is null or #{p1.settlement_status,jdbcType=VARCHAR} ='')                                 "
            + "    and (t.audit_status = #{p1.audit_status,jdbcType=VARCHAR} or #{p1.audit_status,jdbcType=VARCHAR} is null or #{p1.audit_status,jdbcType=VARCHAR} ='')                                 "
            + "    and (DATE_FORMAT(t.in_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p1.in_time_start,jdbcType=DATE}, '%Y%m%d' ) or #{p1.in_time_start,jdbcType=DATE} is null)         "
            + "    and (DATE_FORMAT(t.in_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p1.in_time_end,jdbcType=DATE}, '%Y%m%d' ) or #{p1.in_time_end,jdbcType=DATE} is null)             "
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                            "
            + " ) tab1                                                                                                                                                           "
            + "     inner join b_monitor tab2 on  tab1.id = tab2.id                                                                                                             "
            + " ,(select @row_num:=0) tab3                                                                                                                                       "
            + "     ${p1.sort_sql}                                                                                                                                     "
            + "     limit 500 )tt                                                                                                                                              "
            + "    where (m_id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                                                                             "
            + "    and (no =  #{p1.no,jdbcType=INTEGER} or #{p1.no,jdbcType=INTEGER} is null)                                                                                "
            + "   </script>")
    BMonitorVo getMonitor(@Param("p1") BMonitorVo searchCondition);

    @Select(" <script> "
            +"	SELECT                                                                                                  "
            +"		count( t.id ) c                                                                                     "
            +"	FROM                                                                                                    "
            +"		b_monitor t                                                                                         "
//            +"  LEFT JOIN b_bk_monitor_sync_log t1 ON t.id = t1.monitor_id                                              "
            +"	WHERE                                                                                                   "
            +"	TRUE                                                                                                    "
            + "    and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null or #{p1.status,jdbcType=VARCHAR} ='')                                 "
            + "    and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p1.start_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.start_time,jdbcType=DATE} is null)"
            + "    and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p1.over_time,jdbcType=DATE}, '%Y%m%d' ) or #{p1.over_time,jdbcType=DATE} is null)"
            + "    and (t.settlement_status = #{p1.settlement_status} or #{p1.settlement_status} is null or #{p1.settlement_status} = '')   "
            + "    and (t.audit_status = #{p1.audit_status} or #{p1.audit_status} is null or #{p1.audit_status} = '')   "
//            + "    and (t1.flag != 'ING' OR t1.flag is null)                                                            "
            +"     </script>                                                                                            "
    )
    Long selectPageMyCount(@Param("p1") BBkMonitorVo searchCondition);


    /**
     * id查询
     */
    @Select(" <script>   "
            + common_select
            + "  where true                                                                                                                   "
            + "    and (t.id =  #{p1,jdbcType=INTEGER} or #{p1,jdbcType=INTEGER} is null)                "
            + " </script>     ")
    BMonitorDataDetailMongoV2Vo selectId(@Param("p1") Integer id);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<BMonitorEntity> selectIdsIn(@Param("p1") List<BMonitorVo> searchCondition);

    @Select("     "
            + "		SELECT                                                                                                                                         "
            + "			t1.id,                                                                                                                                     "
            + "			t1.CODE,                                                                                                                                   "
            + "			t1.c_time,                                                                                                                                 "
            + "			t4.NO,                                                                                                                                     "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.one_file,t3_2.one_file) ), JSON_OBJECT( ) ) file_1,                                                  "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.two_file,t3_2.two_file) ), JSON_OBJECT( ) ) file_2,                                                  "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.fourteen_file,t3_2.fourteen_file) ), JSON_OBJECT( ) ) file_40,                                       "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.three_file,t3_2.three_file) ), JSON_OBJECT( ) ) file_3,                                              "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.four_file,t3_2.four_file) ), JSON_OBJECT( ) ) file_4,                                                "
            + "		    ifnull(f_get_file_json ( t3_1.twelve_file ) ,JSON_OBJECT()) file_38,                                                                       "
            + "		    ifnull(f_get_file_json ( t3_1.thirteen_file ) ,JSON_OBJECT()) file_39,                                                                     "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.five_file,t3_2.five_file) ), JSON_OBJECT( ) ) file_5,                                                "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.six_file,t3_2.six_file) ), JSON_OBJECT( ) ) file_6,                                                  "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.seven_file,t3_2.seven_file) ), JSON_OBJECT( ) ) file_7,                                              "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.eight_file,t3_2.eight_file) ), JSON_OBJECT( ) ) file_8,                                              "
            + "			JSON_OBJECT( ) AS file_9,                                                                                                                  "
            + "			JSON_OBJECT( ) AS file_10,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_11,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_12,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_13,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_14,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_15,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_16,                                                                                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.nine_file,t3_2.nine_file) ), JSON_OBJECT( ) ) file_17,                                               "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.ten_file,t3_2.ten_file) ), JSON_OBJECT( ) ) file_18,                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.eleven_file,t3_2.eleven_file) ), JSON_OBJECT( ) ) file_19,                                           "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.one_file,t2_2.one_file) ), JSON_OBJECT( ) ) file_20,                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.two_file,t2_2.two_file )), JSON_OBJECT( ) ) file_21,                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.ten_file,t2_2.ten_file )), JSON_OBJECT( ) ) file_22,                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.three_file,t2_2.three_file )), JSON_OBJECT( ) ) file_23,                                             "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.four_file ,t2_2.four_file)), JSON_OBJECT( ) ) file_24,                                               "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.five_file,t2_2.five_file )), JSON_OBJECT( ) ) file_25,                                               "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.six_file,t2_2.six_file )), JSON_OBJECT( ) ) file_26,                                                 "
            + "			JSON_OBJECT( ) AS file_27,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_28,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_29,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_30,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_31,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_32,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_33,                                                                                                                 "
            + "			JSON_OBJECT( ) AS file_34,                                                                                                                 "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.seven_file,t2_2.seven_file ) ), JSON_OBJECT( ) ) file_35,                                            "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.eight_file,t2_2.eight_file ) ), JSON_OBJECT( ) ) file_36,                                            "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.nine_file,t2_2.nine_file ) ), JSON_OBJECT( ) ) file_37,                                               "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.eleven_file,t2_2.eleven_file ) ), JSON_OBJECT( ) ) file_41,                                          "
            + "			ifnull( f_get_file_json ( ifnull(t2_1.twelve_file,t2_2.twelve_file ) ), JSON_OBJECT( ) ) file_42,                                          "
            + "			ifnull( f_get_file_json ( ifnull(t3_1.fifteen_file,t3_2.fifteen_file ) ), JSON_OBJECT( ) ) file_43                                         "

            + "		FROM                                                                                                                                           "
            + "			b_monitor t1                                                                                                                               "
            + "			LEFT JOIN ( SELECT 'b_monitor_in' serial_type, tab1.* FROM b_monitor_in tab1 ) t2_1 ON t2_1.monitor_id = t1.id                             "
            + "			LEFT JOIN ( SELECT 'b_monitor_unload' serial_type, tab2.* FROM b_monitor_unload tab2 ) t2_2 ON t2_2.monitor_id = t1.id                     "
            + "			LEFT JOIN ( SELECT 'b_monitor_out' serial_type, tab1.* FROM b_monitor_out tab1  ) t3_1 ON t3_1.monitor_id = t1.id                          "
            + "			LEFT JOIN ( SELECT 'b_monitor_delivery' serial_type, tab2.* FROM b_monitor_delivery tab2 ) t3_2 ON t3_2.monitor_id = t1.id                 "
            + "			LEFT JOIN m_vehicle t4 ON t1.vehicle_id = t4.id                                                                                            "
            + "     where t1.id = #{p1,jdbcType=INTEGER}                                                                                                        "
            + "     ")
    @Results({
            @Result(property = "file_1", column = "file_1", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_2", column = "file_2", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_40", column = "file_40", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorFileVoTypeHandler.class),
            @Result(property = "file_3", column = "file_3", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_4", column = "file_4", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_38", column = "file_38", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorFileVoTypeHandler.class),
            @Result(property = "file_39", column = "file_39", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorFileVoTypeHandler.class),
            @Result(property = "file_5", column = "file_5", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_6", column = "file_6", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_7", column = "file_7", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_8", column = "file_8", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_9", column = "file_9", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_10", column = "file_10", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_11", column = "file_11", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_12", column = "file_12", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_13", column = "file_13", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_14", column = "file_14", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_15", column = "file_15", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_16", column = "file_16", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_17", column = "file_17", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_18", column = "file_18", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_19", column = "file_19", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_20", column = "file_20", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_21", column = "file_21", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_22", column = "file_22", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_23", column = "file_23", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_24", column = "file_24", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_25", column = "file_25", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_26", column = "file_26", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_27", column = "file_27", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_28", column = "file_28", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_29", column = "file_29", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_30", column = "file_30", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_31", column = "file_31", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_32", column = "file_32", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_33", column = "file_33", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_34", column = "file_34", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_35", column = "file_35", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_36", column = "file_36", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_37", column = "file_37", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_41", column = "file_41", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_42", column = "file_42", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),
            @Result(property = "file_43", column = "file_43", javaType = BMonitorPreviewFileVo.class, typeHandler = BMonitorPreviewFileVoTypeHandler.class),

    })
    BMonitorFilePreviewBackupDataV2Vo getMonitorFile(@Param("p1") Integer id);

    /**
     * 查询参数添加到mq
     * @param param 参数
     * @return List<>
     */
    @Select("<script>"
            +  "  	SELECT                                                                                              "
            +  "  	  t.id AS monitor_id,                                                                               "
            +  "  	  t.code AS monitor_code,                                                                           "
            +  "  	  t4.id AS monitor_delivery_id,                                                                     "
            +  "  	  t1.id AS monitor_in_id,                                                                           "
            +  "  	  t3.id AS monitor_out_id,                                                                          "
            +  "  	  t2.id AS monitor_unload_id                                                                        "
            +  "  	FROM                                                                                                "
            +  "  	  b_monitor t                                                                                       "
            +  "  	  LEFT JOIN b_monitor_in t1 ON t1.monitor_id = t.id                                                 "
            +  "  	  LEFT JOIN b_monitor_unload t2 ON t2.monitor_id = t.id                                             "
            +  "  	  LEFT JOIN b_monitor_out t3 ON t3.monitor_id = t.id                                                "
            +  "  	  LEFT JOIN b_monitor_delivery t4 ON t4.monitor_id = t.id                                           "
//            +  "      LEFT JOIN b_bk_monitor_sync_log t5 ON t.id = t5.monitor_id                                        "
            +  "    WHERE TRUE                                                                                          "
            + "    and (t.status = #{p3.status,jdbcType=VARCHAR} or #{p3.status,jdbcType=VARCHAR} is null or #{p3.status,jdbcType=VARCHAR} ='')                                 "
            + "    and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &gt;= DATE_FORMAT(#{p3.start_time,jdbcType=DATE}, '%Y%m%d' ) or #{p3.start_time,jdbcType=DATE} is null)"
            + "    and (DATE_FORMAT(t.c_time, '%Y%m%d' ) &lt;= DATE_FORMAT(#{p3.over_time,jdbcType=DATE}, '%Y%m%d' ) or #{p3.over_time,jdbcType=DATE} is null)"
            + "    and (t.settlement_status = #{p3.settlement_status} or #{p3.settlement_status} is null or #{p3.settlement_status} = '')   "
            + "    and (t.audit_status = #{p3.audit_status} or #{p3.audit_status} is null or #{p3.audit_status} = '')   "
            // 正在备份的 不能重复备份
//            + "    and (t5.flag != 'ING' OR t5.flag IS NULL)                                                            "
            + "  <if test='p3.ids != null and p3.ids.size!=0' >                                                         "
            + "    and t.id in                                                                                          "
            + "        <foreach collection='p3.ids' item='item' index='index' open='(' separator=',' close=')'>         "
            + "         #{item}                                                                                         "
            + "        </foreach>                                                                                       "
            + "   </if>                                                                                                 "
//            + "  	LIMIT #{p1}, #{p2}                                                                                  "
            + "</script>"
    )
    List<BBkMonitorLogDetailVo> selectData2Mq(@Param("p3") BBkMonitorVo param);

    @Select("select 1 from b_monitor where id = #{p1.monitor_id} for update")
    List<Integer> selectForUpdate(@Param("p1") BBkMonitorLogDetailVo vo);

    @Select(" <script>   "
            + "		SELECT                                                                                              "
            + "			t1.id,                                                                                          "
            + "			t1.url source_file_url,                                                                         "
            + "			t1.file_name,                                                                                   "
            + "			t1.file_size source_file_size                                                                   "
            + "		FROM                                                                                                "
            + "			s_file_info t1                                                                                  "
            + "		WHERE t1.id in                                                                                      "
            + "  <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>                   "
            + "    #{item}                                                                                              "
            + "  </foreach>                                                                                             "
            + "    </script>  ")
    List<SBackupLogVo> selectBackupFileList(@Param("p1") List<Integer> allFileId);
}
