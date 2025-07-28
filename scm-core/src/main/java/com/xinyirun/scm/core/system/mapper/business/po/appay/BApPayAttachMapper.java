package com.xinyirun.scm.core.system.mapper.business.po.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPayAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPayAttachVo;
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
public interface BApPayAttachMapper extends BaseMapper<BApPayAttachEntity> {

    @Select("SELECT * FROM b_ap_pay_attach WHERE ap_pay_id = #{id}")
    BApPayAttachVo selectByBApId(Integer id);
}
