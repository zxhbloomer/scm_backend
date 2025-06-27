package com.xinyirun.scm.core.system.mapper.sys.schedule.v6;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderInvertedEntity;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-08-20
 */
public interface SBOrderInvertedMapper extends BaseMapper<BOrderInvertedEntity> {


    /**
     * 查询前一天的 实时 数据
     */
    @Select("	SELECT                                                                                                                                          "
            +"		CONCAT(substring( t1.contract_no, 6, 2 ), '-', substring( t1.contract_no, 8, 2 ),'-',                                                       "
            +"        substring( t1.contract_no, 10, 2 ),'-',t3.warehouse_id) AS export_code,                                                                   "
            +"		CONCAT(substring( t1.contract_no, 6, 2 ), '-', substring( t1.contract_no, 8, 2 ),'-',                                                       "
            +"        substring( t1.contract_no, 10, 2 )) AS auction_date,                                                                                      "
            +"		t3.audit_dt AS opening_date,                                                                                                                "
            +"		t1.contract_no AS contract_no,                                                                                                              "
            +"		DATE_ADD( t2.contract_expire_dt, INTERVAL - 7 DAY ) AS delivery_due_date,                                                                   "
            +"		t3.NAME AS warehouse_name,                                                                                                                  "
            +"		t1.contract_num AS contract_quantity,                                                                                                       "
            +"		IFNULL( t3.actual_count, 0 ) AS actual_quantity,                                                                                            "
            +"		IFNULL( t3.actual_count, 0 )- IFNULL( t5.actual_count, 0 ) AS remaining_quantity,                                                           "
            +"		IFNULL( t4.actual_count, 0 ) AS actual_daily_quantity,                                                                                      "
            +"		IFNULL( t5.actual_count, 0 ) AS accumulated_out_quantity,                                                                                   "
            +"		IF(DATEDIFF(DATE_ADD(t2.contract_expire_dt, INTERVAL - 7 DAY ),DATE_ADD(NOW(), INTERVAL - 1 DAY )) <= 0,0,                                  "
            +"			IFNULL(DATEDIFF(DATE_ADD(t2.contract_expire_dt, INTERVAL - 7 DAY ),DATE_ADD(NOW(), INTERVAL - 1 DAY )),0)) AS plan_out_days,            "
            +"		IF((IFNULL( t3.actual_count, 0 )- IFNULL( t5.actual_count, 0 )) <= 0.5,                                                                     "
            +"			ifnull(DATEDIFF( t5.e_dt, t3.audit_dt )+ 1,'-'),'-' ) AS actual_plan_out_days,                                                          "
            +"		CEIL(IF(DATEDIFF(DATE_ADD( t2.contract_expire_dt, INTERVAL - 7 DAY ),NOW()) <= 0,0,                                                         "
            +"			(IFNULL( t3.actual_count, 0 ) - IFNULL( t5.actual_count, 0 ))/ DATEDIFF(                                                                "
            +"			DATE_ADD( t2.contract_expire_dt, INTERVAL - 7 DAY ),NOW()))) AS plan_out_day,                                                           "
            +"		IF(IFNULL( t3.actual_count, 0 ) < 1,0,(IFNULL( t5.actual_count, 0 )/ t3.actual_count) * 100) AS plan_out_speed,                             "
            +"		DATE_ADD( NOW(), INTERVAL - 1 DAY ) AS backups_date,                                                                                        "
            +"		t3.warehouse_id as warehouse_id                                                                                                             "
            +"	FROM                                                                                                                                            "
            +"		b_order t1                                                                                                                                  "
            +"		LEFT JOIN b_in_order t2 ON t1.serial_id = t2.id                                                                                             "
            +"		AND t2.STATUS != '"+ DictConstant.DICT_B_IN_ORDER_STATUS_ONE+"'                                                                             "
            +"		LEFT JOIN (                                                                                                                                 "
            +"		SELECT                                                                                                                                      "
            +"			tab2.order_id,                                                                                                                          "
            +"			tab4.id AS warehouse_id,                                                                                                                "
            +"			tab4.NAME,                                                                                                                              "
            +"			tab2.audit_dt,                                                                                                                          "
            +"			IFNULL( SUM( tab3.actual_count ), 0 ) AS actual_count                                                                                   "
            +"		FROM                                                                                                                                        "
            +"			b_in_plan tab1                                                                                                                          "
            +"			LEFT JOIN b_in_plan_detail tab2 ON tab2.plan_id = tab1.id                                                                               "
            +"			LEFT JOIN b_in tab3 ON tab3.plan_detail_id = tab2.id                                                                                    "
            +"			LEFT JOIN m_warehouse tab4 ON tab4.id = tab2.warehouse_id                                                                               "
            +"		WHERE                                                                                                                                       "
            +"		TRUE                                                                                                                                        "
            +"			AND tab1.type = '"+DictConstant.DICT_B_IN_PLAN_TYPE_CG+"'                                                                               "
            +"			AND tab2.STATUS IN ('"+DictConstant.DICT_B_IN_PLAN_STATUS_TWO +"','"+DictConstant.DICT_B_IN_PLAN_STATUS_FIVE+"')                    "
            +"			AND tab3.STATUS = '"+DictConstant.DICT_B_IN_STATUS_TWO+"'                                                                            "
            +"			AND tab4.warehouse_type = '"+DictConstant.DICT_M_WAREHOUSE_TYPE_ZX+"'                                                                   "
            +"		GROUP BY                                                                                                                                    "
            +"			tab4.id,                                                                                                                                "
            +"			tab2.order_id                                                                                                                           "
            +"		ORDER BY                                                                                                                                    "
            +"			tab2.audit_dt ASC                                                                                                                       "
            +"		) t3 ON t2.id = t3.order_id                                                                                                                 "
            +"		LEFT JOIN (                                                                                                                                 "
            +"		SELECT                                                                                                                                      "
            +"			SUM( tab4.actual_count ) AS actual_count,                                                                                               "
            +"			tab4.warehouse_id,                                                                                                                      "
            +"			tab1.order_id                                                                                                                           "
            +"		FROM                                                                                                                                        "
            +"			b_schedule tab1                                                                                                                         "
            +"			LEFT JOIN b_monitor tab2 ON tab2.schedule_id = tab1.id                                                                                  "
            +"			LEFT JOIN b_monitor_out tab3 ON tab3.monitor_id = tab2.id                                                                               "
            +"			LEFT JOIN b_out tab4 ON tab3.out_id = tab4.id                                                                                           "
            +"			LEFT JOIN m_warehouse tab5 ON tab5.id = tab4.warehouse_id                                                                               "
            +"		WHERE                                                                                                                                       "
            +"		TRUE                                                                                                                                        "
            +"			AND tab5.warehouse_type = '"+DictConstant.DICT_M_WAREHOUSE_TYPE_ZX+"'                                                                   "
            +"			AND tab4.STATUS = '"+DictConstant.DICT_B_OUT_STATUS_PASSED+"'                                                                           "
            +"			AND tab2.STATUS IN ( '"+DictConstant.DICT_B_MONITOR_STATUS_THREE+"',                                                                    "
            +"                '"+DictConstant.DICT_B_MONITOR_STATUS_FOUR+"', '"+DictConstant.DICT_B_MONITOR_STATUS_FIVE+"',                                     "
            +"                '"+DictConstant.DICT_B_MONITOR_STATUS_SIX+"','"+DictConstant.DICT_B_MONITOR_STATUS_SEVEN+"' )                                     "
            +"			AND (date_format( tab4.e_dt, '%Y-%m-%d' ) = date_format(DATE_ADD(NOW(), INTERVAL - 1 DAY ), '%Y-%m-%d'))                                "
            +"		GROUP BY                                                                                                                                    "
            +"			tab5.id,                                                                                                                                "
            +"			tab1.order_id                                                                                                                           "
            +"		) t4 ON t4.order_id = t1.id                                                                                                                 "
            +"		LEFT JOIN (                                                                                                                                 "
            +"		SELECT                                                                                                                                      "
            +"			tab2.order_id,                                                                                                                          "
            +"			tab1.e_dt,                                                                                                                              "
            +"			SUM( tab1.actual_count ) AS actual_count                                                                                                "
            +"		FROM                                                                                                                                        "
            +"			b_out tab1                                                                                                                              "
            +"			LEFT JOIN b_out_plan_detail tab2 ON tab2.id = tab1.plan_detail_id                                                                       "
            +"			LEFT JOIN b_out_plan tab3 ON tab3.id = tab2.plan_id                                                                                     "
            +"			LEFT JOIN m_warehouse tab4 ON tab4.id = tab1.warehouse_id                                                                               "
            +"		WHERE                                                                                                                                       "
            +"		TRUE                                                                                                                                        "
            +"			AND tab3.type =  '"+DictConstant.DICT_B_OUT_PLAN_TYPE_JG+"'                                                                             "
            +"			AND tab2.STATUS =  '"+DictConstant.DICT_B_OUT_PLAN_STATUS_PASSED+"'                                                                     "
            +"			AND tab1.STATUS =  '"+DictConstant.DICT_B_OUT_STATUS_PASSED+"'                                                                          "
            +"			AND tab4.warehouse_type = '"+DictConstant.DICT_M_WAREHOUSE_TYPE_ZX+"'                                                                   "
            +"		GROUP BY                                                                                                                                    "
            +"			tab2.order_id                                                                                                                           "
            +"		ORDER BY                                                                                                                                    "
            +"			tab1.e_dt DESC                                                                                                                          "
            +"		) t5 ON t5.order_id = t2.id                                                                                                                 "
            +"	WHERE                                                                                                                                           "
            +"	TRUE                                                                                                                                            "
            +"		AND t1.serial_type = '"+DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER+"'                                                                       "
            +"		AND t3.order_id IS NOT NULL                                                                                                                 "
            +"		AND t1.contract_no LIKE '%G0%'                                                                                                              "
            +"	GROUP BY                                                                                                                                        "
            +"		t3.warehouse_id,                                                                                                                            "
            +"		t1.contract_no                                                                                                                              ")
    List<BOrderInvertedVo> queryInvertedOrderOutPlan();

    /**
     * 查询钱前一天的快照数据
     */
    @Select("SELECT * FROM b_order_inverted where date_format( backups_date, '%Y-%m-%d' ) = date_format( DATE_ADD( NOW(), INTERVAL - 1 DAY ), '%Y-%m-%d' )")
    List<BOrderInvertedEntity> selectSnapshot();
}
