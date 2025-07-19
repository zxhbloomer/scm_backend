package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应付退款明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundDetailMapper extends BaseMapper<BApReFundDetailEntity> {

    @Select("select * from b_ap_refund_detail where ap_refund_id = #{p1}")
    BApReFundDetailEntity selectByApRefundId(@Param("p1") Integer ap_refund_id);
}
