package com.xinyirun.scm.core.api.mapper.business.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 出库计划 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiOutPlanMapper extends BaseMapper<BOutPlanEntity> {
    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from                                      "
            + "     b_out_plan t1                                   "
            + "  where t1.id =  #{p1,jdbcType=INTEGER}              "
            + "      ")
    ApiOutPlanVo selectPlanById(@Param("p1") int id);

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from                                      "
            + "     b_out_plan t1                                   "
            + "  where t1.extra_code =  #{p1,jdbcType=VARCHAR}      "
            + "      ")
    List<ApiOutPlanVo> selectPlanByExtraCode(@Param("p1") String code);


}
