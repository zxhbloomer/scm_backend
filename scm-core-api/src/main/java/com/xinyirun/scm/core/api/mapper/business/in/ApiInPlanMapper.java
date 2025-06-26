package com.xinyirun.scm.core.api.mapper.business.in;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.in.ApiInPlanVo;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 入库计划 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiInPlanMapper extends BaseMapper<BInPlanEntity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from                                      "
            + "     b_in_plan t1                                    "
            + "  where t1.id =  #{p1,jdbcType=INTEGER}              "
            + "      ")
    ApiInPlanVo selectPlanById(@Param("p1") int id);

}
