package com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer.BCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BCargoRightTransferVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.CargoRightTransferDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 'Cl�h Mapper ��
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-20
 */
@Repository
public interface BCargoRightTransferMapper extends BaseMapper<BCargoRightTransferEntity> {


    /**
     * u��
     */
    @Select("""
            <script>
            SELECT
            	tab1.*,
            	tab3.label as status_name,
            	tab2.detailListData ,
            	tab1.bpm_instance_code as process_code,
            	tab4.name as c_name,
            	tab5.name as u_name
            FROM
            	b_cargo_right_transfer tab1
                LEFT JOIN (select cargo_right_transfer_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'goods_id', goods_id,
                'goods_code', goods_code,
                'goods_name', goods_name,
                'order_qty',order_qty,
                'order_price', order_price,
                'order_amount', order_amount,
                'transfer_qty', transfer_qty,
                'transfer_price', transfer_price,
                'transfer_amount', transfer_amount,
                'quality_status', quality_status,
                'batch_no', batch_no,
                'production_date', production_date,
                'expiry_date', expiry_date )) as detailListData
                 from b_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_po_order tab6 ON tab6.id = tab1.po_order_id
              LEFT JOIN b_po_contract tab7 ON tab7.id = tab1.po_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            	 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            	 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            	 AND (tab1.po_order_id = #{p1.po_order_id}  or #{p1.po_order_id} is null   )
            	 AND (tab1.po_contract_id = #{p1.po_contract_id}  or #{p1.po_contract_id} is null   )

               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='p1.goods_name != null' >
               and exists(
                      select
                        1
                      from
                        b_cargo_right_transfer_detail subt1
                        INNER JOIN b_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>
            GROUP BY
            	tab2.cargo_right_transfer_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = CargoRightTransferDetailListTypeHandler.class),
    })
    IPage<BCargoRightTransferVo> selectPage(Page<BCargoRightTransferVo> page, @Param("p1") BCargoRightTransferVo searchCondition);


    /**
     * id��
     */
    @Select("""
            SELECT
            	tab1.*,
            	tab3.label as status_name,
            	tab2.detailListData ,
            	tab1.bpm_instance_code as process_code,
            	tab4.name as c_name,
            	tab5.name as u_name
            FROM
            	b_cargo_right_transfer tab1
                LEFT JOIN (select cargo_right_transfer_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'goods_id', goods_id,
                'goods_code', goods_code,
                'goods_name', goods_name,
                'order_qty',order_qty,
                'order_price', order_price,
                'order_amount', order_amount,
                'transfer_qty', transfer_qty,
                'transfer_price', transfer_price,
                'transfer_amount', transfer_amount,
                'quality_status', quality_status,
                'batch_no', batch_no,
                'production_date', production_date,
                'expiry_date', expiry_date )) as detailListData
                 from b_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_po_order tab6 ON tab6.id = tab1.po_order_id
              LEFT JOIN b_po_contract tab7 ON tab7.id = tab1.po_contract_id
            	WHERE TRUE AND tab1.id = #{p1}
            	 AND tab1.is_del = false
            GROUP BY
            	tab2.cargo_right_transfer_id
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = CargoRightTransferDetailListTypeHandler.class),
    })
    BCargoRightTransferVo selectId(@Param("p1") Integer id);

    /**
     * ����o
     */
    @Select("""
            <script>
            SELECT
            	SUM( IFNULL(tab2.amount_total,0) )  as  total_amount,
            	SUM( IFNULL(tab2.qty_total,0) )  as  total_qty
            FROM
            	b_cargo_right_transfer tab1
            	LEFT JOIN b_cargo_right_transfer_total tab2  ON tab1.id = tab2.cargo_right_transfer_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            	 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            	 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            	 AND (tab1.po_order_id = #{p1.po_order_id}  or #{p1.po_order_id} is null   )
            	 AND (tab1.po_contract_id = #{p1.po_contract_id}  or #{p1.po_contract_id} is null   )

               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               <if test='p1.goods_name != null' >
               and exists(
                      select
                        1
                      from
                        b_cargo_right_transfer_detail subt1
                        INNER JOIN b_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>

              </script>
            """)
    BCargoRightTransferVo querySum(@Param("p1") BCargoRightTransferVo searchCondition);

    /**
     * !��/&�
     */
    @Select("""
            select * from b_cargo_right_transfer where true and is_del = false
            and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
            and code = #{p1.code}
            """)
    List<BCargoRightTransferVo> validateDuplicateCode(@Param("p1") BCargoRightTransferVo bean);


    /**
     * ����
     */
    @Select("""
            <script>
            SELECT @row_num:= @row_num+ 1 as no,tb1.* from (
               SELECT
            	tab1.*,
            	tab3.label as status_name,
            	tab2.detailListData ,
            	tab1.bpm_instance_code as process_code,
            	tab4.name as c_name,
            	tab5.name as u_name,
            	tab6.code as po_order_code,
            	tab7.contract_code as po_contract_code
            FROM
            	b_cargo_right_transfer tab1
                LEFT JOIN (select cargo_right_transfer_id,JSON_ARRAYAGG(
                JSON_OBJECT( 'sku_code', sku_code,
                'sku_name',sku_name,
                'origin', origin,
                'sku_id', sku_id,
                'unit_id', unit_id,
                'goods_id', goods_id,
                'goods_code', goods_code,
                'goods_name', goods_name,
                'order_qty',order_qty,
                'order_price', order_price,
                'order_amount', order_amount,
                'transfer_qty', transfer_qty,
                'transfer_price', transfer_price,
                'transfer_amount', transfer_amount,
                'quality_status', quality_status,
                'batch_no', batch_no,
                'production_date', production_date,
                'expiry_date', expiry_date )) as detailListData
                 from b_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_po_order tab6 ON tab6.id = tab1.po_order_id
              LEFT JOIN b_po_contract tab7 ON tab7.id = tab1.po_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
               <if test='p1.ids != null and p1.ids.length != 0' >
                and tab1.id in
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            GROUP BY
            	tab2.cargo_right_transfer_id) as tb1,(select @row_num:=0) tb2
            	  </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BCargoRightTransferVo> selectExportList(@Param("p1") BCargoRightTransferVo param);

    @Select("""
            SELECT
            	count(tab1.id)
            FROM
            	b_cargo_right_transfer tab1
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            """)
    Long selectExportCount(@Param("p1") BCargoRightTransferVo param);

    /**
     * 9ncode��'Cl�
     */
    @Select("""
            select * from b_cargo_right_transfer where code = #{code} and is_del = false
            """)
    BCargoRightTransferVo selectByCode(@Param("code") String code);

    /**
     * 9npo_order_code��'Cl�
     */
    @Select("""
            select * from b_cargo_right_transfer where po_order_code = #{po_order_code} and is_del = false
            """)
    BCargoRightTransferVo selectByPoOrderCode(@Param("po_order_code") String po_order_code);

}
