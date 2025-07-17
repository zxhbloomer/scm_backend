package com.xinyirun.scm.core.system.mapper.business.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 退款单明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundPayDetailMapper extends BaseMapper<BApReFundPayDetailEntity> {

    /**
     * 根据退款单ID查询退款单明细
     */
    @Select("SELECT t1.* FROM b_ap_refund_pay_detail t1 WHERE t1.ap_refund_pay_id = #{id}")
    List<BApReFundPayDetailVo> selectById(@Param("id") Integer apRefundPayId);

    /**
     * 根据ap_refund_code查询退款单明细
     */
    @Select("SELECT t1.* FROM b_ap_refund_pay_detail t1 WHERE t1.ap_refund_code = #{code}")
    List<BApReFundPayDetailVo> selectByCode(@Param("code") String code);

}
