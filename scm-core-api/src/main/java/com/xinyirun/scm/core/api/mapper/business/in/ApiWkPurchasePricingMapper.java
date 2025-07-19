package com.xinyirun.scm.core.api.mapper.business.in;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.in.BWkPurchasePricingEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-11
 */
@Repository
public interface ApiWkPurchasePricingMapper extends BaseMapper<BWkPurchasePricingEntity> {
    /**
     * 删除数据临时表
     */
    @Delete(
            "       delete from b_wk_purchase_pricing ;                                               "
    )
    int deleteB_wk_purchase_pricing00();


    /**
     * 锁定临时表
     */
    @Select(
            "       select 1 from b_wk_purchase_pricing for update;                                               "
    )
    List<Integer> lockB_wk_purchase_pricing10();


    /**
     * 插入数据
     */
    @Update(""
            +"	INSERT INTO                                                                                             "
            +"	b_purchase_pricing (                                                                                    "
            +"		CODE,                                                                                               "
            +"		sku_id,                                                                                             "
            +"		sku_code,                                                                                           "
            +"		contract_no,                                                                                        "
            +"		new_price,                                                                                          "
            +"		start_time,                                                                                         "
            +"		end_time,                                                                                           "
            +"		is_deleted,                                                                                         "
            +"		c_time,                                                                                             "
            +"		u_time                                                                                              "
            +"	)                                                                                                       "
            +"	SELECT                                                                                                  "
            +"		t1.code,                                                                                            "
            +"		t2.id sku_id,                                                                                       "
            +"		t1.sku_code,                                                                                        "
            +"		t1.contract_no,                                                                                     "
            +"		t1.new_price,                                                                                       "
            +"		t1.start_time,                                                                                      "
            +"		t1.end_time,                                                                                        "
            +"		t1.is_deleted,                                                                                      "
            +"		now() c_time,                                                                                       "
            +"		now() u_time                                                                                        "
            +"	FROM                                                                                                    "
            +"		b_wk_purchase_pricing t1                                                                            "
            +"		left join m_goods_spec t2 on t1.sku_code = t2.code                                                  "
            +"		left join b_purchase_pricing t3 on t1.code = t3.code                                                "
            +"		where t3.id is null                                                                                 "
    )
    void insertB_wk_purchase_pricing20();

    /**
     * 插入数据
     */
    @Update(""
            + "		update                                                                                              "
            + "			b_purchase_pricing t1                                                                           "
            + "			left join m_goods_spec t2 on t1.sku_code = t2.code                                              "
            + "			inner join b_wk_purchase_pricing t3 on t1.code = t3.code                                        "
            + "			set                                                                                             "
            + "				t1.sku_id = t2.id,                                                                          "
            + "				t1.sku_code = t3.sku_code,                                                                  "
            + "				t1.contract_no = t3.contract_no,                                                            "
            + "				t1.new_price = t3.new_price,                                                                "
            + "				t1.start_time = t3.start_time,                                                              "
            + "				t1.end_time =t3.start_time,                                                                 "
            + "				t1.is_deleted = t3.is_deleted,                                                              "
            + "				t1.u_time = now()                                                                           "
            + "			where t1.code = t3.code                                                                         "
    )
    void updateB_wk_purchase_pricing30();
}
