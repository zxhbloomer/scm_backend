package com.xinyirun.scm.core.system.mapper.business.index;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.index.BPlanIndexEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 计划序号 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BPlanIndexMapper extends BaseMapper<BPlanIndexEntity> {

    /**
     * 按条件获取数据
     * @param plan_id,type
     * @return
     */
    @Select("    select * from b_plan_index                                                                         "
            + "  where plan_id =  #{p1,jdbcType=INTEGER}                                                            "
            + "        and type =  #{p2,jdbcType=VARCHAR}                                                           "
            + "      ")
    BPlanIndexEntity get(@Param("p1") int plan_id,@Param("p2") String type);
}
