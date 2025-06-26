package com.xinyirun.scm.core.system.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBWkCoDetailVo;
import com.xinyirun.scm.bean.api.vo.business.monitor.ApiBWkCoVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BWkCoEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 承运订单work表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-04
 */
@Repository
public interface BWkCoMapper extends BaseMapper<BWkCoEntity> {

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_wk_co ;                                               "
    )
    int deleteB_wk_coWor00k();

    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_wk_co_detail ;                                               "
    )
    int deleteB_wk_co_detail01();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_wk_co for update;                                               "
    )
    List<Integer> lockB_wk_co_detail10();

    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_wk_co_detail for update;                                               "
    )
    List<Integer> lockB_wk_co_detail11();


    /**
     * check
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t1.*,                                                                                           "
            + "			CASE WHEN t1.order_no IS NULL THEN '1'                                                          "
            + "			WHEN t1.carriage_contract_code IS NULL THEN '2'                                                 "
            + "			WHEN ( t2.credit_no IS NULL AND t1.company_credit_no IS NOT NULL ) THEN '3'                     "
            + "			WHEN ( t3.credit_no IS NULL AND t1.org_credit_no IS NOT NULL ) THEN '4'                         "
            + "			END AS flag                                                                                     "
            + "		FROM                                                                                                "
            + "			b_wk_co t1                                                                                      "
            + "			LEFT JOIN m_customer t2 ON t1.company_credit_no = t2.credit_no                                  "
            + "			LEFT JOIN m_owner t3 on t1.org_credit_no = t3.credit_no                                         "
            + "		WHERE                                                                                               "
            + "			t1.order_no IS NULL                                                                             "
            + "			OR t1.carriage_contract_code IS NULL                                                            "
            + "			OR ( t2.credit_no IS NULL AND t1.company_credit_no IS NOT NULL )                                "
            + "			OR ( t3.credit_no IS NULL AND t1.org_credit_no IS NOT NULL )                                    "
            + "			limit 1                                                                                         "
    )
    ApiBWkCoVo checkB_wk_co20();

    /**
     * check
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t1.*,                                                                                           "
            + "			CASE WHEN t1.sku_code IS NULL THEN 0                                                            "
            + "			WHEN t2.code IS NULL THEN 1                                                                     "
            + "			END AS flag                                                                                     "
            + "		FROM                                                                                                "
            + "			b_wk_co_detail t1                                                                               "
            + "			LEFT JOIN m_goods_spec t2 ON t1.sku_code = t2.code                                              "
            + "		WHERE                                                                                               "
            + "			t1.sku_code IS NULL                                                                             "
            + "			OR t2.code IS NULL                                                                              "
            + "			limit 1                                                                                         "
    )
    ApiBWkCoDetailVo checkB_wk_co_detail21();

    /**
     * 插入数据
     */
    @Update(""
            + "		INSERT INTO b_carriage_order (                                                                      "
            + "			order_no,                                                                                       "
            + "			carriage_contract_code,                                                                         "
            + "			STATUS,                                                                                         "
            + "			type_id,                                                                                        "
            + "			type_name,                                                                                      "
            + "			company_name,                                                                                   "
            + "			company_credit_no,                                                                              "
            + "			org_name,                                                                                       "
            + "			org_credit_no,                                                                                  "
            + "			remark,                                                                                         "
            + "			num,                                                                                            "
            + "			price,                                                                                          "
            + "			rate,                                                                                           "
            + "			transport_amount,                                                                               "
            + "			transport_amount_not,                                                                           "
            + "			transport_amount_tax,                                                                           "
            + "			transport_type_name,                                                                            "
            + "			origin_place,                                                                                   "
            + "			destination_place,                                                                              "
            + "		    sign_dt,                                                                                        "
            + "		    deadline_dt,                                                                                    "
            + "		    haul_distance,                                                                                  "
            + "		    sales_contract_code,                                                                            "
            + "		    pay_type,                                                                                       "
            + "			u_time,                                                                                         "
            + "			total_amount                                                                                    "
            + "		) SELECT                                                                                            "
            + "		t1.order_no,                                                                                        "
            + "		t1.carriage_contract_code,                                                                          "
            + "		t1.STATUS,                                                                                          "
            + "		t1.type_id,                                                                                         "
            + "		t1.type_name,                                                                                       "
            + "		t1.company_name,                                                                                    "
            + "		t1.company_credit_no,                                                                               "
            + "		t1.org_name,                                                                                        "
            + "		t1.org_credit_no,                                                                                   "
            + "		t1.remark,                                                                                          "
            + "		t1.num,                                                                                             "
            + "		t1.price,                                                                                           "
            + "		t1.rate,                                                                                            "
            + "		t1.transport_amount,                                                                                "
            + "		t1.transport_amount_not,                                                                            "
            + "		t1.transport_amount_tax,                                                                            "
            + "		t1.transport_type_name,                                                                             "
            + "		t1.origin_place,                                                                                    "
            + "		t1.destination_place,                                                                               "
            + "		t1.sign_dt,                                                                                         "
            + "		t1.deadline_dt,                                                                                     "
            + "		t1.haul_distance,                                                                                   "
            + "		t1.sales_contract_code,                                                                             "
            + "		t1.pay_type,                                                                                        "
            + "		now( ),                                                                                             "
            + "		t1.total_amount                                                                                     "
            + "		FROM                                                                                                "
            + "			b_wk_co t1                                                                                      "
            + "			LEFT JOIN b_carriage_order t2 ON t1.order_no = t2.order_no                                      "
            + "			LEFT JOIN m_customer t3 ON t1.company_credit_no = t3.credit_no                                  "
            + "			LEFT JOIN m_owner t4 ON t1.org_credit_no = t4.credit_no                                         "
            + "		WHERE                                                                                               "
            + "			t2.id IS NULL                                                                                   "
            + "		GROUP BY                                                                                            "
            + "			t1.order_no                                                                                     "
    )
    void insertB_wk_co30();

    /**
     * 更新数据
     */
    @Update(""
            + "	UPDATE b_carriage_order t0                                                                              "
            + "	INNER JOIN b_wk_co t1 ON t0.order_no = t1.order_no                                                      "
            + "	LEFT JOIN m_customer t3 ON t1.company_credit_no = t3.credit_no                                          "
            + "	LEFT JOIN m_owner t4 ON t1.org_credit_no = t4.credit_no                                                 "
            + "	SET t0.order_no = t1.order_no,                                                                          "
            + "	t0.carriage_contract_code = t1.carriage_contract_code,                                                  "
            + "	t0.STATUS = t1.STATUS,                                                                                  "
            + "	t0.type_id = t1.type_id,                                                                                "
            + "	t0.type_name = t1.type_name,                                                                            "
            + "	t0.company_name = t1.company_name,                                                                      "
            + "	t0.company_credit_no = t1.company_credit_no,                                                            "
            + "	t0.org_name = t1.org_name,                                                                              "
            + "	t0.org_credit_no = t1.org_credit_no,                                                                    "
            + "	t0.remark = t1.remark,                                                                                  "
            + "	t0.num = t1.num,                                                                                        "
            + "	t0.price = t1.price,                                                                                    "
            + "	t0.rate = t1.rate,                                                                                      "
            + "	t0.transport_amount = t1.transport_amount,                                                              "
            + "	t0.transport_amount_not = t1.transport_amount_not,                                                      "
            + "	t0.transport_amount_tax = t1.transport_amount_tax,                                                      "
            + "	t0.transport_type_name = t1.transport_type_name,                                                        "
            + "	t0.origin_place = t1.origin_place,                                                                      "
            + "	t0.destination_place = t1.destination_place,                                                            "
            + "	t0.sign_dt = t1.sign_dt,                                                                                "
            + "	t0.deadline_dt = t1.deadline_dt,                                                                        "
            + "	t0.haul_distance = t1.haul_distance,                                                                    "
            + "	t0.sales_contract_code = t1.sales_contract_code,                                                        "
            + "	t0.pay_type = t1.pay_type,                                                                              "
            + "	t0.u_time = now(),                                                                                      "
            + " t0.total_amount = t1.total_amount                                                                       "
            + "	WHERE                                                                                                   "
            + "		t0.order_no = t1.order_no                                                                           "
    )
    void updateB_wk_co30();

    /**
     * 更新明细数据
     */
    @Update(""
            + "	INSERT INTO b_carriage_order_goods (                                                                    "
            + "		order_id,                                                                                           "
            + "		order_no,                                                                                           "
            + "		no,                                                                                                 "
            + "		rate,                                                                                               "
            + "		price,                                                                                              "
            + "		num,                                                                                                "
            + "		amount,                                                                                             "
            + "		sku_id,                                                                                             "
            + "		unit_id,                                                                                            "
            + "		contract_no,                                                                                        "
            + "		sku_code,                                                                                           "
            + "		sku_name,                                                                                           "
            + "		unit_code,                                                                                          "
            + "		unit_name,                                                                                          "
            + "		amount_not,                                                                                         "
            + "		tax_amount,                                                                                         "
            + "		remark,                                                                                             "
            + "		c_time,                                                                                             "
            + "		u_time                                                                                              "
            + "	) SELECT                                                                                                "
            + "	t2.id,                                                                                                  "
            + "	t1.order_no,                                                                                            "
            + "	t1.no,                                                                                                  "
            + "	t1.rate,                                                                                                "
            + "	t1.price,                                                                                               "
            + "	t1.num,                                                                                                 "
            + "	t1.amount,                                                                                              "
            + "	t3.id,                                                                                                  "
            + "	t1.unit_id,                                                                                             "
            + "	t2.carriage_contract_code,                                                                              "
            + "	t1.sku_code,                                                                                            "
            + "	t3.spec,                                                                                                "
            + "	t1.unit_code,                                                                                           "
            + "	t1.unit_name,                                                                                           "
            + "	t1.amount_not,                                                                                          "
            + "	t1.tax_amount,                                                                                          "
            + "	t1.remark,                                                                                              "
            + "	now() u_time,                                                                                           "
            + "	now() c_time                                                                                            "
            + "	FROM                                                                                                    "
            + "		b_wk_co_detail t1                                                                                   "
            + "		INNER JOIN b_carriage_order t2 ON t1.order_no = t2.order_no                                         "
            + "		LEFT JOIN m_goods_spec t3 ON t1.sku_code = t3.                                                      "
            + "		CODE LEFT JOIN m_unit t4 ON t1.unit_code = t4.CODE                                                  "
    )
    void insertB_wk_co_detail31();

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
            + "		b_carriage_order_goods t1                                                                           "
            + "	WHERE                                                                                                   "
            + "		EXISTS ( SELECT 1 FROM b_carriage_order t2 INNER JOIN b_wk_co_detail t3                             "
            + "			ON t2.order_no = t3.order_no WHERE t1.order_id = t2.id )                                        "
    )
    void deleteB_wk_co_detail32();

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
            + "			source_type)                                                                                    "
            + "		SELECT                                                                                              "
            + "			t1.id serial_id,                                                                                "
            + "			'b_carriage_order' serial_type,                                                                 "
            + "			t1.order_no,                                                                                    "
            + "			t1.carriage_contract_code,                                                                      "
            + "			now( ) c_time,                                                                                  "
            + "			now( ) u_time,                                                                                  "
            + "			0 dbversion,                                                                                    "
            + "			'"+ DictConstant.DICT_B_ORDER_SOURCE_TYPE_ERP +"'                                               "
            + "		FROM                                                                                                "
            + "			b_carriage_order t1                                                                             "
            + "			LEFT JOIN b_order t2 ON t1.id = t2.serial_id                                                    "
            + "			AND t2.serial_type = 'b_carriage_order'                                                         "
            + "	WHERE	t2.id is null                                                                                   "
    )
    void insertB_wk_co33();

    /**
     * 插入数据b_order
     */
    @Update(""
            + "	UPDATE b_order t1                                                                                       "
            + "	INNER JOIN b_carriage_order t2 ON t1.serial_id = t2.id                                                  "
            + "	AND t1.serial_type = 'b_carriage_order'                                                                 "
            + "	SET t1.serial_id = t2.id,                                                                               "
            + "	t1.serial_type = 'b_carriage_order',                                                                    "
            + "	t1.order_no = t2.order_no,                                                                              "
            + "	t1.contract_no = t2.carriage_contract_code,                                                             "
            + "	t1.u_time = now( ),                                                                                     "
            + " t1.source_type = '" + DictConstant.DICT_B_ORDER_SOURCE_TYPE_ERP + "'                                    "
            + "	WHERE                                                                                                   "
            + "		t2.id = t1.serial_id                                                                                "
            + "		AND t1.serial_type = 'b_carriage_order'                                                             "
    )
    void updateB_wk_co33();

}
