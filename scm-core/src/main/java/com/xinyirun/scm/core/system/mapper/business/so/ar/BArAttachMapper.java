package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.ar.BArAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 应收账款附件表（Accounts Receivable） Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
public interface BArAttachMapper extends BaseMapper<BArAttachEntity> {

    @Select("select * from b_ar_attach where ar_id = #{p1}")
    BArAttachVo selectByArId(@Param("p1") Integer id);
}