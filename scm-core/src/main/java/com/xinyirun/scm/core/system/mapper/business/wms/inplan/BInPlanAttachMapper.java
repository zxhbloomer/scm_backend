package com.xinyirun.scm.core.system.mapper.business.wms.inplan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanAttachEntity;
import com.xinyirun.scm.bean.system.vo.wms.inplan.BInPlanAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 入库计划附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Repository
public interface BInPlanAttachMapper extends BaseMapper<BInPlanAttachEntity> {

    @Select("select * from b_in_plan_attach where in_plan_id = #{p1}")
    BInPlanAttachVo selectByInPlanId(@Param("p1") Integer id);

}
