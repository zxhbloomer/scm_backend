package com.xinyirun.scm.core.system.mapper.business.so.arrefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundSourceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应收退款关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReFundSourceMapper extends BaseMapper<BArReFundSourceEntity> {

    /**
     * 根据ar_id查询源单
     */
    @Select("SELECT * FROM b_ar_refund_source t WHERE t.ar_refund_id = #{ar_refund_id}")
    List<BArReFundSourceVo> selectByArRefundId(@Param("ar_refund_id") Integer ar_refund_id);
}