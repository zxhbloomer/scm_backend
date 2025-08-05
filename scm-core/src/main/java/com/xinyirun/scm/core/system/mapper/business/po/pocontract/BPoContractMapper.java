package com.xinyirun.scm.core.system.mapper.business.po.pocontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PoContractDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 采购合同表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BPoContractMapper extends BaseMapper<BPoContractEntity> {


    /**
     * 分页查询
     */
    @Select("""
            <script>
            -- 分页查询采购合同信息，包含状态、类型等字典翻译和明细数据聚合
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
            	b_po_contract tab1
                LEFT JOIN (select po_contract_id,JSON_ARRAYAGG(
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
                 from b_po_contract_detail GROUP BY po_contract_id) tab2 ON tab1.id = tab2.po_contract_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_po_contract_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_po_contract_type' AND tab4.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_po_contract_delivery_type' AND tab5.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_po_contract_settle_type' AND tab6.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_po_contract_bill_type' AND tab7.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_po_contract_payment_type' AND tab8.dict_value = tab1.payment_type
                    LEFT JOIN b_po_order tab12 on tab12.po_contract_id = tab1.id
                   and tab12.is_del = false and tab1.type = '0'
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            	 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            	 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )

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
                        b_po_contract_detail subt1
                        INNER JOIN b_po_contract subt2 ON subt1.po_contract_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>
            GROUP BY
            	tab2.po_contract_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoContractDetailListTypeHandler.class),
    })
    IPage<BPoContractVo> selectPage(Page<BPoContractVo> page, @Param("p1") BPoContractVo searchCondition);


    /**
     * id查询
     */
    @Select("""
            -- 根据ID查询采购合同详细信息，包含字典翻译、明细数据和附件信息
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
            	b_po_contract tab1
                LEFT JOIN (select po_contract_id,JSON_ARRAYAGG(
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
                 from b_po_contract_detail GROUP BY po_contract_id) tab2 ON tab1.id = tab2.po_contract_id
            	LEFT JOIN b_po_contract_attach tab3 on tab1.id = tab3.po_contract_id
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_po_contract_status' AND tab6.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_po_contract_type' AND tab7.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_po_contract_delivery_type' AND tab8.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab9 ON tab9.code = 'b_po_contract_settle_type' AND tab9.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab10 ON tab10.code = 'b_po_contract_bill_type' AND tab10.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab11 ON tab11.code = 'b_po_contract_payment_type' AND tab11.dict_value = tab1.payment_type
            	-- p1: 采购合同主表ID参数
            	WHERE TRUE AND tab1.id = #{p1}
            	 -- is_del = false: 删除0-未删除，1-已删除
            	 AND tab1.is_del = false
            GROUP BY
            	tab2.po_contract_id
            """)    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoContractDetailListTypeHandler.class),
    })
    BPoContractVo selectId(@Param("p1") Integer id);

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
            	b_po_contract tab1
            	LEFT JOIN b_po_contract_total tab2  ON tab1.id = tab2.po_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            	 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            	 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )

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
                        b_po_contract_detail subt1
                        INNER JOIN b_po_contract subt2 ON subt1.po_contract_id = subt2.id
                      where subt1.virtual_sku_code_name like CONCAT('%', #{p1.goods_name}, '%')
                        and subt2.id = tab1.id
                     )
               </if>

              </script>
            """)
    BPoContractVo querySum(@Param("p1") BPoContractVo searchCondition);

    /**
     * 校验合同编号是否重复
     */
    @Select("""
            -- 校验合同编号是否重复，排除当前记录
            select * from b_po_contract where true 
            -- is_del = false: 删除0-未删除，1-已删除
            and is_del = false
            -- 当前记录ID，新增时为null，修改时传入当前ID进行排除
            and (id != #{p1.id} or #{p1.id} is null)
            -- 合同编号，为用户手写编号，如果页面未输入，等于code自动编号
            and contract_code = #{p1.contract_code}
            """)
    List<BPoContractVo> validateDuplicateContractCode(@Param("p1") BPoContractVo bean);


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
            	b_po_contract tab1
                LEFT JOIN (select po_contract_id,JSON_ARRAYAGG(
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
                 from b_po_contract_detail GROUP BY po_contract_id) tab2 ON tab1.id = tab2.po_contract_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_po_contract_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_po_contract_type' AND tab4.dict_value = tab1.type
            	LEFT JOIN s_dict_data  tab5 ON tab5.code = 'b_po_contract_delivery_type' AND tab5.dict_value = tab1.delivery_type
            	LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_po_contract_settle_type' AND tab6.dict_value = tab1.settle_type
            	LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_po_contract_bill_type' AND tab7.dict_value = tab1.bill_type
            	LEFT JOIN s_dict_data  tab8 ON tab8.code = 'b_po_contract_payment_type' AND tab8.dict_value = tab1.payment_type
                    LEFT JOIN b_po_order tab12 on tab12.po_contract_id = tab1.id
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
            	tab2.po_contract_id) as tb1,(select @row_num:=0) tb2
            	  </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BPoContractVo> selectExportList(@Param("p1") BPoContractVo param);

    @Select("""
            -- 统计符合导出条件的记录数
            SELECT
            	count(tab1.id)
            FROM
            	b_po_contract tab1
            	WHERE TRUE
            	 -- is_del = false: 删除0-未删除，1-已删除
            	 AND tab1.is_del = false
            	 -- p1.status: 状态参数：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 -- p1.contract_code: 合同编号参数，为用户手写编号，如果页面未输入，等于code自动编号
            	 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')
            """)
    Long selectExportCount(@Param("p1") BPoContractVo param);

    /**
     * 根据code查询采购合同
     */
    @Select("""
            -- 根据自动生成编号查询采购合同
            select * from b_po_contract 
            -- code: 编号自动生成编号参数
            where code = #{code} 
            -- is_del = false: 删除0-未删除，1-已删除
            and is_del = false
            """)
    BPoContractVo selectByCode(@Param("code") String code);

    /**
     * 根据合同编号查询采购合同
     */
    @Select("""
            -- 根据合同编号查询采购合同
            select * from b_po_contract 
            -- contract_code: 合同编号参数，为用户手写编号，如果页面未输入，等于code自动编号
            where contract_code = #{contract_code} 
            -- is_del = false: 删除0-未删除，1-已删除
            and is_del = false
            """)
    BPoContractVo selectByContractCode(@Param("contract_code") String contract_code);

    /**
     * 根据项目编号查询未完成的采购合同编号列表
     * 用于项目完成时校验，只有当项目下所有采购合同都已完成或作废时，项目才能完成
     * 
     * @param projectCode 项目编号
     * @return List<String> 未完成的采购合同编号列表
     * 状态不等于5(已作废)、6(已完成)的合同
     */
    @Select("""
            SELECT code 
            FROM b_po_contract 
            WHERE project_code = #{projectCode} 
              AND status NOT IN ('5', '6') 
              AND is_del = false
            """)
    List<String> selectUnfinishedContractCodesByProjectCode(@Param("projectCode") String projectCode);

}
