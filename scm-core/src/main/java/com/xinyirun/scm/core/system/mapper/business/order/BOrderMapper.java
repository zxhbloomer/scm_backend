package com.xinyirun.scm.core.system.mapper.business.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.order.BOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderGoodsVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库订单 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-03-02
 */
@Repository
public interface BOrderMapper extends BaseMapper<BOrderEntity> {

  String common_select1 =
      "  "
          + "        SELECT                                                                                                                               "
          + "		           t.order_no,                                                                                                                  "
          + "		           t.bill_type,                                                                                                                 "
          + "		           t.ship_name,                                                                                                                 "
          + "		           t.contract_no,                                                                                                               "
          + "		           t.contract_dt,                                                                                                               "
          + "		           t.contract_num,                                                                                                              "
          + "		           t.supplier_id customer_id,                                                                                                   "
          + "		           t.supplier_code customer_code,                                                                                               "
          + "		           t.owner_id,                                                                                                                  "
          + "		           t.owner_code,                                                                                                                "
          + "		           t.business_type_id,                                                                                                          "
          + "		           t.business_type_code,                                                                                                        "
          + "		           t.c_time,                                                                                                                    "
          + "		           t.u_time,                                                                                                                    "
          + "		           t.c_id,                                                                                                                      "
          + "		           t.u_id,                                                                                                                      "
          + "                  t7.*,                                                                                                                        "
          + "                  DATE_FORMAT(t.contract_dt , '%Y年%m月%d日') contract_dtf,                                                                     "
          + "                  t3.name as customer_name,                                                                                                    "
          + "                  t4.name as owner_name,                                                                                                       "
          + "			       t3.short_name customer_short_name,                                                                                           "
          + "			       t4.short_name owner_short_name,                                                                                              "
          + "                  t8.name as goods_name,                                                                                                       "
          + "                  t8.goods_code as goods_code,                                                                                                       "
          + "                  t8.pm,                                                                                                                       "
          + "                  t8.spec,                                                                                                                     "
          + "                  t5.label as bill_type_name ,                                                                                                 "
          + "                  t6.name as business_type_name ,                                                                                              "
          + "                  t1.name as c_name,                                                                                                           "
          + "                  t2.name as u_name,                                                                                                           "
          + "                  t.over_inventory_upper,                                                                                                      "
          + "                  t9.label as delivery_type_name                                                                                               "
          + "           FROM b_in_order t                                                                                                                   "
          + "           LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                              "
          + "           LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                              "
          + "           LEFT JOIN m_customer t3 ON t.supplier_id = t3.id                                                                                    "
          + "           LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                          "
          + "           LEFT JOIN v_dict_info AS t5 ON t5.code = '"+DictConstant.DICT_B_IN_PLAN_BUSINESS_TYPE +"' and t5.dict_value = t.bill_type           "
          + "           LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                          "
          + "   inner join (                                                                                                                                "
          + "   			            select row_number() over(partition by t.order_id                                                                    "
          + "   			                               order by t.sku_code asc) as idx,                                                                 "
          + "                         t.order_id,                                                                                                           "
          + "                         t.sku_id,                                                                                                             "
          + "                         t.sku_code,                                                                                                           "
          + "                         t.unit_id,                                                                                                            "
          + "                         t.unit_code,                                                                                                          "
          + "                         t.unit_name,                                                                                                          "
          + "                         t.price,                                                                                                              "
          + "                         t.num,                                                                                                                "
          + "                         t.amount,                                                                                                             "
          + "                         t.rate,                                                                                                               "
          + "                         t.delivery_date,                                                                                                      "
          + "                         t.delivery_type,                                                                                                      "
          + "                         t.no order_detail_no                                                                                                  "
          + "   			from b_in_order_goods t                                                                                                         "
          + "     ) t7 on t7.order_id = t.id                                                                                                                "
          + "      LEFT JOIN m_goods_spec t8 on t8.id = t7.sku_id                                                                                           "
          + "      LEFT JOIN v_dict_info t9 on t9.dict_value = t7.delivery_type and t9.code = '" + DictConstant.DICT_B_ORDER_DELIVERY_TYPE +"'              "
          + "                                                                                                                                               ";

    String common_select2 = "  "
            + "      SELECT                                                                                                                                 "
            + "		             t.order_no,                                                                                                                "
            + "		             t.bill_type,                                                                                                               "
            + "		             t.ship_name,                                                                                                               "
            + "		             t.contract_no,                                                                                                             "
            + "		             t.contract_dt,                                                                                                             "
            + "		             t.contract_num,                                                                                                            "
            + "		             t.client_id customer_id,                                                                                                   "
            + "		             t.client_code customer_code,                                                                                               "
            + "		             t.owner_id,                                                                                                                "
            + "		             t.owner_code,                                                                                                              "
            + "		             t.business_type_id,                                                                                                        "
            + "		             t.business_type_code,                                                                                                      "
            + "		             t.c_time,                                                                                                                  "
            + "		             t.u_time,                                                                                                                  "
            + "		             t.c_id,                                                                                                                    "
            + "		             t.u_id,                                                                                                                    "
            + "                  t7.*,                                                                                                                      "
            + "                  DATE_FORMAT(t.contract_dt , '%Y年%m月%d日') contract_dtf,                                                                   "
            + "                  t3.name as customer_name,                                                                                                  "
            + "                  t4.name as owner_name,                                                                                                     "
            + "			         t3.short_name customer_short_name,                                                                                         "
            + "			         t4.short_name owner_short_name,                                                                                            "
            + "                  t8.name as goods_name,                                                                                                     "
            + "                  t8.goods_code as goods_code,                                                                                                     "
            + "                  t8.pm,                                                                                                                     "
            + "                  t8.spec,                                                                                                                   "
            + "                  t5.label as bill_type_name ,                                                                                               "
            + "                  t6.name as business_type_name ,                                                                                            "
            + "                  t1.name as c_name,                                                                                                         "
            + "                  t2.name as u_name,                                                                                                         "
            + "                  t.over_inventory_upper,                                                                                                    "
            + "                  t9.label as delivery_type_name                                                                                             "
            + "           FROM                                                                                                                              "
            + "   	          b_out_order t                                                                                                                 "
            + "           LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                            "
            + "           LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                            "
            + "           LEFT JOIN m_customer t3 ON t.client_id = t3.id                                                                                    "
            + "           LEFT JOIN m_owner t4 ON t.owner_id = t4.id                                                                                        "
            + "           LEFT JOIN v_dict_info AS t5 ON t5.code = '"+DictConstant.DICT_B_OUT_PLAN_BUSINESS_TYPE +"' and t5.dict_value = t.bill_type        "
            + "           LEFT JOIN m_business_type t6 ON t.business_type_id = t6.id                                                                        "
            + "           inner join (                                                                                                                      "
            + "           			  select row_number() over(partition by t.order_id                                                                      "
            + "           			                               order by t.sku_code asc) as idx,                                                         "
            + "                         t.order_id,                                                                                                         "
            + "                         t.sku_id,                                                                                                           "
            + "                         t.sku_code,                                                                                                         "
            + "                         t.unit_id,                                                                                                          "
            + "                         t.unit_code,                                                                                                        "
            + "                         t.unit_name,                                                                                                        "
            + "                         t.price,                                                                                                            "
            + "                         t.num,                                                                                                              "
            + "                         t.amount,                                                                                                           "
            + "                         t.rate,                                                                                                             "
            + "                         t.delivery_date,                                                                                                    "
            + "                         t.delivery_type,                                                                                                    "
            + "                         t.no order_detail_no                                                                                                "
            + "           			from b_out_order_goods t                                                                                                "
            + "             ) t7 on t7.order_id = t.id                                                                                                      "
            + "              LEFT JOIN m_goods_spec t8 on t8.id = t7.sku_id                                                                                 "
            + "      LEFT JOIN v_dict_info t9 on t9.dict_value = t7.delivery_type and t9.code = '" + DictConstant.DICT_B_ORDER_DELIVERY_TYPE +"'            "
            + "                                                                        "
            ;

    String common_select = ""
            + "		SELECT                                                                                                                                  "
            + "		    tab1.*,                                                                                                                             "
            + "		    tab2.label bill_type_name,                                                                                                          "
            + "         case when tab1.serial_type = 'b_in_order' then '采购订单' else '销售订单' end as serial_type_name                                     "
            + "		from(                                                                                                                                   "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			t1.serial_type,                                                                                                                     "
            + "			t1.serial_id,                                                                                                                       "
            + "		    t2.*                                                                                                                                "
            + "		FROM                                                                                                                                    "
            + "			b_order t1                                                                                                                          "
            + "			JOIN ("+common_select1+") t2 ON t1.serial_id = t2.order_id                                                                          "
            + "			AND t1.serial_type = 'b_in_order'                                                                                                   "
            + "			union all                                                                                                                           "
            + "		SELECT                                                                                                                                  "
            + "			t1.id,                                                                                                                              "
            + "			t1.serial_type,                                                                                                                     "
            + "			t1.serial_id,                                                                                                                       "
            + "			t2.*                                                                                                                                "
            + "		FROM                                                                                                                                    "
            + "			b_order t1                                                                                                                          "
            + "			JOIN ("+common_select2+") t2 ON t1.serial_id = t2.order_id                                                                          "
            + "			AND t1.serial_type = 'b_out_order'                                                                                                  "
            + "			) tab1                                                                                                                              "
            + "			LEFT JOIN (select * from v_dict_info where code = '"+ DictConstant.DICT_B_IN_BUSINESS_TYPE+"') tab2                                 "
            + "             on tab1.bill_type = tab2.dict_value ";

    String common_select3 = ""
            + "		SELECT                                                                                                                                      "
            + "		    tab1.*,                                                                                                                                 "
            + "		    tab2.label bill_type_name,                                                                                                              "
            + "         case when tab1.serial_type = 'b_in_order' then '采购订单' else '销售订单' end as serial_type_name                                          "
            + "		from(                                                                                                                                       "
            + "		SELECT                                                                                                                                      "
            + "			t1.id,                                                                                                                                  "
            + "			t2.status,                                                                                                                              "
            + "			t2.over_inventory_policy,                                                                                                               "
            + "			ifnull(t2.over_inventory_upper,0) over_inventory_upper,                                                                                 "
            + "			ifnull(t2.over_inventory_lower,0) over_inventory_lower,                                                                                 "
            + "			t1.serial_type,                                                                                                                         "
            + "			t1.serial_id,                                                                                                                           "
            + "			t2.contract_no,                                                                                                                         "
            + "			t2.order_no,                                                                                                                            "
            + "			t2.bill_type,                                                                                                                           "
            + "			t2.contract_dt,                                                                                                                         "
            + "			t2.ship_name,                                                                                                                           "
            + "			t2.supplier_id customer_id,                                                                                                             "
            + "			t3.name customer_name,                                                                                                                  "
            + "			t2.supplier_code customer_code,                                                                                                         "
            + "			t2.owner_id,                                                                                                                            "
            + "			t2.owner_code,                                                                                                                          "
            + "			t4.name owner_name,                                                                                                                     "
            + "			t2.contract_num,                                                                                                                        "
            + "			t2.u_time                                                                                                                               "
            + "		FROM                                                                                                                                        "
            + "			b_order t1                                                                                                                              "
            + "			JOIN b_in_order t2 ON t1.serial_id = t2.id                                                                                              "
            + "			AND t1.serial_type = 'b_in_order'                                                                                                       "
            + "			LEFT JOIN m_customer t3 on t2.supplier_id = t3.id                                                                                       "
            + "			LEFT JOIN m_owner t4 on t2.owner_id = t4.id                                                                                             "
            + "			union all                                                                                                                               "
            + "		SELECT                                                                                                                                      "
            + "			t1.id,                                                                                                                                  "
            + "			t2.status,                                                                                                                              "
            + "			t2.over_inventory_policy,                                                                                                               "
            + "			t2.over_inventory_upper,                                                                                                                "
            + "			t2.over_inventory_lower,                                                                                                                "
            + "			t1.serial_type,                                                                                                                         "
            + "			t1.serial_id,                                                                                                                           "
            + "			t2.contract_no,                                                                                                                         "
            + "			t2.order_no,                                                                                                                            "
            + "			t2.bill_type,                                                                                                                           "
            + "			t2.contract_dt,                                                                                                                         "
            + "			t2.ship_name,                                                                                                                           "
            + "			t2.client_id customer_id,                                                                                                               "
            + "			t3.NAME customer_name,                                                                                                                  "
            + "			t2.client_code customer_code,                                                                                                           "
            + "			t2.owner_id,                                                                                                                            "
            + "			t2.owner_code,                                                                                                                          "
            + "			t4.NAME owner_name,                                                                                                                     "
            + "			t2.contract_num,                                                                                                                        "
            + "			t2.u_time                                                                                                                               "
            + "		FROM                                                                                                                                        "
            + "			b_order t1                                                                                                                              "
            + "			JOIN b_out_order t2 ON t1.serial_id = t2.id                                                                                             "
            + "			AND t1.serial_type = 'b_out_order'                                                                                                      "
            + "			LEFT JOIN m_customer t3 on t2.client_id = t3.id                                                                                         "
            + "			LEFT JOIN m_owner t4 on t2.owner_id = t4.id                                                                                             "
            + "			) tab1                                                                                                                                  "
            + "			LEFT JOIN (select * from v_dict_info where code = '"+ DictConstant.DICT_B_IN_BUSINESS_TYPE+"') tab2 on tab1.bill_type = tab2.dict_value ";


    /**
     * 按订单编号，合同编号获取数据, 请同步修改 selectPage2方法
     */
    @Select("    "
            + common_select
            + "			where true                                                                                                                              "
            + "          and (tab1.contract_no like concat('%',#{p1.contract_no,jdbcType=VARCHAR},'%') or #{p1.contract_no,jdbcType=VARCHAR} is null)           "
            + "          and (tab1.order_no like concat('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)                    "
            + "          and (tab1.serial_type = #{p1.serial_type,jdbcType=VARCHAR} or #{p1.serial_type,jdbcType=VARCHAR} is null                               "
            + "             or #{p1.serial_type,jdbcType=VARCHAR} ='')                                                                                          "
            + "          and (tab1.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null                                     "
            + "             or #{p1.bill_type,jdbcType=VARCHAR} = '')                                                                                           "
            + "          and (tab1.owner_name = #{p1.owner_name,jdbcType=VARCHAR} or #{p1.owner_name,jdbcType=VARCHAR} is null                                  "
            + "           or #{p1.owner_name,jdbcType=VARCHAR} = '' or tab1.owner_short_name = #{p1.owner_name,jdbcType=VARCHAR})                               "
            + "          and (tab1.customer_name = #{p1.customer_name,jdbcType=VARCHAR} or #{p1.customer_name,jdbcType=VARCHAR} is null                         "
            + "            or #{p1.customer_name,jdbcType=VARCHAR} ='' or tab1.customer_short_name = #{p1.customer_name,jdbcType=VARCHAR})                      "
            + "          and (tab1.contract_dt >= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)                                     "
            + "          and (tab1.contract_dt <= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)                                       "
            + "          and (concat(tab1.goods_name,tab1.spec,tab1.sku_code) like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                           "
            + "			  or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = '')                                               "
            + "      ")
    IPage<BOrderVo> selectPage(Page page, @Param("p1") BOrderVo vo);

    /**
     * 按订单编号，合同编号获取数据
     */
    @Select("    "
            + common_select3
            + "			where true                                                                                      "
            + "    and tab1.id =  #{p1.id,jdbcType=INTEGER}                                                             "
            + "      ")
    BOrderVo selectDetail(@Param("p1") BOrderVo vo);

  /**
   * 按订单编号，合同编号获取数据
   */
  @Select("    "
          + common_select3
          + "			where true                                                                                      "
          + "    and tab1.order_no =  #{p1.order_no,jdbcType=VARCHAR}                                                             "
          + "      ")
  BOrderVo selectDetailByOrderNo(@Param("p1") BOrderVo vo);

  /**
   * 按订单编号，合同编号获取数据
   */
  @Select("    "
          + common_select3
          + "			where true                                                                                      "
          + "    and tab1.serial_id =  #{p1.serial_id,jdbcType=INTEGER}                                                 "
          + "    and tab1.serial_type =  #{p1.serial_type,jdbcType=VARCHAR}                                             "
          + "      ")
  BOrderVo selectOrder(@Param("p1") BOrderVo vo);

    /**
     * 查询列表
     */
    @Select("    "
            + "  select t1.*,t2.name sku_name,t2.pm,t2.spec from b_in_order_goods t1                                    "
            + "  LEFT JOIN m_goods_spec t2 on t1.sku_id = t2.id                                                         "
            + "         where true                                                                                      "
            + "         and t1.order_id =  #{p1.order_id,jdbcType=VARCHAR}                                              "
            + "      ")
    List<BOrderGoodsVo> selectInGoodsList(@Param("p1") BOrderGoodsVo vo);

    /**
     * 查询列表
     */
    @Select("    "
            + "  select t1.*,t2.name sku_name,t2.pm,t2.spec from b_out_order_goods t1                                   "
            + "  LEFT JOIN m_goods_spec t2 on t1.sku_id = t2.id                                                         "
            + "         where true                                                                                      "
            + "         and t1.order_id =  #{p1.order_id,jdbcType=VARCHAR}                                              "
            + "      ")
    List<BOrderGoodsVo> selectOutGoodsList(@Param("p1") BOrderGoodsVo vo);

  /**
   * 按订单编号，合同编号获取数据
   */
  @Select("    "
          + common_select
          + "			where true                                                                                                                              "
          // 下一行不要改, 其他可改, 此处 #{p1.contract_no,jdbcType=VARCHAR} 是个字符串, 用 , 隔开的, 不是模糊查询
          + "          and (FIND_IN_SET(tab1.contract_no, #{p1.contract_no,jdbcType=VARCHAR}) or #{p1.contract_no,jdbcType=VARCHAR} is null)                  "
          + "          and (tab1.order_no like concat('%',#{p1.order_no,jdbcType=VARCHAR},'%') or #{p1.order_no,jdbcType=VARCHAR} is null)                    "
          + "          and (tab1.serial_type = #{p1.serial_type,jdbcType=VARCHAR} or #{p1.serial_type,jdbcType=VARCHAR} is null                               "
          + "             or #{p1.serial_type,jdbcType=VARCHAR} ='')                                                                                          "
          + "          and (tab1.bill_type = #{p1.bill_type,jdbcType=VARCHAR} or #{p1.bill_type,jdbcType=VARCHAR} is null                                     "
          + "             or #{p1.bill_type,jdbcType=VARCHAR} = '')                                                                                           "
          + "          and (tab1.owner_name = #{p1.owner_name,jdbcType=VARCHAR} or #{p1.owner_name,jdbcType=VARCHAR} is null                                  "
          + "           or #{p1.owner_name,jdbcType=VARCHAR} = '' or tab1.owner_short_name = #{p1.owner_name,jdbcType=VARCHAR})                               "
          + "          and (tab1.customer_name = #{p1.customer_name,jdbcType=VARCHAR} or #{p1.customer_name,jdbcType=VARCHAR} is null                         "
          + "            or #{p1.customer_name,jdbcType=VARCHAR} ='' or tab1.customer_short_name = #{p1.customer_name,jdbcType=VARCHAR})                      "
          + "          and (tab1.contract_dt >= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)                                     "
          + "          and (tab1.contract_dt <= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)                                       "
          + "          and (concat(tab1.goods_name,tab1.spec,tab1.sku_code) like CONCAT ('%',#{p1.goods_name,jdbcType=VARCHAR},'%')                           "
          + "			  or #{p1.goods_name,jdbcType=VARCHAR} is null or #{p1.goods_name,jdbcType=VARCHAR} = '')                                               "
          + "      ")
  IPage<BOrderVo> selectPage2(Page page, @Param("p1") BOrderVo vo);


  /**
   * 按订单编号，合同编号获取数据
   */
  @Select("    "
          + common_select3
          + "			where true                                                                                      "
          + "    and tab1.contract_no =  #{p1.contract_no,jdbcType=VARCHAR}                                             "
          + "    and tab1.id =  #{p1.id,jdbcType=INTEGER}                                                               "
          + "      ")
  BOrderVo selectDetailByContractNo(@Param("p1") BOrderVo vo);

}
