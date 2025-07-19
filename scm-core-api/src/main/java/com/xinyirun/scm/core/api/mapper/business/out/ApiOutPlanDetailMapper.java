package com.xinyirun.scm.core.api.mapper.business.out;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.out.ApiOutPlanDetailVo;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.out.BOutPlanListVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 出库计划详情 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiOutPlanDetailMapper extends BaseMapper<BOutPlanDetailEntity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from                                              "
            + "     b_out_plan_detail t1                                    "
            + "  where t1.plan_id =  #{p1,jdbcType=INTEGER}                 "
            + "      ")
    List<ApiOutPlanDetailVo> selectByPlanId(@Param("p1") Integer plan_id);

    /**
     * 按计划id修改明细为过期
     */
    @Select("    "
            + "  select t1.*  from                                                                                      "
            + "     b_out_plan_detail t1                                                                                "
            + "     left join b_out_plan t2 on t1.plan_id = t2.id                                                       "
            + "  where t2.extra_code =  #{p1,jdbcType=VARCHAR}                                                          "
            + "      ")
    List<BOutPlanListVo> selectOutPlanByExtraCode(@Param("p1") String code);

    /**
     * 按extra_code修改明细为过期
     */
    @Select("    "
            + "  update                                                                                                 "
            + "     b_out_plan_detail t1                                                                                "
            + "     left join b_out_plan t2 on t1.plan_id = t2.id                                                       "
            + "     set t1.status = '"+ DictConstant.DICT_B_OUT_PLAN_STATUS_EXPIRES+"'                                  "
            + "  where t2.extra_code =  #{p1,jdbcType=VARCHAR}                                                          "
            + "      ")
    void expiresOutPlan(@Param("p1") String code);

    /**
     * 按extra_code修改明细为中止
     */
    @Select("    "
            + "  update                                                                                                 "
            + "     b_out_plan_detail t1                                                                                "
            + "     left join b_out_plan t2 on t1.plan_id = t2.id                                                       "
            + "     set t1.status = '"+ DictConstant.DICT_B_OUT_PLAN_STATUS_DISCONTINUE+"'                              "
            + "  where t2.extra_code =  #{p1,jdbcType=VARCHAR}                                                          "
            + "      ")
    void discontinueOutPlan(@Param("p1") String code);

    /**
     * 按extra_code修改明细为完成
     */
    @Select("    "
            + "  update                                                                                                 "
            + "     b_out_plan_detail t1                                                                                "
            + "     left join b_out_plan t2 on t1.plan_id = t2.id                                                       "
            + "     set t1.status = '"+ DictConstant.DICT_B_OUT_PLAN_STATUS_FINISH+"'                              "
            + "  where t2.extra_code =  #{p1,jdbcType=VARCHAR}                                                          "
            + "      ")
    void finishOutPlan(@Param("p1") String code);
}
