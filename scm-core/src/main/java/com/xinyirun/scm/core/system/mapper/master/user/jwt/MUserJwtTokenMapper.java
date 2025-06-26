package com.xinyirun.scm.core.system.mapper.master.user.jwt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.jwt.MUserJwtTokenEntity;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-12-18
 */
@Repository
public interface MUserJwtTokenMapper extends BaseMapper<MUserJwtTokenEntity> {

    /**
     * 根据user_id,获取jwt token数据
     * @param user_id
     * @return
     */
    @Select("    "
            + " select t.*                          "
            + "   from m_user_jwt_token t           "
            + "  where true                         "
            + "    and t.user_id =  #{p1}           "
            + "      ")
    AppMUserJwtTokenVo selectByUserId(@Param("p1") Long user_id);

}
