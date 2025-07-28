package com.xinyirun.scm.core.system.mapper.business.check;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.check.BCheckResultDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 盘盈盘亏明细 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Repository
public interface BCheckResultDetailMapper extends BaseMapper<BCheckResultDetailEntity> {

    /**
     * 页面查询列表
     */
    @Select("    "
            + " select * from b_check_result_detail                 "
            + "   where true                                        "
            + "   and check_result_id = #{p1,jdbcType=INTEGER}      "
            + "       ")
    List<BCheckResultDetailEntity> selectList(@Param("p1") int check_result_id);


}
