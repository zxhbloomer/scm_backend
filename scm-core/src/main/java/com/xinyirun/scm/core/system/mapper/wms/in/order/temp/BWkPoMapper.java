package com.xinyirun.scm.core.system.mapper.wms.in.order.temp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBWkPoDetailVo;
import com.xinyirun.scm.bean.api.vo.business.in.ApiBWkPoVo;
import com.xinyirun.scm.bean.entity.busniess.in.order.temp.BWkPoEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库订单 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Repository
public interface BWkPoMapper extends BaseMapper<BWkPoEntity> {

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_wk_po ;                                               "
    )
    int deleteB_wk_poWor00k();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_wk_po_detail ;                                               "
    )
    int deleteB_wk_po_detail01();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_wk_po for update;                                               "
    )
    List<Integer> lockB_wk_po_detail10();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_wk_po_detail for update;                                               "
    )
    List<Integer> lockB_wk_po_detail11();


    /**
     * check
     */
    @Select(""
        + "		SELECT                                                                                                  "
        + "			t1.*,                                                                                               "
        + "			CASE WHEN t1.order_no IS NULL THEN '1'                                                              "
        + "			WHEN t1.bill_type IS NULL THEN '2'                                                                  "
        + "			WHEN ( t2.credit_no IS NULL AND t1.supplier_credit_no IS NOT NULL ) THEN '3'                        "
        + "			WHEN ( t3.credit_no IS NULL AND t1.owner_credit_no IS NOT NULL ) THEN '4'                           "
        + "			WHEN ( t4.code IS NULL AND t1.business_type_code IS NOT NULL ) THEN '5'                             "
        + "			END AS flag                                                                                         "
        + "		FROM                                                                                                    "
        + "			b_wk_po t1                                                                                          "
        + "			LEFT JOIN m_customer t2 ON t1.supplier_credit_no = t2.credit_no                                     "
        + "			LEFT JOIN m_owner t3 on t1.owner_credit_no = t3.credit_no                                           "
        + "			LEFT JOIN m_business_type t4 on t1.business_type_code = t4.code                                     "
        + "		WHERE                                                                                                   "
        + "			t1.order_no IS NULL                                                                                 "
        + "			OR t1.bill_type IS NULL                                                                             "
        + "			OR ( t2.credit_no IS NULL AND t1.supplier_credit_no IS NOT NULL )                                   "
        + "			OR ( t3.credit_no IS NULL AND t1.owner_credit_no IS NOT NULL )                                      "
        + "			OR ( t4.code IS NULL AND t1.business_type_code IS NOT NULL )                                        "
        + "			limit 1                                                                                             "
    )
    ApiBWkPoVo checkB_wk_po20();

    /**
     * check
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t1.*,                                                                                           "
            + "			CASE WHEN t1.order_no IS NULL THEN 1                                                            "
            + "			WHEN t2.code IS NULL THEN 2                                                                     "
            + "			WHEN ( t3.code IS NULL AND t1.unit_code IS NOT NULL ) IS NULL THEN 3                            "
            + "			END AS flag                                                                                     "
            + "		FROM                                                                                                "
            + "			b_wk_po_detail t1                                                                               "
            + "			LEFT JOIN m_goods_spec t2 ON t1.sku_code = t2.code                                              "
            + "			LEFT JOIN m_unit t3 on t1.unit_code = t3.code                                                   "
            + "		WHERE                                                                                               "
            + "			t1.sku_code IS NULL                                                                             "
            + "			OR t2.code IS NULL                                                                              "
            + "			OR ( t3.code IS NULL AND t1.unit_code IS NOT NULL )                                             "
            + "			limit 1                                                                                         "
    )
    ApiBWkPoDetailVo checkB_wk_po_detail21();

    /**
     * 插入数据
     */
    @Update(""
            + "		INSERT INTO b_in_order (                                                                            "
            + "			order_no,                                                                                       "
            + "			over_inventory_policy,                                                                          "
            + "			over_inventory_upper,                                                                           "
            + "			over_inventory_lower,                                                                           "
            + "			status,                                                                                         "
            + "			bill_type,                                                                                      "
            + "			contract_no,                                                                                    "
            + "			ship_name,                                                                                      "
            + "			contract_dt,                                                                                    "
            + "			contract_expire_dt,                                                                             "
            + "			contract_num,                                                                                   "
            + "			supplier_id,                                                                                    "
            + "			supplier_code,                                                                                  "
            + "			owner_id,                                                                                       "
            + "			owner_code,                                                                                     "
            + "			business_type_id,                                                                               "
            + "			business_type_code,                                                                             "
            + "			mode_transport_id,                                                                              "
            + "			mode_transport_name,                                                                            "
            + "			u_time                                                                                          "
            + "			)                                                                                               "
            + "		SELECT                                                                                              "
            + "			t1.order_no,                                                                                    "
            + "			t1.over_inventory_policy,                                                                       "
            + "			t1.over_inventory_upper,                                                                        "
            + "			t1.over_inventory_lower,                                                                        "
            + "			t1.status,                                                                                      "
            + "			t1.bill_type,                                                                                   "
            + "			t1.contract_no,                                                                                 "
            + "			t1.ship_name,                                                                                   "
            + "			t1.contract_dt,                                                                                 "
            + "			t1.contract_expire_dt,                                                                          "
            + "			t1.contract_num,                                                                                "
            + "			t3.id supplier_id,                                                                              "
            + "			t3.code supplier_code,                                                                          "
            + "			t4.id owner_id,                                                                                 "
            + "			t4.code owner_code,                                                                             "
            + "			t5.id business_type_id,                                                                         "
            + "			t5.code business_type_code,                                                                     "
            + "			t1.mode_transport_id,                                                                           "
            + "			t1.mode_transport_name,                                                                         "
            + "			now( ) u_time                                                                                   "
            + "		FROM                                                                                                "
            + "			b_wk_po t1                                                                                      "
            + "			LEFT JOIN b_in_order t2 ON t1.order_no = t2.order_no                                            "
            + "			LEFT JOIN m_customer t3 ON t1.supplier_credit_no = t3.credit_no                                 "
            + "			LEFT JOIN m_owner t4 ON t1.owner_credit_no = t4.credit_no                                       "
            + "			LEFT JOIN m_business_type t5 ON t1.business_type_code = t5.code                                 "
            + "			WHERE t2.id is null group by t1.order_no                                                        "
    )
    void insertB_wk_po30();

    /**
     * 更新数据
     */
    @Update(""
            + "	UPDATE                                                                                                  "
            + "		b_in_order t1                                                                                       "
            + "	INNER JOIN b_wk_po t2 ON t1.order_no = t2.order_no                                                      "
            + "	LEFT JOIN m_customer t3 ON t2.supplier_credit_no = t3.credit_no                                         "
            + "	LEFT JOIN m_owner t4 ON t2.owner_credit_no = t4.credit_no                                               "
            + "	LEFT JOIN m_business_type t5 ON t2.business_type_code = t5.CODE                                         "
            + "	SET t1.bill_type = t2.bill_type,                                                                        "
            + "			t1.mode_transport_id = t2.mode_transport_id,                                                    "
            + "			t1.mode_transport_name = t2.mode_transport_name,                                                "
            + "			t1.contract_no = t2.contract_no,                                                                "
            + "			t1.over_inventory_policy = t2.over_inventory_policy,                                            "
            + "			t1.over_inventory_upper = t2.over_inventory_upper,                                              "
            + "			t1.over_inventory_lower = t2.over_inventory_lower,                                              "
            + "			t1.status = t2.status,                                                                          "
            + "			t1.ship_name = t2.ship_name,                                                                    "
            + "			t1.contract_dt = t2.contract_dt,                                                                "
            + "			t1.contract_expire_dt = t2.contract_expire_dt,                                                  "
            + "			t1.contract_num = t2.contract_num,                                                              "
            + "			t1.supplier_id = t3.id,                                                                         "
            + "			t1.supplier_code = t3.CODE,                                                                     "
            + "			t1.owner_id = t4.id,                                                                            "
            + "			t1.owner_code = t4.CODE,                                                                        "
            + "			t1.business_type_id = t2.business_type_id,                                                      "
            + "			t1.business_type_code = t2.business_type_code,                                                  "
            + "         t1.source_type = '"+ DictConstant.DICT_B_ORDER_SOURCE_TYPE_ERP +"',                             "
            + "			t1.u_time = now( )                                                                              "
            + "	WHERE t1.order_no = t2.order_no                                                                         "
    )
    void updateB_wk_po30();

    /**
     * 更新明细数据
     */
    @Update(""
            + "			INSERT INTO b_in_order_goods (                                                                 "
            + "				order_id,                                                                                   "
            + "				sku_id,                                                                                     "
            + "				sku_code,                                                                                   "
            + "				unit_id,                                                                                    "
            + "				unit_code,                                                                                  "
            + "				unit_name,                                                                                  "
            + "				price,                                                                                      "
            + "				num,                                                                                        "
            + "				amount,                                                                                     "
            + "				rate,                                                                                       "
            + "				delivery_date,                                                                              "
            + "				delivery_type,                                                                              "
            + "				u_time,                                                                                     "
            + "				c_time                                                                                      "
            + "				)                                                                                           "
            + "			SELECT                                                                                          "
            + "				t2.id order_id,                                                                             "
            + "				t3.id sku_id,                                                                               "
            + "				t3.code sku_code,                                                                           "
            + "				t4.id unit_id,                                                                              "
            + "				t4.code unit_code,                                                                          "
            + "				t4.name unit_name,                                                                          "
            + "				t1.price,                                                                                   "
            + "				t1.num,                                                                                     "
            + "				t1.amount,                                                                                  "
            + "				t1.rate,                                                                                    "
            + "				t1.delivery_date,                                                                           "
            + "				t1.delivery_type,                                                                           "
            + "				now() u_time,                                                                               "
            + "				now() c_time                                                                                "
            + "			FROM                                                                                            "
            + "				b_wk_po_detail t1                                                                           "
            + "				INNER JOIN b_in_order t2 ON t1.order_no = t2.order_no                                       "
            + "				LEFT JOIN m_goods_spec t3 on t1.sku_code = t3.code                                          "
            + "				LEFT JOIN m_unit t4 on t1.unit_code = t4.code                                               "
    )
    void insertB_wk_po_detail31();

//    /**
//     * 更新明细数据
//     */
//    @Update(""
//            + "	UPDATE b_in_order_goods t1                                                                              "
//            + "	INNER JOIN b_in_order t5 on t1.order_id = t5.id                                                         "
//            + "	INNER JOIN b_wk_po_detail t2 ON t5.order_no = t2.order_no                                               "
//            + "	LEFT JOIN m_goods_spec t3 ON t2.sku_code = t3.CODE                                                      "
//            + "	LEFT JOIN m_unit t4 ON t2.unit_code = t4.CODE                                                           "
//            + "	SET t1.sku_id = t3.id,                                                                                  "
//            + "	t1.sku_code = t3.CODE,                                                                                  "
//            + "	t1.unit_id = t4.id,                                                                                     "
//            + "	t1.unit_code = t4.CODE,                                                                                 "
//            + "	t1.unit_name = t4.NAME,                                                                                 "
//            + "	t1.price = t2.price,                                                                                    "
//            + "	t1.num = t2.num,                                                                                        "
//            + "	t1.amount = t2.amount,                                                                                  "
//            + "	t1.rate = t2.rate,                                                                                      "
//            + "	t1.delivery_date = t2.delivery_date,                                                                    "
//            + "	t1.delivery_type = t2.delivery_type                                                                     "
//    )
//    void updateB_wk_po_detail31();

    /**
     * 删除明细数据
     */
    @Update(""
            + "	DELETE                                                                                                  "
            + "	FROM                                                                                                    "
            + "		b_in_order_goods t1                                                                                 "
            + "	WHERE                                                                                                   "
            + "		EXISTS ( SELECT 1 FROM b_in_order t2 INNER JOIN b_wk_po_detail t3                                   "
            + "			ON t2.order_no = t3.order_no WHERE t1.order_id = t2.id )                                        "
    )
    void deleteB_wk_po_detail32();

    /**
     * 插入数据b_order
     */
    @Update(""
            + "	INSERT INTO b_order (                                                                                   "
            + "			serial_id,                                                                                      "
            + "			serial_type,                                                                                    "
            + "			order_no,                                                                                       "
            + "			contract_no,                                                                                    "
            + "			c_time,                                                                                         "
            + "			u_time,                                                                                         "
            + "			dbversion,                                                                                      "
            + " 	    bill_type,                                                                                      "
            + " 	    ship_name,                                                                                      "
            + " 	    contract_dt,                                                                                    "
            + " 	    contract_num,                                                                                   "
            + "			mode_transport_id,                                                                              "
            + "			mode_transport_name,                                                                            "
            + "			source_type,                                                                                 "
            + " 	    customer_id)                                                                                    "
            + "		SELECT                                                                                              "
            + "			t1.id serial_id,                                                                                "
            + "			'b_in_order' serial_type,                                                                       "
            + "			t1.order_no,                                                                                    "
            + "			t1.contract_no,                                                                                 "
            + "			now( ) c_time,                                                                                  "
            + "			now( ) u_time,                                                                                  "
            + "			0 dbversion,                                                                                    "
            + " 	    t1.bill_type,                                                                                   "
            + " 	    t1.ship_name,                                                                                   "
            + " 	    t1.contract_dt,                                                                                 "
            + " 	    t1.contract_num,                                                                                "
            + "			t1.mode_transport_id,                                                                           "
            + "			t1.mode_transport_name,                                                                         "
            + "			'"+ DictConstant.DICT_B_ORDER_SOURCE_TYPE_ERP +"',                                              "
            + " 	    t1.supplier_id                                                                                  "
            + "		FROM                                                                                                "
            + "			b_in_order t1                                                                                   "
            + "			LEFT JOIN b_order t2 ON t1.id = t2.serial_id                                                    "
            + "			AND t2.serial_type = 'b_in_order'                                                               "
            + "	WHERE	t2.id is null                                                                                   "
    )
    void insertB_wk_po33();

    /**
     * 插入数据b_order
     */
    @Update(""
            + "	UPDATE b_order t1                                                                                       "
            + "		INNER JOIN b_in_order t2 ON t1.serial_id = t2.id                                                    "
            + "			AND t1.serial_type = 'b_in_order'                                                               "
            + "	SET t1.serial_id = t2.id,                                                                               "
            + "		t1.serial_type = 'b_in_order',                                                                      "
            + "		t1.order_no = t2.order_no,                                                                          "
            + "		t1.contract_no = t2.contract_no,                                                                    "
            + "		t1.u_time = now( ),                                                                                 "
            + "		t1.bill_type = t2.bill_type,                                                                        "
            + "		t1.ship_name = t2.ship_name,                                                                        "
            + "		t1.contract_dt=t2.contract_dt,                                                                      "
            + "		t1.contract_num=t2.contract_num,                                                                    "
            + "		t1.business_type_id = t2.business_type_id,                                                          "
            + "		t1.business_type_code = t2.business_type_code,                                                      "
            + "     t1.source_type = '" + DictConstant.DICT_B_ORDER_SOURCE_TYPE_ERP + "',                               "
            + "		t1.customer_id=t2.supplier_id                                                                       "
            + "	WHERE                                                                                                   "
            + "		t2.id = t1.serial_id                                                                                "
            + "		AND t1.serial_type = 'b_in_order'                                                                   "
    )
    void updateB_wk_po33();

}
