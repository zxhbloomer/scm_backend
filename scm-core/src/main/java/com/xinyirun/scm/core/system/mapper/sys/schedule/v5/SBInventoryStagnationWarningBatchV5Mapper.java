package com.xinyirun.scm.core.system.mapper.sys.schedule.v5;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.system.bo.business.message.BMessageBo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 库存港口停滞预警
 */
@Repository
public interface SBInventoryStagnationWarningBatchV5Mapper extends BaseMapper<MInventoryEntity> {


    @Select(" SELECT                                                                                                     "
            +"  t.serial_id,                                                                                             "
            +"  t.serial_type,                                                                                           "
            +"  t.serial_code                                                                                            "
            +" FROM b_message t                                                                                          "
            +" LEFT JOIN m_inventory t1 on t.serial_id = t1.id                                                           "
            +"  and t.serial_type = '"+DictConstant.DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION+"'                         "
            +"  and type = '"+DictConstant.DICT_B_MESSAGE_TYPE_1+"'                                                      "
            +" LEFT JOIN (SELECT max(u_time) AS u_time,warehouse_id, sku_id,owner_id                                     "
            +"  FROM b_in                                                                                                "
            +"  WHERE STATUS IN ("+ DictConstant.DICT_B_IN_STATUS_TWO+","+DictConstant.DICT_B_IN_STATUS_TWO+")  "
            +"   GROUP BY warehouse_id,sku_id,owner_id ) t2                                                              "
            +"  ON t2.warehouse_id = t1.warehouse_id                                                                     "
            +"  AND t2.sku_id = t1.sku_id                                                                                "
            +"  AND t2.owner_id = t1.owner_id                                                                            "
            +" LEFT JOIN (SELECT max(u_time) AS u_time,warehouse_id,sku_id,owner_id                                      "
            +"  FROM b_out                                                                                               "
            +"  WHERE STATUS IN ("+DictConstant.DICT_B_OUT_STATUS_SUBMITTED+","+DictConstant.DICT_B_OUT_STATUS_PASSED+") "
            +"   GROUP BY warehouse_id,sku_id,owner_id ) t3                                                              "
            +"  ON t3.warehouse_id = t1.warehouse_id                                                                     "
            +"  AND t3.sku_id = t1.sku_id                                                                                "
            +"  AND t3.owner_id = t1.owner_id                                                                            "
            +" LEFT JOIN (SELECT max(t1.u_time) AS u_time,t1.warehouse_id,t1.sku_id,t2.owner_id                          "
            +"  FROM b_adjust_detail t1                                                                                  "
            +"  LEFT JOIN b_adjust t2                                                                                    "
            +"  ON t1.adjust_id = t2.id                                                                                  "
            +"  WHERE t1.STATUS="+DictConstant.DICT_B_ADJUST_STATUS_PASSED+"                                             "
            +"   GROUP BY t1.warehouse_id,t1.sku_id,t2.owner_id ) t4                                                     "
            +"  ON t4.warehouse_id = t1.warehouse_id                                                                     "
            +"  AND t4.sku_id = t1.sku_id                                                                                "
            +"  AND t4.owner_id = t1.owner_id                                                                            "
            +" LEFT JOIN m_warehouse t5                                                                                  "
            +"  ON t1.warehouse_id = t5.id                                                                               "
            +"  WHERE t5.warehouse_type = "+DictConstant.DICT_M_WAREHOUSE_TYPE_ZZ+"                                      "
            +" 	AND                                                                                                      "
            +" 	(DATE_FORMAT(t2.u_time,'%Y-%m-%d') >= #{p1}                                                               "
            +" 	or                                                                                                       "
            +" 	DATE_FORMAT(t3.u_time,'%Y-%m-%d')  >= #{p1}                                                               "
            +" 	or                                                                                                       "
            +" 	DATE_FORMAT(t4.u_time,'%Y-%m-%d')  >= #{p1})                                                              ")
    List<BMessageBo> selectIdAndStatus(@Param("p1") LocalDateTime now);


    @Select(" SELECT                                                                                              "
            +" 	t1.id serial_id,                                                                                  "
            +" 	t1.code serial_code,                                                                              "
            +" 	t5.name as warehouse_name,                                                                        "
            +" 	t7.name as goods_name,                                                                            "
            +" 	t6.spec,                                                                                          "
            +" 	t1.qty_avaible                                                                                    "
            +" FROM                                                                                               "
            +" 	m_inventory t1                                                                                    "
            +" LEFT JOIN (SELECT max(u_time) AS u_time,warehouse_id,	sku_id,owner_id                           "
            +" 	FROM b_in WHERE	STATUS                                                                            "
            +" IN ("+ DictConstant.DICT_B_IN_STATUS_TWO+","+DictConstant.DICT_B_IN_STATUS_TWO+")         "
            +" 	  GROUP BY warehouse_id,sku_id,owner_id                                                           "
            +" 	) t2 ON t2.warehouse_id = t1.warehouse_id AND t2.sku_id = t1.sku_id                               "
            +" 	AND t2.owner_id = t1.owner_id                                                                     "
            +" LEFT JOIN (SELECT max(u_time) AS u_time,warehouse_id,sku_id,owner_id                               "
            +" 	FROM b_out WHERE                                                                                  "
            +" 	STATUS IN ("+DictConstant.DICT_B_OUT_STATUS_SUBMITTED+","+DictConstant.DICT_B_OUT_STATUS_PASSED+")"
            +" 	GROUP BY warehouse_id,sku_id,owner_id                                                             "
            +" 	) t3 ON t3.warehouse_id = t1.warehouse_id AND t3.sku_id = t1.sku_id                               "
            +" 	AND t3.owner_id = t1.owner_id                                                                     "
            +" LEFT JOIN (SELECT                                                                                  "
            +" 		max(t1.u_time) AS u_time,t1.warehouse_id,t1.sku_id,t2.owner_id                                "
            +" 	FROM b_adjust_detail t1                                                                           "
            +" 	LEFT JOIN b_adjust t2 ON t1.adjust_id = t2.id                                                     "
            +" 	WHERE t1.STATUS = "+DictConstant.DICT_B_ADJUST_STATUS_PASSED+"                                    "
            +" 	GROUP BY	t1.warehouse_id,t1.sku_id,t2.owner_id                                                 "
            +" 	) t4 ON t4.warehouse_id = t1.warehouse_id AND t4.sku_id = t1.sku_id                               "
            +" 	AND t4.owner_id = t1.owner_id                                                                     "
            +" LEFT JOIN m_warehouse t5 on t1.warehouse_id = t5.id                                                "
            +" LEFT JOIN m_goods_spec t6 on t1.sku_id = t6.id                                                     "
            +" LEFT JOIN m_goods t7 on t6.goods_id = t7.id                                                        "
            +" 	where t5.warehouse_type = "+DictConstant.DICT_M_WAREHOUSE_TYPE_ZZ+" AND t1.qty_avaible>0          "
            +" 	AND                                                                                               "
            +" 	(DATE_FORMAT(t2.u_time,'%Y-%m-%d') < #{p1}  OR t2.u_time IS NULL)                                 "
            +" 	AND                                                                                               "
            +" 	(DATE_FORMAT(t3.u_time,'%Y-%m-%d') < #{p1}  OR t3.u_time IS NULL)                                 "
            +" 	AND                                                                                               "
            +" 	(DATE_FORMAT(t4.u_time,'%Y-%m-%d') < #{p1}  OR t4.u_time IS NULL)                                 "
            +" 	AND not exists (  SELECT  1 FROM b_message t9 where                                               "
            +"       t9.serial_type = '"+DictConstant.DICT_SYS_CODE_TYPE_M_INVENTORY_STAGNATION+"'                "
            +"       and t9.type = "+DictConstant.DICT_B_MESSAGE_TYPE_1+"                                         "
            +"       and t1.id = t9.serial_id and t1.code = t9.serial_code)                                       "
    )
    List<BMessageBo> selectList(@Param("p1") LocalDateTime now);
}
