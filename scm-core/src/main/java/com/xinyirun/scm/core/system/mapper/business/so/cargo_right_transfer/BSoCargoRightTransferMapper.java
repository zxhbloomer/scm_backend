package com.xinyirun.scm.core.system.mapper.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer.BSoCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer.BSoCargoRightTransferVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.SoCargoRightTransferDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 销售货权转移表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Repository
public interface BSoCargoRightTransferMapper extends BaseMapper<BSoCargoRightTransferEntity> {



    /**
     * 分页查询
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
            	b_so_cargo_right_transfer tab1
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
                 from b_so_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_so_order tab6 ON tab6.id = tab1.so_order_id
              LEFT JOIN b_so_contract tab7 ON tab7.id = tab1.so_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            	 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            	 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            	 AND (tab1.so_order_id = #{p1.so_order_id}  or #{p1.so_order_id} is null   )
            	 AND (tab1.so_contract_id = #{p1.so_contract_id}  or #{p1.so_contract_id} is null   )

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
                        b_so_cargo_right_transfer_detail subt1
                        INNER JOIN b_so_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>
            GROUP BY
            	tab2.cargo_right_transfer_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoCargoRightTransferDetailListTypeHandler.class),
    })
    IPage<BSoCargoRightTransferVo> selectPage(Page<BSoCargoRightTransferVo> page, @Param("p1") BSoCargoRightTransferVo searchCondition);


    /**
     * 根据id查询
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
            	b_so_cargo_right_transfer tab1
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
                 from b_so_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_so_order tab6 ON tab6.id = tab1.so_order_id
              LEFT JOIN b_so_contract tab7 ON tab7.id = tab1.so_contract_id
            	WHERE TRUE AND tab1.id = #{p1}
            	 AND tab1.is_del = false
            GROUP BY
            	tab2.cargo_right_transfer_id
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoCargoRightTransferDetailListTypeHandler.class),
    })
    BSoCargoRightTransferVo selectId(@Param("p1") Integer id);

    /**
     * 合计查询
     */
    @Select("""
            <script>
            SELECT
            	SUM( IFNULL(tab2.cargo_right_untransfer_qty_total,0) )  as  cargo_right_untransfer_qty_total,
            	SUM( IFNULL(tab2.cargo_right_transfering_qty_total,0) )  as  cargo_right_transfering_qty_total,
            	SUM( IFNULL(tab2.cargo_right_transferred_qty_total,0) )  as  cargo_right_transferred_qty_total,
            	SUM( IFNULL(tab2.cargo_right_transfer_cancel_qty_total,0) )  as  cargo_right_transfer_cancel_qty_total
            FROM
            	b_so_cargo_right_transfer tab1
            	LEFT JOIN b_so_cargo_right_transfer_total tab2  ON tab1.id = tab2.cargo_right_transfer_id
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_so_order tab6 ON tab6.id = tab1.so_order_id
              LEFT JOIN b_so_contract tab7 ON tab7.id = tab1.so_contract_id
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            	 AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            	 AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            	 AND (tab1.so_order_id = #{p1.so_order_id}  or #{p1.so_order_id} is null   )
            	 AND (tab1.so_contract_id = #{p1.so_contract_id}  or #{p1.so_contract_id} is null   )

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
                        b_so_cargo_right_transfer_detail subt1
                        INNER JOIN b_so_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>

              </script>
            """)
    BSoCargoRightTransferVo querySum(@Param("p1") BSoCargoRightTransferVo searchCondition);

    /**
     * 验证重复编码
     */
    @Select("""
            select * from b_so_cargo_right_transfer where true and is_del = false
            and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
            and code = #{p1.code}
            """)
    List<BSoCargoRightTransferVo> validateDuplicateCode(@Param("p1") BSoCargoRightTransferVo bean);


    /**
     * 导出查询
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
            	tab6.code as so_order_code,
            	tab7.contract_code as so_contract_code
            FROM
            	b_so_cargo_right_transfer tab1
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
                 from b_so_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_so_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_so_order tab6 ON tab6.id = tab1.so_order_id
              LEFT JOIN b_so_contract tab7 ON tab7.id = tab1.so_contract_id
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
    List<BSoCargoRightTransferVo> selectExportList(@Param("p1") BSoCargoRightTransferVo param);

    @Select("""
            SELECT
            	count(tab1.id)
            FROM
            	b_so_cargo_right_transfer tab1
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            """)
    Long selectExportCount(@Param("p1") BSoCargoRightTransferVo param);

    /**
     * 根据code查询货权转移
     */
    @Select("""
            select * from b_so_cargo_right_transfer where code = #{code} and is_del = false
            """)
    BSoCargoRightTransferVo selectByCode(@Param("code") String code);

    /**
     * 根据so_order_code查询货权转移
     */
    @Select("""
            select * from b_so_cargo_right_transfer where so_order_code = #{so_order_code} and is_del = false
            """)
    BSoCargoRightTransferVo selectBySoOrderCode(@Param("so_order_code") String so_order_code);

    /**
     * 根据编号查询货权转移ID
     */
    @Select("""
            select id from b_so_cargo_right_transfer where code = #{code} and is_del = false
            """)
    Integer selectIdByCode(@Param("code") String code);

}