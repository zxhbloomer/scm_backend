package com.xinyirun.scm.core.system.mapper.business.po.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPayEntity;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 付款单表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApPayMapper extends BaseMapper<BApPayEntity> {


    /**
     * 列表查询（分页）
     */
    @Select("""
        <script>
        -- 分页查询付款单信息，包含字典翻译、关联应付账款信息
        SELECT
            tab1.*,
            -- 创建人和修改人姓名
            tab2.NAME AS c_name,
            tab3.NAME AS u_name,
            -- 字典翻译字段
            tab4.label AS status_name,
            tab5.label AS type_name,
            -- 关联的采购订单信息
            tab6.po_order_id AS po_order_id,
            tab6.po_order_code AS po_order_code,
            tab6.po_contract_code AS po_contract_code
        FROM
            b_ap_pay tab1
            -- 关联员工表，获取创建人信息
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            -- 关联员工表，获取修改人信息
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            -- 关联付款单状态字典：0-待付款、1-已付款、2-作废、-1-中止付款
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_pay_status' AND tab4.dict_value = tab1.status
            -- 关联付款类型字典：1-应付、2-预付、3-其他支出
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_type' AND tab5.dict_value = tab1.type
            -- 关联应付账款主表，获取采购订单信息
            LEFT JOIN b_ap tab6 ON tab6.id = tab1.ap_id
            -- 关联应付账款明细表，获取银行账户信息
            LEFT JOIN b_ap_detail tab7 ON tab7.ap_id = tab1.ap_id
            -- 关联银行账户表，获取银行账户详细信息
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE 1=1
          -- #{p1.purchaser_id}: 购买方ID
          <if test='p1.purchaser_id != null'> AND tab1.purchaser_id = #{p1.purchaser_id} </if>
          -- #{p1.supplier_id}: 供应商ID
          <if test='p1.supplier_id != null'> AND tab1.supplier_id = #{p1.supplier_id} </if>
          -- #{p1.ap_code}: 应付账款主表编号，支持模糊查询
          <if test='p1.ap_code != null and p1.ap_code != ""'> AND tab1.ap_code like concat('%', #{p1.ap_code}, '%') </if>
          -- #{p1.code}: 付款单编号，支持模糊查询
          <if test='p1.code != null and p1.code != ""'> AND tab1.code like concat('%', #{p1.code}, '%') </if>
          <if test='p1.status_list != null and p1.status_list.length != 0'>
            -- 付款单状态列表查询：支持多个状态值查询
            AND tab1.status in
            <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
              #{item}
            </foreach>
          </if>
        </script>
        """)
    IPage<BApPayVo> selectPage(Page<BApPayVo> page, @Param("p1") BApPayVo searchCondition);

    /**
     * 获取单条数据
     */
    @Select("""
        -- 根据ID查询单条付款单详细信息，包含字典翻译和关联信息
        SELECT
            tab1.*,
            -- 创建人和修改人姓名
            tab2.NAME AS c_name,
            tab3.NAME AS u_name,
            -- 字典翻译字段
            tab4.label AS status_name,
            tab5.label AS type_name,
            -- 关联的采购订单信息
            tab6.po_order_code AS po_order_code,
            tab6.po_order_id AS po_order_id,
            tab6.po_contract_code AS po_contract_code
        FROM
            b_ap_pay tab1
            -- 关联员工表，获取创建人信息
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            -- 关联员工表，获取修改人信息
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            -- 关联付款单状态字典：0-待付款、1-已付款、2-作废、-1-中止付款
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_pay_status' AND tab4.dict_value = tab1.status
            -- 关联付款类型字典：1-应付、2-预付、3-其他支出
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_type' AND tab5.dict_value = tab1.type
            -- 关联应付账款主表，获取采购订单信息
            LEFT JOIN b_ap tab6 ON tab6.id = tab1.ap_id
            -- 关联应付账款明细表，获取银行账户信息
            LEFT JOIN b_ap_detail tab7 On tab7.ap_id = tab1.ap_id
            -- 关联银行账户表，获取银行账户详细信息
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        -- #{p1}: 付款单主表ID
        WHERE tab1.id = #{p1}
        """)
    BApPayVo selById(@Param("p1") Integer id);

    /**
     * 查询ap_id的付款单状态等于status的付款单
     */
    @Select("""
        -- 根据应付账款ID和状态查询付款单
        select * from b_ap_pay 
        -- #{p1}: 应付账款主表ID
        where ap_id = #{p1} 
        -- #{p2}: 付款单状态（0-待付款、1-已付款、2-作废、-1-中止付款）
        and status = #{p2}
        """)
    List<BApPayVo> selectApPayByStatus(@Param("p1")  Integer apId, @Param("p2") String status);


    /**
     * 查询ap_id的付款单状态不等于status的付款单
     */
    @Select("""
        -- 根据应付账款ID查询付款单，排除指定状态
        select * from b_ap_pay 
        -- #{p1}: 应付账款主表ID
        where ap_id = #{p1} 
        -- #{p2}: 需要排除的付款单状态（通常为'2'-作废）
        and status != #{p2}
        """)
    List<BApPayVo> selectApPayByNotStatus(@Param("p1")  Integer apId, @Param("p2") String status);

    /**
     * 汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select("""
        <script>
        SELECT
          SUM(IFNULL(tab1.payable_amount_total, 0)) as sum_payable_amount_total,
          SUM(IFNULL(tab1.paid_amount_total, 0)) as sum_paid_amount_total
        FROM
          b_ap_pay tab1
          LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
          LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
          LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_pay_status' AND tab4.dict_value = tab1.status
          LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_type' AND tab5.dict_value = tab1.type
          LEFT JOIN b_ap tab6 ON tab6.id = tab1.ap_id
          LEFT JOIN b_ap_detail tab7 ON tab7.ap_id = tab1.ap_id
          LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE 1=1
          <if test='searchCondition.purchaser_id != null'> AND tab1.purchaser_id = #{searchCondition.purchaser_id} </if>
          <if test='searchCondition.supplier_id != null'> AND tab1.supplier_id = #{searchCondition.supplier_id} </if>
          <if test='searchCondition.ap_code != null and searchCondition.ap_code != ""'> AND tab1.ap_code like concat('%', #{searchCondition.ap_code}, '%') </if>
          <if test='searchCondition.code != null and searchCondition.code != ""'> AND tab1.code like concat('%', #{searchCondition.code}, '%') </if>
          <if test='searchCondition.status_list != null and searchCondition.status_list.length != 0'>
            AND tab1.status in
            <foreach collection='searchCondition.status_list' item='item' index='index' open='(' separator=',' close=')'>
              #{item}
            </foreach>
          </if>
        </script>
        """)
    BApPayVo querySum(@Param("searchCondition") BApPayVo searchCondition);

    /**
     * 查询付款单明细
     */
    @Select("""
        -- 根据付款单ID查询付款单明细信息，包含银行账户信息
        SELECT t1.*, t2.name, t2.bank_name, t2.account_number 
        FROM b_ap_pay_detail t1 
        -- 关联银行账户表，获取银行账户基本信息
        LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id 
        -- #{p1}: 付款单主表ID
        WHERE t1.ap_pay_id = #{p1}
        """)
    List<BApPayDetailVo> getApPayDetail(@Param("p1") Integer id);

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select("""
        <script>
        SELECT
          SUM(IFNULL(tab1.payable_amount_total, 0)) as sum_payable_amount_total
        FROM
          b_ap_pay tab1
          LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
          LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
          LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ap_pay_status' AND tab4.dict_value = tab1.status
          LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ap_type' AND tab5.dict_value = tab1.type
          LEFT JOIN b_ap tab6 ON tab6.id = tab1.ap_id
          LEFT JOIN b_ap_detail tab7 ON tab7.ap_id = tab1.ap_id
          LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE true
          AND tab1.id = #{searchCondition.id}
        </script>
        """)
    BApPayVo queryViewSum(@Param("searchCondition") BApPayVo searchCondition);

    /**
     * 查询付款单金额汇总数据
     * @param ap_id 付款主表id
     * @param status 付款单状态
     * @return 金额汇总VO
     */
    @Select("""
        -- 根据应付账款ID和状态查询付款单金额汇总数据
        SELECT t1.id,
          -- payable_amount_total: 付款单计划付款总金额
          sum(t1.payable_amount_total) as payable_amount_total,
          -- paid_amount_total: 付款单已付款总金额
          sum(t1.paid_amount_total) as paid_amount_total,
          -- paying_amount_total: 付款单付款中总金额
          sum(t1.paying_amount_total) as paying_amount_total
        FROM b_ap_pay t1
        -- #{ap_id}: 应付账款主表ID
        WHERE t1.ap_id = #{ap_id}
          -- #{status}: 付款单状态，为空时查询所有状态
          AND (t1.status = #{status} OR #{status} = '' OR #{status} IS NULL)
        """)
    BApPayVo getSumAmount(@Param("ap_id") Integer ap_id, @Param("status") String status);

    /**
     * 更新付款单表（付款单计划付款总金额、付款单已付款总金额、付款单本次付款总金额）
     * @param apIds 付款单id集合
     */
    @Update("""
        <script>
        -- 批量更新付款单表的计划付款、已付款、付款中总金额
        UPDATE b_ap_pay t1
        JOIN (
            -- 子查询：从付款明细表汇总各付款单的金额数据
            SELECT ap_pay_id, 
                   -- pay_amount: 本次付款金额
                   COALESCE(SUM(pay_amount), 0) as total_amount,
                   -- cancel_amount: 作废金额 
                   COALESCE(SUM(cancel_amount), 0) as cancel_amount_total
            FROM b_ap_pay_detail
            GROUP BY ap_pay_id
        ) t2 ON t1.id = t2.ap_pay_id
        SET
            -- 更新计划付款总金额：使用明细表汇总的金额
            t1.payable_amount_total = t2.total_amount,
            -- 更新已付款总金额：状态为'1'-已付款时为总金额，否则为0
            t1.paid_amount_total = CASE WHEN t1.status = '1' THEN t2.total_amount ELSE 0 END,
            -- 更新付款中总金额：状态为'0'-待付款时为总金额，否则为0
            t1.paying_amount_total = CASE WHEN t1.status = '0' THEN t2.total_amount ELSE 0 END,
            -- 更新作废付款金额：状态为'2'-作废时为作废金额，否则为0
            t1.cancel_amount_total = CASE WHEN t1.status = '2' THEN t2.cancel_amount_total ELSE 0 END
        -- #{id}: 应付账款主表ID列表
        WHERE t1.ap_id IN
        <foreach collection='apIds' item='id' open='(' separator=',' close=')'>
          #{id}
        </foreach>
        </script>
        """)
    int updateTotalData(@Param("apIds") LinkedHashSet<Integer> apIds);


}
