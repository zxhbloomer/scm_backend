package com.xinyirun.scm.core.system.mapper.business.so.arrefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应收退款明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReFundDetailMapper extends BaseMapper<BArReFundDetailEntity> {

    @Select("select * from b_ar_refund_detail where ar_refund_id = #{p1}")
    BArReFundDetailEntity selectByArRefundId(@Param("p1") Integer ar_refund_id);
}