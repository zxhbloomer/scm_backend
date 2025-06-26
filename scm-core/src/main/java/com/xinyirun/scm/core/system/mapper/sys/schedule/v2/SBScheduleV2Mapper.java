package com.xinyirun.scm.core.system.mapper.sys.schedule.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.sync.ApiScheduleVo;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyAveragePriceEntity;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDailyAveragePriceVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关于物流订单的mapper
 */
@Repository
public interface SBScheduleV2Mapper extends BaseMapper<BDailyAveragePriceEntity> {

    /**
     * 查询所有待调度、已完成的且手动选择出库计划的物流订单
     */
    @Select(""
            + "	SELECT                                                                         "
            + "		t1.* ,                                                                     "
            + "		t2.carriage_contract_code contract_code,                                   "
            + "		t2.order_no order_code                                                     "
            + "	FROM                                                                           "
            + "		b_schedule t1                                                              "
            + "		INNER JOIN b_carriage_order t2 on t1.carriage_order_id = t2.id             "
            + "	WHERE                                                                          "
            + "		t1.out_rule = '1'                                                          "
            + "		AND t1.out_plan_code IS NOT NULL                                           "
            + "		AND t1.STATUS IN ('0','1')                                                 "
    +"")
    public List<ApiScheduleVo> selectScheduleList(ApiScheduleVo condition);

}
