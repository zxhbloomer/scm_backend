package com.xinyirun.scm.core.api.mapper.business.largescreen;

import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiTodayQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiWarehouseStatisticsVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BContractReportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApiLargeScreenReportMapper {

    @Select({
            " select tt8.warehouse_type, tt8.warehouse_type_name as name, (in_qty - out_qty + qty_diff) as value from ( "
                    + "	SELECT                                                                                         "
                    + "		ifnull(sum(tt3.in_qty), 0) in_qty,                                                         "
                    + "		ifnull(sum(tt4.out_qty), 0) out_qty,                                                       "
                    + "		ifnull(sum(tt5.qty_diff), 0) qty_diff,                                                     "
                    + "     tt7.warehouse_type,                                                                        "
                    + "  CASE tt8.dict_value                                                                          "
                    + "  		WHEN '" + DictConstant.DICT_M_WAREHOUSE_TYPE_ZZ + "' THEN '中转港'                        "
                    + "  		WHEN '" + DictConstant.DICT_M_WAREHOUSE_TYPE_TL + "' THEN '铁路码头库'                     "
                    + "  		ELSE tt8.label END warehouse_type_name                                                  "
                    + "	FROM                                                                                           "
                    + "		(                                                                                          "
                    + "		SELECT                                                                                     "
                    + "			tt1.warehouse_id,                                                                      "
                    + "			tt1.goods_id                                                                           "
                    + "		FROM                                                                                       "
                    + "			(                                                                                      "
                    + "			SELECT                                                                                 "
                    + "				t.warehouse_id,                                                                    "
                    + "				t1.goods_id                                                                        "
                    + "			FROM                                                                                   "
                    + "				b_in t                                                                             "
                    + "				LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                      "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_IN_STATUS_TWO + "'                              "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "         UNION ALL                                                                              "
                    + "			SELECT                                                                                 "
                    + "				t.warehouse_id,                                                                    "
                    + "				t1.goods_id                                                                        "
                    + "			FROM                                                                                   "
                    + "				b_out t                                                                            "
                    + "				LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                      "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                             "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "         UNION ALL                                                                              "
                    + "			SELECT                                                                                 "
                    + "				t.warehouse_id,                                                                    "
                    + "				t1.goods_id                                                                        "
                    + "			FROM                                                                                   "
                    + "				b_adjust_detail t                                                                  "
                    + "				LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                      "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_ADJUST_STATUS_PASSED + "'                          "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "			) tt1                                                                                  "
                    + "     WHERE true                                                                                 "
                    + "		GROUP BY                                                                                   "
                    + "			tt1.warehouse_id,                                                                      "
                    + "			tt1.goods_id                                                                           "
                    + "		) tt2                                                                                      "
                    + "		LEFT JOIN (                                                                                "
                    + "		SELECT                                                                                     "
                    + "			t.warehouse_id,                                                                        "
                    + "			t1.goods_id,                                                                           "
                    + "			sum( actual_weight ) in_qty                                                            "
                    + "		FROM                                                                                       "
                    + "			b_in t                                                                                 "
                    + "			LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                          "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_IN_STATUS_TWO + "'                              "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "		GROUP BY t.warehouse_id, t1.goods_id                                                       "
                    + "		) tt3 ON tt2.warehouse_id = tt3.warehouse_id                                               "
                    + "		AND tt2.goods_id = tt3.goods_id                                                            "
                    + "		left join                                                                                  "
                    + "		(                                                                                          "
                    + "		SELECT                                                                                     "
                    + "			t.warehouse_id,                                                                        "
                    + "			t1.goods_id,                                                                           "
                    + "			sum( actual_weight ) out_qty                                                           "
                    + "		FROM                                                                                       "
                    + "			b_out t                                                                                "
                    + "			LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                          "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                             "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "		GROUP BY t.warehouse_id, t1.goods_id                                                       "
                    + "		) tt4 on tt2.warehouse_id = tt4.warehouse_id                                               "
                    + "		AND tt2.goods_id = tt4.goods_id                                                            "
                    + "		left JOIN                                                                                  "
                    + "		(                                                                                          "
                    + "		SELECT                                                                                     "
                    + "			t.warehouse_id,                                                                        "
                    + "			t1.goods_id,                                                                           "
                    + "			sum( qty_diff ) qty_diff                                                               "
                    + "		FROM                                                                                       "
                    + "			b_adjust_detail t                                                                      "
                    + "			LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                          "
                    + "		WHERE                                                                                      "
                    + "			t.STATUS = '" + DictConstant.DICT_B_ADJUST_STATUS_PASSED + "'                          "
                    + "     and (DATE_FORMAT(t.e_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')             "
                    + "		GROUP BY t.warehouse_id, t1.goods_id                                                       "
                    + "		) tt5 on tt2.warehouse_id = tt5.warehouse_id                                               "
                    + "		AND tt2.goods_id = tt5.goods_id                                                            "
                    + "		left join m_goods tt6 on tt2.goods_id = tt6.id                                             "
                    + "		left join m_warehouse tt7 on tt2.warehouse_id = tt7.id                                     "
                    + "     left join s_dict_data tt8 ON tt7.warehouse_type = tt8.dict_value AND tt8.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "'"
                    + "     where tt7.warehouse_type != '" + DictConstant.DICT_M_WAREHOUSE_TYPE_CL + "'                  "
                    + "     group by tt7.warehouse_type                                                                "
                    + "    ) tt8                                                                                       "
    })
    List<ApiWarehouseStatisticsVo> getWarehouseTypeInventory(@Param("p1") String batchDate);

    @Select({"SELECT                                                                                          "
            + " t5.name goods_prop,                                                                            "
            + " t3.name goods_name,                                                                            "
            + "  SUM(t.contract_num) qty                                                                       "
            + " FROM                                                                                          "
            + "    b_in_order t                                                                               "
            + " LEFT JOIN b_in_order_goods t1 ON t.id = t1.order_id                                           "
            + " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                "
            + " LEFT JOIN m_goods_spec_prop t5 ON t5.id = t2.prop_id                                          "
            + " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                   "
            + " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                    "
            + " WHERE                                                                                         "
            + " t.status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                                 "
            + " and t5.name is not null                                                                       "
            + " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')         "
            + " group by t3.name                                                                              "
    })
    List<BContractReportVo> selectInContractQty(@Param("p1") String batchDate);

    @Select({"<script>                                                                                                 "
            + " SELECT                                                                                               "
            + " t5.name goods_prop,                                                                                   "
            + " t3.name goods_name,                                                                                   "
            + "  SUM(t.contract_num) qty                                                                              "
            + "  FROM                                                                                                 "
            + "    b_out_order t                                                                                      "
            + " LEFT JOIN b_out_order_goods t1 ON t.id = t1.order_id                                                  "
            + " LEFT JOIN m_goods_spec t2 ON t2.id = t1.sku_id                                                        "
            + " LEFT JOIN m_goods t3 ON t3.id = t2.goods_id                                                           "
            + " LEFT JOIN m_owner t4 ON t4.id = t.owner_id                                                            "
            + " LEFT JOIN m_goods_spec_prop t5 ON t5.id = t2.prop_id                                                  "
            + " where t.status != '" + DictConstant.DICT_B_OUT_ORDER_STATUS_ONE + "'                                  "
            + " and t5.name is not null                                                                               "
            + " and (DATE_FORMAT(t.contract_dt, '%Y-%m-%d' ) >= #{p1} or #{p1} is null or #{p1} = '')                      "
            + " group by t3.name                                                                                      "
            + "</script>"
    })
    List<BContractReportVo> selectOutContractQty(@Param("p1") String batchDate);

    /**
     * 查询发货, 收货数量
     *
     * @return
     */
    @Select({
            " SELECT                                                                                           "
                    + " 	sum(ifnull( t20.qty, t21.qty )) AS in_qty,                                                 "
                    + " 	sum(ifnull( t22.qty, t23.qty )) AS out_qty,                                                "
                    + "     sum(if(t.status = '" + DictConstant.DICT_B_MONITOR_STATUS_FOUR + "', ifnull(t22.qty, t23.qty), 0)) as qty_loss,"
                    + "     count(t.id) num                                                                            "
                    + " FROM                                                                                           "
                    + " 	b_monitor t                                                                                "
                    + " 	LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                        "
                    + " 	LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                    "
                    + " 	LEFT JOIN b_monitor_out t22 ON t22.monitor_id = t.id                                       "
                    + " 	LEFT JOIN b_monitor_delivery t23 ON t23.monitor_id = t.id                                  "
                    + " WHERE t.STATUS IN ( 1, 2, 3, 4, 5, 6, 7 )                                                      "
                    + "    and DATE_FORMAT(t.c_time, '%Y%m%d' ) = DATE_FORMAT(now(), '%Y%m%d' )                      "
    })
    BQtyLossScheduleReportVo getScheduleStatistics();

//    @Select(""
//            + "  SELECT                                                                                               "
//            + "  	SUM( tt.wo_qty ) qty_product_today                                                                  "
//            + "  FROM                                                                                                 "
//            + "  	(                                                                                                   "
//            + "  	SELECT                                                                                              "
//            + "  		IFNULL( SUM( t1.wo_qty ), 0 ) wo_qty                                                            "
//            + "  	FROM                                                                                                "
//            + "  		b_wo t                                                                                          "
//            + "  		LEFT JOIN b_wo_product t1 ON t.id = t1.wo_id                                                    "
//            + "  		AND t1.type = '1'                                                                               "
//            + "  	WHERE                                                                                               "
//            + "  		t.`status` = '3'                                                                                "
//            + "  		AND DATE_FORMAT( t.e_time, '%Y%m%d' ) = DATE_FORMAT( now(), '%Y%m%d' ) UNION ALL                "
//            + "  	SELECT                                                                                              "
//            + "  		IFNULL( SUM( t1.wo_qty ), 0 ) wo_qty                                                            "
//            + "  	FROM                                                                                                "
//            + "  		b_rt_wo t                                                                                       "
//            + "  		LEFT JOIN b_rt_wo_product t1 ON t.id = t1.wo_id                                                 "
//            + "  		AND t1.type = '1'                                                                               "
//            + "  	WHERE                                                                                               "
//            + "  		t.`status` = '3'                                                                                "
//            + "  	AND DATE_FORMAT( t.e_time, '%Y%m%d' ) = DATE_FORMAT( now(), '%Y%m%d' )                              "
//            + "  	) tt                                                                                                "
//    )
    @Select(""
            + "  SELECT                                                                                                 "
            + "    	SUM( actual_weight ) qty_product_today                                                              "
            + "  FROM b_in t                                                                                            "
            + "  LEFT JOIN m_goods_spec t1 ON t.sku_id = t1.id                                                          "
            + "  LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                            "
            + "  WHERE t.type = '" + DictConstant.DICT_B_IN_TYPE_SC + "'                                                "
            + "  AND t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                                       "
            + "  AND DATE_FORMAT(t.e_dt, '%Y%m%d') =  DATE_FORMAT(NOW(), '%Y%m%d')                                      "
            + "  AND t2.code NOT IN ('"+ SystemConstants.PRODUCT_COMM_CODE.COMM_RICE_HULL_CODE +"',                     "
            + "  '"+ SystemConstants.PRODUCT_COMM_CODE.COMM_IMPURITIES +"')                                             "
    )
    ApiTodayQtyStatisticsVo selectProductStatistics();

    /**
     * 查询 监管任务 预警数量
     *
     * @return
     */
    @Select("SELECT ifnull(count(id), 0) from b_message where serial_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_MONITOR + "'      ")
    Integer selectMonitorAlarmCount();


    /**
     * 查询采购订单数量
     *
     * @return
     */
    @Select(" SELECT ifnull(count(id), 0) purchase_order_count, ifnull(sum(contract_num), 0) purchase_contract_num from b_in_order where DATE_FORMAT(contract_dt, '%Y%m%d') = DATE_FORMAT(now(), '%Y%m%d') and status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'")
    ApiTodayQtyStatisticsVo selectInOrderNum();

    /**
     * 查询销售订单数量
     *
     * @return
     */
    @Select(" SELECT ifnull(count(id), 0) sales_order_count, ifnull(sum(contract_num), 0) sales_coontract_num from b_out_order where DATE_FORMAT(contract_dt, '%Y%m%d') = DATE_FORMAT(now(), '%Y%m%d') and status != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'")
    ApiTodayQtyStatisticsVo selectOutOrderNum();

    @Select(""
            + "  SELECT                                                                                                "
            + "  CASE t.dict_value                                                                                     "
            + "  		WHEN '" + DictConstant.DICT_M_WAREHOUSE_TYPE_ZZ + "' THEN '中转港'                                 "
            + "  		WHEN '" + DictConstant.DICT_M_WAREHOUSE_TYPE_TL + "' THEN '铁路码头库'                              "
            + "  		ELSE t.label END name,                                                           "
            + "  	count(1) as value,                                                                                  "
            + "  	t.dict_value as warehouse_type                                                                      "
            + "  FROM                                                                                                  "
            + "  	s_dict_data t                                                                                       "
            + "  	LEFT JOIN m_warehouse t1 ON t.`code` = 'm_warehouse_type'                                           "
            + "  	AND t.dict_value = t1.warehouse_type                                                                "
            + "  WHERE                                                                                                 "
            + "  	t.`code` = 'm_warehouse_type'                                                                       "
            + "  	AND t.dict_value != '" + DictConstant.DICT_M_WAREHOUSE_TYPE_CL + "'                                   "
            // 已启用的
            + "    AND t1.enable = '" + SystemConstants.ENABLE_TRUE + "'                                                 "
            + "  GROUP BY                                                                                              "
            + "  	t.dict_value                                                                                        "
    )
    List<ApiWarehouseStatisticsVo> selectWarehouseNumByType();

    /**
     * 查询当天原粮出库数量
     *
     * @return
     */
    @Select(""
            + "  SELECT                                                                                                "
            + "  	ifnull(SUM( tab2.actual_count ), 0)                                                                "
            + "  FROM                                                                                                  "
            + "  	(                                                                                                  "
            + "  	SELECT                                                                                             "
            + "  		t.id,                                                                                          "
            + "  		t1.order_id                                                                                    "
            + "  	FROM                                                                                               "
            + "  		b_out t                                                                                        "
            + "  		LEFT JOIN b_out_plan_detail t1 ON t.plan_detail_id = t1.id                                     "
            + "  	WHERE                                                                                              "
            + "  		t1.order_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER + "'                           "
            + "  		AND t.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                               "
            + "  		AND DATE_FORMAT( t.e_dt, '%Y%m%d' ) = DATE_FORMAT( NOW(), '%Y%m%d' ) UNION                     "
            + "  	SELECT                                                                                             "
            + "  		t3.id,                                                                                         "
            + "  		t4.order_id                                                                                    "
            + "  	FROM                                                                                               "
            + "  		b_out t3                                                                                       "
            + "  		LEFT JOIN b_out_extra t4 ON t3.id = t4.out_id                                                  "
            + "  	WHERE                                                                                              "
            + "  		t4.order_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER + "'                           "
            + "  		AND t3.`status` = '" + DictConstant.DICT_B_OUT_STATUS_PASSED + "'                              "
            + "  		AND DATE_FORMAT( t3.e_dt, '%Y%m%d' ) = DATE_FORMAT( NOW(), '%Y%m%d' )                          "
            + "  	) tab1                                                                                             "
            + "  	LEFT JOIN b_out tab2 ON tab1.id = tab2.id                                                          "
            + "  	LEFT JOIN m_warehouse tab3 ON tab2.warehouse_id = tab3.id                                          "
            + "  	LEFT JOIN b_in_order tab4 ON tab4.id = tab1.order_id                                               "
            + "  WHERE                                                                                                 "
            + "  	tab3.warehouse_type = '" + DictConstant.DICT_M_WAREHOUSE_TYPE_ZX + "'                              "
            + "  	AND tab4.`status` != '" + DictConstant.DICT_B_IN_ORDER_STATUS_ONE + "'                             "
    )
    BigDecimal selectRowGrainNum();

    /**
     * 查询交付数量  收货地址饲料厂,状态卸货完成的监管任务
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "    ifnull(SUM(IFNULL(t20.qty,t21.qty)), 0)                                                             "
            +  "  FROM b_monitor t                                                                                      "
            +  "  LEFT JOIN b_schedule t1 ON t.schedule_id = t1.id                                                      "
            +  "  LEFT JOIN b_monitor_in t20 ON t20.monitor_id = t.id                                                   "
            +  "  LEFT JOIN b_monitor_unload t21 ON t21.monitor_id = t.id                                               "
            +  "  LEFT JOIN m_warehouse t2 ON t1.in_warehouse_id = t2.id                                                "
            +  "  WHERE t.`status` = '"+ DictConstant.DICT_B_MONITOR_STATUS_SEVEN +"'                                   "
            +  "  AND t2.warehouse_type = '" + DictConstant.DICT_M_WAREHOUSE_TYPE_CL+ "'                                "
            +  "  AND DATE_FORMAT(t.c_time,'%Y%m%d') = DATE_FORMAT(now(),'%Y%m%d')                                     "
    )
    BigDecimal selectDeliverCount();

}
