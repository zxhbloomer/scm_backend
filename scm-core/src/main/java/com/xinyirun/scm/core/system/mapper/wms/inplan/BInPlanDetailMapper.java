package com.xinyirun.scm.core.system.mapper.wms.inplan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.inplan.BInPlanDetailEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库计划明细 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Repository
public interface BInPlanDetailMapper extends BaseMapper<BInPlanDetailEntity> {

    /**
     * 根据入库计划ID删除明细数据
     */
    @Delete(""
            + " DELETE FROM b_in_plan_detail t where t.in_plan_id =  #{in_plan_id}            "
            +"    ")
    void deleteByInPlanId(Integer in_plan_id);

    /**
     * 根据入库计划ID查询明细数据
     */
    @Select(""
            + " select * FROM b_in_plan_detail t where t.in_plan_id =  #{in_plan_id}            "
            +"    ")
    List<BInPlanDetailEntity> selectByInPlanId(Integer in_plan_id);

}
