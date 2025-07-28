package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.so.ar.BArEntity;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceVo;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.JsonArrayTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应收账款管理表（Accounts Receivable） Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-25
 */
@Repository
public interface BArMapper extends BaseMapper<BArEntity> {


    String pageSql = """
        SELECT
            tab1.*,
            COALESCE(tabb2.unreceive_amount_total,0) as  unreceive_amount,
            tab2.soOrderListData,
            tab3.bankListData,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as receive_status_name,
            tabb1.one_file as doc_att_file,
            tabb2.receivable_amount_total as receivable_amount,
            tabb2.received_amount_total as received_amount,
            tabb2.receiving_amount_total as receiving_amount,
            tabb2.unreceive_amount_total as unreceive_amount,
            tabb2.stopreceive_amount_total as stopreceive_amount
        FROM
            b_ar tab1
            LEFT JOIN (SELECT
                t1.ar_id,JSON_ARRAYAGG(JSON_OBJECT(
                            'id',t1.id,
                            'code',t1.code,
                            'ar_id',t1.ar_id,
                            'ar_code',t1.ar_code,
                            'type',t1.type,
                            'so_contract_code',t1.so_contract_code,
                            'so_order_id',t1.so_order_id,
                            'so_order_code',t1.so_order_code,
                            'so_goods',t1.so_goods,
                            'qty_total',t1.qty_total,
                            'amount_total',t1.amount_total,
                            'order_amount',t1.order_amount,
                            'remark',t1.remark,
                            'so_advance_payment_amount',(tt2.receiving_amount_total + tt2.received_amount_total),
                            'so_can_advance_payment_amount',(t1.amount_total - ifnull(t3.amount,0))
                        )) AS soOrderListData
                        FROM b_ar_source_advance t1
                        LEFT JOIN  b_ar t2 ON t1.ar_id = t2.id
                        LEFT JOIN b_ar_total tt2 ON t2.id = tt2.ar_id
                        LEFT JOIN (
           SELECT
             tt1.so_contract_code,
             SUM(tt2.receivable_amount_total) AS amount
           FROM
             b_ar tt1
           LEFT JOIN b_ar_total tt2 ON tt1.id = tt2.ar_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.so_contract_code
                  )
                        AS t3 ON t1.so_contract_code = t3.so_contract_code
                        GROUP BY t1.ar_id ) tab2 ON tab1.id = tab2.ar_id
            LEFT JOIN (SELECT
                t1.ar_id,JSON_ARRAYAGG(JSON_OBJECT(
                                'id',t1.id,
                                'code',t1.code,
                                'ar_id',t1.ar_id,
                                'ar_code',t1.ar_code,
                                'bank_accounts_id',t1.bank_accounts_id,
                                'bank_accounts_code',t1.bank_accounts_code,
                                'receivable_amount',t1.receivable_amount,
                                'received_amount',t1.received_amount,
                                'remark',t1.remark,
                                'name',t2.name,
                                'bank_name',t2.bank_name,
                                'account_number',t2.account_number
                        )) AS bankListData
                        FROM b_ar_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
                                                GROUP BY t1.ar_id) tab3 ON tab1.id = tab3.ar_id
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ar_status' AND tab6.dict_value = tab1.status
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ar_type' AND tab7.dict_value = tab1.type
            LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ar_receive_status' AND tab8.dict_value = tab1.receive_status
            LEFT JOIN b_ar_attach tabb1 on tab1.id = tabb1.ar_id
            LEFT JOIN b_ar_total tabb2 on tab1.id = tabb2.ar_id
            WHERE TRUE
        """;


    /**
     * 业务类型查询
     */
    @Select("select dict_value as dict_id ,label as dict_label from s_dict_data where code = 'b_ar_type' and is_del = false")
    List<BArVo> getType();

    /**
     * 分页查询
     */
    @Select("""
        <script>
        """ + pageSql + """
            AND tab1.is_del = false
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            AND (tab1.receive_status = #{p1.receive_status} or #{p1.receive_status} is null or  #{p1.receive_status} = '')
            AND (tab1.so_contract_code like concat('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or  #{p1.so_contract_code} = '')
            AND (tab1.so_order_code like concat('%', #{p1.so_order_code}, '%') or #{p1.so_order_code} is null or  #{p1.so_order_code} = '')
            AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                  <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                    #{item}
                  </foreach>
            </if>
        </script>
        """)
    @Results({
            @Result(property = "soOrderListData", column = "soOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    IPage<BArVo> selectPage(Page page, @Param("p1") BArVo searchCondition);


    /**
     * 根据id查询
     */
    @Select("""
        SELECT
            tab1.*,
            tabb2.unreceive_amount_total as unreceive_amount,
            tab3.bank_accounts_id,
            tab3.bank_accounts_code,
            tab3.receivable_amount,
            tab3.received_amount,
            tab9.name,
            tab9.bank_name,
            tab9.account_number,
            GROUP_CONCAT(tab10.NAME) AS bank_type_name,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as receive_status_name,
            tabb1.one_file as doc_att_file,
            tabb2.receivable_amount_total,
            tabb2.received_amount_total,
            tabb2.receiving_amount_total,
            tabb2.unreceive_amount_total        
         FROM b_ar tab1
         LEFT JOIN b_ar_detail tab3 ON tab1.id = tab3.ar_id
         LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
         LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
         LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ar_status' AND tab6.dict_value = tab1.status
         LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ar_type' AND tab7.dict_value = tab1.type
         LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ar_receive_status' AND tab8.dict_value = tab1.receive_status
         LEFT JOIN b_ar_attach tabb1 on tab1.id = tabb1.ar_id
         LEFT JOIN b_ar_total tabb2 on tab1.id = tabb2.ar_id
         LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
         LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id
        WHERE tab1.id = #{p1}
        GROUP BY tab1.code, tab3.code
        """)
    BArVo selectId(@Param("p1") Integer id);

    @Select("""
        <script>
            SELECT
            @row_num:= @row_num+ 1 as no,
            tab1.*,
            tabb2.unreceive_amount_total as  unreceive_amount,
            tab2.soOrderListData,
            tab3.bankListData,
            tab4.name as c_name,
            tab5.name as u_name,
            tab6.label as status_name,
            tab7.label as type_name,
            tab8.label as receive_status_name
        FROM
            b_ar tab1
            LEFT JOIN (SELECT
                t1.ar_id,JSON_ARRAYAGG(JSON_OBJECT('id',t1.id,
                            'code',t1.code,'ar_id',t1.ar_id,'ar_code',t1.ar_code,
                            'type',t1.type,'so_contract_code',t1.so_contract_code,
                            'so_order_id',t1.so_order_id,
                            'so_order_code',t1.so_order_code,
                            'so_goods',t1.so_goods,
                            'qty_total',t1.qty_total,
                            'amount_total',t1.amount_total,
                            'order_amount',t1.order_amount,
                            'remark',t1.remark,
                            'so_advance_payment_amount',(tt2.receiving_amount_total + tt2.received_amount_total),
                            'so_can_advance_payment_amount',(t1.amount_total - ifnull(t3.amount,0))
                        )) AS soOrderListData
                        FROM b_ar_source_advance t1
                        LEFT JOIN  b_ar t2 ON t1.ar_id = t2.id
                        LEFT JOIN b_ar_total tt2 ON t2.id = tt2.ar_id
                        LEFT JOIN (
           SELECT
             tt1.so_contract_code,
             SUM(tt2.receivable_amount_total) AS amount
           FROM
             b_ar tt1
           LEFT JOIN b_ar_total tt2 ON tt1.id = tt2.ar_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.so_contract_code
                  )
                        AS t3 ON t1.so_contract_code = t3.so_contract_code
                        GROUP BY t1.ar_id ) tab2 ON tab1.id = tab2.ar_id
            LEFT JOIN (SELECT
                t1.ar_id,JSON_ARRAYAGG(JSON_OBJECT('id',t1.id,
                                'code',t1.code,'ar_id',t1.ar_id,'ar_code',t1.ar_code,
                                'bank_accounts_id',t1.bank_accounts_id,
                                'bank_accounts_code',t1.bank_accounts_code,
                                'receivable_amount',t1.receivable_amount,
                                'received_amount',t1.received_amount,
                                'remark',t1.remark,
                                'name',t2.name,
                                'bank_name',t2.bank_name,
                                'account_number',t2.account_number
                        )) AS bankListData
                        FROM b_ar_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
                                                GROUP BY t1.ar_id) tab3 ON tab1.id = tab3.ar_id
            LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
            LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
            LEFT JOIN b_ar_total tabb2 on tab1.id = tabb2.ar_id
            LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ar_status' AND tab6.dict_value = tab1.status
            LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ar_type' AND tab7.dict_value = tab1.type
            LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ar_receive_status' AND tab8.dict_value = tab1.receive_status,
            (select @row_num:=0) tb9
            WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            AND (tab1.receive_status = #{p1.receive_status} or #{p1.receive_status} is null or  #{p1.receive_status} = '')
            AND (tab1.so_contract_code like concat('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or  #{p1.so_contract_code} = '')
            AND (tab1.so_order_code like concat('%', #{p1.so_order_code}, '%') or #{p1.so_order_code} is null or  #{p1.so_order_code} = '')
            AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
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
            @Result(property = "soOrderListData", column = "soOrderListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
            @Result(property = "bankListData", column = "bankListData", javaType = List.class, typeHandler = JsonArrayTypeHandler.class),
    })
    List<BArVo> selectExportList(@Param("p1") BArVo param);

    @Select("""
        <script>
            SELECT
            count(tab1.id)
        FROM
            b_ar tab1
            WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')
            AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')
            AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')
            AND (tab1.receive_status = #{p1.receive_status} or #{p1.receive_status} is null or  #{p1.receive_status} = '')
            AND (tab1.so_contract_code like concat('%', #{p1.so_contract_code}, '%') or #{p1.so_contract_code} is null or  #{p1.so_contract_code} = '')
            AND (tab1.so_order_code like concat('%', #{p1.so_order_code}, '%') or #{p1.so_order_code} is null or  #{p1.so_order_code} = '')
            AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
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
    Long selectExportCount(@Param("p1")BArVo param);

    /**
     * 查询销售订单下 收款账单
     */
    @Select("select * from b_ar tab1 left join b_so_order tab2 on tab1.so_order_code = tab2.code where tab2.id = #{p1} and tab1.is_del = false")
    List<BArVo> selectBySoCode(@Param("p1")String code);

    /**
     * 查询销售订单下 收款账单
     */
    @Select("select * from b_ar tab1 left join b_so_order tab2 on tab1.so_order_code = tab2.code where tab2.id = #{p1} and tab1.status != #{p2} and tab1.is_del = false")
    List<BArVo> selBySoCodeNotByStatus(@Param("p1")Integer code,@Param("p2") String dictBArStatusFive);

    /**
     *
     */


    /**
     * 查询合计信息
     */
    @Select("""
        <script>
        SELECT
            SUM(IFNULL(tabb2.receivable_amount_total, 0)) as receivable_amount_total,
            SUM(IFNULL(tabb2.received_amount_total, 0)) as received_amount_total,
            SUM(IFNULL(tabb2.receiving_amount_total, 0)) as receiving_amount_total,
            SUM(IFNULL(tabb2.unreceive_amount_total, 0)) as unreceive_amount_total,
            SUM(IFNULL(tabb2.stopreceive_amount_total, 0)) as stopreceive_amount_total
        FROM
            b_ar tab1  LEFT JOIN b_ar_total tabb2 on tab1.id = tabb2.ar_id
        WHERE TRUE
            AND tab1.is_del = false
            AND (tab1.id = #{p1.id} OR #{p1.id} IS NULL )
            AND (tab1.status = #{p1.status} OR #{p1.status} IS NULL OR #{p1.status} = '')
            AND (tab1.receive_status = #{p1.receive_status} OR #{p1.receive_status} IS NULL OR #{p1.receive_status} = '')
            AND (tab1.type = #{p1.type} OR #{p1.type} IS NULL OR #{p1.type} = '')
            AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') OR #{p1.code} IS NULL OR #{p1.code} = '')
            AND (tab1.so_contract_code LIKE CONCAT('%', #{p1.so_contract_code}, '%') OR #{p1.so_contract_code} IS NULL OR #{p1.so_contract_code} = '')
            AND (tab1.so_order_code LIKE CONCAT('%', #{p1.so_order_code}, '%') OR #{p1.so_order_code} IS NULL OR #{p1.so_order_code} = '')
            AND (tab1.project_code LIKE CONCAT('%', #{p1.project_code}, '%') OR #{p1.project_code} IS NULL OR #{p1.project_code} = '')
            AND (tab1.customer_id = #{p1.customer_id}  or #{p1.customer_id} is null   )
            AND (tab1.seller_id = #{p1.seller_id}  or #{p1.seller_id} is null   )
            <if test='p1.status_list != null and p1.status_list.length!=0' >
              and tab1.status in
                <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                  #{item}
                </foreach>
            </if>
        </script>
        """)
    BArVo querySum(@Param("p1") BArVo searchCondition);



    /**
     * 获取应收账款预收款关联单据信息（订单）
     * 根据selectId的sql中关于b_ar_source_advance查询单独拆解出来
     * 注意：只能用在更新的情况
     * @param id 应收账款ID
     * @return 预收款关联单据信息
     */
    @Select("""
        SELECT
            t1.*,
            (tabb2.receiving_amount_total + tabb2.received_amount_total) as so_advance_payment_amount,
            (t1.amount_total - ifnull(t3.amount, 0) + t1.order_amount ) as so_can_advance_payment_amount
        FROM b_ar_source_advance t1
        LEFT JOIN b_ar t2 ON t1.ar_id = t2.id
        LEFT JOIN b_ar_total tabb2 on t2.id = tabb2.ar_id
        LEFT JOIN (
           SELECT
             tt1.so_contract_code,
             SUM(tt2.receivable_amount_total) AS amount
           FROM
             b_ar tt1
           LEFT JOIN b_ar_total tt2 ON tt1.id = tt2.ar_id
           WHERE
             tt1.STATUS != '5'
             AND tt1.is_del = FALSE
           GROUP BY
             tt1.so_contract_code
         )
                   AS t3 ON t1.so_contract_code = t3.so_contract_code
        WHERE t1.ar_id = #{p1}
        """)
    List<BArSourceAdvanceVo> getArSourceAdvanceReceiveOnlyUsedInUpdateType(@Param("p1") Integer id);

    /**
     * 获取应收账款明细信息（银行账号）
     * 根据selectId的sql中关于b_ar_detail查询单独拆解出来
     * @param id 应收账款ID
     * @return 应收账款明细信息
     */
    @Select("""
        SELECT
            t1.*,
            t2.name,
            t2.bank_name,
            t2.account_number,
            GROUP_CONCAT(t3.name) AS bank_type_name
        FROM b_ar_detail t1
        LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
        LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
        WHERE t1.ar_id = #{p1}
        GROUP BY t1.id, t1.code, t1.ar_id, t1.ar_code, t1.bank_accounts_id, t1.bank_accounts_code, t1.receivable_amount, t1.received_amount, t1.remark, t2.name, t2.bank_name, t2.account_number
        """)
    List<BArDetailVo> getArDetail(@Param("p1") Integer id);

    /**
     * 获取应收账款源单信息
     * @param id 应收账款ID
     * @return 应收账款源单信息
     */
    @Select("SELECT * FROM b_ar_source WHERE ar_id = #{p1}")
    BArSourceVo getArSource(@Param("p1") Integer id);
}