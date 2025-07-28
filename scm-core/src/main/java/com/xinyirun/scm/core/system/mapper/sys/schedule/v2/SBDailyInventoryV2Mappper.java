package com.xinyirun.scm.core.system.mapper.sys.schedule.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.wms.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDailyInventoryVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关于库存日报表的mapper
 */
@Repository
public interface SBDailyInventoryV2Mappper extends BaseMapper<BDailyInventoryEntity> {

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_work ;                                               "
    )
    int deleteTemoraryTableDailyInventoryWork00();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_temp ;                                               "
    )
    int deleteTemoraryTableDailyInventoryTemp01();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_final_temp ;                                "
    )
    void deleteTemoraryTableDailyInventoryTemp02();

    /**
     * 锁定临时表
     */
    @Select(
        "       select 1 from b_daily_inventory_temp for update nowait;                                               "
    )
    List<Integer> lockTemoraryTableDailyInventoryTemp10();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_daily_inventory_work for update nowait;                                               "
    )
    List<Integer> lockTemoraryTableDailyInventoryWork11();

    /**
     * 锁定每日库存表
     */
    @Select(
            "       select 1 from b_daily_inventory for update nowait;                                               "
    )
    List<Integer> lockTableDailyInventory12();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_daily_inventory_final_temp for update nowait;                                               "
    )
    List<Integer> lockTemoraryTableDailyInventoryFinalTemp13();

//    /**
//     * 锁定临时表
//     */
//    @Select(
//        "  lock tables s_calendar read,                 " +
//        "      b_daily_inventory_temp write,            " +
//        "      b_daily_inventory_work write,            " +
//        "      b_daily_inventory_final_temp write,      " +
//        "      b_daily_inventory write,                 " +
//        "      b_in read,                               " +
//        "      b_out read,                              " +
//        "      b_adjust_detail read,                    " +
//        "      b_adjust read                            " +
//        "   ;       "
//    )
//    void lockAllTable14();

//    /**
//     * 锁定临时表
//     */
//    @Select(
//            "  unlock tables;       "
//    )
//    void unLockAllTable15();

    /**
     * 删除每日库存表
     */
    @Delete(
             "                     "
           + "        delete from b_daily_inventory t1                                                                         "
           + "              where true                                                                                         "
           + "    and (t1.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)  "
           + "    and (t1.location_id =  #{p1.location_id,jdbcType=INTEGER} or #{p1.location_id,jdbcType=INTEGER} is null)     "
           + "    and (t1.bin_id =  #{p1.bin_id,jdbcType=INTEGER} or #{p1.bin_id,jdbcType=INTEGER} is null)                    "
           + "    and (t1.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)              "
           + "    and (t1.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null)                    "
           + "                                                                              "
    )
    int deleteTableDailyInventory13(@Param("p1") BDailyInventoryVo condition);

    /**
     * 插入数据,尚未清洗的数据源，work
     */
    @Update({
            "         insert into b_daily_inventory_work                                                                         "
            + "         (                                                                                                        "
            + "           dt,                                                                                                    "
            + "           type,                                                                                                  "
            + "           bill_type,                                                                                             "
            + "           plan_id,                                                                                               "
            + "           owner_id,                                                                                              "
            + "           owner_code,                                                                                            "
            + "           sku_id,                                                                                                "
            + "           sku_code,                                                                                              "
            + "           warehouse_id,                                                                                          "
            + "           location_id,                                                                                           "
            + "           bin_id,                                                                                                "
            + "           qty,                                                                                                   "
            + "           price,                                                                                                 "
            + "           amount,                                                                                                "
            + "           unit_id,                                                                                               "
            + "           u_time                                                                                                 "
            + "         )                                                                                                        "
            + "     select tt1.*                                                                                                 "
            + "       from (                                                                                                     "
            + "               select t1.u_time dt,                                                                               "
            + "                    '01' as `type`,                                                                               "
            + "                    t1.`type` as bill_type,                                                                       "
            + "                    t1.plan_id ,                                                                                  "
            + "                    t1.owner_id ,                                                                                 "
            + "             	   t1.owner_code ,                                                                               "
            + "             	   t1.sku_id ,                                                                                   "
            + "             	   t1.sku_code ,                                                                                 "
            + "             	   t1.warehouse_id ,                                                                             "
            + "             	   t1.location_id ,                                                                              "
            + "             	   t1.bin_id ,                                                                                   "
            + "             	   t1.actual_weight as qty,                                                                      "
            + "             	   t1.price as price,                                                                            "
            + "             	   t1.amount as amount,                                                                          "
            + "             	   t1.unit_id,                                                                                   "
            + "             	   t1.u_time                                                                                     "
            + "               from b_in t1                                                                                       "
            + "               where t1.status in ('2') or (t1.pre_status = '2' and t1.status = '6')                                                    "
            + "               union all                                                                                          "
            + "             select t1.u_time dt,                                                                                 "
            + "             	   '02' as `type`,                                                                               "
            + "             	   t1.`type` as bill_type,                                                                       "
            + "             	   t1.plan_id ,                                                                                  "
            + "                    t1.owner_id ,                                                                                 "
            + "             	   t1.owner_code ,                                                                               "
            + "             	   t1.sku_id ,                                                                                   "
            + "             	   t1.sku_code ,                                                                                 "
            + "             	   t1.warehouse_id ,                                                                             "
            + "             	   t1.location_id ,                                                                              "
            + "             	   t1.bin_id ,                                                                                   "
            + "             	   actual_weight as qty,                                                                         "
            + "             	   0 as price,                                                                                   "
            + "             	   0 as amount,                                                                                  "
            + "             	   t1.unit_id,                                                                                   "
            + "             	   t1.u_time                                                                                     "
            + "               from b_out t1                                                                                      "
            + "               where t1.status in ('2', '5') or (t1.pre_status = '2' and t1.status = '7')                                                                          "
            + "               union all                                                                                          "
            + "             		select                                                                                       "
            + "             	       t1.u_time as dt,                                                                          "
            + "             	       '03' as `type`,                                                                           "
            + "             	       t2.type as bill_type,                                                                     "
            + "             	       null as plan_id ,                                                                         "
            + "             	       t2.owner_id ,                                                                             "
            + "             		   t2.owner_code ,                                                                           "
            + "             		   t1.sku_id ,                                                                               "
            + "             		   t1.sku_code ,                                                                             "
            + "             		   t1.warehouse_id ,                                                                         "
            + "             		   t1.location_id ,                                                                          "
            + "             		   t1.bin_id ,                                                                               "
            + "             		   t1.qty_diff as qty,                                                                       "
            + "             		   0 as price,                                                                               "
            + "             		   0 as amount,                                                                              "
            + "                		   null as unit_id,                                                                          "
            + "                		   t1.u_time                                                                                 "
            + "             	  from b_adjust_detail t1                                                                        "
            + "              left join b_adjust t2 on t1.adjust_id = t2.id                                                       "
            + "                  where t1.status = 2                                                                             "
            + "       ) tt1                                                                                                      "
            + "   where true                                                                                                     "
            + "    and (tt1.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)  "
            + "    and (tt1.location_id =  #{p1.location_id,jdbcType=INTEGER} or #{p1.location_id,jdbcType=INTEGER} is null)     "
            + "    and (tt1.bin_id =  #{p1.bin_id,jdbcType=INTEGER} or #{p1.bin_id,jdbcType=INTEGER} is null)                    "
            + "    and (tt1.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)              "
            + "    and (tt1.sku_id =  #{p1.sku_id,jdbcType=INTEGER} or #{p1.sku_id,jdbcType=INTEGER} is null)                    "
//            + "    and (tt1.dt >= #{p1.dt,jdbcType=DATE} or #{p1.dt,jdbcType=INTEGER} is null)                                   "
            + "                                                 "
    })
    void createTemoraryWorkData20(@Param("p1") BDailyInventoryVo condition);

    /**
     * 最后插入到每日库存表中
     */
    @Update({
            "   insert into                                                                                            ",
            "   	b_daily_inventory_temp ( dt,                                                                         ",
            "   	owner_id,                                                                                       ",
            "   	owner_code,                                                                                     ",
            "   	sku_id,                                                                                         ",
            "   	sku_code,                                                                                       ",
            "   	warehouse_id,                                                                                   ",
            "   	location_id,                                                                                    ",
            "   	bin_id,                                                                                         ",
            "   	qty,                                                                                            ",
            "   	qty_in,                                                                                         ",
            "   	qty_out,                                                                                        ",
            "   	qty_adjust,                                                                                     ",
            "   	unit_id,                                                                                        ",
            "   	u_time )                                                                                        ",
            "    			select ttt1.dt,                                                                         ",
            "    					ttt1.owner_id,                                                                  ",
            "    					ttt1.owner_code,                                                                ",
            "    					ttt1.sku_id,                                                                    ",
            "    					ttt1.sku_code,                                                                  ",
            "    					ttt1.warehouse_id ,                                                             ",
            "    					ttt1.location_id ,                                                              ",
            "    					ttt1.bin_id ,                                                                   ",
            "    					case when ttt1.lag_sku_id is null then @qty:=ttt1.qty                           ",
            "    					     else @qty:=@qty+ttt1.qty_in - ttt1.qty_out + ttt1.qty_adjust               ",
            "    					end as qty,                                                                     ",
            "    					ttt1.qty_in,                                                                    ",
            "    					ttt1.qty_out,                                                                   ",
            "    					ttt1.qty_adjust,                                                                ",
            "    					ttt1.unit_id,                                                                   ",
            "    					now(3)                                                                          ",
            "                from (                                                                                 ",
            "    		  	 select tt1.dt,                                                                         ",
            "    					tt1.owner_id,                                                                   ",
            "    					tt1.owner_code,                                                                 ",
            "    					tt1.sku_id,                                                                     ",
            "    					tt1.sku_code,                                                                   ",
            "    					tt1.warehouse_id ,                                                              ",
            "    					tt1.location_id ,                                                               ",
            "    					tt1.bin_id ,                                                                    ",
            "    					 lag(tt1.sku_id, 1) over ( partition by tt1.warehouse_id ,                      ",
            "    		                          tt1.location_id ,                                                 ",
            "    		                          tt1.bin_id ,                                                      ",
            "    		                          tt1.owner_id ,                                                    ",
            "    		                          tt1.sku_id                                                        ",
            "    		                 order by tt1.warehouse_id ,                                                ",
            "    		                          tt1.location_id ,                                                 ",
            "    		                          tt1.bin_id ,                                                      ",
            "    		                          tt1.owner_id ,                                                    ",
            "    		                          tt1.sku_id ,                                                      ",
            "    		                          tt1.dt  ) as lag_sku_id,                                           ",
            "    					tt1.qty_in - tt1.qty_out + tt1.qty_adjust as qty,                                ",
            "    					tt1.qty_in,                                                                      ",
            "    					tt1.qty_out,                                                                     ",
            "    					tt1.qty_adjust,                                                                  ",
            "    					tt1.unit_id                                                                      ",
            "    		  from (                                                                                     ",
            "    				  select                                                                             ",
            "    					DATE_FORMAT(t1.dt, '%Y-%m-%d') as dt,                                            ",
            "    					t1.owner_id,                                                                     ",
            "    					t1.owner_code,                                                                   ",
            "    					t1.sku_id,                                                                       ",
            "    					t1.sku_code,                                                                     ",
            "    					t1.warehouse_id ,                                                                ",
            "    					t1.location_id ,                                                                 ",
            "    					t1.bin_id ,                                                                      ",
            "    					sum(case when t1.`type` = '01' then t1.qty else 0 end) as qty_in,                ",
            "    					sum(case when t1.`type` = '02' then t1.qty else 0 end) as qty_out,               ",
            "    					sum(case when t1.`type` = '03' then t1.qty else 0 end) as qty_adjust,            ",
            "    					unit_id as unit_id                                                               ",
            "    				from                                                                                 ",
            "    					b_daily_inventory_work t1                                                        ",
            "    				group by                                                                             ",
            "    					DATE_FORMAT(t1.dt, '%Y-%m-%d'),                                                  ",
            "    					t1.owner_id,                                                                     ",
            "    					t1.sku_id,                                                                       ",
            "    					t1.warehouse_id ,                                                                ",
            "    					t1.location_id ,                                                                 ",
            "    					t1.bin_id                                                                        ",
            "    		  ) tt1                                                                                      ",
            "          )ttt1,(SELECT  @qty := 0) tt2                                                                 ",
            "               ",
    })
    void insertTableDailyInventoryTemp_30();

    /**
     * 开始处理temp表：
     * 在这里，需要有个b_daily_inventory_final_temp，来处理：
     * 1、保持每天数据的完整性，例如1/1有数据，今天日期为2/1，期间即使没有数据，也要自动生成1个月的数据
     * 2、自动生成的数据保持为上一天的数据（仓库、库区、库位、货主、sku）
     */
    @Update({
            "                                          "
                    + "      insert into b_daily_inventory_final_temp (                                                                         "
                    + "      	dt,                                                                                                             "
                    + "      	owner_id,                                                                                                       "
                    + "      	owner_code,                                                                                                     "
                    + "      	sku_id,                                                                                                         "
                    + "      	sku_code,                                                                                                       "
                    + "      	warehouse_id,                                                                                                   "
                    + "      	location_id,                                                                                                    "
                    + "      	bin_id,                                                                                                         "
                    + "      	qty,                                                                                                            "
                    + "      	qty_in,                                                                                                         "
                    + "      	qty_out,                                                                                                        "
                    + "      	qty_adjust,                                                                                                     "
                    + "      	price,                                                                                                          "
                    + "      	amount,                                                                                                         "
                    + "      	unit_id,                                                                                                        "
                    + "      	realtime_amount,                                                                                                "
                    + "      	realtime_price,                                                                                                 "
                    + "      	u_time                                                                                                          "
                    + "      )                                                                                                                  "
                    + "      select tab1.calendar as dt ,                                                                                       "
                    + "             tab1.owner_id,                                                                                              "
                    + "             tab1.owner_code ,                                                                                           "
                    + "             tab1.sku_id,                                                                                                "
                    + "             tab1.sku_code ,                                                                                             "
                    + "             tab1.warehouse_id,                                                                                          "
                    + "             tab1.location_id,                                                                                           "
                    + "             tab1.bin_id,                                                                                                "
                    + "             sum(tab2.qty) qty ,                                                                                         "
                    + "             sum(tab2.qty_in)  qty_in ,                                                                                  "
                    + "             sum(tab2.qty_out) qty_out ,                                                                                 "
                    + "             sum(tab2.qty_adjust) qty_adjust ,                                                                           "
                    + "             tab2.price ,                                                                                                "
                    + "             tab2.amount ,                                                                                               "
                    + "             tab2.unit_id ,                                                                                              "
                    + "             tab2.realtime_amount ,                                                                                      "
                    + "             tab2.realtime_price ,                                                                                       "
                    + "             now(3)                                                                                                      "
                    + "        from (                                                                                                           "
                    + "                select sub1.calendar,                                                                                    "
                    + "                       sub2.*                                                                                            "
                    + "                  from ( select `date` as calendar                                                                       "
                    + "                           from s_calendar t1                                                                            "
                    + "                          where t1.`date` between (select min(dt) from b_daily_inventory_temp) and now(3)                "
                    + "                        ) sub1 ,                                                                                         "
                    + "                        (                                                                                                "
                    + "      					  select t1.warehouse_id ,                                                                      "
                    + "      					         t1.location_id ,                                                                       "
                    + "      					         t1.bin_id ,                                                                            "
                    + "      					         t1.owner_id ,                                                                          "
                    + "      					         t1.owner_code,                                                                         "
                    + "      					         t1.sku_id ,                                                                            "
                    + "      					         t1.sku_code,                                                                           "
                    + "      					         count(1),                                                                              "
                    + "      					         min(dt) min_dt                                                                         "
                    + "      					    from b_daily_inventory_temp t1                                                              "
                    + "      					group by t1.warehouse_id ,                                                                      "
                    + "      					         t1.location_id ,                                                                       "
                    + "      					         t1.bin_id ,                                                                            "
                    + "      					         t1.owner_id ,                                                                          "
                    + "      					         t1.sku_id                                                                              "
                    + "                        ) sub2                                                                                           "
                    + "                  where sub1.calendar >= sub2.min_dt                                                                     "
                    + "      ) tab1 left join b_daily_inventory_temp tab2                                                                       "
                    + "                    on tab1.calendar = tab2.dt                                                                           "
                    + "                   and tab1.warehouse_id = tab2.warehouse_id                                                             "
                    + "                   and tab1.location_id = tab2.location_id                                                               "
                    + "                   and tab1.bin_id = tab2.bin_id                                                                         "
                    + "                   and tab1.owner_id = tab2.owner_id                                                                     "
                    + "                   and tab1.sku_id = tab2.sku_id                                                                         "
                    + "             where true                                                                                                  "
                    +"	            GROUP BY                                                                                                    "
                    +"	            	tab1.sku_id,                                                                                            "
                    +"	            	tab1.warehouse_id,                                                                                      "
                    +"	            	tab1.owner_id,                                                                                          "
                    +"	            	tab1.calendar                                                                                           "
                    + "       order by tab1.calendar                                                                                            "
                    + "                                                 "
    })
    void createTableDailyInventoryFinal_40();

//    @Update({
//            "                                          "
//                    + "          update b_daily_inventory_final_temp t1                                                                            "
//                    + "      inner join b_daily_inventory_final_temp t2                                                                            "
//                    + "              on t1.warehouse_id = t2.warehouse_id                                                                          "
//                    + "             and t1.location_id = t2.location_id                                                                            "
//                    + "             and t1.bin_id = t2.bin_id                                                                                      "
//                    + "             and t1.owner_id = t2.owner_id                                                                                  "
//                    + "             and t1.sku_id = t2.sku_id                                                                                      "
//                    + "             and t2.dt = f_get_dt(t1.warehouse_id,t1.location_id,t1.bin_id,t1.owner_id,t1.sku_id,t1.dt)                     "
//                    + "             set t1.qty = t2.qty,                                                                                           "
//                    + "                 t1.qty_in = t2.qty_in ,                                                                                    "
//                    + "                 t1.qty_out = t2.qty_out ,                                                                                  "
//                    + "                 t1.qty_adjust = t2.qty_adjust ,                                                                            "
//                    + "     	        t1.unit_id = t2.unit_id                                                                                    "
//                    + "                                                                                                                            "
//    })
//    void updateTableDailyInventoryFinal_41();

    @Update({""
            + "		UPDATE b_daily_inventory_final_temp tab1                                                                           "
            + "		INNER JOIN (                                                                                                       "
            + "		SELECT                                                                                                             "
            + "			t1.dt,                                                                                                         "
            + "			sum( t2.qty_in-t2.qty_out+t2.qty_adjust ) qty,                                                                 "
            + "			t1.id                                                                                                          "
            + "		FROM                                                                                                               "
            + "			b_daily_inventory_final_temp t1                                                                                "
            + "			INNER JOIN b_daily_inventory_final_temp t2 ON t1.owner_id = t2.owner_id                                        "
            + "			AND t1.warehouse_id = t2.warehouse_id                                                                          "
            + "			AND t1.sku_id = t2.sku_id                                                                                      "
            + "			AND t1.dt >= t2.dt                                                                                             "
            + "		GROUP BY                                                                                                           "
            + "			t1.sku_id,                                                                                                     "
            + "			t1.owner_id,                                                                                                   "
            + "			t1.warehouse_id,                                                                                               "
            + "			t1.dt                                                                                                          "
            + "			) tab2                                                                                                         "
            + "			ON tab1.id = tab2.id                                                                                           "
            + "			LEFT JOIN (                                                                                                    "
            + "				SELECT                                                                                                     "
            + "					sku_id,                                                                                                "
            + "					price,                                                                                                 "
            + "					price_dt,                                                                                              "
            + "					c_time,                                                                                                "
            + "					row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num             "
            + "				FROM                                                                                                       "
            + "					b_goods_price                                                                                          "
            + "				) tab3 ON tab1.sku_id = tab3.sku_id                                                                        "
            + "				AND tab3.row_num = 1                                                                                       "
            + "				AND DATE_FORMAT( tab3.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( tab1.dt, '%Y-%m-%d' )                         "
            + "			SET tab1.qty = tab2.qty ,tab1.price = tab3.price                                                               "
            + "		WHERE                                                                                                              "
            + "			tab1.id = tab2.id                                                                                              "
    })
    void updateTableDailyInventoryFinal_41();

    @Update({""

            + "	UPDATE b_daily_inventory_final_temp ttt0                                                                                                                                                                                                                    "
            + "	INNER JOIN (                                                                                                                                                                                                                                                "
            + "	SELECT                                                                                                                                                                                                                                                      "
            + "		tt0.source_sku_id,                                                                                                                                                                                                                                      "
            + "		tt0.target_sku_id,                                                                                                                                                                                                                                      "
            + "		sum( tt1.amount ) amount,                                                                                                                                                                                                                               "
            + "		sum( tt1.actual_weight ) actual_weight,                                                                                                                                                                                                                 "
            + "		sum( tt1.amount ) / sum( tt1.actual_weight ) price,                                                                                                                                                                                                     "
            + "		tt1.warehouse_id,                                                                                                                                                                                                                                       "
            + "		tt1.owner_id,                                                                                                                                                                                                                                           "
            + "		tt1.dt                                                                                                                                                                                                                                                  "
            + "	FROM                                                                                                                                                                                                                                                        "
            + "		(                                                                                                                                                                                                                                                       "
            + "	SELECT                                                                                                                                                                                                                                                      "
            + "		t2.warehouse_id,                                                                                                                                                                                                                                        "
            + "		t2.owner_id,                                                                                                                                                                                                                                            "
            + "		t1.target_sku_id,                                                                                                                                                                                                                                       "
            + "		t1.source_sku_id                                                                                                                                                                                                                                        "
            + "	FROM                                                                                                                                                                                                                                                        "
            + "		b_material_convert_detail t1                                                                                                                                                                                                                            "
            + "		INNER JOIN b_material_convert t2 ON t1.material_convert_id = t2.id                                                                                                                                                                                      "
            + "	WHERE                                                                                                                                                                                                                                                       "
            + "		t1.is_effective = TRUE                                                                                                                                                                                                                                  "
            + "		AND t2.is_effective = TRUE                                                                                                                                                                                                                              "
            + "		) tt0                                                                                                                                                                                                                                                   "
            + "		INNER JOIN (                                                                                                                                                                                                                                            "
            + "	SELECT                                                                                                                                                                                                                                                      "
            + "		t1.u_time,                                                                                                                                                                                                                                              "
            + "		t1.sku_id,                                                                                                                                                                                                                                              "
            + "		t1.owner_id,                                                                                                                                                                                                                                            "
            + "		t1.warehouse_id,                                                                                                                                                                                                                                        "
            + "		t0.dt,                                                                                                                                                                                                                                                  "
            + "		sum( t1.actual_weight ) actual_weight,                                                                                                                                                                                                                  "
            + "		sum( t1.actual_weight * t1.price ) amount,                                                                                                                                                                             "
            + "		sum( t1.actual_weight * t1.price ) / sum( t1.actual_weight ) price                                                                                                                                                     "
            + "	FROM                                                                                                                                                                                                                                                        "
            + "		b_daily_inventory_final_temp t0                                                                                                                                                                                                                         "
            + "		INNER JOIN b_in t1 ON t0.warehouse_id = t1.warehouse_id                                                                                                                                                                                                 "
            + "		AND t0.owner_id = t1.owner_id                                                                                                                                                                                                                           "
            + "		AND t0.sku_id = t1.sku_id                                                                                                                                                                                                                               "
            + "		LEFT JOIN (                                                                                                                                                                                                                                             "
            + "	SELECT                                                                                                                                                                                                                                                      "
            + "		*                                                                                                                                                                                                                                                       "
            + "	FROM                                                                                                                                                                                                                                                        "
            + "		( SELECT * FROM b_goods_price ORDER BY c_time DESC LIMIT 10000 ) t                                                                                                                                                                                      "
            + "	GROUP BY                                                                                                                                                                                                                                                    "
            + "		sku_id,                                                                                                                                                                                                                                                 "
            + "		DATE_FORMAT( price_dt, '%Y-%m-%d' )                                                                                                                                                                                                                     "
            + "		) t2 ON t1.sku_id = t2.sku_id                                                                                                                                                                                                                           "
            + "		AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) = DATE_FORMAT( t2.price_dt, '%Y-%m-%d' )                                                                                                                                                                       "
            + "		LEFT JOIN (                                                                                                                                                                                                                                             "
            + "	SELECT                                                                                                                                                                                                                                                      "
            + "		sku_id,                                                                                                                                                                                                                                                 "
            + "		price,                                                                                                                                                                                                                                                  "
            + "		price_dt,                                                                                                                                                                                                                                               "
            + "		c_time,                                                                                                                                                                                                                                                 "
            + "		row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                                                                                                                                              "
            + "	FROM                                                                                                                                                                                                                                                        "
            + "		b_goods_price                                                                                                                                                                                                                                           "
            + "		) t3 ON t3.sku_id = t1.sku_id                                                                                                                                                                                                                           "
            + "		AND t3.row_num = 1                                                                                                                                                                                                                                      "
            + "		AND DATE_FORMAT( t3.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) WHERE t1.STATUS = '2' AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_SUB( t0.dt, INTERVAL  #{p1,jdbcType=INTEGER} DAY ), '%Y-%m-%d' )                 "
            + "		AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) <= DATE_FORMAT( t0.dt, '%Y-%m-%d' )                                                                                                                                                                            "
            + "	GROUP BY                                                                                                                                                                                                                                                    "
            + "		t0.id                                                                                                                                                                                                                                                   "
            + "		) tt1 ON tt0.source_sku_id = tt1.sku_id                                                                                                                                                                                                                 "
            + "		AND tt0.warehouse_id = tt1.warehouse_id                                                                                                                                                                                                                 "
            + "		AND tt0.owner_id = tt1.owner_id                                                                                                                                                                                                                         "
            + "	GROUP BY                                                                                                                                                                                                                                                    "
            + "		tt0.target_sku_id,                                                                                                                                                                                                                                      "
            + "		tt0.warehouse_id,                                                                                                                                                                                                                                       "
            + "		tt0.owner_id,                                                                                                                                                                                                                                           "
            + "		tt1.dt                                                                                                                                                                                                                                                  "
            + "		) ttt1 ON ttt0.sku_id = ttt1.target_sku_id                                                                                                                                                                                                              "
            + "		AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                                                                                                                                               "
            + "		AND ttt0.owner_id = ttt1.owner_id                                                                                                                                                                                                                       "
            + "		AND ttt0.dt = ttt1.dt                                                                                                                                                                                                                                   "
            + "		SET ttt0.price = ttt1.price                                                                                                                                                                                                                             "
            + "	WHERE                                                                                                                                                                                                                                                       "
            + "		ttt0.sku_id = ttt1.target_sku_id                                                                                                                                                                                                                        "
            + "		AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                                                                                                                                               "
            + "		AND ttt0.owner_id = ttt1.owner_id                                                                                                                                                                                                                       "
            + "		AND ttt0.dt = ttt1.dt                                                                                                                                                                                                                                   "

    })
    void updateTableDailyInventoryFinal_42(@Param("p1") Integer days);

    /**
     * 最后插入到每日库存表中
     */
    @Update({
            "   insert                                                                                                                          ",
            "   	into                                                                                                                        ",
            "   	b_daily_inventory ( dt,                                                                                                     ",
            "   	owner_id,                                                                                                                   ",
            "   	owner_code,                                                                                                                 ",
            "   	sku_id,                                                                                                                     ",
            "   	sku_code,                                                                                                                   ",
            "   	warehouse_id,                                                                                                               ",
            "   	location_id,                                                                                                                ",
            "   	bin_id,                                                                                                                     ",
            "   	qty,                                                                                                                        ",
            "   	qty_in,                                                                                                                     ",
            "   	qty_out,                                                                                                                    ",
            "   	qty_adjust,                                                                                                                 ",
            "   	price,                                                                                                                      ",
            "   	inventory_amount,                                                                                                           ",
            "   	unit_id,                                                                                                                    ",
            "   	realtime_amount,                                                                                                            ",
            "   	realtime_price,                                                                                                             ",
            "   	c_time,                                                                                                                     ",
            "   	u_time )                                                                                                                    ",
            "   select                                                                                                                          ",
            "   	t1.dt,                                                                                                                      ",
            "   	t1.owner_id,                                                                                                                ",
            "   	t1.owner_code,                                                                                                              ",
            "   	t1.sku_id,                                                                                                                  ",
            "   	t1.sku_code,                                                                                                                ",
            "   	t1.warehouse_id,                                                                                                            ",
            "   	t1.location_id,                                                                                                             ",
            "   	t1.bin_id,                                                                                                                  ",
            "   	t1.qty,                                                                                                                     ",
            "   	ifnull(t1.qty_in,0) qty_in,                                                                                                 ",
            "   	ifnull(t1.qty_out,0) qty_out,                                                                                               ",
            "   	ifnull(t1.qty_adjust,0) qty_adjust,                                                                                         ",
            "   	t1.price,                                                                                                                   ",
            "   	(t1.price*t1.qty) amount,                                                                                                   ",
            "   	t1.unit_id,                                                                                                                 ",
            "   	0,                                                                                                                          ",
            "   	0,                                                                                                                          ",
            "   	now(),                                                                                                                      ",
            "   	now()                                                                                                                       ",
            "   from                                                                                                                            ",
            "   	b_daily_inventory_final_temp t1                                                                                             ",
            "	left join m_inventory t2                                                                                                        ",
            "	on t1.warehouse_id = t2.warehouse_id                                                                                            ",
            "	and t1.location_id = t2.location_id                                                                                             ",
            "	and t1.bin_id = t2.bin_id                                                                                                       ",
            "	and t1.sku_id = t2.sku_id                                                                                                       ",
            "	and t1.owner_id = t2.owner_id                                                                                                   ",
            "   where ifnull(t1.qty,0) <>0 or ifnull(t1.qty_in,0) <>0 or ifnull(t1.qty_out,0) <>0 or ifnull(t1.qty_adjust,0) <>0                ",
            "               ",
    })
    void insertTableDailyInventoryFinal();
}
