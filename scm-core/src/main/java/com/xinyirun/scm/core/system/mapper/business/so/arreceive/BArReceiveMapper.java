package com.xinyirun.scm.core.system.mapper.business.so.arreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 收款单表 Mapper 接口  
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReceiveMapper extends BaseMapper<BArReceiveEntity> {


    /**
     * 列表查询（分页）
     */
    @Select("""
        <script>
        SELECT
            tab1.*,
            tab2.NAME AS c_name,
            tab3.NAME AS u_name,
            tab4.label AS status_name,
            tab5.label AS type_name,
            tab6.so_order_id AS so_order_id,
            tab6.so_order_code AS so_order_code,
            tab6.so_contract_code AS so_contract_code
        FROM
            b_ar_receive tab1
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ar_receive_status' AND tab4.dict_value = tab1.status
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ar_type' AND tab5.dict_value = tab1.type
            LEFT JOIN b_ar tab6 ON tab6.id = tab1.ar_id
            LEFT JOIN b_ar_detail tab7 ON tab7.ar_id = tab1.ar_id
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE 1=1
          <if test='p1.seller_id != null'> AND tab1.seller_id = #{p1.seller_id} </if>
          <if test='p1.customer_id != null'> AND tab1.customer_id = #{p1.customer_id} </if>
          <if test='p1.ar_code != null and p1.ar_code != ""'> AND tab1.ar_code like concat('%', #{p1.ar_code}, '%') </if>
          <if test='p1.code != null and p1.code != ""'> AND tab1.code like concat('%', #{p1.code}, '%') </if>
          <if test='p1.status_list != null and p1.status_list.length != 0'>
            AND tab1.status in
            <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
              #{item}
            </foreach>
          </if>
        </script>
        """)
    IPage<BArReceiveVo> selectPage(Page<BArReceiveVo> page, @Param("p1") BArReceiveVo searchCondition);

    /**
     * 获取单条数据
     */
    @Select("""
        SELECT
            tab1.*,
            tab2.NAME AS c_name,
            tab3.NAME AS u_name,
            tab4.label AS status_name,
            tab5.label AS type_name,
            tab6.so_order_code AS so_order_code,
            tab6.so_order_id AS so_order_id,
            tab6.so_contract_code AS so_contract_code
        FROM
            b_ar_receive tab1
            LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
            LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
            LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ar_receive_status' AND tab4.dict_value = tab1.status
            LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ar_type' AND tab5.dict_value = tab1.type
            LEFT JOIN b_ar tab6 ON tab6.id = tab1.ar_id
            LEFT JOIN b_ar_detail tab7 ON tab7.ar_id = tab1.ar_id
            LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE tab1.id = #{p1}
        """)
    BArReceiveVo selById(@Param("p1") Integer id);

    /**
     * 查询ar_id的收款单状态等于status的收款单
     */
    @Select("""
        select * from b_ar_receive where ar_id = #{p1} and status = #{p2}
        """)
    List<BArReceiveVo> selectArReceiveByStatus(@Param("p1")  Integer arId, @Param("p2") String status);


    /**
     * 查询ar_id的收款单状态不等于status的收款单
     */
    @Select("""
        select * from b_ar_receive where ar_id = #{p1} and status != #{p2}
        """)
    List<BArReceiveVo> selectArReceiveByNotStatus(@Param("p1")  Integer arId, @Param("p2") String status);

    /**
     * 汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select("""
        <script>
        SELECT
          SUM(IFNULL(tab1.receivable_amount_total, 0)) as sum_receivable_amount_total,
          SUM(IFNULL(tab1.received_amount_total, 0)) as sum_received_amount_total
        FROM
          b_ar_receive tab1
          LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
          LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
          LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ar_receive_status' AND tab4.dict_value = tab1.status
          LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ar_type' AND tab5.dict_value = tab1.type
          LEFT JOIN b_ar tab6 ON tab6.id = tab1.ar_id
          LEFT JOIN b_ar_detail tab7 ON tab7.ar_id = tab1.ar_id
          LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE 1=1
          <if test='searchCondition.seller_id != null'> AND tab1.seller_id = #{searchCondition.seller_id} </if>
          <if test='searchCondition.customer_id != null'> AND tab1.customer_id = #{searchCondition.customer_id} </if>
          <if test='searchCondition.ar_code != null and searchCondition.ar_code != ""'> AND tab1.ar_code like concat('%', #{searchCondition.ar_code}, '%') </if>
          <if test='searchCondition.code != null and searchCondition.code != ""'> AND tab1.code like concat('%', #{searchCondition.code}, '%') </if>
          <if test='searchCondition.status_list != null and searchCondition.status_list.length != 0'>
            AND tab1.status in
            <foreach collection='searchCondition.status_list' item='item' index='index' open='(' separator=',' close=')'>
              #{item}
            </foreach>
          </if>
        </script>
        """)
    BArReceiveVo querySum(@Param("searchCondition") BArReceiveVo searchCondition);

    /**
     * 查询收款单明细
     */
    @Select("""
        SELECT t1.*, t2.name, t2.bank_name, t2.account_number FROM b_ar_receive_detail t1 LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id WHERE t1.ar_receive_id = #{p1}
        """)
    List<BArReceiveDetailVo> getArReceiveDetail(@Param("p1") Integer id);

    /**
     * 单条汇总查询
     * @param searchCondition 查询条件
     * @return 汇总结果
     */
    @Select("""
        <script>
        SELECT
          SUM(IFNULL(tab1.receivable_amount_total, 0)) as sum_receivable_amount_total
        FROM
          b_ar_receive tab1
          LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
          LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
          LEFT JOIN s_dict_data tab4 ON tab4.CODE = 'b_ar_receive_status' AND tab4.dict_value = tab1.status
          LEFT JOIN s_dict_data tab5 ON tab5.CODE = 'b_ar_type' AND tab5.dict_value = tab1.type
          LEFT JOIN b_ar tab6 ON tab6.id = tab1.ar_id
          LEFT JOIN b_ar_detail tab7 ON tab7.ar_id = tab1.ar_id
          LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
        WHERE true
          AND tab1.id = #{searchCondition.id}
        </script>
        """)
    BArReceiveVo queryViewSum(@Param("searchCondition") BArReceiveVo searchCondition);

    /**
     * 查询收款单金额汇总数据
     * @param ar_id 收款主表id
     * @param status 收款单状态
     * @return 金额汇总VO
     */
    @Select("""
        SELECT t1.id,
          sum(t1.receivable_amount_total) as receivable_amount_total,
          sum(t1.received_amount_total) as received_amount_total,
          sum(t1.receiving_amount_total) as receiving_amount_total
        FROM b_ar_receive t1
        WHERE t1.ar_id = #{ar_id}
          AND (t1.status = #{status} OR #{status} = '' OR #{status} IS NULL)
        """)
    BArReceiveVo getSumAmount(@Param("ar_id") Integer ar_id, @Param("status") String status);

    /**
     * 更新收款单表（收款单计划收款总金额、收款单已收款总金额、收款单本次收款总金额）
     * @param arIds 收款单id集合
     */
    @Update("""
        <script>
        UPDATE b_ar_receive t1
        JOIN (
            SELECT ar_receive_id, COALESCE(SUM(receive_amount), 0) as total_amount , COALESCE(SUM(cancel_amount), 0) as cancel_amount_total
            FROM b_ar_receive_detail
            GROUP BY ar_receive_id
        ) t2 ON t1.id = t2.ar_receive_id
        SET
            t1.receivable_amount_total = t2.total_amount,
            t1.received_amount_total = CASE WHEN t1.status = '1' THEN t2.total_amount ELSE 0 END,
            t1.receiving_amount_total = CASE WHEN t1.status = '0' THEN t2.total_amount ELSE 0 END,
            t1.cancel_amount_total = CASE WHEN t1.status = '2' THEN t2.cancel_amount_total ELSE 0 END
        WHERE t1.ar_id IN
        <foreach collection='arIds' item='id' open='(' separator=',' close=')'>
          #{id}
        </foreach>
        </script>
        """)
    int updateTotalData(@Param("arIds") LinkedHashSet<Integer> arIds);


}