package com.xinyirun.scm.core.system.mapper.business.releaseorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.releaseorder.BReleaseOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
@Repository
public interface BReleaseOrderMapper extends BaseMapper<BReleaseOrderEntity> {

    String commSelect = ""
            +  "    t.`code`,                                                                                          "
            +  "    t.id,                                                                                              "
            +  " 	t.type_name,                                                                                       "
            +  " 	t.business_plate_name,                                                                             "
            +  " 	t.business_type_name,                                                                              "
            +  " 	t.contract_code,                                                                                   "
            +  " 	t.order_code,                                                                                      "
            +  " 	t.purchase_order_return_code,                                                                      "
            +  " 	t.customer_name,                                                                                   "
            +  " 	t.customer_code,                                                                                   "
            +  " 	t.consignor_name,                                                                                  "
            +  " 	t.consignor_code,                                                                                  "
            +  " 	t.owner_name,                                                                                      "
            +  " 	t.owner_code,                                                                                      "
            +  " 	t.direct_info,                                                                                     "
            +  " 	t.out_time,                                                                                        "
            +  " 	t.plan_time,                                                                                       "
            +  " 	t.float_controled,                                                                                 "
            +  " 	t.float_up,                                                                                        "
            +  " 	t.float_down,                                                                                      "
            +  " 	t.total_amount,                                                                                    "
            +  " 	t.balance,                                                                                         "
            +  " 	t.use_sealed,                                                                                      "
            +  " 	t.status_name,                                                                                     "
            +  " 	t.remark,                                                                                          "
            +  " 	t.c_name,                                                                                          "
            +  " 	t.u_name,                                                                                          "
            +  " 	t.c_time,                                                                                          "
            +  " 	t.u_time,                                                                                          "
            +  " 	t.source_type                                                                                      "
            +  " FROM                                                                                                  "
            +  "   b_release_order t                                                                                   ";

    @Select(""
            +  " SELECT                                                                                                "
            +  commSelect
            + " where (t.`code` like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')            "
            + " and (t.`type_name` like concat('%', #{p1.type_name}, '%') or #{p1.type_name} is null or #{p1.type_name} = '')"
            + " and (t.`contract_code` like concat('%', #{p1.contract_code}, '%') or #{p1.contract_code} is null or #{p1.contract_code} = '')"
            + " and (t.`order_code` like concat('%', #{p1.order_code}, '%') or #{p1.order_code} is null or #{p1.order_code} = '')"
    )
    IPage<BReleaseOrderVo> selectPages(Page<BReleaseOrderVo> page, @Param("p1") BReleaseOrderVo param);

    @Select(""
            +  " SELECT                                                                                                "
            +  commSelect
            + "where t.id = #{id}                                                                                      "
    )
    BReleaseOrderVo get(Integer id);

    @Select("<script>"
            + " ${p1.params.dataScopeAnnotation_with}                                                                   "
            +  " SELECT                                                                                                 "
            +  "    t.code,                                                                                             "
            +  "    t.id,                                                                                               "
            +  " 	t.type_name,                                                                                        "
            +  " 	t.business_plate_name,                                                                              "
            +  " 	t.business_type_name,                                                                               "
            +  " 	t.contract_code,                                                                                    "
            +  " 	t.order_code,                                                                                       "
            +  " 	t.purchase_order_return_code,                                                                       "
            +  " 	t.customer_name,                                                                                    "
            +  " 	t.customer_code,                                                                                    "
            +  " 	t.consignor_name,                                                                                   "
            +  " 	t.consignor_code,                                                                                   "
            +  " 	t.owner_name,                                                                                       "
            +  " 	t.owner_code,                                                                                       "
            +  " 	t.direct_info,                                                                                      "
            +  " 	t.out_time,                                                                                         "
            +  " 	t.plan_time,                                                                                        "
            +  " 	t.float_controled,                                                                                  "
            +  " 	t.float_up,                                                                                         "
            +  " 	t.float_down,                                                                                       "
            +  " 	t.total_amount,                                                                                     "
            +  " 	t.balance,                                                                                          "
            +  " 	t.use_sealed,                                                                                       "
            +  " 	t.status_name,                                                                                      "
            +  " 	t.remark,                                                                                           "
            +  " 	t.c_name,                                                                                           "
            +  " 	t.u_name,                                                                                           "
            +  " 	t.c_time,                                                                                           "
            +  " 	t.u_time,                                                                                           "
            +  " 	t1.commodity_spec_code,                                                                             "
            +  " 	t1.id detail_id,                                                                                    "
            +  " 	t1.commodity_name,                                                                                  "
            +  " 	t2.pm,                                                                                              "
            +  " 	t1.commodity_spec,                                                                                  "
            +  " 	t1.type_gauge,                                                                                      "
            +  " 	t1.qty,                                                                                             "
            +  " 	t1.no,                                                                                             "
            +  " 	t1.price,                                                                                           "
            +  " 	t1.amount,                                                                                          "
            +  " 	t1.warehouse_name,                                                                                  "
            +  " 	t1.warehouse_code,                                                                                  "
            +  " 	t7.id warehouse_id,                                                                                 "
            +  " 	t1.unit_name,                                                                                       "
            +  " 	t8.id owner_id,                                                                                     "
            +  " 	t.source_type,                                                                                      "
            +  " 	(ifnull(t6.has_product_num, 0) + ifnull(t11.has_product_num, 0)) has_product_num,                   "
            +  " 	t11.has_product_num rt_has_product_num                                                              "
            +  " FROM                                                                                                   "
            +  "   b_release_order t                                                                                    "
            +  " LEFT JOIN b_release_order_detail t1 ON t.id = t1.release_order_id                                      "
            +  " LEFT JOIN m_goods_spec t2 ON t1.commodity_spec_code = t2.`code`                                        "
            +  " LEFT JOIN (SELECT sum(t5.wo_qty) has_product_num, t3.delivery_order_detail_id                          "
            +  "            FROM b_wo t3                                                                                "
            +  "            LEFT JOIN b_wo_product t5 ON t3.id = t5.wo_id and t5.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t3.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                                   "
            +  "            group by t3.delivery_order_detail_id) t6 ON t6.delivery_order_detail_id = t1.id             "
            +  " LEFT JOIN (SELECT sum(t10.wo_qty) has_product_num, t9.delivery_order_detail_id                          "
            +  "            FROM b_rt_wo t9                                                                                "
            +  "            LEFT JOIN b_rt_wo_product t10 ON t9.id = t10.wo_id and t10.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t9.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                                   "
            +  "            group by t9.delivery_order_detail_id) t11 ON t11.delivery_order_detail_id = t1.id             "
            +  " LEFT JOIN m_warehouse t7 ON t7.code = t1.warehouse_code                                                "
            +  " LEFT JOIN m_owner t8 ON t8.code = t.owner_code                                                         "
            + " where (t.`code` like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')             "
            + " and (t.`type_name` like concat('%', #{p1.type_name}, '%') or #{p1.type_name} is null or #{p1.type_name} = '')"
            + " and (t.`contract_code` like concat('%', #{p1.contract_code}, '%') or #{p1.contract_code} is null or #{p1.contract_code} = '')"
            + " and (t.`order_code` like concat('%', #{p1.order_code}, '%') or #{p1.order_code} is null or #{p1.order_code} = '')"
            + " and (t.`order_type` = #{p1.order_type,jdbcType=VARCHAR} or #{p1.order_type,jdbcType=VARCHAR} is null or #{p1.order_type,jdbcType=VARCHAR} = '')"
            + "     ${p1.params.dataScopeAnnotation}                                                                    "
            + " <if test='p1.is_wo'>                                                                                    "
            + "   having (t1.qty &gt; has_product_num)                                                                       "
            + " </if>"
            + "</script>"
    )
    IPage<BReleaseOrderVo> selectCommPages(Page<BReleaseOrderVo> page,@Param("p1") BReleaseOrderVo param);

    @Select(""
            +  " SELECT                                                                                                 "
            +  "    t.code,                                                                                             "
            +  "    t.id,                                                                                               "
            +  " 	t.type_name,                                                                                        "
            +  " 	t.business_plate_name,                                                                              "
            +  " 	t.business_type_name,                                                                               "
            +  " 	t.contract_code,                                                                                    "
            +  " 	t.order_code,                                                                                       "
            +  " 	t.purchase_order_return_code,                                                                       "
            +  " 	t.customer_name,                                                                                    "
            +  " 	t.customer_code,                                                                                    "
            +  " 	t.consignor_name,                                                                                   "
            +  " 	t.consignor_code,                                                                                   "
            +  " 	t.owner_name,                                                                                       "
            +  " 	t.owner_code,                                                                                       "
            +  " 	t8.id as owner_id,                                                                                  "
            +  " 	t.direct_info,                                                                                      "
            +  " 	t.out_time,                                                                                         "
            +  " 	t.plan_time,                                                                                        "
            +  " 	t.float_controled,                                                                                  "
            +  " 	t.float_up,                                                                                         "
            +  " 	t.float_down,                                                                                       "
            +  " 	t.total_amount,                                                                                     "
            +  " 	t.balance,                                                                                          "
            +  " 	t.use_sealed,                                                                                       "
            +  " 	t.status_name,                                                                                      "
            +  " 	t.remark,                                                                                           "
            +  " 	t.c_name,                                                                                           "
            +  " 	t.u_name,                                                                                           "
            +  " 	t.c_time,                                                                                           "
            +  " 	t.u_time,                                                                                           "
            +  " 	t1.commodity_spec_code,                                                                             "
            +  " 	t1.id detail_id,                                                                                    "
            +  " 	t1.commodity_name,                                                                                  "
            +  " 	t2.pm,                                                                                              "
            +  " 	t1.commodity_spec,                                                                                  "
            +  " 	t1.type_gauge,                                                                                      "
            +  " 	t1.qty,                                                                                             "
            +  " 	t1.price,                                                                                           "
            +  " 	t1.amount,                                                                                          "
            +  " 	t1.warehouse_name,                                                                                  "
            +  " 	t1.warehouse_code,                                                                                  "
            +  " 	t7.id warehouse_id,                                                                                 "
            +  " 	t1.unit_name,                                                                                       "
            +  " 	(ifnull(t6.has_product_num, 0) + ifnull(t11.has_product_num,0)) has_product_num                     "
//            +  " 	t11.has_product_num rt_has_product_num                                                              "
            +  " FROM                                                                                                   "
            +  "   b_release_order t                                                                                    "
            +  " LEFT JOIN b_release_order_detail t1 ON t.id = t1.release_order_id                                      "
            +  " LEFT JOIN m_goods_spec t2 ON t1.commodity_spec_code = t2.`code`                                        "
            +  " LEFT JOIN (SELECT sum(t5.wo_qty) has_product_num, t3.delivery_order_detail_id                          "
            +  "            FROM b_wo t3                                                                                "
            +  "            LEFT JOIN b_wo_product t5 ON t3.id = t5.wo_id and t5.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t3.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                                   "
            +  "            group by t3.delivery_order_detail_id) t6 ON t6.delivery_order_detail_id = t1.id             "
            +  " LEFT JOIN (SELECT sum(t10.wo_qty) has_product_num, t9.delivery_order_detail_id                          "
            +  "            FROM b_rt_wo t9                                                                                "
            +  "            LEFT JOIN b_rt_wo_product t10 ON t9.id = t10.wo_id and t10.type = '"+ DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C +"'"
            +  "            WHERE t9.status IN('"+ DictConstant.DICT_B_WO_STATUS_3 +"','"+ DictConstant.DICT_B_WO_STATUS_2 +"')                                   "
            +  "            group by t9.delivery_order_detail_id) t11 ON t11.delivery_order_detail_id = t1.id             "
            +  " LEFT JOIN m_warehouse t7 ON t7.code = t1.warehouse_code                                                "
            +  " LEFT JOIN m_owner t8 ON t8.code = t.owner_code                                                         "
            + " where (t1.id = #{p1})                                                                                   "
    )
    BReleaseOrderVo selectByDetailId(@Param("p1") Integer id);

    /**
     * 新增 / 更新 查询code是否重复, 如果是更新, id值不为空. 新增, id为空
     * @param id
     * @param code
     * @return
     */
    @Select(""
            + "select code from b_release_order where                                                                   "
            + " code = #{code}                                                                                          "
            + " and (id != #{id} or #{id} is null)                                                                       "
    )
    BReleaseOrderVo selectCodeOrId(@Param("id") Integer id,@Param("code") String code);
}
