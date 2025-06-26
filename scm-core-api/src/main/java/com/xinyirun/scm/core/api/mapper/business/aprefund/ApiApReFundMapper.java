package com.xinyirun.scm.core.api.mapper.business.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundDetailVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付账款表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface ApiApReFundMapper extends BaseMapper<BApReFundEntity> {

    String pageSql = "SELECT                                                                                                                      "
            +"	tab1.*,                                                                                                                           "
            +"	(tab1.refund_amount - ifnull(tab1.refunded_amount,0) - ifnull(tab1.refunding_amount,0)) as  not_pay_amount,                       "
            +"	tab4.name as c_name,                                                                                                              "
            +"  tab5.name as u_name,                                                                                                              "
            +"	tab6.label as status_name,                                                                                                        "
            +"	tab7.label as type_name,                                                                                                          "
            +"	tab8.label as pay_status_name																	                                  "
            +"FROM                                                                                                                                "
            +"	b_ap_refund tab1                                                                                                                  "
            +"	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                     "
            +"  LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                     "
            +"	LEFT JOIN s_dict_data tab6 ON tab6.code = '"+ DictConstant.DICT_B_AP_STATUS +"' AND tab6.dict_value = tab1.status                 "
            +"	LEFT JOIN s_dict_data tab7 ON tab7.code = '"+ DictConstant.DICT_B_AP_TYPE +"' AND tab7.dict_value = tab1.type                     "
            +"	LEFT JOIN s_dict_data tab8 ON tab8.code = '"+ DictConstant.DICT_B_AP_PAY_STATUS +"' AND tab8.dict_value = tab1.pay_status         "
            +"	WHERE TRUE                                                                                                                        ";

    /**
     * 根据id查询
     */
    @Select(pageSql +" and tab1.id = #{p1}")
    BApReFundVo selById(@Param("p1") Integer id);

    /**
     * 应付退款管理-业务单据信息
     */
    @Select(
             "SELECT                                                                                                           "
            +"	t1.id,                                                                                                         "
            +"	t1.CODE,                                                                                                       "
            +"	t1.ap_refund_id,                                                                                               "
            +"	t1.ap_refund_code,                                                                                             "
            +"	t1.type,                                                                                                       "
            +"	t1.po_contract_code,                                                                                           "
            +"	t1.po_code,                                                                                                    "
            +"	t1.po_goods,                                                                                                   "
            +"	t1.advance_pay_amount,                                                                                         "
            +"	t1.refunded_amount,                                                                                            "
            +"	t1.refund_amount,                                                                                              "
            +"	t1.refunding_amount,                                                                                           "
            +"	t1.remark,                                                                                                     "
            +"	(t1.advance_pay_amount - IFNULL( t3.amount, 0 )) AS can_refunded_amount                                       "
            +"FROM                                                                                                             "
            +"	b_ap_refund_source_advance t1                                                                                  "
            +"	LEFT JOIN b_ap_refund t2 ON t1.ap_refund_id = t2.id                                                            "
            +"	LEFT JOIN ( SELECT po_contract_code, SUM( refund_amount ) AS amount FROM b_ap_refund                           "
            +"	 WHERE STATUS != '"+DictConstant.DICT_B_AP_REFUND_STATUS_FIVE+"' AND is_del = FALSE GROUP BY po_contract_code) "
            +"	 AS t3 ON t1.po_contract_code = t3.po_contract_code                                                            "
            +"WHERE                                                                                                            "
            +"	t1.ap_refund_id = #{p1.ap_refund_id}                                                                           "
            )
    List<BApReFundSourceAdvanceVo> printPoOrder(@Param("p1")BApReFundVo searchCondition);

    /**
     * 应付账款管理-付款信息
     */
    @Select(
            "SELECT t1.id,                                                                   "
             +"       t1.code,                                                                 "
             +"       t1.ap_refund_id,                                                         "
             +"       t1.ap_refund_code,                                                       "
             +"       t1.bank_accounts_id,                                                     "
             +"       t1.bank_accounts_code,                                                   "
             +"       t1.bank_accounts_type_id,                                                "
             +"       t1.bank_accounts_type_code,                                              "
             +"       t1.refund_amount,                                                        "
             +"       t1.refunded_amount,                                                      "
             +"       t1.remark,                                                               "
             +"       t2.name,                                                                 "
             +"       t2.bank_name,                                                            "
             +"       t2.account_number,                                                       "
             +"       t3.name as accounts_purpose_type_name                                    "
             +"FROM b_ap_refund_detail t1                                                      "
             +"         LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id            "
             +"         LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id  "
             +"where t1.ap_refund_id = #{p1.ap_refund_id};                                     "
    )
    List<BApReFundDetailVo> bankAccounts(@Param("p1")BApReFundVo searchCondition);
}
