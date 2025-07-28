package com.xinyirun.scm.core.system.mapper.business.po.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.BApReFundPayEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayVo;
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
            tab6.po_contract_code AS po_contract_code,
            tab7.order_amount as refund_order_amount
        FROM
            b_ap_refund_pay tab1
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status'
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

    /**
     * 汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select({
        "<script>",
        "SELECT ",
        "  SUM(IFNULL(tab1.refundable_amount_total, 0)) as sum_refundable_amount_total, ",
        "  SUM(IFNULL(tab1.refunded_amount_total, 0)) as sum_refunded_amount_total, ",
        "  SUM(IFNULL(tab1.refunding_amount_total, 0)) as sum_refunding_amount_total, ",
        "  SUM(IFNULL(tab1.unrefund_amount_total, 0)) as sum_unrefund_amount_total, ",
        "  SUM(IFNULL(tab1.cancelrefund_amount_total, 0)) as sum_cancelrefund_amount_total ",
        "FROM ",
        "  b_ap_refund_pay tab1 ",
        "  LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id ",
        "  LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id ",
        "  LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status' AND tab4.dict_value = tab1.status ",
        "  LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type' AND tab5.dict_value = tab1.type ",
        "  LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id ",
        "  LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id ",
        "  LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id ",
        "WHERE 1=1 ",
        "  <if test='searchCondition.purchaser_id != null'> AND tab1.purchaser_id = #{searchCondition.purchaser_id} </if>",
        "  <if test='searchCondition.supplier_id != null'> AND tab1.supplier_id = #{searchCondition.supplier_id} </if>",
        "  <if test='searchCondition.ap_refund_code != null and searchCondition.ap_refund_code != \"\"'> AND tab1.ap_refund_code like concat('%', #{searchCondition.ap_refund_code}, '%') </if>",
        "  <if test='searchCondition.code != null and searchCondition.code != \"\"'> AND tab1.code like concat('%', #{searchCondition.code}, '%') </if>",
        "  <if test='searchCondition.status != null and searchCondition.status != \"\"'> AND tab1.status = #{searchCondition.status} </if>",
        "  <if test='searchCondition.type != null and searchCondition.type != \"\"'> AND tab1.type = #{searchCondition.type} </if>",
        "  <if test='searchCondition.refund_date != null'> AND DATE(tab1.refund_date) = DATE(#{searchCondition.refund_date}) </if>",
        "  <if test='searchCondition.account_name != null and searchCondition.account_name != \"\"'> AND tab8.name LIKE CONCAT('%',#{searchCondition.account_name},'%') </if>",
        "  <if test='searchCondition.bank_name != null and searchCondition.bank_name != \"\"'> AND tab8.bank_name LIKE CONCAT('%',#{searchCondition.bank_name},'%') </if>",
        "</script>"
    })
    BApReFundPayVo querySum(@Param("searchCondition") BApReFundPayVo searchCondition);

    /**
     * 根据id查询详细信息
     * @param id 退款单支付ID
     * @return 退款单支付详细信息
     */
    @Select("""
        SELECT
            tab1.*,
            0 as not_pay_amount,
            tab2.po_goods,
            tab2.order_amount as source_order_amount,
            tab2.po_contract_id,
            tab2.po_order_id,
            tab2.po_contract_code,
            tab2.po_order_code,
            tab2.advance_refund_amount_total,
            tab2.advance_paid_total,
            tab3.ap_refund_pay_id,
            tab3.ap_refund_pay_code,
            tab3.bank_accounts_id,
            tab3.bank_accounts_code,
            tab3.refundable_amount,
            tab3.refunded_amount,
            tab3.refunding_amount,
            tab3.unrefund_amount,
            tab3.order_amount as detail_order_amount,
            tab9.name,
            tab9.bank_name,
            tab9.account_number,
            GROUP_CONCAT(tab10.NAME) AS bank_type_name,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab11.one_file as doc_att_file,
            tab11.two_file as voucher_file
        FROM
            b_ap_refund_pay tab1
            LEFT JOIN b_ap_refund_pay_source_advance tab2 ON tab1.id = tab2.ap_refund_pay_id
            LEFT JOIN b_ap_refund_pay_detail tab3 ON tab1.id = tab3.ap_refund_pay_id
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_pay_status' AND tab6.dict_value = tab1.status
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type
            LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id
            LEFT JOIN b_ap_refund_pay_attach tab11 ON tab1.id = tab11.ap_refund_pay_id
        WHERE TRUE
            AND tab1.id = #{p1}
        GROUP BY tab1.code, tab3.ap_refund_pay_code
        """)
    BApReFundPayVo selectId(@Param("p1") Integer id);

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select({
        "<script>",
        "SELECT ",
        "  SUM(IFNULL(tab1.refundable_amount_total, 0)) as sum_refundable_amount_total, ",
        "  SUM(IFNULL(tab1.refunded_amount_total, 0)) as sum_refunded_amount_total, ",
        "  SUM(IFNULL(tab1.refunding_amount_total, 0)) as sum_refunding_amount_total, ",
        "  SUM(IFNULL(tab1.unrefund_amount_total, 0)) as sum_unrefund_amount_total, ",
        "  SUM(IFNULL(tab1.cancelrefund_amount_total, 0)) as sum_cancelrefund_amount_total ",
        "FROM ",
        "  b_ap_refund_pay tab1 ",
        "  LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id ",
        "  LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id ",
        "  LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status' AND tab4.dict_value = tab1.status ",
        "  LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type' AND tab5.dict_value = tab1.type ",
        "  LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id ",
        "  LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id ",
        "  LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id ",
        "WHERE true ",
        "  AND tab1.id = #{searchCondition.id} ",
        "</script>"
    })
    BApReFundPayVo queryViewSum(@Param("searchCondition") BApReFundPayVo searchCondition);

    /**
     * 查询退款单金额汇总数据
     * @param ap_refund_id 退款主表id
     * @param status 退款单状态
     * @return 金额汇总VO
     */
    @Select("""
        SELECT t1.id,
          sum(t1.refundable_amount_total) as refundable_amount_total,
          sum(t1.refunded_amount_total) as refunded_amount_total,
          sum(t1.refunding_amount_total) as refunding_amount_total
        FROM b_ap_refund_pay t1
        WHERE t1.ap_refund_id = #{ap_refund_id}
          AND (t1.status = #{status} OR #{status} = '' OR #{status} IS NULL)
        """)
    BApReFundPayVo getSumAmount(@Param("ap_refund_id") Integer ap_refund_id, @Param("status") String status);
}
