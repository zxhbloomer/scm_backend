package com.xinyirun.scm.core.system.mapper.query.largescreen;

import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsLargeScreenVo;
import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsMonitorLargeScreenVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/9/27 14:42
 */

@Repository
public interface LogisticsLargeScreenMapper {

    @Select(""
            + "  SELECT                                                                                                 "
            + "  	SUM( actual_weight )                                                                                "
            + "  FROM b_out t                                                                                           "
            + "  LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                                     "
            + "  WHERE t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_ZX +"'                                "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                         "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
    )
    BigDecimal selectRowGrainCount(@Param("p1")String batch);

    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(t.actual_count)                                                                                  "
            + "  FROM b_out t                                                                                           "
            + "  LEFT JOIN b_out_plan_detail t3 ON t.plan_detail_id = t3.id                                             "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                       "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
            + "  AND exists (                                                                                           "
            + "   SELECT                                                                                                "
            + "   1                                                                                                     "
            + "   FROM b_release_order t1                                                                               "
            + "   LEFT JOIN b_out_plan t2 ON t2.release_order_code = t1.`code`                                          "
            + "   LEFT JOIN b_out_plan_detail t3 ON t2.id = t3.plan_id                                                  "
            + "   LEFT JOIN b_out_order t4 ON t3.order_id = t4.id AND t3.order_type = 'b_out_order'                     "
            + "   WHERE t4.`status` != '"+ DictConstant.DICT_B_OUT_ORDER_STATUS_ONE +"'                                 "
//            + "  AND (DATE_FORMAT(t1.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                       "
            + "   AND t.plan_detail_id = t3.id                                                                          "
            + "  )                                                                                                      "
    )
    BigDecimal selectPurchaseOutQty(@Param("p1") String batchDate);

    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(actual_count)                                                                                    "
            + "  FROM b_in t                                                                                            "
            + "  LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + "  LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_IN_STATUS_PASSED +"'                                        "
            + "  AND t.type = '"+ DictConstant.DICT_B_IN_TYPE_SC +"'                                                    "
            + "  AND (t.e_dt_date >= #{p1} or #{p1} is null or #{p1} = '')                                              "
            + "  AND t2.code NOT IN ('"+ SystemConstants.PRODUCT_COMM_CODE.COMM_RICE_HULL_CODE +"',                     "
            + "  '"+ SystemConstants.PRODUCT_COMM_CODE.COMM_IMPURITIES +"')                                             "
    )
    BigDecimal selectProductQty(@Param("p1") String batchDate);

    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(IFNULL(t3.qty,t4.qty))                                                                           "
            + "  FROM b_monitor t                                                                                       "
            + "  LEFT JOIN b_schedule t1 ON t.schedule_id = t1.id                                                       "
            + "  LEFT JOIN m_warehouse t2 ON t1.in_warehouse_id = t2.id                                                 "
            + "  LEFT JOIN b_monitor_in t3 ON t3.monitor_id = t.id                                                      "
            + "  LEFT JOIN b_monitor_unload t4 ON t4.monitor_id = t.id                                                  "
            + "  WHERE t2.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_CL +"'                                "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                      "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
    )
    BigDecimal selectFeedQty(@Param("p1") String batchDate);

    /**
     * 累计派车次数
     * @param batchDate
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` in (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_ONE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"',      "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_TWO +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"')       "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
    )
    int selectMonitorCount(@Param("p1") String batchDate);

    /**
     * 在途车次数量
     * @param batchDate
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` in (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"')                                                       "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
    )
    int selectInTransitMonitor(@Param("p1") String batchDate);

    /**
     * 累计完成车次
     * @param batchDate
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                    "
            + "  AND (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                        "
    )
    int selectCompleteMonitor(@Param("p1") String batchDate);

    /**
     * 当月完成车次
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                    "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                        "
    )
    int selectMonthCompleteMonitor();

    /**
     * 异常车次
     * @param batchDate
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_message t                                                                                       "
            + "  INNER JOIN b_monitor t1 ON t.serial_id = t1.id and t.serial_type = 'b_monitor'                         "
            + "  WHERE t.serial_type = 'b_monitor'                                                                        "
            + "  AND (DATE_FORMAT(t1.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                       "
    )
    int selectUnusualMonitor(@Param("p1") String batchDate);

    /**
     * 当月派车次数
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` in (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_ONE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"',      "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_TWO +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"')       "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                        "
    )
    int selectMonthMonitor();

    /**
     * 物流合同相关
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                                        "
            +  "    tab.carriage_num,                                                                                                           "
            +  "  	tab.complete_carriage_num,                                                                                                  "
            +  "  	tab.in_transit_carriage_num,                                                                                                "
            +  "  	tab.customer_count,                                                                                                         "
            +  "  	(tab.carriage_num - tab.complete_carriage_num - tab.in_transit_carriage_num) predict_carriage_num,                          "
            +  "  	tab.loss_carriage_num,                                                                                                      "
            +  "  	(tab.complete_carriage_num / tab.carriage_num) * 100 carriage_complete_processing                                                 "
            +  "  FROM                                                                                                                          "
            +  "  (                                                                                                                             "
            +  "  SELECT                                                                                                                        "
            +  "  	SUM( t.out_schedule_qty ) carriage_num,                                                                                     "
            +  "  	SUM( t4.out_qty ) complete_carriage_num,                                                                                    "
            +  "  	SUM( t8.in_transit_qty ) in_transit_carriage_num,                                                                           "
            +  "  	COUNT( DISTINCT t9.customer_id ) customer_count,                                                                            "
            +  "  	SUM(t4.qty_loss) loss_carriage_num                                                                                          "
            +  "  FROM                                                                                                                          "
            +  "  	b_schedule t                                                                                                                "
            +  "  	LEFT JOIN (                                                                                                                 "
            +  "  	SELECT                                                                                                                      "
            +  "  		SUM(IFNULL( t2.qty, t3.qty )) out_qty,                                                                                  "
            +  "  		SUM(                                                                                                                    "
            +  "  		CASE                                                                                                                    "
            +  "  				WHEN IFNULL( t10.type, t11.type ) = '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"' THEN 0 ELSE                "
            +  "  			IF(ifnull( t2.qty, t3.qty ) <= ifnull( t10.qty, t11.qty ),0,                                                        "
            +  "  				ifnull( ifnull( t2.qty, t3.qty ) - ifnull( t10.qty, t11.qty ), 0 ))                                             "
            +  "  			END                                                                                                                 "
            +  "  			) AS qty_loss,                                                                                                      "
            +  "  			t1.schedule_id                                                                                                      "
            +  "  		FROM                                                                                                                    "
            +  "  			b_monitor t1                                                                                                        "
            +  "  			LEFT JOIN b_monitor_out t2 ON t2.monitor_id = t1.id                                                                 "
            +  "  			LEFT JOIN b_monitor_delivery t3 ON t3.monitor_id = t1.id                                                            "
            +  "  			LEFT JOIN b_monitor_in t10 ON t10.monitor_id = t1.id                                                                "
            +  "  			LEFT JOIN b_monitor_unload t11 ON t11.monitor_id = t1.id                                                            "
            +  "  		WHERE                                                                                                                   "
            +  "  			t1.`status` = '"+DictConstant.DICT_B_MONITOR_STATUS_SEVEN+"'                                                        "
            +  "  		GROUP BY                                                                                                                "
            +  "  			t1.schedule_id                                                                                                      "
            +  "  		) t4 ON t4.schedule_id = t.id                                                                                           "
            +  "  		LEFT JOIN (                                                                                                             "
            +  "  		SELECT                                                                                                                  "
            +  "  			t5.schedule_id,                                                                                                     "
            +  "  			SUM(                                                                                                                "
            +  "  			IFNULL( t6.qty, t7.qty )) in_transit_qty                                                                            "
            +  "  		FROM                                                                                                                    "
            +  "  			b_monitor t5                                                                                                        "
            +  "  			LEFT JOIN b_monitor_out t6 ON t6.monitor_id = t5.id                                                                 "
            +  "  			LEFT JOIN b_monitor_delivery t7 ON t7.monitor_id = t5.id                                                            "
            +  "  		WHERE                                                                                                                   "
            +  "  			t5.`status` IN ('"+DictConstant.DICT_B_MONITOR_STATUS_FOUR+"', '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"',     "
            +  "                             '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"')                                                    "
            +  "  		GROUP BY                                                                                                                "
            +  "  			t5.schedule_id                                                                                                      "
            +  "  		) t8 ON t8.schedule_id = t.id                                                                                           "
            +  "  		LEFT JOIN b_schedule_info t9 ON t.id = t9.schedule_id                                                                   "
            +  "  	WHERE                                                                                                                       "
            +  "  		t.`status` IN ('"+ DictConstant.DICT_B_SCHEDULE_STATUS_ZERO +"', '"+ DictConstant.DICT_B_SCHEDULE_STATUS_ONE +"' )      "
            +  "  	AND  (DATE_FORMAT(t.c_time, '%Y-%m-%d') >= #{p1} or #{p1} is null or #{p1} = '')                       "
            +  "  	) tab                                                                                                                       "
    )
    LogisticsLargeScreenVo querySchedule(@Param("p1") String batchDate);

    /**
     * 承运商数量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(DISTINCT t.company_name)                                                                       "
            + "  FROM b_carriage_order t                                                                                "
            + "  WHERE t.company_name IS NOT NULL                                                                       "
            + "  AND t.`status` IN  ('执行中', '已完成')                                                                   "
            + "  AND (t.`sign_dt` >= #{p1} or #{p1} is null or #{p1} = '')                                              "
    )
    int selectCustomerCount(@Param("p1") String batchDate);

    /**
     * 合同逾期数量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_carriage_order t                                                                                "
            + "  WHERE deadline_dt < DATE_FORMAT(NOW(), '%Y-%m-%d')                                                     "
            + "  AND (t.`sign_dt` >= #{p1} or #{p1} is null or #{p1} = '')                                              "
            + "  AND t.`status` = '执行中'                                                                               "
    )
    int selectContractOverDueCount(@Param("p1") String batchDate);

    /**
     * 当月完成运输量和损耗
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                   "
            +  "  	SUM(                                                                                                   "
            +  "  	IFNULL( t2.qty, t3.qty )) out_qty,                                                                     "
            +  "  	SUM(                                                                                                   "
            +  "  	CASE                                                                                                   "
            +  "  			                                                                                               "
            +  "  			WHEN IFNULL( t10.type, t11.type ) = '"+ SystemConstants.MONITOR.B_MONITOR_UNLOAD +"' THEN      "
            +  "  			0 ELSE                                                                                         "
            +  "  		IF                                                                                                 "
            +  "  			(                                                                                              "
            +  "  				ifnull( t2.qty, t3.qty ) <= ifnull( t10.qty, t11.qty ),                                    "
            +  "  				0,                                                                                         "
            +  "  			ifnull( ifnull( t2.qty, t3.qty ) - ifnull( t10.qty, t11.qty ), 0 ))                            "
            +  "  		END                                                                                                "
            +  "  		) AS qty_loss                                                                                    "
            +  "  	FROM                                                                                                   "
            +  "  		b_schedule t                                                                                       "
            +  "  		LEFT JOIN b_monitor t1 ON t1.schedule_id = t.id                                                    "
            +  "  		LEFT JOIN b_monitor_out t2 ON t2.monitor_id = t1.id                                                "
            +  "  		LEFT JOIN b_monitor_delivery t3 ON t3.monitor_id = t1.id                                           "
            +  "  		LEFT JOIN b_monitor_in t10 ON t10.monitor_id = t1.id                                               "
            +  "  		LEFT JOIN b_monitor_unload t11 ON t11.monitor_id = t1.id                                           "
            +  "  	WHERE                                                                                                  "
            +  "  		t1.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN+"'                                      "
            +  "  	AND DATE_FORMAT( t1.c_time, '%Y%m' ) = DATE_FORMAT(NOW(), '%Y%m')                                       "
            +  "  	AND t.is_delete = '0'                                                                                  "
    )
    Map<String, BigDecimal> selectCompleteAndLossCarriage();

    /**
     * 当月饲料厂收货量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    sum(ifnull( t20.qty, t21.qty )) AS in_qty                                                            "
            + "  FROM b_monitor t                                                                                       "
            + "  LEFT JOIN b_schedule t1 ON t.schedule_id = t1.id                                                       "
            + "  LEFT JOIN m_warehouse t2 ON t1.in_warehouse_id = t2.id                                                 "
            + "  LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                                    "
            + "  LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                                "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                    "
            + "  AND t2.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_CL +"'                                  "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                        "
    )
    BigDecimal selectMonthFeedInQty();

    /**
     * 当月加工厂生产量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    	SUM( actual_count )                                                                                 "
            + "  FROM b_in t                                                                                            "
            + "  LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + "  LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + "  WHERE t.type = '"+ DictConstant.DICT_B_IN_TYPE_SC +"'                                                  "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_IN_STATUS_PASSED +"'                                          "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                          "
            + "  AND t2.code NOT IN ('"+ SystemConstants.PRODUCT_COMM_CODE.COMM_RICE_HULL_CODE +"',                     "
            + "  '"+ SystemConstants.PRODUCT_COMM_CODE.COMM_IMPURITIES +"')                                             "
    )
    BigDecimal selectMonthProductQty();

    /**
     * 当月原粮出库量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "  	SUM( actual_weight )                                                                                "
            + "  FROM b_out t                                                                                           "
            + "  LEFT JOIN m_warehouse t1 ON t.warehouse_id = t1.id                                                     "
            + "  WHERE t1.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_ZX +"'                                "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                         "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                          "
    )
    BigDecimal selectMonthRawGrainOutQty();

    /**
     * 当月销售出库量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(t.actual_weight)                                                                                 "
            + "  FROM b_out t                                                                                           "
            + "  LEFT JOIN b_out_plan_detail t3 ON t.plan_detail_id = t3.id                                             "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                       "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m') =  DATE_FORMAT(NOW(), '%Y%m')                                          "
            + "  AND exists (                                                                                           "
            + "   SELECT                                                                                                "
            + "   1                                                                                                     "
            + "   FROM b_release_order t1                                                                               "
            + "   LEFT JOIN b_out_plan t2 ON t2.release_order_code = t1.`code`                                          "
            + "   LEFT JOIN b_out_plan_detail t3 ON t2.id = t3.plan_id                                                  "
            + "   LEFT JOIN b_out_order t4 ON t3.order_id = t4.id AND t3.order_type = 'b_out_order'                     "
            + "   WHERE t4.`status` != '-1'                                                                             "
            + "   AND t.plan_detail_id = t3.id                                                                          "
            + "  )                                                                                                      "
    )
    BigDecimal selectMonthSalesOutQty();

    /**
     * 当天派车数
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` in (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_ONE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"',      "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_TWO +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"')       "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                  "
    )
    int selectDailyMonitorCount();

    /**
     * 当天完成派车数
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                    "
            + "  AND DATE_FORMAT(t.in_time, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                   "
    )
    int selectDailyCompleteMonitorCount();

    /**
     * 当天在途车次
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    count(1)                                                                                             "
            + "  FROM b_monitor t                                                                                       "
            + "  WHERE t.`status` in (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"')                                                       "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                    "
    )
    int selectDailyInTransitMonitorCount();

    /**
     * 当天 销售出库量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(t.actual_weight)                                                                                 "
            + "  FROM b_out t                                                                                           "
            + "  LEFT JOIN b_out_plan_detail t3 ON t.plan_detail_id = t3.id                                             "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                       "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                      "
            + "  AND exists (                                                                                           "
            + "   SELECT                                                                                                "
            + "   1                                                                                                     "
            + "   FROM b_release_order t1                                                                               "
            + "   LEFT JOIN b_out_plan t2 ON t2.release_order_code = t1.`code`                                          "
            + "   LEFT JOIN b_out_plan_detail t3 ON t2.id = t3.plan_id                                                  "
            + "   LEFT JOIN b_out_order t4 ON t3.order_id = t4.id AND t3.order_type = 'b_out_order'                     "
            + "   WHERE t4.`status` != '-1'                                                                             "
            + "   AND t.plan_detail_id = t3.id                                                                          "
            + "  )                                                                                                      "
    )
    BigDecimal selectDailySalesQty();

    /**
     * 当天生产数量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    	SUM( actual_weight )                                                                                "
            + "  FROM b_in t                                                                                            "
            + "  LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + "  LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + "  WHERE t.type = '"+ DictConstant.DICT_B_IN_TYPE_SC +"'                                                  "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"'                                         "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                      "
            + "  AND t2.code NOT IN ('"+ SystemConstants.PRODUCT_COMM_CODE.COMM_RICE_HULL_CODE +"',                     "
            + "  '"+ SystemConstants.PRODUCT_COMM_CODE.COMM_IMPURITIES +"')                                             "
    )
    BigDecimal selectDailyProductQty();

    /**
     * 当天收货数量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(IFNULL(t3.qty,t4.qty))                                                                           "
            + "  FROM b_monitor t                                                                                       "
            + "  LEFT JOIN b_schedule t1 ON t.schedule_id = t1.id                                                       "
            + "  LEFT JOIN m_warehouse t2 ON t1.in_warehouse_id = t2.id                                                 "
            + "  LEFT JOIN b_monitor_in t3 ON t3.monitor_id = t.id                                                      "
            + "  LEFT JOIN b_monitor_unload t4 ON t4.monitor_id = t.id                                                  "
            + "  WHERE t2.warehouse_type = '"+ DictConstant.DICT_M_WAREHOUSE_TYPE_CL +"'                                "
            + "  AND t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                      "
            + "  AND DATE_FORMAT(t.in_time, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                   "
    )
    BigDecimal selectDailyInQty();

    /**
     * 当天收货数量
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                 "
            + "     t.`code`,                                                                                           "
            + "  	t1.no vehicle_no,                                                                                   "
            + "     @row_num:= @row_num+ 1 as no,                                                                       "
            + "  	t2.label status_name                                                                                "
            + "  FROM b_monitor t                                                                                       "
            + "  LEFT JOIN m_vehicle t1 ON t.vehicle_id = t1.id                                                         "
            + "  LEFT JOIN s_dict_data t2 ON t.`status` = t2.dict_value AND t2.`code` = 'b_monitor_status'              "
            + "  ,(select @row_num:=0) t3                                                                               "
            + "  WHERE t.`status` IN (                                                                                  "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_ZERO +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_ONE +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_TWO +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_FOUR +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_FIVE +"', '"+ DictConstant.DICT_B_MONITOR_STATUS_SIX +"',       "
            + "  '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"')                                                      "
            + "  AND DATE_FORMAT(t.c_time, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                   "
            + "  ORDER BY t.u_time DESC                                                                                "
            + "  limit 30                                                                                              "
    )
    List<LogisticsMonitorLargeScreenVo> queryMonitor();



    @Select(""
            + "  SELECT                                                                                                 "
            + "    SUM(actual_count)                                                                                    "
            + "  FROM b_in t                                                                                            "
            + "  LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + "  LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + "  WHERE t.`status` = '"+ DictConstant.DICT_B_IN_STATUS_PASSED +"'                                        "
            + "  AND t.type = '"+ DictConstant.DICT_B_IN_TYPE_SC +"'                                                    "
            + "  AND (t.e_dt_date >= #{p1} or #{p1} is null or #{p1} = '')                                              "
            + "  AND t2.code = #{p2}                                                                                    "
    )
    BigDecimal selectProductQtyByCode(@Param("p1") String batchDate,@Param("p2") String code);

}
