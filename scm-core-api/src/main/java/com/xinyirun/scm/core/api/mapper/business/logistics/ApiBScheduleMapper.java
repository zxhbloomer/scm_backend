package com.xinyirun.scm.core.api.mapper.business.logistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.vo.business.logistics.LogisticsContractVo;
import com.xinyirun.scm.bean.entity.busniess.schedule.BScheduleEntity;
import com.xinyirun.scm.bean.system.vo.business.schedule.BScheduleVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-11-07
 */
@Repository
public interface ApiBScheduleMapper extends BaseMapper<BScheduleEntity> {


    @Select(""
            + "			SELECT                                                                                          "
            + "				count( 1 ) c                                                                                "
            + "			FROM                                                                                            "
            + "				b_schedule t1                                                                               "
            + "				LEFT JOIN b_order t2 ON t1.order_id = t2.id                                                 "
            + "			WHERE t1.status <> '"+ DictConstant.DICT_B_SCHEDULE_STATUS_FIVE +"'                             "
            + "			and t2.contract_no = #{p1.contract_no,jdbcType=VARCHAR}                                         "
            + "")
    Integer selectLogisticsOrderCount(@Param("p1") LogisticsContractVo vo);

    @Select(""
            + "	SELECT                                                                                                  "
            + "		GROUP_CONCAT(t1.code) code                                                                          "
            + "	FROM                                                                                                    "
            + "		b_schedule t1                                                                                       "
            + "		LEFT JOIN b_carriage_order t2 ON t1.carriage_order_id = t2.id                                       "
            + "		WHERE t1.status IN ('0','1','2','3','4')                                                            "
            + "		AND t2.order_no = #{p1,jdbcType=VARCHAR}                                                            "
            + "      ")
    String checkCarriageOrder(@Param("p1") String code);

}
