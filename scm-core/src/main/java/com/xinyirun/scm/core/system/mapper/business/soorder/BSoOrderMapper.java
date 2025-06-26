package com.xinyirun.scm.core.system.mapper.business.soorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.SoOrderDetailListTypeHandler;
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
public interface BSoOrderMapper extends BaseMapper<BSoOrderEntity> {

    /**
     * 分页查询
     */
    @Select("	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab8.name as supplier_name,                                                                                                                 "
            +"		tab9.name as purchaser_name,                                                                                                                "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_so_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select so_order_id,CONCAT( '[',GROUP_CONCAT(                                                                                     "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_order_detail GROUP BY so_order_id) tab2 ON tab1.id = tab2.so_order_id                                                            "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_SO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_SO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_SO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"		LEFT JOIN m_enterprise tab8 ON tab8.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab9 ON tab9.id = tab1.purchaser_id                                                                                  "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.so_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                   "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')                      "            +"	GROUP BY                                                                                                                                        "
            +"		tab2.so_order_id                                                                                                                            ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    IPage<SoOrderVo> selectPage(Page<SoOrderVo> page, @Param("p1") SoOrderVo searchCondition);



    /**
     * id查询
     */
    @Select("	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab2.detailListData,                                                                                                                        "
            +"		tab3.four_file as doc_att_file,                                                                                                             "
            +"		tab4.name as supplier_name,                                                                                                                 "
            +"		tab5.name as purchaser_name,                                                                                                                "
            +"		tab6.label as status_name,                                                                                                                  "
            +"		tab7.label as delivery_type_name,                                                                                                           "
            +"		tab8.label as settle_type_name,                                                                                                             "
            +"		tab9.label as bill_type_name,                                                                                                               "
            +"		tab10.label as payment_type_name,                                                                                                           "
            +"		iF(tab11.auto_create_order,'是','否') auto_create_name,                                                                                      "
            +"		tab11.sign_date,                                                                                                                            "
            +"		tab11.expiry_date                                                                                                                           "
            +"	FROM                                                                                                                                            "
            +"		b_so_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select so_order_id,CONCAT( '[',GROUP_CONCAT(                                                                                     "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_order_detail GROUP BY so_order_id) tab2 ON tab1.id = tab2.so_order_id                                                            "
            +"		LEFT JOIN b_so_order_attach tab3 on tab1.id = tab3.so_order_id                                                                              "
            +"		LEFT JOIN m_enterprise tab4 ON tab4.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab5 ON tab5.id = tab1.purchaser_id                                                                                  "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_ORDER_STATUS +"' AND tab6.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_ORDER_DELIVERY_TYPE +"' AND tab7.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab8 ON tab8.code = '"+ DictConstant.DICT_B_SO_ORDER_SETTLE_TYPE +"' AND tab8.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab9 ON tab9.code = '"+ DictConstant.DICT_B_SO_ORDER_BILL_TYPE +"' AND tab9.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab10 ON tab10.code = '"+ DictConstant.DICT_B_SO_ORDER_PAYMENT_TYPE +"' AND tab10.dict_value = tab1.payment_type     "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.so_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                  "            +"		WHERE TRUE AND tab1.id = #{p1} AND tab1.is_del = false                                                                                      "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.so_order_id                                                                                                                            ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    SoOrderVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("	SELECT                                                                                                                                           "
            +"		IF(SUM( tab2.qty * tab2.price ) is not null,SUM( tab2.qty * tab2.price ),0)  as  order_amount_total                                          "
            +"	FROM                                                                                                                                             "
            +"		b_so_order tab1                                                                                                                              "
            +"		LEFT JOIN b_so_order_detail tab2                                                                                                             "
            +"		ON tab1.id = tab2.so_order_id                                                                                                                "
            +"		WHERE TRUE                                                                                                                                   "
            +"		 AND tab1.is_del = false                                                                                                                     "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               "
            +"		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')                      "

    )
    SoOrderVo querySum( @Param("p1") SoOrderVo searchCondition);

    /**
     * 标准合同下推校验 只能下推一个订单
     */
    @Select( "SELECT * FROM b_so_order tab1 LEFT JOIN b_so_contract tab2 ON tab1.so_contract_id = tab2.id                                           "
            +" WHERE tab1.is_del = FALSE AND tab2.type = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE_ZERO +"' AND tab2.id = #{p1.so_contract_id}       ")
    List<SoOrderVo> validateDuplicateContractId(@Param("p1")SoOrderVo searchCondition);

    @Select("SELECT                                                                                                                                             "
            +"	count(tab1.id)                                                                                                                                  "
            +"	FROM                                                                                                                                            "
            +"		b_so_order tab1                                                                                                                             "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')                      ")
    Long selectExportCount(@Param("p1")SoOrderVo param);

    /**
     * 分页查询
     */
    @Select("<script>	                                                                                                                                        "
            +"SELECT @row_num:= @row_num+ 1 as no,tb1.* from (                                                                                                  "
            +"	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as delivery_type_name,                                                                                                           "
            +"		tab5.label as settle_type_name,                                                                                                             "
            +"		tab6.label as bill_type_name,                                                                                                               "
            +"		tab7.label as payment_type_name,                                                                                                            "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab8.name as supplier_name,                                                                                                                 "
            +"		tab9.name as purchaser_name,                                                                                                                "
            +"		tab10.process_code as process_code,                                                                                                         "
            +"		tab12.label as type_name,                                                                                                                   "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_so_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select so_order_id,CONCAT( '[',GROUP_CONCAT(                                                                                     "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_order_detail GROUP BY so_order_id) tab2 ON tab1.id = tab2.so_order_id                                                            "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_SO_ORDER_STATUS +"' AND tab3.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_SO_ORDER_DELIVERY_TYPE +"' AND tab4.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_SO_ORDER_SETTLE_TYPE +"' AND tab5.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_ORDER_BILL_TYPE +"' AND tab6.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_ORDER_PAYMENT_TYPE +"' AND tab7.dict_value = tab1.payment_type        "
            +"		LEFT JOIN m_enterprise tab8 ON tab8.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab9 ON tab9.id = tab1.purchaser_id                                                                                  "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_ORDER+"'                  "
            +"        ORDER BY c_time DESC limit 1) as tab10 on tab10.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.so_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                   "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.so_contract_code = #{p1.so_contract_code} or #{p1.so_contract_code} is null or #{p1.so_contract_code} = '')                      "
            +"   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                             "
            +"    and tab1.id in                                                                                                                                "
            +"        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                  "
            +"         #{item}                                                                                                                                  "
            +"        </foreach>                                                                                                                                "
            +"   </if>                                                                                                                                          "
            +"	GROUP BY                                                                                                                                        "            +"		tab2.so_order_id) as tb1,(select @row_num:=0) tb2                                                                                           "
            +"		  </script>                                                                                                                                 "
           )
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoOrderDetailListTypeHandler.class),
    })
    List<SoOrderVo> selectExportList(@Param("p1")SoOrderVo param);

    /**
     * 根据销售合同id,状态 查询销售订单
     */
    @Select("select * from b_so_order where so_contract_id = #{p1} and status != #{p2} and is_del = false")
    List<SoOrderVo> selectBySoContractIdNotByStatus(@Param("p1") Integer id, @Param("p2") String status);


    /**
     * 根据销售合同id 查询销售订单
     */
    @Select("select * from b_so_order where so_contract_id = #{p1} and is_del = false")
    List<SoOrderVo> selectBySoContractId(@Param("p1")Integer id);
}
