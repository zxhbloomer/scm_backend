package com.xinyirun.scm.core.system.mapper.business.track;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.track.BTrackApiSinoiovEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackApiSinoiovVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Repository
public interface BTrackApiSinoiovMapper extends BaseMapper<BTrackApiSinoiovEntity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from b_track_api_sinoiov t1                                                                   "
            + "  where true                                                                                             "
            + "    and t1.type =  #{p1,jdbcType=VARCHAR}                                                                "
            + "      ")
    BTrackApiSinoiovVo selectByType(@Param("p1") String type);

}
