package com.xinyirun.scm.core.system.mapper.business.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPayAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPayAttachVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 付款单附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundPayAttachMapper extends BaseMapper<BApReFundPayAttachEntity> {

    @Select("SELECT * FROM b_ap_refund_pay_attach WHERE ap_refund_pay_id = #{id}")
    BApReFundPayAttachVo selectByBapId(Integer id);
}
