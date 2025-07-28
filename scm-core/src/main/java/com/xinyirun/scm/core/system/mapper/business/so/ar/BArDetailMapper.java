package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.ar.BArDetailEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应收账款明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArDetailMapper extends BaseMapper<BArDetailEntity> {

    @Select("select * from b_ar_detail where ar_id = #{p1}")
    List<BArDetailEntity> selectByArId(@Param("p1") Integer ar_id);

    /**
     * 根据应收账款ID删除明细数据
     */
    @Delete("""
            DELETE FROM b_ar_detail t where t.ar_id = #{ar_id}
            """)
    void deleteByArId(Integer ar_id);

    /**
     * 批量更新明细表的已收款、收款中、未收款金额
     * @param arIds 应收账款ID集合
     */
    @Update({
            "  <script>                                                                        ",
            "  UPDATE b_ar_detail t1                                                           ",
            "  LEFT JOIN (                                                                     ",
            "      SELECT                                                                      ",
            "          ar_id,                                                                  ",
            "          bank_accounts_id,                                                       ",
            "          SUM(received_amount) as total_received_amount,                          ",
            "          SUM(receiving_amount) as total_receiving_amount,                        ",
            "          SUM(unreceive_amount) as total_unreceive_amount                         ",
            "      FROM                                                                        ",
            "          b_ar_receive_detail t2                                                  ",
            "      GROUP BY                                                                    ",
            "          ar_id, bank_accounts_id                                                 ",
            "  ) t2 ON                                                                         ",
            "      t1.ar_id = t2.ar_id                                                         ",
            "      AND t1.bank_accounts_id = t2.bank_accounts_id                               ",
            "  SET                                                                             ",
            "      t1.received_amount = IFNULL(t2.total_received_amount, 0),                   ",
            "      t1.receiving_amount = IFNULL(t2.total_receiving_amount, 0),                 ",
            "      t1.unreceive_amount = t1.receivable_amount - IFNULL(t2.total_received_amount, 0) - IFNULL(t2.total_receiving_amount, 0) ",
            "  WHERE t1.ar_id in                                                               ",
            "  <foreach collection='arIds' item='item' open='(' separator=',' close=')'>       ",
            "      #{item}                                                                     ",
            "  </foreach>                                                                      ",
            "  </script>                                                                       "
    })
    void updateTotalData(@Param("arIds") java.util.LinkedHashSet<Integer> arIds);
}