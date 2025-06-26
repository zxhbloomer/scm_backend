package com.xinyirun.scm.core.system.mapper.business.releaseorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderDetailVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Repository
public interface BReleaseOrderDetailMapper extends BaseMapper<BReleaseOrderDetailEntity> {

    @Select("SELECT                                                                                                    "
            + " t.id,                                                                                                  "
            + " t.release_order_code,                                                                                  "
            + " t.no,                                                                                                  "
            + " t.release_order_id,                                                                                    "
            + " t.commodity_name,                                                                                      "
            + " t.commodity_code,                                                                                      "
            + " t.commodity_spec,                                                                                      "
            + " t.commodity_spec_code,                                                                                 "
            + " t.commodity_nickname,                                                                                  "
            + " t.type_gauge,                                                                                          "
            + " t.qty,                                                                                                 "
            + " t.price,                                                                                               "
            + " t.real_price,                                                                                          "
            + " t.amount,                                                                                              "
            + " t.collection_date,                                                                                     "
            + " t.unit_name,                                                                                           "
            + " t.warehouse_code,                                                                                      "
            + " t.warehouse_name,                                                                                      "
            + " t.remark,                                                                                              "
            + " t.c_name,                                                                                              "
            + " t.u_name,                                                                                              "
            + " t.c_time,                                                                                              "
            + " t1.pm,                                                                                                 "
            + " (ifnull(tab1.has_product_num, 0) + ifnull(tab2.has_product_num, 0)) has_product_num,                    "
            + " t.u_time                                                                                               "
            + " FROM b_release_order_detail t                                                                          "
            + " LEFT JOIN m_goods_spec t1 ON t.commodity_spec_code = t1.code                                           "
            + " LEFT JOIN (select sum(t3.wo_qty) has_product_num, t2.delivery_order_detail_id                          "
            + "            from b_wo t2                                                                                "
            + "             LEFT JOIN b_wo_product t3 ON t3.wo_id = t2.id                                              "
            + "            where t2.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                                  "
            + "            and t3.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'                             "
            + "            group by t2.delivery_order_detail_id ) tab1 on tab1.delivery_order_detail_id = t.id         "
            +  " LEFT JOIN (SELECT sum(t10.wo_qty) has_product_num, t9.delivery_order_detail_id                         "
            +  "            FROM b_rt_wo t9                                                                             "
            +  "            LEFT JOIN b_rt_wo_product t10 ON t9.id = t10.wo_id and t10.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t9.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                               "
            +  "            group by t9.delivery_order_detail_id) tab2 ON tab2.delivery_order_detail_id = t.id         "
            + " where t.release_order_id = #{id}                                                                        "
    )
    List<BReleaseOrderDetailVo> selectByReleaseId(Integer id);
}
