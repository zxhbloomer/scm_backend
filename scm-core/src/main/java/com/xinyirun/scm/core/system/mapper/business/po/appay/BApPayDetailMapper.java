package com.xinyirun.scm.core.system.mapper.business.po.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPayDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 付款单明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApPayDetailMapper extends BaseMapper<BApPayDetailEntity> {

    /**
     * 根据ap_code查询付款单明细
     */
    @Select("""
            -- 根据应付账款编号查询付款单明细信息
            SELECT t1.* FROM b_ap_pay_detail t1 
            -- #{code}: 应付账款主表编号
            WHERE t1.ap_code = #{code}
            """)
    List<BApPayDetailVo> selectByCode(@Param("code") String code);

    /**
     * 根据id查询付款单明细
     */
    @Select("""
            -- 根据付款单主表ID查询付款单明细信息
            SELECT t1.* FROM b_ap_pay_detail t1 
            -- #{id}: 付款单主表ID
            WHERE t1.ap_pay_id = #{id}
            """)
    List<BApPayDetailVo> selectById(@Param("id") Integer apPayId);

    /**
     * 批量更新付款单明细的已付款、付款中、未付款金额
     * @param apIds 应付账款id集合
     */
//    @Update(
//        "    <script>                                  "+
//        "    UPDATE b_ap_pay_detail t1                                   "+
//        "    LEFT JOIN b_ap_pay t2 ON t1.ap_pay_id = t2.id              "+
//        "    SET                                                      "+
//        "        t1.paid_amount = CASE WHEN t2.status = '1' THEN t1.pay_amount ELSE 0 END,   "+
//        "        t1.paying_amount = CASE WHEN t2.status = '0' THEN t1.pay_amount ELSE 0 END, "+
//        "        t1.unpay_amount = CASE WHEN t2.status = '0' THEN t1.pay_amount ELSE 0 END,   "+
//        "        t1.cancel_amount = CASE WHEN t2.status = '2' THEN t1.pay_amount ELSE 0 END   "+ // 作废
//        "    WHERE t1.ap_id IN                                         "+
//        "    <foreach collection='apIds' item='id' open='(' separator=',' close=')'>        "+
//        "        #{id}                                                 "+
//        "    </foreach>                                                "+
//        "  </script>  "
//    )
//    int updateTotalData(@Param("apIds") LinkedHashSet<Integer> apIds);

}
