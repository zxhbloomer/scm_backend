package com.xinyirun.scm.core.app.serviceimpl.master.user.jwt;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppUpdateResultUtil;
import com.xinyirun.scm.bean.entity.master.user.jwt.MUserJwtTokenEntity;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAppAnnotion;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.jwt.JwtUtil;
import com.xinyirun.scm.core.app.mapper.master.user.jwt.AppMUserJwtTokenMapper;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-18
 */
@Service
public class AppMUserJwtTokenServiceImpl extends AppBaseServiceImpl<AppMUserJwtTokenMapper, MUserJwtTokenEntity> implements AppIMUserJwtTokenService {

    @Autowired
    AppMUserJwtTokenMapper mapper;

    /**
     *  根据user_id,获取jwt token数据
     */
    @Override
    public AppMUserJwtTokenVo selectByUserId(Long user_id) {
        return mapper.selectByUserId(user_id);
    }

    /**
     * 保存token
     */
    @Override
//    @SysLogAppAnnotion("保存app登录时的token")
    public AppMUserJwtTokenVo saveToken(AppMUserJwtTokenVo vo) {
        MUserJwtTokenEntity entity;
        AppMUserJwtTokenVo selectedVo = selectByUserId(vo.getUser_id());
        if(selectedVo != null){
            // 更新
            entity = (MUserJwtTokenEntity) BeanUtilsSupport.copyProperties(selectedVo, MUserJwtTokenEntity.class);
            entity.setToken(vo.getToken());
            entity.setToken_expires_at(vo.getToken_expires_at());
            entity.setLast_login_date(vo.getLast_login_date());
            this.updateById(entity);
        } else {
            // 插入
            entity = (MUserJwtTokenEntity) BeanUtilsSupport.copyProperties(vo, MUserJwtTokenEntity.class);
            this.save(entity);
        }
        AppMUserJwtTokenVo rtnVo =  (AppMUserJwtTokenVo) BeanUtilsSupport.copyProperties(entity, AppMUserJwtTokenVo.class);
        return rtnVo;
    }

    /**
     * check token
     * @param token
     */
    @Override
    public void checkJWTToken(String token, String base64Secret) {
        // 解析token
        // 1、判断是否能够转换成AppUserBo
        AppUserBo appUserBo;
        try {
            String jwtJson = JwtUtil.getUserStringByToken(token, base64Secret);
            appUserBo = JSON.parseObject(jwtJson, AppUserBo.class);
        } catch (Exception e) {
            log.error("checkJWTToken error", e);
            log.debug("转换jwt--->出错");
            throw new JWTAuthException("token不正确，不能正确解析！");
        }
        // 2、判断数据库中是否存在
        AppMUserJwtTokenVo appMUserJwtTokenVo = mapper.selectByUserIdToken(appUserBo.getUser_Id(), token);
        if (appMUserJwtTokenVo == null){
            throw new JWTAuthException("该token非法，没有颁发该token！");
        }
        if(appMUserJwtTokenVo.getStaff_del()){
            throw new JWTAuthException("该用户已经被删除，无法访问服务器资源！");
        }
        if(appMUserJwtTokenVo.getUser_del()){
            throw new JWTAuthException("该用户已经被删除，无法访问服务器资源！");
        }
        if(appMUserJwtTokenVo.getIs_lock()){
            throw new JWTAuthException("该用户已经被锁定，无法访问服务器资源！");
        }
        if(!appMUserJwtTokenVo.getIs_enable()){
            throw new JWTAuthException("该用户未被启用，无法访问服务器资源！");
        }


        // 3、判断是否已经过期
        try {
            if(JwtUtil.isExpired(token, base64Secret)){
                throw new JWTAuthException("该token已过期，请重新发起申请！");
            }
        } catch (Exception e) {
            log.error("checkJWTToken error", e);
            throw new JWTAuthException("token不正确，不能正确解析！");
        }
        AppMUserJwtTokenVo expireData = mapper.selectByUserIdTokenExpire(appUserBo.getUser_Id(), token, LocalDateTime.now());
        if (expireData == null){
            throw new JWTAuthException("该token已过期，请重新获取！");
        }
    }

    /**
     * log out 逻辑，登出逻辑
     */
    @Override
    public AppUpdateResultAo<Integer> logOut() {
        AppJwtBaseBo bo = SecurityUtil.getAppJwtBaseBo();
        int updCount = mapper.updLogOut(bo.getUser_Id(), null , LocalDateTime.now());
        if(updCount != 1){
            throw new JWTAuthException("用户登出失败！");
        }
        return AppUpdateResultUtil.OK(updCount);
    }
}
