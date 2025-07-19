package com.xinyirun.scm.core.system.mapper.business.so.socontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.SoContractVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.SoContractDetailListTypeHandler;
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
public interface BSoContractMapper extends BaseMapper<BSoContractEntity> {


    /**
     * 分页查询
     */
    @Select("	SELECT                                                                                                                                          "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as type_name,                                                                                                                    "
            +"		tab5.label as delivery_type_name,                                                                                                           "
            +"		tab6.label as settle_type_name,                                                                                                             "
            +"		tab7.label as bill_type_name,                                                                                                               "
            +"		tab8.label as payment_type_name,                                                                                                            "
            +"		iF(tab1.auto_create_order,'是','否') auto_create_name,                                                                                       "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab9.name as supplier_name,                                                                                                                 "
            +"		tab10.name as purchaser_name,                                                                                                               "
            +"		tab11.process_code as process_code,                                                                                                         "
            +"		iF(tab12.id,false,true) existence_order,                                                                                                    "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_so_contract tab1                                                                                                                          "
            +"	    LEFT JOIN (select so_contract_id,CONCAT( '[',GROUP_CONCAT(                                                                                  "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id                                                   "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_SO_CONTRACT_STATUS +"' AND tab3.dict_value = tab1.status                 "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE +"' AND tab4.dict_value = tab1.type                     "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_SO_CONTRACT_DELIVERY_TYPE +"' AND tab5.dict_value = tab1.delivery_type   "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_CONTRACT_SETTLE_TYPE +"' AND tab6.dict_value = tab1.settle_type       "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_CONTRACT_BILL_TYPE +"' AND tab7.dict_value = tab1.bill_type           "
            +"		LEFT JOIN s_dict_data  tab8 ON tab8.code = '"+ DictConstant.DICT_B_SO_CONTRACT_PAYMENT_TYPE +"' AND tab8.dict_value = tab1.payment_type     "
            +"		LEFT JOIN m_enterprise tab9 ON tab9.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab10 ON tab10.id = tab1.purchaser_id                                                                                "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '" + SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT +"'             "
            +"         ORDER BY c_time DESC limit 1) as tab11   on tab11.serial_id = tab1.id                                                                    "
            +"      LEFT JOIN b_so_order tab12 on tab12.so_contract_id = tab1.id                                                                                "
            +"         and tab12.is_del = false and tab1.type = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE_ZERO +"'                                               "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')                                  "            +"	GROUP BY                                                                                                                                        "
            +"		tab2.so_contract_id                                                                                                                         ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoContractDetailListTypeHandler.class),
    })
    IPage<SoContractVo> selectPage(Page<SoContractVo> page, @Param("p1") SoContractVo searchCondition);


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
            +"		tab7.label as type_name,                                                                                                                    "
            +"		tab8.label as delivery_type_name,                                                                                                           "
            +"		tab9.label as settle_type_name,                                                                                                             "
            +"		tab10.label as bill_type_name,                                                                                                              "
            +"		tab11.label as payment_type_name,                                                                                                           "
            +"		iF(tab1.auto_create_order,'是','否') auto_create_name                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_so_contract tab1                                                                                                                          "
            +"	    LEFT JOIN (select so_contract_id,CONCAT( '[',GROUP_CONCAT(                                                                                  "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id                                                   "
            +"		LEFT JOIN b_so_contract_attach tab3 on tab1.id = tab3.so_contract_id                                                                        "
            +"		LEFT JOIN m_enterprise tab4 ON tab4.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab5 ON tab5.id = tab1.purchaser_id                                                                                  "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_CONTRACT_STATUS +"' AND tab6.dict_value = tab1.status                 "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE +"' AND tab7.dict_value = tab1.type                     "
            +"		LEFT JOIN s_dict_data  tab8 ON tab8.code = '"+ DictConstant.DICT_B_SO_CONTRACT_DELIVERY_TYPE +"' AND tab8.dict_value = tab1.delivery_type   "
            +"		LEFT JOIN s_dict_data  tab9 ON tab9.code = '"+ DictConstant.DICT_B_SO_CONTRACT_SETTLE_TYPE +"' AND tab9.dict_value = tab1.settle_type       "
            +"		LEFT JOIN s_dict_data  tab10 ON tab10.code = '"+ DictConstant.DICT_B_SO_CONTRACT_BILL_TYPE +"' AND tab10.dict_value = tab1.bill_type        "
            +"		LEFT JOIN s_dict_data  tab11 ON tab11.code = '"+ DictConstant.DICT_B_SO_CONTRACT_PAYMENT_TYPE +"' AND tab11.dict_value = tab1.payment_type  "
            +"		WHERE TRUE AND tab1.id = #{p1}                                                                                                              "            +"		 AND tab1.is_del = false                                                                                                                    "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.so_contract_id                                                                                                                         ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoContractDetailListTypeHandler.class),
    })
    SoContractVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("	SELECT                                                                                                                                           "
            +"		IF(SUM( tab2.qty * tab2.price ) is not null ,SUM( tab2.qty * tab2.price ),0)  as  contract_amount_total                                             "
            +"	FROM                                                                                                                                             "
            +"		b_so_contract tab1                                                                                                                           "
            +"		LEFT JOIN b_so_contract_detail tab2                                                                                                          "
            +"		ON tab1.id = tab2.so_contract_id                                                                                                             "
            +"		WHERE TRUE                                                                                                                                   "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               "
            +"		 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')                                   "
    )
    SoContractVo querySum(@Param("p1") SoContractVo searchCondition);

    /**
     * 校验合同编号是否重复
     */
    @Select( "select * from b_so_contract where true and is_del = false                           "
            +" and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)         "
            +" and contract_code = #{p1.contract_code}                                            ")
    List<SoContractVo> validateDuplicateContractCode(@Param("p1")SoContractVo bean);


    /**
     * 导出限制开关查询
     */
    @Select("SELECT                                                                                                                                    "
            +"	count(tab1.id)                                                                                                                                  "
            +"	FROM                                                                                                                                            "
            +"		b_so_contract tab1                                                                                                                          "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')                                  ")
    Long selectExportCount(@Param("p1")SoContractVo param);

    /**
     * 导出查询
     */
    @Select("<script>	                                                                                                                                  "
            +"SELECT @row_num:= @row_num+ 1 as no,tb1.* from (                                                                                                                  "
            +"   SELECT                                                                                                                  "
            +"		tab1.*,                                                                                                                                     "
            +"		tab3.label as status_name,                                                                                                                  "
            +"		tab4.label as type_name,                                                                                                                    "
            +"		tab5.label as delivery_type_name,                                                                                                           "
            +"		tab6.label as settle_type_name,                                                                                                             "
            +"		tab7.label as bill_type_name,                                                                                                               "
            +"		tab8.label as payment_type_name,                                                                                                            "
            +"		iF(tab1.auto_create_order,'是','否') auto_create_name,                                                                                       "
            +"		tab2.detailListData ,                                                                                                                       "
            +"		tab9.name as supplier_name,                                                                                                                 "
            +"		tab10.name as purchaser_name,                                                                                                               "
            +"		tab11.process_code as process_code,                                                                                                         "
            +"		iF(tab12.id,false,true) existence_order,                                                                                                    "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_so_contract tab1                                                                                                                          "
            +"	    LEFT JOIN (select so_contract_id,CONCAT( '[',GROUP_CONCAT(                                                                                  "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'spec', spec, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_so_contract_detail GROUP BY so_contract_id) tab2 ON tab1.id = tab2.so_contract_id                                                   "
            +"		LEFT JOIN s_dict_data  tab3 ON tab3.code = '"+ DictConstant.DICT_B_SO_CONTRACT_STATUS +"' AND tab3.dict_value = tab1.status                 "
            +"		LEFT JOIN s_dict_data  tab4 ON tab4.code = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE +"' AND tab4.dict_value = tab1.type                     "
            +"		LEFT JOIN s_dict_data  tab5 ON tab5.code = '"+ DictConstant.DICT_B_SO_CONTRACT_DELIVERY_TYPE +"' AND tab5.dict_value = tab1.delivery_type   "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_SO_CONTRACT_SETTLE_TYPE +"' AND tab6.dict_value = tab1.settle_type       "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_SO_CONTRACT_BILL_TYPE +"' AND tab7.dict_value = tab1.bill_type           "
            +"		LEFT JOIN s_dict_data  tab8 ON tab8.code = '"+ DictConstant.DICT_B_SO_CONTRACT_PAYMENT_TYPE +"' AND tab8.dict_value = tab1.payment_type     "
            +"		LEFT JOIN m_enterprise tab9 ON tab9.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab10 ON tab10.id = tab1.purchaser_id                                                                                "
            +"	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_CONTRACT+"'                "
            +"        ORDER BY c_time DESC limit 1) as tab11 on tab11.serial_id = tab1.id                                                                       "
            +"      LEFT JOIN b_so_order tab12 on tab12.so_contract_id = tab1.id                                                                                "
            +"         and tab12.is_del = false and tab1.type = '"+ DictConstant.DICT_B_SO_CONTRACT_TYPE_ZERO +"'                                               "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                               "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                               "
            +"		WHERE TRUE                                                                                                                                  "
            +"		 AND tab1.is_del = false                                                                                                                    "
            +"		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              "
            +"		 AND (tab1.contract_code = #{p1.contract_code} or #{p1.contract_code} is null or #{p1.contract_code} = '')                                  "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                            "
            + "    and tab1.id in                                                                                                                               "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                 "
            + "         #{item}                                                                                                                                 "
            + "        </foreach>                                                                                                                               "
            + "   </if>                                                                                                                                         "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.so_contract_id) as tb1,(select @row_num:=0) tb2                                                                                        "            +"		  </script>                                                                                                                                 "
    )
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = SoContractDetailListTypeHandler.class),
    })
    List<SoContractVo> selectExportList(@Param("p1")SoContractVo param);
}
