package com.xinyirun.scm.core.system.mapper.business.so.arreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * <p>
 * 收款单明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReceiveDetailMapper extends BaseMapper<BArReceiveDetailEntity> {

    /**
     * 根据ar_code查询收款单明细
     */
    @Select("SELECT t1.* FROM b_ar_receive_detail t1 WHERE t1.ar_code = #{code}")
    List<BArReceiveDetailVo> selectByCode(@Param("code") String code);

    /**
     * 根据id查询收款单明细
     */
    @Select("SELECT t1.* FROM b_ar_receive_detail t1 WHERE t1.ar_receive_id = #{id}")
    List<BArReceiveDetailVo> selectById(@Param("id") Integer arReceiveId);

    /**
     * 批量更新收款单明细的已收款、收款中、未收款金额
     * @param arIds 应收账款id集合
     */
//    @Update(
//        "    <script>                                  "+
//        "    UPDATE b_ar_receive_detail t1                                   "+
//        "    LEFT JOIN b_ar_receive t2 ON t1.ar_receive_id = t2.id              "+
//        "    SET                                                      "+
//        "        t1.received_amount = CASE WHEN t2.status = '1' THEN t1.receive_amount ELSE 0 END,   "+
//        "        t1.receiving_amount = CASE WHEN t2.status = '0' THEN t1.receive_amount ELSE 0 END, "+
//        "        t1.unreceive_amount = CASE WHEN t2.status = '0' THEN t1.receive_amount ELSE 0 END,   "+
//        "        t1.cancel_amount = CASE WHEN t2.status = '2' THEN t1.receive_amount ELSE 0 END   "+ // 作废
//        "    WHERE t1.ar_id IN                                         "+
//        "    <foreach collection='arIds' item='id' open='(' separator=',' close=')'>        "+
//        "        #{id}                                                 "+
//        "    </foreach>                                                "+
//        "  </script>  "
//    )
//    int updateTotalData(@Param("arIds") LinkedHashSet<Integer> arIds);

}