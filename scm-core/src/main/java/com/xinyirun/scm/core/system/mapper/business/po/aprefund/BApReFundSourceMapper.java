package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.aprefund.BApReFundSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付退款关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundSourceMapper extends BaseMapper<BApReFundSourceEntity> {

    /**
     * 根据ap_id查询源单
     */
    @Select("SELECT * FROM b_ap_refund_source t WHERE t.ap_refund_id = #{ap_refund_id}")
    List<BApReFundSourceVo> selectByApRefundId(@Param("ap_refund_id") Integer ap_refund_id);
}
