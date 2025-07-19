package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.ap.BApAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 应付账款附件表（Accounts Payable） Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
public interface BApAttachMapper extends BaseMapper<BApAttachEntity> {

    @Select("select * from b_ap_attach where ap_id = #{p1}")
    BApAttachVo selectByApId(@Param("p1") Integer id);
}
