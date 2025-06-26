package com.xinyirun.scm.core.api.mapper.sync;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.sync.BSyncStatusEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 同步状态 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-01-20
 */
@Repository
public interface ApiBSyncStatusMapper extends BaseMapper<BSyncStatusEntity> {


    /**
     * 按条件获取数据
     */
    @Select("    "
            + " select t.id                                    "
            + "   from b_sync_status t                         "
            + "  where true                                    "
            + "    and t.serial_id =  #{p1,jdbcType=INTEGER}   "
            + "    and t.serial_type =  #{p2,jdbcType=VARCHAR} "
            + "      ")
    Integer isExists(@Param("p1") Integer serial_id, @Param("p2")String serial_type);

}
