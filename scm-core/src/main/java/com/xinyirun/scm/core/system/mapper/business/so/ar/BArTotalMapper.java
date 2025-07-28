package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.ar.BArTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArTotalVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应收账款管理表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Repository
public interface BArTotalMapper extends BaseMapper<BArTotalEntity> {

    /**
     * 根据应收账款主表ID查询应收账款财务信息
     * @param arId 应收账款主表ID
     * @return BArTotalVo
     */
    @Select("select * from b_ar_total where ar_id = #{arId}")
    BArTotalVo selectByArId(@Param("arId") Integer arId);

}