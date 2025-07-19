package com.xinyirun.scm.core.api.mapper.business.in;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.wms.inplan.BInPlanDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 入库计划详情 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
@Mapper
public interface ApiInPlanDetailMapper extends BaseMapper<BInPlanDetailEntity> {

//    /**
//     * 中止入库计划
//     * @param id
//     */
//    @Update(""
//            + " UPDATE                                                                                                  "
//            + " b_in_plan_detail set status = '"+ DictConstant.DICT_B_IN_PLAN_STATUS_DISCOUNTED + "', u_time = now()    "
//            + " WHERE id = #{id}                                                                                        "
//            + " AND status in ('"+ DictConstant.DICT_B_IN_PLAN_STATUS_TWO +"', '"+ DictConstant.DICT_B_IN_PLAN_STATUS_FIVE +"')"
//    )
//    void discontinuedInPlan(Integer id);

}
