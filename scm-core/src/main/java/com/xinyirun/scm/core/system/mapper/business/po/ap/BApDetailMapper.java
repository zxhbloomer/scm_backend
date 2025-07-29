package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.ap.BApDetailEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付账款明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApDetailMapper extends BaseMapper<BApDetailEntity> {

    @Select("""
            -- 根据应付账款主表ID查询应付账款明细信息
            select * from b_ap_detail 
            -- #{p1}: 应付账款主表ID
            where ap_id = #{p1}
            """)
    List<BApDetailEntity> selectByApId(@Param("p1") Integer ap_id);

    /**
     * 根据应付账款ID删除明细数据
     */
    @Delete("""
            -- 根据应付账款主表ID删除所有相关明细记录
            DELETE FROM b_ap_detail t 
            -- #{ap_id}: 应付账款主表ID
            where t.ap_id = #{ap_id}
            """)
    void deleteByApId(Integer ap_id);

    /**
     * 批量更新明细表的已付款、付款中、未付款金额
     * @param apIds 应付账款ID集合
     */
    @Update({
            "  <script>                                                                        ",
            "  -- 批量更新应付账款明细表的已付款、付款中、未付款金额                               ",
            "  UPDATE b_ap_detail t1                                                           ",
            "  LEFT JOIN (                                                                     ",
            "      -- 子查询：从付款明细表汇总各银行账户的付款金额统计                            ",
            "      SELECT                                                                      ",
            "          ap_id,                                                                  ",
            "          bank_accounts_id,                                                       ",
            "          -- paid_amount: 已付款金额                                           ",
            "          SUM(paid_amount) as total_paid_amount,                                  ",
            "          -- paying_amount: 付款中金额                                         ",
            "          SUM(paying_amount) as total_paying_amount,                              ",
            "          -- unpay_amount: 未付款金额                                           ",
            "          SUM(unpay_amount) as total_unpay_amount                                 ",
            "      FROM                                                                        ",
            "          b_ap_pay_detail t2                                                      ",
            "      GROUP BY                                                                    ",
            "          ap_id, bank_accounts_id                                                 ",
            "  ) t2 ON                                                                         ",
            "      t1.ap_id = t2.ap_id                                                         ",
            "      -- bank_accounts_id: 银行账户ID，关联明细表和付款明细表                     ",
            "      AND t1.bank_accounts_id = t2.bank_accounts_id                               ",
            "  SET                                                                             ",
            "      -- 更新已付款金额：使用付款明细汇总的金额，没有则为0                         ",
            "      t1.paid_amount = IFNULL(t2.total_paid_amount, 0),                           ",
            "      -- 更新付款中金额：使用付款明细汇总的金额，没有则为0                         ",
            "      t1.paying_amount = IFNULL(t2.total_paying_amount, 0),                       ",
            "      -- 计算未付款金额：应付款金额 - 已付款金额 - 付款中金额                       ",
            "      t1.unpay_amount = t1.payable_amount - IFNULL(t2.total_paid_amount, 0) - IFNULL(t2.total_paying_amount, 0) ",
            "  -- #{item}: 应付账款主表ID列表                                                 ",
            "  WHERE t1.ap_id in                                                               ",
            "  <foreach collection='apIds' item='item' open='(' separator=',' close=')'>       ",
            "      #{item}                                                                     ",
            "  </foreach>                                                                      ",
            "  </script>                                                                       "
    })
    void updateTotalData(@Param("apIds") java.util.LinkedHashSet<Integer> apIds);
}
