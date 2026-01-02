package com.xinyirun.scm.bean.utils.security;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.bo.user.api.ApiUserBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.common.constant.JWTSecurityConstants;
import com.xinyirun.scm.common.constant.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.xinyirun.scm.bean.system.bo.user.login.MUserBo;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.UnknownHostException;

/**
 * 安全类工具类
 * @author Administrator
 */
@Slf4j
public class SecurityUtil {

    /**
     * 获取login的Authentication
     * @return
     */
    public static Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }


    /**
     * 获取 MUserEntity
     * @return
     */
    public static MUserVo getLoginUserEntity(){
        if(SecurityUtil.getAuthentication() == null) {
            return null;
        }
        return ((MUserBo) SecurityUtil.getAuthentication().getPrincipal()).getMUserVo();
    }

    /**
     * 获取login的userid
     * @return
     */
    public static Long getLoginUser_id(){
//        if(SecurityUtil.getAuthentication() == null){
//            return -1L;
//        } else {
//            if(SecurityUtil.getAuthentication().getPrincipal() instanceof MUserBo){
//                return ((MUserBo) SecurityUtil.getAuthentication().getPrincipal()).getMUserVo().getId();
//            } else if (SecurityUtil.getAuthentication().getPrincipal() instanceof ApiUserBo) {
//                return -1L;
//            }
//            return -1L;
//        }
        // jwt登录时
        if (SecurityUtil.isJwtRequest()){
            return SecurityUtil.getAppJwtBaseBo().getUser_Id();
        }
        // api登录时
        if (SecurityUtil.isApiRequest()){
            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
            return null;
        }
        // 如果都不是，则返回pc登录的数据
        return SecurityUtil.getLoginUserEntity().getId();
    }

    /**
     * 获取login的userid
     * @return
     */
    public static Long getStaff_id(){
//        if(SecurityUtil.getAuthentication() == null){
//            return -1L;
//        } else {
//            if (SecurityUtil.getAuthentication().getPrincipal() instanceof ApiUserBo) {
//                return -1L;
//            } else {
//                return SecurityUtil.getLoginUserEntity().getStaff_id();
//            }
//        }
        // jwt登录时
        if (SecurityUtil.isJwtRequest()){
            return SecurityUtil.getAppJwtBaseBo().getStaff_Id();
        }
        // api登录时
        if (SecurityUtil.isApiRequest()){
            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
            return null;
        }
        // app未登录登录时
        if (SecurityUtil.getLoginUserEntity() == null) {
            return null;
        }
        // 如果都不是，则返回pc登录的数据
        return SecurityUtil.getLoginUserEntity().getStaff_id();
    }

    /**
     * 获取login的userid
     * @return
     */
    public static String getStaff_code(){
//        if(SecurityUtil.getAuthentication() == null){
//            return -1L;
//        } else {
//            if (SecurityUtil.getAuthentication().getPrincipal() instanceof ApiUserBo) {
//                return -1L;
//            } else {
//                return SecurityUtil.getLoginUserEntity().getStaff_id();
//            }
//        }
        // jwt登录时
        if (SecurityUtil.isJwtRequest()){
            return SecurityUtil.getAppJwtBaseBo().getStaff_code();
        }
        // api登录时
        if (SecurityUtil.isApiRequest()){
            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
            return null;
        }
        // app未登录登录时
        if (SecurityUtil.getLoginUserEntity() == null) {
            return null;
        }
        // 如果都不是，则返回pc登录的数据
        return SecurityUtil.getLoginUserEntity().getStaff_code();
    }

    public static String getUser_name(){
        // jwt登录时
        if (SecurityUtil.isJwtRequest()){
            return SecurityUtil.getAppJwtBaseBo().getUsername();
        }
        // api登录时
        if (SecurityUtil.isApiRequest()){
            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
            return null;
        }
        // app未登录登录时
        if (SecurityUtil.getLoginUserEntity() == null) {
            return null;
        }
        // 如果都不是，则返回pc登录的数据
        return SecurityUtil.getLoginUserEntity().getLogin_name();
    }

//    public static String getStaff_name(){
//        // jwt登录时
//        if (SecurityUtil.isJwtRequest()){
//            return SecurityUtil.getAppJwtBaseBo().getStaff();
//        }
//        // api登录时
//        if (SecurityUtil.isApiRequest()){
//            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
//            return null;
//        }
//        // app未登录登录时
//        if (SecurityUtil.getLoginUserEntity() == null) {
//            return null;
//        }
//        // 如果都不是，则返回pc登录的数据
//        return SecurityUtil.getLoginUserEntity().getStaff_id();
//    }

    public static Long getUpdateUser_id(){
//        if(SecurityUtil.getStaff_id() < 0 ) {
//            return SecurityUtil.getLoginUser_id();
//        } else {
//            return SecurityUtil.getStaff_id();
//        }
        if (SecurityUtil.isJwtRequest()){
            return SecurityUtil.getAppJwtBaseBo().getStaff_Id();
        }
        // api登录时
        if (SecurityUtil.isApiRequest()){
            SecurityUtil.getApiUserBo();
            // TODO:初始化设置一个用户，为apiuser，并获取到他的id，作为staff_id
            return null;
        }
        // app未登录登录时
        if (SecurityUtil.getLoginUserEntity() == null) {
            return null;
        }
        // 如果都不是， 则返回pc登录的数据
        return SecurityUtil.getLoginUserEntity().getStaff_id();
    }

    /**
     * 判断是否api登录
     * @return
     */
    public static boolean isApiRequest(){
        if (SecurityUtil.getAuthentication() != null && SecurityUtil.getAuthentication().getPrincipal() instanceof ApiUserBo) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否api登录
     * @return
     */
    public static ApiUserBo getApiUserBo(){
        if (SecurityUtil.isApiRequest()) {
            ApiUserBo rtn = (ApiUserBo) SecurityUtil.getAuthentication().getPrincipal();
            return rtn;
        } else {
            return null;
        }
    }

    /**
     * 判断是否是jwt访问
     * @return
     */
    public static boolean isJwtRequest(){
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String header = request.getHeader(JWTSecurityConstants.HEADER_STRING);
            if (header == null || !header.startsWith(JWTSecurityConstants.TOKEN_PREFIX)) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.info("判断jwt出错" + e.getMessage());
            return false;
        }
    }

    /**
     * 获取jwt bean
     * @return
     */
    public static AppJwtBaseBo getAppJwtBaseBo(){
        if(SecurityUtil.isJwtRequest()){
            User user = (User) SecurityUtil.getAuthentication().getPrincipal();
            String toke_data = user.getUsername();
            AppJwtBaseBo rtn = JSON.parseObject(toke_data, AppJwtBaseBo.class);
            return rtn;
        } else {
            return null;
        }
    }

    /**
     * 获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request =
                ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getSession();
    }

    /**
     * 返回session中保存的user session
     *
     */
    public static UserSessionBo getUserSession() {
        HttpSession session = getSession();
        String sessionId = ServletUtil.getSession().getId();
        String key = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + sessionId;
        return (UserSessionBo)session.getAttribute(key);
    }

    /**
     * 返回session中保存的user session sessionId
     *
     */
    public static String getSessionId() {
        HttpSession session = getSession();
        String sessionId = ServletUtil.getSession().getId();
        return sessionId;
    }

    /**
     * 从请求中提取租户ID
     * 根据实际业务情况修改这个方法的实现
     */
    public static String getTenantIdByRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String tenantId = request.getHeader("X-Tenant-ID");
        return tenantId;
    }
}