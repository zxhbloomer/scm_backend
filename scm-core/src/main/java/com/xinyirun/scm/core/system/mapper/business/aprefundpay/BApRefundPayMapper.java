package com.xinyirun.scm.core.system.mapper.business.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 退款单表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApRefundPayMapper extends BaseMapper<BApReFundPayEntity> {


    /**
     * 列表查询
     */
    @Select("""
        SELECT
            tab1.*,
            tab2.NAME AS c_name,
            tab3.NAME AS u_name,
            tab4.label AS status_name,
            tab5.label AS type_name,
            tab6.po_contract_code AS po_contract_code
        FROM
            b_ap_refund_pay tab1
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_one_status'
            AND tab4.dict_value = tab1.status
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type'
            AND tab5.dict_value = tab1.type
            LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id
            LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
            WHERE TRUE
            AND (tab8.name LIKE CONCAT('%',#{p1.account_name},'%') OR #{p1.account_name} IS NULL OR  #{p1.account_name} = '' )
            AND (tab8.bank_name LIKE CONCAT('%',#{p1.bank_name},'%') OR #{p1.bank_name} IS NULL OR  #{p1.bank_name} = '' )
        """)
    IPage<BApReFundPayVo> selectPage(Page page, @Param("p1") BApReFundPayVo searchCondition);

    @Select("""
        SELECT
          tab1.id,
        	tab1.CODE,
        	tab1.ap_refund_id,
        	tab1.ap_refund_code,
        	tab1.STATUS,
        	tab1.type,
        	tab1.supplier_enterprise_bank_name,
        	tab1.supplier_enterprise_code,
        	tab1.supplier_enterprise_version,
        	tab1.supplier_enterprise_name,
        	IFNULL(
        		tab1.buyer_enterprise_bank_name,
        	CONCAT_WS( ' | ', tab8.holder_name, tab8.bank_name, tab8.account_number )) AS buyer_enterprise_bank_name,
        	tab1.buyer_enterprise_code,
        	tab8.id as bank_account_id,
        	tab8.code as bank_account_code,
        	tab7.bank_accounts_type_id,
        	tab7.bank_accounts_type_code,
        	tab6.po_contract_code as trade_no,
        	tab9.id as buyer_enterprise_id,
        	tab1.buyer_enterprise_version,
        	tab1.buyer_enterprise_name,
        	tab1.refund_date,
        	tab1.refund_method,
        	tab10.label as refund_method_name,
        	tab1.refund_amount,
        	tab1.remark,
        	tab1.voucher_remark,
        	tab1.c_id,
        	tab1.c_time,
        	tab1.u_id,
        	tab1.u_time,
        	tab1.dbversion,
        	tab2.NAME AS c_name,
        	tab3.NAME AS u_name,
        	tab4.label AS status_name,
        	tab5.label AS type_name,
        	tab6.po_contract_code AS po_contract_code,
        	tab11.voucher_files AS voucher_file
        FROM
        	b_ap_refund_pay tab1
        	LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
        	LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
        	LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_one_status'
        	AND tab4.dict_value = tab1.status
        	LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type'
        	AND tab5.dict_value = tab1.type
        	LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id
        	LEFT JOIN b_ap_refund_detail tab7 ON tab6.id = tab7.ap_refund_id
        	LEFT JOIN m_bank_accounts tab8 ON tab7.bank_accounts_id = tab8.id
        	LEFT JOIN m_enterprise tab9 ON tab1.buyer_enterprise_code = tab9.code
        	LEFT JOIN s_dict_data tab10 ON tab10.CODE = 'b_po_order_payment_type'
        	AND tab10.dict_value = tab1.refund_method
        	LEFT JOIN b_ap_refund_pay_attach tab11 ON tab11.ap_refund_id = tab1.id
        	where tab1.id = #{p1}
        """)
    BApReFundPayVo selById(@Param("p1") Integer id);

    /**
     * 查询ap_refund_id的付款单状态等于status的付款单
     */
    @Select("select * from b_ap_refund_pay where ap_refund_id = #{p1} and status = #{p2}  ")
    List<BApReFundPayEntity> selectApPayByStatus(@Param("p1")  Integer apId, @Param("p2") String status);


    /**
     * 查询ap_refund_id的付款单状态不等于status的付款单
     */
    @Select("select * from b_ap_refund_pay where ap_refund_id = #{p1} and status != #{p2}  ")
    List<BApReFundPayVo> selectApPayByNotStatus(@Param("p1")  Integer apId, @Param("p2") String status);
}
