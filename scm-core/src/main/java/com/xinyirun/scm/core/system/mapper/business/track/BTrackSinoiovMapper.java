package com.xinyirun.scm.core.system.mapper.business.track;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.track.BTrackSinoiovEntity;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackSinoiovVo;
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
public interface BTrackSinoiovMapper extends BaseMapper<BTrackSinoiovEntity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from b_track_sinoiov t1                                                                       "
            + "  where true                                                                                             "
            + "      ")
    BTrackSinoiovVo selectOne();

}
