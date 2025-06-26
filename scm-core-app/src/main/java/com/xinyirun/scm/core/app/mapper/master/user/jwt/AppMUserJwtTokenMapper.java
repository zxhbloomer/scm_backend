package com.xinyirun.scm.core.app.mapper.master.user.jwt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.jwt.MUserJwtTokenEntity;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @since 2021-12-18
 */
@Repository
public interface AppMUserJwtTokenMapper extends BaseMapper<MUserJwtTokenEntity> {

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

    /**
     * 根据user_id,获取jwt token数据
     * @param user_id
     * @return
     */
    @Select("    "
            + "       select t1.* ,                                  "
            + "              t3.is_del as staff_del ,                "
            + "              t2.is_del as user_del,                  "
            + "              t2.is_lock as is_lock,                  "
            + "              t2.is_enable as is_enable               "
            + "         from m_user_jwt_token t1                     "
            + "   inner join m_user  t2 on t1.user_id = t2.id        "
            + "   inner join m_staff t3 on t3.user_id = t2.id        "
            + "        where true                                    "
            + "          and t1.user_id =  #{p1}                     "
            + "          and t1.token =  #{p2}                       "
            + "      ")
    AppMUserJwtTokenVo selectByUserIdToken(@Param("p1") Long user_id, @Param("p2") String token);

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
            + "    and t.token =  #{p2}             "
            + "    and t.token_expires_at >  #{p3}  "
            + "      ")
    AppMUserJwtTokenVo selectByUserIdTokenExpire(@Param("p1") Long user_id, @Param("p2") String token, @Param("p3") LocalDateTime token_expires_at);


    /**
     * 根据user_id,获取jwt token数据
     * @param user_id
     * @return
     */
    @Update("    "
            + " update m_user_jwt_token t                                                       "
            + "    set t.token = #{p2} ,                                                        "
            + "        t.last_logout_date = #{p3}                                               "
            + "  where true                                                                     "
            + "    and t.user_id =  #{p1}                                                       "
            + "      ")
    int updLogOut(@Param("p1") Long user_id, @Param("p2") String token, @Param("p3") LocalDateTime last_logout_date);

}
