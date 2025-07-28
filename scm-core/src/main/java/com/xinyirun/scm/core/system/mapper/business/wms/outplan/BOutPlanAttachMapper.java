package com.xinyirun.scm.core.system.mapper.business.wms.outplan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.outplan.BOutPlanAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 出库计划附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Repository
public interface BOutPlanAttachMapper extends BaseMapper<BOutPlanAttachEntity> {

    @Select("select * from b_out_plan_attach where out_plan_id = #{p1}")
    BOutPlanAttachVo selectByOutPlanId(@Param("p1") Integer id);

}