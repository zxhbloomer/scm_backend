package com.xinyirun.scm.core.system.mapper.business.check;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.check.BCheckDetailEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 盘点任务明细 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Repository
public interface BCheckDetailMapper extends BaseMapper<BCheckDetailEntity> {

    /**
     * 查询列表
     */
    @Select("    "
            + " select * from b_check_detail                        "
            + "   where true                                        "
            + "   and check_id = #{p1,jdbcType=INTEGER}             "
            + "       ")
    List<BCheckDetailEntity> selectList(@Param("p1") int check_id);

}
