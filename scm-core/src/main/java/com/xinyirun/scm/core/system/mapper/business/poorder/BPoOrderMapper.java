package com.xinyirun.scm.core.system.mapper.business.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PoOrderDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 采购订单表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Repository
public interface BPoOrderMapper extends BaseMapper<BPoOrderEntity> {    /**
     * 分页查询
     */
    @Select("	<script>                                                                                                                                         "
            +" SELECT                                                                                                                                     "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.goods_name ,                                                                                                                           "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name,                                                                                                                       "
            +"		tab11.sign_date,                                                                                                                            "
            +"		tab11.expiry_date,                                                                                                                          "
            +"		tab16.advance_amount_total,                                                                                                                 "
            +"		tab16.payable_pay_total,                                                                                                                    "
            +"		tab16.advance_pay_total,                                                                                                                    "
            +"		tab16.paid_total,                                                                                                                           "
            +"		tab16.amount_total,                                                                                                                         "
            +"		tab16.tax_amount_total,                                                                                                                     "
            +"		tab16.qty_total                                                                                                                             "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select pod.po_order_id,JSON_ARRAYAGG(                                                                                           "
            +"	    JSON_OBJECT(                                                                                                                                "
            +"	        'sku_code', pod.sku_code,                                                                                                               "
            +"	        'sku_name', pod.sku_name,                                                                                                               "
            +"	        'origin', pod.origin,                                                                                                                   "
            +"	        'sku_id', pod.sku_id,                                                                                                                   "
            +"	        'unit_id', pod.unit_id,                                                                                                                 "
            +"	        'qty', pod.qty,                                                                                                                         "
            +"	        'price', pod.price,                                                                                                                     "
            +"	        'amount', pod.amount,                                                                                                                   "
            +"	        'tax_amount', pod.tax_amount,                                                                                                           "
            +"	        'tax_rate', pod.tax_rate,                                                                                                               "
            +"	        'goods_id', pod.goods_id,                                                                                                               "
            +"	        'goods_name', pod.goods_name,                                                                                                           "
            +"	        'goods_code', pod.goods_code,                                                                                                           "
            +"	        'inventory_in_total', podt.inventory_in_total,                                                                                          "
            +"	        'settle_can_qty_total', podt.settle_can_qty_total                                                                                   "
            +"	    )) as detailListData,                                                                                                                       "
            +"	    GROUP_CONCAT(pod.sku_name) as goods_name                                                                                                    "
            +"	     from b_po_order_detail pod LEFT JOIN b_po_order_detail_total podt ON pod.po_order_id = podt.po_order_id GROUP BY pod.po_order_id) tab2 ON tab1.id = tab2.po_order_id"
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_PO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_PO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_PO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_PO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_PO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                  "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		LEFT JOIN b_po_order_total tab16 ON tab16.po_order_id = tab1.id                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                      "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                               "
            +"		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )                                                               "

//            是否预付款查询：is_advance_pay
//            advance_amount_total》0
            + "   <if test='p1.is_advance_pay != null' >                                                                                                                  "
            + "       and tab16.advance_amount_total > 0                                                                                                                  "
            + "   </if>                                                                                                                                                   "


            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab11.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.settle_list != null and p1.settle_list.length!=0' >                                                                                              "
            + "    and tab1.settle_type in                                                                                                                                              "
            + "        <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >                                                                                              "
            + "    and tab1.bill_type in                                                                                                                                              "
            + "        <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            // 商品查询：goods_name
            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_order_detail subt1                                                                                                                      "
            + "            INNER JOIN b_po_order subt2 ON subt1.po_order_id = subt2.id                                                                               "
            + "          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')                                                                         "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_order_id                                                                                                                         "
            +" </script>                                                                                                                                       ")    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoOrderDetailListTypeHandler.class),
    })
    IPage<PoOrderVo> selectPage(Page<PoOrderVo> page, @Param("p1") PoOrderVo searchCondition);



    /**
     * id查询
     */
    @Select("	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.goods_name ,                                                                                                                           "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name,                                                                                                                       "
            +"		tabb1.one_file as doc_att_file,                                                                                                             "
            +"		tab11.sign_date,                                                                                                                            "
            +"		tab11.expiry_date,                                                                                                                          "
            +"		tab16.advance_amount_total,                                                                                                                 "
            +"		tab16.payable_pay_total,                                                                                                                    "
            +"		tab16.advance_pay_total,                                                                                                                    "
            +"		tab16.paid_total,                                                                                                                           "
            +"		tab16.amount_total,                                                                                                                         "
            +"		tab16.tax_amount_total,                                                                                                                     "
            +"		tab16.qty_total                                                                                                                             "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select pod.po_order_id,JSON_ARRAYAGG(                                                                                           "
            +"	    JSON_OBJECT(                                                                                                                                "
            +"	        'sku_code', pod.sku_code,                                                                                                               "
            +"	        'sku_name', pod.sku_name,                                                                                                               "
            +"	        'origin', pod.origin,                                                                                                                   "
            +"	        'sku_id', pod.sku_id,                                                                                                                   "
            +"	        'unit_id', pod.unit_id,                                                                                                                 "
            +"	        'qty', pod.qty,                                                                                                                         "
            +"	        'price', pod.price,                                                                                                                     "
            +"	        'amount', pod.amount,                                                                                                                   "
            +"	        'tax_amount', pod.tax_amount,                                                                                                           "
            +"	        'tax_rate', pod.tax_rate,                                                                                                               "
            +"	        'goods_id', pod.goods_id,                                                                                                               "
            +"	        'goods_name', pod.goods_name,                                                                                                           "
            +"	        'goods_code', pod.goods_code,                                                                                                           "
            +"	        'inventory_in_total', podt.inventory_in_total,                                                                                          "
            +"	        'settle_can_qty_total', podt.settle_can_qty_total                                                                                   "
            +"	    )) as detailListData,                                                                                                                       "
            +"	    GROUP_CONCAT(pod.sku_name) as goods_name                                                                                                    "
            +"	     from b_po_order_detail pod LEFT JOIN b_po_order_detail_total podt ON pod.po_order_id = podt.po_order_id GROUP BY pod.po_order_id) tab2 ON tab1.id = tab2.po_order_id"
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_PO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_PO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_PO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_PO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_PO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"		LEFT JOIN b_po_order_attach tabb1 on tab1.id = tabb1.po_order_id                                                                             "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                  "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		LEFT JOIN b_po_order_total tab16 ON tab16.po_order_id = tab1.id                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.id = #{p1} AND tab1.is_del = false                                                                                                "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_order_id                                                                                                                            ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoOrderDetailListTypeHandler.class),
    })
    PoOrderVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("	<script>                                                                                                                                           "
            +"	SELECT 	                                                                                                                                          "
            +"		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,                                                                        "       // 订单总金额
            +"		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,                                                                                      "       // 订单总采购数量
            +"		SUM( IFNULL(tab2.advance_unpay_total,0) )  as  advance_unpay_total,                                                                      "       // 订单：预付未付总金额
            +"		SUM( IFNULL(tab2.advance_pay_total,0) )  as  advance_pay_total,                                                                          "       // 订单：预付已付款总金额
            +"		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,                                                                      "       // 订单：结算总金额
            +"		SUM( IFNULL(tab3.inventory_in_total,0) )  as  inventory_in_total_sum,                                                                    "       // 实际入库合计
            +"		SUM( IFNULL(tab3.settle_can_qty_total,0) )  as  settle_can_qty_total_sum                                                                 "       // 待结算数量合计
            +"	FROM                                                                                                                                             "
            +"		b_po_order tab1                                                                                                                              "
            +"		LEFT JOIN b_po_order_total AS tab2 ON tab1.id = tab2.po_order_id                                                                               "
            +"		LEFT JOIN b_po_order_detail_total AS tab3 ON tab1.id = tab3.po_order_id                                                                        "
            +"		WHERE TRUE                                                                                                                                   "
            +"		 AND tab1.is_del = false                                                                                                                     "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               "
            +"		 AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                       "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                               "
            +"		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )                                                               "

            //            是否预付款查询：is_advance_pay
//            advance_amount_total》0
            + "   <if test='p1.is_advance_pay != null' >                                                                                                                  "
            + "       and tab2.advance_amount_total > 0                                                                                                                  "
            + "   </if>                                                                                                                                                   "


            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab3.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.settle_list != null and p1.settle_list.length!=0' >                                                                                              "
            + "    and tab1.settle_type in                                                                                                                                              "
            + "        <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >                                                                                              "
            + "    and tab1.bill_type in                                                                                                                                              "
            + "        <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            // 商品查询：goods_name
            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_order_detail subt1                                                                                                                      "
            + "            INNER JOIN b_po_order subt2 ON subt1.po_order_id = subt2.id                                                                               "
            + "          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')                                                                         "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "

            +"  </script>                                                                                                                                  "
    )
    PoOrderVo querySum( @Param("p1") PoOrderVo searchCondition);

    /**
     * 标准合同下推校验 只能下推一个订单
     */
    @Select( "SELECT * FROM b_po_order tab1 LEFT JOIN b_po_contract tab2 ON tab1.po_contract_id = tab2.id                                           "
            +" WHERE tab1.is_del = FALSE AND tab2.type = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE_ZERO +"' AND tab2.id = #{p1.po_contract_id}       ")
    List<PoOrderVo> validateDuplicateContractId(@Param("p1")PoOrderVo searchCondition);

    @Select("SELECT                                                                                                                                             "
            +"	count(tab1.id)                                                                                                                                  "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                          "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.po_contract_code = #{p1.po_contract_code} or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                      ")
    Long selectExportCount(@Param("p1")PoOrderVo param);

    /**
     * id查询
     */
    @Select(
            "<script>	                                                                                                                                        "
            +"SELECT @row_num:= @row_num+ 1 as no,tb1.* from (                                                                                                  "
            +"	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select pod.po_order_id,JSON_ARRAYAGG(                                                                                           "
            +"	    JSON_OBJECT(                                                                                                                                "
            +"	        'sku_code', pod.sku_code,                                                                                                               "
            +"	        'sku_name', pod.sku_name,                                                                                                               "
            +"	        'origin', pod.origin,                                                                                                                   "
            +"	        'sku_id', pod.sku_id,                                                                                                                   "
            +"	        'unit_id', pod.unit_id,                                                                                                                 "
            +"	        'qty', pod.qty,                                                                                                                         "
            +"	        'price', pod.price,                                                                                                                     "
            +"	        'amount', pod.amount,                                                                                                                   "
            +"	        'tax_amount', pod.tax_amount,                                                                                                           "
            +"	        'tax_rate', pod.tax_rate,                                                                                                               "
            +"	        'goods_id', pod.goods_id,                                                                                                               "
            +"	        'goods_name', pod.goods_name,                                                                                                           "
            +"	        'goods_code', pod.goods_code,                                                                                                           "
            +"	        'inventory_in_total', podt.inventory_in_total,                                                                                          "
            +"	        'settle_can_qty_total', podt.settle_can_qty_total                                                                                   "
            +"	    )) as detailListData                                                                                                                        "
            +"	     from b_po_order_detail pod LEFT JOIN b_po_order_detail_total podt ON pod.po_order_id = podt.po_order_id GROUP BY pod.po_order_id) tab2 ON tab1.id = tab2.po_order_id"
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_PO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_PO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_PO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_PO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_PO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                   "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.po_contract_code = #{p1.po_contract_code} or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                      "
            +"   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                             "
            +"    and tab1.id in                                                                                                                                "
            +"        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                  "
            +"         #{item}                                                                                                                                  "
            +"        </foreach>                                                                                                                                "
            +"   </if>                                                                                                                                          "
            +"	GROUP BY                                                                                                                                        "            +"		tab2.po_order_id) as tb1,(select @row_num:=0) tb2                                                                                           "
            +"		  </script>                                                                                                                                 "
    )
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoOrderDetailListTypeHandler.class),
    })
    List<PoOrderVo> selectExportList(@Param("p1")PoOrderVo param);

    /**
     * 根据采购合同id,状态 查询采购订单
     */
    @Select("select * from b_po_order where po_contract_id = #{p1} and status != #{p2} and is_del = false")
    List<PoOrderVo> selectByPoContractIdNotByStatus(@Param("p1")Integer id, @Param("p2") String dictBPoOrderStatusFive);

    /**
     * 根据采购合同id 查询采购订单
     */
    @Select("select * from b_po_order where po_contract_id = #{p1} and is_del = false")
    List<PoOrderVo> selectByPoContractId(@Param("p1")Integer id);

    /**
     * 根据采购合同id 查询采购订单
     */
    @Select("select * from b_po_order " +
            "where po_contract_id = #{p1} " +
            "  and is_del = false " +
            "  and status not in ('" + DictConstant.DICT_B_PO_ORDER_STATUS_FIVE + "','" + DictConstant.DICT_B_PO_ORDER_STATUS_SIX + "')" +
            "  ")
    List<PoOrderVo> selectLivePoByPoContractId(@Param("p1")Integer id);

    /**
     * 根据code查询采购订单
     */
    @Select("select * from b_po_order where code = #{code} and is_del = false")
    PoOrderVo selectByCode(@Param("code") String code);

    /**
     * 分页查询包含结算信息
     */
    @Select("	<script>                                                                                                                                         "
            +" SELECT                                                                                                                                     "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.goods_name ,                                                                                                                           "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name,                                                                                                                       "
            +"		tab11.sign_date,                                                                                                                            "
            +"		tab11.expiry_date,                                                                                                                          "
            +"		tab16.advance_amount_total,                                                                                                                 "
            +"		tab16.payable_pay_total,                                                                                                                    "
            +"		tab16.advance_pay_total,                                                                                                                    "
            +"		tab16.paid_total,                                                                                                                           "
            +"		tab16.amount_total,                                                                                                                         "
            +"		tab16.tax_amount_total,                                                                                                                     "
            +"		tab16.qty_total,                                                                                                                            "
            +"		tab16.settle_amount_total,                                                                                                                  "
            +"		tab16.settle_can_qty_total,                                                                                                                     "
            +"		tab16.advance_unpay_total                                                                                                                   "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select pod.po_order_id,JSON_ARRAYAGG(                                                                                           "
            +"	    JSON_OBJECT(                                                                                                                                "
            +"	        'sku_code', pod.sku_code,                                                                                                               "
            +"	        'sku_name', pod.sku_name,                                                                                                               "
            +"	        'origin', pod.origin,                                                                                                                   "
            +"	        'sku_id', pod.sku_id,                                                                                                                   "
            +"	        'unit_id', pod.unit_id,                                                                                                                 "
            +"	        'qty', pod.qty,                                                                                                                         "
            +"	        'price', pod.price,                                                                                                                     "
            +"	        'amount', pod.amount,                                                                                                                   "
            +"	        'tax_amount', pod.tax_amount,                                                                                                           "
            +"	        'tax_rate', pod.tax_rate,                                                                                                               "
            +"	        'goods_id', pod.goods_id,                                                                                                               "
            +"	        'goods_name', pod.goods_name,                                                                                                           "
            +"	        'goods_code', pod.goods_code,                                                                                                           "
            +"	        'inventory_in_total', podt.inventory_in_total,                                                                                          "
            +"	        'settle_can_qty_total', podt.settle_can_qty_total                                                                                   "
            +"	    )) as detailListData,                                                                                                                       "
            +"	    GROUP_CONCAT(pod.sku_name) as goods_name                                                                                                    "
            +"	     from b_po_order_detail pod LEFT JOIN b_po_order_detail_total podt ON pod.po_order_id = podt.po_order_id GROUP BY pod.po_order_id) tab2 ON tab1.id = tab2.po_order_id"
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_PO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_PO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_PO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_PO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_PO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                  "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		LEFT JOIN b_po_order_total tab16 ON tab16.po_order_id = tab1.id                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND tab16.settle_can_qty_total > 0                                                                                                         "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                      "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                               "
            +"		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )                                                               "

            + "   <if test='p1.is_advance_pay != null' >                                                                                                                  "
            + "       and tab16.advance_amount_total > 0                                                                                                                  "
            + "   </if>                                                                                                                                                   "

            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab11.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.settle_list != null and p1.settle_list.length!=0' >                                                                                              "
            + "    and tab1.settle_type in                                                                                                                                              "
            + "        <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >                                                                                              "
            + "    and tab1.bill_type in                                                                                                                                              "
            + "        <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_order_detail subt1                                                                                                                      "
            + "            INNER JOIN b_po_order subt2 ON subt1.po_order_id = subt2.id                                                                               "
            + "          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')                                                                         "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_order_id                                                                                                                         "
            +" </script>                                                                                                                                       ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoOrderDetailListTypeHandler.class),
    })
    IPage<PoOrderVo> selectOrderListWithSettlePage(Page<PoOrderVo> page, @Param("p1") PoOrderVo searchCondition);

    /**
     * 采购订单结算信息统计
     */
    @Select("	<script>                                                                                                                                           "
            +"	SELECT 	                                                                                                                                          "
            +"		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,                                                                        "       // 订单总金额
            +"		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,                                                                                      "       // 订单总采购数量
            +"		SUM( IFNULL(tab2.advance_unpay_total,0) )  as  advance_unpay_total,                                                                      "       // 订单：预付未付总金额
            +"		SUM( IFNULL(tab2.advance_pay_total,0) )  as  advance_pay_total,                                                                          "       // 订单：预付已付款总金额
            +"		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,                                                                      "       // 订单：结算总金额
            +"		SUM( IFNULL(tab2.settled_qty_total,0) )  as  settled_qty_total,                                                                            "       // 订单：结算总数量
            +"		SUM( IFNULL(tab2.payable_pay_total,0) )  as  payable_pay_total,                                                                          "       // 订单：应付款总金额
            +"		SUM( IFNULL(tab2.paid_total,0) )  as  paid_total,                                                                                        "       // 订单：已付款总金额
            +"		SUM( IFNULL(tab2.tax_amount_total,0) )  as  tax_amount_total,                                                                            "       // 订单：税额总金额
            +"		SUM( IFNULL(tab2.advance_amount_total,0) )  as  advance_amount_total,                                                                    "       // 订单：预付款总金额
            +"		SUM( IFNULL(tab4.inventory_in_total,0) )  as  inventory_in_total_sum,                                                                    "       // 实际入库合计
            +"		SUM( IFNULL(tab4.settle_can_qty_total,0) )  as  settle_can_qty_total_sum                                                                 "       // 待结算数量合计
            +"	FROM                                                                                                                                             "
            +"		b_po_order tab1                                                                                                                              "
            +"		LEFT JOIN b_po_order_total AS tab2 ON tab1.id = tab2.po_order_id                                                                               "
            +"      LEFT JOIN b_po_contract tab3 on tab3.id = tab1.po_contract_id                                                                             "
            +"      LEFT JOIN b_po_order_detail_total AS tab4 ON tab1.id = tab4.po_order_id                                                                        "
            +"		WHERE TRUE                                                                                                                                   "
            +"		 AND tab1.is_del = false                                                                                                                     "
            +"		 AND tab2.settle_can_qty_total > 0                                                                                                          "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               "
            +"		 AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or #{p1.po_contract_code} = '')                       "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                               "
            +"		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )                                                               "

            + "   <if test='p1.is_advance_pay != null' >                                                                                                                  "
            + "       and tab2.advance_amount_total > 0                                                                                                                  "
            + "   </if>                                                                                                                                                   "

            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab3.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.settle_list != null and p1.settle_list.length!=0' >                                                                                              "
            + "    and tab1.settle_type in                                                                                                                                              "
            + "        <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >                                                                                              "
            + "    and tab1.bill_type in                                                                                                                                              "
            + "        <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_order_detail subt1                                                                                                                      "
            + "            INNER JOIN b_po_order subt2 ON subt1.po_order_id = subt2.id                                                                               "
            + "          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')                                                                         "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "

            +"  </script>                                                                                                                                  "
    )
    PoOrderVo queryOrderListWithSettlePageSum( @Param("p1") PoOrderVo searchCondition);
}
