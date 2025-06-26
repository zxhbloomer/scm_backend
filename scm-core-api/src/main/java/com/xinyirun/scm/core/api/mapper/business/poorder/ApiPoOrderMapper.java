package com.xinyirun.scm.core.api.mapper.business.poorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
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
 * @since 2025-01-14
 */
@Repository
public interface ApiPoOrderMapper extends BaseMapper<BPoOrderEntity> {

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
            +"		tab12.label as type_name,                                                                                                                   "
            +"		iF(tab11.auto_create_order,'是','否') auto_create_name,                                                                                       "
            +"		tab11.sign_date,                                                                                                                            "
            +"		tab11.expiry_date,                                                                                                                           "
            +"		tab13.name as c_name,                                                                                                                       "
            +"		tab14.name as u_name                                                                                                                        "
            +"	FROM                                                                                                                                            "
            +"		b_po_order tab1                                                                                                                             "
            +"	    LEFT JOIN (select po_order_id,CONCAT( '[',GROUP_CONCAT(                                                                                     "
            +"	    JSON_OBJECT( 'sku_code', sku_code, 'sku_name',sku_name, 'origin', origin,'sku_id', sku_id, 'unit_id', unit_id,                "
            +"	    'qty',qty, 'price', price,'amount', amount, 'tax_amount', tax_amount, 'tax_rate', tax_rate )), ']' ) as detailListData                      "
            +"	     from b_po_order_detail GROUP BY po_order_id) tab2 ON tab1.id = tab2.po_order_id                                                            "
            +"		LEFT JOIN b_po_order_attach tab3 on tab1.id = tab3.po_order_id                                                                              "
            +"		LEFT JOIN m_enterprise tab4 ON tab4.id = tab1.supplier_id                                                                                   "
            +"		LEFT JOIN m_enterprise tab5 ON tab5.id = tab1.purchaser_id                                                                                  "
            +"		LEFT JOIN s_dict_data  tab6 ON tab6.code = '"+ DictConstant.DICT_B_PO_ORDER_STATUS +"' AND tab6.dict_value = tab1.status                    "
            +"		LEFT JOIN s_dict_data  tab7 ON tab7.code = '"+ DictConstant.DICT_B_PO_ORDER_DELIVERY_TYPE +"' AND tab7.dict_value = tab1.delivery_type      "
            +"		LEFT JOIN s_dict_data  tab8 ON tab8.code = '"+ DictConstant.DICT_B_PO_ORDER_SETTLE_TYPE +"' AND tab8.dict_value = tab1.settle_type          "
            +"		LEFT JOIN s_dict_data  tab9 ON tab9.code = '"+ DictConstant.DICT_B_PO_ORDER_BILL_TYPE +"' AND tab9.dict_value = tab1.bill_type              "
            +"		LEFT JOIN s_dict_data  tab10 ON tab10.code = '"+ DictConstant.DICT_B_PO_ORDER_PAYMENT_TYPE +"' AND tab10.dict_value = tab1.payment_type     "
            +"      LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id                                                                             "
            +"		LEFT JOIN s_dict_data tab12 ON tab12.code = '"+ DictConstant.DICT_B_PO_CONTRACT_TYPE +"' AND tab12.dict_value = tab11.type                  "
            +"      LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             "
            +"      LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             "
            +"		WHERE TRUE AND tab1.id = #{p1} AND tab1.is_del = false                                                                                      "
            +"	GROUP BY                                                                                                                                        "
            +"		tab2.po_order_id                                                                                                                            ")
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    PoOrderVo selectId(@Param("p1") Integer id);


    /**
     * 获取采购合同商品信息
     * @param poContractId
     */
    @Select("SELECT * FROM b_po_order_detail WHERE po_order_id = #{p1}")
    List<PoOrderDetailVo> selectGoodsById(@Param("p1")Integer poContractId);
}
