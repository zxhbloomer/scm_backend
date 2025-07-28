package com.xinyirun.scm.core.system.mapper.sys.schedule.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.wms.inventory.BDailyAveragePriceEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDailyAveragePriceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关于库存日报表的mapper
 */
@Repository
public interface SBDailyAveragePriceV2Mappper extends BaseMapper<BDailyAveragePriceEntity> {

    /**
     * 删除当天数据
     */
    @Delete(""
            + "DELETE                                                                                                   "
            + "FROM                                                                                                     "
            + " b_daily_average_price t                                                                                 "
            + "WHERE                                                                                                    "
            + " DATE_FORMAT( t.dt, '%Y%m%d' ) = DATE_FORMAT(now(), '%Y%m%d' )                                           "
    +"")
    public void deleteIntradayData();

    /**
     * 查询平均单价
     */
    @Select(""
            + "			SELECT                                                                                          "
            + "			 t1.sku_id,                                                                                     "
            + "			 t2.code sku_code,                                                                              "
            + "			 t1.warehouse_id,                                                                               "
            + "			 t3.code warehouse_code,                                                                        "
            + "			 t1.location_id,                                                                                "
            + "			 t4.code location_code,                                                                         "
            + "			 t1.bin_id,                                                                                     "
            + "			 t5.code bin_code,                                                                              "
//            + "			 t1.price,                                                                                      "
//            + "			 t1.amount,                                                                                     "

            + "          CASE WHEN t7.sku_id is not null THEN t1.price ELSE t6.price END AS price,                      "
            + "          CASE WHEN t7.sku_id is not null THEN (t1.qty_avaible+t1.qty_lock)*t1.price                     "
            + "              ELSE (t1.qty_avaible+t1.qty_lock)*t7.price END AS amount,                                  "
            + "          CASE WHEN t7.sku_id is not null THEN true ELSE false END AS is_convert,                        "

            + "			 now() dt                                                                                       "
            + "			FROM                                                                                            "
            + "			 m_inventory t1                                                                                 "
            + "			 LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                                                 "
            + "			 LEFT JOIN m_warehouse t3 ON t1.warehouse_id = t3.id                                            "
            + "			 LEFT JOIN m_location t4 ON t1.location_id = t4.id                                              "
            + "			 LEFT JOIN m_bin t5 ON t1.bin_id = t5.id                                                        "
            + "          LEFT JOIN(  SELECT                                                                             "
            + "         	sku_id,                                                                                     "
            + "         	price,                                                                                      "
            + "         	price_dt,                                                                                   "
            + "         	c_time,                                                                                     "
            + "         	row_number ( ) over ( PARTITION BY sku_id ORDER BY price_dt DESC, c_time DESC ) AS row_num  "
            + "             FROM                                                                                        "
            + "             	b_goods_price                                                                           "
            + "                )t6 on t6.sku_id = t1.sku_id and t6.row_num = 1                                          "
            + "	         LEFT JOIN(     SELECT                                                                          "
            + "	            	t2.target_sku_id sku_id                                                                    "
            + "	            FROM                                                                                        "
            + "	            	b_material_convert t1                                                                   "
            + "	            	INNER JOIN b_material_convert_detail t2 ON t2.material_convert_id = t1.id               "
            + "	            WHERE                                                                                       "
            + "	            	t1.is_effective = TRUE                                                                  "
            + "	            	AND t2.is_effective = TRUE                                                              "
            + "	            GROUP BY                                                                                    "
            + "	            	t2.target_sku_id)t7 on t7.sku_id = t1.sku_id                                              "
    +"")
    public List<BDailyAveragePriceEntity> selectAveragePriceList(BDailyAveragePriceVo condition);

}
