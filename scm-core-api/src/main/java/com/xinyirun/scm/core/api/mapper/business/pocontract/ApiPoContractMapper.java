package com.xinyirun.scm.core.api.mapper.business.pocontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
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
public interface ApiPoContractMapper extends BaseMapper<BPoContractEntity> {

    /**
     * id查询
     */
    @Select("	SELECT                                                                                                                                          "
            + "		tab1.*,                                                                                                                                     "
            + "		tab2.detailListData,                                                                                                                        "
            + "		tab3.four_file as doc_att_file,                                                                                                             "
            + "		tab4.name as supplier_name,                                                                                                                 "
            + "		tab5.name as purchaser_name,                                                                                                                "
            + "		tab6.label as status_name,                                                                                                                  "
            + "		tab7.label as type_name,                                                                                                                    "
            + "		tab8.label as delivery_type_name,                                                                                                           "
            + "		tab9.label as settle_type_name,                                                                                                             "
            + "		tab10.label as bill_type_name,                                                                                                              "
            + "		tab11.label as payment_type_name,                                                                                                           "
            + "		iF(tab1.auto_create_order,'是','否') auto_create_name,                                                                                       "
            + "		tab12.end_time as approve_time                                                                                                              "
            + "	FROM                                                                                                                                            "
            + "		b_po_contract tab1                                                                                                                          "
            + "	    LEFT JOIN (select po_contract_id,CONCAT( '[',GROUP_CONCAT(                                                                                  "
            + "	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            + "	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            + "	     from b_po_contract_detail GROUP BY po_contract_id) tab2 ON tab1.id = tab2.po_contract_id                                                   "
            + "		LEFT JOIN b_po_contract_attach tab3 on tab1.id = tab3.po_contract_id                                                                        "
            + "		LEFT JOIN m_enterprise tab4 ON tab4.id = tab1.supplier_id                                                                                   "
            + "		LEFT JOIN m_enterprise tab5 ON tab5.id = tab1.purchaser_id                                                                                  "
            + "		LEFT JOIN s_dict_data  tab6 ON tab6.code = '" + DictConstant.DICT_B_PO_CONTRACT_STATUS + "' AND tab6.dict_value = tab1.status               "
            + "		LEFT JOIN s_dict_data  tab7 ON tab7.code = '" + DictConstant.DICT_B_PO_CONTRACT_TYPE + "' AND tab7.dict_value = tab1.type                   "
            + "		LEFT JOIN s_dict_data  tab8 ON tab8.code = '" + DictConstant.DICT_B_PO_CONTRACT_DELIVERY_TYPE + "' AND tab8.dict_value = tab1.delivery_type "
            + "		LEFT JOIN s_dict_data  tab9 ON tab9.code = '" + DictConstant.DICT_B_PO_CONTRACT_SETTLE_TYPE + "' AND tab9.dict_value = tab1.settle_type     "
            + "		LEFT JOIN s_dict_data  tab10 ON tab10.code = '" + DictConstant.DICT_B_PO_CONTRACT_BILL_TYPE + "' AND tab10.dict_value = tab1.bill_type      "
            + "		LEFT JOIN s_dict_data  tab11 ON tab11.code = '" + DictConstant.DICT_B_PO_CONTRACT_PAYMENT_TYPE + "' AND tab11.dict_value = tab1.payment_type"
            + "	    LEFT JOIN (SELECT * FROM bpm_instance WHERE serial_type = '"+ SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_CONTRACT+"'               "
            + "        AND status = '"+DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_TWO+"'                                                                    "
            + "        ORDER BY c_time DESC limit 1) as tab12 on tab12.serial_id = tab1.id                                                                      "
            + "		WHERE TRUE AND tab1.id = #{p1}                                                                                                              "
            + "		 AND tab1.is_del = false                                                                                                                    "
            + "	GROUP BY                                                                                                                                        "
            + "		tab2.po_contract_id                                                                                                                         ")    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = PoContractDetailListTypeHandler.class),
    })
    PoContractVo selectId(@Param("p1") Integer id);


    /**
     * 获取采购合同商品信息
     * @param poContractId
     */
    @Select("SELECT * FROM b_po_contract_detail WHERE po_contract_id = #{p1}")
    List<PoContractDetailVo> selectGoodsById(@Param("p1")Integer poContractId);
}
