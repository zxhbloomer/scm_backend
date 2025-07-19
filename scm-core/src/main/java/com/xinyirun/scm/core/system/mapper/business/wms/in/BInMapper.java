package com.xinyirun.scm.core.system.mapper.business.wms.in;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wms.in.BInEntity;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;

/**
 * <p>
 * 入库单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Mapper
public interface BInMapper extends BaseMapper<BInEntity> {

    /**
     * 分页查询入库单
     */
    @Select("""
            <script>
            SELECT
            	tab1.*,
            	t1.name as owner_name,
            	t2.name as consignor_name,
            	t9.name as supplier_name,
            	t3.name as warehouse_name,
            	t4.name as location_name,
            	t5.name as bin_name,
            	t6.name as goods_name,
            	t7.spec as sku_name,
            	t8.name as unit_name,
            	tab3.label as status_name,
            	tab4.label as type_name,
            	tab13.name as c_name,
            	tab14.name as u_name,
            	tab15.code as plan_code,
            	tab16.no as plan_no,
            	tab15.plan_time as plan_time
            FROM
            	b_in tab1
            	LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id
            	LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id
            	LEFT JOIN m_enterprise t9 ON t9.id = tab1.supplier_id
            	LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id
            	LEFT JOIN m_location t4 ON t4.id = tab1.location_id
            	LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id
            	LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id
            	LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code
            	LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id
            	LEFT JOIN s_dict_data tab3 ON tab3.code = 'b_in_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data tab4 ON tab4.code = 'b_in_type' AND tab4.dict_value = tab1.type
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
              LEFT JOIN b_in_plan_detail tab16 ON tab16.id = tab1.plan_detail_id
              LEFT JOIN b_in_plan tab15 ON tab15.id = tab16.in_plan_id
            	WHERE TRUE
            	 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')
            	 AND (tab1.order_code like CONCAT('%', #{param.order_code}, '%') or #{param.order_code} is null or #{param.order_code} = '')
            	 AND (tab1.contract_code like CONCAT('%', #{param.contract_code}, '%') or #{param.contract_code} is null or #{param.contract_code} = '')
            	 AND (tab1.code like CONCAT('%', #{param.code}, '%') or #{param.code} is null or #{param.code} = '')
            	 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')
            	 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)
            	 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)
            	 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)
            	 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)

               <if test='param.status_list != null and param.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.type_list != null and param.type_list.length!=0' >
                and tab1.type in
                    <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.goods_name != null' >
               and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))
               </if>

            	ORDER BY tab1.c_time DESC
            </script>
            """)
    IPage<BInVo> selectPage(Page<BInVo> page, @Param("param") BInVo searchCondition);

    /**
     * 根据ID查询入库单详情
     */
    @Select("""
            SELECT
            	tab1.*,
            	t1.name as owner_name,
            	t2.name as consignor_name,
            	t9.name as supplier_name,
            	t3.name as warehouse_name,
            	t4.name as location_name,
            	t5.name as bin_name,
            	t6.name as goods_name,
            	t7.spec as sku_name,
            	t8.name as unit_name,
            	tab3.label as status_name,
            	tab4.label as type_name,
            	tab5.one_file as doc_one_file,
            	tab5.two_file as doc_two_file,
            	tab5.three_file as doc_three_file,
            	tab5.four_file as doc_four_file,
            	tab13.name as c_name,
            	tab14.name as u_name,
            	tab15.type as po_contract_type,
            	tab16.label as po_contract_type_name,
            	tab15.purchaser_name,
            	tab15.payment_type as po_contract_payment_type,
            	tab17.label as payment_type_name,
            	tab15.settle_type as po_contract_settle_type,
            	tab18.label as settle_type_name,
            	tab15.delivery_date,
            	tab15.delivery_location,
            	tab19.status as po_order_status,
            	tab20.label as po_order_status_name,
            	tab19.delivery_type as po_order_delivery_type,
            	tab21.label as delivery_type_name,
            	tab22.code as plan_code,
            	tab23.no as plan_no,
            	tab22.plan_time as plan_time
            FROM
            	b_in tab1
            	LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id
            	LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id
            	LEFT JOIN m_enterprise t9 ON t9.id = tab1.supplier_id
            	LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id
            	LEFT JOIN m_location t4 ON t4.id = tab1.location_id
            	LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id
            	LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id
            	LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code
            	LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id
            	LEFT JOIN s_dict_data tab3 ON tab3.code = 'b_in_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data tab4 ON tab4.code = 'b_in_type' AND tab4.dict_value = tab1.type
            	LEFT JOIN b_in_attach tab5 ON tab5.in_id = tab1.id
            	LEFT JOIN b_po_contract tab15 ON tab15.id = tab1.contract_id
            	LEFT JOIN s_dict_data tab16 ON tab16.code = 'b_po_contract_type' AND tab16.dict_value = tab15.type
            	LEFT JOIN s_dict_data tab17 ON tab17.code = 'b_po_contract_payment_type' AND tab17.dict_value = tab15.payment_type
            	LEFT JOIN s_dict_data tab18 ON tab18.code = 'b_po_contract_settle_type' AND tab18.dict_value = tab15.settle_type
            	LEFT JOIN b_po_order tab19 ON tab19.id = tab1.order_id
            	LEFT JOIN s_dict_data tab20 ON tab20.code = 'b_po_order_status' AND tab20.dict_value = tab19.status
            	LEFT JOIN s_dict_data tab21 ON tab21.code = 'b_po_order_delivery_type' AND tab21.dict_value = tab19.delivery_type
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
              LEFT JOIN b_in_plan_detail tab23 ON tab23.id = tab1.plan_detail_id
              LEFT JOIN b_in_plan tab22 ON tab22.id = tab23.in_plan_id
            	WHERE tab1.id = #{id}
            """)
    BInVo selectById(@Param("id") Integer id);

    /**
     * 导出查询
     */
    @Select("""
            <script>
            SELECT
            	tab1.*,
            	t1.name as owner_name,
            	t2.name as consignor_name,
            	t9.name as supplier_name,
            	t3.name as warehouse_name,
            	t4.name as location_name,
            	t5.name as bin_name,
            	t6.name as sku_name,
            	t7.spec as sku_spec,
            	t8.name as unit_name,
            	tab3.label as status_name,
            	tab4.label as type_name,
            	tab13.name as c_name,
            	tab14.name as u_name,
            	tab15.code as plan_code,
            	tab16.no as plan_no,
            	tab15.plan_time as plan_time
            FROM
            	b_in tab1
            	LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id
            	LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id
            	LEFT JOIN m_enterprise t9 ON t9.id = tab1.supplier_id
            	LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id
            	LEFT JOIN m_location t4 ON t4.id = tab1.location_id
            	LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id
            	LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id
            	LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code
            	LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id
            	LEFT JOIN s_dict_data tab3 ON tab3.code = 'b_in_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data tab4 ON tab4.code = 'b_in_type' AND tab4.dict_value = tab1.type
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
              LEFT JOIN b_in_plan_detail tab16 ON tab16.id = tab1.plan_detail_id
              LEFT JOIN b_in_plan tab15 ON tab15.id = tab16.in_plan_id
            	WHERE TRUE
            	 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')
            	 AND (tab1.code = #{param.code} or #{param.code} is null or #{param.code} = '')
            	 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')
            	 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)
            	 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)
            	 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)
            	 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)

               <if test='param.status_list != null and param.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.type_list != null and param.type_list.length!=0' >
                and tab1.type in
                    <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.goods_name != null' >
               and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))
               </if>

            	ORDER BY tab1.c_time DESC
            </script>
            """)
    List<BInVo> selectExportList(@Param("param") BInVo param);

    /**
     * 合计查询
     */
    @Select("""
            <script>
            SELECT
            	SUM(tab1.qty) as qty_total,
            	SUM(tab1.amount) as amount_total
            FROM
            	b_in tab1
            	LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id
            	LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id
            	LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id
            	LEFT JOIN m_location t4 ON t4.id = tab1.location_id
            	LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id
            	LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id
            	LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code
            	LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id
            	LEFT JOIN s_dict_data tab3 ON tab3.code = 'b_in_status' AND tab3.dict_value = tab1.status
            	LEFT JOIN s_dict_data tab4 ON tab4.code = 'b_in_type' AND tab4.dict_value = tab1.type
              LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
              LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
            	WHERE TRUE
            	 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')
            	 AND (tab1.code = #{param.code} or #{param.code} is null or #{param.code} = '')
            	 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')
            	 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)
            	 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)
            	 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)
            	 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)

               <if test='param.status_list != null and param.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.type_list != null and param.type_list.length!=0' >
                and tab1.type in
                    <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='param.goods_name != null' >
               and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))
               </if>

            	ORDER BY tab1.c_time DESC
            </script>
            """)
    BInVo querySum(@Param("param") BInVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     *
     * @param id
     * @return
     */
    @Select("""
            select count(1)
            from b_in t1
            where t1.lot = #{p1}
            """)
    Integer countLot(@Param("p1") String id);

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Select("""
            select *
            from b_in t1
            where t1.id = #{p1}
            for update
            """)
    BInEntity setBillInForUpdate(@Param("p1") Integer id);

    /**
     * 根据入库单ID查询合同ID
     */
    @Select("""
            select DISTINCT t.contract_id FROM b_in t where t.id = #{inboundId}
            """)
    List<Integer> selectContractIdsByInboundId(Integer inboundId);
}
