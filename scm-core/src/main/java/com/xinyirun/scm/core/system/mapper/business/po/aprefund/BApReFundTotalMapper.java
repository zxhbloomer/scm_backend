package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.aprefund.BApReFundTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundTotalVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 应付账款退款管理表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-10
 */
@Repository
public interface BApReFundTotalMapper extends BaseMapper<BApReFundTotalEntity> {

    /**
     * 根据应付账款退款主表ID查询应付账款退款财务信息
     * @param apRefundId 应付账款退款主表ID
     * @return BApReFundTotalVo
     */
    @Select("select * from b_ap_refund_total where ap_refund_id = #{apRefundId}")
    BApReFundTotalVo selectByApRefundId(@Param("apRefundId") Integer apRefundId);

    /**
     * 批量更新退款总计数据，从退款源单表中同步数据
     * 将b_ap_refund_source_advance与b_ap_refund_total进行1:1关联更新
     * @param apRefundIdSet 退款主表ID集合
     * @return 影响行数
     */
    @Update("""
            <script>
            UPDATE b_ap_refund_total t2 
            INNER JOIN b_ap_refund_source_advance t1 ON t1.ap_refund_id = t2.ap_refund_id 
            SET 
            t2.refundable_amount_total = t1.refundable_amount_total, 
            t2.refunded_amount_total = t1.refunded_amount_total, 
            t2.refunding_amount_total = t1.refunding_amount_total, 
            t2.unrefund_amount_total = t1.unrefund_amount_total, 
            t2.cancelrefund_amount_total = t1.cancelrefund_amount_total 
            WHERE t2.ap_refund_id IN 
            <foreach collection='apRefundIdSet' item='id' open='(' separator=',' close=')'>
            #{id}
            </foreach>
            </script>
            """)
    int batchUpdateRefundTotalFromSource(@Param("apRefundIdSet") LinkedHashSet<Integer> apRefundIdSet);

    /**
     * 根据合同ID查询相关的退款主表ID集合
     * @param contractId 合同ID
     * @return 退款主表ID集合
     */
    @Select("""
            SELECT DISTINCT t1.id 
            FROM b_ap_refund t1 
            INNER JOIN b_po_contract t2 ON t1.po_contract_id = t2.id 
            WHERE t2.id = #{contractId} AND t1.is_del = false
            """)
    List<Integer> selectRefundIdsByContractId(@Param("contractId") Integer contractId);

    /**
     * 插入缺失的应付退款总计记录
     * 为没有Total记录的退款ID创建初始Total记录
     * @param apRefundIdSet 退款主表ID集合
     * @return 插入行数
     */
    @Update("""
            <script>
            INSERT INTO b_ap_refund_total (ap_refund_id, po_order_id, refundable_amount_total, refunded_amount_total, 
                                          refunding_amount_total, unrefund_amount_total, cancelrefund_amount_total, c_time, u_time)
            SELECT 
                t1.id as ap_refund_id,
                t1.po_order_id,
                0 as refundable_amount_total,
                0 as refunded_amount_total,
                0 as refunding_amount_total,
                0 as unrefund_amount_total,
                0 as cancelrefund_amount_total,
                NOW() as c_time,
                NOW() as u_time
            FROM b_ap_refund t1
            WHERE t1.id IN
            <foreach collection='apRefundIdSet' item='id' open='(' separator=',' close=')'>
                #{id}
            </foreach>
            AND NOT EXISTS (
                SELECT 1 FROM b_ap_refund_total t2 WHERE t2.ap_refund_id = t1.id
            )
            </script>
            """)
    int insertMissingRefundTotal(@Param("apRefundIdSet") LinkedHashSet<Integer> apRefundIdSet);

}