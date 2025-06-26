package com.xinyirun.scm.core.system.mapper.sys.schedule.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.sync.ApiDailyInventoryPriceVo;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyInventoryVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关于库存日报表的mapper
 */
@Repository
public interface SBDailyInventoryBatchV2Mappper extends BaseMapper<BDailyInventoryEntity> {

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_batch_work ;                                               "
    )
    int deleteTemoraryTableDailyInventoryWork00();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_batch_temp ;                                               "
    )
    int deleteTemoraryTableDailyInventoryTemp01();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_batch_final_temp ;                                "
    )
    void deleteTemoraryTableDailyInventoryTemp02();

    /**
     * 锁定临时表
     */
    @Select(
        "       select 1 from b_daily_inventory_batch_temp for update nowait;                                               "
    )
    List<Integer> lockTemoraryTableDailyInventoryTemp10();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_daily_inventory_batch_work for update nowait;                                               "
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
            "       select 1 from b_daily_inventory_batch_final_temp for update nowait;                                               "
    )
    List<Integer> lockTemoraryTableDailyInventoryFinalTemp13();

//    /**
//     * 锁定临时表
//     */
//    @Select(
//        "  lock tables s_calendar read,                 " +
//        "      b_daily_inventory_batch_temp write,            " +
//        "      b_daily_inventory_batch_work write,            " +
//        "      b_daily_inventory_batch_final_temp write,      " +
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
            "         insert into b_daily_inventory_batch_work                                                                         "
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
            + "               where t1.status in ('2') or (t1.pre_status = '2' and t1.status = '6')                              "
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
            + "               where t1.status in ('2') or (t1.pre_status = '2' and t1.status = '7')                              "
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
    void createTemporaryWorkData20(@Param("p1") BDailyInventoryVo condition);

    /**
     * 最后插入到每日库存表中
     */
    @Update({
            "   insert into                                                                                            ",
            "   	b_daily_inventory_batch_temp ( dt,                                                                         ",
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
            "    					b_daily_inventory_batch_work t1                                                        ",
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
     * 在这里，需要有个b_daily_inventory_batch_final_temp，来处理：
     * 1、保持每天数据的完整性，例如1/1有数据，今天日期为2/1，期间即使没有数据，也要自动生成1个月的数据
     * 2、自动生成的数据保持为上一天的数据（仓库、库区、库位、货主、sku）
     */
    @Update({
            "                                          "
                    + "      insert into b_daily_inventory_batch_final_temp (                                                                         "
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
                    + "                          where t1.`date` between (select min(dt) from b_daily_inventory_batch_temp) and now(3)                "
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
                    + "      					    from b_daily_inventory_batch_temp t1                                                              "
                    + "      					group by t1.warehouse_id ,                                                                      "
                    + "      					         t1.location_id ,                                                                       "
                    + "      					         t1.bin_id ,                                                                            "
                    + "      					         t1.owner_id ,                                                                          "
                    + "      					         t1.sku_id                                                                              "
                    + "                        ) sub2                                                                                           "
                    + "                  where sub1.calendar >= sub2.min_dt                                                                     "
                    + "      ) tab1 left join b_daily_inventory_batch_temp tab2                                                                       "
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
//                    + "          update b_daily_inventory_batch_final_temp t1                                                                            "
//                    + "      inner join b_daily_inventory_batch_final_temp t2                                                                            "
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

//    @Update({""
//            + "		UPDATE b_daily_inventory_batch_final_temp tab1                                                                           "
//            + "		INNER JOIN (                                                                                                       "
//            + "		SELECT                                                                                                             "
//            + "			t1.dt,                                                                                                         "
//            + "			sum( t2.qty_in-t2.qty_out+t2.qty_adjust ) qty,                                                                 "
//            + "			t1.id                                                                                                          "
//            + "		FROM                                                                                                               "
//            + "			b_daily_inventory_batch_final_temp t1                                                                                "
//            + "			INNER JOIN b_daily_inventory_batch_final_temp t2 ON t1.owner_id = t2.owner_id                                        "
//            + "			AND t1.warehouse_id = t2.warehouse_id                                                                          "
//            + "			AND t1.sku_id = t2.sku_id                                                                                      "
//            + "			AND t1.dt >= t2.dt                                                                                             "
//            + "		GROUP BY                                                                                                           "
//            + "			t1.sku_id,                                                                                                     "
//            + "			t1.owner_id,                                                                                                   "
//            + "			t1.warehouse_id,                                                                                               "
//            + "			t1.dt                                                                                                          "
//            + "			) tab2                                                                                                         "
//            + "			ON tab1.id = tab2.id                                                                                           "
//            + "			LEFT JOIN (                                                                                                    "
//            + "				SELECT                                                                                                     "
//            + "					sku_id,                                                                                                "
//            + "					price,                                                                                                 "
//            + "					price_dt,                                                                                              "
//            + "					c_time,                                                                                                "
//            + "					row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num             "
//            + "				FROM                                                                                                       "
//            + "					b_goods_price                                                                                          "
//            + "				) tab3 ON tab1.sku_id = tab3.sku_id                                                                        "
//            + "				AND tab3.row_num = 1                                                                                       "
//            + "				AND DATE_FORMAT( tab3.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( tab1.dt, '%Y-%m-%d' )                         "
//            + "			SET tab1.qty = tab2.qty ,tab1.price = tab3.price                                                               "
//            + "		WHERE                                                                                                              "
//            + "			tab1.id = tab2.id                                                                                              "
//    })
//    void updateTableDailyInventoryFinal_41();

    @Update({""
            + "	UPDATE b_daily_inventory_batch_final_temp tab1                                                                                               "
            + "	INNER JOIN (                                                                                                                                 "
            + "		SELECT                                                                                                                                   "
            + "			t1.dt,                                                                                                                               "
            + "			sum( t1.qty_in - t1.qty_out + t1.qty_adjust ) over ( PARTITION BY t1.sku_id, t1.owner_id, t1.warehouse_id ORDER BY dt ) qty,         "
            + "			t1.id                                                                                                                                "
            + "		FROM                                                                                                                                     "
            + "			b_daily_inventory_batch_final_temp t1                                                                                                "
            + "		) tab2 ON tab1.id = tab2.id                                                                                                              "
            + "		LEFT JOIN (                                                                                                                              "
            + "		SELECT                                                                                                                                   "
            + "			sku_id,                                                                                                                              "
            + "			price,                                                                                                                               "
            + "			price_dt,                                                                                                                            "
            + "			c_time,                                                                                                                              "
            + "			row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                           "
            + "		FROM                                                                                                                                     "
            + "			b_goods_price                                                                                                                        "
            + "		) tab3 ON tab1.sku_id = tab3.sku_id                                                                                                      "
            + "		AND tab3.row_num = 1                                                                                                                     "
            + "		AND DATE_FORMAT( tab3.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( tab1.dt, '%Y-%m-%d' )                                                       "
            + "		SET tab1.qty = tab2.qty,                                                                                                                 "
            + "		tab1.price = tab3.price                                                                                                                  "
            + "	WHERE                                                                                                                                        "
            + "		tab1.id = tab2.id                                                                                                                        "
    })
    void updateTableDailyInventoryFinal_41();

//    @Update({""
//            + "	UPDATE b_daily_inventory_batch_final_temp ttt0                                                                                                                                                                                                                    "
//            + "	INNER JOIN (                                                                                                                                                                                                                                                "
//            + "	SELECT                                                                                                                                                                                                                                                      "
//            + "		tt0.source_sku_id,                                                                                                                                                                                                                                      "
//            + "		tt0.target_sku_id,                                                                                                                                                                                                                                      "
//            + "		sum( tt1.amount ) amount,                                                                                                                                                                                                                               "
//            + "		sum( tt1.actual_weight ) actual_weight,                                                                                                                                                                                                                 "
//            + "		sum( tt1.amount ) / sum( tt1.actual_weight ) price,                                                                                                                                                                                                     "
//            + "		tt1.warehouse_id,                                                                                                                                                                                                                                       "
//            + "		tt1.owner_id,                                                                                                                                                                                                                                           "
//            + "		tt1.dt                                                                                                                                                                                                                                                  "
//            + "	FROM                                                                                                                                                                                                                                                        "
//            + "		(                                                                                                                                                                                                                                                       "
//            + "	SELECT                                                                                                                                                                                                                                                      "
//            + "		t2.warehouse_id,                                                                                                                                                                                                                                        "
//            + "		t2.owner_id,                                                                                                                                                                                                                                            "
//            + "		t1.target_sku_id,                                                                                                                                                                                                                                       "
//            + "		t1.source_sku_id                                                                                                                                                                                                                                        "
//            + "	FROM                                                                                                                                                                                                                                                        "
//            + "		b_material_convert_detail t1                                                                                                                                                                                                                            "
//            + "		INNER JOIN b_material_convert t2 ON t1.material_convert_id = t2.id                                                                                                                                                                                      "
//            + "	WHERE                                                                                                                                                                                                                                                       "
//            + "		t1.is_effective = TRUE                                                                                                                                                                                                                                  "
//            + "		AND t2.is_effective = TRUE                                                                                                                                                                                                                              "
//            + "		) tt0                                                                                                                                                                                                                                                   "
//            + "		INNER JOIN (                                                                                                                                                                                                                                            "
//            + "	SELECT                                                                                                                                                                                                                                                      "
//            + "		t1.u_time,                                                                                                                                                                                                                                              "
//            + "		t1.sku_id,                                                                                                                                                                                                                                              "
//            + "		t1.owner_id,                                                                                                                                                                                                                                            "
//            + "		t1.warehouse_id,                                                                                                                                                                                                                                        "
//            + "		t0.dt,                                                                                                                                                                                                                                                  "
//            + "		sum( t1.actual_weight ) actual_weight,                                                                                                                                                                                                                  "
//            + "		sum( t1.actual_weight * t1.price ) amount,                                                                                                                                                                             "
//            + "		sum( t1.actual_weight * t1.price ) / sum( t1.actual_weight ) price                                                                                                                                                     "
//            + "	FROM                                                                                                                                                                                                                                                        "
//            + "		b_daily_inventory_batch_final_temp t0                                                                                                                                                                                                                         "
//            + "		INNER JOIN b_in t1 ON t0.warehouse_id = t1.warehouse_id                                                                                                                                                                                                 "
//            + "		AND t0.owner_id = t1.owner_id                                                                                                                                                                                                                           "
//            + "		AND t0.sku_id = t1.sku_id                                                                                                                                                                                                                               "
//            + "		LEFT JOIN (                                                                                                                                                                                                                                             "
//            + "	SELECT                                                                                                                                                                                                                                                      "
//            + "		*                                                                                                                                                                                                                                                       "
//            + "	FROM                                                                                                                                                                                                                                                        "
//            + "		( SELECT * FROM b_goods_price ORDER BY c_time DESC LIMIT 10000 ) t                                                                                                                                                                                      "
//            + "	GROUP BY                                                                                                                                                                                                                                                    "
//            + "		sku_id,                                                                                                                                                                                                                                                 "
//            + "		DATE_FORMAT( price_dt, '%Y-%m-%d' )                                                                                                                                                                                                                     "
//            + "		) t2 ON t1.sku_id = t2.sku_id                                                                                                                                                                                                                           "
//            + "		AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) = DATE_FORMAT( t2.price_dt, '%Y-%m-%d' )                                                                                                                                                                       "
//            + "		LEFT JOIN (                                                                                                                                                                                                                                             "
//            + "	SELECT                                                                                                                                                                                                                                                      "
//            + "		sku_id,                                                                                                                                                                                                                                                 "
//            + "		price,                                                                                                                                                                                                                                                  "
//            + "		price_dt,                                                                                                                                                                                                                                               "
//            + "		c_time,                                                                                                                                                                                                                                                 "
//            + "		row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                                                                                                                                              "
//            + "	FROM                                                                                                                                                                                                                                                        "
//            + "		b_goods_price                                                                                                                                                                                                                                           "
//            + "		) t3 ON t3.sku_id = t1.sku_id                                                                                                                                                                                                                           "
//            + "		AND t3.row_num = 1                                                                                                                                                                                                                                      "
//            + "		AND DATE_FORMAT( t3.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) WHERE t1.STATUS = '2' AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_SUB( t0.dt, INTERVAL  #{p1,jdbcType=INTEGER} DAY ), '%Y-%m-%d' )                 "
//            + "		AND DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) <= DATE_FORMAT( t0.dt, '%Y-%m-%d' )                                                                                                                                                                            "
//            + "	GROUP BY                                                                                                                                                                                                                                                    "
//            + "		t0.id                                                                                                                                                                                                                                                   "
//            + "		) tt1 ON tt0.source_sku_id = tt1.sku_id                                                                                                                                                                                                                 "
//            + "		AND tt0.warehouse_id = tt1.warehouse_id                                                                                                                                                                                                                 "
//            + "		AND tt0.owner_id = tt1.owner_id                                                                                                                                                                                                                         "
//            + "	GROUP BY                                                                                                                                                                                                                                                    "
//            + "		tt0.target_sku_id,                                                                                                                                                                                                                                      "
//            + "		tt0.warehouse_id,                                                                                                                                                                                                                                       "
//            + "		tt0.owner_id,                                                                                                                                                                                                                                           "
//            + "		tt1.dt                                                                                                                                                                                                                                                  "
//            + "		) ttt1 ON ttt0.sku_id = ttt1.target_sku_id                                                                                                                                                                                                              "
//            + "		AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                                                                                                                                               "
//            + "		AND ttt0.owner_id = ttt1.owner_id                                                                                                                                                                                                                       "
//            + "		AND ttt0.dt = ttt1.dt                                                                                                                                                                                                                                   "
//            + "		SET ttt0.price = ttt1.price                                                                                                                                                                                                                             "
//            + "	WHERE                                                                                                                                                                                                                                                       "
//            + "		ttt0.sku_id = ttt1.target_sku_id                                                                                                                                                                                                                        "
//            + "		AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                                                                                                                                               "
//            + "		AND ttt0.owner_id = ttt1.owner_id                                                                                                                                                                                                                       "
//            + "		AND ttt0.dt = ttt1.dt                                                                                                                                                                                                                                   "
//
//    })
//    void updateTableDailyInventoryFinal_42(@Param("p1") Integer days);

    /**
     * 15天单价计算1
     */
    @Update({""
            + "		UPDATE                                                                                                                                                           "
            + "			b_daily_inventory_batch_final_temp tab0                                                                                                                      "
            + "			INNER JOIN (                                                                                                                                                 "
            + "			SELECT                                                                                                                                                       "
            + "				ttt0.id,                                                                                                                                                 "
            + "				ttt0.dt,                                                                                                                                                 "
            + "				ifnull(sum( ttt1.amount ) / sum( ttt1.qty ), 0) price,                                                                                                   "
            + "				ttt4.target_sku_id,                                                                                                                                      "
            + "				ttt0.owner_id,                                                                                                                                           "
            + "				ttt0.warehouse_id                                                                                                                                        "
            + "			FROM                                                                                                                                                         "
            + "				b_daily_inventory_batch_final_temp ttt0                                                                                                                  "
            + "				INNER JOIN (                                                                                                                                             "
            + "				SELECT                                                                                                                                                   "
            + "					t1.CODE,                                                                                                                                             "
            + "					t2.target_sku_id,                                                                                                                                    "
            + "					t2.source_sku_id,                                                                                                                                    "
            + "					t1.warehouse_id,                                                                                                                                     "
            + "					t1.owner_id,                                                                                                                                         "
            + "					t1.c_time                                                                                                                                            "
            + "				FROM                                                                                                                                                     "
            + "					b_material_convert t1                                                                                                                                "
            + "					INNER JOIN b_material_convert_detail t2 ON t2.material_convert_id = t1.id                                                                            "
            + "				) ttt4 ON ttt4.target_sku_id = ttt0.sku_id                                                                                                               "
            + "				AND ttt4.warehouse_id = ttt0.warehouse_id                                                                                                                "
            + "				AND ttt4.owner_id = ttt4.owner_id                                                                                                                        "
            + "				LEFT JOIN (                                                                                                                                              "
            + "			        SELECT                                                                                                                                               "
            + "			        	DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) dt,                                                                                                         "
            + "			        	t1.owner_id,                                                                                                                                     "
            + "			        	t1.warehouse_id,                                                                                                                                 "
            + "			        	t1.sku_id,                                                                                                                                       "
            + "			        	sum( ifnull( t1.actual_weight, 0 ) ) qty,                                                                                                        "
            + "			        	sum( t1.actual_weight * ifnull( t4.price, 0 ) ) amount                                                                                           "
            + "			        FROM                                                                                                                                                 "
            + "			        	b_in t1                                                                                                                                          "
            + "			        	LEFT JOIN b_in_plan_detail t2 ON t1.plan_detail_id = t2.id                                                                                       "
            + "			        	LEFT JOIN b_in_order t3 ON t2.order_type = 'b_in_order' AND t2.order_id = t3.id                                                                  "
            + "			        	LEFT JOIN b_in_order_goods t4 ON t4.order_id = t3.id                                                                                             "
            + "			        GROUP BY                                                                                                                                             "
            + "			        	t1.owner_id,                                                                                                                                     "
            + "			        	t1.warehouse_id,                                                                                                                                 "
            + "			        	t1.sku_id,                                                                                                                                       "
            + "			        	DATE_FORMAT( t1.u_time, '%Y-%m-%d' )                                                                                                             "
            + "				) ttt1                                                                                                                                                   "
            + "				ON ttt0.owner_id = ttt1.owner_id                                                                                                                         "
            + "				AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                                                "
            + "				AND ttt4.source_sku_id = ttt1.sku_id                                                                                                                     "
            + "				AND DATE_FORMAT( ttt1.dt, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_SUB( ttt0.dt, INTERVAL 15 DAY ), '%Y-%m-%d' )                                                "
            + "			GROUP BY                                                                                                                                                     "
            + "				ttt4.target_sku_id,                                                                                                                                      "
            + "				ttt0.owner_id,                                                                                                                                           "
            + "				ttt0.warehouse_id,                                                                                                                                       "
            + "				ttt0.dt                                                                                                                                                  "
            + "			) tab1 ON tab0.id = tab1.id                                                                                                                                  "
            + "			SET tab0.price = tab1.price                                                                                                                                  "
            + "		WHERE                                                                                                                                                            "
            + "			tab0.id = tab1.id                                                                                                                                            "
    })
    void updateTableDailyInventoryFinal_43();

    /**
     * 15天单价计算2
     */
    @Update({""
            + "	UPDATE b_daily_inventory_batch_final_temp tab0                                                                                                                                            "
            + "	INNER JOIN (                                                                                                                                                                              "
            + "		SELECT                                                                                                                          "
            + "			ttt0.id,                                                                                                                    "
            + "			ttt0.dt,                                                                                                                    "
            + "			ttt1.sku_id,                                                                                                                "
            + "			ifnull( sum( ttt1.amount ) / sum( ttt1.qty ), 0 ) price,                                                                    "
            + "			ttt0.owner_id,                                                                                                              "
            + "			ttt0.warehouse_id                                                                                                           "
            + "		FROM                                                                                                                            "
            + "			b_daily_inventory_batch_final_temp ttt0                                                                                     "
            + "			LEFT JOIN (                                                                                                                 "
            + "			SELECT                                                                                                                      "
            + "				DATE_FORMAT( t1.u_time, '%Y-%m-%d' ) dt,                                                                                "
            + "				t1.owner_id,                                                                                                            "
            + "				t1.warehouse_id,                                                                                                        "
            + "				t1.sku_id,                                                                                                              "
            + "				sum( ifnull( t1.actual_weight, 0 ) ) qty,                                                                               "
            + "				sum( t1.actual_weight * ifnull( t4.price, 0 ) ) amount                                                                  "
            + "			FROM                                                                                                                        "
            + "				b_in t1                                                                                                                 "
            + "				LEFT JOIN b_in_plan_detail t2 ON t1.plan_detail_id = t2.id                                                              "
            + "				LEFT JOIN b_in_order t3 ON t2.order_type = 'b_in_order' AND t2.order_id = t3.id                                         "
            + "				LEFT JOIN b_in_order_goods t4 ON t4.order_id = t3.id                                                                    "
            + "			GROUP BY                                                                                                                    "
            + "				t1.owner_id,                                                                                                            "
            + "				t1.warehouse_id,                                                                                                        "
            + "				t1.sku_id,                                                                                                              "
            + "				DATE_FORMAT( t1.u_time, '%Y-%m-%d' )                                                                                    "
            + "			) ttt1 ON ttt0.owner_id = ttt1.owner_id                                                                                     "
            + "			AND ttt0.warehouse_id = ttt1.warehouse_id                                                                                   "
            + "			AND ttt0.sku_id = ttt1.sku_id                                                                                               "
            + "			AND DATE_FORMAT( ttt1.dt, '%Y-%m-%d' ) >= DATE_FORMAT( DATE_SUB( ttt0.dt, INTERVAL 15 DAY ), '%Y-%m-%d' )                   "
            + "			INNER JOIN m_goods_spec ttt2 ON ttt0.sku_id = ttt2.id                                                                       "
            + "			INNER JOIN m_goods_spec_prop ttt3 ON ttt2.prop_id = ttt3.id                                                                 "
            + "			AND ttt3.CODE IN ( '0', '3' )                                                                                               "
            + "		GROUP BY                                                                                                                        "
            + "			ttt0.sku_id,                                                                                                                "
            + "			ttt0.owner_id,                                                                                                              "
            + "			ttt0.warehouse_id,                                                                                                          "
            + "			ttt0.dt                                                                                                                     "
            + "		) tab1 ON tab0.id = tab1.id                                                                                                                                                           "
            + "		SET tab0.price = tab1.price                                                                                                                                                           "
            + "	WHERE                                                                                                                                                                                     "
            + "		tab0.id = tab1.id                                                                                                                                                                     "
    })
    void updateTableDailyInventoryFinal_44();

    /**
     * 调价函修改价格
     */
    @Update({""
            + "		UPDATE b_daily_inventory_batch_final_temp ttt0                                                                       "
            + "		INNER JOIN b_purchase_pricing ttt1 ON ttt0.sku_code = ttt1.sku_code                                                  "
            + "		AND ttt0.dt BETWEEN ttt1.start_time                                                                                  "
            + "		AND ttt1.end_time                                                                                                    "
            + "		SET ttt0.price = ttt1.new_price                                                                                      "
    })
    void updateTableDailyInventoryFinal_45();

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
            "   	b_daily_inventory_batch_final_temp t1                                                                                             ",
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

    @Update({""
            + "	INSERT INTO b_daily_inventory_price (                                                                   "
            + "		dt,                                                                                                 "
            + "		warehouse_id,                                                                                       "
            + "		warehouse_code,                                                                                     "
            + "		warehouse_name,                                                                                     "
            + "		location_id,                                                                                        "
            + "		location_name,                                                                                      "
            + "		location_code,                                                                                      "
            + "		bin_id,                                                                                             "
            + "		bin_name,                                                                                           "
            + "		bin_code,                                                                                           "
            + "		sku_id,                                                                                             "
            + "		sku_code,                                                                                           "
            + "		sku_name,                                                                                           "
            + "		owner_id,                                                                                           "
            + "		owner_name,                                                                                         "
            + "		owner_code,                                                                                         "
            + "		qty,                                                                                                "
            + "		price,                                                                                              "
            + "		amount,                                                                                             "
            + "		unit_name,                                                                                          "
            + "		c_time,                                                                                             "
            + "		u_time                                                                                              "
            + "	) SELECT                                                                                                "
            + "	DATE_SUB( CURDATE(), INTERVAL #{p1,jdbcType=INTEGER} DAY ) dt,                                          "
            + "	t1.warehouse_id,                                                                                        "
            + "	t2.CODE warehouse_code,                                                                                 "
            + "	t2.NAME warehouse_name,                                                                                 "
            + "	t1.location_id,                                                                                         "
            + "	t3.NAME location_name,                                                                                  "
            + "	t3.CODE location_code,                                                                                  "
            + "	t1.bin_id,                                                                                              "
            + "	t4.NAME bin_name,                                                                                       "
            + "	t4.CODE bin_code,                                                                                       "
            + "	t1.sku_id,                                                                                              "
            + "	t5.CODE sku_code,                                                                                       "
            + "	t5.spec sku_name,                                                                                       "
            + "	t1.owner_id,                                                                                            "
            + "	t6.NAME owner_name,                                                                                     "
            + "	t6.CODE owner_code,                                                                                     "
            + "	t1.qty_avaible qty,                                                                                     "
            + "	IFNULL( t7.price, 0 ) price,                                                                            "
            + "	IFNULL( t7.price, 0 )* t1.qty_avaible amount,                                                           "
            + "	'吨' unit_name,                                                                                          "
            + "	NOW() c_time,                                                                                           "
            + "	NOW() u_time                                                                                            "
            + "	FROM                                                                                                    "
            + "		m_inventory t1                                                                                      "
            + "		LEFT JOIN m_warehouse t2 ON t1.warehouse_id = t2.id                                                 "
            + "		LEFT JOIN m_location t3 ON t1.location_id = t3.id                                                   "
            + "		LEFT JOIN m_bin t4 ON t1.bin_id = t4.id                                                             "
            + "		LEFT JOIN m_goods_spec t5 ON t1.sku_id = t5.id                                                      "
            + "		LEFT JOIN m_owner t6 ON t1.owner_id = t6.id                                                         "
            + "		LEFT JOIN b_daily_inventory t7 ON t1.sku_id = t7.sku_id                                             "
            + "		AND t1.owner_id = t7.owner_id                                                                       "
            + "		AND t1.warehouse_id = t7.warehouse_id                                                               "
            + "		AND t7.dt = DATE_FORMAT( DATE_SUB( CURDATE(), INTERVAL #{p1,jdbcType=INTEGER} DAY ), '%Y-%m-%d' )   "
    })
    void insertDailyPriceTable50(@Param("p1") Integer days);

    @Select({""
            + "	SELECT                                                                                                  "
            + "		t1.dt,                                                                                              "
            + "		t1.warehouse_id,                                                                                    "
            + "		t1.warehouse_code,                                                                                  "
            + "		t1.warehouse_name,                                                                                  "
            + "		ifnull( t2.short_name, t2.NAME ) warehouse_simple_name,                                             "
            + "		t1.location_id,                                                                                     "
            + "		t1.location_code,                                                                                   "
            + "		t1.location_name,                                                                                   "
            + "		ifnull( t3.short_name, t3.NAME ) location_simple_name,                                              "
            + "		t1.bin_id,                                                                                          "
            + "		t1.bin_code,                                                                                        "
            + "		t1.bin_name,                                                                                        "
            + "		t4.NAME bin_simple_name,                                                                            "
            + "		t10.id business_id,                                                                                 "
            + "		t10.CODE business_code,                                                                             "
            + "		t10.NAME business_name,                                                                             "
            + "		t9.id industry_id,                                                                                  "
            + "		t9.CODE industry_code,                                                                              "
            + "		t9.NAME industry_name,                                                                              "
            + "		t8.id category_id,                                                                                  "
            + "		t8.CODE category_code,                                                                              "
            + "		t8.NAME category_name,                                                                              "
            + "		t7.id goods_id,                                                                                     "
            + "		t7.NAME goods_name,                                                                                 "
            + "		t7.CODE goods_code,                                                                                 "
            + "		t11.code prop_id,                                                                                   "
            + "		t11.NAME prop_name,                                                                                 "
            + "		t1.sku_id,                                                                                          "
            + "		t1.sku_code,                                                                                        "
            + "		t1.sku_name,                                                                                        "
            + "		t1.owner_id,                                                                                        "
            + "		t1.owner_code,                                                                                      "
            + "		t1.owner_name,                                                                                      "
            + "		ifnull(t5.short_name, t5.name) owner_simple_name,                                                   "
            + "		t1.qty,                                                                                             "
            + "		t1.price,                                                                                           "
            + "		t1.amount,                                                                                          "
            + "		t1.unit_name,                                                                                       "
            + "		t1.c_time,                                                                                          "
            + "		t1.u_time                                                                                           "
            + "	FROM                                                                                                    "
            + "		b_daily_inventory_price t1                                                                          "
            + "		LEFT JOIN m_warehouse t2 ON t1.warehouse_id = t2.id                                                 "
            + "		LEFT JOIN m_location t3 ON t1.location_id = t3.id                                                   "
            + "		LEFT JOIN m_bin t4 ON t1.bin_id = t4.id                                                             "
            + "		LEFT JOIN m_owner t5 ON t1.owner_id = t5.id                                                         "
            + "		LEFT JOIN m_goods_spec t6 ON t1.sku_id = t6.id                                                      "
            + "		LEFT JOIN m_goods t7 ON t7.id = t6.goods_id                                                         "
            + "		LEFT JOIN m_category t8 ON t8.id = t7.category_id                                                   "
            + "		LEFT JOIN m_industry t9 ON t9.id = t8.industry_id                                                   "
            + "		LEFT JOIN m_business_type t10 ON t10.id = t9.business_id                                            "
            + "		LEFT JOIN m_goods_spec_prop t11 ON t11.id = t6.prop_id                                              "
            + "		WHERE t1.qty > 0                                                                                    "
    +""})
    List<ApiDailyInventoryPriceVo> selectDailyPriceTableAll();

    @Select({""
            + "	SELECT                                                                                                  "
            + "		t1.dt,                                                                                              "
            + "		t1.warehouse_id,                                                                                    "
            + "		t1.warehouse_code,                                                                                  "
            + "		t1.warehouse_name,                                                                                  "
            + "		ifnull( t2.short_name, t2.NAME ) warehouse_simple_name,                                             "
            + "		t1.location_id,                                                                                     "
            + "		t1.location_code,                                                                                   "
            + "		t1.location_name,                                                                                   "
            + "		ifnull( t3.short_name, t3.NAME ) location_simple_name,                                              "
            + "		t1.bin_id,                                                                                          "
            + "		t1.bin_code,                                                                                        "
            + "		t1.bin_name,                                                                                        "
            + "		t4.NAME bin_simple_name,                                                                            "
            + "		t10.id business_id,                                                                                 "
            + "		t10.CODE business_code,                                                                             "
            + "		t10.NAME business_name,                                                                             "
            + "		t9.id industry_id,                                                                                  "
            + "		t9.CODE industry_code,                                                                              "
            + "		t9.NAME industry_name,                                                                              "
            + "		t8.id category_id,                                                                                  "
            + "		t8.CODE category_code,                                                                              "
            + "		t8.NAME category_name,                                                                              "
            + "		t7.id goods_id,                                                                                     "
            + "		t7.NAME goods_name,                                                                                 "
            + "		t7.CODE goods_code,                                                                                 "
            + "		t11.code prop_id,                                                                                   "
            + "		t11.NAME prop_name,                                                                                 "
            + "		t1.sku_id,                                                                                          "
            + "		t1.sku_code,                                                                                        "
            + "		t1.sku_name,                                                                                        "
            + "		t1.owner_id,                                                                                        "
            + "		t1.owner_code,                                                                                      "
            + "		t1.owner_name,                                                                                      "
            + "		ifnull(t5.short_name, t5.name) owner_simple_name,                                                   "
            + "		t1.qty,                                                                                             "
            + "		t1.price,                                                                                           "
            + "		t1.amount,                                                                                          "
            + "		t1.unit_name,                                                                                       "
            + "		t1.c_time,                                                                                          "
            + "		t1.u_time                                                                                           "
            + "	FROM                                                                                                    "
            + "		b_daily_inventory_price t1                                                                          "
            + "		LEFT JOIN m_warehouse t2 ON t1.warehouse_id = t2.id                                                 "
            + "		LEFT JOIN m_location t3 ON t1.location_id = t3.id                                                   "
            + "		LEFT JOIN m_bin t4 ON t1.bin_id = t4.id                                                             "
            + "		LEFT JOIN m_owner t5 ON t1.owner_id = t5.id                                                         "
            + "		LEFT JOIN m_goods_spec t6 ON t1.sku_id = t6.id                                                      "
            + "		LEFT JOIN m_goods t7 ON t7.id = t6.goods_id                                                         "
            + "		LEFT JOIN m_category t8 ON t8.id = t7.category_id                                                   "
            + "		LEFT JOIN m_industry t9 ON t9.id = t8.industry_id                                                   "
            + "		LEFT JOIN m_business_type t10 ON t10.id = t9.business_id                                            "
            + "		LEFT JOIN m_goods_spec_prop t11 ON t11.id = t6.prop_id                                              "
            +"      WHERE t1.dt = DATE_FORMAT( DATE_SUB( CURDATE(), INTERVAL 2 DAY ), '%Y-%m-%d' ) and t1.qty >0        "
            +""})
    List<ApiDailyInventoryPriceVo> selectDailyPriceTableLatest();

    @Update({""
            + "	insert into b_material_daily_price(                                                                                                                                           "
            + "		dt,                                                                                                                                                                       "
            + "		goods_id,                                                                                                                                                                 "
            + "		goods_code,                                                                                                                                                               "
            + "		goods_name,                                                                                                                                                               "
            + "		sku_id,                                                                                                                                                                   "
            + "		sku_code,                                                                                                                                                                 "
            + "		sku_name,                                                                                                                                                                 "
            + "		type,                                                                                                                                                                     "
            + "		query_code,                                                                                                                                                               "
            + "		price,                                                                                                                                                                    "
            + "		c_time                                                                                                                                                                    "
            + "	)                                                                                                                                                                             "
            + "	SELECT                                                                                                                                                                        "
            + "	    NOW() dt,                                                                                                                                                                 "
            + "		t1.goods_id,                                                                                                                                                              "
            + "		t2.code goods_code,                                                                                                                                                       "
            + "		t2.name goods_name,                                                                                                                                                       "
            + "		t1.id sku_id,                                                                                                                                                             "
            + "		t1.code sku_code,                                                                                                                                                         "
            + "		t1.spec sku_name,                                                                                                                                                         "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN '2'                                                                                                                               "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN '0'                                                                                                                               "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN '1'                                                                                                                              "
            + "		END type,                                                                                                                                                                 "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN t4.code                                                                                                                           "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN t4.code                                                                                                                           "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN t5.code                                                                                                                          "
            + "		END query_code,                                                                                                                                                           "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN t4.price                                                                                                                          "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN t4.price                                                                                                                          "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN t5.price                                                                                                                         "
            + "		END price,                                                                                                                                                                "
            + "		NOW() c_time                                                                                                                                                              "
            + "	FROM                                                                                                                                                                          "
            + "		m_goods_spec t1                                                                                                                                                           "
            + "		LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                                                                                               "
            + "		LEFT JOIN m_goods_spec_prop t3 on t1.prop_id = t3.id                                                                                                                      "
            + "		LEFT JOIN b_material_convert_price t4 on t1.id = t4.source_sku_id and DATE_FORMAT( t4.dt, '%Y-%m-%d' ) = DATE_FORMAT( NOW(), '%Y-%m-%d' )                                 "
            + "			LEFT JOIN (                                                                                                                                                           "
            + "			SELECT                                                                                                                                                                "
            + "			 code,                                                                                                                                                                "
            + "				sku_id,                                                                                                                                                           "
            + "				price,                                                                                                                                                            "
            + "				price_dt,                                                                                                                                                         "
            + "				c_time,                                                                                                                                                           "
            + "				row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                                                        "
            + "			FROM                                                                                                                                                                  "
            + "				b_goods_price                                                                                                                                                     "
            + "			) t5 ON t1.id = t5.sku_id                                                                                                                                             "
            + "			AND t5.row_num = 1                                                                                                                                                    "
            + "			AND DATE_FORMAT( t5.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( NOW(), '%Y-%m-%d' )                                                                                        "
    })
    void insertMaterialDailyPriceTable51();

    @Update({""
            + "	insert into b_material_daily_price(                                                                                                                                           "
            + "		goods_id,                                                                                                                                                                 "
            + "		goods_code,                                                                                                                                                               "
            + "		goods_name,                                                                                                                                                               "
            + "		sku_id,                                                                                                                                                                   "
            + "		sku_code,                                                                                                                                                                 "
            + "		sku_name,                                                                                                                                                                 "
            + "		type,                                                                                                                                                                     "
            + "		query_code,                                                                                                                                                               "
            + "		price,                                                                                                                                                                    "
            + "		c_time                                                                                                                                                                    "
            + "	)                                                                                                                                                                             "
            + "	SELECT                                                                                                                                                                        "
            + "		t1.goods_id,                                                                                                                                                              "
            + "		t2.code goods_code,                                                                                                                                                       "
            + "		t2.name goods_name,                                                                                                                                                       "
            + "		t1.id sku_id,                                                                                                                                                             "
            + "		t1.code sku_code,                                                                                                                                                         "
            + "		t1.spec sku_name,                                                                                                                                                         "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN '2'                                                                                                                               "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN '0'                                                                                                                               "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN '1'                                                                                                                              "
            + "		END type,                                                                                                                                                                 "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN t4.code                                                                                                                           "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN t4.code                                                                                                                           "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN t5.code                                                                                                                          "
            + "		END query_code,                                                                                                                                                           "
            + "		CASE WHEN t4.source_sku_id IS NULL THEN t4.price                                                                                                                          "
            + "		 WHEN t4.source_sku_id = t4.sku_id THEN t4.price                                                                                                                          "
            + "		 WHEN t4.source_sku_id <> t4.sku_id THEN t5.price                                                                                                                         "
            + "		END price,                                                                                                                                                                "
            + "		NOW() c_time                                                                                                                                                              "
            + "	FROM                                                                                                                                                                          "
            + "		m_goods_spec t1                                                                                                                                                           "
            + "		LEFT JOIN m_goods t2 ON t1.goods_id = t2.id                                                                                                                               "
            + "		LEFT JOIN m_goods_spec_prop t3 on t1.prop_id = t3.id                                                                                                                      "
            + "		LEFT JOIN b_material_convert_price t4 on t1.id = t4.source_sku_id and DATE_FORMAT( t4.dt, '%Y-%m-%d' ) = DATE_FORMAT( NOW(), '%Y-%m-%d' )                                 "
            + "			LEFT JOIN (                                                                                                                                                           "
            + "			SELECT                                                                                                                                                                "
            + "			 code,                                                                                                                                                                "
            + "				sku_id,                                                                                                                                                           "
            + "				price,                                                                                                                                                            "
            + "				price_dt,                                                                                                                                                         "
            + "				c_time,                                                                                                                                                           "
            + "				row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num                                                                        "
            + "			FROM                                                                                                                                                                  "
            + "				b_goods_price                                                                                                                                                     "
            + "			) t5 ON t1.id = t5.sku_id                                                                                                                                             "
            + "			AND t5.row_num = 1                                                                                                                                                    "
            + "			AND DATE_FORMAT( t5.price_dt, '%Y-%m-%d' ) <= DATE_FORMAT( NOW(), '%Y-%m-%d' )                                                                                        "
    })
    void insertMaterialPriceTable52();
}
