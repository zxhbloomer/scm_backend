package com.xinyirun.scm.core.system.mapper.sys.schedule.v1;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.system.bo.inventory.daily.BDailyInventoryWorkBo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关于库存日报表的mapper
 */
@Deprecated
@Repository
public interface SBDailyInventoryMappper extends BaseMapper<BDailyInventoryEntity> {

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
     * 删除每日库存表
     */
    @Delete(
            "       delete from b_daily_inventory ;                                               "
    )
    int deleteTableDailyInventory13();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_daily_inventory_work ;                                               "
    )
    List<Integer> deleteTemoraryTableDailyInventoryWork11();

    /**
     * 插入数据,尚未清洗的数据源，work
     */
    @Update({
            "         insert into b_daily_inventory_work                                                          "
            + "         (                                                                       "
            + "           dt,                                                                   "
            + "           type,                                                                 "
            + "           bill_type,                                                            "
            + "           plan_id,                                                              "
            + "           owner_id,                                                             "
            + "           owner_code,                                                           "
            + "           sku_id,                                                               "
            + "           sku_code,                                                             "
            + "           warehouse_id,                                                         "
            + "           location_id,                                                          "
            + "           bin_id,                                                               "
            + "           qty,                                                                  "
            + "           price,                                                                "
            + "           amount,                                                               "
            + "           unit_id,                                                              "
            + "           u_time                                                                "
            + "         )                                                                       "
            + "         select t1.inbound_time dt,                                              "
            + "                '01' as `type`,                                                  "
            + "                t1.`type` as bill_type,                                          "
            + "                t1.plan_id ,                                                     "
            + "                t1.owner_id ,                                                    "
            + "         	   t1.owner_code ,                                                  "
            + "         	   t1.sku_id ,                                                      "
            + "         	   t1.sku_code ,                                                    "
            + "         	   t1.warehouse_id ,                                                "
            + "         	   t1.location_id ,                                                 "
            + "         	   t1.bin_id ,                                                      "
            + "         	   t1.actual_weight as qty,                                         "
            + "         	   t1.price as price,                                               "
            + "         	   t1.amount as amount,                                             "
            + "         	   t1.unit_id,                                                      "
            + "         	   t1.u_time                                                        "
            + "           from b_in t1                                                          "
            + "           where t1.status in (1,2)                                              "
            + "           union all                                                             "
            + "         select t1.outbound_time dt,                                             "
            + "         	   '02' as `type`,                                                  "
            + "         	   t1.`type` as bill_type,                                          "
            + "         	   t1.plan_id ,                                                     "
            + "                t1.owner_id ,                                                    "
            + "         	   t1.owner_code ,                                                  "
            + "         	   t1.sku_id ,                                                      "
            + "         	   t1.sku_code ,                                                    "
            + "         	   t1.warehouse_id ,                                                "
            + "         	   t1.location_id ,                                                 "
            + "         	   t1.bin_id ,                                                      "
            + "         	   actual_weight as qty,                                            "
            + "         	   0 as price,                                                      "
            + "         	   0 as amount,                                                     "
            + "         	   t1.unit_id,                                                      "
            + "         	   t1.u_time                                                        "
            + "           from b_out t1                                                         "
            + "           where t1.status in (1,2)                                              "
            + "           union all                                                             "
            + "         		select                                                          "
            + "         	       t1.e_dt as dt,                                               "
            + "         	       '03' as `type`,                                              "
            + "         	       t2.type as bill_type,                                        "
            + "         	       null as plan_id ,                                            "
            + "         	       t2.owner_id ,                                                "
            + "         		   t2.owner_code ,                                              "
            + "         		   t1.sku_id ,                                                  "
            + "         		   t1.sku_code ,                                                "
            + "         		   t1.warehouse_id ,                                            "
            + "         		   t1.location_id ,                                             "
            + "         		   t1.bin_id ,                                                  "
            + "         		   t1.qty_adjust - t1.qty as qty,                               "
            + "         		   0 as price,                                                  "
            + "         		   0 as amount,                                                 "
            + "            		   null as unit_id,                                             "
            + "            		   t1.u_time                                                    "
            + "         	  from b_adjust_detail t1                                           "
            + "          left join b_adjust t2 on t1.adjust_id = t2.id                          "
            + "              where t1.status = 2                                                "
            + "                                                 "
    })
    void createTemoraryData20();

    /**
     * 出库数据：汇总
     */
    @Update({
            "         insert into b_daily_inventory_temp                                        "
            + "        (                                                                        "
            + "         	dt,                                                                 "
            + "         	owner_id ,                                                          "
            + "         	owner_code ,                                                        "
            + "         	sku_id ,                                                            "
            + "         	sku_code ,                                                          "
            + "         	warehouse_id ,                                                      "
            + "         	location_id ,                                                       "
            + "         	bin_id ,                                                            "
            + "         	qty_out                                                             "
            + "         )                                                                       "
            + "         	select                                                              "
            + "         	       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt,                         "
            + "         	       t1.owner_id,                                                 "
            + "         	       t1.owner_code,                                               "
            + "         	       t1.sku_id,                                                   "
            + "         	       t1.sku_code,                                                 "
            + "         	       t1.warehouse_id ,                                            "
            + "         	       t1.location_id ,                                             "
            + "         	       t1.bin_id ,                                                  "
            + "         	       sum(t1.qty) as qty_out                                       "
            + "         	  from b_daily_inventory_work t1                                    "
            + "         	 where t1.`type` ='02'                                               "
            + "           group by DATE_FORMAT(t1.dt,'%Y-%m-%d'),                               "
            + "         	       t1.owner_id,                                                 "
            + "         	       t1.sku_id,                                                   "
            + "         	       t1.warehouse_id ,                                            "
            + "         	       t1.location_id ,                                             "
            + "         	       t1.bin_id                                                    "
            + "           order by t1.dt                                                        "
            + "                                                 "
    })
    void createTemoraryDataOutInsert30();

    /**
     * 清洗调整数据，新增，汇总
     */
    @Update({
            "         insert into b_daily_inventory_temp                                        "
            + "         (                                                                       "
            + "         	dt,                                                                 "
            + "         	owner_id ,                                                          "
            + "         	owner_code ,                                                        "
            + "         	sku_id ,                                                            "
            + "         	sku_code ,                                                          "
            + "         	warehouse_id ,                                                      "
            + "         	location_id ,                                                       "
            + "         	bin_id ,                                                            "
            + "         	qty_adjust                                                          "
            + "         )                                                                       "
            + "           select *                                                              "
            + "             from                                                                "
            + "                 (  select                                                       "
            + "         		       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt,                     "
            + "         		       t1.owner_id,                                             "
            + "         		       t1.owner_code,                                           "
            + "         		       t1.sku_id,                                               "
            + "         		       t1.sku_code,                                             "
            + "         		       t1.warehouse_id ,                                        "
            + "         		       t1.location_id ,                                         "
            + "         		       t1.bin_id ,                                              "
            + "         		       sum(t1.qty) as qty_out                                   "
            + "         		  from b_daily_inventory_work t1                                "
            + "         		 where t1.`type` ='03'                                          "
            + "         	  group by DATE_FORMAT(t1.dt,'%Y-%m-%d'),                           "
            + "         		       t1.owner_id,                                             "
            + "         		       t1.sku_id,                                               "
            + "         		       t1.warehouse_id ,                                        "
            + "         		       t1.location_id ,                                         "
            + "         		       t1.bin_id ) tab1                                         "
            + "         	where not exists (                                                  "
            + "         		select 1                                                        "
            + "         		  from b_daily_inventory_temp tab2                              "
            + "         	     where  tab1.dt = tab2.dt                                        "
            + "         	       and tab1.owner_id = tab2.owner_id                            "
            + "         	       and tab1.sku_id = tab2.sku_id                                "
            + "         	       and tab1.warehouse_id = tab2.warehouse_id                    "
            + "         	       and tab1.location_id = tab2.location_id                      "
            + "         	       and tab1.bin_id = tab2.bin_id                                "
            + "         	)                                                                   "
            + "                                                 "
    })
    void createTemoraryDataAdjustInsert40();

    /**
     * 清洗调整数据，更新，汇总
     */
    @Update({
            "                                                                                    "
            + "    update  b_daily_inventory_temp tab1                                           "
            + "       inner join                                                                 "
            + "    		(select                                                                  "
            + "    		       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt,                              "
            + "    		       t1.owner_id,                                                      "
            + "    		       t1.owner_code,                                                    "
            + "    		       t1.sku_id,                                                        "
            + "    		       t1.sku_code,                                                      "
            + "    		       t1.warehouse_id ,                                                 "
            + "    		       t1.location_id ,                                                  "
            + "    		       t1.bin_id ,                                                       "
            + "    		       sum(t1.qty) as qty_adjust                                         "
            + "    		  from b_daily_inventory_work t1                                         "
            + "    		 where t1.`type` ='03'                                                   "
            + "    	  group by DATE_FORMAT(t1.dt,'%Y-%m-%d'),                                    "
            + "    		       t1.owner_id,                                                      "
            + "    		       t1.sku_id,                                                        "
            + "    		       t1.warehouse_id ,                                                 "
            + "    		       t1.location_id ,                                                  "
            + "    		       t1.bin_id ) tab2                                                  "
            + "    	        on tab1.dt = tab2.dt                                                 "
            + "    	       and tab1.owner_id = tab2.owner_id                                     "
            + "    	       and tab1.sku_id = tab2.sku_id                                         "
            + "    	       and tab1.warehouse_id = tab2.warehouse_id                             "
            + "    	       and tab1.location_id <=> tab2.location_id                             "
            + "    	       and tab1.bin_id <=> tab2.bin_id                                       "
            + "         set tab1.qty_adjust = tab2.qty_adjust                                    "
            + "     where exists                                                                 "
            + "     		(select 1                                                            "
            + "    		  from b_daily_inventory_work t1                                         "
            + "    		 where t1.`type` ='03'                                                   "
            + "    		   and tab1.dt = tab2.dt                                                 "
            + "    	       and tab1.owner_id = tab2.owner_id                                     "
            + "    	       and tab1.sku_id = tab2.sku_id                                         "
            + "    	       and tab1.warehouse_id = tab2.warehouse_id                             "
            + "    	       and tab1.location_id <=> tab2.location_id                             "
            + "    	       and tab1.bin_id <=> tab2.bin_id )                                     "
            + "                                                 "
    })
    void createTemoraryDataAdjustUpdate50();

    /**
     * 清洗入库数据，新增，汇总
     */
    @Update({
            "         insert into b_daily_inventory_temp                                        "
            + "       (                                                              "
            + "       	dt,                                                          "
            + "       	owner_id ,                                                   "
            + "       	owner_code ,                                                 "
            + "       	sku_id ,                                                     "
            + "       	sku_code ,                                                   "
            + "       	warehouse_id ,                                               "
            + "       	location_id ,                                                "
            + "       	bin_id ,                                                     "
            + "       	qty_in                                                       "
            + "       )                                                              "
            + "         select *                                                     "
            + "           from                                                       "
            + "       		(select                                                  "
            + "       		       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt,              "
            + "       		       t1.owner_id,                                      "
            + "       		       t1.owner_code,                                    "
            + "       		       t1.sku_id,                                        "
            + "       		       t1.sku_code,                                      "
            + "       		       t1.warehouse_id ,                                 "
            + "       		       t1.location_id ,                                  "
            + "       		       t1.bin_id ,                                       "
            + "       		       sum(t1.qty) as qty_in                             "
            + "       		  from b_daily_inventory_work t1                         "
            + "       		 where t1.`type` ='01'                                   "
            + "       	  group by DATE_FORMAT(t1.dt,'%Y-%m-%d'),                    "
            + "       		       t1.owner_id,                                      "
            + "       		       t1.sku_id,                                        "
            + "       		       t1.warehouse_id ,                                 "
            + "       		       t1.location_id ,                                  "
            + "       		       t1.bin_id ) tab1                                  "
            + "       	where not exists (                                           "
            + "       		select 1                                                 "
            + "       		  from b_daily_inventory_temp tab2                       "
            + "       	     where tab1.dt = tab2.dt                                 "
            + "       	       and tab1.owner_id = tab2.owner_id                     "
            + "       	       and tab1.sku_id = tab2.sku_id                         "
            + "       	       and tab1.warehouse_id = tab2.warehouse_id             "
            + "       	       and tab1.location_id = tab2.location_id               "
            + "       	       and tab1.bin_id = tab2.bin_id                         "
            + "       	)                                                            "
            + "                                                 "
    })
    void createTemoraryDataInInsert60();

    /**
     * 清洗入库数据，更新，汇总
     */
    @Update({
            "                          "
            + "      update  b_daily_inventory_temp tab1                            "
            + "         inner join                                                  "
            + "      		(select                                                 "
            + "      		       DATE_FORMAT(t1.dt,'%Y-%m-%d') as dt,             "
            + "      		       t1.owner_id,                                     "
            + "      		       t1.owner_code,                                   "
            + "      		       t1.sku_id,                                       "
            + "      		       t1.sku_code,                                     "
            + "      		       t1.warehouse_id ,                                "
            + "      		       t1.location_id ,                                 "
            + "      		       t1.bin_id ,                                      "
            + "      		       sum(t1.qty) as qty_in                            "
            + "      		  from b_daily_inventory_work t1                        "
            + "      		 where t1.`type` ='01'                                  "
            + "      	  group by DATE_FORMAT(t1.dt,'%Y-%m-%d'),                   "
            + "      		       t1.owner_id,                                     "
            + "      		       t1.sku_id,                                       "
            + "      		       t1.warehouse_id ,                                "
            + "      		       t1.location_id ,                                 "
            + "      		       t1.bin_id ) tab2                                 "
            + "      	        on tab1.dt = tab2.dt                                "
            + "      	       and tab1.owner_id = tab2.owner_id                    "
            + "      	       and tab1.sku_id = tab2.sku_id                        "
            + "      	       and tab1.warehouse_id = tab2.warehouse_id            "
            + "      	       and tab1.location_id <=> tab2.location_id            "
            + "      	       and tab1.bin_id <=> tab2.bin_id                      "
            + "           set tab1.qty_in = tab2.qty_in                             "
            + "       where exists                                                  "
            + "       		(select 1                                               "
            + "      		  from b_daily_inventory_work t1                        "
            + "      		 where tab1.dt = tab2.dt                                "
            + "      	       and tab1.owner_id = tab2.owner_id                    "
            + "      	       and tab1.sku_id = tab2.sku_id                        "
            + "      	       and tab1.warehouse_id = tab2.warehouse_id            "
            + "      	       and tab1.location_id <=> tab2.location_id            "
            + "      	       and tab1.bin_id <=> tab2.bin_id )                    "
            + "                                                 "
    })
    void createTemoraryDataInUpdate70();


    /**
     * 抽取数据
     * @return
     */
    @Select("                                                               " +
            "           select                                                                                              " +
            "      	          lag(id, 1) over ( partition by t1.warehouse_id ,                                              " +
            "                                                 t1.location_id ,                                              " +
            "                                                 t1.bin_id ,                                                   " +
            "                                                 t1.owner_id ,                                                 " +
            "                                                 t1.sku_id                                                     " +
            "                                        order by t1.warehouse_id ,                                             " +
            "                                                 t1.location_id ,                                              " +
            "                                                 t1.bin_id ,                                                   " +
            "                                                 t1.owner_id ,                                                 " +
            "                                                 t1.sku_id ,                                                   " +
            "                                                 t1.dt ,                                                       " +
            "                                                 t1.u_time ) as lag_id,                                        " +
            "                  t1.id as data_id,                                                                            " +
            "                  t1.*                                                                                         " +
            "             from b_daily_inventory_work t1                                                                    " +
            "         order by t1.warehouse_id ,                                                                            " +
            "      	           t1.location_id ,                                                                             " +
            "      	           t1.bin_id ,                                                                                  " +
            "      	           t1.owner_id ,                                                                                " +
            "      	           t1.sku_id ,                                                                                  " +
            "      	           t1.dt ,                                                                                      " +
            "      	           t1.u_time                                                                                    " +
            "                          " +
            "   ")
    List<BDailyInventoryWorkBo> getWorkBoList();


    /**
     * 批量插入
     * @param workBoList
     */
    @Insert({
            " <script>  ",
            "         insert into b_daily_inventory_work                                      ",
            "         (                                                                       ",
            "           dt,                                                                   ",
            "           type,                                                                 ",
            "           bill_type,                                                            ",
            "           plan_id,                                                              ",
            "           owner_id,                                                             ",
            "           owner_code,                                                           ",
            "           sku_id,                                                               ",
            "           sku_code,                                                             ",
            "           warehouse_id,                                                         ",
            "           location_id,                                                          ",
            "           bin_id,                                                               ",
            "           qty,                                                                  ",
            "           price,                                                                ",
            "           inventory_qty,                                                               ",
            "           average_price,                                                               ",
            "           amount,                                                               ",
            "           unit_id,                                                              ",
            "           u_time                                                                ",
            "         ) values                                                                ",
            "       <foreach collection='p1' item='item' index='index' separator=','>         ",
            "   (                                                                             ",
            "          #{item.dt},                                           ",
            "          #{item.type},                                           ",
            "          #{item.bill_type,jdbcType=VARCHAR},                                      ",
            "          #{item.plan_id},                                        ",
            "          #{item.owner_id},                                       ",
            "          #{item.owner_code},                                     ",
            "          #{item.sku_id},                                         ",
            "          #{item.sku_code},                                       ",
            "          #{item.warehouse_id},                                   ",
            "          #{item.location_id},                                    ",
            "          #{item.bin_id},                                         ",
            "          #{item.qty},                                            ",
            "          #{item.price},                                          ",
            "          #{item.inventory_qty},                                   ",
            "          #{item.average_price},                                  ",
            "          #{item.amount},                                         ",
            "          #{item.unit_id},                                         ",
            "          #{item.u_time}                                         ",
            "   )                                                                             ",
            "    </foreach>                                                                   ",
            "   </script>"
    })
    void insertBatchWorkTable(@Param("p1") List<BDailyInventoryWorkBo> workBoList);

    /**
     * 更新到b_daily_inventory_temp中，更新当日的average_price（移动平均单价），inventory_qty（当时的库存）,货值（amount）
     */
    @Update({
            "    update                                                               ",
            "    	b_daily_inventory_temp tab1                                       ",
            "    inner join (                                                         ",
            "    	select                                                            ",
            "    		DATE_FORMAT(tt.dt, '%Y-%m-%d') as dt,                         ",
            "    		tt.owner_id,                                                  ",
            "    		tt.sku_id,                                                    ",
            "    		tt.warehouse_id,                                              ",
            "    		tt.location_id,                                               ",
            "    		tt.bin_id,                                                    ",
            "    		tt.inventory_qty,                                             ",
            "    		tt.average_price,                                             ",
            "    		tt.amount                                                     ",
            "    	from                                                              ",
            "    		(                                                             ",
            "    		select                                                        ",
            "    			row_number() over (partition by w.warehouse_id ,          ",
            "    			w.location_id ,                                           ",
            "    			w.owner_id ,                                              ",
            "    			w.sku_id ,                                                ",
            "    			DATE_FORMAT(w.dt, '%Y-%m-%d')                             ",
            "    		order by                                                      ",
            "    			w.warehouse_id ,                                          ",
            "    			w.location_id ,                                           ",
            "    			w.owner_id ,                                              ",
            "    			w.sku_id ,                                                ",
            "    			w.dt desc ,                                               ",
            "    			w.u_time desc ) as rn,                                    ",
            "    			w.*                                                       ",
            "    		from                                                          ",
            "    			b_daily_inventory_work w )tt                              ",
            "    	where                                                             ",
            "    		tt.rn = 1) tab2 on                                            ",
            "    	tab1.dt = tab2.dt                                                 ",
            "    	and tab1.owner_id = tab2.owner_id                                 ",
            "    	and tab1.sku_id = tab2.sku_id                                     ",
            "    	and tab1.warehouse_id = tab2.warehouse_id                         ",
            "    	and tab1.location_id = tab2.location_id                           ",
            "    	and tab1.bin_id = tab2.bin_id                                     ",
            "    set                                                                  ",
            "    	tab1.qty = tab2.inventory_qty,                                    ",
            "    	tab1.price = tab2.average_price,                                  ",
            "    	tab1.amount = tab2.amount                                         ",
            "    where true                                                           ",
            "               ",
    })
    void updateWorkTableFinal();

    /**
     * 删除数据临时表
     */
    @Delete(
        "       delete from b_daily_inventory_temp2 ;                                "
    )
    void deleteTableDailyInventoryTemp2_100();
    /**
     * 锁定临时表
     */
    @Select(
        "       select 1 from b_daily_inventory_temp2 for update nowait;                                               "
    )
    List<Integer> lockTableDailyInventoryTemp2_100();
    /**
     * 开始处理temp表：
     * 在这里，需要有个temp2，来处理：
     * 1、保持每天数据的完整性，例如1/1有数据，今天日期为2/1，期间即使没有数据，也要自动生成1个月的数据
     * 2、自动生成的数据保持为上一天的数据（仓库、库区、库位、货主、sku）
     */
    @Update({
            "                                          "
            + "      insert into b_daily_inventory_temp2 (                "
            + "      	dt,                                                         "
            + "      	owner_id,                                                   "
            + "      	owner_code,                                                 "
            + "      	sku_id,                                                     "
            + "      	sku_code,                                                   "
            + "      	warehouse_id,                                               "
            + "      	location_id,                                                "
            + "      	bin_id,                                                     "
            + "      	qty,                                                        "
            + "      	qty_in,                                                     "
            + "      	qty_out,                                                    "
            + "      	qty_adjust,                                                 "
            + "      	price,                                                      "
            + "      	amount,                                                     "
            + "      	unit_id,                                                    "
            + "      	realtime_amount,                                            "
            + "      	realtime_price,                                             "
            + "      	u_time                                                      "
            + "      )                                                              "
            + "      select tab1.calendar as dt ,                                   "
            + "             tab1.owner_id,                                          "
            + "             tab1.owner_code ,                                       "
            + "             tab1.sku_id,                                            "
            + "             tab1.sku_code ,                                         "
            + "             tab1.warehouse_id,                                      "
            + "             tab1.location_id,                                       "
            + "             tab1.bin_id,                                            "
            + "             tab2.qty ,                                              "
            + "             tab2.qty_in ,                                           "
            + "             tab2.qty_out ,                                          "
            + "             tab2.qty_adjust ,                                       "
            + "             tab2.price ,                                            "
            + "             tab2.amount ,                                           "
            + "             tab2.unit_id ,                                          "
            + "             tab2.realtime_amount ,                                  "
            + "             tab2.realtime_price ,                                   "
            + "             now(3)                                                  "
            + "        from (                                                       "
            + "                select sub1.calendar,                                "
            + "                       sub2.*                                        "
            + "                  from ( select `date` as calendar                   "
            + "                           from s_calendar t1                        "
            + "                          where t1.`date` between (select min(dt) from b_daily_inventory_temp) and now(3)                "
            + "                        ) sub1 ,                                     "
            + "                        (                                            "
            + "      					  select t1.warehouse_id ,                  "
            + "      					         t1.location_id ,                   "
            + "      					         t1.bin_id ,                        "
            + "      					         t1.owner_id ,                      "
            + "      					         t1.owner_code,                     "
            + "      					         t1.sku_id ,                        "
            + "      					         t1.sku_code,                       "
            + "      					         count(1),                          "
            + "      					         min(dt) min_dt                     "
            + "      					    from b_daily_inventory_temp t1          "
            + "      					group by t1.warehouse_id ,                  "
            + "      					         t1.location_id ,                   "
            + "      					         t1.bin_id ,                        "
            + "      					         t1.owner_id ,                      "
            + "      					         t1.sku_id                          "
            + "                        ) sub2                                       "
            + "                  where sub1.calendar >= sub2.min_dt                 "
            + "      ) tab1 left join b_daily_inventory_temp tab2                   "
            + "                    on tab1.calendar = tab2.dt                       "
            + "                   and tab1.warehouse_id = tab2.warehouse_id         "
            + "                   and tab1.location_id = tab2.location_id           "
            + "                   and tab1.bin_id = tab2.bin_id                     "
            + "                   and tab1.owner_id = tab2.owner_id                 "
            + "                   and tab1.sku_id = tab2.sku_id                     "
            + "       order by tab1.calendar                                        "
            + "                                                 "
    })
    void createTableDailyInventoryTemp2_100();

    @Update({
            "                                          "
                    + "          update b_daily_inventory_temp2 t1                                                                "
                    + "      inner join b_daily_inventory_temp2 t2                                                                "
                    + "              on t1.warehouse_id = t2.warehouse_id                                                         "
                    + "             and t1.location_id = t2.location_id                                                           "
                    + "             and t1.bin_id = t2.bin_id                                                                     "
                    + "             and t1.owner_id = t2.owner_id                                                                 "
                    + "             and t1.sku_id = t2.sku_id                                                                     "
                    + "             and t2.dt = f_get_dt(t1.warehouse_id,t1.location_id,t1.bin_id,t1.owner_id,t1.sku_id,t1.dt)    "
                    + "             set t1.qty = t2.qty,                                                                          "
                    + "                 t1.qty_in = t2.qty_in ,                                                                   "
                    + "                 t1.qty_out = t2.qty_out ,                                                                 "
                    + "                 t1.qty_adjust = t2.qty_adjust ,                                                           "
                    + "                 t1.price = t2.price ,                                                                     "
                    + "                 t1.amount =	t2.amount ,                                                                   "
                    + "     	        t1.unit_id = t2.unit_id                                                                   "
                    + "                                                                                                           "
    })
    void updateTableDailyInventoryTemp2_100();

    /**
     * 最后插入到每日库存表中
     */
    @Update({
            "   insert                                   ",
            "   	into                                 ",
            "   	b_daily_inventory ( dt,              ",
            "   	owner_id,                            ",
            "   	owner_code,                          ",
            "   	sku_id,                              ",
            "   	sku_code,                            ",
            "   	warehouse_id,                        ",
            "   	location_id,                         ",
            "   	bin_id,                              ",
            "   	qty,                                 ",
            "   	qty_in,                              ",
            "   	qty_out,                             ",
            "   	qty_adjust,                          ",
            "   	price,                               ",
            "   	inventory_amount,                    ",
            "   	unit_id,                             ",
            "   	realtime_amount,                     ",
            "   	realtime_price,                      ",
            "   	c_time,                              ",
            "   	u_time )                             ",
            "   select                                   ",
            "   	t1.dt,                               ",
            "   	t1.owner_id,                         ",
            "   	t1.owner_code,                       ",
            "   	t1.sku_id,                           ",
            "   	t1.sku_code,                         ",
            "   	t1.warehouse_id,                     ",
            "   	t1.location_id,                      ",
            "   	t1.bin_id,                           ",
            "   	t1.qty,                              ",
            "   	t1.qty_in,                           ",
            "   	t1.qty_out,                          ",
            "   	t1.qty_adjust,                       ",
            "   	t1.price,                            ",
            "   	t1.amount,                           ",
            "   	t1.unit_id,                          ",
            "   	0,                                   ",
            "   	0,                                   ",
            "   	now(),                               ",
            "   	now()                                ",
            "   from                                     ",
            "   	b_daily_inventory_temp2 t1            ",
            "               ",
    })
    void insertTableDailyInventoryFinal();
}
