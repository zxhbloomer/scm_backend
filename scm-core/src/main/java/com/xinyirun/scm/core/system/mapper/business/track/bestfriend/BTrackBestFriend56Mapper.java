package com.xinyirun.scm.core.system.mapper.business.track.bestfriend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.track.BTrackGsh56Entity;
import com.xinyirun.scm.bean.system.vo.business.track.bestfriend.BTrackBestFriend56Vo;
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
public interface BTrackBestFriend56Mapper extends BaseMapper<BTrackGsh56Entity> {

    /**
     * 按条件获取数据
     */
    @Select("    "
            + "  select * from b_track_bestfriend56 t1                                                                  "
            + "  where true                                                                                             "
            + "      ")
    BTrackBestFriend56Vo selectOne();

}
