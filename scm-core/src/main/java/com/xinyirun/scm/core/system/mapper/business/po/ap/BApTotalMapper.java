package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.ap.BApTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApTotalVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应付账款管理表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Repository
public interface BApTotalMapper extends BaseMapper<BApTotalEntity> {

    /**
     * 根据应付账款主表ID查询应付账款财务信息
     * @param apId 应付账款主表ID
     * @return BApTotalVo
     */
    @Select("""
            -- 根据应付账款主表ID查询应付账款财务汇总信息
            select * from b_ap_total 
            -- apId: 应付账款主表ID参数
            where ap_id = #{apId}
            """)
    BApTotalVo selectByApId(@Param("apId") Integer apId);

}
