package com.xinyirun.scm.core.system.mapper.business.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
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
 * 应付账款管理表（Accounts Payable） Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundMapper extends BaseMapper<BApReFundEntity> {

    String pageSql = "SELECT                                                                                                                             "
                    +"	tab1.*,                                                                                                                          "
                    +"	(tab1.refund_amount - ifnull(tab1.refunded_amount,0) - ifnull(tab1.refunding_amount,0)) as  not_pay_amount,                      "
                    +"	tab2.poOrderListData,                                                                                                            "
                    +"	tab3.bankListData,                                                                                                               "
                    +"	tab4.name as c_name,                                                                                                             "
                    +"  tab5.name as u_name,                                                                                                             "
                    +"	tab6.label as status_name,                                                                                                       "
                    +"	tab7.label as type_name,                                                                                                         "
                    +"	tab8.label as pay_status_name																	                                 "
                    +"FROM                                                                                                                               "
                    +"	b_ap_refund tab1                                                                                                                 "
                    +"	LEFT JOIN (SELECT                                                                                                                "
                    +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                              "
                    +"					'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                                "
                    +"					'type',t1.type,'po_contract_code',t1.po_contract_code,                                                           "
                    +"					'po_code',t1.po_code,'po_goods',t1.po_goods,                                                                     "
                    +"					'advance_pay_amount',t1.advance_pay_amount,'refunded_amount',t1.refunded_amount,                                 "
                    +"					'refund_amount',t1.refund_amount,															                     "
                    +"					'refunding_amount',t1.refunding_amount,'remark',t1.remark,                                                       "
                    +"					'can_refunded_amount',t4.paid_amount - IFNULL( t3.amount, 0 ),'refunding_amount',t1.refunding_amount            "
                    +"				)),']' ) AS poOrderListData                                                                                          "
                    +"				FROM b_ap_refund_source_advance t1                                                                                   "
                    +"				LEFT JOIN  b_ap_refund t2 ON t1.ap_refund_id = t2.id                                                                 "
                    +"				LEFT JOIN (SELECT po_contract_code,  SUM(refund_amount)  AS amount FROM b_ap_refund                                  "
                    +"				WHERE STATUS != '"+DictConstant.DICT_B_AP_REFUND_STATUS_FIVE+"' AND is_del = FALSE GROUP BY po_contract_code)        "
                    +"				AS t3 ON t1.po_contract_code = t3.po_contract_code                                                                   "
                    +"				LEFT JOIN b_ap t4 on t1.ap_id = t4.id                                                                                "
                    +"				GROUP BY t1.ap_refund_id ) tab2 ON tab1.id = tab2.ap_refund_id                                                       "
                    +"	LEFT JOIN (SELECT                                                                                                                "
                    +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                              "
                    +"						'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                            "
                    +"						'bank_accounts_id',t1.bank_accounts_id,                                                                      "
                    +"						'bank_accounts_code',t1.bank_accounts_code,                                                                  "
                    +"						'bank_accounts_type_id',t1.bank_accounts_type_id,                                                            "
                    +"						'bank_accounts_type_code',t1.bank_accounts_type_code,                                                        "
                    +"						'refund_amount',t1.refund_amount,'refunded_amount',t1.refunded_amount,                                       "
                    +"						'remark',t1.remark,'name',t2.name,'bank_name',t2.bank_name,                                                  "
                    +"						'account_number',t2.account_number,'accounts_purpose_type_name',t3.name                                      "
                    +"				)),']' ) AS bankListData                                                                                             "
                    +"				FROM b_ap_refund_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id                               "
                    +"				LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id                                                "
                    +"				GROUP BY t1.ap_refund_id) tab3 ON tab1.id = tab3.ap_refund_id                                                        "
                    +"	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                    "
                    +"  LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                    "
                    +"	LEFT JOIN s_dict_data tab6 ON tab6.code = '"+DictConstant.DICT_B_AP_REFUND_STATUS+"' AND tab6.dict_value = tab1.status           "
                    +"	LEFT JOIN s_dict_data tab7 ON tab7.code = '"+DictConstant.DICT_B_AP_REFUND_TYPE+"' AND tab7.dict_value = tab1.type               "
                    +"	LEFT JOIN s_dict_data tab8 ON tab8.code = '"+DictConstant.DICT_B_AP_REFUND_PAY_STATUS +"' AND tab8.dict_value = tab1.pay_status  "
                    +"	WHERE TRUE																														 ";

    /**
     * 业务类型查询
     */
    @Select(" select dict_value as dict_id ,label as dict_label from s_dict_data where code = '"+DictConstant.DICT_B_AP_REFUND_TYPE+"' and is_del = false ")
    List<BApReFundVo> getType();

    /**
     * 分页查询
     */
    @Select(pageSql + " "
    + "  AND tab1.is_del = false                                                                                                                                                                                                    "
    + "  AND (tab1.code = #{p1.code} or #{p1.code} is null or  #{p1.code} = '')                                                                                                                                                     "
    + "  AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                                             "
    + "  AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                                                     "
    + "  AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')                                                                                                                             "
    + "  AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                                                "
    + "  AND (tab1.po_code like concat('%', #{p1.po_code}, '%') or #{p1.po_code} is null or  #{p1.po_code} = '')                                                                                                                    "
    + "  AND (tab1.buyer_enterprise_name like concat('%', #{p1.buyer_enterprise_name}, '%') or #{p1.buyer_enterprise_name} is null or  #{p1.buyer_enterprise_name} = '')                                                            "
    + "  AND (tab1.supplier_enterprise_name like concat('%', #{p1.supplier_enterprise_name}, '%') or #{p1.supplier_enterprise_name} is null or  #{p1.supplier_enterprise_name} = '')                                                "
    )
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BApReFundVo> selectPage(Page page, @Param("p1") BApReFundVo searchCondition);


    /**
     * 根据id查询
     */
    @Select(pageSql +" and tab1.id = #{p1}")
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    BApReFundVo selectId(@Param("p1") Integer id);

    @Select(
            "   <script>	 SELECT                                                                                                                                                                              "
            +"	@row_num:= @row_num+ 1 as no,                                                                                                                                                                    "
            +"	tab1.*,                                                                                                                                                                       "
            +"	(tab1.refund_amount - ifnull(tab1.refunded_amount,0) - ifnull(tab1.refunding_amount,0)) as  not_pay_amount,                                                                                      "
            +"	tab2.poOrderListData,                                                                                                                                                                            "
            +"	tab3.bankListData,                                                                                                                                                                               "
            +"	tab4.name as c_name,                                                                                                                                                                             "
            +"  tab5.name as u_name,                                                                                                                                                                             "
            +"	tab6.label as status_name,                                                                                                                                                                       "
            +"	tab7.label as type_name,                                                                                                                                                                         "
            +"	tab8.label as pay_status_name																	                                                                                                 "
            +"FROM                                                                                                                                                                                               "
            +"	b_ap_refund tab1                                                                                                                                                                                 "
            +"	LEFT JOIN (SELECT                                                                                                                                                                                "
            +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                                                                                              "
            +"					'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                                                                                                "
            +"					'type',t1.type,'po_contract_code',t1.po_contract_code,                                                                                                                           "
            +"					'po_code',t1.po_code,'po_goods',t1.po_goods,                                                                                                                                     "
            +"					'advance_pay_amount',t1.advance_pay_amount,'refunded_amount',t1.refunded_amount,                                                                                                 "
            +"					'refund_amount',t1.refund_amount,															                                                                                     "
            +"					'refunding_amount',t1.refunding_amount,'remark',t1.remark,                                                                                                                       "
            +"					'can_refunded_amount',(t1.advance_pay_amount - IFNULL(t3.amount,0)),'refunding_amount',t1.refunding_amount                                                                       "
            +"				)),']' ) AS poOrderListData                                                                                                                                                          "
            +"				FROM b_ap_refund_source_advance t1                                                                                                                                                   "
            +"				LEFT JOIN  b_ap_refund t2 ON t1.ap_refund_id = t2.id                                                                                                                                 "
            +"				LEFT JOIN (SELECT po_contract_code,SUM(refund_amount) AS amount FROM b_ap_refund                                                                                                     "
            +"				WHERE STATUS != '"+DictConstant.DICT_B_AP_REFUND_STATUS_FIVE+"' AND is_del = FALSE GROUP BY po_contract_code)                                                                        "
            +"				AS t3 ON t1.po_contract_code = t3.po_contract_code                                                                                                                                   "
            +"				GROUP BY t1.ap_refund_id ) tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       "
            +"	LEFT JOIN (SELECT                                                                                                                                                                                "
            +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                                                                                              "
            +"						'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                                                                                            "
            +"						'bank_accounts_id',t1.bank_accounts_id,                                                                                                                                      "
            +"						'bank_accounts_code',t1.bank_accounts_code,                                                                                                                                  "
            +"						'bank_accounts_type_id',t1.bank_accounts_type_id,                                                                                                                            "
            +"						'bank_accounts_type_code',t1.bank_accounts_type_code,                                                                                                                        "
            +"						'refund_amount',t1.refund_amount,'refunded_amount',t1.refunded_amount,                                                                                                       "
            +"						'remark',t1.remark,'name',t2.name,'bank_name',t2.bank_name,                                                                                                                  "
            +"						'account_number',t2.account_number,'accounts_purpose_type_name',t3.name                                                                                                      "
            +"				)),']' ) AS bankListData                                                                                                                                                             "
            +"				FROM b_ap_refund_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id                                                                                               "
            +"				LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id                                                                                                                "
            +"				GROUP BY t1.ap_refund_id) tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                        "
            +"	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                                                                                    "
            +"  LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                                                                                    "
            +"	LEFT JOIN s_dict_data tab6 ON tab6.code = '"+DictConstant.DICT_B_AP_REFUND_STATUS+"' AND tab6.dict_value = tab1.status                                                                           "
            +"	LEFT JOIN s_dict_data tab7 ON tab7.code = '"+DictConstant.DICT_B_AP_REFUND_TYPE+"' AND tab7.dict_value = tab1.type                                                                               "
            +"	LEFT JOIN s_dict_data tab8 ON tab8.code = '"+DictConstant.DICT_B_AP_REFUND_PAY_STATUS +"' AND tab8.dict_value = tab1.pay_status,                                                                 "
            +"	(select @row_num:=0) tb9                                                                                                                                                                         "
            +"	WHERE TRUE                                                                                                                                                                                       "
            +"  AND tab1.is_del = false                                                                                                                                                                          "
            +"  AND (tab1.code = #{p1.code} or #{p1.code} is null or  #{p1.code} = '')                                                                                                                           "
            +"  AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                   "
            +"  AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                           "
            +"  AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')                                                                                                   "
            +"  AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                      "
            +"  AND (tab1.po_code like concat('%', #{p1.po_code}, '%') or #{p1.po_code} is null or  #{p1.po_code} = '')                                                                                          "
            +"  AND (tab1.buyer_enterprise_name like concat('%', #{p1.buyer_enterprise_name}, '%') or #{p1.buyer_enterprise_name} is null or  #{p1.buyer_enterprise_name} = '')                                  "
            +"  AND (tab1.supplier_enterprise_name like concat('%', #{p1.supplier_enterprise_name}, '%') or #{p1.supplier_enterprise_name} is null or  #{p1.supplier_enterprise_name} = '')                      "
            +"   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                                                                              "
            +"    and tab1.id in                                                                                                                                                                                 "
            +"        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                                                                   "
            +"         #{item}                                                                                                                                                                                   "
            +"        </foreach>                                                                                                                                                                                 "
            +"   </if>                                                                                                                                                                                           "
            +"		  </script>                                                                                                                                                                                  "
    )
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BApReFundVo> selectExportList(@Param("p1") BApReFundVo param);

    @Select("   <script>	 SELECT                                                                                                                                                                              "
           +"	count(tab1.id)																	                                                                                                 "
           +"FROM                                                                                                                                                                                               "
            +"	b_ap_refund tab1                                                                                                                                                                                 "
            +"	LEFT JOIN (SELECT                                                                                                                                                                                "
            +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                                                                                              "
            +"					'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                                                                                                "
            +"					'type',t1.type,'po_contract_code',t1.po_contract_code,                                                                                                                           "
            +"					'po_code',t1.po_code,'po_goods',t1.po_goods,                                                                                                                                     "
            +"					'advance_pay_amount',t1.advance_pay_amount,'refunded_amount',t1.refunded_amount,                                                                                                 "
            +"					'refund_amount',t1.refund_amount,															                                                                                     "
            +"					'refunding_amount',t1.refunding_amount,'remark',t1.remark,                                                                                                                       "
            +"					'can_refunded_amount',(t1.advance_pay_amount - IFNULL(t3.amount,0)),'refunding_amount',t1.refunding_amount                                                                       "
            +"				)),']' ) AS poOrderListData                                                                                                                                                          "
            +"				FROM b_ap_refund_source_advance t1                                                                                                                                                   "
            +"				LEFT JOIN  b_ap_refund t2 ON t1.ap_refund_id = t2.id                                                                                                                                 "
            +"				LEFT JOIN (SELECT po_contract_code,  SUM(refund_amount)  AS amount FROM b_ap_refund                                                                                                  "
            +"				WHERE STATUS != '"+DictConstant.DICT_B_AP_REFUND_STATUS_FIVE+"' AND is_del = FALSE GROUP BY po_contract_code)                                                                        "
            +"				AS t3 ON t1.po_contract_code = t3.po_contract_code                                                                                                                                   "
            +"				GROUP BY t1.ap_refund_id ) tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       "
            +"	LEFT JOIN (SELECT                                                                                                                                                                                "
            +"		t1.ap_refund_id,CONCAT('[',GROUP_CONCAT(JSON_OBJECT('id',t1.id,                                                                                                                              "
            +"						'code',t1.code,'ap_refund_id',t1.ap_refund_id,'ap_refund_code',t1.ap_refund_code,                                                                                            "
            +"						'bank_accounts_id',t1.bank_accounts_id,                                                                                                                                      "
            +"						'bank_accounts_code',t1.bank_accounts_code,                                                                                                                                  "
            +"						'bank_accounts_type_id',t1.bank_accounts_type_id,                                                                                                                            "
            +"						'bank_accounts_type_code',t1.bank_accounts_type_code,                                                                                                                        "
            +"						'refund_amount',t1.refund_amount,'refunded_amount',t1.refunded_amount,                                                                                                       "
            +"						'remark',t1.remark,'name',t2.name,'bank_name',t2.bank_name,                                                                                                                  "
            +"						'account_number',t2.account_number,'accounts_purpose_type_name',t3.name                                                                                                      "
            +"				)),']' ) AS bankListData                                                                                                                                                             "
            +"				FROM b_ap_refund_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id                                                                                               "
            +"				LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id                                                                                                                "
            +"				GROUP BY t1.ap_refund_id) tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                        "
            +"	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                                                                                    "
            +"  LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                                                                                    "
            +"	LEFT JOIN s_dict_data tab6 ON tab6.code = '"+DictConstant.DICT_B_AP_REFUND_STATUS+"' AND tab6.dict_value = tab1.status                                                                           "
            +"	LEFT JOIN s_dict_data tab7 ON tab7.code = '"+DictConstant.DICT_B_AP_REFUND_TYPE+"' AND tab7.dict_value = tab1.type                                                                               "
            +"	LEFT JOIN s_dict_data tab8 ON tab8.code = '"+DictConstant.DICT_B_AP_REFUND_PAY_STATUS +"' AND tab8.dict_value = tab1.pay_status,                                                                 "
           +"	(select @row_num:=0) tb9                                                                                                                                                                         "
           +"	WHERE TRUE                                                                                                                                                                                       "
           +"  AND tab1.is_del = false                                                                                                                                                                           "
           +"  AND (tab1.code = #{p1.code} or #{p1.code} is null or  #{p1.code} = '')                                                                                                                            "
           +"  AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                    "
           +"  AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                            "
           +"  AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')                                                                                                    "
           +"  AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                       "
           +"  AND (tab1.po_code like concat('%', #{p1.po_code}, '%') or #{p1.po_code} is null or  #{p1.po_code} = '')                                                                                           "
           +"  AND (tab1.buyer_enterprise_name like concat('%', #{p1.buyer_enterprise_name}, '%') or #{p1.buyer_enterprise_name} is null or  #{p1.buyer_enterprise_name} = '')                                   "
           +"  AND (tab1.supplier_enterprise_name like concat('%', #{p1.supplier_enterprise_name}, '%') or #{p1.supplier_enterprise_name} is null or  #{p1.supplier_enterprise_name} = '')                       "
           +"   <if test='p1.ids != null and p1.ids.length != 0' >                                                                                                                                               "
           +"    and tab1.id in                                                                                                                                                                                  "
           +"        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                                                                    "
           +"         #{item}                                                                                                                                                                                    "
           +"        </foreach>                                                                                                                                                                                  "
           +"   </if>                                                                                                                                                                                            "
           +"		  </script>                                                                                                                                                                                  "
    )
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    Long selectExportCount(@Param("p1") BApReFundVo param);

    /**
     * 查询采购订单下 付款账单
     */
    @Select("select * from b_ap tab1 left join b_po_order tab2 on tab1.po_code = tab2.code where tab2.id = #{p1} and tab1.is_del = false")
    List<BApReFundVo> selectByPoCode(@Param("p1")String code);

    /**
     * 查询采购订单下 付款账单
     */
    @Select("select * from b_ap tab1 left join b_po_order tab2 on tab1.po_code = tab2.code where tab2.id = #{p1} and tab1.status != #{p2} and tab1.is_del = false")
    List<BApReFundVo> selByPoCodeNotByStatus(@Param("p1")Integer code,@Param("p2") String dictBApStatusFive);
}
