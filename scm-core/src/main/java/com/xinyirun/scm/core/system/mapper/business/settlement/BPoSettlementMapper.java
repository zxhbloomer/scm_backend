package com.xinyirun.scm.core.system.mapper.business.settlement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementEntity;
import com.xinyirun.scm.bean.system.vo.business.settlement.BPoSettlementVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 采购结算表 Mapper 接口
 */
@Repository
public interface BPoSettlementMapper extends BaseMapper<BPoSettlementEntity> {

    /**
     * 分页查询
     */
    @Select("	<script>                                                                                                                                         "
            +" SELECT                                                                                                                                     "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as type_name,                                                                                                                    "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab11.process_code as process_code,                                                                                                         "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_po_settlement tab1                                                                                                                        "
            +"	    LEFT JOIN (select po_settlement_id,JSON_ARRAYAGG(                                                                                          "
            +"	    JSON_OBJECT( 'sku_code', sku_code,                                                                                                         "
            +"	    'sku_name',sku_name,                                                                                                                       "
            +"	    'goods_id', goods_id,                                                                                                                      "
            +"	    'goods_code', goods_code,                                                                                                                  "
            +"	    'goods_name', goods_name,                                                                                                                  "
            +"	    'order_qty', order_qty,                                                                                                                    "
            +"	    'order_price', order_price,                                                                                                                "
            +"	    'order_amount', order_amount,                                                                                                              "
            +"	    'settled_qty', settled_qty,                                                                                                                                "
            +"	    'settled_amount', settled_amount,                                                                                                                                "
            +"	    'processing_qty', processing_qty,                                                                                                          "
            +"	    'unprocessed_qty', unprocessed_qty,                                                                                                        "
            +"	    'processed_qty', processed_qty )) as detailListData                                                                                        "
            +"	     from b_po_settlement_detail_source_inbound GROUP BY po_settlement_id) tab2 ON tab1.id = tab2.po_settlement_id                          "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_STATUS + "' AND tab3.dict_value = tab1.status                               "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_TYPE + "' AND tab4.dict_value = tab1.type                                   "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '" + SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_SETTLEMENT + "'                                                  "
            +"        ORDER BY c_time DESC limit 1) as tab11 on tab11.serial_id = tab1.id                                                                       "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                               "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.code = #{p1.code} or #{p1.code} is null or #{p1.code} = '')                                                                      "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                            "

            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab1.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            // 商品查询：goods_name
            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_settlement_detail_source_inbound subt1                                                                                                     "
            + "            INNER JOIN b_po_settlement subt2 ON subt1.po_settlement_id = subt2.id                                                                          "
            + "          where subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%')                                                                                   "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_settlement_id                                                                                                                       "
            +" </script>                                                                                                                                       ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BPoSettlementVo> selectPage(Page<BPoSettlementVo> page, @Param("p1") BPoSettlementVo searchCondition);

    /**
     * id查询
     */
    @Select("	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab2.detailListData,                                                                                                                        "
            +"		tab3.one_file as doc_att_file,                                                                                                             "
            +"		tab6.label as status_name,                                                                                                                  "
            +"		tab7.label as type_name                                                                                                                     "
            +"	FROM                                                                                                                                            "
            +"		b_po_settlement tab1                                                                                                                        "
            +"	    LEFT JOIN (select po_settlement_id,JSON_ARRAYAGG(                                                                                          "
            +"	    JSON_OBJECT( 'sku_code', sku_code,                                                                                                         "
            +"	    'sku_name',sku_name,                                                                                                                        "
            +"	    'goods_id', goods_id,                                                                                                                       "
            +"	    'goods_code', goods_code,                                                                                                                   "
            +"	    'goods_name', goods_name,                                                                                                                   "
            +"	    'order_qty', order_qty,                                                                                                                     "
            +"	    'order_price', order_price,                                                                                                                 "
            +"	    'order_amount', order_amount,                                                                                                               "
            +"	    'settled_qty', settled_qty,                                                                                                                                "
            +"	    'settled_amount', settled_amount,                                                                                                                                "
            +"	    'processing_qty', processing_qty,                                                                                                           "
            +"	    'unprocessed_qty', unprocessed_qty,                                                                                                         "
            +"	    'processed_qty', processed_qty )) as detailListData                                                                                         "
            +"	     from b_po_settlement_detail_source_inbound GROUP BY po_settlement_id) tab2 ON tab1.id = tab2.po_settlement_id                           "
            +"		LEFT JOIN b_po_settlement_attach tab3 on tab1.id = tab3.po_settlement_id                                                                   "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_STATUS + "' AND tab6.dict_value = tab1.status                               "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_TYPE + "' AND tab7.dict_value = tab1.type                                   "
            +"		WHERE TRUE AND tab1.id = #{p1}                                                                                                              "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_settlement_id                                                                                                                       ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BPoSettlementVo selectById(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("	<script>                                                                                                                                           "
            +"	SELECT 	                                                                                                                                          "
            +"		SUM( IFNULL(tab2.processing_qty,0) )  as  processing_qty,                                                                                 "
            +"		SUM( IFNULL(tab2.processing_weight,0) )  as  processing_weight,                                                                           "
            +"		SUM( IFNULL(tab2.processing_volume,0) )  as  processing_volume,                                                                           "
            +"		SUM( IFNULL(tab2.unprocessed_qty,0) )  as  unprocessed_qty,                                                                               "
            +"		SUM( IFNULL(tab2.unprocessed_weight,0) )  as  unprocessed_weight,                                                                         "
            +"		SUM( IFNULL(tab2.unprocessed_volume,0) )  as  unprocessed_volume,                                                                         "
            +"		SUM( IFNULL(tab2.processed_qty,0) )  as  processed_qty,                                                                                   "
            +"		SUM( IFNULL(tab2.processed_weight,0) )  as  processed_weight,                                                                             "
            +"		SUM( IFNULL(tab2.processed_volume,0) )  as  processed_volume                                                                              "
            +"	FROM                                                                                                                                             "
            +"		b_po_settlement tab1                                                                                                                        "
            +"		LEFT JOIN b_po_settlement_total tab2  ON tab1.id = tab2.po_settlement_id                                                                   "
            +"		WHERE TRUE                                                                                                                                   "
            +"		 AND tab1.is_del = false                                                                                                                     "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               "
            +"		 AND (tab1.code = #{p1.code} or #{p1.code} is null or #{p1.code} = '')                                                                      "
            +"		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )                                                               "
            +"		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )                                                            "

            + "   <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              "
            + "    and tab1.status in                                                                                                                                              "
            + "        <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            + "   <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              "
            + "    and tab1.type in                                                                                                                                              "
            + "        <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         "
            + "         #{item}                                                                                                                                                 "
            + "        </foreach>                                                                                                                                               "
            + "   </if>                                                                                                                                                         "

            // 商品查询：goods_name
            + "   <if test='p1.goods_name != null' >                                                                                                  "
            + "   and exists(                                                                                                                                                  "
            + "          select                                                                                                                                            "
            + "            1                                                                                                                                              "
            + "          from                                                                                                                                              "
            + "            b_po_settlement_detail_source_inbound subt1                                                                                                     "
            + "            INNER JOIN b_po_settlement subt2 ON subt1.po_settlement_id = subt2.id                                                                          "
            + "          where subt1.goods_name like CONCAT('%', #{p1.goods_name}, '%')                                                                                   "
            + "            and subt2.id = tab1.id                                                                                                                          "
            + "         )                                                                                                                                                  "
            + "   </if>                                                                                                                                                   "

            +"  </script>                                                                                                                                  "
    )
    BPoSettlementVo querySum(@Param("p1") BPoSettlementVo searchCondition);

    /**
     * 导出查询
     */
    @Select("<script>	                                                                                                                                        "
            +"SELECT @row_num:= @row_num+ 1 as no,tb1.* from (                                                                                                  "
            +"   SELECT                                                                                                                                         "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as type_name,                                                                                                                    "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab11.process_code as process_code,                                                                                                         "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_po_settlement tab1                                                                                                                        "
            +"	    LEFT JOIN (select po_settlement_id,JSON_ARRAYAGG(                                                                                          "
            +"	    JSON_OBJECT( 'sku_code', sku_code,                                                                                                         "
            +"	    'sku_name',sku_name,                                                                                                                       "
            +"	    'goods_id', goods_id,                                                                                                                      "
            +"	    'goods_code', goods_code,                                                                                                                  "
            +"	    'goods_name', goods_name,                                                                                                                  "
            +"	    'order_qty', order_qty,                                                                                                                    "
            +"	    'order_price', order_price,                                                                                                                "
            +"	    'order_amount', order_amount,                                                                                                              "
            +"	    'settled_qty', settled_qty,                                                                                                                                "
            +"	    'settled_amount', settled_amount,                                                                                                                                "
            +"	    'processing_qty', processing_qty,                                                                                                          "
            +"	    'unprocessed_qty', unprocessed_qty,                                                                                                        "
            +"	    'processed_qty', processed_qty )) as detailListData                                                                                        "
            +"	     from b_po_settlement_detail_source_inbound GROUP BY po_settlement_id) tab2 ON tab1.id = tab2.po_settlement_id                          "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_STATUS + "' AND tab3.dict_value = tab1.status                               "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '" + DictConstant.DICT_B_PO_SETTLEMENT_TYPE + "' AND tab4.dict_value = tab1.type                                   "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '" + SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_SETTLEMENT + "'                                                  "
            +"        ORDER BY c_time DESC limit 1) as tab11 on tab11.serial_id = tab1.id                                                                       "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                               "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.code = #{p1.code} or #{p1.code} is null or #{p1.code} = '')                                                                      "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                            "
            + "    and tab1.id in                                                                                                                               "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                 "
            + "         #{item}                                                                                                                                 "
            + "        </foreach>                                                                                                                               "
            + "   </if>                                                                                                                                         "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_settlement_id) as tb1,(select @row_num:=0) tb2                                                                                      "
            +"		  </script>                                                                                                                                 "
    )
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BPoSettlementVo> selectExportList(@Param("p1")BPoSettlementVo param);

    /**
     * 校验结算编号是否重复
     */
    @Select( "select * from b_po_settlement where true and is_del = false                           "
            +" and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)         "
            +" and code = #{p1.code}                                                                 ")
    List<BPoSettlementVo> validateDuplicateCode(@Param("p1")BPoSettlementVo bean);

    /**
     * 根据code查询采购结算
     */
    @Select("select * from b_po_settlement where code = #{code} and is_del = false")
    BPoSettlementVo selectByCode(@Param("code") String code);
} 