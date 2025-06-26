package com.xinyirun.scm.core.app.service.master.user.jwt;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.entity.master.user.jwt.MUserJwtTokenEntity;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-12-18
 */
public interface AppIMUserJwtTokenService extends IService<MUserJwtTokenEntity> {

    /**
     * 获取jwt token数据
     */
    AppMUserJwtTokenVo selectByUserId(Long userId);

    /**
     * 保存token
     */
    AppMUserJwtTokenVo saveToken(AppMUserJwtTokenVo vo);

    /**
     * check token
     * @param token
     */
    void checkJWTToken(String token, String base64Secret);

    /**
     * log out 逻辑，登出逻辑
     */
    AppUpdateResultAo<Integer> logOut();

}
