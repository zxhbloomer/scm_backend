package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.ap.BApDetailEntity;
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

    @Select("select * from b_ap_detail where ap_id = #{p1}")
    List<BApDetailEntity> selectByApId(@Param("p1") Integer ap_id);

    /**
     * 根据应付账款ID删除明细数据
     */
    @Delete("""
            DELETE FROM b_ap_detail t where t.ap_id = #{ap_id}
            """)
    void deleteByApId(Integer ap_id);

    /**
     * 批量更新明细表的已付款、付款中、未付款金额
     * @param apIds 应付账款ID集合
     */
    @Update({
            "  <script>                                                                        ",
            "  UPDATE b_ap_detail t1                                                           ",
            "  LEFT JOIN (                                                                     ",
            "      SELECT                                                                      ",
            "          ap_id,                                                                  ",
            "          bank_accounts_id,                                                       ",
            "          SUM(paid_amount) as total_paid_amount,                                  ",
            "          SUM(paying_amount) as total_paying_amount,                              ",
            "          SUM(unpay_amount) as total_unpay_amount                                 ",
            "      FROM                                                                        ",
            "          b_ap_pay_detail t2                                                      ",
            "      GROUP BY                                                                    ",
            "          ap_id, bank_accounts_id                                                 ",
            "  ) t2 ON                                                                         ",
            "      t1.ap_id = t2.ap_id                                                         ",
            "      AND t1.bank_accounts_id = t2.bank_accounts_id                               ",
            "  SET                                                                             ",
            "      t1.paid_amount = IFNULL(t2.total_paid_amount, 0),                           ",
            "      t1.paying_amount = IFNULL(t2.total_paying_amount, 0),                       ",
            "      t1.unpay_amount = t1.payable_amount - IFNULL(t2.total_paid_amount, 0) - IFNULL(t2.total_paying_amount, 0) ",
            "  WHERE t1.ap_id in                                                               ",
            "  <foreach collection='apIds' item='item' open='(' separator=',' close=')'>       ",
            "      #{item}                                                                     ",
            "  </foreach>                                                                      ",
            "  </script>                                                                       "
    })
    void updateTotalData(@Param("apIds") java.util.LinkedHashSet<Integer> apIds);
}
