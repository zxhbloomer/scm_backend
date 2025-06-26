package com.xinyirun.scm.core.api.mapper.business.releaseorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @author Wang Qianfeng
 * @date 2022/11/30 16:32
 */
@Repository
public interface ApiBReleaseOrderDetailMapper extends BaseMapper<BReleaseOrderDetailEntity> {

    @Update("		update                                                                                              "
            + "			b_release_order_detail t                                                                        "
            + "		inner join b_wk_release_order_detail t1 on t.release_order_code = t1.release_order_code and t.no = t1.no"
            + "			set                                                                                             "
            + "				t.release_order_code = t1.release_order_code,                                               "
            + "				t.no = t1.no,                                                                               "
            + "				t.commodity_code = t1.commodity_code,                                                       "
            + "				t.commodity_name = t1.commodity_name,                                                       "
            + "				t.commodity_spec = t1.commodity_spec,                                                       "
            + "				t.commodity_spec_code = t1.commodity_spec_code,                                             "
            + "				t.commodity_nickname = t1.commodity_nickname,                                               "
            + "				t.qty = t1.qty,                                                                             "
            + "				t.price = t1.price,                                                                         "
            + "				t.amount = t1.amount,                                                                       "
            + "				t.collection_date = t1.collection_date,                                                     "
            + "				t.warehouse_code = t1.warehouse_code,                                                       "
            + "				t.warehouse_name = t1.warehouse_name,                                                       "
            + "				t.remark = t1.remark,                                                                       "
            + "				t.c_name = t1.c_name,                                                                       "
            + "				t.c_time = t1.c_time,                                                                       "
            + "				t.u_time = t1.u_time                                                                        "
            + "          where t.release_order_code = t1.release_order_code and t.no = t1.no                            "
    )
    void updateB_release_order_detail30();

    @Insert("INSERT INTO                                                                                               "
            + " b_release_order_detail (                                                                               "
            + "   release_order_code,                                                                                  "
            + "   no,                                                                                                  "
            + "   release_order_id,                                                                                    "
            + "   commodity_code,                                                                                      "
            + "   commodity_name,                                                                                      "
            + "   commodity_spec,                                                                                      "
            + "   commodity_spec_code,                                                                                 "
            + "   commodity_nickname,                                                                                  "
            + "   type_gauge,                                                                                          "
            + "   qty,                                                                                                 "
            + "   price,                                                                                               "
            + "   real_price,                                                                                          "
            + "   amount,                                                                                              "
            + "   collection_date,                                                                                     "
            + "   unit_name,                                                                                           "
            + "   warehouse_code,                                                                                      "
            + "   warehouse_name,                                                                                      "
            + "   remark,                                                                                              "
            + "   c_name,                                                                                              "
            + "   u_name,                                                                                              "
            + "   c_time,                                                                                              "
            + "   u_time)                                                                                              "
            + " SELECT                                                                                                 "
            + "   t.release_order_code,                                                                                "
            + "   t.no,                                                                                                "
            + "   t1.id,                                                                                               "
            + "   t.commodity_code,                                                                                    "
            + "   t.commodity_name,                                                                                    "
            + "   t.commodity_spec,                                                                                    "
            + "   t.commodity_spec_code,                                                                               "
            + "   t.commodity_nickname,                                                                                "
            + "   t.type_gauge,                                                                                        "
            + "   t.qty,                                                                                               "
            + "   t.price,                                                                                             "
            + "   t.real_price,                                                                                        "
            + "   t.amount,                                                                                            "
            + "   t.collection_date,                                                                                   "
            + "   t.unit_name,                                                                                         "
            + "   t.warehouse_code,                                                                                    "
            + "   t.warehouse_name,                                                                                    "
            + "   t.remark,                                                                                            "
            + "   t.c_name,                                                                                            "
            + "   t.u_name,                                                                                            "
            + "   now(),                                                                                               "
            + "   now()                                                                                                "
            + " FROM                                                                                                   "
            + " b_wk_release_order_detail t                                                                            "
            + " inner join b_release_order t1 ON t.release_order_code = t1.code                                        "
            + " left join b_release_order_detail t2 ON t.release_order_code = t2.release_order_code AND t.no = t2.no                 "
            + " where t2.id is null                                                                                    "
    )
    void insertB_release_order_detail30();

}
