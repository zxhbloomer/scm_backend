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
public interface BApReFundPayMapper extends BaseMapper<BApReFundPayEntity> {


    /**
     * 列表查询
     */
    @Select("""
        -- 退款单列表查询，包含人员、状态、类型等关联信息
        SELECT
            -- tab1.*: 退款单支付表所有字段
            tab1.*,
            -- c_name: 创建人姓名
            tab2.NAME AS c_name,
            -- u_name: 修改人姓名
            tab3.NAME AS u_name,
            -- status_name: 退款单状态显示名称（0-待付款、1已付款、2-作废）
            tab4.label AS status_name,
            -- type_name: 退款类型显示名称（1-应付退款、2-预付退款、3-其他支出退款）
            tab5.label AS type_name,
            -- po_contract_code: 采购合同编号
            tab6.po_contract_code AS po_contract_code,
            -- refund_order_amount: 本次退款金额（来自退款明细表）
            tab7.order_amount as refund_order_amount
        FROM
            -- 主表：退款单支付表
            b_ap_refund_pay tab1
            -- 关联员工表：获取创建人姓名
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            -- 关联员工表：获取修改人姓名
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            -- 关联字典表：获取退款单支付状态显示名称
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status'
            AND tab4.dict_value = tab1.status
            -- 关联字典表：获取退款类型显示名称
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type'
            AND tab5.dict_value = tab1.type
            -- 关联退款主表：获取采购合同编号
            LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id
            -- 关联退款明细表：获取退款金额信息
            LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id
            -- 关联银行账户表：用于查询条件过滤
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
            WHERE TRUE
            -- p1.account_name: 银行账户名称参数模糊查询
            AND (tab8.name LIKE CONCAT('%',#{p1.account_name},'%') OR #{p1.account_name} IS NULL OR  #{p1.account_name} = '' )
            -- p1.bank_name: 银行名称参数模糊查询
            AND (tab8.bank_name LIKE CONCAT('%',#{p1.bank_name},'%') OR #{p1.bank_name} IS NULL OR  #{p1.bank_name} = '' )
        """)
    IPage<BApReFundPayVo> selectPage(Page page, @Param("p1") BApReFundPayVo searchCondition);

    /**
     * 查询ap_refund_id的付款单状态等于status的付款单
     */
    @Select("""
            -- 根据退款主ID和状态查询退款单支付记录
            SELECT * FROM b_ap_refund_pay 
            -- p1: 应付退款主表ID参数
            WHERE ap_refund_id = #{p1} 
            -- p2: 退款单状态参数（0-待付款、1-已付款、2-作废）
            AND status = #{p2}
            """)
    List<BApReFundPayEntity> selectApPayByStatus(@Param("p1")  Integer apId, @Param("p2") String status);


    /**
     * 查询ap_refund_id的付款单状态不等于status的付款单
     */
    @Select("""
            -- 根据退款主ID查询非指定状态的退款单支付记录
            SELECT * FROM b_ap_refund_pay 
            -- p1: 应付退款主表ID参数
            WHERE ap_refund_id = #{p1} 
            -- p2: 需要排除的退款单状态参数
            AND status != #{p2}
            """)
    List<BApReFundPayVo> selectApPayByNotStatus(@Param("p1")  Integer apId, @Param("p2") String status);

    /**
     * 汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select({
        "<script>",
        "-- 退款单支付金额汇总查询，统计各种退款状态的金额合计",
        "SELECT ",
        "  -- sum_refundable_amount_total: 退款金额汇总，使用IFNULL处理空值",
        "  SUM(IFNULL(tab1.refundable_amount_total, 0)) as sum_refundable_amount_total, ",
        "  -- sum_refunded_amount_total: 已退款金额汇总",
        "  SUM(IFNULL(tab1.refunded_amount_total, 0)) as sum_refunded_amount_total, ",
        "  -- sum_refunding_amount_total: 退款中金额汇总",
        "  SUM(IFNULL(tab1.refunding_amount_total, 0)) as sum_refunding_amount_total, ",
        "  -- sum_unrefund_amount_total: 未退款金额汇总",
        "  SUM(IFNULL(tab1.unrefund_amount_total, 0)) as sum_unrefund_amount_total, ",
        "  -- sum_cancelrefund_amount_total: 退款取消金额汇总",
        "  SUM(IFNULL(tab1.cancelrefund_amount_total, 0)) as sum_cancelrefund_amount_total ",
        "FROM ",
        "  -- 主表：退款单支付表",
        "  b_ap_refund_pay tab1 ",
        "  -- 关联员工表：获取创建人信息",
        "  LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id ",
        "  -- 关联员工表：获取修改人信息",
        "  LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id ",
        "  -- 关联字典表：获取退款单支付状态显示名称",
        "  LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status' AND tab4.dict_value = tab1.status ",
        "  -- 关联字典表：获取退款类型显示名称",
        "  LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type' AND tab5.dict_value = tab1.type ",
        "  -- 关联退款主表：用于查询条件关联",
        "  LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id ",
        "  -- 关联退款明细表：用于查询条件关联",
        "  LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id ",
        "  -- 关联银行账户表：用于账户名称和银行名称查询条件",
        "  LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id ",
        "WHERE 1=1 ",
        "  -- MyBatis动态SQL查询条件：支持多种筛选条件",
        "  -- searchCondition.purchaser_id: 购买方ID精确匹配",
        "  <if test='searchCondition.purchaser_id != null'> AND tab1.purchaser_id = #{searchCondition.purchaser_id} </if>",
        "  -- searchCondition.supplier_id: 供应商ID精确匹配",
        "  <if test='searchCondition.supplier_id != null'> AND tab1.supplier_id = #{searchCondition.supplier_id} </if>",
        "  -- searchCondition.ap_refund_code: 应付退款编号模糊查询",
        "  <if test='searchCondition.ap_refund_code != null and searchCondition.ap_refund_code != \"\"'> AND tab1.ap_refund_code like concat('%', #{searchCondition.ap_refund_code}, '%') </if>",
        "  -- searchCondition.code: 退款单编号模糊查询",
        "  <if test='searchCondition.code != null and searchCondition.code != \"\"'> AND tab1.code like concat('%', #{searchCondition.code}, '%') </if>",
        "  -- searchCondition.status: 退款单状态精确匹配",
        "  <if test='searchCondition.status != null and searchCondition.status != \"\"'> AND tab1.status = #{searchCondition.status} </if>",
        "  -- searchCondition.type: 退款类型精确匹配",
        "  <if test='searchCondition.type != null and searchCondition.type != \"\"'> AND tab1.type = #{searchCondition.type} </if>",
        "  -- searchCondition.refund_date: 退款日期精确匹配（只比较日期部分）",
        "  <if test='searchCondition.refund_date != null'> AND DATE(tab1.refund_date) = DATE(#{searchCondition.refund_date}) </if>",
        "  -- searchCondition.account_name: 银行账户名称模糊查询",
        "  <if test='searchCondition.account_name != null and searchCondition.account_name != \"\"'> AND tab8.name LIKE CONCAT('%',#{searchCondition.account_name},'%') </if>",
        "  -- searchCondition.bank_name: 银行名称模糊查询",
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
        -- 根据ID查询退款单支付详细信息，包含所有关联数据和附件信息
        SELECT
            -- tab1.*: 退款单支付表所有字段
            tab1.*,
            -- not_pay_amount: 固定为0，保持接口兼容性
            0 as not_pay_amount,
            -- 来源预付表的相关信息
            tab2.po_goods,
            tab2.order_amount as source_order_amount,
            tab2.po_contract_id,
            tab2.po_order_id,
            tab2.po_contract_code,
            tab2.po_order_code,
            -- advance_refund_amount_total: 可退金额
            tab2.advance_refund_amount_total,
            -- advance_paid_total: 预付款已付金额
            tab2.advance_paid_total,
            -- 退款单支付明细信息
            tab3.ap_refund_pay_id,
            tab3.ap_refund_pay_code,
            tab3.bank_accounts_id,
            tab3.bank_accounts_code,
            -- 明细表的退款金额字段
            tab3.refundable_amount,
            tab3.refunded_amount,
            tab3.refunding_amount,
            tab3.unrefund_amount,
            -- detail_order_amount: 本次退款金额（明细表）
            tab3.order_amount as detail_order_amount,
            -- 银行账户信息
            tab9.name,
            tab9.bank_name,
            tab9.account_number,
            -- bank_type_name: 银行账户类型名称（GROUP_CONCAT聚合）
            GROUP_CONCAT(tab10.NAME) AS bank_type_name,
            -- 人员信息：创建人和修改人姓名
            tab4.name as c_name,
            tab5.name as u_name,
            -- 字典翻译：状态和类型的显示名称
            tab6.label as status_name,
            tab7.label as type_name,
            -- 附件信息
            tab11.one_file as doc_att_file,
            tab11.two_file as voucher_file
        FROM
            -- 主表：退款单支付表
            b_ap_refund_pay tab1
            -- 关联退款单支付来源预付表：获取来源信息
            LEFT JOIN b_ap_refund_pay_source_advance tab2 ON tab1.id = tab2.ap_refund_pay_id
            -- 关联退款单支付明细表：获取支付明细信息
            LEFT JOIN b_ap_refund_pay_detail tab3 ON tab1.id = tab3.ap_refund_pay_id
            -- 关联员工表：获取创建人姓名
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            -- 关联员工表：获取修改人姓名
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            -- 关联字典表：获取退款单支付状态显示名称
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_pay_status' AND tab6.dict_value = tab1.status
            -- 关联字典表：获取退款类型显示名称
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type
            -- 关联银行账户表：获取银行账户详细信息
            LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            -- 关联银行账户类型表：获取账户用途类型
            LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id
            -- 关联退款单支付附件表：获取附件文件信息
            LEFT JOIN b_ap_refund_pay_attach tab11 ON tab1.id = tab11.ap_refund_pay_id
        WHERE TRUE
            -- p1: 退款单支付主表ID参数精确匹配
            AND tab1.id = #{p1}
        -- GROUP BY: 按退款单编号和支付明细编号分组，避免重复数据
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
        "-- 单条退款单支付金额汇总查询，根据ID精确查询单个记录的金额信息",
        "SELECT ",
        "  -- 单条记录的各种退款金额汇总，使用IFNULL处理空值",
        "  SUM(IFNULL(tab1.refundable_amount_total, 0)) as sum_refundable_amount_total, ",
        "  SUM(IFNULL(tab1.refunded_amount_total, 0)) as sum_refunded_amount_total, ",
        "  SUM(IFNULL(tab1.refunding_amount_total, 0)) as sum_refunding_amount_total, ",
        "  SUM(IFNULL(tab1.unrefund_amount_total, 0)) as sum_unrefund_amount_total, ",
        "  SUM(IFNULL(tab1.cancelrefund_amount_total, 0)) as sum_cancelrefund_amount_total ",
        "FROM ",
        "  -- 主表：退款单支付表",
        "  b_ap_refund_pay tab1 ",
        "  -- 以下关联为保持和其他查询的一致性，但在单条查询中实际不会使用到",
        "  LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id ",
        "  LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id ",
        "  LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_refund_pay_status' AND tab4.dict_value = tab1.status ",
        "  LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_refund_type' AND tab5.dict_value = tab1.type ",
        "  LEFT JOIN b_ap_refund tab6 ON tab6.id = tab1.ap_refund_id ",
        "  LEFT JOIN b_ap_refund_detail tab7 ON tab7.ap_refund_id = tab1.ap_refund_id ",
        "  LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id ",
        "WHERE true ",
        "  -- searchCondition.id: 退款单支付主表ID精确匹配",
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
        -- 根据退款主ID和状态查询退款单支付金额汇总信息
        SELECT 
            -- t1.id: 返回第一条记录的ID作为标识
            t1.id,
            -- refundable_amount_total: 退款金额汇总
            sum(t1.refundable_amount_total) as refundable_amount_total,
            -- refunded_amount_total: 已退款金额汇总
            sum(t1.refunded_amount_total) as refunded_amount_total,
            -- refunding_amount_total: 退款中金额汇总
            sum(t1.refunding_amount_total) as refunding_amount_total
        FROM b_ap_refund_pay t1
        WHERE 
            -- ap_refund_id: 应付退款主表ID参数精确匹配
            t1.ap_refund_id = #{ap_refund_id}
            -- status: 退款单状态参数过滤，支持空值和空字符串
            AND (t1.status = #{status} OR #{status} = '' OR #{status} IS NULL)
        """)
    BApReFundPayVo getSumAmount(@Param("ap_refund_id") Integer ap_refund_id, @Param("status") String status);
}
