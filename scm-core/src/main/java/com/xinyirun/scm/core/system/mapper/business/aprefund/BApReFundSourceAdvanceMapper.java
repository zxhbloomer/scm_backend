package com.xinyirun.scm.core.system.mapper.business.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundSourceAdvanceEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应付退款关联单据表-源单-预收款 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundSourceAdvanceMapper extends BaseMapper<BApReFundSourceAdvanceEntity> {

    /**
     * 根据退款单id查询预收款单
     */
    @Select("SELECT * FROM b_ap_refund_source_advance WHERE ap_refund_id = #{p1}")
    BApReFundSourceAdvanceEntity selectByApRefundId(@Param("p1") Integer ap_refund_id);
}
