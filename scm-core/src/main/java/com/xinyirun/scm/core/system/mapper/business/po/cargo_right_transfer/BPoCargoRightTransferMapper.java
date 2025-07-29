package com.xinyirun.scm.core.system.mapper.business.po.cargo_right_transfer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.cargo_right_transfer.BPoCargoRightTransferEntity;
import com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer.BPoCargoRightTransferVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PoCargoRightTransferDetailListTypeHandler;
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
public interface BPoCargoRightTransferMapper extends BaseMapper<BPoCargoRightTransferEntity> {


    /**
     * u��
     */
    @Select("""
            <script>
            -- 货权转移单分页查询，包含明细信息、状态翻译、人员信息等
            SELECT
            	-- tab1.*: 货权转移主表所有字段
            	tab1.*,
            	-- status_name: 状态显示名称(0-待审批,1-审批中,2-执行中,3-驳回,4-作废审批中,5-已作废,6-已完成)
            	tab3.label as status_name,
            	-- detailListData: 货权转移明细JSON数组，包含商品、数量、价格等信息
            	tab2.detailListData ,
            	-- process_code: BPM流程实例编码
            	tab1.bpm_instance_code as process_code,
            	-- c_name: 创建人姓名
            	tab4.name as c_name,
            	-- u_name: 修改人姓名
            	tab5.name as u_name
            FROM
            	-- 主表：货权转移表
            	b_po_cargo_right_transfer tab1
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
                 from b_po_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_po_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_po_order tab6 ON tab6.id = tab1.po_order_id
              LEFT JOIN b_po_contract tab7 ON tab7.id = tab1.po_contract_id
            	WHERE TRUE
            	 -- is_del = false: 查询未删除的记录
            	 AND tab1.is_del = false
            	 -- #{p1.status}: 货权转移状态精确匹配或空值
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 -- #{p1.code}: 货权转移单号模糊查询
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            	 -- #{p1.supplier_id}: 供应商ID精确匹配或空值
            	 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            	 -- #{p1.purchaser_id}: 采购员ID精确匹配或空值
            	 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            	 -- #{p1.po_order_id}: 采购订单ID精确匹配或空值
            	 AND (tab1.po_order_id = #{p1.po_order_id}  or #{p1.po_order_id} is null   )
            	 -- #{p1.po_contract_id}: 采购合同ID精确匹配或空值
            	 AND (tab1.po_contract_id = #{p1.po_contract_id}  or #{p1.po_contract_id} is null   )

               -- 状态列表过滤：支持多状态查询
               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>

               -- 商品名称模糊查询：在明细表中查找匹配的商品或SKU名称
               <if test='p1.goods_name != null' >
               and exists(
                      select
                        1
                      from
                        b_po_cargo_right_transfer_detail subt1
                        INNER JOIN b_po_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      -- #{p1.goods_name}: 在SKU名称或商品名称中模糊查询
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>
            -- 按货权转移ID分组，避免明细表JOIN产生重复数据
            GROUP BY
            	tab2.cargo_right_transfer_id
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoCargoRightTransferDetailListTypeHandler.class),
    })
    IPage<BPoCargoRightTransferVo> selectPage(Page<BPoCargoRightTransferVo> page, @Param("p1") BPoCargoRightTransferVo searchCondition);


    /**
     * 根据ID查询货权转移详细信息
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
            	b_po_cargo_right_transfer tab1
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
                 from b_po_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_po_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
              LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
              LEFT JOIN b_po_order tab6 ON tab6.id = tab1.po_order_id
              LEFT JOIN b_po_contract tab7 ON tab7.id = tab1.po_contract_id
            	WHERE TRUE 
            	-- #{p1}: 货权转移主表ID精确匹配
            	AND tab1.id = #{p1}
            	-- is_del = false: 查询未删除的记录
            	 AND tab1.is_del = false
            GROUP BY
            	tab2.cargo_right_transfer_id
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoCargoRightTransferDetailListTypeHandler.class),
    })
    BPoCargoRightTransferVo selectId(@Param("p1") Integer id);

    /**
     * ����o
     */
    @Select("""
            <script>
            -- 货权转移数量汇总统计，按不同转移状态统计总数量
            SELECT
            	-- cargo_right_untransfer_qty_total: 未转移数量汇总
            	SUM( IFNULL(tab2.cargo_right_untransfer_qty_total,0) )  as  cargo_right_untransfer_qty_total,
            	-- cargo_right_transfering_qty_total: 转移中数量汇总
            	SUM( IFNULL(tab2.cargo_right_transfering_qty_total,0) )  as  cargo_right_transfering_qty_total,
            	-- cargo_right_transferred_qty_total: 已转移数量汇总
            	SUM( IFNULL(tab2.cargo_right_transferred_qty_total,0) )  as  cargo_right_transferred_qty_total,
            	-- cargo_right_transfer_cancel_qty_total: 取消转移数量汇总
            	SUM( IFNULL(tab2.cargo_right_transfer_cancel_qty_total,0) )  as  cargo_right_transfer_cancel_qty_total
            FROM
            	b_po_cargo_right_transfer tab1
            	LEFT JOIN b_po_cargo_right_transfer_total tab2  ON tab1.id = tab2.cargo_right_transfer_id
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
                        b_po_cargo_right_transfer_detail subt1
                        INNER JOIN b_po_cargo_right_transfer subt2 ON subt1.cargo_right_transfer_id = subt2.id
                      where (subt1.sku_name like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%'))
                        and subt2.id = tab1.id
                     )
               </if>

              </script>
            """)
    BPoCargoRightTransferVo querySum(@Param("p1") BPoCargoRightTransferVo searchCondition);

    /**
     * !��/&�
     */
    @Select("""
            -- 验证货权转移单号的唯一性，排除当前记录ID
            select * from b_po_cargo_right_transfer where true 
            -- is_del = false: 只检查未删除的记录
            and is_del = false
            -- #{p1.id}: 排除当前记录ID（新增时为null，修改时为具体ID）
            and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
            -- #{p1.code}: 货权转移单号精确匹配
            and code = #{p1.code}
            """)
    List<BPoCargoRightTransferVo> validateDuplicateCode(@Param("p1") BPoCargoRightTransferVo bean);


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
            	b_po_cargo_right_transfer tab1
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
                 from b_po_cargo_right_transfer_detail GROUP BY cargo_right_transfer_id) tab2 ON tab1.id = tab2.cargo_right_transfer_id
            	LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_po_cargo_right_transfer_status' AND tab3.dict_value = tab1.status
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
    List<BPoCargoRightTransferVo> selectExportList(@Param("p1") BPoCargoRightTransferVo param);

    @Select("""
            SELECT
            	count(tab1.id)
            FROM
            	b_po_cargo_right_transfer tab1
            	WHERE TRUE
            	 AND tab1.is_del = false
            	 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            	 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            """)
    Long selectExportCount(@Param("p1") BPoCargoRightTransferVo param);

    /**
     * 根据编号查询货权转移
     */
    @Select("""
            -- 根据货权转移单号查询货权转移信息
            SELECT * FROM b_po_cargo_right_transfer 
            -- #{code}: 货权转移单号精确匹配
            WHERE code = #{code} 
            -- is_del = false: 查询未删除的记录
            AND is_del = false
            """)
    BPoCargoRightTransferVo selectByCode(@Param("code") String code);

    /**
     * 根据采购订单编号查询货权转移
     */
    @Select("""
            -- 根据采购订单编号查询货权转移信息
            SELECT * FROM b_po_cargo_right_transfer 
            -- #{po_order_code}: 采购订单编号精确匹配
            WHERE po_order_code = #{po_order_code} 
            -- is_del = false: 查询未删除的记录
            AND is_del = false
            """)
    BPoCargoRightTransferVo selectByPoOrderCode(@Param("po_order_code") String po_order_code);

    /**
     * 根据编号查询货权转移ID
     */
    @Select("""
            -- 根据货权转移单号查询对应的主ID
            SELECT id FROM b_po_cargo_right_transfer 
            -- #{code}: 货权转移单号精确匹配
            WHERE code = #{code} 
            -- is_del = false: 查询未删除的记录
            AND is_del = false
            """)
    Integer selectIdByCode(@Param("code") String code);

}
