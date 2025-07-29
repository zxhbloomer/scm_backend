package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.ap.BApAttachEntity;
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

    @Select("""
            -- 根据应付账款主表ID查询附件信息
            select * from b_ap_attach 
            -- #{p1}: 应付账款主表ID
            where ap_id = #{p1}
            """)
    BApAttachVo selectByApId(@Param("p1") Integer id);
}
