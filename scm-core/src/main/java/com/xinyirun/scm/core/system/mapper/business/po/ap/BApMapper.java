package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.ap.BApEntity;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApSourceVo;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApVo;
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
public interface BApMapper extends BaseMapper<BApEntity> {


    String pageSql = """
        SELECT
            tab1.*,
            COALESCE(tabb2.unpay_amount_total,0) as  unpay_amount,
            tab2.poOrderListData,
            tab3.bankListData,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as pay_status_name,
            tabb1.one_file as doc_att_file,
            tabb2.payable_amount_total as payable_amount,
            tabb2.paid_amount_total as paid_amount,
            tabb2.paying_amount_total as paying_amount,
            tabb2.unpay_amount_total as unpay_amount,
            tabb2.stoppay_amount_total as stoppay_amount
        FROM
            b_ap tab1
            LEFT JOIN (SELECT
                t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT(
                            'id',t1.id,
                            'code',t1.code,
                            'ap_id',t1.ap_id,
                            'ap_code',t1.ap_code,
                            'type',t1.type,
                            'po_contract_code',t1.po_contract_code,
                            'po_order_id',t1.po_order_id,
                            'po_order_code',t1.po_order_code,
                            'po_goods',t1.po_goods,
                            'qty_total',t1.qty_total,
                            'amount_total',t1.amount_total,
                            'order_amount',t1.order_amount,
                            'remark',t1.remark,
                            'po_advance_payment_amount',(tt2.paying_amount_total + tt2.paid_amount_total),
                            'po_can_advance_payment_amount',(t1.amount_total - ifnull(t3.amount,0))
                        )) AS poOrderListData
                        FROM b_ap_source_advance t1
                        LEFT JOIN  b_ap t2 ON t1.ap_id = t2.id
                        LEFT JOIN b_ap_total tt2 ON t2.id = tt2.ap_id
                        LEFT JOIN (
           SELECT
             tt1.po_contract_code,
             SUM(tt2.payable_amount_total) AS amount
           FROM
             b_ap tt1
           LEFT JOIN b_ap_total tt2 ON tt1.id = tt2.ap_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.po_contract_code
                  )
                        AS t3 ON t1.po_contract_code = t3.po_contract_code
                        GROUP BY t1.ap_id ) tab2 ON tab1.id = tab2.ap_id
            LEFT JOIN (SELECT
                t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT(
                                'id',t1.id,
                                'code',t1.code,
                                'ap_id',t1.ap_id,
                                'ap_code',t1.ap_code,
                                'bank_accounts_id',t1.bank_accounts_id,
                                'bank_accounts_code',t1.bank_accounts_code,
                                'payable_amount',t1.payable_amount,
                                'paid_amount',t1.paid_amount,
                                'remark',t1.remark,
                                'name',t2.name,
                                'bank_name',t2.bank_name,
                                'account_number',t2.account_number
                        )) AS bankListData
                        FROM b_ap_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
                                                GROUP BY t1.ap_id) tab3 ON tab1.id = tab3.ap_id
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_status' AND tab6.dict_value = tab1.status
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_type' AND tab7.dict_value = tab1.type
            LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_pay_status' AND tab8.dict_value = tab1.pay_status
            LEFT JOIN b_ap_attach tabb1 on tab1.id = tabb1.ap_id
            LEFT JOIN b_ap_total tabb2 on tab1.id = tabb2.ap_id
            WHERE TRUE
        """;


    /**
     * 业务类型查询
     */
    @Select("""
            -- 查询应付账款业务类型字典数据
            select dict_value as dict_id ,label as dict_label 
            from s_dict_data 
            -- code = 'b_ap_type': 应付账款类型字典编码
            where code = 'b_ap_type' 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and is_del = false
            """)
    List<BApVo> getType();

    /**
     * 分页查询
     */
    @Select("""
        <script>
        """ + pageSql + """
            -- is_del = false: 查询未删除的记录（0-未删除，1-已删除）
            AND tab1.is_del = false
            -- p1.code: 应付账款编号参数，支持模糊查询
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            -- p1.status: 状态参数（0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成）
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            -- p1.type: 类型参数（1-应付、2-预付、3-其他支出）
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            -- p1.pay_status: 付款状态参数（0-未付款、1-部分付款、2-已付款、-1-中止付款）
            AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')
            -- p1.po_contract_code: 采购合同编号参数，支持模糊查询
            AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')
            -- p1.po_order_code: 采购订单编号参数，支持模糊查询
            AND (tab1.po_order_code like concat('%', #{p1.po_order_code}, '%') or #{p1.po_order_code} is null or  #{p1.po_order_code} = '')
            -- p1.supplier_id: 供应商ID参数
            AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            -- p1.purchaser_id: 购买方ID参数
            AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            <if test='p1.status_list != null and p1.status_list.length!=0' >
                -- 状态列表查询：支持多个状态值查询
                and tab1.status in
                  <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                    #{item}
                  </foreach>
            </if>
        </script>
        """)
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BApVo> selectPage(Page page, @Param("p1") BApVo searchCondition);


    /**
     * 根据id查询
     */
    @Select("""
        -- 根据应付账款ID查询详细信息，包含字典翻译、明细信息、银行账户信息
        SELECT
            tab1.*,
            -- unpay_amount_total: 未付款总金额
            tabb2.unpay_amount_total as unpay_amount,
            -- 银行账户相关字段
            tab3.bank_accounts_id,
            tab3.bank_accounts_code,
            tab3.payable_amount,
            tab3.paid_amount,
            tab9.name,
            tab9.bank_name,
            tab9.account_number,
            GROUP_CONCAT(tab10.NAME) AS bank_type_name,
            -- 创建人和修改人姓名
            tab4.name as c_name,
            tab5.name as u_name,
            -- 字典翻译字段
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as pay_status_name,
            -- 文档附件
            tabb1.one_file as doc_att_file,
            -- 付款统计字段
            tabb2.payable_amount_total,
            tabb2.paid_amount_total,
            tabb2.paying_amount_total,
            tabb2.unpay_amount_total        
         FROM b_ap tab1
         -- 关联应付账款明细表，获取银行账户信息
         LEFT JOIN b_ap_detail tab3 ON tab1.id = tab3.ap_id
         -- 关联员工表，获取创建人信息
         LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
         -- 关联员工表，获取修改人信息
         LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
         -- 关联状态字典，获取状态名称：0-待审批 1-审批中 2-执行中 3-驳回 4-作废审批中 5-已作废 6-已完成
         LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_status' AND tab6.dict_value = tab1.status
         -- 关联类型字典，获取类型名称：1-应付、2-预付、3-其他支出
         LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_type' AND tab7.dict_value = tab1.type
         -- 关联付款状态字典，获取付款状态名称：0-未付款、1-部分付款、2-已付款、-1-中止付款
         LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_pay_status' AND tab8.dict_value = tab1.pay_status
         -- 关联附件表，获取附件文件
         LEFT JOIN b_ap_attach tabb1 on tab1.id = tabb1.ap_id
         -- 关联汇总表，获取各种付款金额统计
         LEFT JOIN b_ap_total tabb2 on tab1.id = tabb2.ap_id
         -- 关联银行账户主表，获取银行账户基本信息
         LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
         -- 关联银行账户类型表，获取银行账户类型信息
         LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id
        -- p1: 应付账款主表ID参数
        WHERE tab1.id = #{p1}
        GROUP BY tab1.code, tab3.code
        """)
    BApVo selectId(@Param("p1") Integer id);

    @Select("""
        <script>
            SELECT
            @row_num:= @row_num+ 1 as no,
            tab1.*,
            tabb2.unpay_amount_total as  unpay_amount,
            tab2.poOrderListData,
            tab3.bankListData,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as pay_status_name
        FROM
            b_ap tab1
            LEFT JOIN (SELECT
                t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT('id',t1.id,
                            'code',t1.code,'ap_id',t1.ap_id,'ap_code',t1.ap_code,
                            'type',t1.type,'po_contract_code',t1.po_contract_code,
                            'po_order_id',t1.po_order_id,
                            'po_order_code',t1.po_order_code,
                            'po_goods',t1.po_goods,
                            'qty_total',t1.qty_total,
                            'amount_total',t1.amount_total,
                            'order_amount',t1.order_amount,
                            'remark',t1.remark,
                            'po_advance_payment_amount',(tt2.paying_amount_total + tt2.paid_amount_total),
                            'po_can_advance_payment_amount',(t1.amount_total - ifnull(t3.amount,0))
                        )) AS poOrderListData
                        FROM b_ap_source_advance t1
                        LEFT JOIN  b_ap t2 ON t1.ap_id = t2.id
                        LEFT JOIN b_ap_total tt2 ON t2.id = tt2.ap_id
                        LEFT JOIN (
           SELECT
             tt1.po_contract_code,
             SUM(tt2.payable_amount_total) AS amount
           FROM
             b_ap tt1
           LEFT JOIN b_ap_total tt2 ON tt1.id = tt2.ap_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.po_contract_code
                  )
                        AS t3 ON t1.po_contract_code = t3.po_contract_code
                        GROUP BY t1.ap_id ) tab2 ON tab1.id = tab2.ap_id
            LEFT JOIN (SELECT
                t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT('id',t1.id,
                                'code',t1.code,'ap_id',t1.ap_id,'ap_code',t1.ap_code,
                                'bank_accounts_id',t1.bank_accounts_id,
                                'bank_accounts_code',t1.bank_accounts_code,
                                'payable_amount',t1.payable_amount,
                                'paid_amount',t1.paid_amount,
                                'remark',t1.remark,
                                'name',t2.name,
                                'bank_name',t2.bank_name,
                                'account_number',t2.account_number
                        )) AS bankListData
                        FROM b_ap_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
                                                GROUP BY t1.ap_id) tab3 ON tab1.id = tab3.ap_id
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            LEFT JOIN b_ap_total tabb2 on tab1.id = tabb2.ap_id
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_status' AND tab6.dict_value = tab1.status
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_type' AND tab7.dict_value = tab1.type
            LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_pay_status' AND tab8.dict_value = tab1.pay_status,
            (select @row_num:=0) tb9
            WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')
            AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')
            AND (tab1.po_order_code like concat('%', #{p1.po_order_code}, '%') or #{p1.po_order_code} is null or  #{p1.po_order_code} = '')
            AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
           <if test='p1.status_list != null and p1.status_list.length!=0' >
            and tab1.status in
                <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                 #{item}
                </foreach>
           </if>
           <if test='p1.ids != null and p1.ids.length != 0' >
            and tab1.id in
                <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                 #{item}
                </foreach>
           </if>
        </script>
        """)
    @Results({
            @Result(property = "poOrderListData", column = "poOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BApVo> selectExportList(@Param("p1") BApVo param);

    @Select("""
        <script>
            SELECT
            count(tab1.id)
        FROM
            b_ap tab1
            WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            AND (tab1.pay_status = #{p1.pay_status} or #{p1.pay_status} is null or  #{p1.pay_status} = '')
            AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')
            AND (tab1.po_order_code like concat('%', #{p1.po_order_code}, '%') or #{p1.po_order_code} is null or  #{p1.po_order_code} = '')
            AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
           <if test='p1.status_list != null and p1.status_list.length!=0' >
            and tab1.status in
                <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                 #{item}
                </foreach>
           </if>
           <if test='p1.ids != null and p1.ids.length != 0' >
            and tab1.id in
                <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                 #{item}
                </foreach>
           </if>
        </script>
        """)
    Long selectExportCount(@Param("p1")BApVo param);

    /**
     * 查询采购订单下 付款账单
     */
    @Select("""
            -- 根据采购订单ID查询对应的应付账款信息
            select * from b_ap tab1 
            -- 关联采购订单表，通过订单编号关联
            left join b_po_order tab2 on tab1.po_order_code = tab2.code 
            -- p1: 采购订单ID参数
            where tab2.id = #{p1} 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and tab1.is_del = false
            """)
    List<BApVo> selectByPoCode(@Param("p1")String code);

    /**
     * 查询采购订单下 付款账单
     */
    @Select("""
            -- 根据采购订单ID查询应付账款信息，排除指定状态
            select * from b_ap tab1 
            -- 关联采购订单表，通过订单编号关联
            left join b_po_order tab2 on tab1.po_order_code = tab2.code 
            -- p1: 采购订单ID参数
            where tab2.id = #{p1} 
            -- p2: 需要排除的状态参数（通常为'5'-已作废）
            and tab1.status != #{p2} 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and tab1.is_del = false
            """)
    List<BApVo> selByPoCodeNotByStatus(@Param("p1")Integer code,@Param("p2") String dictBApStatusFive);

    /**
     *
     */


    /**
     * 查询合计信息
     */
    @Select("""
        <script>
        SELECT
            SUM(IFNULL(tabb2.payable_amount_total, 0)) as payable_amount_total,
            SUM(IFNULL(tabb2.paid_amount_total, 0)) as paid_amount_total,
            SUM(IFNULL(tabb2.paying_amount_total, 0)) as paying_amount_total,
            SUM(IFNULL(tabb2.unpay_amount_total, 0)) as unpay_amount_total,
            SUM(IFNULL(tabb2.stoppay_amount_total, 0)) as stoppay_amount_total
        FROM
            b_ap tab1  LEFT JOIN b_ap_total tabb2 on tab1.id = tabb2.ap_id
        WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.id = #{p1.id} OR #{p1.id} IS NULL )
            AND (tab1.status = #{p1.status} OR #{p1.status} IS NULL OR #{p1.status} = '')
            AND (tab1.pay_status = #{p1.pay_status} OR #{p1.pay_status} IS NULL OR #{p1.pay_status} = '')
            AND (tab1.type = #{p1.type} OR #{p1.type} IS NULL OR #{p1.type} = '')
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') OR #{p1.code} IS NULL OR #{p1.code} = '')
            AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') OR #{p1.po_contract_code} IS NULL OR #{p1.po_contract_code} = '')
            AND (tab1.po_order_code LIKE CONCAT('%', #{p1.po_order_code}, '%') OR #{p1.po_order_code} IS NULL OR #{p1.po_order_code} = '')
            AND (tab1.project_code LIKE CONCAT('%', #{p1.project_code}, '%') OR #{p1.project_code} IS NULL OR #{p1.project_code} = '')
            AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            <if test='p1.status_list != null and p1.status_list.length!=0' >
              and tab1.status in
                <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                  #{item}
                </foreach>
            </if>
        </script>
        """)
    BApVo querySum(@Param("p1") BApVo searchCondition);



    /**
     * 获取应付账款预付款关联单据信息（订单）
     * 根据selectId的sql中关于b_ap_source_advance查询单独拆解出来
     * 注意：只能用在更新的情况
     * @param id 应付账款ID
     * @return 预付款关联单据信息
     */
    @Select("""
        SELECT
            t1.*,
            (tabb2.paying_amount_total + tabb2.paid_amount_total) as po_advance_payment_amount,
            (t1.amount_total - ifnull(t3.amount, 0) + t1.order_amount ) as po_can_advance_payment_amount
        FROM b_ap_source_advance t1
        LEFT JOIN b_ap t2 ON t1.ap_id = t2.id
        LEFT JOIN b_ap_total tabb2 on t2.id = tabb2.ap_id
        LEFT JOIN (
           SELECT
             tt1.po_contract_code,
             SUM(tt2.payable_amount_total) AS amount
           FROM
             b_ap tt1
           LEFT JOIN b_ap_total tt2 ON tt1.id = tt2.ap_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.po_contract_code
         )
                   AS t3 ON t1.po_contract_code = t3.po_contract_code
        WHERE t1.ap_id = #{p1}
        """)
    List<BApSourceAdvanceVo> getApSourceAdvancePayOnlyUsedInUpdateType(@Param("p1") Integer id);

    /**
     * 获取应付账款明细信息（银行账号）
     * 根据selectId的sql中关于b_ap_detail查询单独拆解出来
     * @param id 应付账款ID
     * @return 应付账款明细信息
     */
    @Select("""
        SELECT
            t1.*,
            t2.name,
            t2.bank_name,
            t2.account_number,
            GROUP_CONCAT(t3.name) AS bank_type_name
        FROM b_ap_detail t1
        LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
        LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
        WHERE t1.ap_id = #{p1}
        GROUP BY t1.id, t1.code, t1.ap_id, t1.ap_code, t1.bank_accounts_id, t1.bank_accounts_code, t1.payable_amount, t1.paid_amount, t1.remark, t2.name, t2.bank_name, t2.account_number
        """)
    List<BApDetailVo> getApDetail(@Param("p1") Integer id);

    /**
     * 获取应付账款源单信息
     * @param id 应付账款ID
     * @return 应付账款源单信息
     */
    @Select("""
            -- 根据应付账款主表ID查询源单信息
            SELECT * FROM b_ap_source 
            -- p1: 应付账款主表ID参数
            WHERE ap_id = #{p1}
            """)
    BApSourceVo getApSource(@Param("p1") Integer id);
}
