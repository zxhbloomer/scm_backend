package com.xinyirun.scm.core.system.mapper.business.so.soorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.SoOrderDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 销售订单表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Repository
public interface BSoOrderMapper extends BaseMapper<BSoOrderEntity> {
    
    /**
     * 分页查询
     */
    @Select("""
            <script>
            SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total,
            		tab16.settle_can_qty_total
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
            
               <if test='p1.is_advance_receive != null' >
                   and tab16.advance_amount_total > 0
               </if>
            
               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.type_list != null and p1.type_list.length!=0' >
                and tab11.type in
                    <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.settle_list != null and p1.settle_list.length!=0' >
                and tab1.settle_type in
                    <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >
                and tab1.bill_type in
                    <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.goods_name != null' >
               and exists(
                      select
                        1
                      from
                        b_so_order_detail subt1
                        INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>
            	GROUP BY
            		tab2.so_order_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<BSoOrderVo> selectPage(Page<BSoOrderVo> page, @Param("p1") BSoOrderVo searchCondition);

    /**
     * 按应收退款条件分页查询
     */
    @Select("""
            <script>
            SELECT
            		tab1.*,
            		tab1.id as so_order_id,
            		tab1.code as so_order_code,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total,
            		tab16.settle_can_qty_total
            		
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
            		 AND tab16.advance_refund_amount_total > 0
            
               <if test='p1.is_advance_receive != null' >
                   and tab16.advance_amount_total > 0
               </if>
            
               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.type_list != null and p1.type_list.length!=0' >
                and tab11.type in
                    <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.settle_list != null and p1.settle_list.length!=0' >
                and tab1.settle_type in
                    <foreach collection='p1.settle_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.bill_type_list != null and p1.bill_type_list.length!=0' >
                and tab1.bill_type in
                    <foreach collection='p1.bill_type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            
               <if test='p1.goods_name != null' >
               and exists(
                      select
                        1
                      from
                        b_so_order_detail subt1
                        INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>
            	GROUP BY
            		tab2.so_order_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<BSoOrderVo> selectPageByArrefund(Page<BSoOrderVo> page, @Param("p1") BSoOrderVo searchCondition);



    /**
     * id查询
     */
    @Select("""
            	SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tabb1.one_file as doc_att_file,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total,
            		tab16.settle_can_qty_total
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
            		LEFT JOIN b_so_order_attach tabb1 on tab1.id = tabb1.so_order_id
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.id = #{p1} AND tab1.is_del = false
            	GROUP BY
            		tab2.so_order_id
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    BSoOrderVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("""
            <script>
            	SELECT
            		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,
            		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,
            		SUM( IFNULL(tab2.advance_unreceive_total,0) )  as  advance_unreceive_total,
            		SUM( IFNULL(tab2.advance_receive_total,0) )  as  advance_receive_total,
            		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,
            		SUM( IFNULL(tab3.inventory_out_total,0) )  as  inventory_out_total_sum,
            		SUM( IFNULL(tab3.settle_can_qty_total,0) )  as  settle_can_qty_total,
            		SUM( IFNULL(tab2.advance_refund_amount_total,0) )  as  advance_refund_amount_total,
            		SUM( IFNULL(tab2.advance_received_total,0) )  as  advance_received_total,
            		SUM( IFNULL(tab2.advance_cancelreceive_total,0) )  as  advance_cancelreceive_total
            	FROM
            		b_so_order tab1
            		LEFT JOIN b_so_order_total AS tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN b_so_order_detail_total AS tab3 ON tab1.id = tab3.so_order_id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )

                   <if test="p1.is_advance_receive != null" >
                       and tab2.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab3.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
              </script>
            """)
    BSoOrderVo querySum(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 按应收退款条件查询合计信息
     */
    @Select("""
        <script>
        	SELECT
        		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,
        		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,
        		SUM( IFNULL(tab2.advance_unreceive_total,0) )  as  advance_unreceive_total,
        		SUM( IFNULL(tab2.advance_receive_total,0) )  as  advance_receive_total,
        		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,
        		SUM( IFNULL(tab3.inventory_out_total,0) )  as  inventory_out_total_sum,
        		SUM( IFNULL(tab3.settle_can_qty_total,0) )  as  settle_can_qty_total,
        		SUM( IFNULL(tab2.advance_refund_amount_total,0) )  as  advance_refund_amount_total,
        		SUM( IFNULL(tab2.advance_received_total,0) )  as  advance_received_total,
        		SUM( IFNULL(tab2.advance_cancelreceive_total,0) )  as  advance_cancelreceive_total
        	FROM
        		b_so_order tab1
        		LEFT JOIN b_so_order_total AS tab2 ON tab1.id = tab2.so_order_id
        		LEFT JOIN b_so_order_detail_total AS tab3 ON tab1.id = tab3.so_order_id
        	WHERE TRUE
        		 AND tab1.is_del = false
        		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
        		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
        		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
        		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
        		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
        		 AND tab2.advance_refund_amount_total > 0

                   <if test="p1.is_advance_receive != null" >
                       and tab2.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab3.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
              </script>
            """)
    BSoOrderVo querySumByArrefund(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 标准合同下推校验 只能下推一个订单
     * 使用常量：DICT_B_SO_CONTRACT_TYPE_ZERO = "0" (标准合同类型)
     */
    @Select("""
            SELECT * FROM b_so_order tab1 LEFT JOIN b_so_contract tab2 ON tab1.so_contract_id = tab2.id                                           
            WHERE tab1.is_del = FALSE AND tab2.type = '0' AND tab2.id = #{p1.so_contract_id}       
            """)
    List<BSoOrderVo> validateDuplicateContractId(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 导出数量统计查询
     * 使用常量：status = 订单状态字典值
     */
    @Select("""
            SELECT                                                                                                                                             
            	count(tab1.id)                                                                                                                                  
            	FROM                                                                                                                                            
            		b_so_order tab1                                                                                                                          
            		WHERE TRUE                                                                                                                                  
            		 AND tab1.is_del = false                                                                                                                    
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              
            		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')                      
            """)
    Long selectExportCount(@Param("p1") BSoOrderVo param);

    /**
     * 导出查询
     */
    @Select(
            """
            <script>
            SELECT @row_num:= @row_num+ 1 as no,tb1.* from (
            	SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
                   <if test="p1.ids != null and p1.ids.length != 0" >
                    and tab1.id in
                        <foreach collection="p1.ids" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
            	GROUP BY
            		tab2.so_order_id) as tb1,(select @row_num:=0) tb2
            		  </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    List<BSoOrderVo> selectExportList(@Param("p1") BSoOrderVo param);

    /**
     * 根据销售合同id,状态 查询销售订单
     * 使用常量：status != 指定状态值（排除特定状态的订单）
     */
    @Select("select * from b_so_order where so_contract_id = #{p1} and status != #{p2} and is_del = false")
    List<BSoOrderVo> selectBySoContractIdNotByStatus(@Param("p1")Integer id, @Param("p2") String dictBSoOrderStatusFive);

    /**
     * 根据销售合同id 查询销售订单
     */
    @Select("select * from b_so_order where so_contract_id = #{p1} and is_del = false")
    List<BSoOrderVo> selectBySoContractId(@Param("p1")Integer id);

    /**
     * 根据销售合同id 查询有效销售订单
     * 使用常量：DICT_B_SO_ORDER_STATUS_FIVE = "5" (已取消状态)
     */
    @Select("""
            select * from b_so_order 
            where so_contract_id = #{p1} 
              and is_del = false 
              and status not in ('5')
            """)
    List<BSoOrderVo> selectLiveSoBySoContractId(@Param("p1")Integer id);

    /**
     * 根据销售合同id查询未完成的销售订单
     * 销售订单状态：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
     * 排除状态5-已作废、6-已完成的订单
     */
    @Select("""
            select * from b_so_order 
            where so_contract_id = #{p1} 
            and status not in ('5', '6')
            and is_del = false
            """)
    List<BSoOrderVo> selectUnfinishedOrdersBySoContractId(@Param("p1")Integer id);

    /**
     * 根据code查询销售订单
     */
    @Select("select * from b_so_order where code = #{code} and is_del = false")
    BSoOrderVo selectByCode(@Param("code") String code);

    /**
     * 分页查询包含结算信息
     */
    @Select("""
            <script>
            SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.settle_amount_total,
            		tab16.settle_can_qty_total,
            		tab16.advance_unreceive_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'so_order_detail_id', t1.id,
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND tab16.settle_can_qty_total > 0
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
                   <if test="p1.is_advance_receive != null" >
                       and tab16.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab11.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null and p1.goods_name != ''" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
            	GROUP BY
            		tab2.so_order_id
             </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<BSoOrderVo> selectOrderListWithSettlePage(Page<BSoOrderVo> page, @Param("p1") BSoOrderVo searchCondition);

    /**
     * 销售订单结算信息统计
     */
    @Select("""
            <script>
            	SELECT
            		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,
            		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,
            		SUM( IFNULL(tab2.advance_unreceive_total,0) )  as  advance_unreceive_total,
            		SUM( IFNULL(tab2.advance_receive_total,0) )  as  advance_receive_total,
            		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,
            		SUM( IFNULL(tab2.settled_qty_total,0) )  as  settled_qty_total,
            		SUM( IFNULL(tab2.received_total,0) )  as  received_total,
            		SUM( IFNULL(tab2.tax_amount_total,0) )  as  tax_amount_total,
            		SUM( IFNULL(tab2.advance_amount_total,0) )  as  advance_amount_total,
            		SUM( IFNULL(tab4.inventory_out_total,0) )  as  inventory_out_total_sum,
            		SUM( IFNULL(tab4.settle_can_qty_total,0) )  as  settle_can_qty_total,
            		SUM( IFNULL(tab2.advance_refund_amount_total,0) )  as  advance_refund_amount_total,
            		SUM( IFNULL(tab2.advance_received_total,0) )  as  advance_received_total,
            		SUM( IFNULL(tab2.advance_cancelreceive_total,0) )  as  advance_cancelreceive_total
            	FROM
            		b_so_order tab1
            		LEFT JOIN b_so_order_total AS tab2 ON tab1.id = tab2.so_order_id
                  LEFT JOIN b_so_contract tab3 on tab3.id = tab1.so_contract_id
                  LEFT JOIN b_so_order_detail_total AS tab4 ON tab1.id = tab4.so_order_id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND tab2.settle_can_qty_total > 0
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )

                   <if test="p1.is_advance_receive != null" >
                       and tab2.advance_amount_total > 0
                   </if>

                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>

                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab3.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>

                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>

                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>

                   <if test="p1.goods_name != null" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>

              </script>
            """)
    BSoOrderVo queryOrderListWithSettlePageSum(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 销售订单专用-分页查询销售订单信息
     */
    @Select("""
            <script>
            SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total,
            		tab16.settle_amount_total,
            		tab16.settle_can_qty_total,
            		tab16.advance_unreceive_total
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'so_order_detail_id', t1.id,
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
                   <if test="p1.is_advance_receive != null" >
                       and tab16.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab11.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null and p1.goods_name != ''" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
            	GROUP BY
            		tab2.so_order_id
             </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<BSoOrderVo> selectOrderListForSalesPage(Page<BSoOrderVo> page, @Param("p1") BSoOrderVo searchCondition);

    /**
     * 销售订单专用-销售订单统计
     */
    @Select("""
            <script>
            	SELECT
            		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,
            		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,
            		SUM( IFNULL(tab2.advance_unreceive_total,0) )  as  advance_unreceive_total,
            		SUM( IFNULL(tab2.advance_receive_total,0) )  as  advance_receive_total,
            		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,
            		SUM( IFNULL(tab2.settled_qty_total,0) )  as  settled_qty_total,
            		SUM( IFNULL(tab2.received_total,0) )  as  received_total,
            		SUM( IFNULL(tab2.tax_amount_total,0) )  as  tax_amount_total,
            		SUM( IFNULL(tab2.advance_amount_total,0) )  as  advance_amount_total,
            		SUM( IFNULL(tab4.inventory_out_total,0) )  as  inventory_out_total_sum,
            		SUM( IFNULL(tab4.settle_can_qty_total,0) )  as  settle_can_qty_total,
            		SUM( IFNULL(tab2.advance_refund_amount_total,0) )  as  advance_refund_amount_total,
            		SUM( IFNULL(tab2.advance_received_total,0) )  as  advance_received_total,
            		SUM( IFNULL(tab2.advance_cancelreceive_total,0) )  as  advance_cancelreceive_total
            	FROM
            		b_so_order tab1
            		LEFT JOIN b_so_order_total AS tab2 ON tab1.id = tab2.so_order_id
                  LEFT JOIN b_so_contract tab3 on tab3.id = tab1.so_contract_id
                  LEFT JOIN b_so_order_detail_total AS tab4 ON tab1.id = tab4.so_order_id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
                   <if test="p1.is_advance_receive != null" >
                       and tab2.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab3.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null and p1.goods_name != ''" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
              </script>
            """)
    BSoOrderVo queryOrderListForSalesPageSum(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 销售订单专用-获取销售订单明细数据
     */
    @Select("""
            SELECT 
                t1.id as so_order_detail_id,
                t1.so_order_id,
                t1.goods_id,
                t1.goods_code,
                t1.goods_name,
                t1.sku_code,
                t1.sku_name,
                t1.sku_id,
                t1.unit_id,
                t1.origin,
                t1.qty,
                t1.price,
                t1.amount
            FROM b_so_order_detail t1 
            INNER JOIN b_so_order t2 ON t1.so_order_id = t2.id
            WHERE t2.id = #{p1.id} 
            AND t2.is_del = false
            """)
    List<BSoOrderDetailVo> selectDetailData(@Param("p1") BSoOrderVo searchCondition);

    /**
     * 货权转移专用-分页查询销售订单信息
     */
    @Select("""
            <script>
            SELECT
            		tab1.*,
            		tab3.label as status_name,
            		tab4.label as delivery_type_name,
            		tab5.label as settle_type_name,
            		tab6.label as bill_type_name,
            		tab7.label as payment_type_name,
            		tab2.goods_name ,
            		tab2.detailListData ,
            		tab1.bpm_instance_code as process_code,
            		tab12.label as type_name,
            		tab13.name as c_name,
            		tab14.name as u_name,
            		tab11.sign_date,
            		tab11.expiry_date,
            		tab16.advance_amount_total,
            		tab16.advance_receive_total,
            		tab16.received_total,
            		tab16.amount_total,
            		tab16.tax_amount_total,
            		tab16.qty_total,
            		tab16.advance_refund_amount_total,
            		tab16.advance_received_total,
            		tab16.advance_cancelreceive_total,
            		tab16.settle_amount_total,
            		tab16.settle_can_qty_total,
            		tab16.advance_unreceive_total
            	FROM
            		b_so_order tab1
            	    LEFT JOIN (select t1.so_order_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'so_order_detail_id', t1.id,
            	        'sku_code', t1.sku_code,
            	        'sku_name', t1.sku_name,
            	        'origin', t1.origin,
            	        'sku_id', t1.sku_id,
            	        'unit_id', t1.unit_id,
            	        'qty', t1.qty,
            	        'price', t1.price,
            	        'amount', t1.amount,
            	        'tax_amount', t1.tax_amount,
            	        'tax_rate', t1.tax_rate,
            	        'goods_id', t1.goods_id,
            	        'goods_name', t1.goods_name,
            	        'goods_code', t1.goods_code,
            	        'inventory_out_total', t2.inventory_out_total,
            	        'settle_can_qty_total', t2.settle_can_qty_total
            	    )) as detailListData,
            	    GROUP_CONCAT(t1.sku_name) as goods_name
            	     from b_so_order_detail t1 LEFT JOIN b_so_order_detail_total t2 ON t2.so_order_detail_id = t1.id GROUP BY t1.so_order_id) tab2 ON tab1.id = tab2.so_order_id
            		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_order_status' AND tab3.dict_value = tab1.status
            		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_order_delivery_type' AND tab4.dict_value = tab1.delivery_type
            		LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_order_settle_type' AND tab5.dict_value = tab1.settle_type
            		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_order_bill_type' AND tab6.dict_value = tab1.bill_type
            		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_order_payment_type' AND tab7.dict_value = tab1.payment_type
                  LEFT JOIN b_so_contract tab11 on tab11.id = tab1.so_contract_id
            		LEFT JOIN s_dict_data tab12 ON tab12.code = 'b_so_contract_type' AND tab12.dict_value = tab11.type
                  LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
                  LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            		LEFT JOIN b_so_order_total tab16 ON tab16.so_order_id = tab1.id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
                   <if test="p1.is_advance_receive != null" >
                       and tab16.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab11.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null and p1.goods_name != ''" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
            	GROUP BY
            		tab2.so_order_id
             </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<BSoOrderVo> selectOrderListForCargoRightTransferPage(Page<BSoOrderVo> page, @Param("p1") BSoOrderVo searchCondition);

    /**
     * 货权转移专用-销售订单统计
     */
    @Select("""
            <script>
            	SELECT
            		SUM( IFNULL(tab2.amount_total,0) )  as  amount_total,
            		SUM( IFNULL(tab2.qty_total,0) )  as  qty_total,
            		SUM( IFNULL(tab2.advance_unreceive_total,0) )  as  advance_unreceive_total,
            		SUM( IFNULL(tab2.advance_receive_total,0) )  as  advance_receive_total,
            		SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total,
            		SUM( IFNULL(tab2.settled_qty_total,0) )  as  settled_qty_total,
            		SUM( IFNULL(tab2.received_total,0) )  as  received_total,
            		SUM( IFNULL(tab2.tax_amount_total,0) )  as  tax_amount_total,
            		SUM( IFNULL(tab2.advance_amount_total,0) )  as  advance_amount_total,
            		SUM( IFNULL(tab4.inventory_out_total,0) )  as  inventory_out_total_sum,
            		SUM( IFNULL(tab4.settle_can_qty_total,0) )  as  settle_can_qty_total,
            		SUM( IFNULL(tab2.advance_refund_amount_total,0) )  as  advance_refund_amount_total,
            		SUM( IFNULL(tab2.advance_received_total,0) )  as  advance_received_total,
            		SUM( IFNULL(tab2.advance_cancelreceive_total,0) )  as  advance_cancelreceive_total
            	FROM
            		b_so_order tab1
            		LEFT JOIN b_so_order_total AS tab2 ON tab1.id = tab2.so_order_id
                  LEFT JOIN b_so_contract tab3 on tab3.id = tab1.so_contract_id
                  LEFT JOIN b_so_order_detail_total AS tab4 ON tab1.id = tab4.so_order_id
            		WHERE TRUE
            		 AND tab1.is_del = false
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')
            		 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            		 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            		 AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null   )
                   <if test="p1.is_advance_receive != null" >
                       and tab2.advance_amount_total > 0
                   </if>
                   <if test="p1.status_list != null and p1.status_list.length!=0" >
                    and tab1.status in
                        <foreach collection="p1.status_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.type_list != null and p1.type_list.length!=0" >
                    and tab3.type in
                        <foreach collection="p1.type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.settle_list != null and p1.settle_list.length!=0" >
                    and tab1.settle_type in
                        <foreach collection="p1.settle_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.bill_type_list != null and p1.bill_type_list.length!=0" >
                    and tab1.bill_type in
                        <foreach collection="p1.bill_type_list" item="item" index="index" open="(" separator="," close=")">
                         #{item}
                        </foreach>
                   </if>
                   <if test="p1.goods_name != null and p1.goods_name != ''" >
                   and exists(
                          select
                            1
                          from
                            b_so_order_detail subt1
                            INNER JOIN b_so_order subt2 ON subt1.so_order_id = subt2.id
                          where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                            and subt2.id = tab1.id
                         )
                   </if>
              </script>
            """)
    BSoOrderVo queryOrderListForCargoRightTransferPageSum(@Param("p1") BSoOrderVo searchCondition);
}