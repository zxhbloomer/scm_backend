package com.xinyirun.scm.core.system.mapper.business.so.socontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.SoContractDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 销售合同表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
@Repository
public interface BSoContractMapper extends BaseMapper<BSoContractEntity> {


    /**
     * 分页查询
     */
    @Select("""
            <script>
            SELECT
            	tab1.*,
            	tab3.label as status_name,
            	tab4.label as type_name,
            	tab5.label as delivery_type_name,
            	tab6.label as settle_type_name,
            	tab7.label as bill_type_name,
            	tab8.label as payment_type_name,
            	iF(tab1.auto_create_order,'是','否') auto_create_name,
            	tab2.detailListData ,
            	tab1.bpm_instance_code as process_code,
            	iF(tab12.id,false,true) existence_order,
            	tab13.name as c_name,
            	tab14.name as u_name
            FROM
            	b_so_contract tab1
                LEFT JOIN (select so_contract_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'qty',qty,
                'price', price,
                'amount', amount,
                'tax_amount', tax_amount,
                'tax_rate', tax_rate )) as detailListData
                 from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_contract_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_contract_type' AND tab4.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_contract_delivery_type' AND tab5.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_contract_settle_type' AND tab6.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_contract_bill_type' AND tab7.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_so_contract_payment_type' AND tab8.dict_value = tab1.payment_type
                    LEFT JOIN b_so_order tab12 on tab12.so_contract_id = tab1.id
                   and tab12.is_del = false and tab1.type = '0'
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            	 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            	 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )

               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='p1.type_list != null and p1.type_list.length!=0' >
                and tab1.type in
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
                        b_so_contract_detail subt1
                        INNER JOIN b_so_contract subt2 ON subt1.so_contract_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>
            GROUP BY
            	tab2.so_contract_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoContractDetailListTypeHandler.class),
    })
    IPage<BSoContractVo> selectPage(Page<BSoContractVo> page, @Param("p1") BSoContractVo searchCondition);


    /**
     * id查询
     */
    @Select("""
            SELECT
            	tab1.*,
            	tab2.detailListData,
            	tab3.one_file as doc_att_file,
            	tab6.label as status_name,
            	tab7.label as type_name,
            	tab8.label as delivery_type_name,
            	tab9.label as settle_type_name,
            	tab10.label as bill_type_name,
            	tab11.label as payment_type_name,
            	iF(tab1.auto_create_order,'是','否') auto_create_name
            FROM
            	b_so_contract tab1
                LEFT JOIN (select so_contract_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'goods_id', goods_id,
                'goods_code', goods_code,
                'goods_name', goods_name,
                'qty',qty,
                'price', price,
                'amount', amount,
                'tax_amount', tax_amount,
                'tax_rate', tax_rate )) as detailListData
                 from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id
            	LEFT JOIN b_so_contract_attach tab3 on tab1.id = tab3.so_contract_id
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_contract_status' AND tab6.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_contract_type' AND tab7.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_so_contract_delivery_type' AND tab8.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab9 ON tab9.code = 'b_so_contract_settle_type' AND tab9.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab10 ON tab10.code = 'b_so_contract_bill_type' AND tab10.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab11 ON tab11.code = 'b_so_contract_payment_type' AND tab11.dict_value = tab1.payment_type
            	WHERE TRUE AND tab1.id = #{p1}
            	 AND tab1.is_del = false
            GROUP BY
            	tab2.so_contract_id
            """)    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoContractDetailListTypeHandler.class),
    })
    BSoContractVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("""
            <script>
            SELECT
            	SUM( IFNULL(tab2.order_amount_total,0) )  as  order_amount_total,
            	SUM( IFNULL(tab2.order_total,0) )  as  order_total,
            	SUM( IFNULL(tab2.advance_unpay_total,0) )  as  advance_unpay_total,
            	SUM( IFNULL(tab2.advance_pay_total,0) )  as  advance_pay_total,
            	SUM( IFNULL(tab2.settle_amount_total,0) )  as  settle_amount_total
            FROM
            	b_so_contract tab1
            	LEFT JOIN b_so_contract_total tab2  ON tab1.id = tab2.so_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            	 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            	 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )

               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='p1.type_list != null and p1.type_list.length!=0' >
                and tab1.type in
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
                        b_so_contract_detail subt1
                        INNER JOIN b_so_contract subt2 ON subt1.so_contract_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>

              </script>
            """)
    BSoContractVo querySum(@Param("p1") BSoContractVo searchCondition);

    /**
     * 校验合同编号是否重复
     */
    @Select("""
            select * from b_so_contract where true and is_del = false
            and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
            and contract_code = #{p1.contract_code}
            """)
    List<BSoContractVo> validateDuplicateContractCode(@Param("p1")BSoContractVo bean);


    /**
     * 导出查询
     */
    @Select("""
            <script>
            SELECT @row_num:= @row_num+ 1 as no,tb1.* from (
               SELECT
            	tab1.*,
            	tab3.label as status_name,
            	tab4.label as type_name,
            	tab5.label as delivery_type_name,
            	tab6.label as settle_type_name,
            	tab7.label as bill_type_name,
            	tab8.label as payment_type_name,
            	iF(tab1.auto_create_order,'是','否') auto_create_name,
            	tab2.detailListData ,
            	tab1.bpm_instance_code as process_code,
            	iF(tab12.id,false,true) existence_order,
            	tab13.name as c_name,
            	tab14.name as u_name
            FROM
            	b_so_contract tab1
                LEFT JOIN (select so_contract_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'spec', spec,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'goods_id', goods_id,
                'goods_code', goods_code,
                'goods_name', goods_name,
                'qty',qty,
                'price', price,
                'amount', amount,
                'tax_amount', tax_amount,
                'tax_rate', tax_rate )) as detailListData
                 from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_contract_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_so_contract_type' AND tab4.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_so_contract_delivery_type' AND tab5.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_so_contract_settle_type' AND tab6.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_so_contract_bill_type' AND tab7.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_so_contract_payment_type' AND tab8.dict_value = tab1.payment_type
                    LEFT JOIN b_so_order tab12 on tab12.so_contract_id = tab1.id
                   and tab12.is_del = false and tab1.type = '0'
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
               <if test='p1.ids != null and p1.ids.length != 0' >
                and tab1.id in
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            GROUP BY
            	tab2.so_contract_id) as tb1,(select @row_num:=0) tb2
            	  </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BSoContractVo> selectExportList(@Param("p1")BSoContractVo param);

    @Select("""
            SELECT
            	count(tab1.id)
            FROM
            	b_so_contract tab1
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            """)
    Long selectExportCount(@Param("p1")BSoContractVo param);

    /**
     * 根据code查询销售合同
     */
    @Select("""
            select * from b_so_contract where code = #{code} and is_del = false
            """)
    BSoContractVo selectByCode(@Param("code") String code);

    /**
     * 根据code查询销售合同
     */
    @Select("""
            select * from b_so_contract where contract_code = #{contract_code} and is_del = false
            """)
    BSoContractVo selectByContractCode(@Param("contract_code") String contract_code);

}