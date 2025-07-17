package com.xinyirun.scm.core.api.mapper.business.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.system.vo.business.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
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
public interface ApiApMapper extends BaseMapper<BApEntity> {

    String pageSql = "SELECT                                                                                                                      "
            +"	tab1.*,                                                                                                                           "
            +"	(tab1.payable_amount - ifnull(tab1.paid_amount,0) - ifnull(tab1.paying_amount,0)) as  not_pay_amount,                             "
            +"	tab4.name as c_name,                                                                                                              "
            +"  tab5.name as u_name,                                                                                                              "
            +"	tab6.label as status_name,                                                                                                        "
            +"	tab7.label as type_name,                                                                                                          "
            +"	tab8.label as pay_status_name																	                                  "
            +"FROM                                                                                                                                "
            +"	b_ap tab1                                                                                                                         "
            +"	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                     "
            +" LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                      "
            +"	LEFT JOIN s_dict_data tab6 ON tab6.code = '"+ DictConstant.DICT_B_AP_STATUS +"' AND tab6.dict_value = tab1.status                 "
            +"	LEFT JOIN s_dict_data tab7 ON tab7.code = '"+ DictConstant.DICT_B_AP_TYPE +"' AND tab7.dict_value = tab1.type                     "
            +"	LEFT JOIN s_dict_data tab8 ON tab8.code = '"+ DictConstant.DICT_B_AP_PAY_STATUS +"' AND tab8.dict_value = tab1.pay_status         "
            +"	WHERE TRUE                                                                                                                        ";

    /**
     * 根据id查询
     */
    @Select(pageSql +" and tab1.id = #{p1}")
    BApVo selById(@Param("p1") Integer id);

    /**
     * 应付账款管理-业务单据信息
     */
    @Select( "	SELECT                                                                                                                "
            +"		t1.id,t1.code,t1.ap_id,t1.ap_code,t1.type,t1.po_contract_code,                                                    "
            +"		t1.po_goods,t1.qty_total,t1.amount_total,t1.order_amount,t1.remark,                                      "
            +"		(t2.paying_amount + t2.paid_amount) as po_advance_payment_amount,                                                 "
            +"		(t1.amount_total - ifnull(t3.amount,0)) as po_can_advance_payment_amount                                          "
            +"		FROM b_ap_source_advance t1                                                                                       "
            +"		LEFT JOIN  b_ap t2 ON t1.ap_id = t2.id                                                                            "
            +"		LEFT JOIN (SELECT po_contract_code, SUM(payable_amount) AS amount FROM b_ap                                       "
            +"		WHERE STATUS != '"+DictConstant.DICT_B_AP_STATUS_FIVE+"' AND is_del = FALSE GROUP BY po_contract_code)            "
            +"		AS t3 ON t1.po_contract_code = t3.po_contract_code                                                                "
            +"		where ap_id = #{p1.ap_id}                                                                                         ")
    List<BApSourceAdvanceVo> printPoOrder(@Param("p1")BApVo searchCondition);

    /**
     * 应付账款管理-付款信息
     */
    @Select(
             "SELECT t1.id,                                                                            "
            +"       t1.code,                                                                 "
            +"       t1.ap_id,                                                                "
            +"       t1.ap_code,                                                              "
            +"       t1.bank_accounts_id,                                                     "
            +"       t1.bank_accounts_code,                                                   "
            +"       t1.bank_accounts_type_id,                                                "
            +"       t1.bank_accounts_type_code,                                              "
            +"       t1.payable_amount,                                                       "
            +"       t1.paid_amount,                                                          "
            +"       t1.remark,                                                               "
            +"       t2.name,                                                                 "
            +"       t2.bank_name,                                                            "
            +"       t2.account_number,                                                       "
            +"       t3.name as accounts_purpose_type_name                                    "
            +"FROM b_ap_detail t1                                                             "
            +"         LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id            "
            +"         LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id  "
            +"where t1.ap_id = #{p1.ap_id};                                                   "
    )
    List<BApDetailVo> bankAccounts(@Param("p1")BApVo searchCondition);
}
